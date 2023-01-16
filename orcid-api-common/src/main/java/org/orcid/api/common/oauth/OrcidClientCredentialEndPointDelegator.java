package org.orcid.api.common.oauth;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

/**
 * @author Declan Newman (declan) Date: 18/04/2012
 */
public interface OrcidClientCredentialEndPointDelegator {

    Response obtainOauth2Token(String authorization, MultivaluedMap<String, String> formParams);
}
