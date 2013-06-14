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
package org.orcid.core.utils.statistics;

public enum StatisticsEnum {
    KEY_LIVE_IDS("liveIds"), 
    KEY_IDS_WITH_VERIFIED_EMAIL("idsWithVerifiedEmail"),
    KEY_IDS_WITH_WORKS("idsWithWorks"),
    KEY_NUMBER_OF_WORKS("works"),
    KEY_WORKS_WITH_DOIS("worksWithDois");
    
    private final String value;

    StatisticsEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatisticsEnum fromValue(String v) {
        for (StatisticsEnum c : StatisticsEnum.values()) {
            if (c.value.equals(v.toLowerCase())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
