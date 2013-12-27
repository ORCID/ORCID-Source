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

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidApiService;
import org.orcid.api.t2.T2OrcidApiService;
import org.orcid.jaxb.model.message.OrcidMessage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class T2OrcidApiClientImpl implements T2OrcidApiService<ClientResponse> {

    private OrcidClientHelper orcidClientHelper;

    public T2OrcidApiClientImpl(URI baseUri, Client client) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, client);
    }

    /**
     * POST an XML representation of the entire ORCID profile
     * 
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    public ClientResponse createProfileXML(OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(PROFILE_POST_PATH).build(), VND_ORCID_XML, orcidMessage);
    }

    /**
     * POST an JSON representation of the entire ORCID profile
     * 
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    public ClientResponse createProfileJson(OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(PROFILE_POST_PATH).build(), VND_ORCID_JSON, orcidMessage);
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
    public ClientResponse updateBioDetailsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only the
     * Biography details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    public ClientResponse updateBioDetailsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(BIO_PATH_NO_REGEX).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * POST an XML representation of the ORCID record containing only works
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    public ClientResponse addWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only works
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    public ClientResponse addWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * PUT an XML representation of the ORCID record containing only works
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    public ClientResponse updateWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * PUT a JSON representation of the ORCID record containing only works
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @Override
    public ClientResponse updateWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(WORKS_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * POST an XML representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         affiliation(s)
     */
    @Override
    public ClientResponse addAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         affiliation(s)
     */
    @Override
    public ClientResponse addAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * PUT an XML representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         affiliation(s)
     */
    @Override
    public ClientResponse updateAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * PUT a JSON representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         affiliation(s)
     */
    @Override
    public ClientResponse updateAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * POST an XML representation of the ORCID record containing only
     * grants details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse addGrantsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only
     * grants details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse addGrantsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * PUT an XML representation of the ORCID record containing only
     * grants details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse updateGrantsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * PUT a JSON representation of the ORCID record containing only
     * grants details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse updateGrantsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
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
    @Override
    public ClientResponse addExternalIdentifiersXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
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
    @Override
    public ClientResponse addExternalIdentifiersJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(EXTERNAL_IDENTIFIER_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * DELETE a resource. </p> <strong>This resource is not available to OAuth2
     * clients.</strong>
     * 
     * @param orcid
     * @return
     */
    @Override
    public ClientResponse deleteProfileJson(@PathParam("orcid") String orcid) {
        return deleteClientResponse(UriBuilder.fromPath(PROFILE_DELETE_PATH).build(orcid), VND_ORCID_JSON);
    }

    /**
     * DELETE a resource. </p> <strong>This resource is not available to OAuth2
     * clients.</strong>
     * 
     * @param orcid
     * @return
     */
    @Override
    public ClientResponse deleteProfileXML(@PathParam("orcid") String orcid) {
        return deleteClientResponse(UriBuilder.fromPath(PROFILE_DELETE_PATH).build(orcid), VND_ORCID_XML);
    }

    private ClientResponse postClientResponse(URI uri, String accept, OrcidMessage orcidMessage) {
        return orcidClientHelper.postClientResponse(uri, accept, orcidMessage);
    }

    private ClientResponse putClientResponse(URI uri, String accept, OrcidMessage orcidMessage) {
        return orcidClientHelper.putClientResponse(uri, accept, orcidMessage);
    }

    private ClientResponse deleteClientResponse(URI uri, String accept) {
        return orcidClientHelper.deleteClientResponse(uri, accept);
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
     * GETs the RDF Turtle representation of the ORCID record containing only
     * the Biography details
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
     * GETs the HTML representation of the ORCID record containing only affiliation
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    public ClientResponse viewAffiliationsDetailsHtml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), MediaType.TEXT_HTML);
    }

    /**
     * GETs the XML representation of the ORCID record containing only affiliation
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    public ClientResponse viewAffiliationsDetailsXml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), VND_ORCID_XML);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only affiliation
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    public ClientResponse viewAffiliationsDetailsJson(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(AFFILIATIONS_PATH).build(orcid), VND_ORCID_JSON);
    }

    /**
     * GETs the HTML representation of the ORCID record containing only grant
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    public ClientResponse viewGrantsDetailsHtml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), MediaType.TEXT_HTML);
    }

    /**
     * GETs the XML representation of the ORCID record containing only grant
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @Override
    public ClientResponse viewGrantsDetailsXml(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), VND_ORCID_XML);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only grant
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    public ClientResponse viewGrantsDetailsJson(@PathParam("orcid") String orcid) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(GRANTS_PATH).build(orcid), VND_ORCID_JSON);
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

    /**
     * TODO
     * */
    @Override
    public ClientResponse registerWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhook_uri) {
        return null;
    }

    /**
     * TODO
     * */
    @Override
    public ClientResponse registerWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhook_uri) {
        return null;
    }

    /**
     * TODO
     * */
    @Override
    public ClientResponse unregisterWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhook_uri) {
        return null;
    }

    /**
     * TODO
     * */
    @Override
    public ClientResponse unregisterWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhook_uri) {
        return null;
    }
}