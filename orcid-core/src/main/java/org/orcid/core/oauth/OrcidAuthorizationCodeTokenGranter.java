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
package org.orcid.core.oauth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationRequestHolder;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class OrcidAuthorizationCodeTokenGranter extends AbstractTokenGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthorizationCodeTokenGranter.class);

    private static final String GRANT_TYPE = "authorization_code";

    private final AuthorizationCodeServices authorizationCodeServices;

    public OrcidAuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices, AuthorizationCodeServices authorizationCodeServices,
            ClientDetailsService clientDetailsService) {
        super(tokenServices, clientDetailsService, GRANT_TYPE);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(AuthorizationRequest authorizationRequest) {

        Map<String, String> parameters = authorizationRequest.getAuthorizationParameters();
        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get(AuthorizationRequest.REDIRECT_URI);

        LOGGER.info("Getting OAuth2 authentication: code={}, redirectUri={}, clientId={}, scope={}, state={}", new Object[] { authorizationCode, redirectUri,
                authorizationRequest.getClientId(), authorizationRequest.getScope(), authorizationRequest.getState() });

        if (authorizationCode == null) {
            throw new OAuth2Exception("An authorization code must be supplied.");
        }

        AuthorizationRequestHolder storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        if (storedAuth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
        }

        AuthorizationRequest pendingAuthorizationRequest = storedAuth.getAuthenticationRequest();
        LOGGER.info("Found pending authorization request: redirectUri={}, clientId={}, scope={}, state={}", new Object[] { pendingAuthorizationRequest.getRedirectUri(),
                pendingAuthorizationRequest.getClientId(), pendingAuthorizationRequest.getScope(), pendingAuthorizationRequest.getState() });
        // https://jira.springsource.org/browse/SECOAUTH-333
        // This might be null, if the authorization was done without the
        // redirect_uri parameter
        String redirectUriApprovalParameter = pendingAuthorizationRequest.getAuthorizationParameters().get(AuthorizationRequest.REDIRECT_URI);

        if ((redirectUri != null || redirectUriApprovalParameter != null) && !pendingAuthorizationRequest.getRedirectUri().equals(redirectUri)) {
            throw new RedirectMismatchException("Redirect URI mismatch.");
        }

        String pendingClientId = pendingAuthorizationRequest.getClientId();
        String clientId = authorizationRequest.getClientId();
        LOGGER.info("Comparing client ids: pendingClientId={}, authorizationRequest.clientId={}", pendingClientId, clientId);
        if (clientId != null && !clientId.equals(pendingClientId)) {
            // just a sanity check.
            throw new InvalidClientException("Client ID mismatch");
        }

        // Secret is not required in the authorization request, so it won't be
        // available
        // in the pendingAuthorizationRequest. We do want to check that a secret
        // is provided
        // in the token request, but that happens elsewhere.

        Map<String, String> combinedParameters = new HashMap<String, String>(storedAuth.getAuthenticationRequest().getAuthorizationParameters());
        // Combine the parameters adding the new ones last so they override if
        // there are any clashes
        combinedParameters.putAll(parameters);
        // Similarly scopes are not required in the token request, so we don't
        // make a comparison here, just
        // enforce validity through the AuthorizationRequestFactory.
        DefaultAuthorizationRequest outgoingRequest = new DefaultAuthorizationRequest(pendingAuthorizationRequest);
        outgoingRequest.setAuthorizationParameters(combinedParameters);

        Authentication userAuth = storedAuth.getUserAuthentication();
        return new OAuth2Authentication(outgoingRequest, userAuth);

    }

}
