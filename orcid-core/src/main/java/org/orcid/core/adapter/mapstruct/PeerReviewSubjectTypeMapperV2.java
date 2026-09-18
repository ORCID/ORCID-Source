package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.record_v2.WorkType;

/**
 * Replaces the old Orika-only {@code org.orcid.core.adapter.converter.PeerReviewSubjectTypeConverter}.
 * Plain, framework-free conversion logic - no Orika dependency.
 */
@Mapper(componentModel = "spring")
public abstract class PeerReviewSubjectTypeMapperV2 {

    public static final PeerReviewSubjectTypeMapperV2 INSTANCE = Mappers.getMapper(PeerReviewSubjectTypeMapperV2.class);

    public String convertTo(WorkType source) {
        return source.name();
    }

    public WorkType convertFrom(String source) {
        try {
            return WorkType.valueOf(source);
        } catch (IllegalArgumentException e) {
            if (org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name().equals(source)) {
                return WorkType.DISSERTATION;
            }
            return WorkType.OTHER;
        }
    }
}
