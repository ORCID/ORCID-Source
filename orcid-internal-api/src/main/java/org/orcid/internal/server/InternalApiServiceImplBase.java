package org.orcid.internal.server;

import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_ACCOUNT_RECOVERY_MATCH;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_ACCOUNT_RECOVERY_RESET_LINK;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_FIND_ORCID_BY_EMAIL;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_PERSON_READ;
import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_TOGGLZ_READ;
import static org.orcid.core.api.OrcidApiConstants.MEMBER_INFO;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.net.util.Base64;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.internal.util.AccountRecoveryMatchRequest;
import org.orcid.internal.util.AccountRecoveryResetLinkRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

//import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Component
@Path("/")
public class InternalApiServiceImplBase {
    private InternalApiServiceDelegator serviceDelegator;
    
    public void setServiceDelegator(InternalApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
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
    
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_PERSON_READ)
    public Response viewPersonDetails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPersonLastModified(orcid);         
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
        return serviceDelegator.viewMemberInfo(member);
    }
    
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_TOGGLZ_READ)
    public Response viewTogglz() {
        return serviceDelegator.viewTogglz();
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

    /**
     * Confirms that an ORCID iD and an email address belong to the same record, for the lost email
     * account recovery workflow. Answers for records the public lookups will not resolve, such as
     * locked, deactivated or unclaimed ones.
     *
     * @param request the iD and email to check as a pair
     * @return whether the pair matches, plus the record status when it does
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_ACCOUNT_RECOVERY_MATCH)
    public Response accountRecoveryMatch(AccountRecoveryMatchRequest request) {
        return serviceDelegator.accountRecoveryMatch(request);
    }

    /**
     * Mints a single use password reset link for a record whose owner has already been identified
     * by support. Equivalent to the reset link an admin generates by hand today.
     *
     * @param request the iD to mint the link for
     * @return the reset link and when it expires
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(INTERNAL_API_ACCOUNT_RECOVERY_RESET_LINK)
    public Response accountRecoveryResetLink(AccountRecoveryResetLinkRequest request) {
        return serviceDelegator.accountRecoveryResetLink(request);
    }
}
