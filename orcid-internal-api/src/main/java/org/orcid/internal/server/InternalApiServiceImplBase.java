package org.orcid.internal.server;

import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_PERSON_READ;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_TOGGLZ_READ;
import static org.orcid.core.api.OrcidApiConstants.MEMBER_INFO;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.orcid.api.common.T2OrcidApiService;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public abstract class InternalApiServiceImplBase {
    private InternalApiServiceDelegator serviceDelegator;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    public void setServiceDelegator(InternalApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }
    
    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    @ApiOperation(value = "Check the server status", hidden = true)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }
    
    /**
     * 
     * @param formParams
     * @return
     */
    @POST
    @Path(T2OrcidApiService.OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@FormParam("grant_type") String grantType, MultivaluedMap<String, String> formParams) {
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
    }
    
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_PERSON_READ)
    public Response viewPersonDetails(@PathParam("orcid") String orcid) {
        Response response = serviceDelegator.viewPersonLastModified(orcid); 
        return response;
    }
    
    /**
     * 
     * @param formParams
     * @return
     */
    @POST
    @Path(MEMBER_INFO)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response viewMemberDetails(@RequestParam String member) {
        Response response = serviceDelegator.viewMemberInfo(member);
        return response;
    }
    
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_TOGGLZ_READ)
    public Response viewTogglz() {
        Response response = serviceDelegator.viewTogglz();
        return response;
    }
}
