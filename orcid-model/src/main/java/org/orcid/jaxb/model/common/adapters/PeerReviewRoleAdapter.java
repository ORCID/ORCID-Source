package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.Role;

public class PeerReviewRoleAdapter extends XmlAdapter<String, Role> {

    @Override
    public Role unmarshal(String v) throws Exception {
        try {
            return Role.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(Role.class, v);
        }
    }

    @Override
    public String marshal(Role v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(Role.class, String.valueOf(v));
        }
    }
}
