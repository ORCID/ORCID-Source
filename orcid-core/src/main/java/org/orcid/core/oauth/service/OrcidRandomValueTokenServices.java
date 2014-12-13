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
package org.orcid.core.oauth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidRandomValueTokenServices extends DefaultTokenServices {
    private final int writeValiditySeconds;
    private final int readValiditySeconds;

    @Resource(name = "orcidTokenStore")
    private TokenStore tokenStore;

    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRandomValueTokenServices.class);

    public OrcidRandomValueTokenServices(ClientDetailsService clientDetailsService, int writeValiditySeconds, int readValiditySeconds) {
        this.writeValiditySeconds = writeValiditySeconds;
        this.readValiditySeconds = readValiditySeconds;
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
        OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);

        Map<String, Object> additionalInfo = new HashMap<>();
        String userOrcid = authInfo.getUserOrcid();
        additionalInfo.put("orcid", userOrcid);
        if (userOrcid != null) {
            OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(userOrcid, LoadOptions.BIO_ONLY);
            String name = orcidProfile.getOrcidBio().getPersonalDetails().retrievePublicDisplayName();
            additionalInfo.put("name", name);
        }
        // TODO: As of Jan 2015 all new tokens are persistent tokens (Not
        // persistent per se, but, they are the able-to-be-persistent tokens),
        // so, in a near future we will be able to remove the
        // OauthTokensConstants.TOKEN_VERSION param from the additionalInfo
        // object
        additionalInfo.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
        if (isPersistentTokenEnabled(authentication.getAuthorizationRequest()))
            additionalInfo.put("persistent", true);

        if (existingAccessToken != null) {
            if (existingAccessToken.isExpired()) {
                tokenStore.removeAccessToken(existingAccessToken);
                LOGGER.info("Existing but expired access token found: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(),
                        userOrcid });
            } else {
                DefaultOAuth2AccessToken updatedAccessToken = new DefaultOAuth2AccessToken(existingAccessToken);
                int validitySeconds = getAccessTokenValiditySeconds(authentication.getAuthorizationRequest());
                if (validitySeconds > 0) {
                    updatedAccessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
                }
                updatedAccessToken.setAdditionalInformation(additionalInfo);
                tokenStore.storeAccessToken(updatedAccessToken, authentication);
                LOGGER.info("Existing reusable access token found: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(),
                        userOrcid });
                return updatedAccessToken;
            }
        }
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(super.createAccessToken(authentication));

        accessToken.setAdditionalInformation(additionalInfo);
        tokenStore.storeAccessToken(accessToken, authentication);
        LOGGER.info("Creating new access token: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(), userOrcid });
        return accessToken;
    }

    /**
     * The access token validity period in seconds
     * 
     * @param authorizationRequest
     *            the current authorization request
     * @return the access token validity period in seconds
     */
    @Override
    protected int getAccessTokenValiditySeconds(AuthorizationRequest authorizationRequest) {
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(authorizationRequest.getScope());
        if (requestedScopes.size() == 1 && ScopePathType.ORCID_PROFILE_CREATE.equals(requestedScopes.iterator().next())) {
            return readValiditySeconds;
        }

        if (isClientCredentialsGrantType(authorizationRequest)) {
            boolean isClientCredentialsScope = false;

            for (ScopePathType scope : requestedScopes) {
                if (scope.isClientCreditalScope()) {
                    isClientCredentialsScope = true;
                    break;
                }
            }

            if (isClientCredentialsScope) {
                String clientId = authorizationRequest.getClientId();
                ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
                if (clientDetails != null && clientDetails.isPersistentTokensEnabled())
                    return readValiditySeconds;
            }
        } else if (isPersistentTokenEnabled(authorizationRequest)) {
            return readValiditySeconds;
        }

        return writeValiditySeconds;
    }

    public int getWriteValiditySeconds() {
        return writeValiditySeconds;
    }

    public int getReadValiditySeconds() {
        return readValiditySeconds;
    }

    /**
     * Checks the authorization code to verify if the user enable the persistent
     * token or not
     * */
    private boolean isPersistentTokenEnabled(AuthorizationRequest authorizationRequest) {
        if (authorizationRequest != null) {
            Map<String, String> params = authorizationRequest.getAuthorizationParameters();
            if (params != null) {
                if (params.containsKey(OauthTokensConstants.IS_PERSISTENT)) {
                    String isPersistent = params.get(OauthTokensConstants.IS_PERSISTENT);
                    if (Boolean.valueOf(isPersistent)) {
                        return true;
                    }
                } else if (params.containsKey("code")) {
                    String code = params.get("code");
                    if (orcidOauth2AuthoriziationCodeDetailDao.find(code) != null) {
                        if (orcidOauth2AuthoriziationCodeDetailDao.isPersistentToken(code)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if the authorization request grant type is client_credentials
     * */
    private boolean isClientCredentialsGrantType(AuthorizationRequest authorizationRequest) {
        Map<String, String> params = authorizationRequest.getAuthorizationParameters();
        if (params != null) {
            if (params.containsKey(OauthTokensConstants.GRANT_TYPE)) {
                String grantType = params.get(OauthTokensConstants.GRANT_TYPE);
                if (OauthTokensConstants.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType))
                    return true;
            }
        }
        return false;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else {
            // If it is, respect the token expiration
            if (accessToken.isExpired()) {
                tokenStore.removeAccessToken(accessToken);
                throw new InvalidTokenException("Access token expired: " + accessTokenValue);
            }
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        return result;
    }
}
