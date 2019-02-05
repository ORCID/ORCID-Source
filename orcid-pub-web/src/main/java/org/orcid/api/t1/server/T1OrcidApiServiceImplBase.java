package org.orcid.api.t1.server;

import static org.orcid.core.api.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.core.api.OrcidApiConstants.BIO_PATH;
import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXPERIMENTAL_RDF_V1;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.TEXT_N3;
import static org.orcid.core.api.OrcidApiConstants.TEXT_TURTLE;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.api.common.delegator.impl.OrcidApiServiceVersionedDelegatorImpl;
import org.orcid.core.manager.impl.ValidationManagerImpl;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.oauth.OAuthErrorUtils;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
abstract public class T1OrcidApiServiceImplBase implements OrcidApiService<Response>, InitializingBean {

    @Value("${org.orcid.core.pubBaseUri:http://orcid.org}")
    private String pubBaseUri;

    @Context
    protected UriInfo uriInfo;

    protected OrcidApiServiceDelegator orcidApiServiceDelegator;

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
     * returns a redirect to experimental rdf api
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return A 303 See Other redirect
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
        return Response.seeOther(uri).build();
    }


    /**
     * returns a redirect to experimental rdf api
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @return A 303 See Other redirect
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
        return Response.seeOther(uri).build();
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

    protected void registerSearchMetrics(Response results) {
        OrcidMessage orcidMessage = (OrcidMessage) results.getEntity();
        if (orcidMessage != null && orcidMessage.getOrcidSearchResults() != null && !orcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty()) {
            return;
        }
    }

    /**
     * @param formParams
     * @return
     */
    @POST
    @Path(T2OrcidApiService.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) String authorization, @FormParam("grant_type") String grantType, MultivaluedMap<String, String> formParams) {
        try {
            return orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
        } catch(Exception e) {
            OAuthError error = OAuthErrorUtils.getOAuthError(e);
            HttpStatus status = HttpStatus.valueOf(error.getResponseStatus().getStatusCode());
            return Response.status(status.value()).entity(error).build();
        }
    }

}