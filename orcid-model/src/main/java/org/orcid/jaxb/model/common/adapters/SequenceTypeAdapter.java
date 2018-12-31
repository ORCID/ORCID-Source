package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.ContributorRole;
import org.orcid.jaxb.model.common.SequenceType;

public class SequenceTypeAdapter extends XmlAdapter<String, SequenceType> {

    @Override
    public SequenceType unmarshal(String v) throws Exception {
        try {
            return SequenceType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(ContributorRole.class, v);
        }
    }

    @Override
    public String marshal(SequenceType v) throws Exception {
        return v.value();
    }
}
