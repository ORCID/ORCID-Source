package org.orcid.core.utils.v3;

import org.orcid.persistence.jpa.entities.SourceEntity;

public class SourceEntityUtils {

    public static String getSourceName(SourceEntity sourceEntity) {
        if (sourceEntity.getCachedSourceName() != null) {
            return sourceEntity.getCachedSourceName();
        }
        if (sourceEntity.getSourceClient() != null) {
            return sourceEntity.getSourceClient().getClientName();
        }
        if (sourceEntity.getSourceProfile() != null) {
            // Set the source name
            if (sourceEntity.getSourceProfile().getRecordNameEntity() != null) {
                return RecordNameUtils.getPublicName(sourceEntity.getSourceProfile().getRecordNameEntity());
            } 
        }
        return null;
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
    
    /**
     * Call this method before storing in cache to prevent a whole profile or
     * client being serialized.
     * 
     * WARNING: The entity must be detached (using DAO) so that the source is
     * not made null in DB.
     */
    public static void prepareForCache(SourceEntity sourceEntity) {
        if (!sourceEntity.isDetached()) {
            throw new IllegalStateException("Must not prepare source entity for cache, unless it is detached");
        }
        sourceEntity.setCachedSourceId(getSourceId(sourceEntity));
        sourceEntity.setCachedSourceName(getSourceName(sourceEntity));
        sourceEntity.setSourceClient(null);
        sourceEntity.setSourceProfile(null);
    }
    
}
