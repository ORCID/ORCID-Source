# Testing ORCID-Source

How to run the tests, how to write a new one, and the rules a test migration is
reviewed against.

> `TESTAUTO.md` describes a Selenium blackbox suite in `orcid-integration-test/`
> that was removed from this repository in June 2022. Every path in it dangles.
> End-to-end coverage lives in the separate `orcid-cypress_tests-private`
> repository. This file covers the JVM tests that run in CI.

## Running tests

```bash
mvn test --projects orcid-core          # one module's unit tests
mvn test -P db-tests --projects orcid-persistence   # only the tests that need a database
mvn test -P all-tests --projects orcid-core         # both stages
mvn test --projects orcid-core -Djacoco.skip=true   # skip coverage instrumentation
```

Two stages exist, and the difference is not "fast and slow" but "what the test
is allowed to depend on".

| Stage | Selected by | Depends on | Runs in CI as |
| --- | --- | --- | --- |
| Unit (default) | no category | nothing outside the JVM | the `test_mvn` matrix |
| Database | `@Category(DatabaseTest.class)` | HSQLDB, Spring context, DBUnit fixtures | the `db-tests` job |

A unit test that boots a Spring context or touches a database is in the wrong
stage. Categorise it or, better, mock its collaborators.

## Coverage

Every module reports JaCoCo line and branch coverage on each pull request, as a
comment and in the job summary. There are no thresholds: the report exists so a
change's effect on coverage is visible to its reviewer, not to gate merges.

```bash
mvn test --projects orcid-core
open orcid-core/target/site/jacoco/index.html
```

The per-module figure counts only that module's **own** tests. A class in
`orcid-core` executed solely by an `orcid-api-web` integration test shows as
uncovered here, and that is deliberate: it is exactly the class that needs a
unit test of its own before the integration test that reaches it can be
retired.

The JaCoCo agent costs about 12% of test time (measured on `orcid-persistence`:
46.0s to 51.5s). Pass `-Djacoco.skip=true` locally when that matters.

## Verified

Run on 2026-09-05 against `4c802f0c13`, rebased on `origin/main` at `5c67777e9a`
(v3.0.54). Unit stage 3,597 tests, database stage 763, both green from a clean
tree.

Parity with `main` is not a claim here, it is a table. Each row is a proof `main`
holds by running a real delegator, manager and security manager over fixtures.
Each mutation was applied to production code on its own, with a control run first,
and reverted byte-identically afterwards; every one turned exactly the named test
red and nothing else.

| Mutation applied to production code | Went red |
| --- | --- |
| v3 work manager: delete the remove-path source guard | `v3.WorkManagerImplMockTest` |
| v2 security manager: `checkSource` returns early | `OrcidSecurityManager_SourceTest` |
| v3 `isMyToken`: delete the claimed-record refusal | `v3.OrcidSecurityManager_generalTest`, 2 cases |
| v2 and v3 `isMyToken`: invert the claimed predicate | both general tests, 6 cases |
| v2 address manager: delete the delete-path source guard | `AddressManagerImplMockTest` |
| v2 address manager: pass null as the original visibility | `AddressManagerImplMockTest` |
| v2 affiliations manager: drop the visibility re-apply | `AffiliationsManagerImplMockTest` |
| v3 read-only work manager: delete the leftover put-code loop | `WorkManagerReadOnlyImplTest` |
| v2 email read-only manager: delete the verified filter | `EmailManagerReadOnlyTest` |
| v3 delegator: set a visibility before calling the manager | `..._WorksTest` visibility-null case |
| v3 delegator: move the profile guard below the manager call | `...ErrorsTest`, 5 delete cases |

The last row is the one worth understanding. Those five tests already asserted the
right exception before the `@After` was added, and stayed green with the guard
moved below the write. Only `verifyNoInteractions` catches it. That is R2's second
half, and it is why the table is the evidence rather than the test names.

Two things this table does not cover, deliberately. The research-resource and
group-id delete guards have no proof here because `main` has none either, so
adding one would be new work rather than parity. And a run of the whole suite
under mutation would be stronger still; these eleven were chosen because each is
the sole catcher of a rule `main` proves.

## The database stage runs only what a change can have affected

Every leg of the database stage reports on every pull request, but a leg whose
module the change cannot have reached finishes green in seconds instead of
spending two minutes proving nothing moved. Nothing is skipped: a skipped job
never reports its check, and a required check that never reports blocks the
merge for ever.

The decision is Maven's dependency graph, never a list of paths. A file belongs
to the module it sits in; the legs that run are those modules plus every module
that depends on them. So a change in `orcid-web` runs the web leg alone, a
change in `orcid-persistence` runs persistence, core, web and api-web, and a
change in `orcid-core` runs core, web and api-web but not persistence, because
persistence does not depend on core.

Everything runs when the change cannot be attributed: the root pom, anything
under `.github`, `orcid-test`, a directory that is not a reactor module, or any
event without a pull-request base to diff against. Over-running is the intended
failure mode; a silently skipped test is not.

Two things keep it honest. The graph is parsed from the poms by
`.github/scripts/reactor_graph.py`, and `db_stage_drift` fails if that parse
stops matching `reactor-graph.txt`, the snapshot taken from Maven's own
`dependency:tree`. And the parse keys on artifact names rather than group ids,
because `orcid-web` declares `orcid-core` through
`<groupId>${project.parent.groupId}</groupId>` -- a parser that insists on the
literal `org.orcid` drops the most important edge in the graph and concludes
that a change in `orcid-core` cannot affect `orcid-web`.

To see the decision for a change you have in hand:

```
git diff --name-only origin/main...HEAD | python3 .github/scripts/reactor_graph.py --plan
```

A `workflow_dispatch` run has no pull-request base, so it always runs every leg.

## Writing a unit test

Use `@RunWith(MockitoJUnitRunner.class)`, put `@InjectMocks` on the production
implementation class, and `@Mock` only the collaborators that class actually
touches.

```java
@RunWith(MockitoJUnitRunner.class)
public class WorkManagerImplMockTest {

    @Mock
    private WorkDao workDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private WorkManagerImpl workManager = new WorkManagerImpl(100, 100);

    @Test
    public void checkSourceAndRemoveWorkRefusesAWorkSourcedByAnotherClient() {
        WorkEntity work = new WorkEntity();
        work.setId(WORK_ID);
        work.setOrcid(Actors.USER_A);
        work.setClientSourceId(Actors.CLIENT_B);
        when(workDao.getWork(Actors.USER_A, WORK_ID)).thenReturn(work);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(work);

        try {
            workManager.checkSourceAndRemoveWork(Actors.USER_A, WORK_ID);
            fail("a client must not be able to delete a work another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(workDao, never()).removeWork(anyString(), anyLong());
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
```

Copied from `orcid-core/src/test/java/org/orcid/core/manager/v3/WorkManagerImplMockTest.java`,
so it compiles and runs as written. Two details in it are the point of the whole
example, and both were got wrong before:

- **No actor is set.** At the manager layer nothing reads the security context:
  `SourceManager` and `OrcidSecurityManager` are both mocks, so the five stubbings
  `Actors.memberClient(...)` installs on a bearer token are never touched and the
  strict runner fails the class with `UnnecessaryStubbingException`. Use the
  `Actors` constants for vocabulary and set an actor only where something reads it,
  which means the delegator and above.
- **`WrongSourceException` has one constructor and it takes the params map.** The
  parameters are worth asserting: they are what the API renders, and asserting the
  type alone passes against an exception thrown for a different reason.

The `verify(..., never())` line is the half that matters. Asserting the exception
proves the guard ran; asserting the DAO was never called proves it ran *before*
the write. A guard moved below the write still throws, and only the `never()`
catches it -- see R2.

### Actors

`org.orcid.core.utils.Actors` puts one of the Registry's actors into the
security context. Each entry point authenticates differently, and hand-rolling
the wrong token shape is the usual way to write a security test that passes
without proving anything.

| Call | Models |
| --- | --- |
| `Actors.user(orcid)` | a signed-in user, `ROLE_USER` |
| `Actors.admin()` | an administrator, `ROLE_ADMIN` plus `ROLE_USER` |
| `Actors.group(orcid)` | a member account, `ROLE_GROUP` |
| `Actors.delegate(realOrcid, effectiveOrcid)` | a trusted individual acting for another record |
| `Actors.memberClient(clientId, userOrcid, scopes...)` | a member API client holding a user's token |
| `Actors.clientCredentials(clientId, scopes...)` | a machine client with no user behind it |
| `Actors.publicClient()` | a public API client |
| `Actors.anonymous()` | no authentication at all |
| `Actors.clear()` | empties the context — call from `@After`, always |

`Actors.USER_A` acts and `Actors.USER_B` is the record that must be left alone,
so a cross-user test reads the same way in every module. `CLIENT_A` and
`CLIENT_B` do the same for "this item was created by a different member".

The security context is static. A test that sets an actor and does not clear it
authorises the next test in the same JVM.

## The rules

These govern the migration away from Spring-context and DBUnit tests, and they
are what a reviewer checks.

**R1 — Test the class in the layer that owns the rule.** DAOs are database
tests. Managers mock DAOs and caches. Delegators and controllers mock managers
and the security manager.

**R2 — A removed integration assertion becomes two unit assertions.** When an
integration test proved an access-control outcome by driving the real security
manager against fixtures, the replacement needs both halves, and the pull
request lists the mapping:

- at the boundary, `verify(...)` that the guard was invoked with the right
  arguments, and a `doThrow(...)` case proving the thrown exception stops the
  operation;
- on the security manager itself, a test proving it throws for that input.

Only the first half is not enough. It shows the guard was called, not that the
guard is right.

**R3 — A rule enforced in SQL stays a database test.** `and orcid = :orcid` in
a DELETE is not observable through a mocked DAO. Categorise those with
`@Category(DatabaseTest.class)`.

**R4 — Coverage measures, it does not gate.** No thresholds, and no obligation
to write tests for code that has none today.

**R5 — Read the executed test count before and after.** A drop is acceptable
when the R2 mapping explains it. An unexplained drop is a defect.

**R6 — Delete the old test in the same pull request as its replacement**, never
earlier.

**R7 — Mutate before you trust.** A green suite is not evidence that it proves
anything. Break the guard the test claims to cover, run the test, and check it
fails:

```bash
# disable the guard, temporarily
#   public void checkSourceAndThrow(...) { if (true) { return; } ... }
mvn test --projects orcid-core -Dtest='org.orcid.core.manager.v3.OrcidSecurityManager*Test'
# then restore the production file
```

This costs minutes and is the only thing that distinguishes a test which proves
a rule from one that merely exercises it. It is how we learned that
`checkSourceAndThrow` — the rule stopping one member from modifying an item
another member created — could be deleted outright with all 190 tests still
green, before *and* after the migration.

Restore the production file immediately afterwards, and never stage it. Stage
explicit paths, never `git add -A`.

## What not to do

- Do not assert on call order when the outcome is what matters. A test that
  mirrors the implementation line by line passes for any behaviour.
- Do not mock a value object. Build it.
- Do not mock collaborators the class under test never touches; unnecessary
  stubs fail the strict Mockito runner and hide which dependencies are real.
- Do not keep a Spring context "just for the fixtures". Name the fact the
  fixture supplied and stub it.
- Do not add a security fix while migrating a test. Report it and let it be
  scheduled separately — a fix describes the weakness it closes to everyone who
  can read the branch, days before it is deployed.
