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
package org.orcid.api.t2.integration;

import static org.orcid.api.common.OrcidApiConstants.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.t2.T2OAuthAPIService;
import org.orcid.api.t2.T2OrcidApiService;
import org.orcid.jaxb.model.message.OrcidMessage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class T2OAuthOrcidApiClientImpl implements T2OAuthAPIService<ClientResponse> {

    private OrcidClientHelper orcidClientHelper;

    public T2OAuthOrcidApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }

    @Override
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(PROFILE_POST_PATH)
    public ClientResponse createProfileXML(OrcidMessage orcidMessage, String token) {
        URI createProfilePath = UriBuilder.fromPath(PROFILE_POST_PATH).build();
        return postClientResponseWithToken(createProfilePath, VND_ORCID_XML, orcidMessage, token);
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
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_POST_PATH)
    public ClientResponse createProfileJson(OrcidMessage orcidMessage, String token) {
        URI createProfilePath = UriBuilder.fromPath(PROFILE_POST_PATH).build();
        return postClientResponseWithToken(createProfilePath, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    /**
     * GETs the XML representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(BIO_PATH)
    public ClientResponse updateBioDetailsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token) {
        URI bioPath = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        ClientResponse clientResponse = putClientResponseWithToken(bioPath, VND_ORCID_XML, orcidMessage, token);
        return clientResponse;
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_PATH)
    public ClientResponse updateBioDetailsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token) {
        URI bioPathWithOrcidUrl = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return putClientResponseWithToken(bioPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public ClientResponse addWorksXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI worksPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(WORKS_PATH, orcid);
        return postClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public ClientResponse addWorksJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI worksPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(WORKS_PATH, orcid);
        return postClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public ClientResponse updateWorksXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI worksPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(WORKS_PATH, orcid);
        return putClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public ClientResponse updateWorksJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI worksPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(WORKS_PATH, orcid);
        return putClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(AFFILIATIONS_PATH)
    public ClientResponse addAffiliationsXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI affiliationsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(AFFILIATIONS_PATH, orcid);
        return postClientResponseWithToken(affiliationsPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    public ClientResponse addAffiliationsJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI affiliationsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(AFFILIATIONS_PATH, orcid);
        return postClientResponseWithToken(affiliationsPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(AFFILIATIONS_PATH)
    public ClientResponse updateAffiliationsXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI affiliationsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(AFFILIATIONS_PATH, orcid);
        return putClientResponseWithToken(affiliationsPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    public ClientResponse updateAffiliationsJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI affiliationsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(AFFILIATIONS_PATH, orcid);
        return putClientResponseWithToken(affiliationsPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }
    
    @Override
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(GRANTS_PATH)
    public ClientResponse addGrantsXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI grantsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(GRANTS_PATH, orcid);
        return postClientResponseWithToken(grantsPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GRANTS_PATH)
    public ClientResponse addGrantsJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI grantsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(GRANTS_PATH, orcid);
        return postClientResponseWithToken(grantsPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(GRANTS_PATH)
    public ClientResponse updateGrantsXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI grantsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(GRANTS_PATH, orcid);
        return putClientResponseWithToken(grantsPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GRANTS_PATH)
    public ClientResponse updateGrantsJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI grantsPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(GRANTS_PATH, orcid);
        return putClientResponseWithToken(grantsPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }
    
    @Override
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public ClientResponse addExternalIdentifiersXml(String orcid, OrcidMessage orcidMessage, String token) {
        URI worksPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(EXTERNAL_IDENTIFIER_PATH, orcid);
        return postClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_XML, orcidMessage, token);
    }

    @Override
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public ClientResponse addExternalIdentifiersJson(String orcid, OrcidMessage orcidMessage, String token) {
        URI worksPathWithOrcidUrl = orcidClientHelper.deriveUriFromRestPath(EXTERNAL_IDENTIFIER_PATH, orcid);
        return postClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_JSON, orcidMessage, token);
    }

    @Override
    @PUT
    @Consumes(MediaType.WILDCARD)
    @Path(WEBHOOKS_PATH)
    public ClientResponse registerWebhook(String orcid, String webhookUri, String token) {
        URI worksPathWithOrcidUrl = UriBuilder.fromPath(WEBHOOKS_PATH).buildFromEncoded(orcid, webhookUri);
        return putClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_JSON, null, token);
    }

    @Override
    @DELETE
    @Consumes(MediaType.WILDCARD)
    @Path(WEBHOOKS_PATH)
    public ClientResponse unregisterWebhook(String orcid, String webhookUri, String token) {
        URI worksPathWithOrcidUrl = UriBuilder.fromPath(WEBHOOKS_PATH).buildFromEncoded(orcid, webhookUri);
        return deleteClientResponseWithToken(worksPathWithOrcidUrl, VND_ORCID_JSON, token);
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
    public ClientResponse viewFullDetailsXml(String orcid, String accessToken) {
        URI profilePathWithOrcidUrl = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientResponseWithToken(profilePathWithOrcidUrl, VND_ORCID_XML, accessToken);
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
    public ClientResponse viewFullDetailsJson(@PathParam("orcid") String orcid, String accessToken) {
        URI profilePathWithOrcidUrl = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientResponseWithToken(profilePathWithOrcidUrl, VND_ORCID_JSON, accessToken);
    }

    private ClientResponse postClientResponseWithToken(URI restPath, String accept, OrcidMessage orcidMessage, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).post(ClientResponse.class, orcidMessage);
    }

    private ClientResponse putClientResponseWithToken(URI restPath, String accept, OrcidMessage orcidMessage, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).put(ClientResponse.class, orcidMessage);
    }

    private ClientResponse deleteClientResponseWithToken(URI restPath, String accept, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).delete(ClientResponse.class);
    }

    private ClientResponse getClientResponseWithToken(URI restPath, String accept, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).get(ClientResponse.class);
    }

    private WebResource.Builder setupRequestCommonParams(URI restpath, String accept, String oauthToken) {

        WebResource rootResource = orcidClientHelper.createRootResource(restpath);
        WebResource.Builder built = addOauthHeader(rootResource, oauthToken).accept(accept).type(accept);
        return built;
    }

    @Override
    public ClientResponse viewStatusText(String accessToken) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(STATUS_PATH).build(), MediaType.TEXT_PLAIN);
    }

    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(BIO_PATH)
    public ClientResponse viewBioDetailsHtml(@PathParam("orcid") String orcid, String accessToken) {
        URI bioPathWithOrcid = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return getClientResponseWithToken(bioPathWithOrcid, MediaType.TEXT_HTML, accessToken);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(BIO_PATH)
    public ClientResponse viewBioDetailsXml(@PathParam("orcid") String orcid, String accessToken) {
        URI bioPathWithOrcid = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return getClientResponseWithToken(bioPathWithOrcid, VND_ORCID_XML, accessToken);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_XML })
    @Path(BIO_PATH)
    public ClientResponse viewBioDetailsJson(@PathParam("orcid") String orcid, String accessToken) {
        URI bioPathWithOrcid = UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid);
        return getClientResponseWithToken(bioPathWithOrcid, VND_ORCID_JSON, accessToken);
    }

    @Override
    public ClientResponse viewExternalIdentifiersHtml(@PathParam("orcid") String orcid, String accessToken) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClientResponse viewExternalIdentifiersXml(@PathParam("orcid") String orcid, String accessToken) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClientResponse viewExternalIdentifiersJson(@PathParam("orcid") String orcid, String accessToken) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(PROFILE_GET_PATH)
    public ClientResponse viewFullDetailsHtml(@PathParam("orcid") String orcid, String accessToken) {
        URI bioPathWithOrcid = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientResponseWithToken(bioPathWithOrcid, MediaType.TEXT_HTML, accessToken);
    }

    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(WORKS_PATH)
    public ClientResponse viewWorksDetailsHtml(@PathParam("orcid") String orcid, String accessToken) {
        URI worksPathWithOrcid = UriBuilder.fromPath(WORKS_PATH).build(orcid);
        return getClientResponseWithToken(worksPathWithOrcid, MediaType.TEXT_HTML, accessToken);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML })
    @Path(WORKS_PATH)
    public ClientResponse viewWorksDetailsXml(@PathParam("orcid") String orcid, String accessToken) {
        URI worksPathWithOrcid = UriBuilder.fromPath(WORKS_PATH).build(orcid);
        return getClientResponseWithToken(worksPathWithOrcid, VND_ORCID_XML, accessToken);
    }

    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public ClientResponse viewWorksDetailsJson(@PathParam("orcid") String orcid, String accessToken) {
        URI worksPathWithOrcid = UriBuilder.fromPath(WORKS_PATH).build(orcid);
        return getClientResponseWithToken(worksPathWithOrcid, VND_ORCID_JSON, accessToken);
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
    public ClientResponse searchByQueryJSON(String query, String accessToken) {
        URI bioSearchpath = UriBuilder.fromPath(BIO_SEARCH_PATH).replaceQuery(query).build();
        return getClientResponseWithToken(bioSearchpath, VND_ORCID_JSON, accessToken);
    }

    @Override
    /**
     * Gets the XML representation any Orcid Profiles (BIO) only 
     * relevant to the given query
     * @param query
     * @return
     */
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML })
    @Path(BIO_SEARCH_PATH)
    public ClientResponse searchByQueryXML(String query, String accessToken) {
        // TODO Auto-generated method stub
        return null;
    }

    private WebResource.Builder addOauthHeader(WebResource webResource, String oAuthToken) {
        return webResource.header("Authorization", "Bearer " + oAuthToken);
    }

}