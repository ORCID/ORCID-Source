package org.orcid.core.utils;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

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

    private int maxElementsInMemory = 100;

    private int timeToIdleSeconds = 60;

    private long maxMegaBytesOnDisk = 0;

    private boolean copyValues = true;

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

    public int getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(int timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public long getMaxMegaBytesOnDisk() {
        return maxMegaBytesOnDisk;
    }

    public void setMaxMegaBytesOnDisk(long maxMegaBytesOnDisk) {
        this.maxMegaBytesOnDisk = maxMegaBytesOnDisk;
    }

    public boolean isCopyValues() {
        return copyValues;
    }

    public void setCopyValues(boolean copyValues) {
        this.copyValues = copyValues;
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
            if (this.maxMegaBytesOnDisk > 0) {
                resourcePoolsBuilder.disk(this.maxMegaBytesOnDisk, MemoryUnit.MB);
            }
            CacheConfigurationBuilder<Object, Serializable> cacheConfigurationBuilder = CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Serializable.class, resourcePoolsBuilder)
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.of(this.timeToIdleSeconds, ChronoUnit.SECONDS)));
            if (this.copyValues) {
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
