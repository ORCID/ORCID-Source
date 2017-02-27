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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.rc1.BlackBoxBaseRC1;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class Orcid3StepOauthFlowTest extends BlackBoxBaseRC1 {
    private static final Pattern ERROR_PATTERN = Pattern.compile("error=(.+)&");
    private static final Pattern ERROR_DESCRIPTION_PATTERN = Pattern.compile("error_description=(.+)");
    private static final String ERROR_NAME = "invalid_scope";

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

    @Test
    public void testInvalidScopeThrowException() throws JSONException, InterruptedException {
        signout();
        String scopes = "/orcid-profile/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        String url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/orcid-profile/create");

        scopes = "/orcid-works/create /orcid-profile/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/orcid-profile/create");

        scopes = "/orcid-profile/create /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/orcid-profile/create");

        scopes = "/read-public";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/read-public");

        scopes = "/orcid-works/create /read-public";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/read-public");

        scopes = "/read-public /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/read-public");

        scopes = "/webhook";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/webhook");

        scopes = "/orcid-works/create /webhook";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/webhook");

        scopes = "/webhook /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/webhook");

        scopes = "/premium-notification";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/premium-notification");

        scopes = "/orcid-works/create /premium-notification";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/premium-notification");

        scopes = "/premium-notification /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/premium-notification");
        
        scopes = "/group-id-record/read /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/group-id-record/read");
        
        scopes = "/group-id-record/update /orcid-works/create";
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", this.getWebBaseUrl(), this.getClient1ClientId(), scopes, this.getClient1RedirectUri()));
        url = webDriver.getCurrentUrl();
        evaluateUrl(url, "/group-id-record/update");
        
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
