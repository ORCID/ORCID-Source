package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.ContributorRole;
import org.orcid.jaxb.model.common.Relationship;

public class RelationshipAdapter extends XmlAdapter<String, Relationship> {

    @Override
    public Relationship unmarshal(String v) throws Exception {
        try {
            return Relationship.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(ContributorRole.class, v);
        }
    }

    @Override
    public String marshal(Relationship v) throws Exception {
        return v.value();
    }
}
