package org.orcid.core.oauth.service;

import java.util.Map;

import org.orcid.core.constants.RevokeReason;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

public interface OrcidTokenStore extends TokenStore {

    OrcidOauth2TokenDetail readOrcidOauth2TokenDetail(String tokenValue);
    
    OAuth2AccessToken readEvenDisabledAccessToken(String tokenValue);
    
    OAuth2Authentication readAuthenticationEvenOnDisabledTokens(String tokenValue);
    
    void storeRevokedAccessToken(DefaultOAuth2AccessToken token, OAuth2Authentication authentication, RevokeReason revokeReason);
    
    void removeAccessToken(String accessTokenValue);
    
    OAuth2Authentication readAuthenticationFromCachedToken(Map<String, String> cachedTokenData);
    
    void isClientEnabled(String clientId) throws InvalidTokenException;
    
    String readClientId(String tokenValue);
}
