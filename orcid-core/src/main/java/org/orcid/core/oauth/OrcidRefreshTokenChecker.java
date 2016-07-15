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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;

/**
 * @author Angel Montenegro
 */
public class OrcidRefreshTokenChecker {
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int shotTokenValidity;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    private final OAuth2RequestFactory oAuth2RequestFactory;

    public OrcidRefreshTokenChecker(OAuth2RequestFactory oAuth2RequestFactory) {
        this.oAuth2RequestFactory = oAuth2RequestFactory;
    }

    public OAuth2Request validateCredentials(String grantType, TokenRequest tokenRequest) {
        String authorization = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.AUTHORIZATION);
        String clientId = tokenRequest.getClientId();
        String scopes = tokenRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
        String revokeOldString = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.REVOKE_OLD);
        Long expireIn = tokenRequest.getRequestParameters().containsKey(OrcidOauth2Constants.EXPIRE_IN)
                ? Long.valueOf(tokenRequest.getRequestParameters().get(OrcidOauth2Constants.EXPIRE_IN)) : 0L;
        String refreshToken = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.REFRESH_TOKEN);

        OrcidOauth2TokenDetail token = orcidOauth2TokenDetailDao.findByTokenValue(authorization);

        // Verify the token belongs to this client
        if (!clientId.equals(token.getClientDetailsId())) {
            throw new IllegalArgumentException("This token doesnt belong to the given client");
        }

        // Verify client is enabled
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);

        // Verify the token is not expired
        if (token.getTokenExpiration() != null) {
            if (token.getTokenExpiration().before(new Date())) {
                throw new InvalidTokenException("Access token expired: " + authorization);
            }
        }

        // Verify access token and refresh token are linked
        if (!refreshToken.equals(token.getRefreshTokenValue())) {
            throw new InvalidTokenException("Token and refresh token does not match");
        }

        // Verify scopes are not wider than the token scopes
        if (PojoUtil.isEmpty(scopes)) {
            scopes = token.getScope();
        } else {
            Set<ScopePathType> requiredScopes = ScopePathType.getScopesFromSpaceSeparatedString(scopes);
            Set<ScopePathType> simpleTokenScopes = ScopePathType.getScopesFromSpaceSeparatedString(token.getScope());
            // This collection contains all tokens that should be allowed given
            // the scopes that the parent token contains
            Set<ScopePathType> combinedTokenScopes = new HashSet<ScopePathType>();
            for (ScopePathType scope : simpleTokenScopes) {
                combinedTokenScopes.addAll(scope.combined());
            }

            // Check that all requiredScopes are included in the list of
            // combinedTokenScopes
            for (ScopePathType scope : requiredScopes) {
                if (!combinedTokenScopes.contains(scope)) {
                    throw new InvalidScopeException("The given scope '" + scope + "' is not included in the parent token");
                }
            }
        }

        // Validate the expiration for the new token is no later than the parent
        // token expiration
        if (expireIn <= 0) {
            // shotTokenValidity should never be used, but, set this validation
            // just in case
            expireIn = token.getTokenExpiration() == null ? shotTokenValidity : token.getTokenExpiration().getTime();
        } else {
            //Validate that it will expire before the parent token
            long parentTokenExpiration = token.getTokenExpiration() == null ? shotTokenValidity : token.getTokenExpiration().getTime();
            long newTokenExpiration = System.currentTimeMillis() + expireIn;
            if(newTokenExpiration > parentTokenExpiration) {
                throw new InvalidScopeException("Token expiration cant be after " + token.getTokenExpiration());
            }
        }
        
        //TODO: Set the token expiration 
        //TODO: disable the other token if needed
        
        // TODO
        return null;
    }
}
