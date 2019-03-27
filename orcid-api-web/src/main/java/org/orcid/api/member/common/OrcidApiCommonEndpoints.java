package org.orcid.api.member.common;

import static org.orcid.api.common.T2OrcidApiService.OAUTH_TOKEN;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.net.URI;

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
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.APIRecordCreateManager;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;

@Path("/")
public class OrcidApiCommonEndpoints {

    @Context
    private UriInfo uriInfo;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private APIRecordCreateManager apiRecordCreateManager;
    
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
        // TODO: we should remove this ASAP
        OrcidProfile orcidProfile = apiRecordCreateManager.createProfile(orcidMessage);
        return getCreatedResponse(orcidProfile);
    }

    private Response getCreatedResponse(OrcidProfile profile) {
        if (profile != null && profile.getOrcidIdentifier() != null) {
            URI uri = uriInfo.getBaseUriBuilder().path("/").path(PROFILE_GET_PATH).build(profile.getOrcidIdentifier().getPath());
            return Response.created(uri).build();
        } else {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_findorcid.exception"));
        }
    }
}
