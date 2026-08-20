package org.orcid.core.adapter.mapstruct;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

@Mapper
public interface SourceMapperV3 {

    SourceMapperV3 INSTANCE = Mappers.getMapper(SourceMapperV3.class);

    default Source toSource(SourceAwareEntity<?> entity, Map<String, Source> sourceMap, SourceEntityUtils sourceEntityUtils) {
        Source source = null;
        if (sourceMap != null) {
            source = sourceMap.get(SourceEntityUtils.getSourceKey(entity));
        }
        return sourceEntityUtils.mergeAndPopulateSource(source, entity);
    }
}
