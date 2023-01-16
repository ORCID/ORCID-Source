package org.orcid.internal.server;

import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_FIND_ORCID_BY_EMAIL;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_PERSON_READ;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_TOGGLZ_READ;
import static org.orcid.core.api.OrcidApiConstants.MEMBER_INFO;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.OAUTH_TOKEN;

import javax.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.apache.commons.net.util.Base64;
import org.orcid.api.common.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;

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
    @Operation(description = "Check the server status", hidden = true)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
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

    /**
     *
     * @param email must be encoded in Base64 format
     * @return
     */
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_FIND_ORCID_BY_EMAIL)
    public Response findOrcidByEmail(@PathParam("email") String email) {
        return serviceDelegator.findOrcidByEmail(new String(Base64.decodeBase64(email.getBytes())));
    }
}
