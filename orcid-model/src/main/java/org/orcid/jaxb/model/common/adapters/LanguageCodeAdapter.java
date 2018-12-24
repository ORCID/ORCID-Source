package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.LanguageCode;

public class LanguageCodeAdapter extends XmlAdapter<String, LanguageCode> {
    @Override
    public LanguageCode unmarshal(String v) throws Exception {
        try {
            return LanguageCode.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(LanguageCode.class, v);
        }
    }

    @Override
    public String marshal(LanguageCode v) throws Exception {
        return v.name();
    }
}
