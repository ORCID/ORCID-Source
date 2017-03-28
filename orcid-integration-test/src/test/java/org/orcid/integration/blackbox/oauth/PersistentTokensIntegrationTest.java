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
package org.orcid.integration.blackbox.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PersistentTokensIntegrationTest extends BlackBoxBaseV2Release {

    @Before
    public void before() {
        signout();
    }
    
    @Test
    public void createLongLivedTokenTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "/orcid-works/create";
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        assertNotNull(jsonObject.get("expires_in"));
        Integer expiresIn = (Integer) jsonObject.get("expires_in");
        // Lives more than 19 year
        assertTrue(expiresIn > 60 * 60 * 24 * 365 * 19);
    }

    @Test
    public void createShortLivedTokenTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser2OrcidId();
        String password = getUser2Password();
        String scope = "/orcid-works/create";
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, false);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        assertNotNull(jsonObject.get("expires_in"));
        Integer expiresIn = (Integer) jsonObject.get("expires_in");
        // Lives less than an hour
        assertTrue(expiresIn <= 60 * 60);
    }

    @Test
    public void persistentTokenCheckboxNotVisibleWhenPersistentTokensIsDisabledOnClient() {
        getWebDriver().get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", getWebBaseUrl(), getClient2ClientId(),
                "/orcid-bio/read-limited", getClient2RedirectUri()));

        try {
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("enablePersistentToken")), getWebDriver());
            fail("Element enablePersistentToken should not be displayed");
        } catch (TimeoutException e) {

        }
    }
}
