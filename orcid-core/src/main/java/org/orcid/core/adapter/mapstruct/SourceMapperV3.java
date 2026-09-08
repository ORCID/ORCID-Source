package org.orcid.core.adapter.mapstruct;

import java.util.Map;

import org.mapstruct.Context;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

public class SourceMapperV3 {

    private final SourceEntityUtils sourceEntityUtils;

    public SourceMapperV3(SourceEntityUtils sourceEntityUtils) {
        this.sourceEntityUtils = sourceEntityUtils;
    }

    /**
     * Conversion used by MapStruct's implicit "source = ." mapping.
     */
    public Source toSource(SourceAwareEntity<?> entity, @Context Map<String, Source> sourceMap) {
        if (entity == null) {
            return null;
        }
        if (sourceMap != null) {
            Source source = sourceMap.get(SourceEntityUtils.getSourceKey(entity));
            if (source != null) {
                return source;
            }
        }
        return sourceEntityUtils.mergeAndPopulateSource(null, entity);
    }
}
