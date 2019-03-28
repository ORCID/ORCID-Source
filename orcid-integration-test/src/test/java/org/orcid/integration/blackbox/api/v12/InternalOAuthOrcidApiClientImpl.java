package org.orcid.integration.blackbox.api.v12;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.core.api.OrcidApiConstants;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class InternalOAuthOrcidApiClientImpl implements OAuthInternalAPIService<ClientResponse> {

    private OrcidClientHelper orcidClientHelper;

    public InternalOAuthOrcidApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }

    /**
     * * Obtains the parameters necessary to perform an Oauth2 token request
     * using client_credential authentication
     * 
     * @param formParams
     *            the grant_type grant_type parameter, telling us what the
     *            client type is.
     * @return
     */
    @Override
    @POST
    @Path(OrcidApiConstants.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2TokenPost(String grantType, MultivaluedMap<String, String> formParams) {
        WebResource resource = orcidClientHelper.createRootResource(OrcidApiConstants.OAUTH_TOKEN);
        return resource.entity(formParams).post(ClientResponse.class);
    }

    @Override
    @POST
    @Path(OrcidApiConstants.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2RefreshTokenPost(String grantType, String token, MultivaluedMap<String, String> formParams) {
        WebResource resource = orcidClientHelper.createRootResource(OrcidApiConstants.OAUTH_TOKEN);
        WebResource.Builder builder = resource.header("Authorization", "Bearer " + token);
        return builder.entity(formParams).post(ClientResponse.class);
    }

    @Override
    @POST
    @Path(T2OrcidApiService.OAUTH_REVOKE)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse revokeTokenWithBasicAuth(String token, String clientId, String clientSecret) {
        orcidClientHelper.addBasicAuth(clientId, clientSecret);
        WebResource resource = orcidClientHelper.createRootResource(T2OrcidApiService.OAUTH_REVOKE);
        WebResource.Builder builder = resource.getRequestBuilder();
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl ();
        formParams.add("token", token);
        ClientResponse response = builder.entity(formParams).post(ClientResponse.class);
        orcidClientHelper.removeBasicAuth();
        return response;
    }

    @Override
    public ClientResponse revokeTokenWithPlainCredentials(String token, String clientId, String clientSecret) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", token);
        WebResource resource = orcidClientHelper.createRootResource(T2OrcidApiService.OAUTH_REVOKE);
        WebResource.Builder builder = resource.getRequestBuilder();
        return builder.entity(params).post(ClientResponse.class);        
    }   
}