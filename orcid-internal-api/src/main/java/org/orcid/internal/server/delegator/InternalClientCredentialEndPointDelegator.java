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
package org.orcid.internal.server.delegator;

import java.util.Set;

import javax.ws.rs.core.Response;

/**
 * @author Angel Montenegro
 */
public interface InternalClientCredentialEndPointDelegator {
    Response obtainOauth2Token(String clientId, String clientSecret, String refreshToken, String grantType, String code, Set<String> scopes, String state,
            String redirectUri, String resourceId);
}
