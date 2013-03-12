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

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

import org.orcid.api.common.OrcidApiService;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

import static org.orcid.api.common.OrcidApiConstants.*;

/**
 * Copyright 2011-2012 ORCID
 *
 * @author Declan Newman (declan) Date: 01/03/2012
 */
@Component
@Path("/")
public class T1OrcidApiServiceImpl implements OrcidApiService<Response> {

    final static Counter T1_GET_REQUESTS = Metrics.newCounter(T1OrcidApiServiceImpl.class, "T1-GET-REQUESTS");
    final static Counter T1_SEARCH_REQUESTS = Metrics.newCounter(T1OrcidApiServiceImpl.class, "T1-SEARCH-REQUESTS");


    final static Counter T1_SEARCH_RESULTS_NONE_FOUND = Metrics.newCounter(T1OrcidApiServiceImpl.class, "T1-SEARCH-RESULTS-NONE-FOUND");
    final static Counter T1_SEARCH_RESULTS_FOUND = Metrics.newCounter(T1OrcidApiServiceImpl.class, "T1-SEARCH-RESULTS-FOUND");

    @Context
    private UriInfo uriInfo;

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Resource(name = "orcidServiceDelegator")
    private OrcidApiServiceDelegator serviceDelegator;

    public void setServiceDelegator(OrcidApiServiceDelegator serviceDelegator) {
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
     *         the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(BIO_PATH)
    public Response viewBioDetailsHtml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        Response response = serviceDelegator.findBioDetails(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-bio.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing only the
     * Biography details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(BIO_PATH)
    public Response viewBioDetailsXml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findBioDetails(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only the
     * Biography details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_PATH)
    public Response viewBioDetailsJson(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findBioDetails(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID external identifiers
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response viewExternalIdentifiersHtml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        Response response = serviceDelegator.findExternalIdentifiers(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-external-ids.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing only the
     * external identifiers
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response viewExternalIdentifiersXml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findExternalIdentifiers(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only the
     * external identifiers
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIER_PATH)
    public Response viewExternalIdentifiersJson(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findExternalIdentifiers(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID record containing all details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(PROFILE_GET_PATH)
    public Response viewFullDetailsHtml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        Response response = serviceDelegator.findFullDetails(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-profile.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing all details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(PROFILE_GET_PATH)
    public Response viewFullDetailsXml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findFullDetails(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing all details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_GET_PATH)
    public Response viewFullDetailsJson(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findFullDetails(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID record containing only work
     * details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(WORKS_PATH)
    public Response viewWorksDetailsHtml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        Response response = serviceDelegator.findWorksDetails(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-works.xml\"").build();
    }

    /**
     * GETs the XML representation of the ORCID record containing only work
     * details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public Response viewWorksDetailsXml(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findWorksDetails(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only work
     * details
     *
     * @param orcid
     *         the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public Response viewWorksDetailsJson(@PathParam("orcid") String orcid) {
        T1_GET_REQUESTS.inc();
        return serviceDelegator.findWorksDetails(orcid);
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
        Response jsonQueryResults = serviceDelegator.searchByQuery(queryParams);
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
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(BIO_SEARCH_PATH)
    public Response searchByQueryXML(String query) {
        T1_SEARCH_REQUESTS.inc();
        Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
        Response xmlQueryResults = serviceDelegator.searchByQuery(queryParams);
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
