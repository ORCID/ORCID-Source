package org.orcid.jaxb.model.common;

import java.io.Serializable;

public enum Role implements Serializable {
    REVIEWER("reviewer"),
    EDITOR("editor"),
    MEMBER("member"),
    CHAIR("chair"),
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
