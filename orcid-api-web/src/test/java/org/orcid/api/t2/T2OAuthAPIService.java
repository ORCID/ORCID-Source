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

import static org.orcid.api.common.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.api.common.OrcidApiConstants.BIO_PATH;
import static org.orcid.api.common.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.api.common.OrcidApiConstants.ORCID_JSON;
import static org.orcid.api.common.OrcidApiConstants.ORCID_XML;
import static org.orcid.api.common.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.api.common.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.api.common.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.api.common.OrcidApiConstants.WEBHOOKS_PATH;
import static org.orcid.api.common.OrcidApiConstants.WORKS_PATH;
import static org.orcid.api.common.OrcidApiConstants.GRANTS_PATH;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.orcid.api.common.OrcidApiConstants;
import org.orcid.jaxb.model.message.OrcidMessage;

import com.sun.jersey.api.client.ClientResponse;

public interface T2OAuthAPIService<T> extends OAuthOrcidApiService<T> {

    @POST
    @Path(T2OrcidApiService.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClientResponse obtainOauth2TokenPost(String grantType, MultivaluedMap<String, String> formParams);

    /**
     * POST an XML representation of the entire ORCID profile
     * 
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @POST
    @Produces(value = { OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(OrcidApiConstants.PROFILE_POST_PATH)
    T createProfileXML(OrcidMessage orcidMessage, String token);

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
    T createProfileJson(OrcidMessage orcidMessage, String token);

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
    T updateBioDetailsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T updateBioDetailsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T addWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T addWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T updateWorksXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T updateWorksJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T addAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T addAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T updateAffiliationsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T updateAffiliationsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

    /**
     * POST an XML representation of the ORCID record containing only
     * grant details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         grant(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(GRANTS_PATH)
    T addGrantsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

    /**
     * POST a JSON representation of the ORCID record containing only
     * grant details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         grant(s)
     */
    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GRANTS_PATH)
    T addGrantsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

    /**
     * PUT an XML representation of the ORCID record containing only
     * grant details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the XML representation of the ORCID record including the added
     *         grant(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(GRANTS_PATH)
    T updateGrantsXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

    /**
     * PUT a JSON representation of the ORCID record containing only
     * grant details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record including the added
     *         grant(s)
     */
    @PUT
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GRANTS_PATH)
    T updateGrantsJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

    
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
    T addExternalIdentifiersXml(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

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
    T addExternalIdentifiersJson(@PathParam("orcid") String orcid, OrcidMessage orcidMessage, String token);

    /**
     * PUT a webhook callback url to an ORCID record
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param the
     *            url encoded webhook url
     * @return the response code
     */
    @PUT
    @Consumes(value = { MediaType.WILDCARD })
    @Path(WEBHOOKS_PATH)
    T registerWebhook(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri, String token);

    /**
     * DELETE a webhook callback url to an ORCID record
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param the
     *            url encoded webhook url
     * @return the response code
     */
    @PUT
    @Consumes(value = { MediaType.WILDCARD })
    @Path(WEBHOOKS_PATH)
    T unregisterWebhook(String orcid, String webhookUri, String token);

}
