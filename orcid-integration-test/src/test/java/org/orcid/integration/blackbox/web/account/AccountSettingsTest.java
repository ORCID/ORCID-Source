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
package org.orcid.integration.blackbox.web.account;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.orcid.integration.blackbox.client.AccountSettingsPage;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Delegate;
import org.orcid.integration.blackbox.client.AccountSettingsPage.DelegateSearchResult;
import org.orcid.integration.blackbox.client.AccountSettingsPage.DelegatesSection;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Email;
import org.orcid.integration.blackbox.client.AccountSettingsPage.EmailsSection;
import org.orcid.integration.blackbox.client.OrcidUi;
import org.orcid.integration.blackbox.client.OrcidUi.AccountSwitcherSection;
import org.orcid.integration.blackbox.client.OrcidUi.AccountToSwitchTo;
import org.orcid.integration.blackbox.web.SigninTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-web-context.xml", "classpath:test-memberV2-context.xml" })
public class AccountSettingsTest extends BlackBoxBase {
    private OrcidUi orcidUi;

    @Before
    public void before() {
        orcidUi = new OrcidUi(getWebBaseUrl(), webDriver);
    }

    @After
    public void after() {
    }

    @Test
    public void emailsTest() {
        logUserOut();
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, getUser1UserName(), getUser1Password());
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);        
        EmailsSection emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);        
        assertTrue("Should be able to add email", emailsSection.canAddEmail());
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        emailsSection.addEmail(emailValue);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);        
        List<Email> emails = emailsSection.getEmails();
        Email addedEmail = emails.stream().filter(e -> e.getEmail().equals(emailValue)).findFirst().get();
        assertNotNull("The added email should be there: " + emailValue, addedEmail);
        addedEmail.delete();
        emails = emailsSection.getEmails();
        assertFalse("The added email should NOT be there: " + emailValue, emails.stream().anyMatch(e -> e.getEmail().equals(emailValue)));
    }

    @Test
    public void emailsTestAsDelegate() {
        logUserOut();
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, getUser1UserName(), getUser1Password());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        DelegatesSection delegatesSection = accountSettingsPage.getDelegatesSection();
        // If user 2 is already a delegate, then delete and add again - just to
        // make sure we can!
        Optional<Delegate> existingDelegate = delegatesSection.getDelegates().stream().filter(e -> e.getDelegateId().equals(getUser2OrcidId())).findFirst();
        if (existingDelegate.isPresent()) {
            existingDelegate.get().revoke();
        }
        delegatesSection.searchForDelegate(getUser2OrcidId());
        DelegateSearchResult delegateSearchResult = delegatesSection.getDelegateSearchResults().stream().filter(e -> e.getDelegateId().equals(getUser2OrcidId())).findFirst()
                .get();
        assertNotNull(delegateSearchResult);
        delegateSearchResult.add();
        Delegate addedDelegate = delegatesSection.getDelegates().stream().filter(e -> e.getDelegateId().equals(getUser2OrcidId())).findFirst().get();
        assertNotNull(addedDelegate);
        // Now sign in as user 2
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, getUser2UserName(), getUser2Password());
        // And switch to user 1 in delegation mode
        AccountSwitcherSection accountSwitcherSection = orcidUi.getAccountSwitcherSection();
        accountSwitcherSection.open();
        AccountToSwitchTo accountToSwitchTo = accountSwitcherSection.getAccountsToSwitchTo().stream().filter(e -> e.getAccountId().equals(getUser1OrcidId())).findFirst()
                .get();
        assertNotNull(accountToSwitchTo);
        accountToSwitchTo.switchTo();
        // Got to account settings page as delegate
        accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        // Check that add email section is not there
        EmailsSection emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        assertFalse("Should NOT be able to add email", emailsSection.canAddEmail());
    }

}
