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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.integration.blackbox.client.AccountSettingsPage;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Email;
import org.orcid.integration.blackbox.client.AccountSettingsPage.EmailsSection;
import org.orcid.integration.blackbox.client.OrcidUi;
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
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;

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

}
