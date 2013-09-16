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
package org.orcid.api.t2;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.api.common.OrcidApiService;
import org.orcid.jaxb.model.message.OrcidMessage;

import static org.orcid.api.common.OrcidApiConstants.*;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 07/03/2012
 */
public interface T2OrcidApiService<T> extends OrcidApiService<T> {

    public static final String OAUTH_TOKEN = "/oauth/token";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String CLIENT_SECRET_PARAM = "client_secret";
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String CODE_PARAM = "code";
    public static final String SCOPE_PARAM = "scope";
    public static final String STATE_PARAM = "state";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";

    public static final String CLIENT_CREDENTIAL_PARAM = "client_credential";

    /**
     * POST an XML representation of the entire ORCID profile
     * 
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(PROFILE_POST_PATH)
    T createProfileXML(OrcidMessage orcidMessage);

    /**
     * POST an JSON representation of the entire ORCID profile
     * 
     * @return the JSON representation of the ORCID record including the added
     *         work(s)
     */
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_POST_PATH)
    T createProfileJson(OrcidMessage orcidMessage);

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
    T updateBioDetailsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T updateBioDetailsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T addWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T addWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T updateWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T updateWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

    /**
     * POST an XML representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         affiliations(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(AFFILIATIONS_PATH)
    T addAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

    /**
     * POST a JSON representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         affiliations(s)
     */
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    T addAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

    /**
     * PUT an XML representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         affiliations(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(AFFILIATIONS_PATH)
    T updateAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

    /**
     * PUT a JSON representation of the ORCID record containing only
     * affiliations details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         affiliations(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    T updateAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T addExternalIdentifiersXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

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
    T addExternalIdentifiersJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage);

    /**
     * DELETE a resource. </p> <strong>This resource is not available to Oauth2
     * clients.</strong>
     * 
     * @param orcid
     * @return
     */
    @DELETE
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PROFILE_DELETE_PATH)
    T deleteProfileJson(@PathParam("orcid") String orcid);

    /**
     * DELETE a resource. </p> <strong>This resource is not available to OAuth2
     * clients.</strong>
     * 
     * @param orcid
     * @return
     */
    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(PROFILE_DELETE_PATH)
    T deleteProfileXML(@PathParam("orcid") String orcid);

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
    T registerWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri);

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
    T registerWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri);

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
    T unregisterWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri);

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
    T unregisterWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri);
}
