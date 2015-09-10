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
package org.orcid.internal.server.delegator.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.internal.server.delegator.InternalClientCredentialEndPointDelegator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;

import com.orcid.api.common.server.delegator.impl.OrcidClientCredentialEndPointDelegatorImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class InternalClientCredentialEndPointDelegatorImpl extends OrcidClientCredentialEndPointDelegatorImpl implements InternalClientCredentialEndPointDelegator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidClientCredentialEndPointDelegatorImpl.class);

    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    @Resource
    protected LocaleManager localeManager;
    
    @Override
    public Response obtainOauth2Token(String clientId, String clientSecret, String refreshToken, String grantType, String code, Set<String> scopes, String state,
            String redirectUri, String resourceId) {
        
        LOGGER.info("OAuth2 authorization requested to the internal API: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] { clientId,
                grantType, refreshToken, code, scopes, state, redirectUri });

        Authentication client = getClientAuthentication();
        if (!client.isAuthenticated()) {
            LOGGER.info("Not authenticated for OAuth2: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] {
                    clientId, grantType, refreshToken, code, scopes, state, redirectUri });
            throw new InsufficientAuthenticationException(localeManager.resolveMessage("apiError.client_not_authenticated.exception"));
        } 
        
        // Verify it is a client_credentials grant type request
        if(!OauthTokensConstants.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            Object params[] = {grantType};
            throw new UnsupportedGrantTypeException(localeManager.resolveMessage("apiError.unsupported_client_type.exception", params));
        }
        
        // Verify it is requesting an internal scope
        HashSet <String> filteredScopes = new HashSet<String>();
        for(String scope : scopes) {
            ScopePathType scopeType = ScopePathType.fromValue(scope);
            if(scopeType.isInternalScope()) {
                filteredScopes.add(scope);
            }            
        }
        
        if(filteredScopes.isEmpty()) {
            String message = localeManager.resolveMessage("apiError.9015.developerMessage", new Object[]{});
            throw new OrcidInvalidScopeException(message);
        }
        
        OAuth2AccessToken token = generateToken(client, scopes, code, redirectUri, grantType, refreshToken, state);
        return getResponse(token);                
    }

}
