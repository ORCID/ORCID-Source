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
package org.orcid.api.t2.server.delegator.impl;

import org.orcid.api.t2.server.delegator.OrcidClientCredentialEndPointDelegator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 18/04/2012
 */
@Component("orcidClientCredentialEndPointDelegator")
public class OrcidClientCredentialEndPointDelegatorImpl extends AbstractEndpoint implements OrcidClientCredentialEndPointDelegator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidClientCredentialEndPointDelegatorImpl.class);

    public Response obtainOauth2Token(String clientId, String clientSecret, String grantType, String refreshToken, String code, Set<String> scopes, String state,
            String redirectUri, String resourceId) {

        LOGGER.info("OAuth2 authorization requested: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] { clientId,
                grantType, refreshToken, code, scopes, state, redirectUri });

        Authentication client = getClientAuthentication();
        if (!client.isAuthenticated()) {
            LOGGER.info("Not authenticated for OAuth2: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] {
                    clientId, grantType, refreshToken, code, scopes, state, redirectUri });
            throw new InsufficientAuthenticationException("The client is not authenticated.");
        }

        clientId = client.getName();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("code", code);
        parameters.put("redirect_uri", redirectUri);
        parameters.put("refresh_token", refreshToken);

        OAuth2AccessToken token = getTokenGranter().grant(grantType, parameters, clientId, scopes);
        if (token == null) {
            LOGGER.info("Unsupported grant type for OAuth2: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] {
                    clientId, grantType, refreshToken, code, scopes, state, redirectUri });
            throw new UnsupportedGrantTypeException("Unsupported grant type: " + grantType);
        }
        LOGGER.info("OAuth2 access token granted: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}, token={}", new Object[] {
                clientId, grantType, refreshToken, code, scopes, state, redirectUri, token });
        return getResponse(token);
    }

    private Response getResponse(OAuth2AccessToken accessToken) {
        return Response.ok(accessToken).header("Cache-Control", "no-store").header("Pragma", "no-cache").build();
    }

    private Authentication getClientAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication;
        } else {
            throw new InsufficientAuthenticationException("No client authentication found.");
        }

    }

}
