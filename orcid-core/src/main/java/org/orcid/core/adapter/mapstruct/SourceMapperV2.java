package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.utils.OrcidStringUtils;

@Mapper
public interface SourceMapperV2 {

    SourceMapperV2 INSTANCE = Mappers.getMapper(SourceMapperV2.class);

    default Source toSource(SourceAwareEntity<?> entity, OrcidUrlManager orcidUrlManager, ClientDetailsManagerReadOnly clientDetailsManagerReadOnly,
            SourceNameCacheManager sourceNameCacheManager) {
        String sourceId = entity.getElementSourceId();
        if (StringUtils.isEmpty(sourceId)) {
            return null;
        }

        Source source;
        if (OrcidStringUtils.isClientId(sourceId) || clientDetailsManagerReadOnly.isLegacyClientId(sourceId)) {
            source = createClientSource(sourceId, orcidUrlManager);
        } else {
            source = createOrcidSource(sourceId, orcidUrlManager);
        }

        source.setSourceName(new SourceName(sourceNameCacheManager.retrieve(sourceId)));
        return source;
    }

    private Source createClientSource(String sourceId, OrcidUrlManager orcidUrlManager) {
        Source source = new Source();
        SourceClientId sourceClientId = new SourceClientId();
        source.setSourceClientId(sourceClientId);
        sourceClientId.setHost(orcidUrlManager.getBaseHost());
        sourceClientId.setUri(orcidUrlManager.getBaseUriHttp() + "/client/" + sourceId);
        sourceClientId.setPath(sourceId);
        return source;
    }

    private Source createOrcidSource(String sourceId, OrcidUrlManager orcidUrlManager) {
        Source source = new Source();
        SourceOrcid sourceOrcid = new SourceOrcid();
        source.setSourceOrcid(sourceOrcid);
        sourceOrcid.setHost(orcidUrlManager.getBaseHost());
        sourceOrcid.setUri(orcidUrlManager.getBaseUriHttp() + "/" + sourceId);
        sourceOrcid.setPath(sourceId);
        return source;
    }
}
