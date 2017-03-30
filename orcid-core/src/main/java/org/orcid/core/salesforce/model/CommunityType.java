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
package org.orcid.core.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public enum CommunityType {

    ASSOCIATION("Association"), FUNDER("Funder"), GOVERNMENT("Government"), REPOSITORY("Repository/Profile Org"), RESEARCH_INSTITUTE("Research Institute");

    private final String value;

    CommunityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CommunityType fromValue(String v) {
        for (CommunityType c : CommunityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
