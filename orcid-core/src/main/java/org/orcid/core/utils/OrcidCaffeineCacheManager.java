package org.orcid.core.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * Registry and manager for programmatic Caffeine caches.
 */
public class OrcidCaffeineCacheManager {

    private final ConcurrentMap<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();
    private final Set<Cache<?, ?>> registeredCaches = ConcurrentHashMap.newKeySet();

    public void registerCache(String name, Cache<?, ?> cache) {
        caches.put(name, cache);
        registeredCaches.add(cache);
    }

    public void unregisterCache(String name, Cache<?, ?> cache) {
        caches.remove(name, cache);
        registeredCaches.remove(cache);
    }

    public Cache<?, ?> getCache(String name) {
        return caches.get(name);
    }

    public void clearAll() {
        for (Cache<?, ?> cache : registeredCaches) {
            cache.invalidateAll();
        }
    }

    public ConcurrentMap<String, Cache<?, ?>> getCaches() {
        return caches;
    }
}
