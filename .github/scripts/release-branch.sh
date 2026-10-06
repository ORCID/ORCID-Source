#!/usr/bin/env bash
# Release-branch helpers for cut_release.yml and pushrelease.yml.
#
# A release branch is named release-<major>.<minor> and owns that patch line.
# The cut also tags main's head v<major>.<minor+1>.0, so main's own versioning
# (ORCID/version-bump-action: highest tag in the repository, plus one) moves to
# the next line and never hands out a number from the branch's.
#
#   plan-cut
#       What a cut would create, as key=value lines for $GITHUB_OUTPUT. Refuses
#       unless main's head is exactly its latest tag, so every merge is built.
#   next-patch <branch>
#       version_tag=<next tag in the branch's line>, for $GITHUB_OUTPUT.
#   unported <upstream> <head> [<limit>]
#       Commits in <head> whose change is not in <upstream>, oldest first, one
#       sha per line. Merge commits and changelog commits are left out. With
#       <limit>, only commits after it; an all-zero or unknown <limit> (a new
#       branch, a force push) gives nothing.
#
# Tags and branches are read from $REMOTE (default origin) with ls-remote, so
# they do not need to have been fetched.

set -euo pipefail

REMOTE="${REMOTE:-origin}"
MAIN_BRANCH="${MAIN_BRANCH:-main}"

die() {
  echo "release-branch: $*" >&2
  exit 1
}

# "<tag> <commit>" for every v<major>.<minor>.<patch> tag on the remote, lowest
# version first. Annotated tags are peeled to the commit they point at.
remote_tags() {
  local refs
  refs="$(git ls-remote --tags "$REMOTE" 'refs/tags/v*')"
  awk '{
      ref = $2
      sub("^refs/tags/", "", ref)
      peeled = sub("\\^\\{\\}$", "", ref)
      if (peeled || !(ref in sha)) sha[ref] = $1
    }
    END { for (ref in sha) print ref, sha[ref] }' <<<"$refs" \
    | { grep -E '^v[0-9]+\.[0-9]+\.[0-9]+ ' || true; } \
    | sort -V -k1,1
}

remote_branch_commit() {
  git ls-remote --heads "$REMOTE" "refs/heads/$1" | awk '{ print $1 }'
}

plan_cut() {
  local tags latest commit major minor patch branch main_tag main_commit prev_line
  tags="$(remote_tags)"
  [ -n "$tags" ] || die "no v<major>.<minor>.<patch> tag on $REMOTE"

  read -r latest commit <<<"$(tail -n1 <<<"$tags")"
  IFS=. read -r major minor patch <<<"${latest#v}"
  branch="release-${major}.${minor}"
  main_tag="v${major}.$((minor + 1)).0"

  main_commit="$(remote_branch_commit "$MAIN_BRANCH")"
  [ -n "$main_commit" ] || die "$REMOTE has no $MAIN_BRANCH branch"
  if [ "$main_commit" != "$commit" ]; then
    echo "$MAIN_BRANCH is at $main_commit, but the latest tag $latest is $commit." >&2
    if git cat-file -e "${main_commit}^{commit}" 2>/dev/null \
      && git cat-file -e "${commit}^{commit}" 2>/dev/null; then
      echo "Not yet built and tagged:" >&2
      git log --oneline "${commit}..${main_commit}" >&2
    fi
    die "wait for pushmain to tag $MAIN_BRANCH's head, then cut"
  fi

  # The previous cut tagged main's head v<major>.<minor>.0 next to the last
  # tag of the line before. Until main builds something, the latest tag is that
  # bare tag, which has no artifact behind it: cutting again would start a
  # release line from nothing.
  prev_line="v${major}.$((minor - 1))."
  if [ "$patch" = 0 ] \
    && awk -v c="$commit" -v p="$prev_line" '$2 == c && index($1, p) == 1 { found = 1 } END { exit !found }' <<<"$tags"; then
    die "$latest is the tag the last cut put on $MAIN_BRANCH; nothing has been built on $MAIN_BRANCH since"
  fi

  [ -z "$(remote_branch_commit "$branch")" ] || die "$branch already exists on $REMOTE"

  echo "branch=$branch"
  echo "commit=$commit"
  echo "release_tag=$latest"
  echo "main_tag=$main_tag"
}

next_patch() {
  local branch="$1" line line_re tags latest last main_line
  [[ "$branch" =~ ^release-([0-9]+)\.([0-9]+)$ ]] \
    || die "'$branch' is not a release branch (release-<major>.<minor>)"
  line="v${BASH_REMATCH[1]}.${BASH_REMATCH[2]}"
  line_re="^${line//./\\.}\\.[0-9]+\$"

  tags="$(remote_tags | cut -d' ' -f1)"
  last="$({ grep -E "$line_re" <<<"$tags" || true; } | tail -n1)"
  [ -n "$last" ] || die "no ${line}.<patch> tag on $REMOTE; release branches start at a tag (cut_release.yml)"

  # If main is still on this line, its next build would take the same number.
  latest="$(tail -n1 <<<"$tags")"
  main_line="${latest%.*}"
  if [ "$main_line" = "$line" ] \
    || [ "$(printf '%s\n%s\n' "$line" "$main_line" | sort -V | tail -n1)" != "$main_line" ]; then
    die "the latest tag is $latest, so $MAIN_BRANCH has not moved past ${line}; finish the cut (cut_release.yml) first"
  fi

  echo "version_tag=${line}.$((${last##*.} + 1))"
}

unported() {
  local args=("$1" "$2") limit mark sha subject
  if [ $# -ge 3 ]; then
    limit="$3"
    if [[ "$limit" =~ ^0+$ ]] || ! git cat-file -e "${limit}^{commit}" 2>/dev/null; then
      return 0
    fi
    args+=("$limit")
  fi
  git cherry "${args[@]}" | while read -r mark sha; do
    [ "$mark" = "+" ] || continue
    subject="$(git log -1 --format=%s "$sha")"
    [[ "$subject" =~ ^v[0-9]+\.[0-9]+\.[0-9]+\ changelog\ update$ ]] && continue
    echo "$sha"
  done
}

case "${1:-}" in
  plan-cut)
    [ $# -eq 1 ] || die "usage: $0 plan-cut"
    plan_cut
    ;;
  next-patch)
    [ $# -eq 2 ] || die "usage: $0 next-patch <branch>"
    next_patch "$2"
    ;;
  unported)
    [ $# -ge 3 ] && [ $# -le 4 ] || die "usage: $0 unported <upstream> <head> [<limit>]"
    shift
    unported "$@"
    ;;
  *)
    die "usage: $0 plan-cut | next-patch <branch> | unported <upstream> <head> [<limit>]"
    ;;
esac
