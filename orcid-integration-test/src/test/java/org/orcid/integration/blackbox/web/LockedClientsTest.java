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
package org.orcid.integration.blackbox.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Emails;
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
public class LockedClientsTest extends BlackBoxBaseV2Release {
    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Test
    public void testMember() throws InterruptedException, JSONException {
        // The member must be unlocked to begin the test
        List<String> scopes = getScopes(ScopePathType.READ_LIMITED);
        String accessToken = getAccessToken(scopes);
        ClientResponse getAllResponse = memberV2ApiClient.getEmails(this.getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), getAllResponse.getStatus());
        Emails emails = getAllResponse.getEntity(Emails.class);
        assertNotNull(emails);
        assertNotNull(emails.getEmails());

        // Lock and try to get authorization code
        adminLockAccount(this.getAdminUserName(), this.getAdminPassword(), this.getMember1Orcid());
        lookForErrorsOnAuthorizationCodePage(this.getClient1ClientId(), ScopePathType.READ_LIMITED.value(), this.getClient1RedirectUri());

        // Try to use access token while the client is locked
        getAllResponse = memberV2ApiClient.getEmails(this.getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        assertEquals(ClientResponse.Status.UNAUTHORIZED.getStatusCode(), getAllResponse.getStatus());
        String error = getAllResponse.getEntity(String.class);
        assertNotNull(error);
        assertTrue(error.contains("invalid_token"));
        assertTrue("Incorrect error message: " + error, error.contains("The client is locked"));
        // unlock to finish
        adminUnlockAccount(this.getAdminUserName(), this.getAdminPassword(), this.getMember1Orcid());
    }

    private void lookForErrorsOnAuthorizationCodePage(String clientId, String scopes, String redirectUri) {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", getWebBaseUrl(), clientId, scopes, redirectUri));
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.urlContains("error"));
        String currentUrl = webDriver.getCurrentUrl();
        if (currentUrl.contains("error=client_locked")) {
            return;
        }
        fail();
    }
}