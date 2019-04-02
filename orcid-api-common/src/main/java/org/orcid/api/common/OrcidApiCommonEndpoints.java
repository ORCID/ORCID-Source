package org.orcid.api.common;

import static org.orcid.core.api.OrcidApiConstants.OAUTH_TOKEN;

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
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.springframework.stereotype.Component;

@Path(OAUTH_TOKEN)
@Component
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
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) String authorization, @FormParam("grant_type") String grantType,
            MultivaluedMap<String, String> formParams) {
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
    }    
}
