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

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.common_rc2.Visibility;
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
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    LockerObjectsManager pubLocks = new LockerObjectsManager();

    @Resource(name = "sourceNameCache")
    private Cache sourceNameCache;

    LockerObjectsManager lockers = new LockerObjectsManager();

    private String releaseName = ReleaseNameUtils.getReleaseName();    
    
    @Override    
    public String retrieve(String sourceId) throws IllegalArgumentException {
        Object key = new OrcidCacheKey(sourceId, releaseName);        
        SourceNameData sourceNameData = toSourceNameData(sourceNameCache.get(key));
        Date dbDate = null;
        if(sourceNameData != null) {
            dbDate = retrieveLastModifiedDate(sourceId, sourceNameData.isClient());
        } else {
            dbDate = retrieveLastModifiedDate(sourceId, null);
        }
        if (needsFresh(dbDate, sourceNameData))
            try {
                synchronized (lockers.obtainLock(sourceId)) {
                    sourceNameData = toSourceNameData(sourceNameCache.get(key));
                    if (needsFresh(dbDate, sourceNameData)) {                                                
                        if(sourceNameData != null) {
                            sourceNameData = getSourceNameData(sourceId, sourceNameData.isClient());
                        } else {
                            sourceNameData = getSourceNameData(sourceId, null);
                        }
                                                
                        if(sourceNameData == null) {
                            LOGGER.error("Unable to find id " + sourceId);
                            throw new IllegalArgumentException("Invalid source " + sourceId);  
                        }
                                                                        
                        sourceNameCache.put(new Element(key, sourceNameData));
                    }
                }
            } finally {
                lockers.releaseLock(sourceId);
            }
        return sourceNameData.getName();
    }    

    @Override
    public void removeAll() {
        sourceNameCache.removeAll();
    }

    @Override
    public void remove(String sourceId) {
        sourceNameCache.remove(new OrcidCacheKey(sourceId, releaseName));
    }
    
    private SourceNameData toSourceNameData(Element element) {
        return (SourceNameData) (element != null ? element.getObjectValue() : null);
    }
    
    private SourceNameData getSourceNameData(String clientId, Boolean isClient) {
        SourceNameData result = null;
                
        if((isClient != null && isClient) || clientDetailsDao.existsAndIsNotPublicClient(clientId)) {                                                
            try {
                ClientDetailsEntity clientDetails = clientDetailsDao.find(clientId);
                result = new SourceNameData();                
                result.setLastModified(clientDetails.getLastModified());
                result.setClient(true);
                result.setName(clientDetails.getClientName());
            } catch(Exception e1) {
                //If it fails here, don't panic, it might be a public client
            }                        
        } 
        
        //If it is still null, check the profile table
        if(result == null) {
            try {
                RecordNameEntity recordName = recordNameDao.getRecordName(clientId);
                result = new SourceNameData();
                result.setLastModified(recordName.getLastModified());
                result.setClient(false);
                result.setName(getPublicNameFromRecordName(recordName));
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
    
    private Date retrieveLastModifiedDate(String sourceId, Boolean isClient) {
        Date lastModified = null;
        if(sourceId == null) {
            throw new IllegalArgumentException("Source Id should not be null");
        }
        
        //If we still don't know what kind of source is, or, if we know is a client because of the boolean, or if we know is a client because of the 'APP-' name
        if(isClient == null || isClient|| sourceId.startsWith("APP-")) {
            try {
                lastModified = clientDetailsDao.getLastModifiedIfNotPublicClient(sourceId);
            } catch(Exception e1) {
                LOGGER.debug("Unable to find client id in client details table: " + sourceId, e1);
            }
        }
        
        //If client details is still null, check at the profile table
        if(lastModified == null) {
            try {
                return recordNameDao.getLastModified(sourceId);
            } catch(Exception e2) {
                //If we still cant find the last modified, propagate the error                
                LOGGER.error("Unable to find id in any of the tables: " + sourceId, e2);
                throw new IllegalArgumentException("Unable to find id in any of the tables: " + sourceId, e2);
            }
        }
        
        return lastModified;                    
    }
    
    
    static public boolean needsFresh(Date dbDate, SourceNameData data) {
        if (data == null || (data.getLastModified() == null))
            return true;
        if (dbDate == null) // is this possible?
            return true;
        Date lastModified = data.getLastModified();
        if (lastModified.getTime() != dbDate.getTime())
            return true;
        return false;
    }        
}

class SourceNameData implements Serializable {        
    private static final long serialVersionUID = -8472529187391016000L;
    private String name;
    private boolean isClient;
    private Date lastModified;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }    
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    public boolean isClient() {
        return isClient;
    }
    public void setClient(boolean isClient) {
        this.isClient = isClient;
    }    
}
