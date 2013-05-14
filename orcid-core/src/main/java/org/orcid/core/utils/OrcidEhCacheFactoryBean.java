/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidEhCacheFactoryBean implements FactoryBean<Ehcache>, InitializingBean {

    private CacheManager cacheManager;

    private String cacheName;

    private int maxElementsInMemory = 10000;

    private int timeToLiveSeconds = 120;

    private int timeToIdleSeconds = 120;

    private boolean copyOnRead = true;

    private boolean copyOnWrite = true;

    private Ehcache cache;

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

    @Override
    public Ehcache getObject() {
        return this.cache;
    }

    @Override
    public Class<? extends Ehcache> getObjectType() {
        return (this.cache != null ? this.cache.getClass() : Ehcache.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Cache existingCache = cacheManager.getCache(cacheName);
        if (existingCache == null) {
            CacheConfiguration config = createConfig();
            this.cache = new Cache(config);
            cacheManager.addCache(this.cache);
        }
    }

    private CacheConfiguration createConfig() {
        CacheConfiguration config = new CacheConfiguration();
        config.setName(this.cacheName);
        config.setMaxEntriesLocalHeap(this.maxElementsInMemory);
        config.setTimeToLiveSeconds(this.timeToLiveSeconds);
        config.setTimeToIdleSeconds(this.timeToIdleSeconds);
        config.setCopyOnRead(this.copyOnRead);
        config.setCopyOnWrite(this.copyOnWrite);
        return config;
    }

}
