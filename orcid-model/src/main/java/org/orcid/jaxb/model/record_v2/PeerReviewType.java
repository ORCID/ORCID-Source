package org.orcid.jaxb.model.record_v2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModel;

@XmlType(name = "type")
@XmlEnum
@ApiModel(value = "PeerReviewTypeV2_0")
public enum PeerReviewType implements Serializable {
    @XmlEnumValue("review")
    REVIEW("review"),
    @XmlEnumValue("evaluation")
    EVALUATION("evaluation");
        
    private final String value;

    PeerReviewType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @JsonValue
    public String jsonValue() {
        return this.name();
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
