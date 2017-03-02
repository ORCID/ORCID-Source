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

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.OauthAuthorizationPageHelper;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.orcid.integration.blackbox.web.SigninTest;
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
public class OauthAuthorizationPageTest extends BlackBoxBaseRC2 {
    private static final String STATE_PARAM = "MyStateParam";
    private static final String SCOPES = "/activities/update /read-limited";    
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.*)");
    private static final Pattern STATE_PARAM_PATTERN = Pattern.compile("state=(.+)");

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;    
    
    @Before
    public void before() {
        BBBUtil.logUserOut(getWebBaseUrl(), webDriver);
    }        
    
    @Test
    public void stateParamIsPersistentAndReturnedOnLoginTest() throws JSONException, InterruptedException, URISyntaxException {
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), SCOPES, STATE_PARAM, this.getUser1UserName(), this.getUser1Password(), true, webDriver);
        Matcher matcher = STATE_PARAM_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String stateParam = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(stateParam));
        assertEquals(STATE_PARAM, stateParam);
    }

    @Test
    public void stateParamIsPersistentAndReurnedWhenAlreadyLoggedInTest() throws JSONException, InterruptedException, URISyntaxException {
        signout();
        signin();
        String currentUrl = OauthAuthorizationPageHelper.authorizeOnAlreadyLoggedInUser(webDriver, this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), "/read-limited /funding/update /orcid-works/update /orcid-bio/external-identifiers/create", STATE_PARAM);
        Matcher matcher = STATE_PARAM_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String stateParam = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(stateParam));
        assertEquals(STATE_PARAM, stateParam);
    }

    @Test
    public void invalidRedirectUriAllowsLoginThenShowErrorTest() throws InterruptedException {
        String invalidRedirectUri = "http://www.orcid.org/worng/redirect/uri";        
        
        String formattedAuthorizationScreen = String.format(OauthAuthorizationPageHelper.authorizationScreenUrl, this.getWebBaseUrl(), this.getClient1ClientId(), SCOPES, invalidRedirectUri);
        formattedAuthorizationScreen += "&state=" + STATE_PARAM;
        formattedAuthorizationScreen += "#show_login";
        webDriver.get(formattedAuthorizationScreen);
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());

        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='userId']")), webDriver);
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
                
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(this.getUser1UserName());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(this.getUser1Password());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        BBBUtil.ngAwareClick( webDriver.findElement(By.id("login-authorize-button")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getCurrentUrl().contains("/oauth/error/redirect-uri-mismatch");
            }
        }, webDriver);                
        
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue("URL is:" + currentUrl, currentUrl.contains("/oauth/error/redirect-uri-mismatch"));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("client_id=" + this.getClient1ClientId()));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("response_type=code"));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("redirect_uri=" + invalidRedirectUri));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("scope="));
    }

    @Test
    public void useAuthorizationCodeWithInalidScopesTest() throws InterruptedException, JSONException {
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.ORCID_WORKS_CREATE.value(), null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);        
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.ORCID_WORKS_UPDATE.getContent(), this.getClient1RedirectUri(),
                authorizationCode);

        assertEquals(400, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject result = new JSONObject(body);
        assertNotNull(result);
        assertEquals("invalid_scope", result.get("error"));
        assertEquals("Invalid scopes: /orcid-works/update available scopes for this code are: [/orcid-works/create]", result.get("error_description"));
    }

    @Test
    public void useAuthorizationCodeWithoutScopesTest() throws InterruptedException, JSONException {
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.ORCID_WORKS_CREATE.value(), null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), null, this.getClient1RedirectUri(), authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }

    @Test
    public void dontSkipAuthorizationScreenIfShortTokenAlreadyExists() throws InterruptedException, JSONException {
        // clean up any token lying around
        BBBUtil.revokeApplicationsAccess(webDriver);
        // get the authorization code
        signout();
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.value(), null, this.getUser1UserName(), this.getUser1Password(), false, webDriver);
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.getContent(), this.getClient1RedirectUri(),
                authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));

        // Then, ask again for the same permissions. 
        //First login
        webDriver.get(this.getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(this.getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, this.getUser1UserName(), this.getUser1Password());
        //Then ask for the same permission
        String url =String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(),
                ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.getContent(), this.getClient1RedirectUri());
        webDriver.get(url);
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[contains(text(),'has asked for the following access to your ORCID Record')]")), webDriver);
    }

    @Test
    public void skipAuthorizationScreenIfTokenLongLifeAlreadyExists() throws InterruptedException, JSONException {
        // First get the authorization code
        signout();
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.ORCID_BIO_UPDATE.value(), null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.ORCID_BIO_UPDATE.getContent(), this.getClient1RedirectUri(),
                authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));

        // Then, ask again for the same permissions. 
        //First login
        webDriver.get(this.getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(this.getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, this.getUser1UserName(), this.getUser1Password());
        //Then ask for the same permission
        String url =String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(),
                ScopePathType.ORCID_BIO_UPDATE.getContent(), this.getClient1RedirectUri());
        webDriver.get(url);
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//title[contains(text(),'ORCID Playground')]")), webDriver);
        
        currentUrl = webDriver.getCurrentUrl();
        matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.ORCID_BIO_UPDATE.getContent(), this.getClient1RedirectUri(), authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String otherAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(otherAccessToken);
        assertFalse(PojoUtil.isEmpty(otherAccessToken));
    }

    /**
     * Test that asking for different scopes generates different tokens
     * 
     * IMPORTANT NOTE: For this test to run, the user should not have tokens for
     * any of the following scopes: - FUNDING_CREATE - AFFILIATIONS_CREATE -
     * ORCID_WORKS_UPDATE
     * */
    @Test
    public void testDifferentScopesGeneratesDifferentAccessTokens() throws InterruptedException, JSONException {
        // First get the authorization code
        signout();
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.FUNDING_CREATE.value(), null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.FUNDING_CREATE.getContent(), this.getClient1RedirectUri(),
                authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
        
        
        signout();
        // Then, ask again for permissions over other scopes. 
        currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.AFFILIATIONS_CREATE.value(), null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);
        matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.AFFILIATIONS_CREATE.getContent(), this.getClient1RedirectUri(), authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String otherAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(otherAccessToken);
        assertFalse(PojoUtil.isEmpty(otherAccessToken));

        assertFalse(otherAccessToken.equals(accessToken));

        signout();
        currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), ScopePathType.ORCID_WORKS_UPDATE.value(), null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);
        matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        tokenResponse = getClientResponse(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.ORCID_WORKS_UPDATE.getContent(), this.getClient1RedirectUri(), authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String otherAccessToken2 = (String) jsonObject.get("access_token");
        assertNotNull(otherAccessToken2);
        assertFalse(PojoUtil.isEmpty(otherAccessToken2));

        assertFalse(otherAccessToken2.equals(accessToken));
        assertFalse(otherAccessToken2.equals(otherAccessToken));
    }

    public ClientResponse getClientResponse(String clientId, String clientSecret, String scopes, String client1RedirectUri, String authorizationCode) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        if (scopes != null) {
            params.add("scope", scopes);
        }
        params.add("redirect_uri", this.getClient1RedirectUri());
        params.add("code", authorizationCode);
        return t2OAuthClient.obtainOauth2TokenPost("client_credentials", params);
    }
}
