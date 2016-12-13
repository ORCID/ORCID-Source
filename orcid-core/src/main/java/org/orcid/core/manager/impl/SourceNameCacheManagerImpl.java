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

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceNameCacheManagerImpl implements SourceNameCacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceNameCacheManagerImpl.class);
        
    @Resource(name = "sourceNameCache")
    private Cache sourceNameCache;

    LockerObjectsManager lockers = new LockerObjectsManager();

    private String releaseName = ReleaseNameUtils.getReleaseName();    
    
    private RecordNameDao recordNameDao;
    
    private ClientDetailsDao clientDetailsDao;            
    
    public void setRecordNameDao(RecordNameDao recordNameDao) {
		this.recordNameDao = recordNameDao;
	}

	public void setClientDetailsDao(ClientDetailsDao clientDetailsDao) {
		this.clientDetailsDao = clientDetailsDao;
	}

	@Override    
    public String retrieve(String sourceId) throws IllegalArgumentException {
        String cacheKey = getCacheKey(sourceId);
        String sourceName = getSourceNameFromCache(sourceNameCache.get(cacheKey));
        
        if(sourceName == null) {
            try {
                synchronized (lockers.obtainLock(sourceId)) {
                    sourceName = getSourceNameFromCache(sourceNameCache.get(cacheKey));
                    if(sourceName == null) {
                        LOGGER.debug("Fetching source name for: " + sourceId);
                        sourceName = getSourceName(sourceId);
                        // If the source name is null and no exception was
                        // thrown, it means it is not public, so, lets store an
                        // empty string
                        if (sourceName == null) {
                            sourceNameCache.put(new Element(cacheKey, StringUtils.EMPTY));
                        } else {
                            sourceNameCache.put(new Element(cacheKey, sourceName));
                        }                        
                    }                    
                }
            } finally {
                lockers.releaseLock(sourceId);
            }            
        }
        //If source name is empty, it means the name is not public, so, return a null value instead
        if(StringUtils.EMPTY.equals(sourceName)) {
            return null;
        }
        return sourceName;
    }    

    @Override
    public void removeAll() {
        sourceNameCache.removeAll();
    }

    @Override
    public void remove(String sourceId) {
        sourceNameCache.remove(getCacheKey(sourceId));
    }
    
    private String getSourceNameFromCache(Element element) {
        return (String) (element != null ? element.getObjectValue() : null);
    }
    
    private String getCacheKey(String sourceId) {
        return releaseName + "_" + sourceId;
    }
    
    private String getSourceName(String clientId) {
        String result = null;
                
        if(clientDetailsDao.existsAndIsNotPublicClient(clientId)) {                                                
            try {
                ClientDetailsEntity clientDetails = clientDetailsDao.find(clientId);
                result = clientDetails.getClientName();
            } catch(Exception e1) {
                //If it fails here, don't panic, it might be a public client
            }                        
        } 
        
        //If it is still null, check the profile table
        if(result == null) {
            try {
                RecordNameEntity recordName = recordNameDao.getRecordName(clientId);
                result = getPublicNameFromRecordName(recordName);
            } catch(Exception e2) {
                //If it fails to find the name in the record_name table, then it might be an error
                throw new IllegalArgumentException("Unable to find source name for: " + clientId);
            }       
        }        
        return result;
    }
    
    private String getPublicNameFromRecordName(RecordNameEntity recordName) {
        if (Visibility.PUBLIC.equals(recordName.getVisibility())) {
            if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                // If credit name is empty
                return recordName.getGivenNames() + (StringUtils.isEmpty(recordName.getFamilyName()) ? ""
                        : " " + recordName.getFamilyName());
            }
        } else {
            return null;
        }
    }
}
