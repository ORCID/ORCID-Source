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
package org.orcid.api.t1.integration;

import static org.orcid.api.common.OrcidApiConstants.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidApiService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/04/2012
 */
public class T1OrcidApiClientImpl implements OrcidApiService<ClientResponse> {

    protected OrcidClientHelper orcidClientHelper;

    public T1OrcidApiClientImpl(URI baseUri, Client jerseyClient) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, jerseyClient);
    }

    @Override
    public ClientResponse viewStatusText() {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(STATUS_PATH).build(), MediaType.TEXT_PLAIN);
    }

    /**
     * GETs the HTML representation of the ORCID record
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    public ClientResponse viewBioDetailsHtml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), MediaType.TEXT_HTML);
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
    public ClientResponse viewBioDetailsXml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), VND_ORCID_XML);
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
    public ClientResponse viewBioDetailsRdf(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), APPLICATION_RDFXML);
    }

    /**
     * GETs the RDF Turtle representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the RDF Turtle representation of the ORCID record
     */
    @Override
    public ClientResponse viewBioDetailsTurtle(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), TEXT_TURTLE);
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
    public ClientResponse viewBioDetailsJson(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), VND_ORCID_JSON);
    }

    /**
     * GETs the HTML representation of the ORCID external identifiers
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    public ClientResponse viewExternalIdentifiersHtml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid), MediaType.TEXT_HTML);
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
    public ClientResponse viewExternalIdentifiersXml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid), VND_ORCID_XML);
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
    public ClientResponse viewExternalIdentifiersJson(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid), VND_ORCID_JSON);
    }

    /**
     * GETs the HTML representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    public ClientResponse viewFullDetailsHtml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid), MediaType.TEXT_HTML);
    }

    /**
     * GETs the XML representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    public ClientResponse viewFullDetailsXml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid), VND_ORCID_XML);
    }

    /**
     * GETs the JSON representation of the ORCID record containing all details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    public ClientResponse viewFullDetailsJson(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid), VND_ORCID_JSON);
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
    public ClientResponse viewWorksDetailsHtml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), MediaType.TEXT_HTML);
    }

    /**
     * GETs the XML representation of the ORCID record containing only work
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    public ClientResponse viewWorksDetailsXml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), VND_ORCID_XML);
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
    public ClientResponse viewWorksDetailsJson(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), VND_ORCID_JSON);
    }

    /**
     * @see OrcidApiService#searchByQueryJSON(String)
     */
    @Override
    public ClientResponse searchByQueryJSON(String query) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_SEARCH_PATH).replaceQuery(query).build(), VND_ORCID_JSON);
    }

    /**
     * @see OrcidApiService#searchByQueryXML(String)
     */
    @Override
    public ClientResponse searchByQueryXML(String query) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(BIO_SEARCH_PATH).replaceQuery(query).build(), VND_ORCID_XML);
    }

}
