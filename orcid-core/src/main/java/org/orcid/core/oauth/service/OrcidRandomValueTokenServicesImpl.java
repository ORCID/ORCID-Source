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

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidRandomValueTokenServicesImpl extends DefaultTokenServices implements OrcidRandomValueTokenServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRandomValueTokenServicesImpl.class);
    
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;
    @Value("${org.orcid.core.token.read_validity_seconds:631138519}")
    private int readValiditySeconds;
    
    private TokenStore orcidtokenStore;

    private TokenEnhancer customTokenEnhancer;
    
    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;       

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    public OrcidRandomValueTokenServicesImpl() {        
    }
    
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
        String userOrcid = authInfo.getUserOrcid();                
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            accessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }        
        accessToken.setScope(authentication.getOAuth2Request().getScope());
        
        if(customTokenEnhancer != null) {
            accessToken = new DefaultOAuth2AccessToken(customTokenEnhancer.enhance(accessToken, authentication));
        }
        
        if(this.isSupportRefreshToken(authentication.getOAuth2Request())) {
            OAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken(UUID.randomUUID().toString());
            accessToken.setRefreshToken(refreshToken);
        }
        
        orcidtokenStore.storeAccessToken(accessToken, authentication);
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
    protected int getAccessTokenValiditySeconds(OAuth2Request authorizationRequest) {
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(authorizationRequest.getScope());
        if (isClientCredentialsGrantType(authorizationRequest)) {
            boolean allAreClientCredentialsScopes = true;

            for (ScopePathType scope : requestedScopes) {
                if (!scope.isClientCreditalScope()) {
                    allAreClientCredentialsScopes = false;
                    break;
                }
            }

            if (allAreClientCredentialsScopes) {
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
    private boolean isPersistentTokenEnabled(OAuth2Request authorizationRequest) {
        if (authorizationRequest != null) {
            Map<String, String> params = authorizationRequest.getRequestParameters();
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
    private boolean isClientCredentialsGrantType(OAuth2Request authorizationRequest) {
        Map<String, String> params = authorizationRequest.getRequestParameters();
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
        OAuth2AccessToken accessToken = orcidtokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else {
            // If it is, respect the token expiration
            if (accessToken.isExpired()) {
                orcidtokenStore.removeAccessToken(accessToken);
                throw new InvalidTokenException("Access token expired: " + accessTokenValue);
            }
            Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
            if(additionalInfo != null) {
                String clientId = (String)additionalInfo.get(OauthTokensConstants.CLIENT_ID);
                ClientDetailsEntity clientEntity = clientDetailsEntityCacheManager.retrieve(clientId);
                try {
                    orcidOAuth2RequestValidator.validateClientIsEnabled(clientEntity);
                } catch (LockedException le) {
                    throw new InvalidTokenException(le.getMessage());
                }
            }                        
        }
                
        OAuth2Authentication result = orcidtokenStore.readAuthentication(accessToken);
        return result;
    }    
    
    public void setOrcidtokenStore(TokenStore orcidtokenStore) {
        super.setTokenStore(orcidtokenStore);
        this.orcidtokenStore = orcidtokenStore;
    }

    public void setCustomTokenEnhancer(TokenEnhancer customTokenEnhancer) {
        super.setTokenEnhancer(customTokenEnhancer);
        this.customTokenEnhancer = customTokenEnhancer;
    }        
    
    public boolean tokenAlreadyExists(String clientId, String userId, Collection<String> scopes) {
        Collection<OAuth2AccessToken> existingTokens = orcidtokenStore.findTokensByClientIdAndUserName(clientId, userId);
        
        if(existingTokens == null || existingTokens.isEmpty()) {
            return false;
        }
        
        for(OAuth2AccessToken token : existingTokens) {
            if(!token.isExpired()) {
                if(token.getScope().containsAll(scopes) && scopes.containsAll(token.getScope())) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
