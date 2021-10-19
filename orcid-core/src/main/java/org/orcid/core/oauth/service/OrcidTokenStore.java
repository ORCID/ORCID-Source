package org.orcid.core.oauth.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

public interface OrcidTokenStore extends TokenStore {

    OAuth2AccessToken readEvenDisabledAccessToken(String tokenValue);
}
