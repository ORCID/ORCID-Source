package org.orcid.test;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Mockito's strict runner, kept strict under the build's category filter.
 *
 * <p>
 * {@link MockitoJUnitRunner} fails a class whose tests left a stubbing unused,
 * but only when the class ran unfiltered: once anything calls
 * {@code filter(...)} it assumes a single test was picked out and skips the
 * check. Surefire applies the {@link DatabaseTest} category filter to every
 * class in both stages, so on the plain runner the check never ran in CI or
 * in a default {@code mvn test}, only in an IDE or under {@code -P all-tests}.
 *
 * <p>
 * A filter that keeps every test of the class has not picked anything out, so
 * this runner ignores it and the check runs. A filter that drops tests --
 * {@code -Dtest=Class#method}, or the database stage skipping a unit test --
 * is applied as before, and the check is skipped exactly as Mockito intends.
 */
public class OrcidMockitoJUnitRunner extends MockitoJUnitRunner {

    public OrcidMockitoJUnitRunner(Class<?> klass) throws InvocationTargetException {
        super(klass);
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        if (keepsEveryTest(filter, getDescription())) {
            return;
        }
        super.filter(filter);
    }

    private static boolean keepsEveryTest(Filter filter, Description description) {
        for (Description child : description.getChildren()) {
            if (!filter.shouldRun(child)) {
                return false;
            }
        }
        return true;
    }
}
