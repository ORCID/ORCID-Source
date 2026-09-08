package org.orcid.core.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * Registry and manager for programmatic Caffeine caches.
 */
public class OrcidCaffeineCacheManager {

    private final ConcurrentMap<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();

    public void registerCache(String name, Cache<?, ?> cache) {
        caches.put(name, cache);
    }

    public Cache<?, ?> getCache(String name) {
        return caches.get(name);
    }

    public void clearAll() {
        for (Cache<?, ?> cache : caches.values()) {
            cache.invalidateAll();
        }
    }

    public ConcurrentMap<String, Cache<?, ?>> getCaches() {
        return caches;
    }
}
