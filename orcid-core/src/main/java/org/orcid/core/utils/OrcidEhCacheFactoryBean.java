package org.orcid.core.utils;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidEhCacheFactoryBean implements FactoryBean<Cache<?, ?>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidEhCacheFactoryBean.class);

    private CacheManager cacheManager;

    private String cacheName;

    private int maxElementsInMemory = 10000;

    private int timeToLiveSeconds = 120;

    private int timeToIdleSeconds = 120;

    private int maxElementsOnDisk = 0;

    private String maxBytesLocalDisk = "5368709120";

    private boolean copyOnRead = true;

    private boolean copyOnWrite = true;

    private String strategy = "NONE";

    private CacheLoaderWriter<Object, Serializable> cacheLoaderWriter;

    private Cache<?, ?> cache;

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public int getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public int getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(int timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public int getMaxElementsOnDisk() {
        return maxElementsOnDisk;
    }

    public void setMaxElementsOnDisk(int maxElementsOnDisk) {
        this.maxElementsOnDisk = maxElementsOnDisk;
    }

    public String getMaxBytesLocalDisk() {
        return maxBytesLocalDisk;
    }

    public void setMaxBytesLocalDisk(String maxBytesLocalDisk) {
        this.maxBytesLocalDisk = maxBytesLocalDisk;
    }

    public boolean isCopyOnRead() {
        return copyOnRead;
    }

    public void setCopyOnRead(boolean copyOnRead) {
        this.copyOnRead = copyOnRead;
    }

    public boolean isCopyOnWrite() {
        return copyOnWrite;
    }

    public void setCopyOnWrite(boolean copyOnWrite) {
        this.copyOnWrite = copyOnWrite;
    }

    public void setCacheLoaderWriter(CacheLoaderWriter<Object, Serializable> cacheLoaderWriter) {
        this.cacheLoaderWriter = cacheLoaderWriter;
    }

    @Override
    public Cache<?, ?> getObject() {
        return this.cache;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.cache != null ? this.cache.getClass() : Cache.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Cache<?, ?> existingCache = cacheManager.getCache(cacheName, Object.class, Serializable.class);
        if (existingCache == null) {
            ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.heap(this.maxElementsInMemory);
            if (StringUtils.isNotBlank(this.maxBytesLocalDisk)) {
                resourcePoolsBuilder.disk(Long.valueOf(this.maxBytesLocalDisk).longValue(), MemoryUnit.B);
            }
            CacheConfigurationBuilder<Object, Serializable> cacheConfigurationBuilder = CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Serializable.class, resourcePoolsBuilder)
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.of(this.timeToLiveSeconds, ChronoUnit.SECONDS)));
            if (this.copyOnRead || this.copyOnWrite) {
                cacheConfigurationBuilder = cacheConfigurationBuilder.withValueSerializingCopier();
            }
            if (this.cacheLoaderWriter != null) {
                cacheConfigurationBuilder = cacheConfigurationBuilder.withLoaderWriter(this.cacheLoaderWriter);
            }
            this.cache = cacheManager.createCache(cacheName, cacheConfigurationBuilder.build());
        } else {
            this.cache = existingCache;
        }
    }

}
