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
package org.orcid.integration.blackbox.api.v12;

import static org.orcid.core.api.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.BIO_PATH;
import static org.orcid.core.api.OrcidApiConstants.BIO_PATH_NO_REGEX;
import static org.orcid.core.api.OrcidApiConstants.BIO_SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_PATH;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORKS_PATH;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.api.common.T2OrcidApiService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class T1OAuthOrcidApiClientImpl implements T1OAuthAPIService<ClientResponse> {

    private OrcidClientHelper orcidClientHelper;

    public T1OAuthOrcidApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
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
    @Path(T2OrcidApiService.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2TokenPost(String grantType, MultivaluedMap<String, String> formParams) {
        WebResource resource = orcidClientHelper.createRootResource(T2OrcidApiService.OAUTH_TOKEN);
        return resource.entity(formParams).post(ClientResponse.class);
    }

    @Override
    @POST
    @Path(T2OrcidApiService.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2RefreshTokenPost(String grantType, String token, MultivaluedMap<String, String> formParams) {
        WebResource resource = orcidClientHelper.createRootResource(T2OrcidApiService.OAUTH_TOKEN);
        WebResource.Builder builder = resource.header("Authorization", "Bearer " + token);
        return builder.entity(formParams).post(ClientResponse.class);
    }

    /**
     * GETs the XML representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(PROFILE_GET_PATH)
    @Override
    public ClientResponse viewFullDetailsXml(String orcid) {
        URI profilePathWithOrcidUrl = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientResponse(profilePathWithOrcidUrl, VND_ORCID_XML);
    }

    
    public ClientResponse viewFullDetailsXml(String orcid, String messageVersion) {
        String path = '/' + messageVersion + PROFILE_GET_PATH;
        URI profilePathWithOrcidUrl = UriBuilder.fromPath(path).build(orcid);
        return getClientResponse(profilePathWithOrcidUrl, VND_ORCID_XML);
    }
    
    /**
     * GETs the JSON representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_GET_PATH)
    @Override
    public ClientResponse viewFullDetailsJson(@PathParam("orcid") String orcid) {
        URI profilePathWithOrcidUrl = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientResponse(profilePathWithOrcidUrl, VND_ORCID_JSON);
    }    

    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(BIO_PATH)
    public ClientResponse viewBioDetailsHtml(@PathParam("orcid") String orcid) {
        URI bioPathWithOrcid = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return getClientResponse(bioPathWithOrcid, MediaType.TEXT_HTML);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(BIO_PATH)
    public ClientResponse viewBioDetailsXml(@PathParam("orcid") String orcid) {
        URI bioPathWithOrcid = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return getClientResponse(bioPathWithOrcid, VND_ORCID_XML);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_XML })
    @Path(BIO_PATH)
    public ClientResponse viewBioDetailsJson(@PathParam("orcid") String orcid) {
        URI bioPathWithOrcid = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return getClientResponse(bioPathWithOrcid, VND_ORCID_JSON);
    }    
    
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(PROFILE_GET_PATH)
    public ClientResponse viewFullDetailsHtml(@PathParam("orcid") String orcid) {
        URI bioPathWithOrcid = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientResponse(bioPathWithOrcid, MediaType.TEXT_HTML);
    }

    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(WORKS_PATH)
    public ClientResponse viewWorksDetailsHtml(@PathParam("orcid") String orcid) {
        URI worksPathWithOrcid = UriBuilder.fromPath(WORKS_PATH).build(orcid);
        return getClientResponse(worksPathWithOrcid, MediaType.TEXT_HTML);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML })
    @Path(WORKS_PATH)
    public ClientResponse viewWorksDetailsXml(@PathParam("orcid") String orcid) {
        URI worksPathWithOrcid = UriBuilder.fromPath(WORKS_PATH).build(orcid);
        return getClientResponse(worksPathWithOrcid, VND_ORCID_XML);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public ClientResponse viewWorksDetailsJson(@PathParam("orcid") String orcid) {
        URI worksPathWithOrcid = UriBuilder.fromPath(WORKS_PATH).build(orcid);
        return getClientResponse(worksPathWithOrcid, VND_ORCID_JSON);
    }

    @Override
    /**
     * Gets the JSON representation any Orcid Profiles (BIO) only 
     * relevant to the given query
     * @param query
     * @return
     */
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_SEARCH_PATH)
    public ClientResponse searchByQueryJSON(String query) {
        URI bioSearchpath = UriBuilder.fromPath(BIO_SEARCH_PATH).replaceQuery(query).build();
        return getClientResponse(bioSearchpath, VND_ORCID_JSON);
    }
    
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML })
    @Path(FUNDING_PATH)
    public ClientResponse viewFundingDetailsXml(@PathParam("orcid") String orcid) {
        URI fundingPathWithOrcid = UriBuilder.fromPath(FUNDING_PATH).build(orcid);
        return getClientResponse(fundingPathWithOrcid, VND_ORCID_XML);
    }
    
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML })
    @Path(AFFILIATIONS_PATH)
    public ClientResponse viewAffiliationDetailsXml(@PathParam("orcid") String orcid) {
        URI affiliationPathWithOrcid = UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid);
        return getClientResponse(affiliationPathWithOrcid, VND_ORCID_XML);
    }   
        
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public ClientResponse viewExternalIdentifiersHtml(String orcid) {
        URI affiliationPathWithOrcid = UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid);
        return getClientResponse(affiliationPathWithOrcid, MediaType.TEXT_HTML);
    }
    
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML })
    @Path(EXTERNAL_IDENTIFIER_PATH)    
    public ClientResponse viewExternalIdentifiersXml(String orcid) {
        URI affiliationPathWithOrcid = UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid);
        return getClientResponse(affiliationPathWithOrcid, VND_ORCID_XML);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public ClientResponse viewExternalIdentifiersJson(String orcid) {
        URI affiliationPathWithOrcid = UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid);
        return getClientResponse(affiliationPathWithOrcid, VND_ORCID_JSON);
    }

    @Override
    public ClientResponse searchByQueryXML(String query) {
        // TODO Auto-generated method stub
        return null;
    }    
    
    private ClientResponse getClientResponse(URI restPath, String accept) {
        WebResource rootResource = orcidClientHelper.createRootResource(restPath);
        WebResource.Builder built = rootResource.accept(accept).type(accept);
        return built.get(ClientResponse.class);
    }    
}