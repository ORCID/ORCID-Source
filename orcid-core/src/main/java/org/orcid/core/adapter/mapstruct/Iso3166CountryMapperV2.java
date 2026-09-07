package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common_v2.Iso3166Country;

/**
 * Replaces the old Orika-only {@code org.orcid.core.adapter.converter.Iso3166CountryConverter}.
 * Plain, framework-free conversion logic - no Orika dependency.
 */
@Mapper(componentModel = "spring")
public abstract class Iso3166CountryMapperV2 {

    public static final Iso3166CountryMapperV2 INSTANCE = Mappers.getMapper(Iso3166CountryMapperV2.class);

    public String convertTo(Iso3166Country source) {
        return source == null ? null : source.name();
    }

    public Iso3166Country convertFrom(String source) {
        return (source == null || source.isEmpty()) ? null : Iso3166Country.valueOf(source);
    }
}
