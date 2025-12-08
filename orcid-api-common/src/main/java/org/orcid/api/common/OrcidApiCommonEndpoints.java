package org.orcid.api.common;

import static org.orcid.core.api.OrcidApiConstants.OAUTH_TOKEN;
import static org.orcid.core.constants.OrcidOauth2Constants.*;

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
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.api.common.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.togglz.Features;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@Path(OAUTH_TOKEN)
public class OrcidApiCommonEndpoints {
    @Context
    private UriInfo uriInfo;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;

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
            throw new UnsupportedGrantTypeException("grant_type is missing");
        }

        if(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.isActive()) {
            Response response = null;
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
            Object entity = response.getEntity();
            int statusCode = response.getStatus();
            return Response.status(statusCode).entity(entity).header(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name(),"ON").build();
        } else {
            MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
            if (clientId != null) {
                formParams.add(OrcidOauth2Constants.CLIENT_ID_PARAM, clientId);
            }
            if (scopeList != null) {
                formParams.add(OrcidOauth2Constants.SCOPE_PARAM, scopeList);
            }
            if (grantType != null) {
                formParams.add(OrcidOauth2Constants.GRANT_TYPE, grantType);
            }

            if (code != null) {
                formParams.add("code", code);
            }

            if (state != null) {
                formParams.add(OrcidOauth2Constants.STATE_PARAM, state);
            }

            if (redirectUri != null) {
                formParams.add(OrcidOauth2Constants.REDIRECT_URI_PARAM, redirectUri);
            }

            if (redirectUri != null) {
                formParams.add(OrcidOauth2Constants.REDIRECT_URI_PARAM, redirectUri);
            }

            if (refreshToken != null) {
                formParams.add(OrcidOauth2Constants.REFRESH_TOKEN, refreshToken);
            }

            if (revokeOld != null) {
                formParams.add(OrcidOauth2Constants.REVOKE_OLD, revokeOld);
            }
            // IETF Token exchange
            if (subjectToken != null) {
                formParams.add(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, subjectToken);
            }
            if (subjectTokenType != null) {
                formParams.add(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, subjectTokenType);
            }
            if (requestedTokenType != null) {
                formParams.add(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, requestedTokenType);
            }

            return orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
        }
    }

}
