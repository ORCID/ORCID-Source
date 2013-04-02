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
package org.orcid.api.t2.server;

import static org.orcid.api.common.OrcidApiConstants.BIO_PATH;
import static org.orcid.api.common.OrcidApiConstants.BIO_SEARCH_PATH;
import static org.orcid.api.common.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.api.common.OrcidApiConstants.ORCID_JSON;
import static org.orcid.api.common.OrcidApiConstants.ORCID_XML;
import static org.orcid.api.common.OrcidApiConstants.PROFILE_DELETE_PATH;
import static org.orcid.api.common.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.api.common.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.api.common.OrcidApiConstants.STATUS_PATH;
import static org.orcid.api.common.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.api.common.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.api.common.OrcidApiConstants.WEBHOOKS_PATH;
import static org.orcid.api.common.OrcidApiConstants.WORKS_PATH;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.t2.T2OrcidApiService;
import org.orcid.api.t2.server.delegator.OrcidClientCredentialEndPointDelegator;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Component;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 07/03/2012
 */
@Component
@Path("/")
public class T2OrcidApiServiceImpl implements T2OrcidApiService<Response> {

    @Context
    private UriInfo uriInfo;

    final static Counter T2_GET_REQUESTS = Metrics.newCounter(T2OrcidApiServiceImpl.class, "T2-GET-REQUESTS");
    final static Counter T2_SEARCH_REQUESTS = Metrics.newCounter(T2OrcidApiServiceImpl.class, "T2-SEARCH-REQUESTS");
    final static Counter T2_PUT_REQUESTS = Metrics.newCounter(T2OrcidApiServiceImpl.class, "T2-PUT-REQUESTS");
    final static Counter T2_POST_REQUESTS = Metrics.newCounter(T2OrcidApiServiceImpl.class, "T2-POST-REQUESTS");

    final static Counter T2_SEARCH_RESULTS_NONE_FOUND = Metrics.newCounter(T2OrcidApiServiceImpl.class, "T2-SEARCH-RESULTS-NONE-FOUND");
    final static Counter T2_SEARCH_RESULTS_FOUND = Metrics.newCounter(T2OrcidApiServiceImpl.class, "T2-SEARCH-RESULTS-FOUND");

    @Resource
    private T2OrcidApiServiceDelegator serviceDelegator;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setServiceDelegator(T2OrcidApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }

    /**
     * GETs the HTML representation of the ORCID record
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(BIO_PATH)
    public Response viewBioDetailsHtml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        Response response = serviceDelegator.findBioDetails(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-bio.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(BIO_PATH)
    public Response viewBioDetailsXml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findBioDetails(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_PATH)
    public Response viewBioDetailsJson(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findBioDetails(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID external identifiers
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response viewExternalIdentifiersHtml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        Response response = serviceDelegator.findExternalIdentifiers(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-external-ids.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing only the
     * external identifiers
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response viewExternalIdentifiersXml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findExternalIdentifiers(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only the
     * external identifiers
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response viewExternalIdentifiersJson(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findExternalIdentifiers(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(PROFILE_GET_PATH)
    public Response viewFullDetailsHtml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        Response response = serviceDelegator.findFullDetails(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-profile.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(PROFILE_GET_PATH)
    public Response viewFullDetailsXml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findFullDetails(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_GET_PATH)
    public Response viewFullDetailsJson(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findFullDetails(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID record containing only work
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(WORKS_PATH)
    public Response viewWorksDetailsHtml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        Response response = serviceDelegator.findWorksDetails(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-bio.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing only work
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(WORKS_PATH)
    public Response viewWorksDetailsXml(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findWorksDetails(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only work
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public Response viewWorksDetailsJson(@PathParam("orcid") String orcid) {
        T2_GET_REQUESTS.inc();
        return serviceDelegator.findWorksDetails(orcid);
    }

    /**
     * POST an XML representation of the entire ORCID profile
     * 
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(PROFILE_POST_PATH)
    public Response createProfileXML(OrcidMessage orcidMessage) {
        T2_POST_REQUESTS.inc();
        return serviceDelegator.createProfile(uriInfo, orcidMessage);
    }

    /**
     * POST an JSON representation of the entire ORCID profile
     * 
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_POST_PATH)
    public Response createProfileJson(OrcidMessage orcidMessage) {
        T2_POST_REQUESTS.inc();
        return serviceDelegator.createProfile(uriInfo, orcidMessage);
    }

    /**
     * GETs the XML representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(BIO_PATH)
    public Response updateBioDetailsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_PUT_REQUESTS.inc();
        return serviceDelegator.updateBioDetails(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_PATH)
    public Response updateBioDetailsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_PUT_REQUESTS.inc();
        return serviceDelegator.updateBioDetails(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST an XML representation of the ORCID work containing only works
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public Response addWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_POST_REQUESTS.inc();
        return serviceDelegator.addWorks(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID work containing only works
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public Response addWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_POST_REQUESTS.inc();
        return serviceDelegator.addWorks(uriInfo, orcid, orcidMessage);
    }

    /**
     * PUT an XML representation of the ORCID work containing only works details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public Response updateWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_PUT_REQUESTS.inc();
        return serviceDelegator.updateWorks(uriInfo, orcid, orcidMessage);
    }

    /**
     * PUT a JSON representation of the ORCID work containing only works details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public Response updateWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_PUT_REQUESTS.inc();
        return serviceDelegator.updateWorks(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST an XML representation of the ORCID external identifiers containing
     * only the URLs details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         external identifiers(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response addExternalIdentifiersXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_POST_REQUESTS.inc();
        return serviceDelegator.addExternalIdentifiers(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID external identifiers containing
     * only the URLs details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         external identifiers(s)
     */
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response addExternalIdentifiersJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        T2_POST_REQUESTS.inc();
        return serviceDelegator.addExternalIdentifiers(uriInfo, orcid, orcidMessage);
    }

    /**
     * DELETE a resource. </p> <strong>This resource is not available to OAuth2
     * clients.</strong>
     * 
     * @param orcid
     * @return
     */
    @Override
    @DELETE
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_DELETE_PATH)
    public Response deleteProfileJson(@PathParam("orcid") String orcid) {
        return serviceDelegator.deleteProfile(uriInfo, orcid);
    }

    /**
     * DELETE a resource. </p> <strong>This resource is not available to OAuth2
     * clients.</strong>
     * 
     * @param orcid
     * @return
     */
    @Override
    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(PROFILE_DELETE_PATH)
    public Response deleteProfileXML(@PathParam("orcid") String orcid) {
        return serviceDelegator.deleteProfile(uriInfo, orcid);
    }

    /**
     * 
     * @param formParams
     * @return
     */
    @POST
    @Path(OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@FormParam("grant_type") String grantType, MultivaluedMap<String, String> formParams) {
        String clientId = formParams.getFirst("client_id");
        String clientSecret = formParams.getFirst("client_secret");
        String code = formParams.getFirst("code");
        String state = formParams.getFirst("state");
        String redirectUri = formParams.getFirst("redirect_uri");
        String resourceId = formParams.getFirst("resource_id");
        String refreshToken = formParams.getFirst("refresh_token");
        String scopeList = formParams.getFirst("scope");
        Set<String> scopes = new HashSet<String>();
        if (StringUtils.isNotEmpty(scopeList)) {
            scopes = OAuth2Utils.parseParameterList(scopeList);
        }
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(clientId, clientSecret, grantType, refreshToken, code, scopes, state, redirectUri, resourceId);
    }

    /**
     * Gets the JSON representation any Orcid Profiles (BIO) only relevant to
     * the given query
     * 
     * @param query
     * @return
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_SEARCH_PATH)
    public Response searchByQueryJSON(@QueryParam("bogus") @DefaultValue("") String query) {
        T2_SEARCH_REQUESTS.inc();
        Map<String, List<String>> solrParams = uriInfo.getQueryParameters();
        Response jsonQueryResults = serviceDelegator.searchByQuery(solrParams);
        registerSearchMetrics(jsonQueryResults);
        return jsonQueryResults;
    }

    /**
     * Gets the XML representation any Orcid Profiles (BIO) only relevant to the
     * given query
     * 
     * @param query
     * @return
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(BIO_SEARCH_PATH)
    public Response searchByQueryXML(@QueryParam("bogus") @DefaultValue("") String query) {
        T2_SEARCH_REQUESTS.inc();
        Map<String, List<String>> solrParams = uriInfo.getQueryParameters();
        Response xmlQueryResults = serviceDelegator.searchByQuery(solrParams);
        registerSearchMetrics(xmlQueryResults);
        return xmlQueryResults;
    }

    private void registerSearchMetrics(Response results) {
        OrcidMessage orcidMessage = (OrcidMessage) results.getEntity();
        if (orcidMessage != null && orcidMessage.getOrcidSearchResults() != null && !orcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty()) {
            T2_SEARCH_RESULTS_FOUND.inc(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().size());
            return;
        }

        T2_SEARCH_RESULTS_NONE_FOUND.inc();
    }

    /**
     * Register a new webhook to a specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be added to the user
     * @return
     * */
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(WEBHOOKS_PATH)
    public Response registerWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.registerWebhook(uriInfo, orcid, webhookUri);
    }

    /**
     * Register a new webhook to a specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be added to the user
     * @return
     * */
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WEBHOOKS_PATH)
    public Response registerWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.registerWebhook(uriInfo, orcid, webhookUri);
    }

    /**
     * Unregister a webhook from specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be deleted from the user
     * @return
     * */
    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(WEBHOOKS_PATH)
    public Response unregisterWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.unregisterWebhook(orcid, webhookUri);
    }

    /**
     * Unregister a webhook from specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be deleted from the user
     * @return
     * */
    @DELETE
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WEBHOOKS_PATH)
    public Response unregisterWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.unregisterWebhook(orcid, webhookUri);
    }
}
