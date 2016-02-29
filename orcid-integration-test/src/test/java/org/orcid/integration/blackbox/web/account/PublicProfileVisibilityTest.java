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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.orcid.integration.blackbox.client.AccountSettingsPage;
import org.orcid.integration.blackbox.client.AccountSettingsPage.Email;
import org.orcid.integration.blackbox.client.AccountSettingsPage.EmailsSection;
import org.orcid.integration.blackbox.client.OrcidUi;
import org.orcid.integration.blackbox.client.SigninPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PublicProfileVisibilityTest extends BlackBoxBase {

    private WebDriver webDriver;
    private OrcidUi orcidUi;
    
    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        orcidUi = new OrcidUi(getWebBaseUrl(), webDriver);
        signin();
    }
    
    @After
    public void after() {
        signout();
        orcidUi.quit();
    }

    @Test
    public void emailPrivacyTest() {        
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        EmailsSection emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        emailsSection.addEmail(emailValue);
        List<Email> emails = emailsSection.getEmails();
        Email addedEmail = emails.stream().filter(e -> e.getEmail().equals(emailValue)).findFirst().get();
        assertNotNull("The added email should be there: " + emailValue, addedEmail);
        
        //Change Visibility
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        WebElement toggle = webDriver.findElement(By.id("open-edit-emails"));
        toggle.click();
        WebElement privateVisibility = webDriver.findElement(By.id("email-"+emailValue+"-public-id"));
        privateVisibility.click();
        
        //Verify
        List<WebElement> list = checkPublicProfile(emailValue);
        assertTrue(list.size() > 0);
        
        //Revert Visibility
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        toggle = webDriver.findElement(By.id("open-edit-emails"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("email-"+emailValue+"-private-id"));
        privateVisibility.click();        
        
        //Verify
        list = checkPublicProfile(emailValue);
        assertEquals(0, list.size());
        
        //Rollback changes
        accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        emailsSection.removeEmail(emailValue);                
    }
    
    @Test
    public void otherNamesPrivacyTest() {
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("orcid-id")));
        WebElement toggle = webDriver.findElement(By.id("open-edit-other-names"));
        toggle.click();
        WebElement textBox = webDriver.findElement(By.id("other-name"));
        textBox.clear();
        String otherNameValue = "OtherName" + System.currentTimeMillis();
        textBox.sendKeys(otherNameValue);
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("other-names-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-other-names"));
        saveButton.click();        
        
        //Verify
        List<WebElement> list = checkPublicProfile(otherNameValue);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("orcid-id")));
        toggle = webDriver.findElement(By.id("open-edit-other-names"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("other-names-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-other-names"));
        saveButton.click();
        new WebDriverWait(webDriver, 1);
        
        //Verify
        list = checkPublicProfile(otherNameValue);
        assertTrue(list.size() > 0);
        
        //Rollback changes
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("orcid-id")));
        toggle = webDriver.findElement(By.id("open-edit-other-names"));
        toggle.click();    
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("other-name" + otherNameValue)));
        WebElement otherNameToDelete = webDriver.findElement(By.xpath("//input[@id = 'other-name" + otherNameValue + "']/following-sibling::a[1]"));
        otherNameToDelete.click();
        saveButton = webDriver.findElement(By.id("save-other-names"));
        saveButton.click();
        new WebDriverWait(webDriver, 1);
    }
    
    @Test
    public void countryPrivacyTest() {
        WebElement toggle = webDriver.findElement(By.id("open-edit-country"));
        toggle.click();
        Select selectBox = new Select(webDriver.findElement(By.id("country")));
        selectBox.selectByValue("IN");
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("country-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
        
        //Verify
        List<WebElement> list = checkPublicProfile("India");
        assertEquals(0, list.size());
        
        //Set Public visibility
        toggle = webDriver.findElement(By.id("open-edit-country"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("country-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
        
        //Verify
        list = checkPublicProfile("India");
        assertTrue(list.size() > 0);
    }
    
    @Test
    public void keyWordPrivacyTest() {
        WebElement toggle = webDriver.findElement(By.id("open-edit-keywords"));
        toggle.click();
        WebElement textBox = webDriver.findElement(By.id("keywords"));
        textBox.clear();
        String keywordValue = "Keyword" + System.currentTimeMillis();
        textBox.sendKeys(keywordValue);
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("keywords-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-keywords"));
        saveButton.click();
        
        //Verify
        List<WebElement> list = checkPublicProfile(keywordValue);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        toggle = webDriver.findElement(By.id("open-edit-keywords"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("keywords-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-keywords"));
        saveButton.click();
        
        //Verify
        list = checkPublicProfile(keywordValue);
        assertTrue(list.size() > 0);
    }
    
    @Test
    public void websitesPrivacyTest() {
        WebElement toggle = webDriver.findElement(By.id("open-edit-websites"));
        toggle.click();
        WebElement textBox1 = webDriver.findElement(By.id("website-desc"));
        long timestamp = System.currentTimeMillis();
        String websiteDesc = "Website" + timestamp;
        textBox1.clear();
        textBox1.sendKeys(websiteDesc);
        WebElement textBox2 = webDriver.findElement(By.id("website-value"));
        String websiteValue = "http://www."+timestamp+".com";
        textBox2.clear();
        textBox2.sendKeys(websiteValue);
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("websites-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-websites"));
        saveButton.click();
        
        //Verify
        List<WebElement> list = checkPublicProfile(websiteDesc);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        toggle = webDriver.findElement(By.id("open-edit-websites"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("websites-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-websites"));
        saveButton.click();
        
        //Verify
        list = checkPublicProfile(websiteDesc);
        assertTrue(list.size() > 0);
    }
    
    @Test
    public void educationPrivacyTest() throws InterruptedException {
        Actions action = new Actions(webDriver);
        WebElement container = webDriver.findElement(By.id("add-education-container"));
        action.moveToElement(container).moveToElement(webDriver.findElement(By.id("add-education"))).click().build().perform();
        Thread.sleep(1000);
        WebElement textBox = webDriver.findElement(By.id("affiliationName"));
        String educationName = "Education" + System.currentTimeMillis();
        textBox.sendKeys(educationName);
        textBox = webDriver.findElement(By.id("city"));
        textBox.sendKeys("New Delhi");
        
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        WebElement saveButton = webDriver.findElement(By.id("save-education"));
        saveButton.click();
        
        Thread.sleep(1000);
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("affiliation-"+educationName+"-private-id"));
        privateVisibility.click();

        //Verify
        List<WebElement> list = checkPublicProfile(educationName);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        privateVisibility = webDriver.findElement(By.id("affiliation-"+educationName+"-public-id"));
        privateVisibility.click();
        
        //Verify
        list = checkPublicProfile(educationName);
        assertTrue(list.size() > 0);
    }
    
    @Test
    public void employmentPrivacyTest() throws InterruptedException {
        Actions action = new Actions(webDriver);
        WebElement container = webDriver.findElement(By.id("add-employment-container"));
        action.moveToElement(container).moveToElement(webDriver.findElement(By.id("add-employment"))).click().build().perform();
        Thread.sleep(1000);
        WebElement textBox = webDriver.findElement(By.id("affiliationName"));
        String employmentName = "Employment" + System.currentTimeMillis();
        textBox.sendKeys(employmentName);
        textBox = webDriver.findElement(By.id("city"));
        textBox.sendKeys("New Delhi");
        
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        WebElement saveButton = webDriver.findElement(By.id("save-education"));
        saveButton.click();
        
        Thread.sleep(1000);
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("affiliation-"+employmentName+"-private-id"));
        privateVisibility.click();

        //Verify
        List<WebElement> list = checkPublicProfile(employmentName);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        privateVisibility = webDriver.findElement(By.id("affiliation-"+employmentName+"-public-id"));
        privateVisibility.click();
        
        //Verify
        list = checkPublicProfile(employmentName);
        assertTrue(list.size() > 0);
    }
    
    @Test
    public void fundingPrivacyTest() throws InterruptedException {
        Actions action = new Actions(webDriver);
        WebElement container = webDriver.findElement(By.id("add-funding-container"));
        action.moveToElement(container).moveToElement(webDriver.findElement(By.id("add-funding"))).click().build().perform();
        Thread.sleep(1000);
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editFunding.fundingType.value']")));
        selectBox.selectByVisibleText("Award");
        
        WebElement textBox = webDriver.findElement(By.id("fundingTitle"));
        String fundingName = "Funding" + System.currentTimeMillis();
        textBox.sendKeys(fundingName);
        textBox = webDriver.findElement(By.id("fundingName"));
        textBox.sendKeys("FundingTestAgency");
        textBox = webDriver.findElement(By.id("city"));
        textBox.sendKeys("New Delhi");
        selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editFunding.country.value']")));
        selectBox.selectByVisibleText("India");
        
        WebElement saveButton = webDriver.findElement(By.id("save-funding"));
        saveButton.click();
        
        Thread.sleep(1000);
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("funding-"+fundingName+"-private-id"));
        privateVisibility.click();

        //Verify
        List<WebElement> list = checkPublicProfile(fundingName);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        privateVisibility = webDriver.findElement(By.id("funding-"+fundingName+"-public-id"));
        privateVisibility.click();
        
        //Verify
        list = checkPublicProfile(fundingName);
        assertTrue(list.size() > 0);
    }
    
    @Test
    public void workPrivacyTest() throws InterruptedException {
        Actions action = new Actions(webDriver);
        WebElement container = webDriver.findElement(By.id("add-work-container"));
        action.moveToElement(container).moveToElement(webDriver.findElement(By.id("add-work"))).click().build().perform();
        Thread.sleep(1000);
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        selectBox.selectByVisibleText("Publication");
        
        WebElement textBox = webDriver.findElement(By.id("work-title"));
        String workName = "Work" + System.currentTimeMillis();
        textBox.sendKeys(workName);
        
        WebElement saveButton = webDriver.findElement(By.id("save-new-work"));
        saveButton.click();
        
        Thread.sleep(1000);
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("work-"+workName+"-private-id"));
        privateVisibility.click();

        //Verify
        List<WebElement> list = checkPublicProfile(workName);
        assertEquals(0, list.size());
        
        //Set Public Visibility
        privateVisibility = webDriver.findElement(By.id("work-"+workName+"-public-id"));
        privateVisibility.click();
        
        //Verify
        list = checkPublicProfile(workName);
        assertTrue(list.size() > 0);
    }
    
    private void signin() {
        SigninPage signinPage = orcidUi.getSigninPage();
        signinPage.visit();
        signinPage.signIn(getUser1UserName(), getUser1Password());
    }
    
    private void signout() {
       webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
    }
    
    private List<WebElement> checkPublicProfile(String value) {
        webDriver.get(getWebBaseUrl() + "/" + getUser1OrcidId());
        (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("orcid-id")));        
        List<WebElement> list = webDriver.findElements(By.xpath("//*[contains(text(),'" + value + "')]"));
        return list;
    }
}
