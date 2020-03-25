package org.orcid.jaxb.model.v3.release.record;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Daniel Palafox
 *
 */
@XmlEnum
@ApiModel(value = "SourceTypeV3_0")
public enum SourceType {
    @XmlEnumValue("USER") USER("USER");

    private final String value;

    SourceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SourceType fromValue(String v) {
        for (SourceType c : SourceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
