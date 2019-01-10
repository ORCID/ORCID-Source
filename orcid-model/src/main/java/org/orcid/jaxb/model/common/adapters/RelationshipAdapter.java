package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.Relationship;

public class RelationshipAdapter extends XmlAdapter<String, Relationship> {

    @Override
    public Relationship unmarshal(String v) throws Exception {
        try {
            return Relationship.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(Relationship.class, v);
        }
    }

    @Override
    public String marshal(Relationship v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(Relationship.class, String.valueOf(v));
        }  
    }
}
