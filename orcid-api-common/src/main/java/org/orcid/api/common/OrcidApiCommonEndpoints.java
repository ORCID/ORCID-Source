package org.orcid.api.common;

import static org.orcid.core.api.OrcidApiConstants.OAUTH_TOKEN;
import static org.orcid.core.constants.OrcidOauth2Constants.*;

import jakarta.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.togglz.Features;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
@Path(OAUTH_TOKEN)
public class OrcidApiCommonEndpoints {
    @Context
    private UriInfo uriInfo;

    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) String authorization,
        @FormParam(OrcidOauth2Constants.CLIENT_ID_PARAM) String clientId, @FormParam(OrcidOauth2Constants.CLIENT_SECRET_PARAM) String clientSecret,
        @FormParam(OrcidOauth2Constants.SCOPE_PARAM) String scopeList, @FormParam(OrcidOauth2Constants.GRANT_TYPE) String grantType,
        @FormParam("code") String code, @FormParam(OrcidOauth2Constants.STATE_PARAM) String state, @FormParam(OrcidOauth2Constants.REDIRECT_URI_PARAM) String redirectUri,
        @FormParam(OrcidOauth2Constants.REFRESH_TOKEN) String refreshToken, @FormParam(OrcidOauth2Constants.REVOKE_OLD) String revokeOld,
        @FormParam(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN) String subjectToken, @FormParam(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE)
        String subjectTokenType, @FormParam(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE) String requestedTokenType)
        throws IOException, URISyntaxException, InterruptedException {

        // Token delegation is not implemented in the authorization server
        if(grantType == null) {
            throw new IllegalArgumentException("grant_type is missing");
        } else if(clientId == null || clientId.length() > 50 || StringUtils.isBlank(clientId)) {
            throw new IllegalArgumentException("client_id is missing or invalid");
        } else if(clientSecret == null || clientSecret.length() > 100 || StringUtils.isBlank(clientSecret)) {
            throw new IllegalArgumentException("client_secret is missing or invalid");
        }

        Response response = null;
        if(StringUtils.isNotBlank(authorization)) {
            switch (grantType) {
                case OrcidOauth2Constants.GRANT_TYPE_AUTHORIZATION_CODE:
                    response = authorizationServerUtil.forwardAuthorizationCodeExchangeRequest(authorization, redirectUri, code);
                    break;
                case OrcidOauth2Constants.GRANT_TYPE_REFRESH_TOKEN:
                    response = authorizationServerUtil.forwardRefreshTokenRequest(authorization, refreshToken, scopeList);
                    break;
                case OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS:
                    response = authorizationServerUtil.forwardClientCredentialsRequest(authorization, scopeList);
                    break;
                case IETF_EXCHANGE_GRANT_TYPE:
                    response = authorizationServerUtil.forwardTokenExchangeRequest(authorization, subjectToken, subjectTokenType, requestedTokenType, scopeList);
                    break;
                default:
                    response = authorizationServerUtil.forwardOtherTokenExchangeRequest(authorization, grantType, code, scopeList);
                    break;
            }
        } else {
            switch (grantType) {
                case OrcidOauth2Constants.GRANT_TYPE_AUTHORIZATION_CODE:
                    response = authorizationServerUtil.forwardAuthorizationCodeExchangeRequest(clientId, clientSecret, redirectUri, code);
                    break;
                case OrcidOauth2Constants.GRANT_TYPE_REFRESH_TOKEN:
                    response = authorizationServerUtil.forwardRefreshTokenRequest(clientId, clientSecret, refreshToken, scopeList);
                    break;
                case OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS:
                    response = authorizationServerUtil.forwardClientCredentialsRequest(clientId, clientSecret, scopeList);
                    break;
                case IETF_EXCHANGE_GRANT_TYPE:
                    response = authorizationServerUtil.forwardTokenExchangeRequest(clientId, clientSecret, subjectToken, subjectTokenType, requestedTokenType, scopeList);
                    break;
                default:
                    response = authorizationServerUtil.forwardOtherTokenExchangeRequest(clientId, clientSecret, grantType, code, scopeList);
                    break;
            }
        }
        Object entity = response.getEntity();
        int statusCode = response.getStatus();
        return Response.status(statusCode).entity(entity).header(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name(),"ON").build();
    }
}
