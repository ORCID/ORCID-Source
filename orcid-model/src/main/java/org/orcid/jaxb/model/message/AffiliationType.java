/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * 2011-2012 - ORCID
 * 
 * @author Declan Newman (declan) Date: 31/07/2012
 */
@XmlType(name = "affiliation-type")
@XmlEnum
public enum AffiliationType implements Serializable {

    /**
     * Current primary institution
     * 
     */
    @XmlEnumValue("current-primary-institution")
    CURRENT_PRIMARY_INSTITUTION("current-primary-institution"),

    /**
     * Past institution
     * 
     */
    @XmlEnumValue("past-institution")
    PAST_INSTITUTION("past-institution"),

    /**
     * Published here
     * 
     */
    @XmlEnumValue("published-here")
    PUBLISHED_HERE("published-here"),

    /**
     * Current institution
     * 
     */
    @XmlEnumValue("current-institution")
    CURRENT_INSTITUTION("current-institution"),

    /**
     * Funded by
     * 
     */
    @XmlEnumValue("funded-by")
    FUNDED_BY("funded-by"),

    /**
     * Educated here
     * 
     */
    @XmlEnumValue("educated-here")
    EDUCATED_HERE("educated-here");

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
