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

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author Angel Montenegro
 */
public class OrcidRefreshTokenTokenGranter implements TokenGranter {
    private final OrcidClientCredentialsChecker clientCredentialsChecker;

    private final AuthorizationServerTokenServices tokenServices;

    private final OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    public OrcidRefreshTokenTokenGranter(OrcidClientCredentialsChecker clientCredentialsChecker, AuthorizationServerTokenServices tokenServices, OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao) {
        this.clientCredentialsChecker = clientCredentialsChecker;
        this.tokenServices = tokenServices;
        this.orcidOauth2TokenDetailDao = orcidOauth2TokenDetailDao;
    }

    @Override 
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (!OrcidOauth2Constants.REFRESH_TOKEN.equals(grantType)) {
            return null;
        }
        
        String authorization = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.AUTHORIZATION);
        String clientId = tokenRequest.getClientId();
        String scopes = tokenRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
        String revokeOldString = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.REVOKE_OLD);
        String expireInString = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.EXPIRE_IN);
        String refreshToken = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.REFRESH_TOKEN);
        
        
        OrcidOauth2TokenDetail token = orcidOauth2TokenDetailDao.findByTokenValue(authorization);
        
        //Verify the token is not expired
        if(token.getTokenExpiration() != null) {
            if(token.getTokenExpiration().before(new Date())) {
                throw new InvalidTokenException("Access token expired: " + authorization);
            }
        }
        
        //Verify access token and refresh token are linked
        if(!refreshToken.equals(token.getRefreshTokenValue())) {
            throw new InvalidTokenException("Token and refresh token does not match");
        }
        
        //Verify scopes are not wider than the token scopes
        if(PojoUtil.isEmpty(scopes)) {
            scopes = token.getScope();
        } else {
            Set<ScopePathType> requiredScopes = ScopePathType.getScopesFromSpaceSeparatedString(scopes);
            Set<ScopePathType> simpleTokenScopes = ScopePathType.getScopesFromSpaceSeparatedString(token.getScope());
            //This collection contains all tokens that should be allowed given the scopes that the parent token contains
            Set<ScopePathType> combinedTokenScopes = new HashSet<ScopePathType>();
            for(ScopePathType scope : simpleTokenScopes) {
                combinedTokenScopes.addAll(scope.combined());
            }
            
            //Check that all requiredScopes are included in the list of combinedTokenScopes
            for(ScopePathType scope : requiredScopes) {
                if(!combinedTokenScopes.contains(scope)) {
                    throw new InvalidScopeException("The given scope '" + scope + "' is not included in the parent token");
                }
            }
        }
        
        //Verify refreshed token expiration is less than or equal than existing token
        
        //TODO: compare token values against the user, the refresh token and other validations
        
        
        return null;
    }
}
