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
package org.orcid.internal.server;

import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_PERSON_READ;
import static org.orcid.core.api.OrcidApiConstants.MEMBER_INFO;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;

import java.util.HashSet;
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
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.web.bind.annotation.RequestParam;

import com.orcid.api.common.server.delegator.OrcidClientCredentialEndPointDelegator;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public abstract class InternalApiServiceImplBase {
    @Context
    private UriInfo uriInfo;

    @Value("${org.orcid.core.baseUri}")
    protected String baseUri;
    
    @Value("${org.orcid.core.internalApiBaseUri}")
    protected String internalApiBaseUri;

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
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(clientId, clientSecret, refreshToken, grantType, code, scopes, state, redirectUri, resourceId);
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
}
