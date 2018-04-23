package org.orcid.jaxb.model.v3.rc1.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Declan Newman (declan) Date: 31/07/2012
 */
@XmlType(name = "type")
@XmlEnum
public enum AffiliationType implements Serializable {

    @XmlEnumValue("distinction")
    DISTINCTION("distinction"),
    
    @XmlEnumValue("education")
    EDUCATION("education"),

    @XmlEnumValue("employment")
    EMPLOYMENT("employment"),
    
    @XmlEnumValue("invited-postition")
    INVITED_POSITION("invited-position"),
    
    @XmlEnumValue("membership")
    MEMBERSHIP("membership"),
    
    @XmlEnumValue("qualification")
    QUALIFICATION("qualification"),
    
    @XmlEnumValue("service")
    SERVICE("service");
    
    private final String value;

    AffiliationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }       

    public static AffiliationType fromValue(String v) {
        for (AffiliationType c : AffiliationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
