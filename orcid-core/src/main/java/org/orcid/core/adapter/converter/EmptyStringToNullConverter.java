package org.orcid.core.adapter.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class EmptyStringToNullConverter extends BidirectionalConverter<String, String> {

    @Override
    public String convertTo(String source, Type<String> destinationType) {
        if (source != null && source.trim().isEmpty()) {
            return null;
        }
        return source;
    }

    @Override
    public String convertFrom(String source, Type<String> destinationType) {
        if (source != null && source.trim().isEmpty()) {
            return null;
        }
        return source;
    }
}
