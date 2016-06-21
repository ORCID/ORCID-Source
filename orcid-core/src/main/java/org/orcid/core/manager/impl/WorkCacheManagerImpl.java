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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.manager.WorkCacheManager;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.utils.ReleaseNameUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 
 * @author Will Simpson
 *
 */
public class WorkCacheManagerImpl implements WorkCacheManager {

    @Resource
    private WorkDao workDao;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "workLastModifiedCache")
    private Cache workLastModifiedCache;

    @Resource(name = "publicWorkLastModifiedCache")
    private Cache publicWorkLastModifiedCache;

    @Resource(name = "minimizedWorkCache")
    private Cache minimizedWorkCache;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private LockerObjectsManager lockers = new LockerObjectsManager();

    private LockerObjectsManager publicWorkLastModifiedListLockers = new LockerObjectsManager();

    private LockerObjectsManager lockerMinimizedWork = new LockerObjectsManager();

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
        MinimizedWorkEntity minimizedWorkEntity = toMinimizedWork(minimizedWorkCache.get(key));
        if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
            try {
                synchronized (lockerMinimizedWork.obtainLock(Long.toString(workId))) {
                    minimizedWorkEntity = toMinimizedWork(minimizedWorkCache.get(key));
                    if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
                        minimizedWorkEntity = workDao.getMinimizedWorkEntity(workId);
                        workDao.detach(minimizedWorkEntity);                        
                        minimizedWorkCache.put(new Element(key, minimizedWorkEntity));
                    }
                }
            } finally {
                publicWorkLastModifiedListLockers.releaseLock(Long.toString(workId));
            }
        }
        return minimizedWorkEntity;
    }
        
    
    /** Fetches a list of minimised works - does this by checking cache and then fetching all misses in one go from the DB.
     * 
     * @param workIdsWithLastModified
     * @return
     */
    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorkList(Map<Long,Date> workIdsWithLastModified) {
        List<MinimizedWorkEntity> returnList = new ArrayList<MinimizedWorkEntity>();
        List<Long> fetchList = new ArrayList<Long>();
        
        for (Long workId : workIdsWithLastModified.keySet()){
            
            //get works from the cache if we can
            Object key = new WorkCacheKey(workId, releaseName);
            MinimizedWorkEntity cachedWork = toMinimizedWork(minimizedWorkCache.get(key));
            if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(workId).getTime()) {
                fetchList.add(workId);
            }else{
                returnList.add(cachedWork);
            }
        }
            
        //now fetch all the others that are *not* in the cache
        if (fetchList.size()>0){
            List<MinimizedWorkEntity> refreshedWorks = workDao.getMinimizedWorkEntities(fetchList);
            for (MinimizedWorkEntity mWorkRefreshedFromDB : refreshedWorks){
                Object key = new WorkCacheKey(mWorkRefreshedFromDB.getId(), releaseName);
                try {
                    synchronized (lockerMinimizedWork.obtainLock(Long.toString(mWorkRefreshedFromDB.getId()))) {
                        //check cache again here to prevent race condition since something could have updated while we were fetching from DB 
                        //(or can we skip because new last modified is always going to be after profile last modified as provided)
                        MinimizedWorkEntity cachedWork = toMinimizedWork(minimizedWorkCache.get(key));
                        if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(mWorkRefreshedFromDB.getId()).getTime()) {
                            workDao.detach(mWorkRefreshedFromDB);                        
                            minimizedWorkCache.put(new Element(key, mWorkRefreshedFromDB));
                            returnList.add(mWorkRefreshedFromDB);
                        }else{
                            returnList.add(cachedWork);
                        }
                        
                    }
                } finally {
                    publicWorkLastModifiedListLockers.releaseLock(Long.toString(mWorkRefreshedFromDB.getId()));
                }
            }
        }

        return returnList;
    }

    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrieveWorkLastModifiedList(orcid, profileLastModified);        
        Map<Long, Date> workIdsWithLastModified = workLastModifiedList.stream().collect(Collectors.toMap(WorkLastModifiedEntity::getId, WorkLastModifiedEntity::getLastModified));
        return this.retrieveMinimizedWorkList(workIdsWithLastModified);
    }

    @Override
    public List<MinimizedWorkEntity> retrievePublicMinimizedWorks(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrievePublicWorkLastModifiedList(orcid, profileLastModified);
        Map<Long, Date> workIdsWithLastModified = workLastModifiedList.stream().collect(Collectors.toMap(WorkLastModifiedEntity::getId, WorkLastModifiedEntity::getLastModified));
        return this.retrieveMinimizedWorkList(workIdsWithLastModified);
    }

    private MinimizedWorkEntity toMinimizedWork(Element element) {
        return (MinimizedWorkEntity) (element != null ? element.getObjectValue() : null);
    }

    @SuppressWarnings("unchecked")
    private List<WorkLastModifiedEntity> toWorkLastModifiedList(Element element) {
        return (List<WorkLastModifiedEntity>) (element != null ? element.getObjectValue() : null);
    }

}
