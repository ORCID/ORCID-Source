package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.PeerReviewType;

public class PeerReviewTypeAdapter extends XmlAdapter<String, PeerReviewType> {

    @Override
    public PeerReviewType unmarshal(String v) throws Exception {
        try {
            return PeerReviewType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(PeerReviewType.class, v);
        }
    }

    @Override
    public String marshal(PeerReviewType v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(PeerReviewType.class, String.valueOf(v));
        }
    }
}
