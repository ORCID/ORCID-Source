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
package org.orcid.pojo.ajaxForm;

public class PojoUtil {
    public static boolean isEmpty(Text text) {
        if (text == null || text.getValue() == null || text.getValue().trim().isEmpty()) return true;
        return false;
    }

    
    public static boolean isEmpty(String string) {
        if (string == null || string.trim().isEmpty()) return true;
        return false;
    }

}
