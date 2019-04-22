package org.orcid.integration.blackbox.api.v12;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.orcid.api.common.T2OrcidApiService;
import org.orcid.core.api.OrcidApiConstants;

import com.sun.jersey.api.client.ClientResponse;

public interface OAuthInternalAPIService<T> {

    @POST
    @Path(OrcidApiConstants.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2TokenPost(String grantType, MultivaluedMap<String, String> formParams);

    @POST
    @Path(OrcidApiConstants.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2RefreshTokenPost(String grantType, String token, MultivaluedMap<String, String> formParams);

    @POST
    @Path(T2OrcidApiService.OAUTH_REVOKE)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse revokeTokenWithBasicAuth(String token, String clientId, String clientSecret);
    
    @POST
    @Path(T2OrcidApiService.OAUTH_REVOKE)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse revokeTokenWithPlainCredentials(String token, String clientId, String clientSecret);
}
