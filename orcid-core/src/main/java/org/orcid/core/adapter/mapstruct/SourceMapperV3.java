package org.orcid.core.adapter.mapstruct;

import java.util.Map;

import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

public class SourceMapperV3 {

    private final SourceEntityUtils sourceEntityUtils;

    public SourceMapperV3(SourceEntityUtils sourceEntityUtils) {
        this.sourceEntityUtils = sourceEntityUtils;
    }

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
