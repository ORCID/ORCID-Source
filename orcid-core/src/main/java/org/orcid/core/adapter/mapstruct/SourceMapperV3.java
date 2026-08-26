package org.orcid.core.adapter.mapstruct;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

@Mapper(componentModel = "spring")
public abstract class SourceMapperV3 {

    public static final SourceMapperV3 INSTANCE = Mappers.getMapper(SourceMapperV3.class);

    @Autowired
    protected SourceEntityUtils sourceEntityUtils;

    /**
     * Single-entity conversion used by MapStruct's implicit "source = ." mapping.
     */
    public Source toSource(SourceAwareEntity<?> entity) {
        if (entity == null) {
            return null;
        }
        return sourceEntityUtils.mergeAndPopulateSource(null, entity);
    }

    public Source toSource(SourceAwareEntity<?> entity, Map<String, Source> sourceMap, SourceEntityUtils sourceEntityUtils) {
        Source source = null;
        if (sourceMap != null) {
            source = sourceMap.get(SourceEntityUtils.getSourceKey(entity));
        }
        return sourceEntityUtils.mergeAndPopulateSource(source, entity);
    }
}
