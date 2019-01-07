package org.orcid.core.utils.v3;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.SourceClientId;
import org.orcid.jaxb.model.v3.rc2.common.SourceName;
import org.orcid.jaxb.model.v3.rc2.common.SourceOrcid;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

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
    
    /** Utility method that copies from model to entity, as entity can't see model and vis-versa.
     * 
     * @param from
     * @param to
     */
    @SuppressWarnings("deprecation")
    public static void populateSourceAwareEntityFromSource(Source from, SourceAwareEntity<?> to) {
        //Set the source
        if(from.getSourceOrcid() != null && from.getSourceOrcid().getPath() != null) {
            to.setSourceId(from.getSourceOrcid().getPath());
        }
        if(from.getSourceClientId() != null && from.getSourceClientId().getPath() != null) {
            to.setClientSourceId(from.getSourceClientId().getPath());
        }   
        //Set the OBO
        if(from.getAssertionOriginOrcid() != null && from.getAssertionOriginOrcid().getPath() != null) {
            to.setAssertionOriginSourceId(from.getAssertionOriginOrcid().getPath());
        }
        if(from.getAssertionOriginClientId() != null && from.getAssertionOriginClientId().getPath() != null) {
            to.setAssertionOriginClientSourceId(from.getAssertionOriginClientId().getPath());
        }   
    }
    
    /** Utility that copies source ids from entity into new Source model.
     * 
     */
    public static Source extractSourceFromEntity(SourceAwareEntity<?> e) {
        Source source = new Source();
        
        String sourceId = e.getSourceId();
        
        //orcid
        if (!StringUtils.isEmpty(sourceId)) {            
            ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            ClientDetailsManagerReadOnly clientDetailsManagerReadOnly = (ClientDetailsManagerReadOnly) context.getBean("clientDetailsManagerReadOnlyV3");
            if(OrcidStringUtils.isClientId(sourceId) || clientDetailsManagerReadOnly.isLegacyClientId(sourceId)) {
                source.setSourceClientId(new SourceClientId(sourceId));
            } else {
                source.setSourceOrcid(new SourceOrcid(sourceId));
            }            
        }
        //client
        if (!StringUtils.isEmpty(e.getClientSourceId()))
            source.setSourceClientId(new SourceClientId(e.getClientSourceId()));
        //obo
        if (!StringUtils.isEmpty(e.getAssertionOriginSourceId()))
            source.setAssertionOriginOrcid(new SourceOrcid(e.getAssertionOriginSourceId()));
        if (!StringUtils.isEmpty(e.getAssertionOriginClientSourceId()))
            source.setAssertionOriginClientId(new SourceClientId(e.getAssertionOriginClientSourceId()));
        return source;
    }
    
    public static Source extractSourceFromEntityComplete(SourceAwareEntity<?> b, SourceNameCacheManager sourceNameCacheManager, OrcidUrlManager orcidUrlManager) {
        Source s = extractSourceFromEntity(b);
        //Set the source
        if(s.getSourceOrcid() != null && s.getSourceOrcid().getPath() != null) {
            s.getSourceOrcid().setHost(orcidUrlManager.getBaseHost());
            s.getSourceOrcid().setUri(orcidUrlManager.getBaseUrl() + "/" + s.getSourceOrcid().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getSourceOrcid().getPath());
            if (sourceNameValue != null) {
                s.setSourceName(new SourceName(sourceNameValue));
            }
        }
        if(s.getSourceClientId() != null && s.getSourceClientId().getPath() != null) {
            s.getSourceClientId().setHost(orcidUrlManager.getBaseHost());
            s.getSourceClientId().setUri(orcidUrlManager.getBaseUrl() + "/client/" + s.getSourceClientId().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getSourceClientId().getPath());
            if (sourceNameValue != null) {
                s.setSourceName(new SourceName(sourceNameValue));
            }
        }   
        //Set the OBO
        if(s.getAssertionOriginOrcid() != null && s.getAssertionOriginOrcid().getPath() != null) {
            s.getAssertionOriginOrcid().setHost(orcidUrlManager.getBaseHost());
            s.getAssertionOriginOrcid().setUri(orcidUrlManager.getBaseUrl() + "/" + s.getAssertionOriginOrcid().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getAssertionOriginOrcid().getPath());
            if (sourceNameValue != null) {
                s.setAssertionOriginName(new SourceName(sourceNameValue));
            }
        }
        if(s.getAssertionOriginClientId() != null && s.getAssertionOriginClientId().getPath() != null) {
            s.getAssertionOriginClientId().setHost(orcidUrlManager.getBaseHost());
            s.getAssertionOriginClientId().setUri(orcidUrlManager.getBaseUrl() + "/client/" + s.getAssertionOriginClientId().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getAssertionOriginClientId().getPath());
            if (sourceNameValue != null) {
                s.setAssertionOriginName(new SourceName(sourceNameValue));
            }
        } 
        return s;
    }
    //=================================
    //utils to help refactoring for OBO
    //=================================
    
    
    /** Used to check for duplicates adding via API.
     * 
     * @param active
     * @param existing
     * @return
     */
    public static boolean isTheSameForDuplicateChecking(Source activeSource,SourceAwareEntity<?> existingEntity) {
        Source existing = extractSourceFromEntity(existingEntity);
        return existing.equals(activeSource);
    }

    public static boolean isTheSameForDuplicateChecking(Source active,Source existing) {
        return existing.equals(active);
    }
    
    /** Used only for errors when validating I think...
     * 
     * @param activeSource
     * @return
     */
    public static String getSourceName(Source activeSource) {
        if (activeSource.getSourceName() != null && StringUtils.isEmpty(activeSource.getSourceName().getContent()))
            return activeSource.getSourceName().getContent();
        else
            return null;
    }

    /** Used to check if activeSource can update/delete an item.
     * 
     * @param activeSource
     * @param existingEntity
     * @return
     */
    public static boolean isTheSameForPermissionChecking(Source activeSource, SourceAwareEntity<?> existingEntity) {
        Source existing = extractSourceFromEntity(existingEntity);
        return existing.equals(activeSource);
    }
    
}
