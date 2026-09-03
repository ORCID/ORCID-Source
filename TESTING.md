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

## Writing a unit test

Use `@RunWith(MockitoJUnitRunner.class)`, put `@InjectMocks` on the production
implementation class, and `@Mock` only the collaborators that class actually
touches.

```java
@RunWith(MockitoJUnitRunner.class)
public class WorkManagerImplTest {

    @InjectMocks
    private WorkManagerImpl workManager = new WorkManagerImpl();

    @Mock
    private WorkDao workDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @After
    public void after() {
        Actors.clear();
    }

    @Test
    public void deleteRefusesAWorkSourcedByAnotherClient() {
        Actors.memberClient(Actors.CLIENT_A, Actors.USER_A, ScopePathType.ORCID_WORKS_UPDATE);
        WorkEntity work = Works.sourcedBy(Actors.CLIENT_B);
        when(workDao.getWork(Actors.USER_A, 1L)).thenReturn(work);
        doThrow(new WrongSourceException()).when(orcidSecurityManager).checkSourceAndThrow(work);

        assertThrows(WrongSourceException.class,
                () -> workManager.checkSourceAndRemoveWork(Actors.USER_A, 1L));
        verify(workDao, never()).removeWork(anyString(), anyLong());
    }
}
```

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
