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

    static final String[] POSSIBLE_REMOTE_USER_HEADERS = new String[] { "persistent-id", "edu-person-unique-id", "targeted-id-oid", "targeted-id" };

    static final String SHIB_IDENTITY_PROVIDER_HEADER = "shib-identity-provider";

    static final String EPPN_HEADER = "eppn";

    static final String DISPLAY_NAME_HEADER = "displayname";

    static final String GIVEN_NAME_HEADER = "givenname";

    static final String SN_HEADER = "sn";

    void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid, Map<String, String> headers)
            throws UnsupportedEncodingException;

    void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException;

    HeaderCheckResult checkHeaders(Map<String, String> originalHeaders, Map<String, String> currentHeaders);
}
