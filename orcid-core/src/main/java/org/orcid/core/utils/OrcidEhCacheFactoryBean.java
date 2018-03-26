package org.orcid.core.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidEhCacheFactoryBean implements FactoryBean<Ehcache>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidEhCacheFactoryBean.class);

    private CacheManager cacheManager;

    private String cacheName;

    private int maxElementsInMemory = 10000;

    private int timeToLiveSeconds = 120;

    private int timeToIdleSeconds = 120;

    private int maxElementsOnDisk = 0;

    private String maxBytesLocalDisk = "5g";

    private boolean copyOnRead = true;

    private boolean copyOnWrite = true;

    private String strategy = "NONE";

    private CacheEntryFactory cacheEntryFactory;

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

    public CacheEntryFactory getCacheEntryFactory() {
        return cacheEntryFactory;
    }

    public void setCacheEntryFactory(CacheEntryFactory cacheEntryFactory) {
        this.cacheEntryFactory = cacheEntryFactory;
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
        Ehcache existingCache = cacheManager.getEhcache(cacheName);
        String diskStorePath = cacheManager.getConfiguration().getDiskStoreConfiguration().getPath();
        LOGGER.debug("Cache manager disk store path = " + diskStorePath);
        if (existingCache == null) {
            CacheConfiguration config = createConfig();
            if (cacheEntryFactory != null) {
                this.cache = new SelfPopulatingCache(new Cache(config), cacheEntryFactory);
            } else {
                this.cache = new Cache(config);
            }
            cacheManager.addCache(this.cache);
        } else {
            this.cache = existingCache;
        }
    }

    private CacheConfiguration createConfig() {
        CacheConfiguration config = new CacheConfiguration();
        config.setName(this.cacheName);
        config.setMaxEntriesLocalHeap(this.maxElementsInMemory);
        config.setMaxElementsOnDisk(this.maxElementsOnDisk);
        config.setMaxBytesLocalDisk(this.maxBytesLocalDisk);
        config.setTimeToLiveSeconds(this.timeToLiveSeconds);
        config.setTimeToIdleSeconds(this.timeToIdleSeconds);
        config.setCopyOnRead(this.copyOnRead);
        config.setCopyOnWrite(this.copyOnWrite);
        config.persistence(new PersistenceConfiguration().strategy(this.strategy));
        return config;
    }

}
