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
package org.orcid.core.oauth.service;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 15/04/2012
 */

@Service("orcidTokenStore")
public class OrcidTokenStoreServiceImpl implements TokenStore {

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private ProfileEntityManager profileEntityManager;

    private static final AuthenticationKeyGenerator KEY_GENERATOR = new DefaultAuthenticationKeyGenerator();

    /**
     * Read the authentication stored under the specified token value.
     * 
     * @param token
     *            The token value under which the authentication is stored.
     * @return The authentication, or null if none.
     */
    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(token);
        return getOAuth2AuthenticationFromDetails(detail);
    }

    /**
     * Store an access token.
     * 
     * @param token
     *            The token to store.
     * @param authentication
     *            The authentication associated with the token.
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        OrcidOauth2TokenDetail detail = populatePropertiesFromTokenAndAuthentication(token, authentication, null);
        if (!authentication.isClientOnly() && ProfileEntity.class.isAssignableFrom(authentication.getUserAuthentication().getPrincipal().getClass())) {
            Map<String, Object> additionalInformation = new HashMap<String, Object>();
            ProfileEntity principal = (ProfileEntity) authentication.getUserAuthentication().getPrincipal();
            additionalInformation.put("orcid", principal.getId());
            token.setAdditionalInformation(additionalInformation);
        }
        orcidOauthTokenDetailService.removeConflictsAndCreateNew(detail);
    }

    /**
     * Read an access token from the store.
     * 
     * @param tokenValue
     *            The token value.
     * @return The access token to read.
     */
    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        if (tokenValue == null) {
            return null;
        }

        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(tokenValue);
        return getOauth2AccessTokenFromDetails(detail);
    }

    /**
     * Remove an access token from the database.
     * 
     * @param tokenValue
     *            The token to remove from the database.
     */
    @Override
    @Transactional
    public void removeAccessToken(String tokenValue) {
        orcidOauthTokenDetailService.disableAccessToken(tokenValue);
    }

    /**
     * Store the specified refresh token in the database.
     * 
     * @param refreshToken
     *            The refresh token to store.
     * @param authentication
     *            The authentication associated with the refresh token.
     */
    @Override
    public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        // The refresh token will be stored during the token creation. We don't
        // want to create it beforehand
    }

    /**
     * Read a refresh token from the store.
     * 
     * @param refreshTokenValue
     *            The value of the token to read.
     * @return The token.
     */
    @Override
    public ExpiringOAuth2RefreshToken readRefreshToken(String refreshTokenValue) {
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findByRefreshTokenValue(refreshTokenValue);
        if (detail != null && StringUtils.isNotBlank(detail.getTokenValue())) {
            return new ExpiringOAuth2RefreshToken(detail.getRefreshTokenValue(), detail.getRefreshTokenExpiration());
        }
        return null;
    }

    /**
     * @param refreshTokenValue
     *            a refresh token value
     * @return the authentication originally used to grant the refresh token
     */
    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(String refreshTokenValue) {
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findByRefreshTokenValue(refreshTokenValue);
        return getOAuth2AuthenticationFromDetails(detail);
    }

    /**
     * Remove a refresh token from the database.
     * 
     * @param refreshTokenValue
     *            The value of the token to remove from the database.
     */
    @Override
    @Transactional
    public void removeRefreshToken(String refreshTokenValue) {
        orcidOauthTokenDetailService.removeByRefreshTokenValue(refreshTokenValue);
    }

    /**
     * Remove an access token using a refresh token. This functionality is
     * necessary so refresh tokens can't be used to create an unlimited number
     * of access tokens.
     * 
     * @param refreshTokenValue
     *            The refresh token.
     */
    @Override
    @Transactional
    public void removeAccessTokenUsingRefreshToken(String refreshTokenValue) {
        orcidOauthTokenDetailService.disableAccessTokenByRefreshToken(refreshTokenValue);
    }

    /**
     * Retrieve an access token stored against the provided authentication key,
     * if it exists.
     * 
     * @param authentication
     *            the authentication key for the access token
     * @return the access token or null if there was none
     */
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String authKey = KEY_GENERATOR.extractKey(authentication);
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findByAuthenticationKey(authKey);
        return getOauth2AccessTokenFromDetails(detail);
    }

    /**
     * @param userName
     *            the user name to search
     * @return a collection of access tokens
     */
    @Override
    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByUserName(userName);
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        if (details != null && !details.isEmpty()) {
            for (OrcidOauth2TokenDetail detail : details) {
                accessTokens.add(getOauth2AccessTokenFromDetails(detail));
            }
        }
        return accessTokens;
    }

    /**
     * @param clientId
     *            the client id
     * @return a collection of access tokens
     */
    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByClientId(clientId);
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        if (details != null && !details.isEmpty()) {
            for (OrcidOauth2TokenDetail detail : details) {
                accessTokens.add(getOauth2AccessTokenFromDetails(detail));
            }
        }
        return accessTokens;
    }

    private OAuth2AccessToken getOauth2AccessTokenFromDetails(OrcidOauth2TokenDetail detail) {
        OAuth2AccessToken token = null;
        if (detail != null && StringUtils.isNotBlank(detail.getTokenValue())) {
            token = new OAuth2AccessToken(detail.getTokenValue());
            token.setExpiration(detail.getTokenExpiration());
            token.setScope(OAuth2Utils.parseParameterList(detail.getScope()));
            token.setTokenType(detail.getTokenType());
            String refreshToken = detail.getRefreshTokenValue();
            OAuth2RefreshToken rt;
            if (StringUtils.isNotBlank(refreshToken)) {
                if (detail.getRefreshTokenExpiration() != null) {
                    rt = new ExpiringOAuth2RefreshToken(detail.getRefreshTokenValue(), detail.getRefreshTokenExpiration());
                } else {
                    rt = new OAuth2RefreshToken(detail.getRefreshTokenValue());
                }
                token.setRefreshToken(rt);
            }
            ProfileEntity profile = detail.getProfile();
            if (profile != null) {
                Map<String, Object> additionalInfo = new HashMap<String, Object>();
                additionalInfo.put("orcid", profile.getId());
                token.setAdditionalInformation(additionalInfo);
            }
        }

        return token;
    }

    private OAuth2Authentication getOAuth2AuthenticationFromDetails(OrcidOauth2TokenDetail details) {
        if (details != null) {
            ClientDetailsEntity clientDetailsEntity = details.getClientDetailsEntity();
            Authentication authentication = null;
            AuthorizationRequest request = null;
            if (clientDetailsEntity != null) {
                Set<String> scopes = OAuth2Utils.parseParameterList(details.getScope());
                request = new AuthorizationRequest(clientDetailsEntity.getClientId(), scopes, clientDetailsEntity.getAuthorities(), Arrays
                        .asList(details.getResourceId()));
                request = request.approved(details.isApproved());
                ProfileEntity profile = details.getProfile();
                if (profile != null) {
                    authentication = new OrcidOauth2UserAuthentication(profile, details.isApproved());
                }
            }
            return new OrcidOAuth2Authentication(request, authentication, details.getTokenValue());
        }
        throw new InvalidTokenException("Token not found");
    }

    private OrcidOauth2TokenDetail populatePropertiesFromTokenAndAuthentication(OAuth2AccessToken token, OAuth2Authentication authentication,
            OrcidOauth2TokenDetail detail) {
        AuthorizationRequest authorizationRequest = authentication.getAuthorizationRequest();
        if (detail == null) {
            detail = new OrcidOauth2TokenDetail();
        }
        ClientDetailsEntity clientDetails = clientDetailsDao.findByClientId(authorizationRequest.getClientId());
        String authKey = KEY_GENERATOR.extractKey(authentication);
        detail.setAuthenticationKey(authKey);
        detail.setClientDetailsEntity(clientDetails);

        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        if (refreshToken != null && StringUtils.isNotBlank(refreshToken.getValue())) {
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                detail.setRefreshTokenExpiration(((ExpiringOAuth2RefreshToken) refreshToken).getExpiration());
            }
            detail.setRefreshTokenValue(refreshToken.getValue());
        }
        if (!authentication.isClientOnly()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof ProfileEntity) {
                ProfileEntity profileEntity = (ProfileEntity) authentication.getPrincipal();
                profileEntity = profileEntityManager.findByOrcid(profileEntity.getId());
                detail.setProfile(profileEntity);
            }
        }

        detail.setTokenValue(token.getValue());
        detail.setTokenType(token.getTokenType());
        detail.setTokenExpiration(token.getExpiration());
        detail.setApproved(authorizationRequest.isApproved());
        detail.setRedirectUri(authorizationRequest.getRedirectUri());

        detail.setResourceId(OAuth2Utils.formatParameterList(authorizationRequest.getResourceIds()));
        detail.setResponseType(OAuth2Utils.formatParameterList(authorizationRequest.getResponseTypes()));
        detail.setScope(OAuth2Utils.formatParameterList(authorizationRequest.getScope()));
        detail.setState(authorizationRequest.getState());
        return detail;
    }

}
