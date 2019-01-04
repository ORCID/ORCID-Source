package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.Iso3166Country;

public class Iso3166CountryAdapter extends XmlAdapter<String, Iso3166Country> {

    @Override
    public Iso3166Country unmarshal(String v) throws Exception {
        try {
            return Iso3166Country.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(Iso3166Country.class, v);
        }
    }

    @Override
    public String marshal(Iso3166Country v) throws Exception {
        try {
            return v.name();
        } catch (Exception e) {
            throw new IllegalEnumValueException(Iso3166Country.class, String.valueOf(v));
        }  
    }
}
