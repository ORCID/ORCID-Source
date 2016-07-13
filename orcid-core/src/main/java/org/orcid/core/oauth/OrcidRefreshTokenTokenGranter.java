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
package org.orcid.core.oauth;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author Angel Montenegro
 */
public class OrcidRefreshTokenTokenGranter implements TokenGranter {
    private final OrcidClientCredentialsChecker clientCredentialsChecker;

    private final AuthorizationServerTokenServices tokenServices;
    
    //TODO!!!! should this go here?
    private BearerTokenExtractor tokenExtractor = new BearerTokenExtractor();
    
   public OrcidRefreshTokenTokenGranter(OrcidClientCredentialsChecker clientCredentialsChecker, AuthorizationServerTokenServices tokenServices) {
        this.clientCredentialsChecker = clientCredentialsChecker;
        this.tokenServices = tokenServices;
    }
    
    @Override
    //TODO! Here we need the token info, we need the scopes only in the case the user provided that info, if not, we will take them from the existing token if they exists
    //Scopes comes in requestParameters["scope"]
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (!OrcidOauth2Constants.REFRESH_TOKEN.equals(grantType)) {
            return null;
        }
        String clientId = tokenRequest.getClientId();
        String scopes = tokenRequest.getRequestParameters().get("scope");
        return null;
    }
}
