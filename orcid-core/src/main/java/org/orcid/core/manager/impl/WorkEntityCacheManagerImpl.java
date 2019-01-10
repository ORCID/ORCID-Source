package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.ehcache.Cache;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author Will Simpson
 *
 */
public class WorkEntityCacheManagerImpl implements WorkEntityCacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkEntityCacheManagerImpl.class);   
    
    @Resource(name = "workLastModifiedCache")
    private Cache<ProfileCacheKey, List<WorkLastModifiedEntity>> workLastModifiedCache;

    @Resource(name = "publicWorkLastModifiedCache")
    private Cache<ProfileCacheKey, List<WorkLastModifiedEntity>> publicWorkLastModifiedCache;

    @Resource(name = "minimizedWorkEntityCache")
    private Cache<WorkCacheKey, WorkBaseEntity> minimizedWorkEntityCache;

    @Resource(name = "fullWorkEntityCache")
    private Cache<WorkCacheKey, WorkEntity> fullWorkEntityCache;

    private final Integer batchSize;    
    
    private String releaseName = ReleaseNameUtils.getReleaseName();

    private WorkDao workDao;

    @Resource
    private SlackManager slackManager;

    public WorkEntityCacheManagerImpl(@Value("${org.orcid.works.db.batch_size:10}") Integer batchSize, @Value("${org.orcid.core.works.bulk.read.max:100}") Integer bulkReadSize) {
        this.batchSize = (batchSize == null || batchSize < 1 || batchSize > bulkReadSize) ? (bulkReadSize == null || bulkReadSize > 100 ? 100 : bulkReadSize) : batchSize;
    }
    
    public void setWorkDao(WorkDao workDao) {
        this.workDao = workDao;
    }

    @Override
    public List<WorkLastModifiedEntity> retrieveWorkLastModifiedList(String orcid, long profileLastModified) {
        ProfileCacheKey key = new ProfileCacheKey(orcid, profileLastModified, releaseName);
        List<WorkLastModifiedEntity> workLastModifiedList = workLastModifiedCache.get(key);
        if (workLastModifiedList == null) {
            workLastModifiedList = workDao.getWorkLastModifiedList(orcid);
            workLastModifiedCache.put(key, workLastModifiedList);
        }
        return workLastModifiedList;
    }

    @Override
    public List<WorkLastModifiedEntity> retrievePublicWorkLastModifiedList(String orcid, long profileLastModified) {
        ProfileCacheKey key = new ProfileCacheKey(orcid, profileLastModified, releaseName);
        List<WorkLastModifiedEntity> workLastModifiedList = publicWorkLastModifiedCache.get(key);
        if (workLastModifiedList == null) {
            workLastModifiedList = workDao.getPublicWorkLastModifiedList(orcid);
            publicWorkLastModifiedCache.put(key, workLastModifiedList);
        }
        return workLastModifiedList;
    }

    @Override
    public MinimizedWorkEntity retrieveMinimizedWork(long workId, long workLastModified) {
        WorkCacheKey key = new WorkCacheKey(workId, releaseName);
        MinimizedWorkEntity minimizedWorkEntity = (MinimizedWorkEntity) minimizedWorkEntityCache.get(key);
        if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
            minimizedWorkEntity = (MinimizedWorkEntity) minimizedWorkEntityCache.get(key);
            if (minimizedWorkEntity == null || minimizedWorkEntity.getLastModified().getTime() < workLastModified) {
                minimizedWorkEntity = workDao.getMinimizedWorkEntity(workId);
                workDao.detach(minimizedWorkEntity);
                minimizedWorkEntityCache.put(key, minimizedWorkEntity);
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
        WorkCacheKey key = new WorkCacheKey(workId, releaseName);
        WorkEntity workEntity = fullWorkEntityCache.get(key);
        if (workEntity == null || workEntity.getLastModified().getTime() < workLastModified) {
            workEntity = workDao.getWork(orcid, workId);
            workDao.detach(workEntity);
            fullWorkEntityCache.put(key, workEntity);
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
    public <T extends WorkBaseEntity> List<T> retrieveWorkList(String orcid, Map<Long, Date> workIdsWithLastModified, Cache<WorkCacheKey, WorkBaseEntity> workCache,
            Function<List<Long>, List<T>> workRetriever) {
        WorkBaseEntity[] returnArray = new WorkBaseEntity[workIdsWithLastModified.size()];
        List<Long> fetchList = new ArrayList<Long>();
        Map<Long, Integer> fetchListIndexOrder = new LinkedHashMap<Long, Integer>();
        int index = 0;

        for (Long workId : workIdsWithLastModified.keySet()) {
            // get works from the cache if we can
            WorkCacheKey key = new WorkCacheKey(workId, releaseName);
            WorkBaseEntity cachedWork = workCache.get(key);
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
                WorkCacheKey key = new WorkCacheKey(mWorkRefreshedFromDB.getId(), releaseName);
                WorkBaseEntity cachedWork = workCache.get(key);
                int returnListIndex = fetchListIndexOrder.get(mWorkRefreshedFromDB.getId());
                if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(mWorkRefreshedFromDB.getId()).getTime()) {
                    workCache.put(key, mWorkRefreshedFromDB);
                    returnArray[returnListIndex] = mWorkRefreshedFromDB;
                } else {
                    returnArray[returnListIndex] = cachedWork;
                }
            }
        }
        return (List<T>) Arrays.asList(returnArray);
    }

    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified) {
        Map<Long, Date> workIdsWithLastModified = retrieveWorkLastModifiedMap(orcid, profileLastModified);
        List<MinimizedWorkEntity> retrieveWorkList = retrieveWorkList(orcid, workIdsWithLastModified, minimizedWorkEntityCache,
                idList -> workDao.getMinimizedWorkEntities(idList));
        return retrieveWorkList;
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

    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, List<Long> ids, long profileLastModified) {
        Map<Long, Date> workIdsWithLastModified = retrieveWorkLastModifiedMap(orcid, profileLastModified);
        Map<Long, Date> filteredWorkIdsWithLastModified = new HashMap<>();
        for (Long id : ids) {
            filteredWorkIdsWithLastModified.put(id, workIdsWithLastModified.get(id));
        }
        return retrieveWorkList(orcid, filteredWorkIdsWithLastModified, minimizedWorkEntityCache, idList -> workDao.getMinimizedWorkEntities(idList));
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
            WorkCacheKey key = new WorkCacheKey(workId, releaseName);
                WorkEntity cachedWork = fullWorkEntityCache.get(key);                        
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
            List<WorkEntity> refreshedWorks = workDao.getWorkEntities(orcid, fetchList);
            for (WorkEntity mWorkRefreshedFromDB : refreshedWorks) {
                WorkCacheKey key = new WorkCacheKey(mWorkRefreshedFromDB.getId(), releaseName);
                    WorkEntity cachedWork =fullWorkEntityCache.get(key);                        
                    int returnListIndex = fetchListIndexOrder.get(mWorkRefreshedFromDB.getId());
                    if (cachedWork == null || cachedWork.getLastModified().getTime() < workIdsWithLastModified.get(mWorkRefreshedFromDB.getId()).getTime()) {
                        fullWorkEntityCache.put(key, mWorkRefreshedFromDB);
                        returnArray[returnListIndex] = mWorkRefreshedFromDB;
                    } else {
                        returnArray[returnListIndex] = cachedWork;
                    }
            }
        }
        return (List<WorkEntity>) Arrays.asList(returnArray);
    }

    @Override
    public List<WorkEntity> retrieveFullWorks(String orcid, List<Long> workIds) {
        List<Long> worksToFetchFromDB = new ArrayList<Long>();
        List<WorkEntity> works = new ArrayList<WorkEntity>();
        
        List<WorkLastModifiedEntity> lastModifiedList = workDao.getWorkLastModifiedList(orcid, workIds);
        Map<Long, Long> lastModifiedMap = toIdsLastModifiedMap(lastModifiedList);
        
        for(Long workId : workIds) {
            WorkCacheKey key = new WorkCacheKey(workId, releaseName);
            WorkEntity workEntity = fullWorkEntityCache.get(key);
            if (workEntity == null || !lastModifiedMap.containsKey(workEntity.getId()) || workEntity.getLastModified().getTime() < lastModifiedMap.get(workEntity.getId())) {
                worksToFetchFromDB.add(workId);
            } else {
                works.add(workEntity);
            }
        }
        
        List<List<Long>> lists = ListUtils.partition(worksToFetchFromDB, batchSize);
        for(List<Long> idsList : lists) {
            List<WorkEntity> workList = workDao.getWorkEntities(orcid, idsList);
            for(WorkEntity work : workList) {
                WorkCacheKey key = new WorkCacheKey(work.getId(), releaseName);
                fullWorkEntityCache.put(key, work);
                works.add(work);
            }
        }
        
        return works;
    }
    
    private Map<Long, Long> toIdsLastModifiedMap(List<WorkLastModifiedEntity> entities) {
        Map<Long, Long> map = new HashMap<Long, Long>();
        for(WorkLastModifiedEntity entity : entities) {
            map.put(entity.getId(), entity.getLastModified().getTime());
        }
        return map;
    }
}
