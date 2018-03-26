package org.orcid.core.oauth;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author Declan Newman (declan) Date: 18/04/2012
 */
public interface OrcidClientCredentialEndPointDelegator {

    Response obtainOauth2Token(String authorization, MultivaluedMap<String, String> formParams);
}
