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
package org.orcid.integration.blackbox.api;

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
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.rc1.BlackBoxBaseRC1;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class Orcid2StepOauthFlowTest extends BlackBoxBaseRC1 {

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

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
        // It expires in 20 years less some secs
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
        // It expires in 20 years less some secs
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
        // It expires in 20 years less some secs
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
        params.add("client_id", this.getClient1ClientId());
        params.add("client_secret", this.getClient1ClientSecret());
        params.add("grant_type", "client_credentials");
        params.add("scope", scope);
        return oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
    }
}
