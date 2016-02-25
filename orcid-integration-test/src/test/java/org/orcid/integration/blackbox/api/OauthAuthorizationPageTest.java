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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.rc1.BlackBoxBase;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.message.OrcidMessage;
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
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class OauthAuthorizationPageTest extends BlackBoxBase {

    private static final String STATE_PARAM = "MyStateParam";
    private static final String SCOPES = "/activities/update /read-limited";
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");
    private static final Pattern STATE_PARAM_PATTERN = Pattern.compile("state=(.+)");

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    private WebDriver webDriver;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        revokeApplicationsAccess(client1ClientId);
    }

    @After
    public void after() {
        webDriver.close();
        webDriver.quit();
        revokeApplicationsAccess(client1ClientId);
    }    
    
    @Test
    public void stateParamIsPersistentAndReturnedOnLoginTest() throws JSONException, InterruptedException, URISyntaxException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s&state=%s", webBaseUrl, client1ClientId, SCOPES,
                client1RedirectUri, STATE_PARAM));
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(user1UserName);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(user1Password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = STATE_PARAM_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String stateParam = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(stateParam));
        assertEquals(STATE_PARAM, stateParam);
    }

    @Test
    public void stateParamIsPersistentAndReurnedWhenAlreadyLoggedInTest() throws JSONException, InterruptedException, URISyntaxException {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get(webBaseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(webBaseUrl + "/my-orcid");
        // Sign in
        SigninTest.signIn(webDriver, user1UserName, user1Password);
        // Go to the authroization page
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s&state=%s", webBaseUrl, client1ClientId, SCOPES,
                client1RedirectUri, STATE_PARAM));
        By userIdElementLocator = By.id("authorize");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement authorizeButton = webDriver.findElement(By.id("authorize"));
        authorizeButton.click();
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = STATE_PARAM_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String stateParam = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(stateParam));
        assertEquals(STATE_PARAM, stateParam);
        webDriver.close();
    }

    @Test
    public void invalidRedirectUriAllowsLoginThenShowErrorTest() throws InterruptedException {
        String invalidRedirectUri = "http://www.orcid.org/worng/redirect/uri";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s&state=%s", webBaseUrl, client1ClientId, SCOPES,
                invalidRedirectUri, STATE_PARAM));
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(user1UserName);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(user1Password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getCurrentUrl().contains("/oauth/error/redirect-uri-mismatch");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue("URL is:" + currentUrl, currentUrl.contains("/oauth/error/redirect-uri-mismatch"));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("client_id=" + client1ClientId));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("response_type=code"));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("redirect_uri=" + invalidRedirectUri));
        assertTrue("URL is:" + currentUrl, currentUrl.contains("scope="));
    }

    @Test
    public void useAuthorizationCodeWithInalidScopesTest() throws InterruptedException, JSONException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.ORCID_WORKS_CREATE.getContent(), client1RedirectUri));
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(user1UserName);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(user1Password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, ScopePathType.ORCID_WORKS_UPDATE.getContent(), client1RedirectUri,
                authorizationCode);

        assertEquals(401, tokenResponse.getStatus());
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("OAuth2 problem : Invalid scopes: /orcid-works/update available scopes for this code are: [/orcid-works/create]", result.getErrorDesc().getContent());
    }

    @Test
    public void useAuthorizationCodeWithoutScopesTest() throws InterruptedException, JSONException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.ORCID_WORKS_CREATE.getContent(), client1RedirectUri));
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(user1UserName);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(user1Password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, null, client1RedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }

    @Test
    public void skipAuthorizationScreenIfTokenAlreadyExists() throws InterruptedException, JSONException {
        // First get the authorization code
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.ORCID_BIO_UPDATE.getContent(), client1RedirectUri));
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(user1UserName);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(user1Password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, ScopePathType.ORCID_BIO_UPDATE.getContent(), client1RedirectUri,
                authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));

        // Then, ask again for the same permissions. Note that the user is
        // already logged in
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.ORCID_BIO_UPDATE.getContent(), client1RedirectUri));

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        currentUrl = webDriver.getCurrentUrl();
        matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, ScopePathType.ORCID_BIO_UPDATE.getContent(), client1RedirectUri, authorizationCode);
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
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.FUNDING_CREATE.getContent(), client1RedirectUri));
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(user1UserName);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(user1Password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        ClientResponse tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, ScopePathType.FUNDING_CREATE.getContent(), client1RedirectUri,
                authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));

        // Then, ask again for permissions over other scopes. Note that the user
        // is already logged in
        String url = String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.AFFILIATIONS_CREATE.getContent(), client1RedirectUri);
        webDriver.get(url);

        By authorizeElementLocator = By.id("authorize");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(authorizeElementLocator));
        WebElement authorizeButton = webDriver.findElement(authorizeElementLocator);
        authorizeButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getCurrentUrl().contains("code=");
            }
        });

        currentUrl = webDriver.getCurrentUrl();
        matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, ScopePathType.AFFILIATIONS_CREATE.getContent(), client1RedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        body = tokenResponse.getEntity(String.class);
        jsonObject = new JSONObject(body);
        String otherAccessToken = (String) jsonObject.get("access_token");
        assertNotNull(otherAccessToken);
        assertFalse(PojoUtil.isEmpty(otherAccessToken));

        assertFalse(otherAccessToken.equals(accessToken));

        // Repeat the process again with other scope
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.ORCID_WORKS_UPDATE.getContent(), client1RedirectUri));

        authorizeElementLocator = By.id("authorize");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(authorizeElementLocator));
        authorizeButton = webDriver.findElement(authorizeElementLocator);
        authorizeButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getCurrentUrl().contains("code=");
            }
        });

        currentUrl = webDriver.getCurrentUrl();
        matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        authorizationCode = matcher.group(1);
        assertFalse(PojoUtil.isEmpty(authorizationCode));

        tokenResponse = getClientResponse(client1ClientId, client1ClientSecret, ScopePathType.ORCID_WORKS_UPDATE.getContent(), client1RedirectUri, authorizationCode);
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
        params.add("redirect_uri", client1RedirectUri);
        params.add("code", authorizationCode);
        return t2OAuthClient.obtainOauth2TokenPost("client_credentials", params);
    }
}
