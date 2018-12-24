package org.orcid.jaxb.model.common.adapters;

import java.util.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CurrencyAdapter extends XmlAdapter<String, Currency> {

    @Override
    public Currency unmarshal(String v) throws Exception {
        try {
            return Currency.getInstance(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(Currency.class, v);
        }
    }

    @Override
    public String marshal(Currency v) throws Exception {
        return v.getCurrencyCode();
    }
}
