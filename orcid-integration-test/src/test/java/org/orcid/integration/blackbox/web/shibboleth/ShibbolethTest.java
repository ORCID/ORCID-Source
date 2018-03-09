package org.orcid.integration.blackbox.web.shibboleth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BlackBoxBase;
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

    private WebDriver createFireFoxDriverWithModifyHeaders() throws IOException {
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new ImmutablePair<>("persistent-id", "integration-test-" + System.currentTimeMillis() + "@orcid.org"));
        headers.add(new ImmutablePair<>("Shib-Identity-Provider", "https://integrationtest.orcid.org/idp/shibboleth"));
        return createFireFoxDriverWithModifyHeaders(headers);
    }

    private WebDriver createFireFoxDriverWithModifyHeaders(List<Pair<String, String>> headers) throws IOException {
        FirefoxProfile fireFoxProfile = new FirefoxProfile();
        File modifyHeaders = new File(System.getProperty("user.dir") + "/src/test/resources/modify_headers-0.7.1.1-fx.xpi");
        fireFoxProfile.addExtension(modifyHeaders);
        fireFoxProfile.setPreference("modifyheaders.headers.count", headers.size());
        for (int i = 0; i < headers.size(); i++) {
            fireFoxProfile.setPreference("modifyheaders.headers.action" + i, "Add");
            fireFoxProfile.setPreference("modifyheaders.headers.name" + i, headers.get(i).getLeft());
            fireFoxProfile.setPreference("modifyheaders.headers.value" + i, headers.get(i).getRight());
            fireFoxProfile.setPreference("modifyheaders.headers.enabled" + i, true);
        }
        fireFoxProfile.setPreference("modifyheaders.config.active", true);
        fireFoxProfile.setPreference("modifyheaders.config.alwaysOn", true);

        FirefoxOptions options = new FirefoxOptions();
        options.setCapability(FirefoxDriver.PROFILE, fireFoxProfile);
        // Marionette does not allow untrusted certs yet
        options.setCapability(FirefoxDriver.MARIONETTE, false);
        WebDriver webDriver = new FirefoxDriver(options);
        return webDriver;
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void testLinkAndSignInWithShibboleth() throws IOException {
        webDriver = createFireFoxDriverWithModifyHeaders();
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/shibboleth/signin");
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='userId']")));
        BlackBoxBase.signIn(webDriver, user1UserName, user1Password);
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., '" + user1OrcidId + "')]")));
        // Check can sign in again without linking
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/shibboleth/signin");
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., '" + user1OrcidId + "')]")));
    }

    @Test
    public void testLinkAndSignInWithShibbolethWithoutPersistentId() throws IOException {
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new ImmutablePair<>("Shib-Identity-Provider", "https://integrationtest.orcid.org/idp/shibboleth"));
        webDriver = createFireFoxDriverWithModifyHeaders(headers);
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/shibboleth/signin");
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[string(.) = 'Sorry! Sign in via your institutional account was unsuccessful.']")));
    }

    @Test
    public void testLinkAndSignInWithShibbolethWithoutPersistentIdButWithEppn() throws IOException {
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new ImmutablePair<>("Shib-Identity-Provider", "https://integrationtest.orcid.org/idp/shibboleth"));
        headers.add(new ImmutablePair<>("eppn", "integrationtest@orcid.org"));
        webDriver = createFireFoxDriverWithModifyHeaders(headers);
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/shibboleth/signin");
        new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//p[string(.) = 'Sorry! Sign in via your institutional account (integrationtest@orcid.org) was unsuccessful.']")));
    }

}
