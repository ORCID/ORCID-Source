package org.orcid.internal.server.delegator;

import javax.ws.rs.core.Response;

/**
 * @author Angel Montenegro
 */
public interface InternalClientCredentialEndPointDelegator {
    Response obtainOauth2Token(String clientId, String scopeList, String grantType);
}
