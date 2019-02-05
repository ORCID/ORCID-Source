package org.orcid.jaxb.model.record_v2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModel;

/**
 * @author Declan Newman (declan)
 *         Date: 07/08/2012
 */
@XmlType(name = "sequence")
@XmlEnum
@ApiModel(value = "SequenceTypeV2_0")
public enum SequenceType implements Serializable {

    @XmlEnumValue("first")
    FIRST("first"), @XmlEnumValue("additional")
    ADDITIONAL("additional");

    private final String value;

    SequenceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @JsonValue
    public String jsonValue() {
        return this.name();
    }
    
    public static SequenceType fromValue(String v) {
        for (SequenceType c : SequenceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
