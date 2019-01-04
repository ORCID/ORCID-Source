package org.orcid.jaxb.model.common;

import java.io.Serializable;

public enum PeerReviewType implements Serializable {
    REVIEW("review"),
    EVALUATION("evaluation");
        
    private final String value;

    PeerReviewType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static PeerReviewType fromValue(String v) {
        for (PeerReviewType c : PeerReviewType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
