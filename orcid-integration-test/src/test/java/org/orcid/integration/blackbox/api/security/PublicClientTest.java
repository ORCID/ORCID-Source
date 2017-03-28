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
package org.orcid.integration.blackbox.api.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.orcid.api.common.OauthAuthorizationPageHelper;
import org.orcid.integration.blackbox.api.v12.T1OAuthOrcidApiClientImpl;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PublicClientTest extends BlackBoxBaseV2Release {

    @Resource(name = "t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient;
    
    @Resource(name = "t1OAuthClient")
    protected T1OAuthOrcidApiClientImpl t1OAuthClient;
    
    
    @Test
    public void testAuthenticateIsTheOnlyScopeThatWorksForPublicClient() throws JSONException, InterruptedException {
        String clientId = getPublicClientId();
        String clientRedirectUri = getPublicClientRedirectUri();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        WebDriver webDriver = getWebDriver();
        for (ScopePathType scope : ScopePathType.values()) {
            if (ScopePathType.AUTHENTICATE.equals(scope)) {
                String authCode = getAuthorizationCode(clientId, clientRedirectUri, scope.value(), userId, password, true);
                assertFalse(PojoUtil.isEmpty(authCode));
            } else {
                String authorizationPageUrl = String.format(OauthAuthorizationPageHelper.authorizationScreenUrl, getWebBaseUrl(), clientId, scope.value(), clientRedirectUri);
                webDriver.get(authorizationPageUrl);
                String authCodeUrl = webDriver.getCurrentUrl();
                assertFalse(PojoUtil.isEmpty(authCodeUrl));
                assertTrue(authCodeUrl.contains("error=invalid_scope"));
            }
        }
    }
    
    @Test
    public void testAuthenticateOnPublicAPI() throws JSONException, InterruptedException {
        String clientId = getPublicClientId();
        String clientRedirectUri = getPublicClientRedirectUri();
        String clientSecret = getPublicClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, "/authenticate", userId, password,
                true);
        
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("scope", "/authenticate");
        params.add("redirect_uri", clientRedirectUri);
        params.add("code", authorizationCode);
        ClientResponse response = t1OAuthClient.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, response.getStatus());
        String body = response.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        assertNotNull(jsonObject);assertNotNull(jsonObject.get("access_token"));
        assertEquals(userId, jsonObject.get("orcid"));        
    }
    
    @Test
    public void testAuthenticateOnMembersAPI() throws JSONException, InterruptedException {
        String clientId = getPublicClientId();
        String clientRedirectUri = getPublicClientRedirectUri();
        String clientSecret = getPublicClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, "/authenticate", userId, password,
                true);
        
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("scope", "/authenticate");
        params.add("redirect_uri", clientRedirectUri);
        params.add("code", authorizationCode);
        ClientResponse response = t2OAuthClient.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, response.getStatus());
        String body = response.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        assertNotNull(jsonObject);assertNotNull(jsonObject.get("access_token"));
        assertEquals(userId, jsonObject.get("orcid"));        
    }
}
