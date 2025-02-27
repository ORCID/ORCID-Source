package org.orcid.frontend.util;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class OriginalAuthorizationRequestLocalCache {

    @Value("${org.orcid.core.session.localCache.ttl:900}")
    private int cacheTTLInSeconds;

    @Value("${org.orcid.core.session.localCache.heap:10000}")
    private int heapSize;

    private CacheManager cacheManager;
    private Cache<String, Map> cache;

    public OriginalAuthorizationRequestLocalCache() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().build();
        cacheManager.init();
    }

    @PostConstruct
    public void initCache() {
        cache = cacheManager
                .createCache("squaredNumber", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                String.class, Map.class,
                                ResourcePoolsBuilder.heap(heapSize)).withExpiry(Expirations.timeToLiveExpiration(Duration.of(cacheTTLInSeconds, TimeUnit.SECONDS))));
    }

    public Map get(String key) {
        return cache.get(key);
    }

    public void put(String key, Map value) {
        cache.put(key, value);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
