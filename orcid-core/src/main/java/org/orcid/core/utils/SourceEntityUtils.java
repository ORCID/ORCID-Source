package org.orcid.core.utils;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidAware;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

import static org.orcid.core.constants.EmailConstants.ORCID_EMAIL_VALIDATOR_CLIENT_ID;
import static org.orcid.core.constants.EmailConstants.ORCID_EMAIL_VALIDATOR_CLIENT_NAME;

public class SourceEntityUtils {

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;

    @Resource
    private OrcidUrlManager orcidUrlManager;

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
            return recordNameManagerReadOnlyV3.fetchDisplayablePublicName(orcid);
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

    /**
     * Utility method that copies from model to entity, as entity can't see
     * model and vis-versa.
     * 
     * @param from
     * @param to
     */
    @SuppressWarnings("deprecation")
    public static void populateSourceAwareEntityFromSource(Source from, SourceAwareEntity<?> to) {
        // Set the source
        if (from.getSourceOrcid() != null && from.getSourceOrcid().getPath() != null) {
            to.setSourceId(from.getSourceOrcid().getPath());
        }
        if (from.getSourceClientId() != null && from.getSourceClientId().getPath() != null) {
            to.setClientSourceId(from.getSourceClientId().getPath());
        }
        // Set the member OBO
        if (from.getAssertionOriginClientId() != null && from.getAssertionOriginClientId().getPath() != null) {
            to.setAssertionOriginClientSourceId(from.getAssertionOriginClientId().getPath());
        }
    }

    /**
     * Utility that copies source ids from entity into new Source model.
     * 
     */
    public static Source extractSourceFromEntity(SourceAwareEntity<?> e, ClientDetailsEntityCacheManager clientDetailsEntityCacheManager) {
        Source source = new Source();
        // orcid
        if (!StringUtils.isEmpty(e.getSourceId())) {
            source.setSourceOrcid(new SourceOrcid(e.getSourceId()));
        }

        // client
        if (!StringUtils.isEmpty(e.getClientSourceId())) {
            source.setSourceClientId(new SourceClientId(e.getClientSourceId()));
            if(e instanceof OrcidAware) {
                ClientDetailsEntity clientSource = clientDetailsEntityCacheManager.retrieve(e.getClientSourceId());
                if (clientSource.isUserOBOEnabled()) {
                    String orcidId = null;
                    if (e instanceof OrcidAware) {                        
                        orcidId = ((OrcidAware) e).getOrcid();
                    }
                    source.setAssertionOriginOrcid(new SourceOrcid(orcidId));
                }     
            }
        }

        // member obo
        if (!StringUtils.isEmpty(e.getAssertionOriginClientSourceId())) {
            source.setAssertionOriginClientId(new SourceClientId(e.getAssertionOriginClientSourceId()));
        }

        return source;
    }

    public static Source extractSourceFromProfileComplete(ProfileEntity profile, SourceNameCacheManager sourceNameCacheManager, OrcidUrlManager orcidUrlManager) {
        Source source = new Source();
        SourceEntity entity = profile.getSource();
        if (entity.getSourceProfile() != null) {
            source.setSourceOrcid(new SourceOrcid(entity.getSourceProfile().getId()));
        }
        if (entity.getSourceClient() != null) {
            source.setSourceClientId(new SourceClientId(entity.getSourceClient().getId()));
        }
        populateSource(source, sourceNameCacheManager, orcidUrlManager);
        return source;
    }

    public static Source extractSourceFromEntityComplete(SourceAwareEntity<?> b, SourceNameCacheManager sourceNameCacheManager, OrcidUrlManager orcidUrlManager,
            ClientDetailsEntityCacheManager clientDetailsEntityCacheManager) {
        Source s = extractSourceFromEntity(b, clientDetailsEntityCacheManager);
        populateSource(s, sourceNameCacheManager, orcidUrlManager);
        return s;
    }

    public static void populateSource(Source s, SourceNameCacheManager sourceNameCacheManager, OrcidUrlManager orcidUrlManager) {
        // Set the source
        if (s.getSourceOrcid() != null && s.getSourceOrcid().getPath() != null) {
            s.getSourceOrcid().setHost(orcidUrlManager.getBaseHost());
            s.getSourceOrcid().setUri(orcidUrlManager.getBaseUrl() + "/" + s.getSourceOrcid().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getSourceOrcid().getPath());
            if (sourceNameValue != null) {
                s.setSourceName(new SourceName(sourceNameValue));
            }
        }
        if (s.getSourceClientId() != null && s.getSourceClientId().getPath() != null) {
            s.getSourceClientId().setHost(orcidUrlManager.getBaseHost());
            s.getSourceClientId().setUri(orcidUrlManager.getBaseUrl() + "/client/" + s.getSourceClientId().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getSourceClientId().getPath());
            if (sourceNameValue != null) {
                s.setSourceName(new SourceName(sourceNameValue));
            }
        }
        // Set the OBO
        if (s.getAssertionOriginOrcid() != null && s.getAssertionOriginOrcid().getPath() != null) {
            s.getAssertionOriginOrcid().setHost(orcidUrlManager.getBaseHost());
            s.getAssertionOriginOrcid().setUri(orcidUrlManager.getBaseUrl() + "/" + s.getAssertionOriginOrcid().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getAssertionOriginOrcid().getPath());
            if (sourceNameValue != null) {
                s.setAssertionOriginName(new SourceName(sourceNameValue));
            }
        }
        if (s.getAssertionOriginClientId() != null && s.getAssertionOriginClientId().getPath() != null) {
            s.getAssertionOriginClientId().setHost(orcidUrlManager.getBaseHost());
            s.getAssertionOriginClientId().setUri(orcidUrlManager.getBaseUrl() + "/client/" + s.getAssertionOriginClientId().getPath());
            String sourceNameValue = sourceNameCacheManager.retrieve(s.getAssertionOriginClientId().getPath());
            if (sourceNameValue != null) {
                s.setAssertionOriginName(new SourceName(sourceNameValue));
            }
        }
    }

    // =================================
    // utils to help refactoring for OBO
    // =================================

    /**
     * Used to check for duplicates adding via API.
     * 
     * @param active
     * @param existing
     * @return
     */
    public static boolean isTheSameForDuplicateChecking(Source activeSource, SourceAwareEntity<?> existingEntity,
            ClientDetailsEntityCacheManager clientDetailsEntityCacheManager) {
        Source existing = extractSourceFromEntity(existingEntity, clientDetailsEntityCacheManager);
        return existing.equals(activeSource);
    }

    public static boolean isTheSameForDuplicateChecking(Source active, Source existing) {
        return existing.equals(active);
    }

    /**
     * Used only for errors when validating I think...
     * 
     * @param activeSource
     * @return
     */
    public static String getSourceName(Source activeSource) {
        if (activeSource.getSourceName() != null && !StringUtils.isEmpty(activeSource.getSourceName().getContent()))
            return activeSource.getSourceName().getContent();
        else
            return null;
    }

    /**
     * Used to check if activeSource can update/delete an item.
     * 
     * @param activeSource
     * @param existingEntity
     * @return
     */
    public static boolean isTheSameForPermissionChecking(Source activeSource, SourceAwareEntity<?> existingEntity,
            ClientDetailsEntityCacheManager clientDetailsEntityCacheManager) {
        Source existing = extractSourceFromEntity(existingEntity, clientDetailsEntityCacheManager);
        return existing.equals(activeSource);
    }

    /**
     * Convert source-orcid to source-client-id populated with ORCID email validator details
     *
     * @param source
     * @return
     */
    public Source convertEmailSourceToOrcidValidator(Source source) {
        source.setSourceOrcid(null);
        SourceName sourceName = source.getSourceName();
        if (sourceName != null) {
            sourceName.setContent(ORCID_EMAIL_VALIDATOR_CLIENT_NAME);
        } else {
            sourceName = new SourceName();
            sourceName.setContent(ORCID_EMAIL_VALIDATOR_CLIENT_NAME);
            source.setSourceName(sourceName);
        }
        SourceClientId sourceClientId = new SourceClientId(ORCID_EMAIL_VALIDATOR_CLIENT_ID);
        sourceClientId.setPath(ORCID_EMAIL_VALIDATOR_CLIENT_ID);
        sourceClientId.setHost(orcidUrlManager.getBaseHost());
        sourceClientId.setUri(orcidUrlManager.getBaseUrl() + "/client/" + ORCID_EMAIL_VALIDATOR_CLIENT_ID);
        source.setSourceClientId(sourceClientId);
        return source;
    }

    /**
     * Convert source-orcid to source-client-id populated with ORCID email validator details
     *
     * @param source
     * @return
     */
    public org.orcid.jaxb.model.common_v2.Source convertEmailSourceToOrcidValidator(org.orcid.jaxb.model.common_v2.Source source) {
        source.setSourceOrcid(null);
        org.orcid.jaxb.model.common_v2.SourceName sourceName = source.getSourceName();
        if (sourceName != null) {
            sourceName.setContent(ORCID_EMAIL_VALIDATOR_CLIENT_NAME);
        } else {
            sourceName = new org.orcid.jaxb.model.common_v2.SourceName();
            sourceName.setContent(ORCID_EMAIL_VALIDATOR_CLIENT_NAME);
            source.setSourceName(sourceName);
        }
        org.orcid.jaxb.model.common_v2.SourceClientId sourceClientId = new org.orcid.jaxb.model.common_v2.SourceClientId(ORCID_EMAIL_VALIDATOR_CLIENT_ID);
        sourceClientId.setPath(ORCID_EMAIL_VALIDATOR_CLIENT_ID);
        sourceClientId.setHost(orcidUrlManager.getBaseHost());
        sourceClientId.setUri(orcidUrlManager.getBaseUrl() + "/client/" + ORCID_EMAIL_VALIDATOR_CLIENT_ID);
        source.setSourceClientId(sourceClientId);
        return source;
    }
}
