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
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PublicProfileVisibilityTest extends BlackBoxBase {
    private static final int FIVE = 5;
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
        
        //Change Visibility to public
        showMyOrcidPage();
        WebElement toggle = webDriver.findElement(By.id("open-edit-emails"));
        toggle.click();
        WebElement privateVisibility = webDriver.findElement(By.id("email-"+emailValue+"-public-id"));
        privateVisibility.click();
        
        //Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='email_" + emailValue + "']")));
        WebElement emailDiv = webDriver.findElement(By.xpath("//div[@id='email_" + emailValue + "']"));
        assertNotNull(emailDiv);
        assertEquals(emailValue, emailDiv.getText());
        
        //Revert Visibility to private
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-emails"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("email-"+emailValue+"-private-id"));
        privateVisibility.click();        
        
        //Verify
        try {
            (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='email_" + emailValue + "']")));
            webDriver.findElement(By.xpath("//div[@id='email_" + emailValue + "']"));
            fail("Just found email '" + emailValue + "' that should be private");
        } catch(Exception e) {
            
        }
        
        //Rollback changes
        accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        emailsSection.removeEmail(emailValue);                
    }
    
    @Test
    public void otherNamesPrivacyTest() {
        showMyOrcidPage();
        WebElement toggle = webDriver.findElement(By.id("open-edit-other-names"));
        toggle.click();
        List<WebElement> otherNameTextBoxList = webDriver.findElements(By.xpath("//input[@name='other-name']"));
        WebElement textBox = null;
        for(WebElement element : otherNameTextBoxList) {
            if(PojoUtil.isEmpty(element.getAttribute("value"))) {
                textBox = element;
                break;
            }
        }
        String otherNameValue = "OtherName" + System.currentTimeMillis();
        textBox.sendKeys(otherNameValue);
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("other-names-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-other-names"));
        saveButton.click();        
        
        //Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-other-names-div']")));
            WebElement otherNamesDiv = webDriver.findElement(By.xpath("//div[@id='public-other-names-div']"));
            assertNotNull(otherNamesDiv);
            assertFalse(PojoUtil.isEmpty(otherNamesDiv.getText()));
            assertFalse(otherNamesDiv.getText().contains(otherNameValue));            
        } catch(Exception e) {
            
        }
        
        //Set Public Visibility
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-other-names"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("other-names-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-other-names"));
        saveButton.click();
        new WebDriverWait(webDriver, 1);
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-other-names")));
        
        //Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-other-names-div']")));
        WebElement otherNamesDiv = webDriver.findElement(By.xpath("//div[@id='public-other-names-div']"));
        assertNotNull(otherNamesDiv);
        assertFalse(PojoUtil.isEmpty(otherNamesDiv.getText()));
        assertTrue(otherNamesDiv.getText().contains(otherNameValue));
        
        //Rollback changes
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-other-names"));
        toggle.click();    
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@test='" + otherNameValue + "']")));
        WebElement otherNameToDelete = webDriver.findElement(By.xpath("//input[text() = '" + otherNameValue + "']"));
        otherNameToDelete.click();
        saveButton = webDriver.findElement(By.id("save-other-names"));
        saveButton.click();
        new WebDriverWait(webDriver, 1);
    }
    
    @Test
    public void countryPrivacyTest() {
        showMyOrcidPage();
        String initialValue = "US";
        WebElement toggle = webDriver.findElement(By.id("open-edit-country"));
        toggle.click();
        Select selectBox = new Select(webDriver.findElement(By.id("country")));
        WebElement selectedCountry = selectBox.getFirstSelectedOption();
        if(selectedCountry != null) {
            initialValue = selectedCountry.getAttribute("value");
        }
        selectBox.selectByValue("IN");
                
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("country-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
        
        //Verify        
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-country-div']")));
            webDriver.findElement(By.xpath("//div[@id='public-country-div']"));
            fail("Just found country 'India' which should be private");
        } catch(Exception e) {
            
        }
                
        //Set Public visibility
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-country"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("country-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-country")));
        
        //Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-country-div']")));
        WebElement countryDiv = webDriver.findElement(By.xpath("//div[@id='public-country-div']"));
        assertNotNull(countryDiv);
        assertFalse(PojoUtil.isEmpty(countryDiv.getText()));
        assertTrue(countryDiv.getText().contains("India"));
        
        //Rollback changes
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-country"));
        toggle.click();
        selectBox = new Select(webDriver.findElement(By.id("country")));
        selectedCountry = selectBox.getFirstSelectedOption();
        selectBox.selectByValue(initialValue);
        saveButton = webDriver.findElement(By.id("save-country"));
        saveButton.click();
    }
    
    @Test
    public void keyWordPrivacyTest() {
        showMyOrcidPage();
        WebElement toggle = webDriver.findElement(By.id("open-edit-keywords"));
        toggle.click();
        WebElement textBox = webDriver.findElement(By.id("keyword"));
        textBox.clear();
        String keywordValue = "keyword" + System.currentTimeMillis();
        textBox.sendKeys(keywordValue);
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("keywords-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-keywords"));
        saveButton.click();
        
        //Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-keywords-div']")));
            WebElement keywordsDiv = webDriver.findElement(By.xpath("//div[@id='public-keywords-div']"));
            assertNotNull(keywordsDiv);
            assertFalse(PojoUtil.isEmpty(keywordsDiv.getText()));
            assertFalse(keywordsDiv.getText().contains(keywordValue));
        } catch(Exception e) {
            
        }
                
        //Set Public Visibility
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-keywords"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("keywords-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-keywords"));
        saveButton.click();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-keywords")));
        
        //Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-keywords-div']")));
        WebElement keywordsDiv = webDriver.findElement(By.xpath("//div[@id='public-keywords-div']"));
        assertNotNull(keywordsDiv);
        assertFalse(PojoUtil.isEmpty(keywordsDiv.getText()));
        assertTrue(keywordsDiv.getText().contains(keywordValue));
        
        //Rollback changes                
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-keywords"));
        toggle.click();    
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("keyword" + keywordValue)));
        WebElement otherNameToDelete = webDriver.findElement(By.xpath("//input[@id = 'keyword" + keywordValue + "']/following-sibling::a[1]"));
        otherNameToDelete.click();
        saveButton = webDriver.findElement(By.id("save-keywords"));
        saveButton.click();
        new WebDriverWait(webDriver, 1);
        
    }
    
    @Test
    public void websitesPrivacyTest() {
        WebElement toggle = webDriver.findElement(By.id("open-edit-websites"));
        toggle.click();
        WebElement textBox1 = webDriver.findElement(By.id("website-desc"));
        WebElement textBox2 = webDriver.findElement(By.id("website-value"));
        
        long timestamp = System.currentTimeMillis();
        String websiteDesc = "Website" + timestamp;
        textBox1.clear();
        textBox1.sendKeys(websiteDesc);
        
        String websiteValue = "http://orcid.org/" + timestamp;
        textBox2.clear();
        textBox2.sendKeys(websiteValue);
        
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("websites-private-id"));
        privateVisibility.click();
        WebElement saveButton = webDriver.findElement(By.id("save-websites"));
        saveButton.click();
        
        //Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-researcher-urls-div']")));
            WebElement researcherUrlsDiv = webDriver.findElement(By.xpath("//div[@id='public-researcher-urls-div']"));
            assertNotNull(researcherUrlsDiv);
            assertFalse(PojoUtil.isEmpty(researcherUrlsDiv.getText()));
            assertFalse(researcherUrlsDiv.getText().contains(websiteValue));
        } catch(Exception e) {
            
        }
        
        //Set Public Visibility
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-websites"));
        toggle.click();
        privateVisibility = webDriver.findElement(By.id("websites-public-id"));
        privateVisibility.click();
        saveButton = webDriver.findElement(By.id("save-websites"));
        saveButton.click();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")));
        
        //Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='public-researcher-urls-div']")));
        WebElement researcherUrlsDiv = webDriver.findElement(By.xpath("//div[@id='public-researcher-urls-div']"));
        assertNotNull(researcherUrlsDiv);
        assertFalse(PojoUtil.isEmpty(researcherUrlsDiv.getText()));
        assertTrue(researcherUrlsDiv.getText(), researcherUrlsDiv.getText().contains(websiteDesc));
        
        //Rollback changes
        showMyOrcidPage();
        toggle = webDriver.findElement(By.id("open-edit-websites"));
        toggle.click();    
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("delete-website_" + websiteDesc)));
        WebElement otherNameToDelete = webDriver.findElement(ById.id("delete-website_" + websiteDesc));
        otherNameToDelete.click();
        saveButton = webDriver.findElement(By.id("save-websites"));
        saveButton.click();
        new WebDriverWait(webDriver, 1);
    }        
    
    
    
    
    
    
    
    
    
    @Test
    public void educationPrivacyTest() throws InterruptedException {
        Actions action = new Actions(webDriver);
        WebElement container = webDriver.findElement(By.id("add-education-container"));
        action.moveToElement(container).moveToElement(webDriver.findElement(By.id("add-education"))).click().build().perform();
        
        WebElement textBox = webDriver.findElement(By.id("affiliationName"));
        String educationName = "Education" + System.currentTimeMillis();
        textBox.sendKeys(educationName);
        textBox = webDriver.findElement(By.id("city"));
        textBox.sendKeys("New Delhi");
        
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        WebElement saveButton = webDriver.findElement(By.id("save-education"));
        saveButton.click();
                
        //Set Private Visibility
        WebElement privateVisibility = webDriver.findElement(By.id("affiliation-"+educationName+"-private-id"));
        privateVisibility.click();
        webDriver.quit();
        
        //Verify
        showPublicProfilePage();
        try {
            (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + educationName + "')]")));
            fail();
        } catch(Exception e) {
            
        }                        
        
        //Set Public Visibility
        showMyOrcidPage();
        privateVisibility = webDriver.findElement(By.id("affiliation-"+educationName+"-public-id"));
        privateVisibility.click();
        
        //Verify
        showPublicProfilePage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + educationName + "')]")));
        
        //Rollback changes
        showMyOrcidPage();
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + educationName + "']]")));
        WebElement educationToDelete = webDriver.findElement(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + educationName + "']]"));
        String putCode = educationToDelete.getAttribute("orcid-put-code");
        WebElement deleteButton = webDriver.findElement(ById.id("delete-affiliation_" + putCode));
        deleteButton.click();
        
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm_delete_affiliation")));
        WebElement confirmDeleteButton = webDriver.findElement(ById.id("confirm_delete_affiliation"));
        confirmDeleteButton.click();
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
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.visibilityOfElementLocated(ById.id("orcid-id")));
    }
    
    private void showPublicProfilePage() {
        webDriver.get(getWebBaseUrl() + "/" + getUser1OrcidId());        
        (new WebDriverWait(webDriver, FIVE)).until(ExpectedConditions.presenceOfElementLocated(By.id("orcid-id")));
    }
    
}
