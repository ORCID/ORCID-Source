package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.oauth.OAuthErrorUtils;
import org.orcid.core.togglz.Features;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.orcid.core.constants.OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE;


@Controller("oauthGenericCallsController")
public class OauthGenericCallsController {
    private static final Logger logger = Logger.getLogger(OauthGenericCallsController.class);

    @Context
    private UriInfo uriInfo;

    @Resource
    private AuthorizationServerUtil authorizationServerUtil;
    
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> obtainOauth2TokenPost(HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        String grantType = request.getParameter("grant_type");
        if(grantType == null) {
            OAuthError error = new OAuthError();
            error.setErrorDescription("grant_type is missing");
            error.setError(OAuthError.UNSUPPORTED_GRANT_TYPE);
            error.setResponseStatus(Response.Status.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            Response response = null;
            if(StringUtils.isNotBlank(request.getHeader("Authorization"))) {
                response = handleBasicAuthentication(grantType, request);
            } else {
                response = handlePlainClientCredentials(grantType, request);
            }

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name(),
                    "ON");
            return ResponseEntity.status(response.getStatus()).headers(responseHeaders).body(response.getEntity());
        } catch(Exception e) {
            OAuthError error = OAuthErrorUtils.getOAuthError(e);
            Map<String, String[]> params = request.getParameterMap();
            if(params != null && !params.isEmpty()) {
                String paramList = params.entrySet().stream()
                        .map(entry -> {
                            String paramValues = (entry.getValue() == null || entry.getValue().length == 0) ? "-NOTHING-" : String.join(",", entry.getValue());
                            return entry.getKey() + "=" + paramValues;
                        })
                        .collect(Collectors.joining(", "));
                logger.error("Exception sending request to authorization server: " + error.getErrorDescription() + " - Param list: " + paramList, e);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    private Response handlePlainClientCredentials(String grantType, HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        String redirectUri = request.getParameter("redirect_uri");
        String code = request.getParameter("code");
        String scopeList = request.getParameter("scope");
        String refreshToken = request.getParameter("refresh_token");
        String subjectToken = request.getParameter("subject_token");
        String subjectTokenType = request.getParameter("subject_token_type");
        String requestedTokenType = request.getParameter("requested_token_type");

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
        return response;
    }

    private Response handleBasicAuthentication(String grantType, HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        String authorization = request.getHeader("Authorization");
        String redirectUri = request.getParameter("redirect_uri");
        String code = request.getParameter("code");
        String scopeList = request.getParameter("scope");
        String refreshToken = request.getParameter("refresh_token");
        String subjectToken = request.getParameter("subject_token");
        String subjectTokenType = request.getParameter("subject_token_type");
        String requestedTokenType = request.getParameter("requested_token_type");

        Response response = null;

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
        return response;
    }

}
