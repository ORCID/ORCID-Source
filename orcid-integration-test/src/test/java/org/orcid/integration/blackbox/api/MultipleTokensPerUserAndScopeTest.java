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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
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
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class MultipleTokensPerUserAndScopeTest {
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");    
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;
    @Resource
    private MemberV2ApiClientImpl memberV2ApiClient;

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;
    
    @Before
    public void before() {
        webDriver = new FirefoxDriver();
    }

    @After
    public void after() {
        webDriver.quit();
    }
    
    @Test
    public void skipAuthorizationScreenIfTokenAlreadyExists() throws InterruptedException, JSONException {
        // First get the authorization code
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId,
                ScopePathType.ORCID_BIO_UPDATE.getContent(), redirectUri));
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
    }
}
