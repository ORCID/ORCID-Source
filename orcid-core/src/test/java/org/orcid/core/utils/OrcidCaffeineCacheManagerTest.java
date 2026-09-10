package org.orcid.core.utils;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class OrcidCaffeineCacheManagerTest {

    @Test
    public void clearAllClearsCachesWithTheSameNameFromDifferentContexts() {
        OrcidCaffeineCacheManager cacheManager = new OrcidCaffeineCacheManager();
        Cache<String, String> firstContextCache = Caffeine.newBuilder().build();
        Cache<String, String> secondContextCache = Caffeine.newBuilder().build();
        firstContextCache.put("key", "first");
        secondContextCache.put("key", "second");
        cacheManager.registerCache("shared-name", firstContextCache);
        cacheManager.registerCache("shared-name", secondContextCache);

        cacheManager.clearAll();

        assertNull(firstContextCache.getIfPresent("key"));
        assertNull(secondContextCache.getIfPresent("key"));
    }
}