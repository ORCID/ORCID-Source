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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.orcid.integration.blackbox.client.AccountSettingsPage;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Email;
import org.orcid.integration.blackbox.client.AccountSettingsPage.EmailsSection;
import org.orcid.integration.blackbox.client.OrcidUi;
import org.orcid.integration.blackbox.client.SigninPage;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class PublicProfileVisibilityTest extends BlackBoxBaseRC2 {
    private static final int TIMEOUT_SECONDS = 10;
    private static final int SLEEP_MILLISECONDS = 100;
    private WebDriver webDriver;
    private OrcidUi orcidUi;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        // webDriver.manage().window().maximize();
        orcidUi = new OrcidUi(getWebBaseUrl(), webDriver);
        signin();
    }

    @After
    public void after() {
        signout();
        orcidUi.quit();
    }

    @Test
    public void emailPrivacyTest() throws InterruptedException {
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        EmailsSection emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        emailsSection.addEmail(emailValue);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        List<Email> emails = emailsSection.getEmails();
        Email addedEmail = emails.stream().filter(e -> e.getEmail().equals(emailValue)).findFirst().get();
        assertNotNull("The added email should be there: " + emailValue, addedEmail);

        // Change Visibility to public
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-emails")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-emails")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("email-" + emailValue + "-public-id")));
        WebElement privateVisibility = webDriver.findElement(By.id("email-" + emailValue + "-public-id"));
        privateVisibility.click();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());

        // Verify
        showPublicProfilePage();

        // Revert Visibility to private
        showMyOrcidPage();
        ngAwareClick(webDriver.findElement(By.id("open-edit-emails")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("email-" + emailValue + "-private-id")));
        ngAwareClick(webDriver.findElement(By.id("email-" + emailValue + "-private-id")), webDriver);
        // Verify
        // TODO: figure out how to know when the post response returns.
        showMyOrcidPage();

        try {
            showPublicProfilePage();
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@name='email' and text() = '" + emailValue + "']")));
            fail("Just found email '" + emailValue + "' that should be private");
        } catch (Exception e) {

        }

        // Rollback changes
        accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        emailsSection.removeEmail(emailValue);
    }

    @Test
    public void otherNamesPrivacyTest() throws InterruptedException {
        showMyOrcidPage();
        ngAwareClick(webDriver.findElement(By.id("open-edit-other-names")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='save-other-names']")));
        List<WebElement> elements = webDriver.findElements(By.xpath("//input[@name='other-name']"));
        WebElement textBox = null;
        for (WebElement element : elements) {
            if (PojoUtil.isEmpty(element.getAttribute("value"))) {
                textBox = element;
                break;
            }
        }
        String otherNameValue = "OtherName" + System.currentTimeMillis();
        textBox.sendKeys(otherNameValue);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());

        // Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("other-names-private-id"));
        privateVisibility.click();
        ngAwareClick(webDriver.findElement(By.id("save-other-names")), webDriver);

        // Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-other-names-div']")));
            WebElement otherNamesDiv = webDriver.findElement(By.xpath("//div[@id='public-other-names-div']"));
            assertNotNull(otherNamesDiv);
            assertFalse(PojoUtil.isEmpty(otherNamesDiv.getText()));
            assertFalse(otherNamesDiv.getText().contains(otherNameValue));
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        ngAwareClick(webDriver.findElement(By.id("open-edit-other-names")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='save-other-names']")));
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("other-names-public-id")));
        ngAwareClick(webDriver.findElement(By.id("other-names-public-id")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.elementToBeClickable(By.id("save-other-names")));
        ngAwareClick(webDriver.findElement(By.id("save-other-names")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.elementToBeClickable(By.id("open-edit-other-names")));

        // Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-other-names-div']")));
        WebElement otherNamesDiv = webDriver.findElement(By.xpath("//div[@id='public-other-names-div']"));
        assertNotNull(otherNamesDiv);
        assertFalse(PojoUtil.isEmpty(otherNamesDiv.getText()));
        assertTrue(otherNamesDiv.getText().contains(otherNameValue));

        // Rollback changes
        showMyOrcidPage();
        ngAwareClick(webDriver.findElement(By.id("open-edit-other-names")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        List<WebElement> otherNames = webDriver.findElements(By.xpath("//input[@name='other-name']"));
        WebElement toClick = null;
        for (WebElement element : otherNames) {
            if (otherNameValue.equals(element.getAttribute("value"))) {
                toClick = element.findElement(By.xpath(".//following-sibling::a[1]"));
                toClick.click();
                break;
            }
        }
        ngAwareClick(webDriver.findElement(By.id("save-other-names")), webDriver);
        new WebDriverWait(webDriver, 1);
    }

    @Test
    public void countryPrivacyTest() {
        showMyOrcidPage();
        String initialValue = "US";
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-country")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-country")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        Select selectBox = new Select(webDriver.findElement(By.id("country")));
        WebElement selectedCountry = selectBox.getFirstSelectedOption();
        if (selectedCountry != null) {
            initialValue = selectedCountry.getAttribute("value");
        }
        selectBox.selectByValue("IN");

        // Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("country-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-country")));

        // Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-country-div']")));
            webDriver.findElement(By.xpath("//div[@id='public-country-div']"));
            fail("Just found country 'India' which should be private");
        } catch (Exception e) {

        }

        // Set Public visibility
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-country")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-country")), webDriver);
        privateVisibility = webDriver.findElement(By.id("country-public-id"));
        privateVisibility.click();
        ngAwareClick(webDriver.findElement(By.id("save-country")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-country")));

        // Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-country-div']")));
        WebElement countryDiv = webDriver.findElement(By.xpath("//div[@id='public-country-div']"));
        assertNotNull(countryDiv);
        assertFalse(PojoUtil.isEmpty(countryDiv.getText()));
        assertTrue(countryDiv.getText().contains("India"));

        // Rollback changes
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-country")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-country")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("country")));
        selectBox = new Select(webDriver.findElement(By.id("country")));
        selectedCountry = selectBox.getFirstSelectedOption();
        selectBox.selectByValue(initialValue);
        saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
    }

    @Test
    public void keyWordPrivacyTest() throws InterruptedException {
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-keywords")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-keywords")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='keyword']")));
        List<WebElement> elements = webDriver.findElements(By.xpath("//input[@name='keyword']"));
        WebElement textBox = null;
        for (WebElement element : elements) {
            if (PojoUtil.isEmpty(element.getAttribute("value"))) {
                textBox = element;
                break;
            }
        }

        String keywordValue = "keyword" + System.currentTimeMillis();
        textBox.sendKeys(keywordValue);

        // Set Private Visibility
        ngAwareClick(webDriver.findElement(By.id("keywords-private-id")), webDriver);
        ngAwareClick(webDriver.findElement(By.id("save-keywords")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-keywords")));

        // Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-keywords-div']")));
            WebElement keywordsDiv = webDriver.findElement(By.xpath("//div[@id='public-keywords-div']"));
            assertNotNull(keywordsDiv);
            assertFalse(PojoUtil.isEmpty(keywordsDiv.getText()));
            assertFalse(keywordsDiv.getText().contains(keywordValue));
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-keywords")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-keywords")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("save-keywords")));
        ngAwareClick(webDriver.findElement(By.id("keywords-public-id")), webDriver);
        ngAwareClick(webDriver.findElement(By.id("save-keywords")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-keywords")));

        // Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-keywords-div']")));
        WebElement keywordsDiv = webDriver.findElement(By.xpath("//div[@id='public-keywords-div']"));
        assertNotNull(keywordsDiv);
        assertFalse(PojoUtil.isEmpty(keywordsDiv.getText()));
        assertTrue(keywordsDiv.getText().contains(keywordValue));

        // Rollback changes
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-keywords")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-keywords")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("save-keywords")));
        List<WebElement> keywords = webDriver.findElements(By.xpath("//input[@name='keyword']"));
        for (WebElement element : keywords) {
            if (keywordValue.equals(element.getAttribute("value"))) {
                WebElement toClick = element.findElement(By.xpath(".//following-sibling::a[1]"));
                toClick.click();
                break;
            }
        }

        ngAwareClick(webDriver.findElement(By.id("save-keywords")), webDriver);
        new WebDriverWait(webDriver, 1);
    }

    @Test
    public void websitesPrivacyTest() {
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-websites")), webDriver);
        List<WebElement> elements = webDriver.findElements(By.xpath("//input[@name='website-url']"));
        WebElement websiteUrl = null;
        WebElement websiteName = null;
        for (WebElement element : elements) {
            if (PojoUtil.isEmpty(element.getAttribute("value"))) {
                websiteUrl = element;
                (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//preceding-sibling::input[@name='website-name']")));
                websiteName = element.findElement(By.xpath(".//preceding-sibling::input[@name='website-name']"));
            }
        }

        long timestamp = System.currentTimeMillis();
        String websiteDesc = "Website" + timestamp;
        angularHasFinishedProcessing();
        websiteName.clear();
        angularHasFinishedProcessing();
        websiteName.sendKeys(websiteDesc);
        angularHasFinishedProcessing();

        String websiteValue = "http://orcid.org/" + timestamp;
        angularHasFinishedProcessing();
        websiteUrl.clear();
        angularHasFinishedProcessing();
        websiteUrl.sendKeys(websiteValue);
        angularHasFinishedProcessing();

        // Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("websites-private-id"));
        privateVisibility.click();
        ngAwareClick(webDriver.findElement(By.id("save-websites")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")));

        // Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-researcher-urls-div']")));
            WebElement researcherUrlsDiv = webDriver.findElement(By.xpath("//div[@id='public-researcher-urls-div']"));
            assertNotNull(researcherUrlsDiv);
            assertFalse(PojoUtil.isEmpty(researcherUrlsDiv.getText()));
            assertFalse(researcherUrlsDiv.getText().contains(websiteValue));
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-websites")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("save-websites")));
        ngAwareClick(webDriver.findElement(By.id("websites-public-id")), webDriver);
        ngAwareClick(webDriver.findElement(By.id("save-websites")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")));

        // Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-researcher-urls-div']")));
        WebElement researcherUrlsDiv = webDriver.findElement(By.xpath("//div[@id='public-researcher-urls-div']"));
        assertNotNull(researcherUrlsDiv);
        assertFalse(PojoUtil.isEmpty(researcherUrlsDiv.getText()));
        assertTrue(researcherUrlsDiv.getText(), researcherUrlsDiv.getText().contains(websiteDesc));

        // Rollback changes
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")));
        ngAwareClick(webDriver.findElement(By.id("open-edit-websites")), webDriver);

        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='save-websites']")));
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='website-url']")));
        List<WebElement> websites = webDriver.findElements(By.xpath("//input[@name='website-url']"));
        for (WebElement element : websites) {
            if (websiteValue.equals(element.getAttribute("value"))) {
                ngAwareClick(element.findElement(By.xpath(".//following-sibling::a[1]")), webDriver);
                break;
            }
        }

        ngAwareClick(webDriver.findElement(By.id("save-websites")), webDriver);
        angularHasFinishedProcessing();
    }

    @Test
    public void educationPrivacyTest() {
        Actions action = new Actions(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.presenceOfElementLocated(ById.id("add-education-container")));
        WebElement container = webDriver.findElement(By.id("add-education-container"));
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.presenceOfElementLocated(ById.id("add-education")));
        action.moveToElement(container).moveToElement(webDriver.findElement(By.id("add-education"))).click().build().perform();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("affiliationName")));

        WebElement textBox = webDriver.findElement(By.id("affiliationName"));
        String educationName = "Education" + System.currentTimeMillis();
        textBox.sendKeys(educationName);
        textBox = webDriver.findElement(By.id("city"));
        textBox.sendKeys("New Delhi");

        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ngAwareClick(webDriver.findElement(By.xpath("//button[@id='save-education']")), webDriver);
        noSpinners(webDriver);
        noCboxOverlay(webDriver);

        // Set Private Visibility
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")));
        WebElement educationElement = webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]"));
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")));
        ngAwareClick(webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")), webDriver);

        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")));

        // Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + educationName + "')]")));
            fail();
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")));
        educationElement = webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]"));
        ngAwareClick(educationElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);

        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")));

        // Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + educationName + "')]")));

        // Rollback changes
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")));
        educationElement = webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]"));
        String putCode = educationElement.getAttribute("education-put-code");
        ngAwareClick(webDriver.findElement(By.id("delete-affiliation_" + putCode)), webDriver);


        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm_delete_affiliation")));
        ngAwareClick(webDriver.findElement(ById.id("confirm_delete_affiliation")), webDriver);

    }

    @Test
    public void employmentPrivacyTest() {
        noSpinners(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("add-employment-container")));
        ngAwareClick(webDriver.findElement(By.id("add-employment-container")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("add-employment")));
        ngAwareClick(webDriver.findElement(By.id("add-employment")), webDriver);
        noSpinners(webDriver);
        
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("affiliationName")));

        WebElement textBox = webDriver.findElement(By.id("affiliationName"));
        String employmentName = "Employment" + System.currentTimeMillis();
        textBox.sendKeys(employmentName);
        textBox = webDriver.findElement(By.id("city"));
        textBox.sendKeys("New Delhi");

        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ngAwareClick(webDriver.findElement(By.xpath("//button[@id='save-education']")), webDriver);
        angularHasFinishedProcessing();
        noSpinners(webDriver);
        noCboxOverlay(webDriver);
        
        // Set Private Visibility
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")));
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")));
        ngAwareClick(webDriver.findElement(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")));

        // Verify
        showPublicProfilePage();
        noSpinners(webDriver);
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + employmentName + "')]")));
            fail();
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        noSpinners(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")));
        WebElement educationElement = webDriver.findElement(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]"));
        ngAwareClick(educationElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);

        
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")));

        // Verify
        showPublicProfilePage();
        noSpinners(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + employmentName + "')]")));

        // Rollback changes
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")));
        educationElement = webDriver.findElement(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]"));
        String putCode = educationElement.getAttribute("employment-put-code");
        ngAwareClick(webDriver.findElement(By.id("delete-affiliation_" + putCode)), webDriver);
        noSpinners(webDriver);
        
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm_delete_affiliation")));
        ngAwareClick(webDriver.findElement(ById.id("confirm_delete_affiliation")), webDriver);

    }

    @Test
    public void fundingPrivacyTest() throws InterruptedException {
        noSpinners(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.presenceOfElementLocated(ById.id("add-funding-container")));
        ngAwareClick(webDriver.findElement(By.id("add-funding-container")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.presenceOfElementLocated(ById.id("add-funding")));
        ngAwareClick(webDriver.findElement(By.id("add-funding")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("fundingType")));
        angularHasFinishedProcessing();
        long time = System.currentTimeMillis();

        WebElement type = webDriver.findElement(By.id("fundingType"));
        type.sendKeys("Award");
        angularHasFinishedProcessing();
        String fundingTitle = "Funding Title " + time;
        WebElement title = webDriver.findElement(By.id("fundingTitle"));
        title.sendKeys(fundingTitle);
        angularHasFinishedProcessing();
        WebElement name = webDriver.findElement(By.id("fundingName"));
        name.sendKeys("Name " + time);
        angularHasFinishedProcessing();
        WebElement city = webDriver.findElement(By.id("city"));
        city.sendKeys("San Jose");
        angularHasFinishedProcessing();
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editFunding.country.value']")));
        selectBox.selectByVisibleText("United States");
        angularHasFinishedProcessing();
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ngAwareClick(webDriver.findElement(By.id("save-funding")), webDriver);
        noSpinners(webDriver);
        
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")));

        // Change to private
        WebElement fundingElement = webDriver.findElement(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        ngAwareClick(fundingElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")));

        // Check public page
        showPublicProfilePage();
        noSpinners(webDriver);
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + fundingTitle + "')]")));
            fail();
        } catch (Exception e) {

        }

        // Change to public
        showMyOrcidPage();
        noSpinners(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")));
        fundingElement = webDriver.findElement(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        ngAwareClick(fundingElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")));

        // Check public page
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + fundingTitle + "')]")));

        // Rollback changes
        showMyOrcidPage();
        noSpinners(webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")));
        fundingElement = webDriver.findElement(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        String putCode = fundingElement.getAttribute("funding-put-code");
        ngAwareClick(fundingElement.findElement(By.id("delete-funding_" + putCode)), webDriver);
        
        angularHasFinishedProcessing();
        
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm-delete-funding")));
        ngAwareClick(webDriver.findElement(ById.id("confirm-delete-funding")), webDriver);

    }

    @Test
    public void workPrivacyTest() throws InterruptedException {
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.presenceOfElementLocated(ById.id("add-work-container")));
        ngAwareClick(webDriver.findElement(By.id("add-work-container")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.presenceOfElementLocated(ById.id("add-work")));
        ngAwareClick(webDriver.findElement(By.id("add-work")), webDriver);
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("workCategory")));

        WebElement category = webDriver.findElement(By.id("workCategory"));
        category.sendKeys("Publication");
        angularHasFinishedProcessing();
        String workTitle = "Work " + System.currentTimeMillis();
        WebElement title = webDriver.findElement(By.id("work-title"));
        title.sendKeys(workTitle);
        angularHasFinishedProcessing();
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ngAwareClick(webDriver.findElement(By.id("save-new-work")), webDriver);
        noSpinners(webDriver);
        
        // Set private
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));

        WebElement workElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]"));
        ngAwareClick(workElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[3]/a")), webDriver);

        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));

        // Check public page
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));
            fail();
        } catch (Exception e) {

        }

        // Set public
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));

        workElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]"));
        WebElement publicVisibilityIcon = workElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a"));
        publicVisibilityIcon.click();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));

        // Check public page
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));

        // Rollback
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]")));
        workElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]"));
        String putCode = workElement.getAttribute("orcid-put-code");
        String deleteJsStr = "angular.element('*[ng-app]').injector().get('worksSrvc').deleteWork('" + putCode + "');";
        ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
    }

    @Test
    public void peerReviewPrivacyTest() throws InterruptedException, JSONException, URISyntaxException {
        // Create peer review group
        String accessToken = super.getAccessToken(ScopePathType.ACTIVITIES_UPDATE.value() + " " + ScopePathType.ACTIVITIES_READ_LIMITED.value(),
                this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        GroupIdRecord g1 = super.createGroupIdRecord();

        // Create peer review
        long time = System.currentTimeMillis();
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc2/samples/peer-review-2.0_rc2.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setGroupId(g1.getGroupId());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(wExtId);

        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        peerReview = getResponse.getEntity(PeerReview.class);

        // Set it private
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));
        WebElement peerReviewElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]"));
        WebElement privateVisibilityIcon = peerReviewElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[3]/a"));
        privateVisibilityIcon.click();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));

        // Check the public page
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));
            fail();
        } catch (Exception e) {

        }

        // Set it public
        showMyOrcidPage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));
        peerReviewElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]"));
        WebElement publicVisibilityIcon = peerReviewElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a"));
        publicVisibilityIcon.click();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));

        // Check the public page
        showPublicProfilePage();
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));

        // Rollback
        ClientResponse deleteResponse = memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), peerReview.getPutCode(), accessToken);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    private void signin() {
        SigninPage signinPage = orcidUi.getSigninPage();
        signinPage.visit();
        signinPage.signIn(getUser1UserName(), getUser1Password());
    }

    private void signout() {
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
    }

    private void showMyOrcidPage() {
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
    }

    private void showPublicProfilePage() {
        webDriver.get(getWebBaseUrl() + "/" + getUser1OrcidId());
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
    }

}
