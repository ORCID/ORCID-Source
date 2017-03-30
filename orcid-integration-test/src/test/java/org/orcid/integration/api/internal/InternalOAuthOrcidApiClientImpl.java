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
package org.orcid.integration.api.internal;

import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_PERSON_READ;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.core.api.OrcidApiConstants;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class InternalOAuthOrcidApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public InternalOAuthOrcidApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }
    
    @GET
    @Produces("text/plain")
    @Path("/status")
    public ClientResponse viewStatusText(String orcid, String accessToken) {
        URI statusPath = UriBuilder.fromPath(STATUS_PATH).build();
        return orcidClientHelper.getClientResponseWithToken(statusPath, MediaType.TEXT_HTML, accessToken);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{orcid}/authenticate")
    public ClientResponse viewPersonLastModified(String orcid, String accessToken) {
        URI statusPath = UriBuilder.fromPath(INTERNAL_API_PERSON_READ).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(statusPath, MediaType.APPLICATION_JSON, accessToken);
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
    @POST
    @Path(T2OrcidApiService.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2TokenPost(String grantType, MultivaluedMap<String, String> formParams) {
        WebResource resource = orcidClientHelper.createRootResource(T2OrcidApiService.OAUTH_TOKEN);  
        resource.accept(MediaType.APPLICATION_JSON);
        return resource.entity(formParams).post(ClientResponse.class);
    }
    
    @GET
    @Path(OrcidApiConstants.MEMBER_INFO)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.TEXT_PLAIN)
    public ClientResponse viewMemberDetails(String member) {
        URI statusPath = UriBuilder.fromPath(OrcidApiConstants.MEMBER_INFO).build();
        WebResource webResource = orcidClientHelper.createRootResource(statusPath);        
        return webResource.entity(member).post(ClientResponse.class);
    }
}