package org.orcid.core.utils;

import javax.annotation.Resource;

import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.stereotype.Component;

@Component
public class SourceEntityUtils {

    @Resource
    private RecordNameDao recordNameDaoReadOnly;
    
    @Resource
    private ProfileLastModifiedAspect profileLastModifiedAspect;
    
    public String getSourceName(SourceEntity sourceEntity) {
        if (sourceEntity.getCachedSourceName() != null) {
            return sourceEntity.getCachedSourceName();
        }
        if (sourceEntity.getSourceClient() != null) {
            return sourceEntity.getSourceClient().getClientName();
        }
        if (sourceEntity.getSourceProfile() != null) {
            String orcid = sourceEntity.getSourceProfile().getId();
            // Set the source name
            return RecordNameUtils.getPublicName(recordNameDaoReadOnly.getRecordName(orcid, profileLastModifiedAspect.retrieveLastModifiedDate(orcid).getTime())); 
        }
        return null;
    }

    /**
     * Call this method before storing in cache to prevent a whole profile or
     * client being serialized.
     * 
     * WARNING: The entity must be detached (using DAO) so that the source is
     * not made null in DB.
     */
    public void prepareForCache(SourceEntity sourceEntity) {
        if (!sourceEntity.isDetached()) {
            throw new IllegalStateException("Must not prepare source entity for cache, unless it is detached");
        }
        sourceEntity.setCachedSourceId(getSourceId(sourceEntity));
        sourceEntity.setCachedSourceName(getSourceName(sourceEntity));
        sourceEntity.setSourceClient(null);
        sourceEntity.setSourceProfile(null);
    }
    
    public static String getSourceId(SourceEntity sourceEntity) {
        if (sourceEntity.getCachedSourceId() != null) {
            return sourceEntity.getCachedSourceId();
        }
        if (sourceEntity.getSourceClient() != null) {
            return sourceEntity.getSourceClient().getClientId();
        }
        if (sourceEntity.getSourceProfile() != null) {
            return sourceEntity.getSourceProfile().getId();
        }
        return null;
    }
}
