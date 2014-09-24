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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidRandomValueTokenServices extends DefaultTokenServices {        
    private final int writeValiditySeconds;
    private final int readValiditySeconds;

    @Resource(name = "orcidTokenStore")
    private TokenStore tokenStore;

    @Value("${org.orcid.core.oauth.usePersistentTokens:false}")
    private boolean usePersistentTokens;

    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRandomValueTokenServices.class);

    public OrcidRandomValueTokenServices(ClientDetailsService clientDetailsService, int writeValiditySeconds, int readValiditySeconds) {
        this.writeValiditySeconds = writeValiditySeconds;
        this.readValiditySeconds = readValiditySeconds;
    }

    public boolean isUsePersistentTokens() {
        return usePersistentTokens;
    }

    public void setUsePersistentTokens(boolean usePersistentTokens) {
        this.usePersistentTokens = usePersistentTokens;
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
        OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("orcid", authInfo.getUserOrcid());
        if(usePersistentTokens) {
            additionalInfo.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
            if (isPersistentTokenEnabled(authentication.getAuthorizationRequest()))
                additionalInfo.put("persistent", true);
        } else {
            additionalInfo.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.NON_PERSISTENT_TOKEN);
        }
        
        if (existingAccessToken != null) {
            if (existingAccessToken.isExpired()) {
                tokenStore.removeAccessToken(existingAccessToken);
                LOGGER.info("Existing but expired access token found: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(),
                        authInfo.getUserOrcid() });
            } else {
                DefaultOAuth2AccessToken updatedAccessToken = new DefaultOAuth2AccessToken(existingAccessToken);
                int validitySeconds = getAccessTokenValiditySeconds(authentication.getAuthorizationRequest());
                if (validitySeconds > 0) {
                    updatedAccessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
                }
                updatedAccessToken.setAdditionalInformation(additionalInfo);
                tokenStore.storeAccessToken(updatedAccessToken, authentication);
                LOGGER.info("Existing reusable access token found: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(),
                        authInfo.getUserOrcid() });
                return updatedAccessToken;
            }
        }
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(super.createAccessToken(authentication));

        accessToken.setAdditionalInformation(additionalInfo);
        tokenStore.storeAccessToken(accessToken, authentication);
        LOGGER.info("Creating new access token: clientId={}, scopes={}, userOrcid={}",
                new Object[] { authInfo.getClientId(), authInfo.getScopes(), authInfo.getUserOrcid() });
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
        
        if(usePersistentTokens) {
            if (isPersistentTokenEnabled(authorizationRequest))
                return readValiditySeconds;            
        } else {
            /*
             * Tokens should last for the longest life span,
             * DefaultPermissionChecker will strip scopes that are past their
             * lifetimes
             */
            for (ScopePathType scope : requestedScopes) {
                if (!scope.isUserGrantWriteScope()) {
                    return readValiditySeconds;
                }
            }
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
        /**
         * If persistent tokens are enabled on server, check the authorization
         * request, otherwise, return false
         * */
        if (usePersistentTokens) {
            if (authorizationRequest != null) {
                Map<String, String> params = authorizationRequest.getAuthorizationParameters();
                if (params != null && params.containsKey("code")) {
                    String code = params.get("code");
                    if (orcidOauth2AuthoriziationCodeDetailDao.isPersistentToken(code)) {
                        return true;
                    }
                }
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
            // Check if the persistent token is enabled
            if (usePersistentTokens) {
                // If it is, respect the token expiration
                if (accessToken.isExpired()) {
                    tokenStore.removeAccessToken(accessToken);
                    throw new InvalidTokenException("Access token expired: " + accessTokenValue);
                }
            } else {
                // If not, check the scopes and recalculate the token expiration
                // if needed
                Date newExpirationDate = getExpirationDateWhenPersistentTokenIsDisabled(accessToken);
                if (newExpirationDate != null && newExpirationDate.before(new Date())) {
                    tokenStore.removeAccessToken(accessToken);
                    throw new InvalidTokenException("Access token expired: " + accessTokenValue);
                }
            }
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        return result;
    }

    public Date getExpirationDateWhenPersistentTokenIsDisabled(OAuth2AccessToken accessToken) {
        Set<String> scopes = accessToken.getScope();
        Date currentExpirationDate = accessToken.getExpiration();
        // If it is creating a profile, just return the same value
        if (scopes.size() == 1 && ScopePathType.ORCID_PROFILE_CREATE.value().equals(scopes.iterator().next())) {
            return currentExpirationDate;
        }

        // If it is not a write scope, just return the same value
        Iterator<String> scopesIt = scopes.iterator();
        while (scopesIt.hasNext()) {
            String scope = scopesIt.next();
            ScopePathType scopeType = ScopePathType.fromValue(scope);
            if (!scopeType.isUserGrantWriteScope()) {
                return currentExpirationDate;
            }
        }

        // If we get here it means is a write scope, so, we have to get the
        // creation date and modify the expiration date to creation date + 1hour
        Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
        if (additionalInfo != null && additionalInfo.containsKey(OrcidTokenStoreServiceImpl.PERSISTENT)) {
            boolean isPersistentToken = (Boolean) additionalInfo.get(OrcidTokenStoreServiceImpl.PERSISTENT);
            if (isPersistentToken) {
                Date createdDate = (Date) additionalInfo.get(OrcidTokenStoreServiceImpl.DATE_CREATED);
                return addOneHour(createdDate);
            }
        }

        return currentExpirationDate;
    }

    private Date addOneHour(Date currentDate) {
        return DateUtils.addHours(currentDate, 1);
    }
}
