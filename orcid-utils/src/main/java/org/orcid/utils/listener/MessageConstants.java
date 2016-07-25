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
package org.orcid.utils.listener;

public enum MessageConstants {

    ORCID("o"),DATE("d"),METHOD("m"),
    TYPE("t"),
    TYPE_LAST_UPDATED("lu");
    
    public final String value;
    
    MessageConstants(String s){
        value = s;
    }
}
