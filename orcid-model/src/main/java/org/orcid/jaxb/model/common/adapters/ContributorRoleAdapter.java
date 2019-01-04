package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.ContributorRole;

public class ContributorRoleAdapter extends XmlAdapter<String, ContributorRole> {

    @Override
    public ContributorRole unmarshal(String v) throws Exception {
        try {
            return ContributorRole.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(ContributorRole.class, v);
        }
    }

    @Override
    public String marshal(ContributorRole v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(ContributorRole.class, String.valueOf(v));
        }
    }
}
