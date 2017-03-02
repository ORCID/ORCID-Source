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

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.blackbox.api.v12.T1OAuthOrcidApiClientImpl;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class LockUnlockRecordTest extends BlackBoxBaseV2Release {
    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2; 
    @Resource(name = "t1OAuthClient_1_2")
    protected T1OAuthOrcidApiClientImpl t1OAuthClient;
    
    private String accessToken = null;

    @Test
    public void lockUnlockTest() throws InterruptedException, JSONException {
        accessToken = getClientCredentialsAccessToken(ScopePathType.READ_PUBLIC, this.getClient1ClientId(), this.getClient1ClientSecret(), APIRequestType.MEMBER);
        
        signout();
        adminUnlockAccount(getAdminUserName(), getAdminPassword(), getUser1OrcidId());
        // Init.. Should be unlocked.
        assertFalse(checkIfLockedUI());
        assertFalse(checkIfLockedApi());
        assertFalse(checkIfLockedPub());
        // Lock account
        adminLockAccount(getAdminUserName(), getAdminPassword(), getUser1OrcidId());

        // Verify
        assertTrue(checkIfLockedUI());
        assertTrue(checkIfLockedApi());
        assertTrue(checkIfLockedPub());

        // /Unlock account
        adminUnlockAccount(getAdminUserName(), getAdminPassword(), getUser1OrcidId());

        // Verify
        assertFalse(checkIfLockedUI());
        assertFalse(checkIfLockedApi());
        assertFalse(checkIfLockedPub());
    }

    private boolean checkIfLockedUI() {
        webDriver.get(this.getWebBaseUrl() + "/" + getUser1OrcidId());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        if (webDriver.findElements(By.id("error_locked")).size() != 0) {
            return true;
        }
        return false;
    }

    public boolean checkIfLockedApi() {
        ClientResponse response = t2OAuthClient_1_2.viewFullDetailsXml(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        if (message.getOrcidProfile() == null && message.getErrorDesc() != null) {
            assertEquals("Account locked : The given account " + getUser1OrcidId() + " is locked", message.getErrorDesc().getContent());
            return true;
        }
        return false;
    }

    public boolean checkIfLockedPub() {
        ClientResponse response = t1OAuthClient.viewFullDetailsXml(getUser1OrcidId());
        assertNotNull(response);
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        if (message.getOrcidProfile() == null && message.getErrorDesc() != null) {
            assertEquals("Account locked : The given account " + getUser1OrcidId() + " is locked", message.getErrorDesc().getContent());
            return true;
        }
        return false;
    }
}
