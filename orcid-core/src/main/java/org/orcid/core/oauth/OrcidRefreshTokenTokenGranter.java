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

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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
        
        //TODO: compare token values against the user, the refresh token and other validations
        
        
        return null;
    }
}
