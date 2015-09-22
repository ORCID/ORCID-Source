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

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class OrcidAuthorizationCodeTokenGranter extends AbstractTokenGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthorizationCodeTokenGranter.class);

    private static final String GRANT_TYPE = "authorization_code";

    private final AuthorizationCodeServices authorizationCodeServices;

    @Value("${org.orcid.core.oauth.auth_code.expiration_minutes:1440}")    
    private int authorizationCodeExpiration;
    
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    public OrcidAuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices, AuthorizationCodeServices authorizationCodeServices,
            ClientDetailsService clientDetailsService, OAuth2RequestFactory oAuth2RequestFactory) {
        super(tokenServices, clientDetailsService, oAuth2RequestFactory, GRANT_TYPE);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get(OAuth2Utils.REDIRECT_URI);

        LOGGER.info("Getting OAuth2 authentication: code={}, redirectUri={}, clientId={}, scope={}", new Object[] { authorizationCode, redirectUri,
                tokenRequest.getClientId(), tokenRequest.getScope() });

        if (authorizationCode == null) {
            throw new OAuth2Exception("An authorization code must be supplied.");
        }

        //Validate scopes
        OrcidOauth2AuthoriziationCodeDetail codeDetails = orcidOauth2AuthoriziationCodeDetailDao.find(authorizationCode);        
        if(codeDetails == null) {
            throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
        } else {
            // Check auth code expiration
            Date tokenCreationDate = codeDetails.getDateCreated();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tokenCreationDate);
            calendar.add(Calendar.MINUTE, authorizationCodeExpiration);            
            Date tokenExpirationDate = calendar.getTime();
            
            if(tokenExpirationDate.before(new Date())) {
                throw new IllegalArgumentException("Authorization code has expired");
            }
            
            // Check granted scopes
            Set<String> grantedScopes = codeDetails.getScopes();
            Set<String> requestScopes = tokenRequest.getScope();
            
            for(String requestScope : requestScopes) {
                if(!grantedScopes.contains(requestScope)) {
                    throw new InvalidGrantException("Invalid scopes: " + requestScope + " available scopes for this code are: " + grantedScopes);
                }
            }                        
            
        }        
        
        //Consume code        
        OAuth2Authentication storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        if (storedAuth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
        }                

        OAuth2Request pendingAuthorizationRequest = storedAuth.getOAuth2Request();
        //Regenerate the authorization request but now with the request parameters
        pendingAuthorizationRequest = pendingAuthorizationRequest.createOAuth2Request(parameters);
        
        LOGGER.info("Found pending authorization request: redirectUri={}, clientId={}, scope={}, is_approved={}", new Object[] { pendingAuthorizationRequest.getRedirectUri(),
                pendingAuthorizationRequest.getClientId(), pendingAuthorizationRequest.getScope(), pendingAuthorizationRequest.isApproved() });
        // https://jira.springsource.org/browse/SECOAUTH-333
        // This might be null, if the authorization was done without the
        // redirect_uri parameter
        String redirectUriApprovalParameter = pendingAuthorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
        
        if ((redirectUri != null || redirectUriApprovalParameter != null) && !pendingAuthorizationRequest.getRedirectUri().equals(redirectUri)) {
            throw new RedirectMismatchException("Redirect URI mismatch.");
        }

        String pendingClientId = pendingAuthorizationRequest.getClientId();
        String clientId = client.getClientId();
        LOGGER.info("Comparing client ids: pendingClientId={}, authorizationRequest.clientId={}", pendingClientId, clientId);
        if (clientId != null && !clientId.equals(pendingClientId)) {
            // just a sanity check.
            throw new InvalidClientException("Client ID mismatch. pendingClientId:" + pendingClientId + " db clientId: " +clientId);
        }        
                
        Authentication userAuth = storedAuth.getUserAuthentication();
        return new OAuth2Authentication(pendingAuthorizationRequest, userAuth);

    }

}
