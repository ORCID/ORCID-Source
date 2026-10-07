#!/usr/bin/env bash
# Tests for release-branch.sh, run against throwaway repositories in a
# temporary directory. Runs as a pre-commit hook, so the required lint check
# runs it on every pull request.
#
#   .github/scripts/release-branch.test.sh

set -euo pipefail

# Under a git hook these point at the outer repository.
# shellcheck disable=SC2046
unset $(git rev-parse --local-env-vars)

SCRIPT="$(cd "$(dirname "$0")" && pwd)/release-branch.sh"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

# Keep the developer's own git config (signing, hooks, default branch) out.
export HOME="$TMP" GIT_CONFIG_NOSYSTEM=1
export GIT_AUTHOR_NAME=test GIT_AUTHOR_EMAIL=test@example.org
export GIT_COMMITTER_NAME=test GIT_COMMITTER_EMAIL=test@example.org

passed=0
failed=0

ok() {
  passed=$((passed + 1))
  echo "ok   - $1"
}

not_ok() {
  failed=$((failed + 1))
  echo "FAIL - $1"
  echo "       $2"
}

flat() {
  local text="$1"
  echo "${text//$'\n'/ | }"
}

# expect <name> <wanted stdout> <command...>
expect() {
  local name="$1" want="$2" got
  shift 2
  if got="$("$@" 2>&1)"; then
    if [ "$got" = "$want" ]; then
      ok "$name"
    else
      not_ok "$name" "wanted [$(flat "$want")], got [$(flat "$got")]"
    fi
  else
    not_ok "$name" "exited non-zero: $(flat "$got")"
  fi
}

# refuse <name> <fragment of the error> <command...>
refuse() {
  local name="$1" want="$2" got
  shift 2
  if got="$("$@" 2>&1)"; then
    not_ok "$name" "succeeded with [$(flat "$got")]"
  elif [[ "$got" == *"$want"* ]]; then
    ok "$name"
  else
    not_ok "$name" "wanted an error containing [$want], got [$(flat "$got")]"
  fi
}

rb() {
  "$SCRIPT" "$@"
}

# A commit with a change of its own, so git cherry can tell commits apart.
change() {
  echo "$2" >"$1"
  git add "$1"
  git commit -q -m "$2"
  git rev-parse HEAD
}

git init -q --bare "$TMP/origin.git"
git init -q -b main "$TMP/work"
cd "$TMP/work"
git remote add origin "$TMP/origin.git"

change history "history" >/dev/null
git tag v2.149.14
change old "old line" >/dev/null
git tag v3.2.7
change a "v3.22 work" >/dev/null
git tag v3.22.5
change b "v3.23.1 work" >/dev/null
git tag v3.23.1
change c "v3.23.9 work" >/dev/null
git tag v3.23.9
git tag v3.23.11-rc1
git tag release-3.23.99
C10="$(change d "v3.23.10 work")"
git tag -a -m "annotated" v3.23.10
git push -q origin main --tags

echo "# before the cut"
expect "plan-cut cuts the latest tag, annotated tags peeled" \
  "$(printf 'branch=release-3.23\ncommit=%s\nrelease_tag=v3.23.10\nmain_tag=v3.24.0' "$C10")" \
  rb plan-cut
refuse "next-patch refuses while main is still on the line" \
  "has not moved past v3.23" rb next-patch release-3.23
refuse "next-patch refuses main" "is not a release branch" rb next-patch main
refuse "next-patch refuses a three-part name" "is not a release branch" rb next-patch release-3.23.1
refuse "next-patch refuses a one-part name" "is not a release branch" rb next-patch release-3

UNBUILT="$(change e "merged, pipeline still running")"
git push -q origin main
refuse "plan-cut refuses while main has untagged commits" \
  "wait for pushmain" rb plan-cut
git tag v3.23.11
git push -q origin v3.23.11
expect "plan-cut cuts once main's head is tagged" \
  "$(printf 'branch=release-3.23\ncommit=%s\nrelease_tag=v3.23.11\nmain_tag=v3.24.0' "$UNBUILT")" \
  rb plan-cut

echo "# the cut"
git push -q origin "$UNBUILT:refs/heads/release-3.23" "$UNBUILT:refs/tags/v3.24.0"
refuse "plan-cut refuses to cut again before main builds" \
  "nothing has been built on main since" rb plan-cut
expect "next-patch sorts by version, not text" "version_tag=v3.23.12" rb next-patch release-3.23
expect "next-patch keeps v3.2 apart from v3.22 and v3.23" "version_tag=v3.2.8" rb next-patch release-3.2
refuse "next-patch refuses a line with no tag" "no v3.25.<patch> tag" rb next-patch release-3.25

echo "# fixes on the release branch"
git switch -q -c rel "$UNBUILT"
FIX1="$(change fix1 "PD-1 fix the thing")"
change CHANGELOG.md "v3.23.12 changelog update" >/dev/null
git tag v3.23.12
git push -q origin rel:release-3.23 v3.23.12
AFTER1="$(git rev-parse HEAD)"
expect "next-patch follows the branch's own tags" "version_tag=v3.23.13" rb next-patch release-3.23
expect "unported lists the fix, not the changelog commit" "$FIX1" rb unported origin/main rel
expect "unported with a limit lists that push" "$FIX1" rb unported origin/main "$AFTER1" "$UNBUILT"

git switch -q -c pr rel
FIX2="$(change fix2 "PD-2 second fix")"
git switch -q rel
git merge -q --no-ff -m "Merge pull request #2 from ORCID/pr" pr
git push -q origin rel:release-3.23
AFTER2="$(git rev-parse HEAD)"
expect "unported skips merge commits and earlier pushes" "$FIX2" rb unported origin/main "$AFTER2" "$AFTER1"
expect "unported ignores an all-zero limit" "" rb unported origin/main "$AFTER2" 0000000000000000000000000000000000000000
expect "unported ignores an unknown limit" "" rb unported origin/main "$AFTER2" 1234567890123456789012345678901234567890

echo "# forward-port, and main moving on"
git switch -q main
# Clean and without -x: recognised by its change alone.
git cherry-pick "$FIX1" >/dev/null
git push -q origin main
expect "unported recognises a cherry-picked fix by its change" "$FIX2" rb unported origin/main rel

git tag v3.24.1
git push -q origin v3.24.1
expect "next-patch ignores main's later tags" "version_tag=v3.23.13" rb next-patch release-3.23
refuse "plan-cut refuses while the last release has a fix not on main" \
  "release-3.23 has changes that are not on main" rb plan-cut
refuse "plan-cut names the fix that is missing" "${FIX2:0:10} PD-2 second fix" rb plan-cut

# Forward-ported by hand after a conflict: the change differs, the -x line names it.
git cherry-pick -x "$FIX2" >/dev/null
echo "PD-2 second fix, resolved against main" >fix2
git commit -q -a --amend --no-edit
git push -q origin main
expect "unported recognises a resolved cherry-pick by its -x line" "" rb unported origin/main rel

MAIN_BUILT="$(git rev-parse HEAD)"
git tag v3.24.2
git push -q origin v3.24.2
expect "plan-cut cuts once the last release is on main" \
  "$(printf 'branch=release-3.24\ncommit=%s\nrelease_tag=v3.24.2\nmain_tag=v3.25.0' "$MAIN_BUILT")" \
  rb plan-cut
git push -q origin "$MAIN_BUILT:refs/heads/release-3.24"
refuse "plan-cut refuses when the branch exists" "release-3.24 already exists" rb plan-cut

echo
echo "$passed passed, $failed failed"
[ "$failed" -eq 0 ]
