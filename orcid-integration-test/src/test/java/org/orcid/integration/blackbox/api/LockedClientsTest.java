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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.springframework.beans.factory.annotation.Value;
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
public class LockedClientsTest extends BlackBoxBase {

    // ADMIN USER DATA
    @Value("${org.orcid.web.adminUser.username}")
    public String adminUserName;
    @Value("${org.orcid.web.adminUser.password}")
    public String adminPassword;

    // USER DATA
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1Username;

    // MEMBER DATA
    @Value("${org.orcid.web.locked.member.id}")
    public String memberId;
    // CLIENT DATA
    @Value("${org.orcid.web.locked.member.client.id}")
    public String lockedClientId;
    @Value("${org.orcid.web.locked.member.client.secret}")
    public String lockedClientSecret;
    @Value("${org.orcid.web.locked.member.client.ruri}")
    public String lockedClientRedirectUri;
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Test
    public void testMember() throws InterruptedException, JSONException {
        // The member must be unlocked to begin the test
        String accessToken = getAccessTokenWithScopePath(ScopePathType.READ_LIMITED, lockedClientId, lockedClientSecret, lockedClientRedirectUri);
        ClientResponse getAllResponse = memberV2ApiClient.getEmails(user1OrcidId, accessToken);
        assertNotNull(getAllResponse);
        Emails emails = getAllResponse.getEntity(Emails.class);
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());

        // Lock and try to get authorization code
        adminLockAccount(adminUserName, adminPassword, memberId);
        lookForErrorsOnAuthorizationCodePage(lockedClientId, ScopePathType.READ_LIMITED.value(), lockedClientRedirectUri);

        // Try to use access token while the client is locked
        getAllResponse = memberV2ApiClient.getEmails(user1OrcidId, accessToken);
        assertNotNull(getAllResponse);

        // unlock to finish
        adminUnlockAccount(adminUserName, adminPassword, memberId);
    }

    private String getAccessTokenWithScopePath(ScopePathType scope, String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        String accessToken = super.getAccessToken(scope.value(), clientId, clientSecret, redirectUri);
        return accessToken;
    }

    private void lookForErrorsOnAuthorizationCodePage(String clientId, String scopes, String redirectUri) {
        webDriver = new FirefoxDriver();
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, clientId, scopes, redirectUri));
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.urlContains("error"));
        String currentUrl = webDriver.getCurrentUrl();
        if (currentUrl.contains("error=client_locked")) {
            return;
        }
        fail();
    }
}
