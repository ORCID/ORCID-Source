package org.orcid.frontend.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

/**
 * Regression tests for the concurrency defect that took a node down on 2026-07-15.
 *
 * <p>{@link LanguagesMap} lazily populated an unsynchronised static {@code TreeMap} on the
 * request path. When a freshly restarted node took full traffic with an empty map, concurrent
 * {@code put}s corrupted the red-black tree (cyclic child pointers) and every {@code getEntry}
 * walk spun forever RUNNABLE, saturating all Tomcat exec threads and tripping the watchdog.
 *
 * <p>The stress test below drives the exact pattern: many threads racing to insert many distinct
 * locales. Against the old {@code TreeMap} it spins (caught by the hard test timeout); against the
 * {@code ConcurrentHashMap}/{@code computeIfAbsent} fix it completes quickly and consistently.
 */
public class LanguagesMapTest {

    private final LanguagesMap languagesMap = new LanguagesMap();

    @Test
    public void returnsNonEmptyMapForLocale() {
        Map<String, String> map = languagesMap.getLanguagesMap(Locale.US);
        assertNotNull(map);
        assertFalse("expected available languages for en_US", map.isEmpty());
    }

    @Test
    public void nullLocaleDefaultsToUsAndDoesNotThrow() {
        Map<String, String> fromNull = languagesMap.getLanguagesMap(null);
        Map<String, String> fromUs = languagesMap.getLanguagesMap(Locale.US);
        assertNotNull(fromNull);
        assertEquals(fromUs, fromNull);
    }

    @Test
    public void repeatedCallsReturnTheSameCachedInstance() {
        Map<String, String> first = languagesMap.getLanguagesMap(Locale.FRANCE);
        Map<String, String> second = languagesMap.getLanguagesMap(Locale.FRANCE);
        assertSame("locale lookup should be memoised, not rebuilt per call", first, second);
    }

    /**
     * Hammer the lazy-population path from many threads across many distinct locales.
     * A hard timeout guards against the CPU spin the old implementation exhibited.
     */
    @Test(timeout = 30000)
    public void concurrentPopulationDoesNotCorruptTheMapOrSpin() throws Exception {
        Locale[] locales = Locale.getAvailableLocales();
        assertTrue("JVM should expose many locales to race on", locales.length > 20);

        final int threads = 32;
        final int iterationsPerThread = 200;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        for (int t = 0; t < threads; t++) {
            final int seed = t;
            futures.add(pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        Locale locale = locales[(seed + i) % locales.length];
                        Map<String, String> map = languagesMap.getLanguagesMap(locale);
                        assertNotNull(map);
                    }
                } catch (Throwable th) {
                    failure.compareAndSet(null, th);
                }
            }));
        }

        start.countDown();
        pool.shutdown();
        if (!pool.awaitTermination(25, TimeUnit.SECONDS)) {
            pool.shutdownNow();
            fail("getLanguagesMap did not complete under concurrency - likely spinning on a corrupted map");
        }
        if (failure.get() != null) {
            throw new AssertionError("concurrent access failed", failure.get());
        }

        // Post-conditions: every distinct locale resolves to a stable, cached instance.
        for (Locale locale : locales) {
            Map<String, String> a = languagesMap.getLanguagesMap(locale);
            Map<String, String> b = languagesMap.getLanguagesMap(locale);
            assertSame("locale " + locale + " should be cached after warm-up", a, b);
        }
    }
}
