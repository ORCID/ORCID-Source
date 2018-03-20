package org.orcid.internal.server.delegator;

import java.util.Set;

import javax.ws.rs.core.Response;

/**
 * @author Angel Montenegro
 */
public interface InternalClientCredentialEndPointDelegator {
    Response obtainOauth2Token(String clientId, String clientSecret, String refreshToken, String grantType, String code, Set<String> scopes, String state,
            String redirectUri, String resourceId);
}
