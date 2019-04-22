package org.orcid.core.adapter.v3.converter;

import org.orcid.jaxb.model.v3.release.common.CreditName;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class CreditNameConverter extends BidirectionalConverter<CreditName, String> {

    @Override
    public String convertTo(CreditName source, Type<String> destinationType) {
        return source.getContent();
    }

    @Override
    public CreditName convertFrom(String source, Type<CreditName> destinationType) {
        if (source != null && source.trim().isEmpty()) {
            return null;
        }
        CreditName creditName = new CreditName();
        creditName.setContent(source);
        return creditName;
    }
}
