package org.orcid.core.oauth.authorizationServer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class AuthorizationServerUtil {
    private static final Logger logger = Logger.getLogger(AuthorizationServerUtil.class);

    public static final Set<String> AUTH_SERVER_ALLOWED_GRANT_TYPES = Set.of("authorization_code", "refresh_token", "client_credentials", "urn:ietf:params:oauth:grant-type:token-exchange");

    private final String authorizationServerTokenExchangeEndpoint;

    private final String authorizationServerIntrospectionEndpoint;

    @Resource
    private HttpRequestUtils httpRequestUtils;

    @Value("${org.orcid.authorization.server.tokenIntrospection.clientId}")
    private String tokenIntrospectionClientId;
    @Value("${org.orcid.authorization.server.tokenIntrospection.clientSecret}")
    private String tokenIntrospectionClientSecret;

    public AuthorizationServerUtil(@Value("${org.orcid.authorization.server.url}") String authorizationServerUrl) {
        this.authorizationServerTokenExchangeEndpoint = authorizationServerUrl.endsWith("/") ? authorizationServerUrl + "oauth/token" : authorizationServerUrl + "/oauth/token";
        this.authorizationServerIntrospectionEndpoint = authorizationServerUrl.endsWith("/") ? authorizationServerUrl + "oauth2/introspect" : authorizationServerUrl + "/oauth2/introspect";
    }

    public Response forwardAuthorizationCodeExchangeRequest(String clientId, String clientSecret, String redirectUri, String code) throws IOException, URISyntaxException, InterruptedException {
        if(logger.isTraceEnabled()) {
            logger.trace("Using authorization server to exchange authorization code");
        }

        Map<String, String> parameters = new HashMap<String, String>();
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_ID_PARAM, clientId, parameters);
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_SECRET_PARAM, clientSecret, parameters);
        addToMapOrThrow(OrcidOauth2Constants.CODE_PARAM, code, parameters);

        // Set the grant type
        parameters.put(OrcidOauth2Constants.GRANT_TYPE, OrcidOauth2Constants.GRANT_TYPE_AUTHORIZATION_CODE);
        // Set the redirect uri if it is not blank
        if(!StringUtils.isBlank(redirectUri)) {
            parameters.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, redirectUri);
        }

        // Post and respond
        return this.doPost(parameters);
    }

    public Response forwardRefreshTokenRequest(String clientId, String clientSecret, String refreshToken, String scope) throws IOException, URISyntaxException, InterruptedException {
        if(logger.isTraceEnabled()) {
            logger.trace("Using authorization server to refresh a token");
        }

        Map<String, String> parameters = new HashMap<String, String>();
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_ID_PARAM, clientId, parameters);
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_SECRET_PARAM, clientSecret, parameters);
        addToMapOrThrow(OrcidOauth2Constants.REFRESH_TOKEN, refreshToken, parameters);
        // Scope is not required
        if(!StringUtils.isBlank(scope)) {
            parameters.put(OrcidOauth2Constants.SCOPE_PARAM, scope);
        }

        // Set the grant type
        parameters.put(OrcidOauth2Constants.GRANT_TYPE, OrcidOauth2Constants.GRANT_TYPE_REFRESH_TOKEN);

        // Post and respond
        return this.doPost(parameters);
    }

    public Response forwardClientCredentialsRequest(String clientId, String clientSecret, String scope) throws IOException, URISyntaxException, InterruptedException {
        if(logger.isTraceEnabled()) {
            logger.trace("Using authorization server to refresh a token");
        }

        Map<String, String> parameters = new HashMap<String, String>();
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_ID_PARAM, clientId, parameters);
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_SECRET_PARAM, clientSecret, parameters);
        addToMapOrThrow(OrcidOauth2Constants.SCOPE_PARAM, scope, parameters);

        // Set the grant type
        parameters.put(OrcidOauth2Constants.GRANT_TYPE, OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS);

        // Post and respond
        return this.doPost(parameters);
    }

    public Response forwardTokenExchangeRequest(String clientId, String clientSecret, String subjectToken, String subjectTokenType, String requestedTokenType, String scope) throws IOException, URISyntaxException, InterruptedException {
        if(logger.isTraceEnabled()) {
            logger.trace("Using authorization server to exchange a token");
        }

        Map<String, String> parameters = new HashMap<String, String>();
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_ID_PARAM, clientId, parameters);
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_SECRET_PARAM, clientSecret, parameters);
        addToMapOrThrow(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, subjectToken, parameters);
        addToMapOrThrow(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, subjectTokenType, parameters);
        addToMapOrThrow(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, requestedTokenType, parameters);
        // Scope is not required
        if(!StringUtils.isBlank(scope)) {
            parameters.put(OrcidOauth2Constants.SCOPE_PARAM, scope);
        }

        // Set the grant type
        parameters.put(OrcidOauth2Constants.GRANT_TYPE, OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE);

        // Post and respond
        return this.doPost(parameters);
    }

    public JSONObject tokenIntrospection(String tokenValue) throws IOException, URISyntaxException, InterruptedException, JSONException {
        if(logger.isTraceEnabled()) {
            logger.trace("Using authorization server for token introspection");
        }
        Map<String, String> parameters = new HashMap<String, String>();
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_ID_PARAM, tokenIntrospectionClientId, parameters);
        addToMapOrThrow(OrcidOauth2Constants.CLIENT_SECRET_PARAM, tokenIntrospectionClientSecret, parameters);
        addToMapOrThrow(OrcidOauth2Constants.TOKEN, tokenValue, parameters);

        Response response = this.doPost(this.authorizationServerIntrospectionEndpoint, parameters);

        if (response != null && (response.getStatus() == 200)) {
            String responseString = (String) response.getEntity();
            return new JSONObject(responseString);
        }

        return null;
    }

    private void addToMapOrThrow(String name, String value, Map<String, String> parameters) {
        if(StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(name + " is required");
        }

        parameters.put(name, value);
    }

    private Response doPost(Map<String, String> parameters) throws IOException, URISyntaxException, InterruptedException {
        return doPost(authorizationServerTokenExchangeEndpoint, parameters);
    }

    private Response doPost(String uri, Map<String, String> parameters) throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> tokenResponse = httpRequestUtils.doPost(uri, parameters);
        int statusCode = tokenResponse.statusCode();
        String tokenResult = tokenResponse.body();

        Response.ResponseBuilder responseBuilder = Response.status(statusCode);
        responseBuilder.entity(tokenResult);
        // TODO: Remove this header when the togglz is removed
        responseBuilder.header(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name(), "ON");
        tokenResponse.headers()
                .firstValue("Content-Type")
                .ifPresent(contentType -> responseBuilder.type(MediaType.valueOf(contentType)));
        return responseBuilder.build();
    }
}
