package org.orcid.core.oauth.authorizationServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpHeaders;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLSession;
import java.net.URI;

import jakarta.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServerUtilTest {

    private static final String AUTH_URL = "https://auth.example.org";
    private static final String TOKEN_ENDPOINT = AUTH_URL + "/oauth/token";
    private static final String INTROSPECT_ENDPOINT = AUTH_URL + "/oauth2/introspect";
    private static final String REVOKE_ENDPOINT = AUTH_URL + "/oauth2/revoke";
    private static final String INTROSPECTION_CLIENT_ID = "introspection-client";
    private static final String INTROSPECTION_CLIENT_SECRET = "introspection-secret";
    private static final String BASIC_AUTH = "Basic abc123";

    @Mock
    private HttpRequestUtils httpRequestUtils;

    private AuthorizationServerUtil authorizationServerUtil;

    @Before
    public void setUp() {
        authorizationServerUtil = new AuthorizationServerUtil(AUTH_URL, INTROSPECTION_CLIENT_ID, INTROSPECTION_CLIENT_SECRET);
        ReflectionTestUtils.setField(authorizationServerUtil, "httpRequestUtils", httpRequestUtils);
    }

    @Test
    public void forwardAuthorizationCodeExchangeRequest_withClientCredentials_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        Response response = authorizationServerUtil.forwardAuthorizationCodeExchangeRequest("client-id", "client-secret", "https://app/callback", "code-1");

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getEntity());
        assertEquals("ON", response.getHeaderString(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name()));
        assertEquals("application/json", response.getMediaType().toString());

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("client-id", params.get(OrcidOauth2Constants.CLIENT_ID_PARAM));
        assertEquals("client-secret", params.get(OrcidOauth2Constants.CLIENT_SECRET_PARAM));
        assertEquals("code-1", params.get(OrcidOauth2Constants.CODE_PARAM));
        assertEquals(OrcidOauth2Constants.GRANT_TYPE_AUTHORIZATION_CODE, params.get(OrcidOauth2Constants.GRANT_TYPE));
        assertEquals("https://app/callback", params.get(OrcidOauth2Constants.REDIRECT_URI_PARAM));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardAuthorizationCodeExchangeRequest_withClientCredentials_requiresClientId() throws Exception {
        authorizationServerUtil.forwardAuthorizationCodeExchangeRequest(" ", "client-secret", "https://app/callback", "code-1");
    }

    @Test
    public void forwardAuthorizationCodeExchangeRequest_withBasicAuth_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), anyMap())).thenReturn(mockHttpResponse(201, "created", "application/json"));

        Response response = authorizationServerUtil.forwardAuthorizationCodeExchangeRequest(BASIC_AUTH, "", null);

        assertEquals(201, response.getStatus());
        assertEquals("created", response.getEntity());

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("", params.get(OrcidOauth2Constants.CODE_PARAM));
        assertEquals(OrcidOauth2Constants.GRANT_TYPE_AUTHORIZATION_CODE, params.get(OrcidOauth2Constants.GRANT_TYPE));
        assertTrue(!params.containsKey(OrcidOauth2Constants.REDIRECT_URI_PARAM));
    }

    @Test
    public void forwardRefreshTokenRequest_withClientCredentials_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardRefreshTokenRequest("client-id", "client-secret", "refresh-1", "read-limited");

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("refresh-1", params.get(OrcidOauth2Constants.REFRESH_TOKEN));
        assertEquals("read-limited", params.get(OrcidOauth2Constants.SCOPE_PARAM));
        assertEquals(OrcidOauth2Constants.GRANT_TYPE_REFRESH_TOKEN, params.get(OrcidOauth2Constants.GRANT_TYPE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardRefreshTokenRequest_withClientCredentials_requiresRefreshToken() throws Exception {
        authorizationServerUtil.forwardRefreshTokenRequest("client-id", "client-secret", null, "read-limited");
    }

    @Test
    public void forwardRefreshTokenRequest_withBasicAuth_omitsBlankScope() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardRefreshTokenRequest(BASIC_AUTH, "refresh-1", " ");

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertTrue(!params.containsKey(OrcidOauth2Constants.SCOPE_PARAM));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardRefreshTokenRequest_withBasicAuth_requiresRefreshToken() throws Exception {
        authorizationServerUtil.forwardRefreshTokenRequest(BASIC_AUTH, "", "scope");
    }

    @Test
    public void forwardClientCredentialsRequest_withClientCredentials_setsEmptyScopeWhenNull() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", null));

        authorizationServerUtil.forwardClientCredentialsRequest("client-id", "client-secret", null);

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("", params.get(OrcidOauth2Constants.SCOPE_PARAM));
        assertEquals(OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS, params.get(OrcidOauth2Constants.GRANT_TYPE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardClientCredentialsRequest_withClientCredentials_requiresClientSecret() throws Exception {
        authorizationServerUtil.forwardClientCredentialsRequest("client-id", " ", "scope");
    }

    @Test
    public void forwardClientCredentialsRequest_withBasicAuth_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardClientCredentialsRequest(BASIC_AUTH, "activities/update");

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("activities/update", params.get(OrcidOauth2Constants.SCOPE_PARAM));
        assertEquals(OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS, params.get(OrcidOauth2Constants.GRANT_TYPE));
    }

    @Test
    public void forwardTokenExchangeRequest_withClientCredentials_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardTokenExchangeRequest("client-id", "client-secret", "subject-token", "subject-token-type", "requested-token-type", "openid");

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("subject-token", params.get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN));
        assertEquals("subject-token-type", params.get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE));
        assertEquals("requested-token-type", params.get(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE));
        assertEquals("openid", params.get(OrcidOauth2Constants.SCOPE_PARAM));
        assertEquals(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE, params.get(OrcidOauth2Constants.GRANT_TYPE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardTokenExchangeRequest_withClientCredentials_requiresSubjectTokenType() throws Exception {
        authorizationServerUtil.forwardTokenExchangeRequest("client-id", "client-secret", "subject-token", "", "requested-token-type", "openid");
    }

    @Test
    public void forwardTokenExchangeRequest_withBasicAuth_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardTokenExchangeRequest(BASIC_AUTH, "subject-token", "subject-token-type", "requested-token-type", null);

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertTrue(!params.containsKey(OrcidOauth2Constants.SCOPE_PARAM));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardTokenExchangeRequest_withBasicAuth_requiresRequestedTokenType() throws Exception {
        authorizationServerUtil.forwardTokenExchangeRequest(BASIC_AUTH, "subject-token", "subject-token-type", null, "scope");
    }

    @Test
    public void forwardTokenRevocationRequest_withClientCredentials_usesRevocationEndpoint() throws Exception {
        when(httpRequestUtils.doPost(eq(REVOKE_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardTokenRevocationRequest("client-id", "client-secret", "token-1");

        verify(httpRequestUtils).doPost(eq(REVOKE_ENDPOINT), anyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardTokenRevocationRequest_withClientCredentials_requiresToken() throws Exception {
        authorizationServerUtil.forwardTokenRevocationRequest("client-id", "client-secret", " ");
    }

    @Test
    public void forwardTokenRevocationRequest_withBasicAuth_usesRevocationEndpoint() throws Exception {
        when(httpRequestUtils.doPost(eq(REVOKE_ENDPOINT), eq(BASIC_AUTH), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardTokenRevocationRequest(BASIC_AUTH, "token-1");

        verify(httpRequestUtils).doPost(eq(REVOKE_ENDPOINT), eq(BASIC_AUTH), anyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardTokenRevocationRequest_withBasicAuth_requiresToken() throws Exception {
        authorizationServerUtil.forwardTokenRevocationRequest(BASIC_AUTH, null);
    }

    @Test
    public void forwardOtherTokenExchangeRequest_withClientCredentials_allowsBlankGrantTypeAndOptionalFields() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardOtherTokenExchangeRequest("client-id", "client-secret", "", null, " ");

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("", params.get(OrcidOauth2Constants.GRANT_TYPE));
        assertTrue(!params.containsKey(OrcidOauth2Constants.CODE_PARAM));
        assertTrue(!params.containsKey(OrcidOauth2Constants.SCOPE_PARAM));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forwardOtherTokenExchangeRequest_withClientCredentials_requiresClientId() throws Exception {
        authorizationServerUtil.forwardOtherTokenExchangeRequest(null, "client-secret", "custom", "code", "scope");
    }

    @Test
    public void forwardOtherTokenExchangeRequest_withBasicAuth_buildsExpectedRequest() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        authorizationServerUtil.forwardOtherTokenExchangeRequest(BASIC_AUTH, "custom-grant", "abc", "scope");

        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), eq(BASIC_AUTH), paramsCaptor.capture());
        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("custom-grant", params.get(OrcidOauth2Constants.GRANT_TYPE));
        assertEquals("abc", params.get(OrcidOauth2Constants.CODE_PARAM));
        assertEquals("scope", params.get(OrcidOauth2Constants.SCOPE_PARAM));
    }

    @Test
    public void tokenIntrospection_returnsJsonObjectOn200() throws Exception {
        String payload = "{\"active\":true,\"scope\":\"/authenticate\",\"username\":\"0000-0001\"}";
        when(httpRequestUtils.doPost(eq(INTROSPECT_ENDPOINT), eq(expectedIntrospectionAuthHeader()), anyMap()))
                .thenReturn(mockHttpResponse(200, payload, "application/json"));

        JSONObject result = authorizationServerUtil.tokenIntrospection("token-1");

        assertTrue(result.getBoolean("active"));
        assertEquals("/authenticate", result.getString("scope"));
        assertEquals("0000-0001", result.getString("username"));
    }

    @Test
    public void tokenIntrospection_returnsNullOnNon200() throws Exception {
        when(httpRequestUtils.doPost(eq(INTROSPECT_ENDPOINT), eq(expectedIntrospectionAuthHeader()), anyMap()))
                .thenReturn(mockHttpResponse(401, "{\"error\":\"invalid_token\"}", "application/json"));

        JSONObject result = authorizationServerUtil.tokenIntrospection("token-1");

        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tokenIntrospection_requiresToken() throws Exception {
        authorizationServerUtil.tokenIntrospection("  ");
    }

    @Test(expected = JSONException.class)
    public void tokenIntrospection_throwsOnInvalidJsonResponse() throws Exception {
        when(httpRequestUtils.doPost(eq(INTROSPECT_ENDPOINT), eq(expectedIntrospectionAuthHeader()), anyMap()))
                .thenReturn(mockHttpResponse(200, "not-json", "application/json"));

        authorizationServerUtil.tokenIntrospection("token-1");
    }

    @Test
    public void doPost_retriesOnceOnTimeoutException_thenReturnsSuccessResponse() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap()))
                .thenThrow(new HttpTimeoutException("timeout"))
                .thenReturn(mockHttpResponse(200, "ok", "application/json"));

        Response response = authorizationServerUtil.forwardRefreshTokenRequest("client-id", "client-secret", "refresh-1", "scope");

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getEntity());
        verify(httpRequestUtils, times(2)).doPost(eq(TOKEN_ENDPOINT), anyMap());
    }

    @Test
    public void doPost_returns504WhenRetryAlsoTimesOutWithException() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap()))
                .thenThrow(new HttpTimeoutException("timeout-1"))
                .thenThrow(new HttpTimeoutException("timeout-2"));

        Response response = authorizationServerUtil.forwardRefreshTokenRequest("client-id", "client-secret", "refresh-1", "scope");

        assertEquals(504, response.getStatus());
        assertEquals("Gateway Timeout", response.getEntity());
        assertEquals("ON", response.getHeaderString(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name()));
    }

    @Test
    public void doPost_retriesOnceOnTimeoutStatus_thenReturnsSecondResponse() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap()))
                .thenReturn(mockHttpResponse(408, "timeout", "application/json"))
                .thenReturn(mockHttpResponse(200, "ok", "application/json"));

        Response response = authorizationServerUtil.forwardRefreshTokenRequest("client-id", "client-secret", "refresh-1", "scope");

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getEntity());
        verify(httpRequestUtils, times(2)).doPost(eq(TOKEN_ENDPOINT), anyMap());
    }

    @Test
    public void doPost_returns504WhenRetryAlsoReturnsTimeoutStatus() throws Exception {
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap()))
                .thenReturn(mockHttpResponse(504, "timeout-1", "application/json"))
                .thenReturn(mockHttpResponse(408, "timeout-2", "application/json"));

        Response response = authorizationServerUtil.forwardRefreshTokenRequest("client-id", "client-secret", "refresh-1", "scope");

        assertEquals(504, response.getStatus());
        assertEquals("Gateway Timeout", response.getEntity());
    }

    @Test
    public void constructor_handlesBaseUrlWithTrailingSlash() throws Exception {
        AuthorizationServerUtil utilWithTrailingSlash = new AuthorizationServerUtil(AUTH_URL + "/", INTROSPECTION_CLIENT_ID, INTROSPECTION_CLIENT_SECRET);
        ReflectionTestUtils.setField(utilWithTrailingSlash, "httpRequestUtils", httpRequestUtils);
        when(httpRequestUtils.doPost(eq(TOKEN_ENDPOINT), anyMap())).thenReturn(mockHttpResponse(200, "ok", "application/json"));

        utilWithTrailingSlash.forwardClientCredentialsRequest("client-id", "client-secret", "scope");

        verify(httpRequestUtils).doPost(eq(TOKEN_ENDPOINT), anyMap());
    }

    private String expectedIntrospectionAuthHeader() {
        String credentials = INTROSPECTION_CLIENT_ID + ":" + INTROSPECTION_CLIENT_SECRET;
        return "Basic " + Base64.encodeBase64URLSafeString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> mockHttpResponse(int statusCode, String body, String contentType) {
        return new StubHttpResponse(statusCode, body, httpHeaders(contentType));
    }

    private HttpHeaders httpHeaders(String contentType) {
        if (contentType == null) {
            return HttpHeaders.of(Collections.<String, List<String>>emptyMap(), (k, v) -> true);
        }
        return HttpHeaders.of(Map.of("Content-Type", List.of(contentType)), (k, v) -> true);
    }

    private static class StubHttpResponse implements HttpResponse<String> {
        private final int statusCode;
        private final String body;
        private final HttpHeaders headers;

        private StubHttpResponse(int statusCode, String body, HttpHeaders headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return headers;
        }

        @Override
        public String body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return null;
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
