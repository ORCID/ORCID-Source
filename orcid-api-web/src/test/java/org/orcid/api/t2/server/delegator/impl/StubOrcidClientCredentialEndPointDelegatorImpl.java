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
package org.orcid.api.t2.server.delegator.impl;

import java.util.Set;

import javax.ws.rs.core.Response;

import org.orcid.api.t2.server.delegator.OrcidClientCredentialEndPointDelegator;

public class StubOrcidClientCredentialEndPointDelegatorImpl implements OrcidClientCredentialEndPointDelegator {

    @Override
    public Response obtainOauth2Token(String clientId, String clientSecret, String refreshToken, String grantType, String code, Set<String> scopes, String state,
            String redirectUri, String resourceId) {
        // TODO Auto-generated method stub
        return null;
    }

}
