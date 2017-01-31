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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PersistentTokensIntegrationTest extends BlackBoxBaseV2Release {

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
        String userId = getUser1OrcidId();
        String password = getUser1Password();
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
}

// TODO:
/**
 * 
 * 
 * @Test public void createAuthenticatedTokenToGeneratePersistentTokenTest()
 *       throws InterruptedException { List<String> items = new
 *       ArrayList<String>(); items.add("enablePersistentToken"); String
 *       authorizationCode =
 *       webDriverHelper.obtainAuthorizationCode("/orcid-works/create",
 *       CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, true);
 *       assertFalse(PojoUtil.isEmpty(authorizationCode));
 * 
 *       OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity =
 *       authorizationCodeDetailDao.find(authorizationCode);
 *       assertNotNull(authorizationCodeEntity);
 *       assertTrue(authorizationCodeEntity.isPersistent()); }
 * 
 * @Test public void createNonPersistentAuthenticatedTokenTest() throws
 *       InterruptedException { List<String> items = new ArrayList<String>();
 *       items.add("enablePersistentToken"); String authorizationCode =
 *       webDriverHelper.obtainAuthorizationCode("/orcid-works/create",
 *       CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, false);
 *       assertFalse(PojoUtil.isEmpty(authorizationCode));
 * 
 *       OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity =
 *       authorizationCodeDetailDao.find(authorizationCode);
 *       assertNotNull(authorizationCodeEntity);
 *       assertFalse(authorizationCodeEntity.isPersistent()); }
 * 
 * @Test public void createPersistentToken() throws InterruptedException,
 *       JSONException { Date beforeCreatingToken = twentyYearsTime();
 *       List<String> items = new ArrayList<String>();
 *       items.add("enablePersistentToken"); String authorizationCode =
 *       webDriverHelper.obtainAuthorizationCode("/orcid-works/create",
 *       CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, true);
 *       assertFalse(PojoUtil.isEmpty(authorizationCode)); String accessToken =
 *       obtainAccessToken(CLIENT_DETAILS_ID, authorizationCode, redirectUri,
 *       "/orcid-works/create"); assertFalse(PojoUtil.isEmpty(accessToken));
 *       OrcidOauth2TokenDetail tokenEntity =
 *       oauth2TokenDetailDao.findByTokenValue(accessToken);
 *       assertNotNull(tokenEntity); assertTrue(tokenEntity.isPersistent());
 *       Date tokenExpiration = tokenEntity.getTokenExpiration();
 *       assertNotNull(tokenExpiration); Thread.sleep(2000); Date
 *       afterCreatingToken = twentyYearsTime();
 * 
 *       //confirm the token expires in 20 years
 *       assertTrue(tokenExpiration.after(beforeCreatingToken));
 *       assertTrue(tokenExpiration.before(afterCreatingToken)); }
 * 
 * @Test public void createNonPersistentToken() throws InterruptedException,
 *       JSONException { Date beforeCreatingToken = oneHoursTime(); List<String>
 *       items = new ArrayList<String>(); items.add("enablePersistentToken");
 *       String authorizationCode =
 *       webDriverHelper.obtainAuthorizationCode("/orcid-works/create",
 *       CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, false);
 *       assertFalse(PojoUtil.isEmpty(authorizationCode)); String accessToken =
 *       obtainAccessToken(CLIENT_DETAILS_ID, authorizationCode, redirectUri,
 *       "/orcid-works/create"); assertFalse(PojoUtil.isEmpty(accessToken));
 *       OrcidOauth2TokenDetail tokenEntity =
 *       oauth2TokenDetailDao.findByTokenValue(accessToken);
 *       assertNotNull(tokenEntity); assertFalse(tokenEntity.isPersistent());
 *       Date tokenExpiration = tokenEntity.getTokenExpiration();
 *       assertNotNull(tokenExpiration); Thread.sleep(2000); Date
 *       afterCreatingToken = oneHoursTime();
 * 
 *       //confirm the token expires in 1 hour
 *       assertTrue(tokenExpiration.after(beforeCreatingToken));
 *       assertTrue(tokenExpiration.before(afterCreatingToken)); }
 * 
 * @Test public void
 *       persistentTokenCheckboxNotVisibleWhenPersistentTokensIsDisabledOnClient()
 *       {
 *       webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s",
 *       webBaseUrl, NO_PERSISTENT_TOKEN_CLIENT_DETAILS_ID,
 *       "/orcid-bio/read-limited", redirectUri));
 * 
 *       // Switch to the login form By scopesUl = By.id("scopes-ul"); By
 *       switchFromLinkLocator = By.id("enablePersistentToken"); (new
 *       WebDriverWait(webDriver,
 *       DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(scopesUl));
 * 
 *       try { webDriver.findElement(switchFromLinkLocator); fail("Element
 *       enablePersistentToken should not be displayed"); }
 *       catch(NoSuchElementException e) {
 * 
 *       } }
 * 
 * 
 */
