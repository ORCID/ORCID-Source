package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.FundingType;

public class FundingTypeAdapter extends XmlAdapter<String, FundingType> {

    @Override
    public FundingType unmarshal(String v) throws Exception {
        try {
            return FundingType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(FundingType.class, v);
        }
    }

    @Override
    public String marshal(FundingType v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(FundingType.class, String.valueOf(v));
        }  
    }
}
