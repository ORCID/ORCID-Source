#!/usr/bin/env python3
"""The reactor's module graph, and which database-stage legs a change can affect.

The database stage is the slow half of CI and most pull requests cannot have
broken it. This works out which of its legs a change could possibly affect, so
the rest can report success without spending two minutes proving nothing
changed. Nothing is ever skipped: see the db_tests job in test_mvn.yml.

The rule is Maven's dependency graph, never a list of paths. A file belongs to
the reactor module it sits in; the legs that must run are those modules plus
every module that depends on them, transitively. Anything that cannot be placed
in a module -- the root pom, .github, a directory outside <modules> -- runs
everything, so the failure mode is over-running rather than a silently skipped
test.

Two details that cost real time when they were got wrong:

  * Dependencies are matched on artifactId alone, not on groupId. orcid-web
    declares orcid-core with <groupId>${project.parent.groupId}</groupId>, so a
    parser that insists on the literal org.orcid silently drops the single most
    important edge in the graph and reports that a change in orcid-core cannot
    affect orcid-web. Reactor artifact names are unique, so the name is enough.

  * <dependencyManagement> declares versions, not edges, and is skipped.

The graph this derives is checked against Maven's own dependency:tree by the
db_stage_drift job, using the snapshot in reactor-graph.txt beside this file.
Regenerate that snapshot with --graph when a pom gains a dependency.
"""

import argparse
import os
import re
import sys
import xml.etree.ElementTree as ET

# A change to any of these reshapes every module's build or test run, so they
# can never be attributed to one module.
GLOBAL_PATHS = ("pom.xml", ".github/", ".mvn/", ".tool-versions")


def _strip_ns(elem):
    for e in elem.iter():
        if isinstance(e.tag, str) and "}" in e.tag:
            e.tag = e.tag.split("}", 1)[1]
    return elem


def reactor_modules(root="."):
    """The <modules> of the root pom, in declaration order."""
    tree = _strip_ns(ET.parse(os.path.join(root, "pom.xml")).getroot())
    return [m.text.strip() for m in tree.findall("./modules/module") if m.text]


def direct_edges(root=".", modules=None):
    """module -> set of reactor modules it declares a dependency on."""
    modules = modules if modules is not None else reactor_modules(root)
    known = set(modules)
    edges = {m: set() for m in modules}
    for m in modules:
        pom = os.path.join(root, m, "pom.xml")
        if not os.path.isfile(pom):
            continue
        tree = _strip_ns(ET.parse(pom).getroot())
        for dm in tree.findall("./dependencyManagement"):
            tree.remove(dm)  # versions, not edges
        for dep in tree.iter("dependency"):
            artifact = dep.findtext("artifactId", "").strip()
            if artifact in known and artifact != m:
                edges[m].add(artifact)
    return edges


def _closure(seeds, adjacency):
    seen, stack = set(), list(seeds)
    while stack:
        node = stack.pop()
        for nxt in adjacency.get(node, ()):
            if nxt not in seen:
                seen.add(nxt)
                stack.append(nxt)
    return seen


def depends_on(root=".", modules=None):
    """module -> every reactor module it depends on, transitively."""
    edges = direct_edges(root, modules)
    return {m: _closure([m], edges) for m in edges}


def dependents(root=".", modules=None):
    """module -> every reactor module that depends on it, transitively."""
    edges = direct_edges(root, modules)
    reverse = {m: set() for m in edges}
    for m, deps in edges.items():
        for d in deps:
            reverse[d].add(m)
    return {m: _closure([m], reverse) for m in reverse}


def plan(changed_files, root=".", legs=None):
    """Decide which legs must run.

    Returns (modules, reason, everything). `everything` is True when the change
    could not be attributed, in which case `modules` is every leg.
    """
    modules = reactor_modules(root)
    legs = sorted(legs) if legs else sorted(modules)
    known = set(modules)

    touched, unplaceable = set(), []
    for path in changed_files:
        path = path.strip()
        if not path:
            continue
        if any(path == g or path.startswith(g) for g in GLOBAL_PATHS):
            unplaceable.append(path)
            continue
        head = path.split("/", 1)[0]
        if head in known:
            touched.add(head)
        else:
            unplaceable.append(path)

    if unplaceable:
        reason = ("changes outside any single module (%s%s) affect every leg"
                  % (", ".join(unplaceable[:3]),
                     ", and %d more" % (len(unplaceable) - 3) if len(unplaceable) > 3 else ""))
        return legs, reason, True

    if not touched:
        return legs, "no changed files could be read; running everything", True

    rev = dependents(root, modules)
    affected = set()
    for m in touched:
        affected |= rev.get(m, {m})
        affected.add(m)
    needed = sorted(affected & set(legs))
    reason = ("changed: %s; affects: %s" % (", ".join(sorted(touched)),
                                            ", ".join(needed) if needed else "no database legs"))
    return needed, reason, False


def legs_from_workflow(path=".github/workflows/test_mvn.yml"):
    """The modules the db_tests matrix declares.

    Read from the workflow rather than hardcoded, so the plan and the matrix
    cannot drift apart. Parsed as YAML, not with a job-name regex: the workflow
    contains its own job names inside script blocks, and a text range over them
    matches the wrong thing.
    """
    text = open(path).read()
    try:
        import yaml
        wf = yaml.safe_load(text)
        include = wf["jobs"]["db_tests"]["strategy"]["matrix"]["include"]
        return [entry["project"] for entry in include]
    except ImportError:
        pass
    # PyYAML is not guaranteed on a runner. Fall back to the one text shape the
    # matrix can have, anchored so a job name quoted inside a script block --
    # deeper indentation, more text on the line -- cannot match.
    lines = text.splitlines()
    try:
        start = next(i for i, l in enumerate(lines) if l == "  db_tests:")
    except StopIteration:
        raise SystemExit("reactor_graph: no db_tests job in " + path)
    legs = []
    in_include = False
    for l in lines[start + 1:]:
        if re.match(r"^  [a-z_]+:\s*$", l):
            break  # next top-level job
        if l.strip() == "include:":
            in_include = True
            continue
        m = re.match(r"^\s+- project:\s*([a-z-]+)\s*$", l)
        if in_include and m:
            legs.append(m.group(1))
        elif in_include and l.strip() and not l.startswith("          "):
            in_include = False
    if not legs:
        raise SystemExit("reactor_graph: could not read the db_tests matrix from " + path)
    return legs


def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--root", default=".")
    ap.add_argument("--graph", action="store_true",
                    help="print 'module: transitive deps', the format of reactor-graph.txt")
    ap.add_argument("--plan", action="store_true",
                    help="read changed paths on stdin, print the legs that must run")
    ap.add_argument("--all", action="store_true",
                    help="every leg runs; pair with --why to say what forced it")
    ap.add_argument("--why", default="",
                    help="the reason, when --all is given")
    ap.add_argument("--legs", default="",
                    help="space-separated modules that have a database leg; "
                         "defaults to the db_tests matrix in test_mvn.yml")
    args = ap.parse_args()

    if args.graph:
        deps = depends_on(args.root)
        for m in reactor_modules(args.root):
            print("%s: %s" % (m, " ".join(sorted(deps.get(m, ())))))
        return 0

    if args.plan:
        legs = args.legs.split() if args.legs else legs_from_workflow(
            os.path.join(args.root, ".github/workflows/test_mvn.yml"))
        if args.all:
            modules, reason, everything = sorted(legs), args.why or "running everything", True
        else:
            files = sys.stdin.read().splitlines()
            modules, reason, everything = plan(files, args.root, legs)
        out = os.environ.get("GITHUB_OUTPUT")
        payload = [("modules", " ".join(modules)),
                   ("reason", reason),
                   ("everything", "true" if everything else "false")]
        if out:
            with open(out, "a") as fh:
                for k, v in payload:
                    fh.write("%s=%s\n" % (k, v))
        for k, v in payload:
            print("%s=%s" % (k, v))
        return 0

    ap.print_help()
    return 2


if __name__ == "__main__":
    sys.exit(main())
