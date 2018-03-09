package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.orcid.core.manager.SlackManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.util.TimeUtil;

/**
 * 
 * @author Will Simpson
 *
 */
public class WorkEntityCacheManagerImpl implements WorkEntityCacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkEntityCacheManagerImpl.class);

    // Default time to live will be one hour
    private static final int DEFAULT_TTL = 3600;

    // Default time to idle will be one hour
    private static final int DEFAULT_TTI = 3600;

    @Resource(name = "workLastModifiedCache")
    private Cache workLastModifiedCache;
    private int workLastModifiedCacheTTL;
    private int workLastModifiedCacheTTI;

    @Resource(name = "publicWorkLastModifiedCache")
    private Cache publicWorkLastModifiedCache;
    private int publicWorkLastModifiedCacheTTL;
    private int publicWorkLastModifiedCacheTTI;

    @Resource(name = "minimizedWorkEntityCache")
    private Cache minimizedWorkEntityCache;
    private int minimizedWorkEntityCacheTTL;
    private int minimizedWorkEntityCacheTTI;

    @Resource(name = "fullWorkEntityCache")
    private Cache fullWorkEntityCache;
    private int fullWorkEntityCacheTTL;
    private int fullWorkEntityCacheTTI;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private WorkDao workDao;

    @PostConstruct
    private void init() {
        CacheConfiguration config1 = fullWorkEntityCache.getCacheConfiguration();
        fullWorkEntityCacheTTI = config1.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config1.getTimeToIdleSeconds()) : DEFAULT_TTI;
        fullWorkEntityCacheTTL = config1.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config1.getTimeToLiveSeconds()) : DEFAULT_TTL;

        CacheConfiguration config2 = workLastModifiedCache.getCacheConfiguration();
        workLastModifiedCacheTTI = config2.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config2.getTimeToIdleSeconds()) : DEFAULT_TTI;
        workLastModifiedCacheTTL = config2.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config2.getTimeToLiveSeconds()) : DEFAULT_TTL;

        CacheConfiguration config3 = publicWorkLastModifiedCache.getCacheConfiguration();
        publicWorkLastModifiedCacheTTI = config3.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config3.getTimeToIdleSeconds()) : DEFAULT_TTI;
        publicWorkLastModifiedCacheTTL = config3.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config3.getTimeToLiveSeconds()) : DEFAULT_TTL;

        CacheConfiguration config4 = minimizedWorkEntityCache.getCacheConfiguration();
        minimizedWorkEntityCacheTTI = config4.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config4.getTimeToIdleSeconds()) : DEFAULT_TTI;
        minimizedWorkEntityCacheTTL = config4.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config4.getTimeToLiveSeconds()) : DEFAULT_TTL;    
    }

    @Resource
    private SlackManager slackManager;

    public void setWorkDao(WorkDao workDao) {
        this.workDao = workDao;
    }

    @Override
    public List<WorkLastModifiedEntity> retrieveWorkLastModifiedList(String orcid, long profileLastModified) {
        Object key = new ProfileCacheKey(orcid, profileLastModified, releaseName);
        List<WorkLastModifiedEntity> workLastModifiedList = null;
        try {
            workLastModifiedCache.acquireReadLockOnKey(key);
            workLastModifiedList = toWorkLastModifiedList(getElementFromCache(workLastModifiedCache, key, orcid));
        } finally {
            workLastModifiedCache.releaseReadLockOnKey(key);
        }
        if (workLastModifiedList == null) {
            try {
                workLastModifiedCache.acquireWriteLockOnKey(key);
                workLastModifiedList = toWorkLastModifiedList(getElementFromCache(workLastModifiedCache, key, orcid));
                if (workLastModifiedList == null) {
                    workLastModifiedList = workDao.getWorkLastModifiedList(orcid);
                    workLastModifiedCache.put(createElement(key, workLastModifiedList, workLastModifiedCache));
                }

            } finally {
                workLastModifiedCache.releaseWriteLockOnKey(key);
            }
        }
        return workLastModifiedList;
    }

    @Override
    public List<WorkLastModifiedEntity> retrievePublicWorkLastModifiedList(String orcid, long profileLastModified) {
        Object key = new ProfileCacheKey(orcid, profileLastModified, releaseName);
        List<WorkLastModifiedEntity> workLastModifiedList = null;
        try {
            publicWorkLastModifiedCache.acquireReadLockOnKey(key);
            workLastModifiedList = toWorkLastModifiedList(getElementFromCache(publicWorkLastModifiedCache, key, orcid));
        } finally {
            publicWorkLastModifiedCache.releaseReadLockOnKey(key);
        }
        if (workLastModifiedList == null) {
            try {
                publicWorkLastModifiedCache.acquireWriteLockOnKey(key);
                workLastModifiedList = toWorkLastModifiedList(getElementFromCache(publicWorkLastModifiedCache, key, orcid));
                if (workLastModifiedList == null) {
                    workLastModifiedList = workDao.getPublicWorkLastModifiedList(orcid);
                    publicWorkLastModifiedCache.put(createElement(key, workLastModifiedList, publicWorkLastModifiedCache));
                }

            } finally {
                publicWorkLastModifiedCache.releaseWriteLockOnKey(key);
            }
        }
        return workLastModifiedList;
    }
    
    @Override
    public MinimizedWorkEntity retrieveMinimizedWork(long workId, long workLastModified) {
        Object key = new WorkCacheKey(workId, releaseName);
        try {
            minimizedWorkEntityCache.acquireReadLockOnKey(key);
            getElementFromCache(minimizedWorkEntityCache, key, null);
        } finally {
            minimizedWorkEntityCache.releaseReadLockOnKey(key);
        }
        MinimizedWorkEntity minimizedWorkEntity = toMinimizedWork(getElementFromCache(minimizedWorkEntityCache, key, null));
        if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {

            try {
                minimizedWorkEntityCache.acquireWriteLockOnKey(key);
                minimizedWorkEntity = toMinimizedWork(getElementFromCache(minimizedWorkEntityCache, key, null));
                if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
                    minimizedWorkEntity = workDao.getMinimizedWorkEntity(workId);
                    workDao.detach(minimizedWorkEntity);
                    minimizedWorkEntityCache.put(createElement(key, minimizedWorkEntity, minimizedWorkEntityCache));
                }
            } finally {
                minimizedWorkEntityCache.releaseWriteLockOnKey(key);
            }
        }
        return minimizedWorkEntity;
    }
    
    /**
     * Retrieves a full WorkEntity
     * 
     * @param workId
     * @param workLastModified
     * @return a WorkEntity
     */
    @Override    
    public WorkEntity retrieveFullWork(String orcid, long workId, long workLastModified) {
        Object key = new WorkCacheKey(workId, releaseName);
        WorkEntity workEntity = null;

        try {
            fullWorkEntityCache.acquireReadLockOnKey(key);
            workEntity = (WorkEntity) toWorkBaseEntity(getElementFromCache(fullWorkEntityCache, key, orcid));
        } finally {
            fullWorkEntityCache.releaseReadLockOnKey(key);
        }
        if (workEntity == null || workEntity.getLastModified().getTime() < workLastModified) {
            try {
                fullWorkEntityCache.acquireWriteLockOnKey(key);
                workEntity = (WorkEntity) toWorkBaseEntity(getElementFromCache(fullWorkEntityCache, key, orcid));
                if (workEntity == null || workEntity.getLastModified().getTime() < workLastModified) {
                    workEntity = workDao.getWork(orcid, workId);
                    workDao.detach(workEntity);
                    fullWorkEntityCache.put(createElement(key, workEntity, fullWorkEntityCache));
                }

            } finally {
                fullWorkEntityCache.releaseWriteLockOnKey(key);
            }
        }
        return workEntity;
    }
    
    /**
     * Fetches a list of minimized works - does this by checking cache and then
     * fetching all misses in one go from the DB.
     * 
     * @param workIdsWithLastModified
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends WorkBaseEntity> List<T> retrieveWorkList(String orcid, Map<Long, Date> workIdsWithLastModified, Cache workCache,
            Function<List<Long>, List<T>> workRetriever) {
        WorkBaseEntity[] returnArray = new WorkBaseEntity[workIdsWithLastModified.size()];
        List<Long> fetchList = new ArrayList<Long>();
        Map<Long, Integer> fetchListIndexOrder = new LinkedHashMap<Long, Integer>();
        int index = 0;

        for (Long workId : workIdsWithLastModified.keySet()) {
            // get works from the cache if we can
            Object key = new WorkCacheKey(workId, releaseName);
            try {
                workCache.acquireReadLockOnKey(key);
                WorkBaseEntity cachedWork = toWorkBaseEntity(getElementFromCache(workCache, key, orcid));
                if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(workId).getTime()) {
                    fetchListIndexOrder.put(workId, index);
                    fetchList.add(workId);
                } else {
                    returnArray[index] = cachedWork;
                }
                index++;
            } finally {
                workCache.releaseReadLockOnKey(key);
            }
        }

        // now fetch all the others that are *not* in the cache
        if (fetchList.size() > 0) {
            List<? extends WorkBaseEntity> refreshedWorks = workRetriever.apply(fetchList);
            for (WorkBaseEntity mWorkRefreshedFromDB : refreshedWorks) {
                Object key = new WorkCacheKey(mWorkRefreshedFromDB.getId(), releaseName);
                try {
                    workCache.acquireWriteLockOnKey(key);
                    // check cache again here to prevent race condition
                    // since something could have updated while we were
                    // fetching from DB
                    // (or can we skip because new last modified is always
                    // going to be after profile last modified as provided)
                    WorkBaseEntity cachedWork = toWorkBaseEntity(getElementFromCache(workCache, key, orcid));
                    int returnListIndex = fetchListIndexOrder.get(mWorkRefreshedFromDB.getId());
                    if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(mWorkRefreshedFromDB.getId()).getTime()) {
                        workCache.put(createElement(key, mWorkRefreshedFromDB, workCache));
                        returnArray[returnListIndex] = mWorkRefreshedFromDB;
                    } else {
                        returnArray[returnListIndex] = cachedWork;
                    }

                } finally {
                    workCache.releaseWriteLockOnKey(key);
                }
            }
        }
        return (List<T>) Arrays.asList(returnArray);
    }

    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified) {
        Map<Long, Date> workIdsWithLastModified = retrieveWorkLastModifiedMap(orcid, profileLastModified);
        return retrieveWorkList(orcid, workIdsWithLastModified, minimizedWorkEntityCache, idList -> workDao.getMinimizedWorkEntities(idList));
    }

    @Override
    public List<MinimizedWorkEntity> retrievePublicMinimizedWorks(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrievePublicWorkLastModifiedList(orcid, profileLastModified);
        Map<Long, Date> workIdsWithLastModified = workLastModifiedList.stream()
                .collect(Collectors.toMap(WorkLastModifiedEntity::getId, WorkLastModifiedEntity::getLastModified, (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
        return this.retrieveWorkList(orcid, workIdsWithLastModified, minimizedWorkEntityCache, idList -> workDao.getMinimizedWorkEntities(idList));
    }

    private Map<Long, Date> retrieveWorkLastModifiedMap(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrieveWorkLastModifiedList(orcid, profileLastModified);
        // @formatter:off
        Map<Long, Date> workIdsWithLastModified = workLastModifiedList.stream().collect(Collectors.toMap(
            WorkLastModifiedEntity::getId, 
            WorkLastModifiedEntity::getLastModified, 
            (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
            }, 
            LinkedHashMap::new));
        // @formatter:off
        return workIdsWithLastModified;
    }

    private MinimizedWorkEntity toMinimizedWork(Element element) {
        return (MinimizedWorkEntity) (element != null ? element.getObjectValue() : null);
    }

    private WorkBaseEntity toWorkBaseEntity(Element element) {
        return (WorkBaseEntity) (element != null ? element.getObjectValue() : null);
    }
    
    @SuppressWarnings("unchecked")
    private List<WorkLastModifiedEntity> toWorkLastModifiedList(Element element) {
        return (List<WorkLastModifiedEntity>) (element != null ? element.getObjectValue() : null);
    }    
    
    private Element getElementFromCache(Cache cache, Object key, String orcid) {
        try {
            return cache.get(key);
        } catch(Exception e) {
            String message;
            if(PojoUtil.isEmpty(orcid)) {
                message = String.format("Exception fetching element: '%s'.\n%s", key, e.getMessage());
            } else {
                message = String.format("Exception fetching element: '%s' that belongs to '%s'.\n%s", key, orcid, e.getMessage());
            }            
            LOGGER.error(message, e);
            slackManager.sendSystemAlert(message);
            throw e;
        }
    }

    @Override
    public void evictExpiredElements() {   
        // Evict full works
        LOGGER.info("Elements on fullWorkEntityCache before eviction: " + fullWorkEntityCache.getSize());
        fullWorkEntityCache.evictExpiredElements();
        fullWorkEntityCache.flush();
        LOGGER.info("Elements on fullWorkEntityCache after eviction: " + fullWorkEntityCache.getSize());
        
        // Evict minimized works
        LOGGER.info("Elements on minimizedWorkEntityCache before eviction: " + minimizedWorkEntityCache.getSize());
        minimizedWorkEntityCache.evictExpiredElements();
        minimizedWorkEntityCache.flush();        
        LOGGER.info("Elements on minimizedWorkEntityCache after eviction: " + minimizedWorkEntityCache.getSize());        
    }
    
    private Element createElement(Object key, Object element, Cache cache) {
        if(cache.equals(workLastModifiedCache)) {  
            return new Element(key, element, workLastModifiedCacheTTI, workLastModifiedCacheTTL);
        } else if (cache.equals(publicWorkLastModifiedCache)) {
            return new Element(key, element, publicWorkLastModifiedCacheTTI, publicWorkLastModifiedCacheTTL);
        } else if (cache.equals(minimizedWorkEntityCache)) {
            return new Element(key, element, minimizedWorkEntityCacheTTI, minimizedWorkEntityCacheTTL);
        } else if (cache.equals(fullWorkEntityCache)) {
            return new Element(key, element, fullWorkEntityCacheTTI, fullWorkEntityCacheTTL);
        } else {
            return new Element(key, element, DEFAULT_TTI, DEFAULT_TTL);
        }                
    }   
        
    @Override
    @Deprecated
    public List<WorkEntity> retrieveFullWorks(String orcid, long profileLastModified) {
        Map<Long, Date> workIdsWithLastModified = retrieveWorkLastModifiedMap(orcid, profileLastModified);
        WorkEntity[] returnArray = new WorkEntity[workIdsWithLastModified.size()];
        List<Long> fetchList = new ArrayList<Long>();
        Map<Long, Integer> fetchListIndexOrder = new LinkedHashMap<Long, Integer>();
        int index = 0;

        for (Long workId : workIdsWithLastModified.keySet()) {
            // get works from the cache if we can
            Object key = new WorkCacheKey(workId, releaseName);
            try {
                fullWorkEntityCache.acquireReadLockOnKey(key);
                Element element = getElementFromCache(fullWorkEntityCache, key, orcid);
                WorkEntity cachedWork = (WorkEntity) (element != null ? element.getObjectValue() : null);                        
                if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(workId).getTime()) {
                    fetchListIndexOrder.put(workId, index);
                    fetchList.add(workId);
                } else {
                    returnArray[index] = cachedWork;
                }
                index++;
            } finally {
                fullWorkEntityCache.releaseReadLockOnKey(key);
            }
        }

        // now fetch all the others that are *not* in the cache
        if (fetchList.size() > 0) {
            List<WorkEntity> refreshedWorks = workDao.getWorkEntities(fetchList);
            for (WorkEntity mWorkRefreshedFromDB : refreshedWorks) {
                Object key = new WorkCacheKey(mWorkRefreshedFromDB.getId(), releaseName);
                try {
                    fullWorkEntityCache.acquireWriteLockOnKey(key);
                    // check cache again here to prevent race condition
                    // since something could have updated while we were
                    // fetching from DB
                    // (or can we skip because new last modified is always
                    // going to be after profile last modified as provided)
                    Element element = getElementFromCache(fullWorkEntityCache, key, orcid);
                    WorkEntity cachedWork = (WorkEntity) (element != null ? element.getObjectValue() : null);                        
                    int returnListIndex = fetchListIndexOrder.get(mWorkRefreshedFromDB.getId());
                    if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(mWorkRefreshedFromDB.getId()).getTime()) {
                        fullWorkEntityCache.put(createElement(key, mWorkRefreshedFromDB, fullWorkEntityCache));
                        returnArray[returnListIndex] = mWorkRefreshedFromDB;
                    } else {
                        returnArray[returnListIndex] = cachedWork;
                    }

                } finally {
                    fullWorkEntityCache.releaseWriteLockOnKey(key);
                }
            }
        }
        return (List<WorkEntity>) Arrays.asList(returnArray);
    }
}
