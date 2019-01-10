package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.CitationType;

public class CitationTypeAdapter extends XmlAdapter<String, CitationType> {

    @Override
    public CitationType unmarshal(String v) throws Exception {
        try {
            return CitationType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(CitationType.class, v);
        }
    }

    @Override
    public String marshal(CitationType v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(CitationType.class, String.valueOf(v));
        }
    }
}
