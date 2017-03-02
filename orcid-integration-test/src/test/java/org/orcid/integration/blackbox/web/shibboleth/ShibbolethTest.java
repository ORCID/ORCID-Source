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
package org.orcid.integration.blackbox.web.shibboleth;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.web.SigninTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class ShibbolethTest {

    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    private WebDriver webDriver;

    @Value("${org.orcid.web.baseUri:https://localhost:8443/orcid-web}")
    public String baseUri;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;

    @Before
    public void before() throws IOException {
        createFireFoxDriverWithModifyHeaders();
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
    }

    private void createFireFoxDriverWithModifyHeaders() throws IOException {
        FirefoxProfile fireFoxProfile = new FirefoxProfile();
        File modifyHeaders = new File(System.getProperty("user.dir") + "/src/test/resources/modify-headers-0.7.1.1.xpi");
        fireFoxProfile.setEnableNativeEvents(false);
        fireFoxProfile.addExtension(modifyHeaders);

        fireFoxProfile.setPreference("modifyheaders.headers.count", 2);
        fireFoxProfile.setPreference("modifyheaders.headers.action0", "Add");
        fireFoxProfile.setPreference("modifyheaders.headers.name0", "persistent-id");
        fireFoxProfile.setPreference("modifyheaders.headers.value0", "integration-test-" + System.currentTimeMillis() + "@orcid.org");
        fireFoxProfile.setPreference("modifyheaders.headers.enabled0", true);
        fireFoxProfile.setPreference("modifyheaders.headers.action1", "Add");
        fireFoxProfile.setPreference("modifyheaders.headers.name1", "Shib-Identity-Provider");
        fireFoxProfile.setPreference("modifyheaders.headers.value1", "https://integrationtest.orcid.org/idp/shibboleth");
        fireFoxProfile.setPreference("modifyheaders.headers.enabled1", true);
        fireFoxProfile.setPreference("modifyheaders.config.active", true);
        fireFoxProfile.setPreference("modifyheaders.config.alwaysOn", true);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("firefox");
        capabilities.setPlatform(org.openqa.selenium.Platform.ANY);
        capabilities.setCapability(FirefoxDriver.PROFILE, fireFoxProfile);
        // Marionette does not allow untrusted certs yet
        capabilities.setCapability(FirefoxDriver.MARIONETTE, false);
        webDriver = new FirefoxDriver(capabilities);
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void testLinkAndSignInWithShibboleth() {
        webDriver.get(baseUri + "/shibboleth/signin");
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='userId']")));
        SigninTest.signIn(webDriver, user1UserName, user1Password);
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., '" + user1OrcidId + "')]")));
    }

}
