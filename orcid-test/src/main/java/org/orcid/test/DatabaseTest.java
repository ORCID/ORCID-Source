package org.orcid.test;

/**
 * JUnit 4 category marker for a test that needs a real database.
 *
 * <p>
 * Tests carrying {@code @Category(DatabaseTest.class)} are excluded from the
 * default {@code mvn test} run and executed by the {@code db-tests} profile
 * instead:
 *
 * <pre>
 * mvn test                  # unit tests only (fast, no database, no Spring context)
 * mvn test -P db-tests      # only the database tests
 * mvn test -P all-tests     # both
 * </pre>
 *
 * <p>
 * Use it for the rules a mock genuinely cannot prove, not as a way to keep a
 * slow test alive. The clearest case is a rule enforced by a SQL predicate: a
 * DAO whose delete is scoped {@code and orcid = :orcid} is only shown to reject
 * another record's identifier by running the statement against a database. A
 * mocked DAO would return whatever the test told it to and prove nothing.
 *
 * <p>
 * Everything else -- a manager's branching, a delegator's guard invocation, a
 * controller's response -- belongs in a mocked unit test with no category.
 *
 * @see org.orcid.test.DBUnitTest
 */
public interface DatabaseTest {
}
