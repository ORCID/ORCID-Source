package T2OrcidApiServiceImplBase;

import static org.orcid.api.common.T2OrcidApiService.OAUTH_TOKEN;
import static org.orcid.api.common.T2OrcidApiService.OAUTH_REVOKE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.net.URI;

import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.orcid.core.locale.LocaleManager;

@Path("/")
public class OrcidApiCommonEndpoints {

    @Context
    private UriInfo uriInfo;
    
    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    @Resource
    private LocaleManager localeManager;
    
    /**
     * 
     * @param formParams
     * @return
     */
    @POST
    @Path(OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) String authorization, @FormParam("grant_type") String grantType,
            MultivaluedMap<String, String> formParams) {
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
    }
    
    /**
     * POST an XML representation of the entire ORCID profile
     * 
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */    
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path("/v1.2" + PROFILE_POST_PATH)
    @AccessControl(requiredScope = ScopePathType.ORCID_PROFILE_CREATE)
    public Response createProfile(OrcidMessage orcidMessage) {
        return createProfile(uriInfo, orcidMessage);
    }
    
    public Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            setSponsorFromAuthentication(orcidProfile);
            orcidProfile = orcidProfileManager.createOrcidProfileAndNotify(orcidProfile);
            return getCreatedResponse(uriInfo, PROFILE_GET_PATH, orcidProfile);
        } catch (DataAccessException e) {
            if (e.getCause() != null && ConstraintViolationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_email_exists.exception"));
            }
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_createorcid.exception"), e);
        }
    }
    
    public void setSponsorFromAuthentication(OrcidProfile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (profile.getOrcidHistory() == null) {
            OrcidHistory orcidHistory = new OrcidHistory();
            orcidHistory.setCreationMethod(CreationMethod.API);
            profile.setOrcidHistory(orcidHistory);
        }
        profile.getOrcidHistory().setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            Source sponsor = new Source();
            String sponsorId = authorizationRequest.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(sponsorId);
            if (clientDetails != null) {
                sponsor.setSourceName(new SourceName(clientDetails.getClientName()));
                sponsor.setSourceClientId(new SourceClientId(sponsorId));                
            }
            profile.getOrcidHistory().setSource(sponsor);
        }
    }
    
    private Response getCreatedResponse(UriInfo uriInfo, String requested, OrcidProfile profile) {
        if (profile != null && profile.getOrcidIdentifier() != null) {
            URI uri = uriInfo.getBaseUriBuilder().path("/").path(requested).build(profile.getOrcidIdentifier().getPath());
            return Response.created(uri).build();            
        } else {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_findorcid.exception"));
        }
    }
    
}
