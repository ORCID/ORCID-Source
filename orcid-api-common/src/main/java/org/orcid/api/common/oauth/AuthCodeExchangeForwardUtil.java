package org.orcid.api.common.oauth;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Value;
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
public class AuthCodeExchangeForwardUtil {
    private static final Logger logger = Logger.getLogger(AuthCodeExchangeForwardUtil.class);

    public static final Set<String> AUTH_SERVER_ALLOWED_GRANT_TYPES = Set.of("authorization_code", "client_credentials");

    private final String authorizationServerTokenExchangeEndpoint;

    @Resource
    private HttpRequestUtils httpRequestUtils;

    public AuthCodeExchangeForwardUtil(@Value("${org.orcid.authorization.server.url}") String authorizationServerUrl) {
        this.authorizationServerTokenExchangeEndpoint = authorizationServerUrl.endsWith("/") ? authorizationServerUrl + "oauth/token" : authorizationServerUrl + "/oauth/token";
    }

    public Response forwardAuthorizationCodeExchangeRequest(String clientId, String clientSecret, String redirectUri, String grantType, String code, String scope) throws IOException, URISyntaxException, InterruptedException {
        if(logger.isTraceEnabled()) {
            logger.trace("Using authorization server to exchange authorization code: '" + code + "'");
        }
        if(StringUtils.isBlank(clientId)) {
            throw new IllegalArgumentException(OrcidOauth2Constants.CLIENT_ID_PARAM + " is required");
        }
        if(StringUtils.isBlank(clientSecret)) {
            throw new IllegalArgumentException("client_secret is required");
        }

        if(StringUtils.isBlank(redirectUri)) {
            throw new IllegalArgumentException("redirect_uri is required");
        }

        if(StringUtils.isBlank(grantType)) {
            throw new IllegalArgumentException("grant_type is required");
        } else if(!AUTH_SERVER_ALLOWED_GRANT_TYPES.contains(grantType)) {
            throw new IllegalArgumentException("Unsupported grant_type " + grantType);
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("client_secret", clientSecret);
        parameters.put("redirect_uri", redirectUri);
        parameters.put("grant_type", grantType);
        switch(grantType) {
            case "authorization_code":
                if(StringUtils.isBlank(code)) {
                    throw new IllegalArgumentException("code is required");
                }
                parameters.put("code", code);
                break;
            case "client_credentials":
                if(StringUtils.isBlank(scope)) {
                    throw new IllegalArgumentException("scope is required");
                }
                parameters.put("scope", scope);
                break;
        }

        HttpResponse<String> tokenResponse = httpRequestUtils.doPost(authorizationServerTokenExchangeEndpoint, parameters);
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
