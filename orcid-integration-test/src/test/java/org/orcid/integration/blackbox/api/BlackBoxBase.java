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

import static org.orcid.integration.blackbox.api.BBBUtil.findElement;
import static org.orcid.integration.blackbox.api.BBBUtil.findElementById;
import static org.orcid.integration.blackbox.api.BBBUtil.findElementByXpath;
import static org.orcid.integration.blackbox.api.BBBUtil.findElements;
import static org.orcid.integration.blackbox.api.BBBUtil.findElementsByXpath;
import static org.orcid.integration.blackbox.api.BBBUtil.ngAwareClick;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForAngular;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForCboxComplete;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementVisibility;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForNoCboxOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class BlackBoxBase {
    // Admin user
    @Value("${org.orcid.web.adminUser.username}")
    private String adminUserName;
    @Value("${org.orcid.web.adminUser.password}")
    private String adminPassword;
    @Value("${org.orcid.web.adminUser.orcidId}")
    private String adminOrcidId;
    @Value("${org.orcid.web.adminUser.names.given_name}")
    private String adminGivenName;
    @Value("${org.orcid.web.adminUser.names.family_names}")
    private String adminFamilyNames;
    @Value("${org.orcid.web.adminUser.names.credit_name}")
    private String adminCreditName;
    @Value("${org.orcid.web.adminUser.bio}")
    private String adminBio;

    // User # 1
    @Value("${org.orcid.web.testUser1.username}")
    private String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    private String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    private String user1OrcidId;
    @Value("${org.orcid.web.testUser1.names.given_name}")
    private String user1GivenName;
    @Value("${org.orcid.web.testUser1.names.family_names}")
    private String user1FamilyNames;
    @Value("${org.orcid.web.testUser1.names.credit_name}")
    private String user1CreditName;
    @Value("${org.orcid.web.testUser1.bio}")
    private String user1Bio;

    // User # 2
    @Value("${org.orcid.web.testUser2.username}")
    private String user2UserName;
    @Value("${org.orcid.web.testUser2.password}")
    private String user2Password;
    @Value("${org.orcid.web.testUser2.orcidId}")
    private String user2OrcidId;
    @Value("${org.orcid.web.testUser2.names.given_name}")
    private String user2GivenName;
    @Value("${org.orcid.web.testUser2.names.family_names}")
    private String user2FamilyNames;
    @Value("${org.orcid.web.testUser2.names.credit_name}")
    private String user2CreditName;
    @Value("${org.orcid.web.testUser2.bio}")
    private String user2Bio;

    // Public client
    @Value("${org.orcid.web.publicClient1.clientId}")
    private String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    private String publicClientSecret;
    @Value("${org.orcid.web.publicClient1.name}")
    private String publicClientName;
    @Value("${org.orcid.web.publicClient1.redirectUri}")
    private String publicClientRedirectUri;
    @Value("${org.orcid.web.publicClient1.description}")
    private String publicClientDescription;
    @Value("${org.orcid.web.publicClient1.website}")
    private String publicClientWebsite;
    // Lets assume testUser1 is also the owner of the public client
    @Value("${org.orcid.web.testUser1.orcidId}")
    private String publicClientUserOwner;

    // Member # 1
    @Value("${org.orcid.web.member.id}")
    private String member1Orcid;
    @Value("${org.orcid.web.member.email}")
    private String member1Email;
    @Value("${org.orcid.web.member.password}")
    private String member1Password;
    @Value("${org.orcid.web.member.type}")
    private String member1Type;
    @Value("${org.orcid.web.member.name}")
    private String member1Name;

    // Client # 1
    @Value("${org.orcid.web.testClient1.clientId}")
    private String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    private String client1ClientSecret;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.name}")
    private String client1Name;
    @Value("${org.orcid.web.testClient1.description}")
    private String client1Description;
    @Value("${org.orcid.web.testClient1.website}")
    private String client1Website;

    // Client # 2
    @Value("${org.orcid.web.testClient2.clientId}")
    private String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    private String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    private String client2RedirectUri;
    @Value("${org.orcid.web.testClient2.name}")
    private String client2Name;
    @Value("${org.orcid.web.testClient2.description}")
    private String client2Description;
    @Value("${org.orcid.web.testClient2.website}")
    private String client2Website;    

    @Value("${org.orcid.web.baseUri:https://localhost:8443/orcid-web}")
    private String webBaseUrl;
    @Resource
    protected OauthHelper oauthHelper;
    
    private static Map<String, String> accessTokens = new HashMap<String, String>();
    private static Map<String, String> clientCredentialsAccessTokens = new HashMap<String, String>();
    
    protected static WebDriver webDriver = BlackBoxWebDriver.getWebDriver();
    
    public void adminSignIn(String adminUserName, String adminPassword) {
        webDriver.get(this.getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        webDriver.get(this.getWebBaseUrl() + "/admin-actions");
        SigninTest.signIn(webDriver, adminUserName, adminPassword);
        SigninTest.dismissVerifyEmailModal(webDriver);
    }

    public void adminUnlockAccount(String adminUserName, String adminPassword, String orcidToUnlock) {
        // Login Admin
        adminSignIn(adminUserName, adminPassword);
        try {
            // Unlock the account
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='unlockProfileDiv']/p[1]/a[2]")));
            BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//div[@id='unlockProfileDiv']/p[1]/a[2]")), webDriver);

            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            WebElement unLockProfileOrcidId = webDriver.findElement(By.id("orcid_to_unlock"));
            unLockProfileOrcidId.sendKeys(orcidToUnlock);
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());

            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("bottom-confirm-unlock-profile")));
            BBBUtil.ngAwareClick(webDriver.findElement(By.id("bottom-confirm-unlock-profile")), webDriver);
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());

            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-unlock")));
            BBBUtil.ngAwareClick(webDriver.findElement(By.id("btn-unlock")), webDriver);
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());

        } catch(TimeoutException t) {
            //Account might be already unlocked
        } 
    }
    
    public void logUserOut() {
        BBBUtil.logUserOut(getWebBaseUrl(), webDriver);
    }
    
    public void adminLockAccount(String adminUserName, String adminPassword, String orcidToLock) {
        adminSignIn(adminUserName, adminPassword);
        try {
            // Lock the account
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='lockProfileDiv']/p[1]/a[2]")));
            BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//div[@id='lockProfileDiv']/p[1]/a[2]")), webDriver);

            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("bottom-confirm-lock-profile")), webDriver);

            WebElement lockProfileOrcidId = webDriver.findElement(By.id("orcid_to_lock"));
            lockProfileOrcidId.sendKeys(orcidToLock);
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("bottom-confirm-lock-profile")), webDriver);
            BBBUtil.ngAwareClick(webDriver.findElement(By.id("bottom-confirm-lock-profile")), webDriver);

            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-lock")));
            BBBUtil.ngAwareClick(webDriver.findElement(By.id("btn-lock")), webDriver);
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        } catch (TimeoutException t) {
            // Account might be already locked
        } 
    }

    public void changeDefaultUserVisibility(WebDriver webDriver, Visibility visibility) {
        BBBUtil.logUserOut(getWebBaseUrl(),webDriver);
        webDriver.get(getWebBaseUrl() + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        SigninTest.signIn(webDriver, getUser1UserName(), getUser1Password());
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(),webDriver);
        
        By privacyPreferenceToggle = By.id("privacyPreferencesToggle");
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(privacyPreferenceToggle), webDriver);
        WebElement toggle = webDriver.findElement(privacyPreferenceToggle);
        BBBUtil.ngAwareClick(toggle, webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        String clickXPathStr = "//div[@id='privacy-settings' and contains(text(),'By default, who should')]//a[contains(@ng-click,'" + visibility.value().toUpperCase() + "')]";
        String clickWorkedStr =  "//div[@id='privacy-settings' and contains(text(),'By default, who should ')]//li[@class='" +visibility.value().toLowerCase() + "Active']//a[contains(@ng-click,'" + visibility.value().toUpperCase() + "')]";

        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ByXPath.xpath(clickXPathStr)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(ByXPath.xpath(clickXPathStr)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ByXPath.xpath(clickWorkedStr)), webDriver);
        // this is really evil, suggest JPA isn't flushing/persisting as quick as we would like
        try {Thread.sleep(500);} catch(Exception e) {};
    }
    
    public void changeBiography(String bioValue, Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        
        try {
            By editBio = By.xpath("//div[@ng-controller='BiographyCtrl']//div[@class='row']//div[2]//ul//li//a[@ng-click='toggleEdit()']");
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(editBio));
            WebElement editBioButton = webDriver.findElement(editBio);
            editBioButton.click();
            
            By saveBio = By.xpath("//button[@ng-click='setBiographyForm()']");
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(saveBio));
            
            //Change the content if needed
            if(bioValue != null) {
                By bioTextArea = By.xpath("//textarea[@ng-model='biographyForm.biography.value']");
                (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(bioTextArea));
                WebElement bioTextAreaElement = webDriver.findElement(bioTextArea);
                bioTextAreaElement.clear();
                bioTextAreaElement.sendKeys(bioValue);
            }
            
            //Change the visibility
            By bioOPrivacySelector = By.xpath("//div[@id = 'bio-section']//ul[@class='privacyToggle']/li[" + privacyIndex + "]/a"); 
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(bioOPrivacySelector));            
            WebElement bioOPrivacySelectorLimitedElement = webDriver.findElement(bioOPrivacySelector);
            bioOPrivacySelectorLimitedElement.click(); 
            
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        } catch (Exception e) {
            System.out.println("Unable to find nother-names-visibility-limited element");
            e.printStackTrace();
            throw e;
        }
    }
    
    public void changeNamesVisibility(Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        
        try {                                    
            By openEditNames = By.xpath("//div[@id = 'names-section']//span[@id = 'open-edit-names']"); 
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(openEditNames));            
            WebElement openEditNamesElement = webDriver.findElement(openEditNames);
            openEditNamesElement.click();
            
            By namesVisibility = By.xpath("//div[@id = 'names-section']//ul[@class='privacyToggle']/li[" + privacyIndex + "]/a");
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(namesVisibility));
            WebElement namesVisibilityElement = webDriver.findElement(namesVisibility);
            namesVisibilityElement.click();
            
            By saveButton = By.xpath("//div[@id = 'names-section']//ul[@class='workspace-section-toolbar']//li[1]//button");
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(saveButton));
            WebElement button = webDriver.findElement(saveButton);
            button.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Unable to find names-visibility-limited element");
            e.printStackTrace();
            throw e;
        }
    }
    
    protected void changeOtherNamesVisibility(Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        
        try {
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            By openEditOtherNames = By.xpath("//div[@id = 'other-names-section']//span[@id = 'open-edit-other-names']"); 
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(openEditOtherNames));            
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
            BBBUtil.ngAwareClick(webDriver.findElement(openEditOtherNames), webDriver);
            
            By namesVisibility = By.xpath("//div[@ng-repeat='otherName in otherNamesForm.otherNames']//ul[@class='privacyToggle']/li[" + privacyIndex + "]/a");
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(namesVisibility));
            List<WebElement> namesVisibilityElements = webDriver.findElements(namesVisibility);
               for (WebElement webElement:namesVisibilityElements)
                   BBBUtil.ngAwareClick(webElement, webDriver);
            
            By saveButton = By.xpath("//button[@ng-click='setOtherNamesForm(true)']");
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(saveButton));
            WebElement button = webDriver.findElement(saveButton);
            BBBUtil.ngAwareClick(button, webDriver);
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        } catch (Exception e) {
            System.out.println("Unable to find biography-visibility-limited element");
            e.printStackTrace();
            throw e;
        }
    }   
    
    public void showMyOrcidPage() {
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }

    public void showPublicProfilePage() {
        webDriver.get(getWebBaseUrl() + "/" + getUser1OrcidId());
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }
    
    public void signin() {
        webDriver.get(getWebBaseUrl() + "/signin");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        SigninTest.signIn(webDriver, getUser1UserName(), getUser1Password());
    }

    public void signout() {
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
    }
    
    public void openEditCountryModal() {
        waitForElementVisibility(By.id("country-open-edit-modal"));
        ngAwareClick(findElementById("country-open-edit-modal"));
        waitForCboxComplete();
    }

    public void saveEditCountryModal() {
        ngAwareClick(findElementByXpath("//div[@id='colorbox']//button[contains('Save changes',text())]"));        
        waitForElementVisibility(By.id("country-open-edit-modal"));
        waitForNoCboxOverlay();
    }
    
    public void deleteAllCountriesInCountryModal() {
        waitForAngular();
        By rowBy = By.xpath("//div[@ng-repeat='country in countryForm.addresses']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement: webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//span[@ng-click='deleteCountry(country)']")));
            waitForAngular();
        }
    }

    public void setCountryInCountryModal(String countryString) {
        By addNew = By.xpath("//a[@ng-click='addNewModal()']/span");
        waitForElementVisibility(addNew);
        waitForAngular();
        ngAwareClick(findElement(addNew));
        By selectLocator = By.xpath("//div[@ng-repeat='country in countryForm.addresses']//select[@name='country'][last()]");
        waitForElementVisibility(selectLocator);
        Select selectBox = new Select(findElement(selectLocator));
        selectBox.selectByValue(countryString);
    }

    public void markAllPublicInCountryModal() {
        ngAwareClick(findElementByXpath("//div[@ng-repeat='country in countryForm.addresses']//a[@name='privacy-toggle-3-public']"));
    }

    public void markAllPrivateInCountryModal() {
        ngAwareClick(findElementByXpath("//div[@ng-repeat='country in countryForm.addresses']//a[@name='privacy-toggle-3-private']"));
    }
    
    public void openEditKeywordModal() {
        waitForElementVisibility(By.id("keyword-open-edit-modal"));
        ngAwareClick(findElementById("keyword-open-edit-modal"));
        waitForCboxComplete();
    }

    public void saveEditKeywordModal() {
        ngAwareClick(findElementByXpath("//div[@id='colorbox']//button[contains('Save changes',text())]"));        
        waitForElementVisibility(By.id("keyword-open-edit-modal"));
        waitForNoCboxOverlay();
    }
    
    public void deleteAllKeywordsInKeywordModal() {
        waitForAngular();
        By rowBy = By.xpath("//div[@ng-repeat='keyword in keywordsForm.keywords']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement: webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//span[@ng-click='deleteKeyword(keyword)']")));
            waitForAngular();
        }
    }

    public void addKeywordInKeywordModal(String keywordString) {
        By addNew = By.xpath("//a[@ng-click='addNewModal()']/span");
        waitForElementVisibility(addNew);
        waitForAngular();
        ngAwareClick(findElement(addNew));
        By emptyInput = By.xpath("(//input[@ng-model='keyword.content'])[last()]");
        waitForElementVisibility(emptyInput);
        WebElement input = findElement(emptyInput);
        input.sendKeys(keywordString);
    }

    public void markAllPublicInKeywordModal() {
        ngAwareClick(findElementByXpath("//div[@ng-repeat='keyword in keywordsForm.keywords']//a[@name='privacy-toggle-3-public']"));
    }

    public void markAllPrivateInKeywordModal() {
        ngAwareClick(findElementByXpath("//div[@ng-repeat='keyword in keywordsForm.keywords']//a[@name='privacy-toggle-3-private']"));
    }
    
    public void removeAllWorks() {
        List<WebElement> trashCans = findWorksTrashCans();
        while (!trashCans.isEmpty()) {
            for (WebElement trashCan : trashCans) {
                ngAwareClick(trashCan);
                waitForAngular();
                By deleteButton = By.xpath("//div[@id='colorbox']//div[@class='btn btn-danger']");
                waitForElementVisibility(deleteButton);
                ngAwareClick(findElement(deleteButton));
                waitForNoCboxOverlay();
            }
            trashCans = findWorksTrashCans();
        }
    }

    public List<WebElement> findWorksTrashCans() {
        String trashCansXpath = "//div[@id='workspace-publications']//span[@class='glyphicon glyphicon-trash']";
        List<WebElement> trashCans = findElementsByXpath(trashCansXpath).stream().filter(t -> t.isDisplayed()).collect(Collectors.toList());
        return trashCans;
    }
    
    protected int getPrivacyIndex(Visibility visibility) {
        switch(visibility) {
        case PUBLIC:
            return 1;
        case LIMITED:
            return 2;
        case PRIVATE:
            return 3;
        default:
            return 1;
        }
    }
    
    protected List<String> getScopes(ScopePathType ... params) {
        List<String> scopes = new ArrayList<String>();
        for(ScopePathType scope : params) {
            scopes.add(scope.value());
        }
        return scopes;
    }
    
    public String getAccessToken(List<String> scopes) throws InterruptedException, JSONException{
        return getAccessToken(getUser1OrcidId(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
    
    public String getAccessToken(String userName, String userPassword, List<String> scopes, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {                
        Collections.sort(scopes);
        String scopesString = StringUtils.join(scopes, " ");
        String accessTokenKey = clientId + ":" + userName + ":" + scopesString;
        if(accessTokens.containsKey(accessTokenKey)) {
            return accessTokens.get(accessTokenKey);
        }
        
        WebDriverHelper webDriverHelper = new WebDriverHelper(getWebDriver(), getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);                        
        String token = oauthHelper.obtainAccessToken(clientId, clientSecret, scopesString, userName, userPassword, clientRedirectUri);
        accessTokens.put(accessTokenKey, token);
        return token;
    }
    
    public String getNonCachedAccessTokens(String userName, String userPassword, List<String> scopes, String clientId, String clientSecret, String clientRedirectUri) throws JSONException, InterruptedException {
        String scopesString = StringUtils.join(scopes, " ");
        WebDriverHelper webDriverHelper = new WebDriverHelper(getWebDriver(), getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);                        
        String token = oauthHelper.obtainAccessToken(clientId, clientSecret, scopesString, userName, userPassword, clientRedirectUri);        
        return token;
    }
    
    public String getClientCredentialsAccessToken(ScopePathType scope, String clientId, String clientSecret, APIRequestType requestType) throws JSONException {
        String accessTokenKey = clientId + ":" + scope.value();
        
        if(clientCredentialsAccessTokens.containsKey(accessTokenKey)) {
            return clientCredentialsAccessTokens.get(accessTokenKey);
        }
        
        String token = oauthHelper.getClientCredentialsAccessToken(clientId, clientSecret, scope, requestType);
        clientCredentialsAccessTokens.put(accessTokenKey, token);
        return token;
    }
    
    public void showAccountSettingsPage() {
        webDriver.get(getWebBaseUrl() + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }
    
    public void openEditEmailsSectionOnAccountSettingsPage() {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("account-settings-toggle-email-edit")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("account-settings-toggle-email-edit")), webDriver);
    }
    
    public boolean emailExists(String emailValue) {
        String emailXpath = "//div[@ng-controller='EmailEditCtrl']/descendant::tr[@name='email' and descendant::span[text() = '" + emailValue + "']]";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(emailXpath)), webDriver);
        return true;
    }
    
    public void updatePrimaryEmailVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String primaryEmailVisibilityXpath = "//div[@ng-controller='EmailEditCtrl']/descendant::tr[@name='email' and descendant::td[contains(@class, 'primaryEmail')]]/td[6]/descendant::ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(primaryEmailVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(primaryEmailVisibilityXpath)), webDriver);
    }
    
    public void updateEmailVisibility(String emailValue, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String emailVisibilityXpath = "//div[@ng-controller='EmailEditCtrl']/descendant::tr[@name='email' and descendant::span[text() = '" + emailValue + "']]/td[6]/descendant::ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(emailVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(emailVisibilityXpath)), webDriver);
    }
    
    public void addEmail(String emailValue, Visibility visibility) {
        String emailFormXpath = "//div[@ng-controller='EmailEditCtrl']/descendant::input[@type='email']";
        String saveButtonXpath = "//div[@ng-controller='EmailEditCtrl']/descendant::input[@type='email']/following-sibling::span[1]";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(emailFormXpath)), webDriver);
        WebElement emailInputElement = webDriver.findElement(By.xpath(emailFormXpath));
        emailInputElement.sendKeys(emailValue);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(saveButtonXpath)), webDriver);
        updateEmailVisibility(emailValue, visibility);
    }
    
    public void removeEmail(String emailValue) {
        String deleteEmailXpath = "//div[@ng-controller='EmailEditCtrl']/descendant::tr[@name='email' and descendant::span[text() = '" + emailValue + "']]/td[5]/a[@name='delete-email']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(deleteEmailXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(deleteEmailXpath)), webDriver);        
    }  
    
    public void removePopOver() {
        Actions a = new Actions(webDriver);
        a.moveByOffset(500, 500).perform();        
    }
    
    public String getAdminUserName() {
        return adminUserName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getAdminOrcidId() {
        return adminOrcidId;
    }

    public String getAdminGivenName() {
        return adminGivenName;
    }

    public String getAdminFamilyNames() {
        return adminFamilyNames;
    }

    public String getAdminCreditName() {
        return adminCreditName;
    }

    public String getAdminBio() {
        return adminBio;
    }

    public String getUser1UserName() {
        return user1UserName;
    }

    public String getUser1Password() {
        return user1Password;
    }

    public String getUser1OrcidId() {
        return user1OrcidId;
    }

    public String getUser1GivenName() {
        return user1GivenName;
    }

    public String getUser1FamilyNames() {
        return user1FamilyNames;
    }

    public String getUser1CreditName() {
        return user1CreditName;
    }

    public String getUser1Bio() {
        return user1Bio;
    }

    public String getUser2UserName() {
        return user2UserName;
    }

    public String getUser2Password() {
        return user2Password;
    }

    public String getUser2OrcidId() {
        return user2OrcidId;
    }

    public String getUser2GivenName() {
        return user2GivenName;
    }

    public String getUser2FamilyNames() {
        return user2FamilyNames;
    }

    public String getUser2CreditName() {
        return user2CreditName;
    }

    public String getUser2Bio() {
        return user2Bio;
    }

    public String getPublicClientId() {
        return publicClientId;
    }

    public String getPublicClientSecret() {
        return publicClientSecret;
    }

    public String getPublicClientName() {
        return publicClientName;
    }

    public String getPublicClientRedirectUri() {
        return publicClientRedirectUri;
    }

    public String getPublicClientDescription() {
        return publicClientDescription;
    }

    public String getPublicClientWebsite() {
        return publicClientWebsite;
    }

    public String getPublicClientUserOwner() {
        return publicClientUserOwner;
    }

    public String getMember1Orcid() {
        return member1Orcid;
    }

    public String getMember1Email() {
        return member1Email;
    }

    public String getMember1Password() {
        return member1Password;
    }

    public String getMember1Type() {
        return member1Type;
    }

    public String getMember1Name() {
        return member1Name;
    }

    public String getClient1ClientId() {
        return client1ClientId;
    }

    public String getClient1ClientSecret() {
        return client1ClientSecret;
    }

    public String getClient1RedirectUri() {
        return client1RedirectUri;
    }

    public String getClient1Name() {
        return client1Name;
    }

    public String getClient1Description() {
        return client1Description;
    }

    public String getClient1Website() {
        return client1Website;
    }

    public String getClient2ClientId() {
        return client2ClientId;
    }

    public String getClient2ClientSecret() {
        return client2ClientSecret;
    }

    public String getClient2RedirectUri() {
        return client2RedirectUri;
    }

    public String getClient2Name() {
        return client2Name;
    }

    public String getClient2Description() {
        return client2Description;
    }

    public String getClient2Website() {
        return client2Website;
    }

    public String getWebBaseUrl() {
        return webBaseUrl;
    }

    public OauthHelper getOauthHelper() {
        return oauthHelper;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }
}