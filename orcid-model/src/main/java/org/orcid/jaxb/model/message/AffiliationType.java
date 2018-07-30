package org.orcid.jaxb.model.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 31/07/2012
 */
@XmlType(name = "type")
@XmlEnum
public enum AffiliationType implements Serializable {

    @XmlEnumValue("education")
    EDUCATION("education"),

    @XmlEnumValue("employment")
    EMPLOYMENT("employment"),
    
    // New affiliation types are used INTERNALLY only on 1.2, not to be exposed
    @XmlEnumValue("distinction")
    DISTINCTION("distinction"),
    
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
    
    @JsonValue
    public String getName() {
        return this.name();
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
