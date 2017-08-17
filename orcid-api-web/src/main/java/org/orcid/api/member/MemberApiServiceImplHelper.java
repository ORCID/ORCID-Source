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
package org.orcid.api.member;

import org.orcid.core.exception.PutCodeFormatException;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class MemberApiServiceImplHelper {

    protected Long getPutCode(String putCode) {
        Long putCodeNum = null;
        try {
            putCodeNum = Long.valueOf(putCode);
        } catch (Exception e) {
            throw new PutCodeFormatException();
        }
        return putCodeNum;
    }
}
