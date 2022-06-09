package org.orcid.core.oauth.service;

import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

public interface OrcidTokenStore extends TokenStore {

    OrcidOauth2TokenDetail readOrcidOauth2TokenDetail(String tokenValue);
    
    OAuth2AccessToken readEvenDisabledAccessToken(String tokenValue);
    
    OAuth2Authentication readAuthenticationEvenOnDisabledTokens(String tokenValue);
}
