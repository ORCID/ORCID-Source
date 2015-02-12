/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import static org.orcid.core.api.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.core.api.OrcidApiConstants.BIO_PATH;
import static org.orcid.core.api.OrcidApiConstants.BIO_SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXPERIMENTAL_RDF_V1;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_PATH;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.TEXT_N3;
import static org.orcid.core.api.OrcidApiConstants.TEXT_TURTLE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORKS_PATH;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.common.OrcidApiService;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.api.common.delegator.impl.OrcidApiServiceVersionedDelegatorImpl;
import org.orcid.core.manager.impl.ValidationManagerImpl;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.util.OAuth2Utils;

import com.orcid.api.common.server.delegator.OrcidClientCredentialEndPointDelegator;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
abstract public class T1OrcidApiServiceImplBase implements OrcidApiService<Response>, InitializingBean {

    public static final String OAUTH_TOKEN = "/oauth/token";

    @Value("${org.orcid.core.pubBaseUri:http://orcid.org}")
    private String pubBaseUri;

    @Context
    private UriInfo uriInfo;

    private OrcidApiServiceDelegator orcidApiServiceDelegator;

    /**
     * Only used if service delegator is not set and this bean needs to
     * configure one for itself.
     */
    private String externalVersion;

    /**
     * Only used if service delegator is not set and this bean needs to
     * configure one for itself.
     */
    @Resource(name = "t1OrcidApiServiceDelegatorPrototype")
    private OrcidApiServiceVersionedDelegatorImpl orcidApiServiceDelegatorPrototype;

    // Base the RDF stuff on the root version of the API, because sits outside
    // the versioning mechanism
    @Resource(name = "t1OrcidApiServiceDelegatorLatest")
    private OrcidApiServiceDelegator orcidApiServiceDelegatorLatest;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setOrcidApiServiceDelegator(OrcidApiServiceDelegator orcidApiServiceDelegator) {
        this.orcidApiServiceDelegator = orcidApiServiceDelegator;
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
        if (orcidApiServiceDelegator == null && externalVersion != null) {
            orcidApiServiceDelegatorPrototype.setExternalVersion(externalVersion);
            ValidationManagerImpl outgoingValidationManagerImpl = new ValidationManagerImpl();
            outgoingValidationManagerImpl.setVersion(externalVersion);
            outgoingValidationManagerImpl.setValidateBibtex(false);
            orcidApiServiceDelegatorPrototype.setOutgoingValidationManager(outgoingValidationManagerImpl);
            orcidApiServiceDelegator = orcidApiServiceDelegatorPrototype;
        }
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
        return orcidApiServiceDelegatorLatest.findBioDetails(orcid);
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
        return orcidApiServiceDelegatorLatest.findBioDetails(orcid);
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
        return orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID record containing only
     * affiliation details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(AFFILIATIONS_PATH)
    public Response viewAffiliationsDetailsHtml(@PathParam("orcid") String orcid) {
        Response response = orcidApiServiceDelegator.findAffiliationsDetailsFromPublicCache(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-affiliations.xml\"").build();
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
        return orcidApiServiceDelegator.findAffiliationsDetailsFromPublicCache(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only
     * affiliation details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(AFFILIATIONS_PATH)
    public Response viewAffiliationsDetailsJson(@PathParam("orcid") String orcid) {
        return orcidApiServiceDelegator.findAffiliationsDetailsFromPublicCache(orcid);
    }

    /**
     * GETs the HTML representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the HTML representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(FUNDING_PATH)
    public Response viewFundingDetailsHtml(@PathParam("orcid") String orcid) {
        Response response = orcidApiServiceDelegator.findFundingDetailsFromPublicCache(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-grants.xml\"").build();
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
        return orcidApiServiceDelegator.findFundingDetailsFromPublicCache(orcid);
    }

    /**
     * GETs the JSON representation of the ORCID record containing only funding
     * details
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return the JSON representation of the ORCID record
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_PATH)
    public Response viewFundingDetailsJson(@PathParam("orcid") String orcid) {
        return orcidApiServiceDelegator.findFundingDetailsFromPublicCache(orcid);
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
        return orcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
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
        return orcidApiServiceDelegator.redirectClientToGroup(clientId);
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
        Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
        Response xmlQueryResults = orcidApiServiceDelegator.searchByQuery(queryParams);
        registerSearchMetrics(xmlQueryResults);
        return xmlQueryResults;
    }

    private void registerSearchMetrics(Response results) {
        OrcidMessage orcidMessage = (OrcidMessage) results.getEntity();
        if (orcidMessage != null && orcidMessage.getOrcidSearchResults() != null && !orcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty()) {
            return;
        }
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

}