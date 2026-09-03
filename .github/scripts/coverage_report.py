#!/usr/bin/env python3
"""Render JaCoCo + Surefire results from the test matrix as one Markdown report.

Reads the artifacts downloaded by the `coverage_report` job in test_mvn.yml:

    <artifacts>/coverage-<module>/jacoco.xml            (unit stage)
    <artifacts>/coverage-<module>/surefire/TEST-*.xml   (optional)
    <artifacts>/coverage-db-<module>/jacoco.xml         (database stage, optional)

The two stages are reported side by side rather than combined. After the
migration, a module's DAO coverage comes only from the database stage, so a
single column would show orcid-persistence near zero and read as a regression
when nothing regressed. Merging them would need the .exec files and every
module's class files, which this job deliberately does not build.

and writes Markdown to stdout: a per-module coverage table, the executed-test
count per module, and — when a list of changed files is supplied — the coverage
of just those files.

This measures. It does not gate: there are no thresholds and the exit code is 0
unless the inputs are unreadable.

Usage:
    coverage_report.py <artifacts-dir> [--changed-files <file-with-one-path-per-line>]
"""

import argparse
import os
import sys
import xml.etree.ElementTree as ET

# Line coverage below this is flagged with a marker in the table. Purely
# informational -- it changes no exit code and blocks nothing.
LOW_COVERAGE_MARKER = 25.0

MARKER = "<!-- orcid-coverage-report -->"


def pct(covered, missed):
    total = covered + missed
    if total == 0:
        return None
    return 100.0 * covered / total


def fmt_pct(value):
    return "n/a" if value is None else f"{value:.1f}%"


def counters(element):
    """Return {counter type: (covered, missed)} for the direct children of element."""
    out = {}
    for counter in element.findall("counter"):
        out[counter.get("type")] = (
            int(counter.get("covered", 0)),
            int(counter.get("missed", 0)),
        )
    return out


def read_jacoco(path):
    """Parse one module's jacoco.xml into totals and per-source-file figures."""
    root = ET.parse(path).getroot()
    totals = counters(root)
    files = {}
    for package in root.findall("package"):
        package_name = package.get("name", "")
        for source in package.findall("sourcefile"):
            rel = f"{package_name}/{source.get('name')}" if package_name else source.get("name")
            files[rel] = counters(source)
    return totals, files


def read_surefire(directory):
    """Sum tests/failures/errors/skipped across a module's Surefire XML reports."""
    if not os.path.isdir(directory):
        return None
    tally = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    found = False
    for name in sorted(os.listdir(directory)):
        if not (name.startswith("TEST-") and name.endswith(".xml")):
            continue
        try:
            suite = ET.parse(os.path.join(directory, name)).getroot()
        except ET.ParseError:
            continue
        found = True
        for key in tally:
            tally[key] += int(suite.get(key, 0) or 0)
    return tally if found else None


def collect(artifacts_dir):
    """Yield (module, totals, per-file counters, surefire tally) per module, sorted."""
    modules = []
    for entry in sorted(os.listdir(artifacts_dir)):
        if not entry.startswith("coverage-") or entry.startswith("coverage-db-"):
            continue
        module = entry[len("coverage-"):]
        base = os.path.join(artifacts_dir, entry)
        jacoco = os.path.join(base, "jacoco.xml")
        if not os.path.isfile(jacoco):
            # A module whose tests failed before the report was written still
            # gets a row, so a missing module is visible rather than silent.
            modules.append((module, None, {}, read_surefire(os.path.join(base, "surefire"))))
            continue
        try:
            totals, files = read_jacoco(jacoco)
        except ET.ParseError as exc:
            print(f"warning: unreadable {jacoco}: {exc}", file=sys.stderr)
            modules.append((module, None, {}, None))
            continue
        modules.append((module, totals, files, read_surefire(os.path.join(base, "surefire"))))
    return modules


def collect_db(artifacts_dir):
    """Line/branch totals from the database stage, keyed by module."""
    out = {}
    for entry in sorted(os.listdir(artifacts_dir)):
        if not entry.startswith("coverage-db-"):
            continue
        module = entry[len("coverage-db-"):]
        jacoco = os.path.join(artifacts_dir, entry, "jacoco.xml")
        if not os.path.isfile(jacoco):
            continue
        try:
            totals, _files = read_jacoco(jacoco)
        except ET.ParseError:
            continue
        out[module] = totals
    return out


def module_table(modules, db_totals):
    show_db = bool(db_totals)
    header = "| Module | Lines | Branches | Covered / total lines | Tests run |"
    rule = "| --- | ---: | ---: | ---: | ---: |"
    if show_db:
        header = ("| Module | Lines | Branches | Covered / total lines | Tests run "
                  "| Lines (db stage) |")
        rule = "| --- | ---: | ---: | ---: | ---: | ---: |"
    lines = [header, rule]
    line_cov = line_missed = branch_cov = branch_missed = tests = 0
    for module, totals, _files, surefire in modules:
        if totals is None:
            lines.append(f"| `{module}` | — | — | no report | — |"
                         + (" — |" if show_db else ""))
            continue
        lc, lm = totals.get("LINE", (0, 0))
        bc, bm = totals.get("BRANCH", (0, 0))
        line_cov += lc
        line_missed += lm
        branch_cov += bc
        branch_missed += bm
        line_pct = pct(lc, lm)
        flag = " ⚠️" if line_pct is not None and line_pct < LOW_COVERAGE_MARKER else ""
        if surefire:
            tests += surefire["tests"]
            run = str(surefire["tests"])
            bad = surefire["failures"] + surefire["errors"]
            if bad:
                run += f" ({bad} failed)"
        else:
            run = "—"
        row = (f"| `{module}` | {fmt_pct(line_pct)}{flag} | {fmt_pct(pct(bc, bm))} "
               f"| {lc:,} / {lc + lm:,} | {run} |")
        if show_db:
            db = db_totals.get(module)
            if db is None:
                row += " — |"
            else:
                dlc, dlm = db.get("LINE", (0, 0))
                row += f" {fmt_pct(pct(dlc, dlm))} |"
        lines.append(row)
    total_row = (f"| **Reactor** | **{fmt_pct(pct(line_cov, line_missed))}** "
                 f"| **{fmt_pct(pct(branch_cov, branch_missed))}** "
                 f"| **{line_cov:,} / {line_cov + line_missed:,}** | **{tests:,}** |")
    if show_db:
        total_row += " |"
    lines.append(total_row)
    return lines


def changed_file_table(modules, changed_paths):
    """Coverage of the main-source files this pull request touched.

    A changed path is repo-relative (`orcid-core/src/main/java/org/orcid/...`);
    JaCoCo keys files by package path (`org/orcid/.../Foo.java`) within a module,
    so match on the module prefix plus the package-relative tail.
    """
    rows = []
    for path in changed_paths:
        if not path.endswith(".java") or "/src/main/java/" not in path:
            continue
        module, tail = path.split("/src/main/java/", 1)
        for name, _totals, files, _surefire in modules:
            if name != module:
                continue
            file_counters = files.get(tail)
            if file_counters is None:
                # Compiled but never loaded by any test, or not compiled at all.
                rows.append((path, None, None))
            else:
                lc, lm = file_counters.get("LINE", (0, 0))
                bc, bm = file_counters.get("BRANCH", (0, 0))
                rows.append((path, pct(lc, lm), pct(bc, bm)))
            break
    if not rows:
        return []
    out = [
        "",
        "#### Files changed in this pull request",
        "",
        "| File | Lines | Branches |",
        "| --- | ---: | ---: |",
    ]
    for path, line_pct, branch_pct in sorted(rows):
        if line_pct is None:
            out.append(f"| `{path}` | not covered by this module's tests | — |")
        else:
            out.append(f"| `{path}` | {fmt_pct(line_pct)} | {fmt_pct(branch_pct)} |")
    return out


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("artifacts_dir")
    parser.add_argument("--changed-files", help="file listing changed paths, one per line")
    parser.add_argument("--title", default="Unit test coverage")
    args = parser.parse_args()

    if not os.path.isdir(args.artifacts_dir):
        print(f"error: no such directory: {args.artifacts_dir}", file=sys.stderr)
        return 1

    modules = collect(args.artifacts_dir)
    db_totals = collect_db(args.artifacts_dir)
    if not modules:
        print(f"{MARKER}\n### {args.title}\n\nNo coverage artifacts were produced.")
        return 0

    out = [MARKER, f"### {args.title}", ""]
    out += module_table(modules, db_totals)

    if args.changed_files and os.path.isfile(args.changed_files):
        with open(args.changed_files, encoding="utf-8") as handle:
            changed = [line.strip() for line in handle if line.strip()]
        out += changed_file_table(modules, changed)

    out += [
        "",
        f"<sub>JaCoCo line and branch coverage per module, from each module's own unit tests. "
        f"No thresholds are enforced; ⚠️ marks under {LOW_COVERAGE_MARKER:.0f}% line coverage "
        f"for information only."
        + (" The database stage is reported separately, not merged: a DAO covered only "
           "there would otherwise read as uncovered." if db_totals else "")
        + "</sub>",
    ]
    print("\n".join(out))
    return 0


if __name__ == "__main__":
    sys.exit(main())
