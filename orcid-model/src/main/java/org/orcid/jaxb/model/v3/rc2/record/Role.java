package org.orcid.jaxb.model.v3.rc2.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

@XmlType(name = "role")
@XmlEnum
public enum Role implements Serializable {
    @XmlEnumValue("reviewer")
    REVIEWER("reviewer"),
    @XmlEnumValue("editor")
    EDITOR("editor"),
    @XmlEnumValue("member")
    MEMBER("member"),
    @XmlEnumValue("chair")
    CHAIR("chair"),
    @XmlEnumValue("organizer")
    ORGANIZER("organizer");
        
    private final String value;

    Role(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static Role fromValue(String v) {
        for (Role c : Role.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
