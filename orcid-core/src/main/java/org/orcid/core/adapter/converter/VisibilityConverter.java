package org.orcid.core.adapter.converter;

import org.orcid.jaxb.model.common_v2.Visibility;

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
