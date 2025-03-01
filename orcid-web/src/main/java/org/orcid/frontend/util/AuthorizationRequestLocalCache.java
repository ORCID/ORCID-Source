package org.orcid.frontend.util;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class AuthorizationRequestLocalCache {

    @Value("${org.orcid.core.session.localCache.ttl:900}")
    private int cacheTTLInSeconds;

    @Value("${org.orcid.core.session.localCache.heap:10000}")
    private int heapSize;

    private CacheManager cacheManager;
    private Cache<String, AuthorizationRequest> cache;

    public AuthorizationRequestLocalCache() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().build();
        cacheManager.init();
    }

    @PostConstruct
    public void initCache() {
        cache = cacheManager
                .createCache("authorizationRequestLocalCache", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                String.class, AuthorizationRequest.class,
                                ResourcePoolsBuilder.heap(heapSize)).withExpiry(Expirations.timeToLiveExpiration(Duration.of(cacheTTLInSeconds, TimeUnit.SECONDS))));
    }

    public AuthorizationRequest get(String key) {
        return cache.get(key);
    }

    public void put(String key, AuthorizationRequest value) {
        cache.put(key, value);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
