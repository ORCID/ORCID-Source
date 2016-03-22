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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Emails;
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
public class LockedClientsTest extends BlackBoxBaseRC2 {    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Test
    public void testMember() throws InterruptedException, JSONException {
        // unlock to start
        adminUnlockAccount(this.getAdminUserName(), this.getAdminPassword(), this.getLockedMemberOrcid());
        // The member must be unlocked to begin the test
        String accessToken = getAccessToken(ScopePathType.READ_LIMITED.value(), getLockedMemberClient1ClientId(), getLockedMemberClient1ClientSecret(), getLockedMemberClient1RedirectUri());
        ClientResponse getAllResponse = memberV2ApiClient.getEmails(this.getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        Emails emails = getAllResponse.getEntity(Emails.class);
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());

        // Lock and try to get authorization code
        adminLockAccount(this.getAdminUserName(), this.getAdminPassword(), this.getLockedMemberOrcid());
        lookForErrorsOnAuthorizationCodePage(this.getLockedMemberClient1ClientId(), ScopePathType.READ_LIMITED.value(), this.getLockedMemberClient1RedirectUri());

        // Try to use access token while the client is locked
        getAllResponse = memberV2ApiClient.getEmails(this.getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);

        // unlock to finish
        adminUnlockAccount(this.getAdminUserName(), this.getAdminPassword(), this.getLockedMemberOrcid());
    }

    private void lookForErrorsOnAuthorizationCodePage(String clientId, String scopes, String redirectUri) {
        webDriver = new FirefoxDriver();
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", getWebBaseUrl(), clientId, scopes, redirectUri));
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.urlContains("error"));
        String currentUrl = webDriver.getCurrentUrl();
        if (currentUrl.contains("error=client_locked")) {
            return;
        }
        fail();
    }
}