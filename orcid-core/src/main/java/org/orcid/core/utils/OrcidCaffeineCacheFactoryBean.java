package org.orcid.core.utils;

import java.time.Duration;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

public class OrcidCaffeineCacheFactoryBean implements FactoryBean<Cache<?, ?>>, InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidCaffeineCacheFactoryBean.class);

    private OrcidCaffeineCacheManager cacheManager;

    private String cacheName;

    private int maxElementsInMemory = 10000;

    private int maxMegaBytesInMemory = 0;

    private int timeToIdleSeconds = 60;

    private int timeToLiveSeconds = 0;

    private long maxMegaBytesOnDisk = 0;

    private boolean copyValues = true;

    private Object cacheLoaderWriter;

    private Cache<?, ?> cache;

    public OrcidCaffeineCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(OrcidCaffeineCacheManager cacheManager) {
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

    public int getMaxMegaBytesInMemory() {
        return maxMegaBytesInMemory;
    }

    public void setMaxMegaBytesInMemory(int maxMegaBytesInMemory) {
        this.maxMegaBytesInMemory = maxMegaBytesInMemory;
    }

    public int getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(int timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
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

    public Object getCacheLoaderWriter() {
        return cacheLoaderWriter;
    }

    public void setCacheLoaderWriter(Object cacheLoaderWriter) {
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

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        long maxSize = maxMegaBytesInMemory > 0 ? (long) maxMegaBytesInMemory * 1000 : (maxElementsInMemory > 0 ? maxElementsInMemory : 10000);
        builder.maximumSize(maxSize);

        if (timeToIdleSeconds > 0) {
            builder.expireAfterAccess(Duration.ofSeconds(timeToIdleSeconds));
        } else if (timeToLiveSeconds > 0) {
            builder.expireAfterWrite(Duration.ofSeconds(timeToLiveSeconds));
        }

        if (cacheLoaderWriter != null) {
            if (cacheLoaderWriter instanceof CacheLoader) {
                this.cache = builder.build((CacheLoader<Object, Object>) cacheLoaderWriter);
            } else if (cacheLoaderWriter instanceof Function) {
                Function<Object, Object> fn = (Function<Object, Object>) cacheLoaderWriter;
                this.cache = builder.build(fn::apply);
            } else {
                try {
                    java.lang.reflect.Method loadMethod = cacheLoaderWriter.getClass().getMethod("load", Object.class);
                    this.cache = builder.build(key -> {
                        try {
                            return loadMethod.invoke(cacheLoaderWriter, key);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (NoSuchMethodException e) {
                    this.cache = builder.build();
                }
            }
        } else {
            this.cache = builder.build();
        }

        if (cacheManager != null && cacheName != null) {
            cacheManager.registerCache(cacheName, this.cache);
        }
    }

    @Override
    public void destroy() {
        if (cacheManager != null && cacheName != null && cache != null) {
            cacheManager.unregisterCache(cacheName, cache);
        }
    }
}
