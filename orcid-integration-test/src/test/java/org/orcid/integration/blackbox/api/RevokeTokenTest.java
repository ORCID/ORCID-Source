package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.blackbox.api.v2_1.release.MemberV2_1ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class RevokeTokenTest extends BlackBoxBase {

    @Resource(name = "memberV2_1ApiClient")
    private MemberV2_1ApiClientImpl memberV2_1ApiClient;

    @Resource
    private OauthHelper oauthHelper;

    @Test
    public void revokeTokenTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();
        String accessToken = getAccessToken();

        // Verify token works
        ClientResponse response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Record record = response.getEntity(Record.class);
        assertNotNull(record);
        assertNotNull(record.getOrcidIdentifier());
        assertEquals(getUser1OrcidId(), record.getOrcidIdentifier().getPath());

        // Revoke the token
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(accessToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());

        // Verify the token was revoked
        response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals(401, response.getStatus());
    }

    @Test
    public void revokeClientCredentialTokenTest() throws JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();

        String accessToken = oauthHelper.getClientCredentialsAccessToken(clientId, clientSecret, ScopePathType.READ_PUBLIC);

        // Verify token works
        ClientResponse response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Record record = response.getEntity(Record.class);
        assertNotNull(record);
        assertNotNull(record.getOrcidIdentifier());
        assertEquals(getUser1OrcidId(), record.getOrcidIdentifier().getPath());

        // Revoke the token
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(accessToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());

        // Verify the token was revoked
        response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals(401, response.getStatus());
    }

    @Test
    public void invalidCredentialsTest() throws JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();

        String accessToken = oauthHelper.getClientCredentialsAccessToken(clientId, clientSecret, ScopePathType.READ_PUBLIC);

        // Revoke it with an incorrect client secret
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(accessToken, clientId, "invalid-secret");
        assertNotNull(revokeResponse);
        assertEquals(401, revokeResponse.getStatus());

        // Revoke it with the right client secret
        revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(accessToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());
    }

    @Test
    public void invalidTokenTest() throws JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();

        // Revoke it with the right client secret
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth("invalid-token-value", clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());
    }

    @Test
    public void revokeByRefreshTokenValueTest() throws InterruptedException, JSONException {
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

        // Verify access token works
        ClientResponse response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Record record = response.getEntity(Record.class);
        assertNotNull(record);
        assertNotNull(record.getOrcidIdentifier());
        assertEquals(getUser1OrcidId(), record.getOrcidIdentifier().getPath());

        // Revoke it using refresh token value
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(refreshToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());

        // Verify access token was revoked
        response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals(401, response.getStatus());
    }

    @Test
    public void revokeTokenTwiceTest() throws JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();

        String accessToken = oauthHelper.getClientCredentialsAccessToken(clientId, clientSecret, ScopePathType.READ_PUBLIC);

        // Revoke it 
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(accessToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());
        
        // Revoke it again
        revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithBasicAuth(accessToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());
    }

    @Test
    public void revokeWithPlainCredentialsTest() throws JSONException {
        String clientId = getClient1ClientId();
        String clientSecret = getClient1ClientSecret();

        String accessToken = oauthHelper.getClientCredentialsAccessToken(clientId, clientSecret, ScopePathType.READ_PUBLIC);

        // Verify access token works
        ClientResponse response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Record record = response.getEntity(Record.class);
        assertNotNull(record);
        assertNotNull(record.getOrcidIdentifier());
        assertEquals(getUser1OrcidId(), record.getOrcidIdentifier().getPath());
        
        // Revoke it 
        ClientResponse revokeResponse = oauthHelper.getOauthWebClient().revokeTokenWithPlainCredentials(accessToken, clientId, clientSecret);
        assertNotNull(revokeResponse);
        assertEquals(200, revokeResponse.getStatus());
        
        // Verify access token was revoked
        response = memberV2_1ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals(401, response.getStatus());
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        return getNonCachedAccessTokens(getUser1OrcidId(), getUser1Password(), Arrays.asList(ScopePathType.READ_LIMITED.value()), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
}
