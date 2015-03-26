/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.record.peer_review;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "type")
@XmlEnum
public enum Type implements Serializable {
    @XmlEnumValue("REVIEW")
    REVIEW("REVIEW"),
    @XmlEnumValue("EVALUATION")
    EVALUATION("EVALUATION");
        
    private final String value;

    Type(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static Type fromValue(String v) {
        for (Type c : Type.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
