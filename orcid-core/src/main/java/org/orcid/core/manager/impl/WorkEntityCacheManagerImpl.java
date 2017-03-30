/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.utils.ReleaseNameUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 
 * @author Will Simpson
 *
 */
public class WorkEntityCacheManagerImpl implements WorkEntityCacheManager {    
    @Resource(name = "workLastModifiedCache")
    private Cache workLastModifiedCache;

    @Resource(name = "publicWorkLastModifiedCache")
    private Cache publicWorkLastModifiedCache;

    @Resource(name = "minimizedWorkEntityCache")
    private Cache minimizedWorkEntityCache;
    
    @Resource(name = "fullWorkEntityCache")
    private Cache fullWorkEntityCache;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private LockerObjectsManager lockers = new LockerObjectsManager();

    private LockerObjectsManager publicWorkLastModifiedListLockers = new LockerObjectsManager();

    private LockerObjectsManager lockerMinimizedWork = new LockerObjectsManager();
    
    private LockerObjectsManager lockerFullWork = new LockerObjectsManager();

    private WorkDao workDao;

    public void setWorkDao(WorkDao workDao) {
        this.workDao = workDao;
    }

    @Override
    public List<WorkLastModifiedEntity> retrieveWorkLastModifiedList(String orcid, long profileLastModified) {
        Object key = new ProfileCacheKey(orcid, profileLastModified, releaseName);
        List<WorkLastModifiedEntity> workLastModifiedList = toWorkLastModifiedList(workLastModifiedCache.get(key));
        if (workLastModifiedList == null) {
            try {
                synchronized (lockers.obtainLock(orcid)) {
                    workLastModifiedList = toWorkLastModifiedList(workLastModifiedCache.get(key));
                    if (workLastModifiedList == null) {
                        workLastModifiedList = workDao.getWorkLastModifiedList(orcid);
                        workLastModifiedCache.put(new Element(key, workLastModifiedList));
                    }
                }
            } finally {
                lockers.releaseLock(orcid);
            }
        }
        return workLastModifiedList;
    }

    @Override
    public List<WorkLastModifiedEntity> retrievePublicWorkLastModifiedList(String orcid, long profileLastModified) {
        Object key = new ProfileCacheKey(orcid, profileLastModified, releaseName);
        List<WorkLastModifiedEntity> workLastModifiedList = toWorkLastModifiedList(publicWorkLastModifiedCache.get(key));
        if (workLastModifiedList == null) {
            try {
                synchronized (publicWorkLastModifiedListLockers.obtainLock(orcid)) {
                    workLastModifiedList = toWorkLastModifiedList(publicWorkLastModifiedCache.get(key));
                    if (workLastModifiedList == null) {
                        workLastModifiedList = workDao.getPublicWorkLastModifiedList(orcid);
                        publicWorkLastModifiedCache.put(new Element(key, workLastModifiedList));
                    }
                }
            } finally {
                publicWorkLastModifiedListLockers.releaseLock(orcid);
            }
        }
        return workLastModifiedList;
    }

    @Override
    public MinimizedWorkEntity retrieveMinimizedWork(long workId, long workLastModified) {
        Object key = new WorkCacheKey(workId, releaseName);
        MinimizedWorkEntity minimizedWorkEntity = toMinimizedWork(minimizedWorkEntityCache.get(key));
        if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
            try {
                synchronized (lockerMinimizedWork.obtainLock(Long.toString(workId))) {
                    minimizedWorkEntity = toMinimizedWork(minimizedWorkEntityCache.get(key));
                    if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
                        minimizedWorkEntity = workDao.getMinimizedWorkEntity(workId);
                        workDao.detach(minimizedWorkEntity);                        
                        minimizedWorkEntityCache.put(new Element(key, minimizedWorkEntity));
                    }
                }
            } finally {
                lockerMinimizedWork.releaseLock(Long.toString(workId));
            }
        }
        return minimizedWorkEntity;
    }
    
    /**
     * Retrieves a full WorkEntity
     * @param workId
     * @param workLastModified
     * @return a WorkEntity
     */
    @Override
    public WorkEntity retrieveFullWork(String orcid, long workId, long workLastModified) {
        Object key = new WorkCacheKey(workId, releaseName);
        WorkEntity workEntity = (WorkEntity) toWorkBaseEntity(fullWorkEntityCache.get(key));
        if (workEntity == null || workEntity.getLastModified().getTime() < workLastModified) {
            try {
                synchronized (lockerFullWork.obtainLock(Long.toString(workId))) {
                    workEntity = (WorkEntity) toWorkBaseEntity(fullWorkEntityCache.get(key));
                    if (workEntity == null || workEntity.getLastModified().getTime() < workLastModified) {
                        workEntity = workDao.getWork(orcid, workId);
                        workDao.detach(workEntity);                        
                        fullWorkEntityCache.put(new Element(key, workEntity));
                    }
                }
            } finally {
                lockerMinimizedWork.releaseLock(Long.toString(workId));
            }
        }
        return workEntity;
    }
            
    /**
     * Fetches a list of minimised works - does this by checking cache and then
     * fetching all misses in one go from the DB.
     * 
     * @param workIdsWithLastModified
     * @return
     */
    @Override
    public <T extends WorkBaseEntity> List<T> retrieveWorkList(Map<Long, Date> workIdsWithLastModified, Cache workCache,
            LockerObjectsManager lockerObjectsManager, Function<List<Long>, List<T>> workRetriever) {
        WorkBaseEntity[] returnArray = new WorkBaseEntity[workIdsWithLastModified.size()];
        List<Long> fetchList = new ArrayList<Long>();
        Map<Long, Integer> fetchListIndexOrder = new LinkedHashMap<Long, Integer>();
        int index = 0;

        for (Long workId : workIdsWithLastModified.keySet()) {
            // get works from the cache if we can
            Object key = new WorkCacheKey(workId, releaseName);
            WorkBaseEntity cachedWork = toWorkBaseEntity(workCache.get(key));
            if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(workId).getTime()) {
                fetchListIndexOrder.put(workId, index);
                fetchList.add(workId);
            } else {
                returnArray[index] = cachedWork;
            }
            index++;
        }

        // now fetch all the others that are *not* in the cache
        if (fetchList.size() > 0) {
            List<? extends WorkBaseEntity> refreshedWorks = workRetriever.apply(fetchList);
            for (WorkBaseEntity mWorkRefreshedFromDB : refreshedWorks) {
                Object key = new WorkCacheKey(mWorkRefreshedFromDB.getId(), releaseName);
                try {
                    synchronized (lockerObjectsManager.obtainLock(Long.toString(mWorkRefreshedFromDB.getId()))) {
                        // check cache again here to prevent race condition
                        // since something could have updated while we were
                        // fetching from DB
                        // (or can we skip because new last modified is always
                        // going to be after profile last modified as provided)
                        WorkBaseEntity cachedWork = toWorkBaseEntity(workCache.get(key));
                        int returnListIndex = fetchListIndexOrder.get(mWorkRefreshedFromDB.getId());
                        if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(mWorkRefreshedFromDB.getId()).getTime()) {
                            workCache.put(new Element(key, mWorkRefreshedFromDB));
                            returnArray[returnListIndex] = mWorkRefreshedFromDB;
                        } else {
                            returnArray[returnListIndex] = cachedWork;
                        }
                    }
                } finally {
                    lockerObjectsManager.releaseLock(Long.toString(mWorkRefreshedFromDB.getId()));
                }
            }
        }
        @SuppressWarnings("unchecked")
        List<T> results = (List<T>) Arrays.asList(returnArray);
        return results;
    }

    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified) {
        Map<Long, Date> workIdsWithLastModified = retrieveWorkLastModifiedMap(orcid, profileLastModified);
        return retrieveWorkList(workIdsWithLastModified, minimizedWorkEntityCache, lockerMinimizedWork, idList -> workDao.getMinimizedWorkEntities(idList));
    }

    @Override
    public List<MinimizedWorkEntity> retrievePublicMinimizedWorks(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrievePublicWorkLastModifiedList(orcid, profileLastModified);        
        Map<Long, Date> workIdsWithLastModified = workLastModifiedList.stream().collect(Collectors.toMap(
                WorkLastModifiedEntity::getId, 
                WorkLastModifiedEntity::getLastModified,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, 
                LinkedHashMap::new));
        return this.retrieveWorkList(workIdsWithLastModified, minimizedWorkEntityCache, lockerMinimizedWork, idList -> workDao.getMinimizedWorkEntities(idList));
    }
    
    @Override
    public List<WorkEntity> retrieveFullWorks(String orcid, long profileLastModified) {
        Map<Long, Date> workIdsWithLastModified = retrieveWorkLastModifiedMap(orcid, profileLastModified);
        return retrieveWorkList(workIdsWithLastModified, fullWorkEntityCache, lockerFullWork, idList -> workDao.getWorkEntities(idList));
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

}
