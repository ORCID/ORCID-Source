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
package org.orcid.api.t1.server;

import static org.orcid.api.common.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.api.common.OrcidApiConstants.BIO_PATH;
import static org.orcid.api.common.OrcidApiConstants.BIO_SEARCH_PATH;
import static org.orcid.api.common.OrcidApiConstants.EXPERIMENTAL_RDF_V1;
import static org.orcid.api.common.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.api.common.OrcidApiConstants.ORCID_JSON;
import static org.orcid.api.common.OrcidApiConstants.ORCID_XML;
import static org.orcid.api.common.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.api.common.OrcidApiConstants.STATUS_PATH;
import static org.orcid.api.common.OrcidApiConstants.TEXT_N3;
import static org.orcid.api.common.OrcidApiConstants.TEXT_TURTLE;
import static org.orcid.api.common.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.api.common.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.api.common.OrcidApiConstants.WORKS_PATH;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.common.OrcidApiService;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.beans.factory.annotation.Value;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 01/03/2012
 */
abstract public class T1OrcidApiServiceImplBase implements OrcidApiService<Response> {

    @Value("${org.orcid.core.pubBaseUri:http://orcid.org}")
    private String pubBaseUri;

    final static Counter T1_GET_REQUESTS = Metrics.newCounter(T1OrcidApiServiceImplBase.class, "T1-GET-REQUESTS");
    final static Counter T1_SEARCH_REQUESTS = Metrics.newCounter(T1OrcidApiServiceImplBase.class, "T1-SEARCH-REQUESTS");

    final static Counter T1_SEARCH_RESULTS_NONE_FOUND = Metrics.newCounter(T1OrcidApiServiceImplBase.class, "T1-SEARCH-RESULTS-NONE-FOUND");
    final static Counter T1_SEARCH_RESULTS_FOUND = Metrics.newCounter(T1OrcidApiServiceImplBase.class, "T1-SEARCH-RESULTS-FOUND");

    @Context
    private UriInfo uriInfo;

    private OrcidApiServiceDelegator orcidApiServiceDelegator;

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setOrcidApiServiceDelegator(OrcidApiServiceDelegator orcidApiServiceDelegator) {
        this.orcidApiServiceDelegator = orcidApiServiceDelegator;
    }

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    public Response viewStatusText() {
        return orcidApiServiceDelegator.viewStatusText();
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
        T1_GET_REQUESTS.inc();
        Response response = orcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
    }

    /**
     * returns a redirect to experimental rdf api
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return A 307 redirect
     */
    @GET
    @Produces(value = { APPLICATION_RDFXML })
    @Path(BIO_PATH)
    public Response redirBioDetailsRdf(@PathParam("orcid") String orcid) {
        URI uri = null;
        try {
            uri = new URI(pubBaseUri + EXPERIMENTAL_RDF_V1 + "/" + orcid);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * GETs the RDF/XML representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the RDF/XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { APPLICATION_RDFXML })
    @Path(EXPERIMENTAL_RDF_V1 + BIO_PATH)
    public Response viewBioDetailsRdf(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findBioDetails(orcid);
    }

    /**
     * returns a redirect to experimental rdf api
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return A 307 redirect
     */
    @GET
    @Produces(value = { TEXT_N3, TEXT_TURTLE })
    @Path(BIO_PATH)
    public Response redirBioDetailsTurtle(@PathParam("orcid") String orcid) {
        URI uri = null;
        try {
            uri = new URI(pubBaseUri + EXPERIMENTAL_RDF_V1 + "/" + orcid);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * GETs the RDF Turtle representation of the ORCID record containing only
     * the Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the RDF Turtle representation of the ORCID record
     */
    @GET
    @Produces(value = { TEXT_N3, TEXT_TURTLE })
    @Path(EXPERIMENTAL_RDF_V1 + BIO_PATH)
    public Response viewBioDetailsTurtle(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findBioDetails(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        Response response = orcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        Response response = orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        Response response = orcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-works.xml\"").build();
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
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
        T1_GET_REQUESTS.inc();
        return orcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
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
    public Response searchByQueryJSON(String query) {
        T1_SEARCH_REQUESTS.inc();
        Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
        Response jsonQueryResults = orcidApiServiceDelegator.searchByQuery(queryParams);
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
    public Response searchByQueryXML(String query) {
        T1_SEARCH_REQUESTS.inc();
        Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
        Response xmlQueryResults = orcidApiServiceDelegator.searchByQuery(queryParams);
        registerSearchMetrics(xmlQueryResults);
        return xmlQueryResults;
    }

    private void registerSearchMetrics(Response results) {
        OrcidMessage orcidMessage = (OrcidMessage) results.getEntity();
        if (orcidMessage != null && orcidMessage.getOrcidSearchResults() != null && !orcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty()) {
            T1_SEARCH_RESULTS_FOUND.inc(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().size());
            return;
        }

        T1_SEARCH_RESULTS_NONE_FOUND.inc();
    }

}
