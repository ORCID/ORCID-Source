package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.PeerReviewSubjectType;

public class PeerReviewSubjectTypeAdapter extends XmlAdapter<String, PeerReviewSubjectType> {

    @Override
    public PeerReviewSubjectType unmarshal(String v) throws Exception {
        try {
            return PeerReviewSubjectType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(PeerReviewSubjectType.class, v);
        }
    }

    @Override
    public String marshal(PeerReviewSubjectType v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(PeerReviewSubjectType.class, String.valueOf(v));
        }  
    }
}
