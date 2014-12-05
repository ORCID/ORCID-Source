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
package org.orcid.integration.api.t2.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.test.DBUnitTest;
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
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class Orcid2StepOauthFlowTest extends DBUnitTest {

    private static final String CLIENT_DETAILS_ID = "APP-5555555555555555";
    
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);        
    }
    
    @Before
    public void before() {
        clientDetailsManager.updateLastModified(CLIENT_DETAILS_ID);
    }
    
    @AfterClass
    public static void after() throws Exception {
        removeDBUnitData(DATA_FILES);
    } 
    
    @Test
    public void testWebhook() throws InterruptedException, JSONException {        
        ClientResponse tokenResponse = getClientResponse("/webhook");
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(accessToken.length() == 0);
        
        int expiresIn = (Integer) jsonObject.get("expires_in"); 
        assertNotNull(expiresIn);        
        //It expires in 20 years less some secs
        assertTrue(expiresIn > (631138519) - 120);
        
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/webhook", scope);
        
    }
        
    @Test
    public void testReadPublic() throws InterruptedException, JSONException {
        ClientResponse tokenResponse = getClientResponse("/read-public");
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(accessToken.length() == 0);
        
        int expiresIn = (Integer) jsonObject.get("expires_in"); 
        assertNotNull(expiresIn);        
        //It expires in 20 years less some secs
        assertTrue(expiresIn > (631138519) - 120);
        
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/read-public", scope);
        
    }
    
    @Test
    public void testOrcidProfileCreate() throws InterruptedException, JSONException {        
        ClientResponse tokenResponse = getClientResponse("/orcid-profile/create");
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(accessToken.length() == 0);
        
        int expiresIn = (Integer) jsonObject.get("expires_in"); 
        assertNotNull(expiresIn);        
        //It expires in 20 years less some secs
        assertTrue(expiresIn > (631138519) - 120);
        
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-profile/create", scope);
        
    }
    
    @Test
    public void testInvalidScopesAreIgnored() throws InterruptedException, JSONException {
        ClientResponse tokenResponse = getClientResponse("/orcid-profile/create /orcid-profile/read-limited");
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-profile/create", scope);
        
        tokenResponse = getClientResponse("/orcid-profile/create /orcid-works/read-limited");
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-profile/create", scope);
        
        tokenResponse = getClientResponse("/orcid-profile/create /funding/create");
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-profile/create", scope);
        
        tokenResponse = getClientResponse("/orcid-profile/create /affiliations/update");
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-profile/create", scope);
    }
    
    private ClientResponse getClientResponse(String scope) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", CLIENT_DETAILS_ID);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "client_credentials");
        params.add("scope", scope);
        return oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
    }
    
}
