package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.FundingContributorRole;

public class FundingContributorRoleAdapter extends XmlAdapter<String, FundingContributorRole> {

    @Override
    public FundingContributorRole unmarshal(String v) throws Exception {
        try {
            return FundingContributorRole.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(FundingContributorRole.class, v);
        }
    }

    @Override
    public String marshal(FundingContributorRole v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(FundingContributorRole.class, String.valueOf(v));
        }
    }
}
