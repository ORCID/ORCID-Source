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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class RequestInfoFormLocalCache {

    @Value("${org.orcid.core.session.localCache.ttl:10800}")
    private int cacheTTLInSeconds;

    @Value("${org.orcid.core.session.localCache.heap:50000}")
    private int heapSize;

    private CacheManager cacheManager;
    private Cache<String, RequestInfoForm> cache;

    public RequestInfoFormLocalCache() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().build();
        cacheManager.init();
    }

    @PostConstruct
    public void initCache() {
        cache = cacheManager
                .createCache("requestInfoFormLocalCache", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                String.class, RequestInfoForm.class,
                                ResourcePoolsBuilder.heap(heapSize)).withExpiry(Expirations.timeToLiveExpiration(Duration.of(cacheTTLInSeconds, TimeUnit.SECONDS))));
    }

    public RequestInfoForm get(String key) {
        return cache.get(key);
    }

    public void put(String key, RequestInfoForm value) {
        cache.put(key, value);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
