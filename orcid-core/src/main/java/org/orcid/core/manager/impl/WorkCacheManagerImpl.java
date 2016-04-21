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

import java.util.List;
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
                        minimizedWorkCache.put(new Element(key, minimizedWorkEntity));
                    }
                }
            } finally {
                publicWorkLastModifiedListLockers.releaseLock(Long.toString(workId));
            }
        }
        return minimizedWorkEntity;
    }

    @Override
    public List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrieveWorkLastModifiedList(orcid, profileLastModified);
        List<MinimizedWorkEntity> works = workLastModifiedList.stream().map(e -> retrieveMinimizedWork(e.getId(), e.getLastModified().getTime()))
                .collect(Collectors.toList());
        return works;
    }

    @Override
    public List<MinimizedWorkEntity> retrievePublicMinimizedWorks(String orcid, long profileLastModified) {
        List<WorkLastModifiedEntity> workLastModifiedList = retrievePublicWorkLastModifiedList(orcid, profileLastModified);
        List<MinimizedWorkEntity> works = workLastModifiedList.stream().map(e -> retrieveMinimizedWork(e.getId(), e.getLastModified().getTime()))
                .collect(Collectors.toList());
        return works;
    }

    private MinimizedWorkEntity toMinimizedWork(Element element) {
        return (MinimizedWorkEntity) (element != null ? element.getObjectValue() : null);
    }

    @SuppressWarnings("unchecked")
    private List<WorkLastModifiedEntity> toWorkLastModifiedList(Element element) {
        return (List<WorkLastModifiedEntity>) (element != null ? element.getObjectValue() : null);
    }

}
