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

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.integration.blackbox.client.AccountSettingsPage;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Delegate;
import org.orcid.integration.blackbox.client.AccountSettingsPage.DelegateSearchResult;
import org.orcid.integration.blackbox.client.AccountSettingsPage.DelegatesSection;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Email;
import org.orcid.integration.blackbox.client.AccountSettingsPage.EmailsSection;
import org.orcid.integration.blackbox.client.OrcidUi;
import org.orcid.integration.blackbox.client.OrcidUi.AccountSwitcherSection;
import org.orcid.integration.blackbox.client.OrcidUi.AccountToSwitchTo;
import org.orcid.integration.blackbox.client.SigninPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-web-context.xml" })
public class AccountSettingsTest {

    private WebDriver webDriver;
    private OrcidUi orcidUi;

    @Value("${org.orcid.web.baseUri}")
    public String baseUri;

    // User 1
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;

    // User 2
    @Value("${org.orcid.web.testUser2.username}")
    public String user2UserName;
    @Value("${org.orcid.web.testUser2.password}")
    public String user2Password;
    @Value("${org.orcid.web.testUser2.orcidId}")
    public String user2OrcidId;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        orcidUi = new OrcidUi(baseUri, webDriver);
    }

    @After
    public void after() {
        orcidUi.quit();
    }

    @Test
    public void emailsTest() {
        SigninPage signinPage = orcidUi.getSigninPage();
        signinPage.visit();
        signinPage.signIn(user1UserName, user1Password);
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        EmailsSection emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        emailsSection.addEmail(emailValue);
        List<Email> emails = emailsSection.getEmails();
        Email addedEmail = emails.stream().filter(e -> e.getEmail().equals(emailValue)).findFirst().get();
        assertNotNull("The added email should be there: " + emailValue, addedEmail);
        addedEmail.delete();
        emails = emailsSection.getEmails();
        assertFalse("The added email should NOT be there: " + emailValue, emails.stream().anyMatch(e -> e.getEmail().equals(emailValue)));
    }

    @Test
    public void emailsTestAsDelegate() {
        // User 1 is going to make user 2 a delegate
        SigninPage signinPage = orcidUi.getSigninPage();
        signinPage.visit();
        signinPage.signIn(user1UserName, user1Password);
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        DelegatesSection delegatesSection = accountSettingsPage.getDelegatesSection();
        // If user 2 is already a delegate, then delete and add again - just to
        // make sure we can!
        Optional<Delegate> existingDelegate = delegatesSection.getDelegates().stream().filter(e -> e.getDelegateId().equals(user2OrcidId)).findFirst();
        if (existingDelegate.isPresent()) {
            existingDelegate.get().revoke();
        }
        delegatesSection.searchForDelegate(user2OrcidId);
        DelegateSearchResult delegateSearchResult = delegatesSection.getDelegateSearchResults().stream().filter(e -> e.getDelegateId().equals(user2OrcidId)).findFirst()
                .get();
        assertNotNull(delegateSearchResult);
        delegateSearchResult.add();
        Delegate addedDelegate = delegatesSection.getDelegates().stream().filter(e -> e.getDelegateId().equals(user2OrcidId)).findFirst().get();
        assertNotNull(addedDelegate);
        // Now sign in as user 2
        signinPage.visit();
        signinPage.signIn(user2UserName, user2Password);
        // And switch to user 1 in delegation mode
        AccountSwitcherSection accountSwitcherSection = orcidUi.getAccountSwitcherSection();
        accountSwitcherSection.open();
        AccountToSwitchTo accountToSwitchTo = accountSwitcherSection.getAccountsToSwitchTo().stream().filter(e -> e.getAccountId().equals(user1OrcidId)).findFirst()
                .get();
        assertNotNull(accountToSwitchTo);
        accountToSwitchTo.switchTo();
    }

}
