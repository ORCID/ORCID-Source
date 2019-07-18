package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceNameCacheManagerImpl implements SourceNameCacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceNameCacheManagerImpl.class);

    private static String REQUEST_PROFILE_NAME = "REQUEST_PROFILE_NAME";

    @Resource(name = "sourceNameCache")
    private Cache<String, String> sourceNameCache;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private RecordNameDao recordNameDao;

    private ClientDetailsDao clientDetailsDao;
    
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;

    public void setRecordNameDao(RecordNameDao recordNameDao) {
        this.recordNameDao = recordNameDao;
    }

    public void setClientDetailsDao(ClientDetailsDao clientDetailsDao) {
        this.clientDetailsDao = clientDetailsDao;
    }
    
    public void setProfileEntityManager(ProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    @Override
    public String retrieve(String sourceId) throws IllegalArgumentException {
        String key = getCacheKey(sourceId);
        String sourceName = sourceNameCache.get(key);
        if (sourceName == null) {
            LOGGER.debug("Fetching source name for: " + sourceId);
            sourceName = getProfileSourceNameFromRequest(sourceId);
            if (sourceName == null) {
                sourceName = getClientSourceName(sourceId);
                if (sourceName != null) {
                    sourceNameCache.put(key, sourceName);
                } else {
                    sourceName = getProfileSourceNameFromDb(sourceId);
                }
            }
        }
        // If source name is empty, it means the name is not public, so, return
        // a null value instead
        if (StringUtils.EMPTY.equals(sourceName)) {
            return null;
        }
        return sourceName;
    }

    @Override
    public void removeAll() {
        sourceNameCache.clear();
    }

    @Override
    public void remove(String sourceId) {
        sourceNameCache.remove(getCacheKey(sourceId));
    }

    private String getCacheKey(String sourceId) {
        return releaseName + "_" + sourceId;
    }

    private String getClientSourceName(String clientId) {
        if (clientDetailsDao.existsAndIsNotPublicClient(clientId)) {
            ClientDetailsEntity clientDetails = clientDetailsDao.find(clientId);
            return clientDetails != null ? clientDetails.getClientName() : null;
        }
        return null;
    }

    private String getProfileSourceNameFromRequest(String orcid) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            Object requestAttribute = sra.getAttribute(getProfileNameSRAKey(orcid), ServletRequestAttributes.SCOPE_REQUEST);
            if (requestAttribute != null) {
                return (String) requestAttribute;
            }
        }
        return null;
    }

    private String getProfileSourceNameFromDb(String orcid) {
        RecordNameEntity recordName = null;
        try {
            recordName = recordNameDao.getRecordName(orcid, profileEntityManager.getLastModified(orcid));
            if (recordName == null) {
                throw new IllegalArgumentException("Unable to find source name for: " + orcid);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot find source name from profile matching " + orcid, e);
            throw new IllegalArgumentException("Unable to find source name for: " + orcid);
        }
        String name = recordNameManagerReadOnlyV3.fetchDisplayablePublicName(orcid);
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            sra.setAttribute(getProfileNameSRAKey(orcid), name != null ? name : StringUtils.EMPTY, ServletRequestAttributes.SCOPE_REQUEST);
        }
        return name;
    }

    private String getProfileNameSRAKey(String orcid) {
        return REQUEST_PROFILE_NAME + "_" + orcid;
    }

}
