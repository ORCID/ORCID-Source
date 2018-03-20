package org.orcid.core.oauth;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
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
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (!CLIENT_CREDENTIALS.equals(grantType)) {
            return null;
        }

        OAuth2Request authorizationRequest = clientCredentialsChecker.validateCredentials(grantType, tokenRequest);
        
        return tokenServices.createAccessToken(new OAuth2Authentication(authorizationRequest, null));
    }

}
