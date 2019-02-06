package org.orcid.api.t2.server;

import static org.orcid.core.api.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.BIO_PATH;
import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_PATH;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_DELETE_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WEBHOOKS_PATH;
import static org.orcid.core.api.OrcidApiConstants.WORKS_PATH;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.api.t2.server.delegator.impl.T2OrcidApiServiceVersionedDelegatorImpl;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Declan Newman (declan) Date: 07/03/2012
 */
abstract public class T2OrcidApiServiceImplBase implements T2OrcidApiService<Response>, InitializingBean {

    @Context
    private UriInfo uriInfo;

    private T2OrcidApiServiceDelegator serviceDelegator;        

    /**
     * Only used if service delegator is not set and this bean needs to
     * configure one for itself.
     */
    private String externalVersion;

    /**
     * Only used if service delegator is not set and this bean needs to
     * configure one for itself.
     */
    @Resource(name = "t2OrcidApiServiceDelegatorPrototype")
    private T2OrcidApiServiceVersionedDelegatorImpl serviceDelegatorPrototype;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setServiceDelegator(T2OrcidApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /**
     * 
     * @param externalVersion
     *            The API schema version to use. Not needed if we are setting a
     *            service delegator explicitly (and not relying on this bean to
     *            configure one for itself).
     */
    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Automatically configure a service delegator, if one hasn't been set
        if (serviceDelegator == null && externalVersion != null) {
            serviceDelegatorPrototype.setExternalVersion(externalVersion);
            serviceDelegatorPrototype.autoConfigureValidators();
            serviceDelegator = serviceDelegatorPrototype;
        }
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
     * GETs the XML representation of the ORCID record containing only
     * affiliation details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(AFFILIATIONS_PATH)
    public Response viewAffiliationsDetailsXml(@PathParam("orcid") String orcid) {
        return serviceDelegator.findAffiliationsDetails(orcid);
    }

    /**
     * GETs the XML representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record
     */
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(FUNDING_PATH)
    public Response viewFundingDetailsXml(@PathParam("orcid") String orcid) {
        return serviceDelegator.findFundingDetails(orcid);
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
        return serviceDelegator.findWorksDetails(orcid);
    }

    /**
     * Sends a redirect from the client URI to the group URI
     * 
     * @param clientId
     *            the client ID that corresponds to the client
     * @return a redirect to the ORCID record for the client's group
     */
    @Override
    @GET
    @Path(CLIENT_PATH)
    public Response viewClient(@PathParam("client_id") String clientId) {
        return serviceDelegator.redirectClientToGroup(clientId);
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
        return serviceDelegator.updateBioDetails(uriInfo, orcid, orcidMessage);
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
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public Response addWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.addWorks(uriInfo, orcid, orcidMessage);
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
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public Response addWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.addWorks(uriInfo, orcid, orcidMessage);
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
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(WORKS_PATH)
    public Response updateWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.updateWorks(uriInfo, orcid, orcidMessage);
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
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS_PATH)
    public Response updateWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.updateWorks(uriInfo, orcid, orcidMessage);
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
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(AFFILIATIONS_PATH)
    public Response addAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.addAffiliations(uriInfo, orcid, orcidMessage);
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
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    public Response addAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.addAffiliations(uriInfo, orcid, orcidMessage);
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
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(AFFILIATIONS_PATH)
    public Response updateAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.updateAffiliations(uriInfo, orcid, orcidMessage);
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
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    public Response updateAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.updateAffiliations(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST an XML representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         funding(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(FUNDING_PATH)
    public Response addFundingXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.addFunding(uriInfo, orcid, orcidMessage);
    }

    /**
     * POST a JSON representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         funding(s)
     */
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_PATH)
    public Response addFundingJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.addFunding(uriInfo, orcid, orcidMessage);
    }

    /**
     * PUT an XML representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         funding(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(FUNDING_PATH)
    public Response updateFundingXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.updateFunding(uriInfo, orcid, orcidMessage);
    }

    /**
     * PUT a JSON representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         funding(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_PATH)
    public Response updateFundingJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage) {
        return serviceDelegator.updateFunding(uriInfo, orcid, orcidMessage);
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
    public Response obtainOauth2TokenPost(@HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) String authorization, @FormParam("grant_type") String grantType, MultivaluedMap<String, String> formParams) {
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
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
        return serviceDelegator.unregisterWebhook(uriInfo, orcid, webhookUri);
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
        return serviceDelegator.unregisterWebhook(uriInfo, orcid, webhookUri);
    }
}