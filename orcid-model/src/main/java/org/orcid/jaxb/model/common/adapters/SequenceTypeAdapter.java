package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.SequenceType;

public class SequenceTypeAdapter extends XmlAdapter<String, SequenceType> {

    @Override
    public SequenceType unmarshal(String v) throws Exception {
        try {
            return SequenceType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(SequenceType.class, v);
        }
    }

    @Override
    public String marshal(SequenceType v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(SequenceType.class, String.valueOf(v));
        }  
    }
}
