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
package org.orcid.core.manager;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.orcid.pojo.HeaderCheckResult;

public interface InstitutionalSignInManager {
    void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid, Map<String, String> headers)
            throws UnsupportedEncodingException;

    void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException;

    HeaderCheckResult checkHeaders(Map<String, String> originalHeaders, Map<String, String> currentHeaders);
}
