package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.common.Visibility;


@Mapper(componentModel = "spring")
public abstract class VisibilityMapperV3 {

    public static final VisibilityMapperV3 INSTANCE = Mappers.getMapper(VisibilityMapperV3.class);

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
        // .trim().toUpperCase() ensures strict Enum valueOf() doesn't crash on dirty DB data
        return Visibility.valueOf(source.trim().toUpperCase());
    }
}