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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class OauthAuthorizationPageTest {

    private static final String STATE_PARAM = "MyStateParam";
    private static final String SCOPES = "/activities/update /activities/read-limited";
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");
    private static final Pattern STATE_PARAM_PATTERN = Pattern.compile("state=(.+)");
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;
    
    private WebDriver webDriver;

    @Test
    public void testAuthorizeAndRegister() throws JSONException, InterruptedException, URISyntaxException {
        webDriver = new FirefoxDriver();        
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s&state=%s", webBaseUrl, client1ClientId, SCOPES, redirectUri, STATE_PARAM));
        By registerForm = By.id("register");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(registerForm));
        
        String time = String.valueOf(System.currentTimeMillis());
        
        webDriver.findElement(By.id("register-form-given-names")).sendKeys(time);
        webDriver.findElement(By.id("register-form-email")).sendKeys(time + "@" + time + ".com");
        webDriver.findElement(By.id("register-form-confirm-email")).sendKeys(time + "@" + time + ".com");
        webDriver.findElement(By.id("register-form-password")).sendKeys(time + "a");
        webDriver.findElement(By.id("register-form-confirm-password")).sendKeys(time  + "a");
        webDriver.findElement(By.id("register-form-authorize")).click();
        
        //TODO: Check this form!
        webDriver.findElement(By.id("register-form-term-box"));
        
        
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
        matcher = STATE_PARAM_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String stateParam = matcher.group(1);
        assertEquals(STATE_PARAM, stateParam);
        
        ClientResponse token = getClientResponse(client1ClientId, client1ClientSecret, SCOPES, STATE_PARAM, authorizationCode);
        assertEquals(200, token.getStatus());
        String body = token.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        
        String scopes = (String) jsonObject.get("scope");
        assertEquals(SCOPES, scopes);
        webDriver.quit();
    }
    
    
    
    public ClientResponse getClientResponse(String clientId, String clientSecret, String scopes, String redirectUri, String authorizationCode) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        if(scopes != null)
            params.add("scope", scopes);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        return t2OAuthClient.obtainOauth2TokenPost("client_credentials", params);
    }
}
