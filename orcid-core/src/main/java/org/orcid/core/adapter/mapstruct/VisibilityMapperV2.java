package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common_v2.Visibility;

@Mapper(componentModel = "spring")
public abstract class VisibilityMapperV2 {

    public static final VisibilityMapperV2 INSTANCE = Mappers.getMapper(VisibilityMapperV2.class);

    public String convertTo(Visibility source) {
        if (source == null) {
            return null;
        }
        return source.name();
    }

    public Visibility convertFrom(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        return Visibility.valueOf(source.trim().toUpperCase());
    }
}