package org.orcid.core.oauth;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.TokenRequest;

/**
 * @author Angel Montenegro
 */
public class OrcidRefreshTokenChecker {
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    public void validateRequest(String grantType, TokenRequest tokenRequest, Long requestTimeInMillis) {
        String authorization = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.AUTHORIZATION);
        String clientId = tokenRequest.getClientId();
        String scopes = tokenRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
        Long expireIn = tokenRequest.getRequestParameters().containsKey(OrcidOauth2Constants.EXPIRES_IN)
                ? Long.valueOf(tokenRequest.getRequestParameters().get(OrcidOauth2Constants.EXPIRES_IN)) : 0L;
        String refreshToken = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.REFRESH_TOKEN);

        OrcidOauth2TokenDetail token = null;
        try {
            token = orcidOauth2TokenDetailDao.findByRefreshTokenValue(refreshToken);
        } catch (NoResultException e) {
            throw new InvalidTokenException("Unable to find refresh token", e);
        }

        // Verify the token belongs to this client
        if (!clientId.equals(token.getClientDetailsId())) {
            throw new IllegalArgumentException("This token does not belong to the given client");
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

        // Verify the token is not disabled
        if (token.getTokenDisabled() != null && token.getTokenDisabled()) {
            throw new InvalidTokenException("Parent token is disabled");
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
                    throw new InvalidScopeException("The given scope '" + scope.value() + "' is not allowed for the parent token");
                }
            }
        }

        // Validate the expiration for the new token is no later than the parent
        // token expiration.
        long parentTokenExpiration = token.getTokenExpiration() == null ? System.currentTimeMillis() : token.getTokenExpiration().getTime();
        if (expireIn > parentTokenExpiration) {
            throw new IllegalArgumentException("Token expiration can't be after " + token.getTokenExpiration());
        }
    }
}
