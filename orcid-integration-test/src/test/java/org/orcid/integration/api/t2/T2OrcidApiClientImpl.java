package org.orcid.integration.api.t2;

import static org.orcid.core.api.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.BIO_PATH_NO_REGEX;
import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_DELETE_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORKS_PATH;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.api.common.T2OrcidApiService;
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
     * fundings details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse addFundingXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(FUNDING_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only
     * fundings details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse addFundingJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return postClientResponse(UriBuilder.fromPath(FUNDING_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
    }

    /**
     * PUT an XML representation of the ORCID record containing only
     * fundings details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse updateFundingXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(FUNDING_PATH).build(orcid), VND_ORCID_XML, orcidMessage);
    }

    /**
     * PUT a JSON representation of the ORCID record containing only
     * fundings details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         grant(s)
     */
    @Override
    public ClientResponse updateFundingJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return putClientResponse(UriBuilder.fromPath(FUNDING_PATH).build(orcid), VND_ORCID_JSON, orcidMessage);
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

    @Override
    public ClientResponse viewClient(String clientId) {
        return orcidClientHelper.getClientResponse(UriBuilder.fromPath(CLIENT_PATH).build(clientId), MediaType.WILDCARD);
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