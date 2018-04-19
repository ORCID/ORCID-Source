package org.orcid.core.adapter.v3.converter;

import org.orcid.jaxb.model.v3.dev1.common.Visibility;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class VisibilityConverter extends BidirectionalConverter<Visibility, String> {

    @Override
    public String convertTo(Visibility source, Type<String> destinationType) {
        return source.name();
    }

    @Override
    public Visibility convertFrom(String source, Type<Visibility> destinationType) {
        return Visibility.valueOf(source);
    }
}
