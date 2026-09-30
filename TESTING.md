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

`orcid-web`, `orcid-api-web` and `orcid-pub-web` read two shared security-context
helpers from a jar that `orcid-core` attaches, so testing one of them on its own
needs a current `orcid-core` in the local repository. If `--projects orcid-web`
fails to resolve `org.orcid:orcid-core:jar:tests`, run `mvn install --projects
orcid-core` once, or add `-am`. The same applies after switching branches, since
every module carries the same fixed version.

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
tree. Re-verified against v3.23.10 on 2026-09-21; see the section after the table.

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

### Re-verified after the helpers moved out of production

`Actors` and `SecurityContextTestUtils` were moved from `orcid-core/src/main` to
that module's test tree so that Mockito could stop being a compile dependency.
`Actors` is load-bearing for the table above, so the move was checked rather than
assumed. Re-run on 2026-09-05 against `fd4dc071dd`:

| Check | Result |
| --- | --- |
| Unit stage, clean tree | 3,597 green |
| Database stage, clean tree | 763 green |
| Web modules built alone, as CI does | 383, 1,232, 405 green |
| First three mutations above, control first | each red on its own test, production restored byte-identically |
| `mockito-core` in all seven WARs | 592KB present in every one before, absent from every one after |
| `orcid-core` test jar contents | the two helper classes, no test resources |
| Release-path build, `-Dmaven.test.skip`, jar deleted first | green, jar reattached with both classes |
| `actionlint`, `db_tests` matrix drift | both clean |

The three web modules read the helpers from a test jar that `orcid-core` now
attaches. It carries those two classes and nothing else on purpose: `orcid-core`'s
test tree also holds a `test-core-context.xml` and a `log4j.xml` that those modules
define themselves, and shipping the whole tree would leave which copy
`DBUnitTest`'s `classpath:test-core-context.xml` resolves to a matter of classpath
order. `byte-buddy` stays in the WARs; Hibernate needs it, and it is declared for
that reason, not for Mockito.

That jar is attached even under `-Dmaven.test.skip`, which the release path passes.
The `test-jar` goal honours that flag by default, so without the override a release
build would attach nothing and the three web modules could not be resolved at all.
The two helper classes are compiled under the same flag for the same reason.

One consequence for local work: the three web modules can no longer be tested alone
against a local repository that predates this change. Run `mvn install --projects
orcid-core` once, or add `-am`, before `mvn test --projects orcid-web`.

### Re-verified against v3.23.10

`main` moved 270 commits between the fork point and 2026-09-21, most of them the
Java 21 transaction work: `@Transactional` on the DAOs, a pooled HSQLDB datasource
with `autoCommit=false`, and an existing-activities list added to the work, funding
and affiliation managers. Production moved under the proofs above, so they were
re-run rather than assumed. Merged at `43b125bb2c` (v3.23.10):

| Check | Result |
| --- | --- |
| Unit stage, clean tree | 3,937 green |
| Database stage, clean tree | 734 green |
| v3 work manager: delete the remove-path source guard | red on `v3.WorkManagerImplMockTest` |
| v2 security manager: `checkSource` returns early | red on `OrcidSecurityManager_SourceTest` |
| v3 delegator: move the profile guard below the manager call | red on `...ErrorsTest`, 5 delete cases |
| v3 token check: invert the claimed predicate | red on `v3.OrcidSecurityManager_generalTest` |
| v2 address manager: delete the delete-path source guard | red on `AddressManagerImplMockTest` |
| Production restored after every mutation | byte-identical, working tree clean |
| `actionlint`, `db_tests` matrix drift | both clean |

The unit stage is up 340 and the database stage down 29, and the two move together:
`main` independently rewrote `EmailManagerTest`, `ProfileEntityManagerImplTest`,
`PasswordResetControllerTest`, `FundingsControllerTest` and
`PublicProfileControllerTest` onto Mockito while this branch was open. Its versions
are supersets, so they are the ones kept, and the three that no longer need a
database moved from the second count into the first. Four proofs those files held
here but `main`'s versions did not were ported across: that deactivating or
deprecating a record drops its user connections and its notifications, that a reset
token whose redis entry has gone is refused, that a successful reset clears the
sign-in lock, and that a refused funding edit does not fall through to a create.

Two things `main` changed that are worth knowing:

- **J21-005 tightened the client-credentials rule.** A client with
  `ORCID_PROFILE_CREATE` may now act on an unclaimed record only if it is that
  record's source. The positive test here was asserting the old rule, so its
  fixture now gives the record a source. The new half of the predicate is proved
  nowhere, on either side: `main` shipped `d089fb9d57` without a test, and its only
  related coverage is the claimed refusal these tests already held. Worth a ticket.
- **`PublicProfileControllerTest` lost a proof to mocking.** `main`'s version stubs
  `getGroupedAffiliations(orcid, true)` with a public-only fixture, so the manager's
  visibility filtering is asserted by nothing. The production method this branch's
  version drove no longer exists, so there is nothing to port; the filtering belongs
  in an `AffiliationsManagerReadOnly` database test. Worth a ticket.

## The database stage

Both stages run in full on every push. The unit stage is eleven parallel jobs,
one per module; the database stage is six, one per module that holds a test
marked `@Category(DatabaseTest.class)`. The split exists so the unit stage can
report in a couple of minutes without waiting on a database, not so that work
can be skipped -- nothing selects which tests to run based on what changed.

The other five reactor modules are absent from the database matrix because they
hold no database test, and each would pay about 100s of setup to run nothing.
That is the one omission, and `db_stage_drift` guards it: it reads the matrix
out of this workflow and the module list out of the root pom, and fails the
build if a module gains its first database test without being added. It warns
in the other direction, where a listed module no longer has any.

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
