package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.AvailableLocales;

public class AvailableLocalesAdapter extends XmlAdapter<String, AvailableLocales> {

    @Override
    public AvailableLocales unmarshal(String v) throws Exception {
        try {
            return AvailableLocales.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(AvailableLocales.class, v);
        }
    }

    @Override
    public String marshal(AvailableLocales v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(AvailableLocales.class, String.valueOf(v));
        }
    }
}
