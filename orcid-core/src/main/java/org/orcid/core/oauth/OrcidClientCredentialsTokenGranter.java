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
package org.orcid.core.oauth;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 10/05/2012
 */
public class OrcidClientCredentialsTokenGranter implements TokenGranter {

    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private final OrcidClientCredentialsChecker clientCredentialsChecker;

    private final AuthorizationServerTokenServices tokenServices;

    public OrcidClientCredentialsTokenGranter(OrcidClientCredentialsChecker clientCredentialsChecker, AuthorizationServerTokenServices tokenServices) {
        this.clientCredentialsChecker = clientCredentialsChecker;
        this.tokenServices = tokenServices;
    }

    @Override
    public OAuth2AccessToken grant(String grantType, AuthorizationRequest authorizationRequest) {
        if (!CLIENT_CREDENTIALS.equals(grantType)) {
            return null;
        }

        AuthorizationRequest clientToken = clientCredentialsChecker.validateCredentials(grantType, authorizationRequest.getClientId(), authorizationRequest.getScope());
        return tokenServices.createAccessToken(new OAuth2Authentication(clientToken, null));
    }

}
