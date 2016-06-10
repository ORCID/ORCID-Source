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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.integration.api.pub.PublicV1ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.rc1.BlackBoxBaseRC1;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml", "classpath:orcid-api-client-context.xml" })
public class LockUnlockRecordTest extends BlackBoxBaseRC1 {
    @Resource(name = "t2OrcidApiClient1_2")
    private T2OrcidApiService<ClientResponse> t2Client1_2;
    @Resource
    private PublicV1ApiClientImpl publicV1ApiClient;

    @Test
    public void lockUnlockTest() throws InterruptedException {
        logUserOut();
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
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(documentReady());
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        if (webDriver.findElements(By.id("error_locked")).size() != 0) {
            return true;
        }
        return false;
    }

    public boolean checkIfLockedApi() {
        ClientResponse response = t2Client1_2.viewFullDetailsXml(getUser1OrcidId());
        assertNotNull(response);
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        if (message.getOrcidProfile() == null && message.getErrorDesc() != null) {
            assertEquals("Account locked : The given account " + getUser1OrcidId() + " is locked", message.getErrorDesc().getContent());
            return true;
        }
        return false;
    }

    public boolean checkIfLockedPub() {
        ClientResponse response = publicV1ApiClient.viewPublicProfile(getUser1OrcidId());
        assertNotNull(response);
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        if (message.getOrcidProfile() == null && message.getErrorDesc() != null) {
            assertEquals("Account locked : The given account " + getUser1OrcidId() + " is locked", message.getErrorDesc().getContent());
            return true;
        }
        return false;
    }
}
