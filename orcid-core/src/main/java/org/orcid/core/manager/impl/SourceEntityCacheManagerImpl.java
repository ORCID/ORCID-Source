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

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.SourceDao;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.manager.cache.SourceEntityCacheManager;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * @author Angel Montenegro
 * */
public class SourceEntityCacheManagerImpl implements SourceEntityCacheManager {

    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Resource
    private SourceDao sourceDao;
    
    LockerObjectsManager pubLocks = new LockerObjectsManager();

    @Resource(name = "sourceEntityCache")
    private Cache sourceEntityCache;

    LockerObjectsManager lockers = new LockerObjectsManager();

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private static final Logger LOG = LoggerFactory.getLogger(SourceEntityCacheManagerImpl.class);
    
    @Override
    @Transactional
    public SourceEntity retrieve(String id) throws IllegalArgumentException {
        Object key = new OrcidCacheKey(id, releaseName);
        Date dbDate = retrieveLastModifiedDate(id);
        SourceEntity sourceEntity = toSourceEntity(sourceEntityCache.get(key));
        if (needsFresh(dbDate, sourceEntity))
            try {
                synchronized (lockers.obtainLock(id)) {
                    sourceEntity = toSourceEntity(sourceEntityCache.get(key));
                    if (needsFresh(dbDate, sourceEntity)) {
                        BaseEntity<String> entity = getEntity(id);
                        if(entity == null) {
                            LOG.error("Unable to find id " + id);
                            throw new IllegalArgumentException("Invalid source " + id);  
                        }
                        if(sourceEntity == null) {
                            sourceEntity = new SourceEntity();
                        }
                        
                        if(entity instanceof ClientDetailsEntity) {
                            sourceEntity.setSourceClient((ClientDetailsEntity) entity);
                        } else {
                            sourceEntity.setSourceProfile((ProfileEntity) entity);
                        }                            
                        
                        //Cache the source name
                        sourceEntity.getSourceName();
                        
                        sourceEntityCache.put(new Element(key, sourceEntity));
                    }
                }
            } finally {
                lockers.releaseLock(id);
            }
        return sourceEntity;
    }

    @Override
    public void put(SourceEntity entity) {
        put(entity.getSourceId(), entity);
    }
    public void put(String id, SourceEntity entity) {
        try {
            synchronized (lockers.obtainLock(id)) {
                sourceEntityCache.put(new Element(new OrcidCacheKey(id, releaseName), entity));
            }
        } finally {
            lockers.releaseLock(id);
        }
    }

    @Override
    public void removeAll() {
        sourceEntityCache.removeAll();
    }

    @Override
    public void remove(String id) {
        sourceEntityCache.remove(new OrcidCacheKey(id, releaseName));
    }
    
    private SourceEntity toSourceEntity(Element element) {
        return (SourceEntity) (element != null ? element.getObjectValue() : null);
    }
    
    private BaseEntity<String> getEntity(String id) {
        BaseEntity<String> result = null;
        // First look for the entity in the client_details table
        if (clientDetailsDao.existsAndIsNotPublicClient(id)) {
            ClientDetailsEntity clientDetails = clientDetailsDao.find(id);
            result = (BaseEntity<String>) clientDetails;            
        } else {
            // If it is not there, it means the source belongs to a record, so,
            // fetch it from the profile table
            ProfileEntity profile = profileDao.find(id);
            result = (BaseEntity<String>) profile;
        }

        return result;
    }
    
    private Date retrieveLastModifiedDate(String id) {
        try {
            return sourceDao.getLastModified(id);
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to find last modified for id:" + id);
        }
    }
    
    static public boolean needsFresh(Date dbDate, SourceEntity entity) {
        if (entity == null || (entity.getSourceClient() == null && entity.getSourceProfile() == null))
            return true;
        if (dbDate == null) // is this possible?
            return true;
        Date lastModified = entity.getSourceClient() == null ? entity.getSourceProfile().getLastModified() : entity.getSourceClient().getLastModified();
        if (lastModified.getTime() != dbDate.getTime())
            return true;
        return false;
    }
}
