package org.orcid.core.adapter.converter;

import org.orcid.jaxb.model.common_v2.Iso3166Country;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class Iso3166CountryConverter extends BidirectionalConverter<Iso3166Country, String> {

    @Override
    public String convertTo(Iso3166Country source, Type<String> destinationType) {
        return source == null ? null : source.name();
    }

    @Override
    public Iso3166Country convertFrom(String source, Type<Iso3166Country> destinationType) {
        return (source == null || source.isEmpty()) ? null : Iso3166Country.valueOf(source);
    }
}
