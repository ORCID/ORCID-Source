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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.rc1.BlackBoxBase;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class OauthExchangeCodeTest extends BlackBoxBase {

    @Resource(name = "pubClient")
    private T2OAuthAPIService<ClientResponse> clientPub;

    @Resource(name = "apiClient")
    private T2OAuthAPIService<ClientResponse> clientApi;

    @Resource(name = "rootClient")
    private T2OAuthAPIService<ClientResponse> clientRoot;

    private WebDriver webDriver;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void pubTokenTest() throws Exception {
        String code = getAuthorizationCode();
        ClientResponse tokenResponse = clientPub.obtainOauth2TokenPost("client_credentials", getParamMap(code));

        assertEquals(200, tokenResponse.getStatus());
        JSONObject jsonObject = new JSONObject(tokenResponse.getEntity(String.class));
        String token = (String) jsonObject.get("access_token");
        assertFalse(PojoUtil.isEmpty(token));
    }

    @Test
    public void apiTokenTest() throws Exception {
        String code = getAuthorizationCode();
        ClientResponse tokenResponse = clientApi.obtainOauth2TokenPost("client_credentials", getParamMap(code));

        assertEquals(200, tokenResponse.getStatus());
        JSONObject jsonObject = new JSONObject(tokenResponse.getEntity(String.class));
        String token = (String) jsonObject.get("access_token");
        assertFalse(PojoUtil.isEmpty(token));
    }

    @Test
    public void rootTokenTest() throws Exception {
        String code = getAuthorizationCode();
        ClientResponse tokenResponse = clientRoot.obtainOauth2TokenPost("client_credentials", getParamMap(code));

        assertEquals(200, tokenResponse.getStatus());
        JSONObject jsonObject = new JSONObject(tokenResponse.getEntity(String.class));
        String token = (String) jsonObject.get("access_token");
        assertFalse(PojoUtil.isEmpty(token));
    }

    private String getAuthorizationCode() {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, "/activities/update",
                client1RedirectUri));
        List<WebElement> signInEl = webDriver.findElements(By.id("in-register-switch-form"));
        if (signInEl.size() != 0) {
            signInEl.get(0).click();
            webDriver.findElement(By.id("userId")).sendKeys(user1UserName);
            webDriver.findElement(By.id("password")).sendKeys(user1Password);
            webDriver.findElement(By.id("authorize-button")).click();
        } else {
            webDriver.findElement(By.id("authorize-button")).click();
        }

        (new WebDriverWait(webDriver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("OAuth 2.0 Playground") || d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = Pattern.compile("code=(.+)").matcher(currentUrl);
        assertTrue(matcher.find());
        return matcher.group(1);
    }

    public MultivaluedMap<String, String> getParamMap(String authorizationCode) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "authorization_code");
        params.add("scope", "/activities/update");
        params.add("redirect_uri", client1RedirectUri);
        params.add("code", authorizationCode);
        return params;
    }
}
