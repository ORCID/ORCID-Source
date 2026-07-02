package org.orcid.core.utils;

import jakarta.annotation.Resource;

import java.util.Optional;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SourceEntityUtils {

    public static final String SOURCE_MAP = "sourceMap";

    public static final String DO_NOT_POPULATE_SOURCES = "DO_NOT_POPULATE_SOURCES";

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;

    @Resource(name = "sourceNameCacheManager")
    private SourceNameCacheManager sourceNameCacheManager;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

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

    public static String getSourceName(Source activeSource) {
        if (activeSource != null && activeSource.getSourceName() != null && !StringUtils.isEmpty(activeSource.getSourceName().getContent()))
            return activeSource.getSourceName().getContent();
        else
            return null;
    }

    public static String getSourceKey(SourceAwareEntity<?> e) {
        String sourceOrcid = Optional.ofNullable(e.getSourceId()).filter(StringUtils::isNotEmpty).orElse("-");
        String clientSourceId = Optional.ofNullable(e.getClientSourceId()).filter(StringUtils::isNotEmpty).orElse("-");
        String assertionOriginClientSourceId = Optional.ofNullable(e.getAssertionOriginClientSourceId()).filter(StringUtils::isNotEmpty).orElse("-");
        return String.join("|", sourceOrcid, clientSourceId, assertionOriginClientSourceId);
    }

    public String getSourceName(SourceEntity sourceEntity) {
        if (sourceEntity.getCachedSourceName() != null) {
            return sourceEntity.getCachedSourceName();
        }
        if (sourceEntity.getSourceClient() != null) {
            return sourceEntity.getSourceClient().getClientName();
        }
        if (sourceEntity.getSourceProfile() != null) {
            String orcid = sourceEntity.getSourceProfile().getId();
            // Set the source name - only if orcid is not null
            if (orcid != null) {
                return recordNameManagerReadOnlyV3.fetchDisplayablePublicName(orcid);
            }
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
    public void populateSourceAwareEntityFromSource(Source from, SourceAwareEntity<?> to) {
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
     */
    public Source extractSourceFromEntity(SourceAwareEntity<?> e) {
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
                if (clientSource != null && clientSource.isUserOBOEnabled()) {
                    String orcidId = ((OrcidAware) e).getOrcid();
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

    public Source extractSourceFromProfileComplete(ProfileEntity profile) {
        Source source = new Source();
        SourceEntity entity = profile.getSource();
        if (entity.getSourceProfile() != null) {
            source.setSourceOrcid(new SourceOrcid(entity.getSourceProfile().getId()));
        }
        if (entity.getSourceClient() != null) {
            source.setSourceClientId(new SourceClientId(entity.getSourceClient().getId()));
        }
        populateSource(source);
        return source;
    }

    public Source extractSourceFromEntityComplete(SourceAwareEntity<?> b) {
        Source s = extractSourceFromEntity(b);
        populateSource(s);
        return s;
    }

    private void populateSource(Source s) {
        // Set the source
        if (s.getSourceOrcid() != null && s.getSourceOrcid().getPath() != null) {
            s.getSourceOrcid().setHost(orcidUrlManager.getBaseHost());
            s.getSourceOrcid().setUri(orcidUrlManager.getBaseUrl() + "/" + s.getSourceOrcid().getPath());
            String sourceNameValue = getSourceName(s.getSourceOrcid().getPath());
            if (sourceNameValue != null) {
                s.setSourceName(new SourceName(sourceNameValue));
            }
        }
        if (s.getSourceClientId() != null && s.getSourceClientId().getPath() != null) {
            s.getSourceClientId().setHost(orcidUrlManager.getBaseHost());
            s.getSourceClientId().setUri(orcidUrlManager.getBaseUrl() + "/client/" + s.getSourceClientId().getPath());
            String sourceNameValue = getSourceName(s.getSourceClientId().getPath());
            if (sourceNameValue != null) {
                s.setSourceName(new SourceName(sourceNameValue));
            }
        }
        // Set the OBO
        if (s.getAssertionOriginOrcid() != null && s.getAssertionOriginOrcid().getPath() != null) {
            s.getAssertionOriginOrcid().setHost(orcidUrlManager.getBaseHost());
            s.getAssertionOriginOrcid().setUri(orcidUrlManager.getBaseUrl() + "/" + s.getAssertionOriginOrcid().getPath());
            String sourceNameValue = getSourceName(s.getAssertionOriginOrcid().getPath());
            if (sourceNameValue != null) {
                s.setAssertionOriginName(new SourceName(sourceNameValue));
            }
        }
        if (s.getAssertionOriginClientId() != null && s.getAssertionOriginClientId().getPath() != null) {
            s.getAssertionOriginClientId().setHost(orcidUrlManager.getBaseHost());
            s.getAssertionOriginClientId().setUri(orcidUrlManager.getBaseUrl() + "/client/" + s.getAssertionOriginClientId().getPath());
            String sourceNameValue = getSourceName(s.getAssertionOriginClientId().getPath());
            if (sourceNameValue != null) {
                s.setAssertionOriginName(new SourceName(sourceNameValue));
            }
        }
    }

    private String getSourceName(String sourceId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object doNotPopulateSource = attributes.getAttribute(SourceEntityUtils.DO_NOT_POPULATE_SOURCES, RequestAttributes.SCOPE_REQUEST);
            if(doNotPopulateSource != null) {
                return null;
            }
        }
        return sourceNameCacheManager.retrieve(sourceId);
    }

    // =================================
    // utils to help refactoring for OBO
    // =================================
    public boolean isTheSameSource(Source active, Source existing) {
        return existing.equals(active);
    }

    public boolean isTheSameSource(Source active, SourceAwareEntity<?> existingEntity) {
        Source existing = extractSourceFromEntity(existingEntity);
        return existing.equals(active);
    }
}
