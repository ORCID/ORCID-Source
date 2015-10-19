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

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class Orcid3StepOauthFlowTest extends BlackBoxBase {

    private static final Pattern ERROR_PATTERN = Pattern.compile("error=(.+)&");
    
    private static final Pattern ERROR_DESCRIPTION_PATTERN = Pattern.compile("error_description=(.+)");
    
    private static final String ERROR_NAME = "invalid_scope";
    
    private WebDriver webDriver;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

    @Before
    @Transactional
    public void before() {
        webDriver = new FirefoxDriver();
    }

    @After
    public void after() {
        webDriver.quit();
    }
    
    @Test
    public void testInvalidScopeThrowException() throws JSONException, InterruptedException {
        String scopes="/orcid-profile/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        String url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/orcid-profile/create");
                
        scopes="/orcid-works/create /orcid-profile/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/orcid-profile/create");
                        
        scopes="/orcid-profile/create /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/orcid-profile/create");        
        
        scopes="/read-public";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/read-public");        
        
        scopes="/orcid-works/create /read-public";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/read-public");
        
        scopes="/read-public /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/read-public");
                
        scopes="/webhook";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/webhook");
        
        scopes="/orcid-works/create /webhook";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/webhook");
        
        scopes="/webhook /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/webhook");                
        
        scopes="/premium-notification";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/premium-notification");
        
        scopes="/orcid-works/create /premium-notification";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/premium-notification");
        
        scopes="/premium-notification /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, client1ClientId, scopes, redirectUri));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/premium-notification");
    }
    
    private void evaluateUrl(String currentUrl, String invalidScope) {
        Matcher matcher = ERROR_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String error = matcher.group(1);
        assertTrue(ERROR_NAME.equals(error));
        
        matcher = ERROR_DESCRIPTION_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String errorDescription = matcher.group(1);
        assertTrue(errorDescription.contains(invalidScope));
    }
}
