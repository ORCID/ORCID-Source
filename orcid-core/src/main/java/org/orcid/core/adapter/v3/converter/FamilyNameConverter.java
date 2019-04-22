package org.orcid.core.adapter.v3.converter;

import org.orcid.jaxb.model.v3.release.record.FamilyName;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FamilyNameConverter extends BidirectionalConverter<FamilyName, String> {

    @Override
    public String convertTo(FamilyName source, Type<String> destinationType) {
        return source.getContent();
    }

    @Override
    public FamilyName convertFrom(String source, Type<FamilyName> destinationType) {
        if (source != null && source.trim().isEmpty()) {
            return null;
        }
        return new FamilyName(source);
    }
}
