package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class RefreshTokenTest extends BlackBoxBase {

    @Resource
    private OauthHelper oauthHelper;

    @Test
    public void generateRefreshTokenInMemberAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.ACTIVITIES_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT2Client().obtainOauth2RefreshTokenPost("refresh_token", accessToken, params);
        assertNotNull(tokenResponse);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String refreshedAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(refreshedAccessToken);
        String refreshedRefreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshedRefreshToken);

        assertFalse(refreshedAccessToken.equals(accessToken));
        assertFalse(refreshedRefreshToken.equals(refreshToken));
    }

    @Test
    public void generateRefreshTokenInMemberWithBasicAuthMemberAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.ACTIVITIES_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT2Client().obtainOauth2RefreshTokenPostWithBasicAuth("refresh_token", clientId, clientSecret, params);
        assertNotNull(tokenResponse);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String refreshedAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(refreshedAccessToken);
        String refreshedRefreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshedRefreshToken);

        assertFalse(refreshedAccessToken.equals(accessToken));
        assertFalse(refreshedRefreshToken.equals(refreshToken));
    }

    @Test
    public void generateRefreshTokenInMemberWithoutOriginalTokenAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.ACTIVITIES_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT2Client().obtainOauth2RefreshTokenPost("refresh_token", null, params);
        assertNotNull(tokenResponse);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String refreshedAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(refreshedAccessToken);
        String refreshedRefreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshedRefreshToken);

        assertFalse(refreshedAccessToken.equals(accessToken));
        assertFalse(refreshedRefreshToken.equals(refreshToken));
    }

    @Test
    public void generateRefreshTokenWithWrongClientIdInMemberAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.ACTIVITIES_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", getClient2ClientId());
        params.add("client_secret", getClient2ClientSecret());
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT2Client().obtainOauth2RefreshTokenPost("refresh_token", accessToken, params);
        assertNotNull(tokenResponse);
        assertEquals(400, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String error = (String) jsonObject.get("error");
        assertEquals("invalid_request", error);
        String errorDescription = (String) jsonObject.get("error_description");
        assertEquals("This token does not belong to the given client", errorDescription);
    }

    @Test
    public void generateRefreshTokenWithBadClientSecretInMemberAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.ACTIVITIES_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", "bad-secret");
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT2Client().obtainOauth2RefreshTokenPost("refresh_token", accessToken, params);
        assertNotNull(tokenResponse);
        assertEquals(401, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String error = (String) jsonObject.get("error");
        assertEquals("invalid_client", error);
        String errorDescription = (String) jsonObject.get("error_description");
        assertEquals("Bad client credentials", errorDescription);
    }

    @Test
    public void generateRefreshTokenWithNoClientCredentialsInMemberAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.ACTIVITIES_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT2Client().obtainOauth2RefreshTokenPost("refresh_token", null, params);
        assertNotNull(tokenResponse);
        assertEquals(401, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String error = (String) jsonObject.get("error");
        assertEquals("unauthorized", error);
        String errorDescription = (String) jsonObject.get("error_description");
        assertEquals("An Authentication object was not found in the SecurityContext", errorDescription);
    }

    @Test
    public void generateRefreshTokenInPublicAPITest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String redirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String userPassword = getUser1Password();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String authorizationCode = oauthHelper.getAuthorizationCode(clientId, ScopePathType.PERSON_UPDATE.value(), userId, userPassword, true);
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(clientId, clientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        String refreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshToken);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        tokenResponse = oauthHelper.getOauthT1Client().obtainOauth2RefreshTokenPost("refresh_token", accessToken, params);
        assertNotNull(tokenResponse);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String refreshedAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(refreshedAccessToken);
        String refreshedRefreshToken = (String) jsonObject.get("refresh_token");
        assertNotNull(refreshedRefreshToken);

        assertFalse(refreshedAccessToken.equals(accessToken));
        assertFalse(refreshedRefreshToken.equals(refreshToken));
    }
}
