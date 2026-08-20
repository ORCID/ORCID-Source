package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.utils.OrcidStringUtils;

@Mapper(componentModel = "spring")
public abstract class SourceMapperV2 {

    @Autowired
    protected OrcidUrlManager orcidUrlManager;

    @Autowired
    protected ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Autowired
    protected SourceNameCacheManager sourceNameCacheManager;


    public Source toSource(SourceAwareEntity<?> entity) {
        if (entity == null) {
            return null;
        }
        
        String sourceId = entity.getElementSourceId();
        if (StringUtils.isEmpty(sourceId)) {
            return null;
        }

        Source source;
        if (OrcidStringUtils.isClientId(sourceId) || clientDetailsManagerReadOnly.isLegacyClientId(sourceId)) {
            source = createClientSource(sourceId);
        } else {
            source = createOrcidSource(sourceId);
        }

        source.setSourceName(new SourceName(sourceNameCacheManager.retrieve(sourceId)));
        return source;
    }

    private Source createClientSource(String sourceId) {
        Source source = new Source();
        SourceClientId sourceClientId = new SourceClientId();
        source.setSourceClientId(sourceClientId);
        sourceClientId.setHost(orcidUrlManager.getBaseHost());
        sourceClientId.setUri(orcidUrlManager.getBaseUriHttp() + "/client/" + sourceId);
        sourceClientId.setPath(sourceId);
        return source;
    }

    private Source createOrcidSource(String sourceId) {
        Source source = new Source();
        SourceOrcid sourceOrcid = new SourceOrcid();
        source.setSourceOrcid(sourceOrcid);
        sourceOrcid.setHost(orcidUrlManager.getBaseHost());
        sourceOrcid.setUri(orcidUrlManager.getBaseUriHttp() + "/" + sourceId);
        sourceOrcid.setPath(sourceId);
        return source;
    }
}