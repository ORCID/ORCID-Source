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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.ClientResponse;

public class BlackBoxBase {
    private static String SAVE_BUTTON_XPATH = "//div[@id='colorbox']//button[contains('Save changes',text())]";
    
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
    @Resource(name = "memberV2ApiClient")
    protected MemberV2ApiClientImpl memberV2ApiClient;
    
    private static Map<String, String> accessTokens = new HashMap<String, String>();
    private static Map<String, String> clientCredentialsAccessTokens = new HashMap<String, String>();
    protected static List<GroupIdRecord> groupRecords = null;
    
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

    public static void changeDefaultUserVisibility(WebDriver webDriver, Visibility visibility) {
        String baseUri = BBBUtil.getProperty("org.orcid.web.baseUri");
        String userOrcid = BBBUtil.getProperty("org.orcid.web.testUser1.username");
        String userPassword = BBBUtil.getProperty("org.orcid.web.testUser1.password");
        
        if(PojoUtil.isEmpty(baseUri)) {
            baseUri = "https://localhost:8443/orcid-web";
        }
        
        BBBUtil.logUserOut(baseUri, webDriver);
        webDriver.get(baseUri + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        SigninTest.signIn(webDriver, userOrcid, userPassword);
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
    }
    
    public static void changeBiography(String bioValue, Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        
        try {
            By editBio = By.xpath("//div[@ng-controller='BiographyCtrl']//div[@class='row']//div[2]//div[@ng-click='toggleEdit()']");
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
    
    public static void changeNamesVisibility(Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        
        try {                                    
            By openEditNames = By.xpath("//div[@id = 'names-section']//div[@id = 'open-edit-names']"); 
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
    
    public static void showMyOrcidPage() {
        String baseUrl = BBBUtil.getProperty("org.orcid.web.baseUri");
        webDriver.get(baseUrl + "/my-orcid");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }

    public static void signin() {
        String userOrcid = BBBUtil.getProperty("org.orcid.web.testUser1.username");
        String userPassword = BBBUtil.getProperty("org.orcid.web.testUser1.password");
        signin(userOrcid, userPassword);
    }
    
    public static void signin(String userName, String password) {
        String baseUrl = BBBUtil.getProperty("org.orcid.web.baseUri");        
        webDriver.get(baseUrl + "/signin");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        SigninTest.signIn(webDriver, userName, password);
    }

    public static void signout() {
        String baseUrl = BBBUtil.getProperty("org.orcid.web.baseUri");
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
    }
    
    /**
     * AUTHORIZATION CODE FUNCTIONS
     * @throws InterruptedException 
     */
    public String getAuthorizationCode(String clientId, String clientRedirectUri, String scopes, String userId, String password, boolean longLife) throws InterruptedException {
        WebDriverHelper webDriverHelper = new WebDriverHelper(getWebDriver(), getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper); 
        return oauthHelper.getAuthorizationCode(clientId, scopes, userId, password, longLife);
    }
    
    public String getFullAuthorizationCodeUrl(String clientId, String clientRedirectUri, String scopes, String userId, String password, boolean longLife) throws InterruptedException {
        WebDriverHelper webDriverHelper = new WebDriverHelper(getWebDriver(), getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper); 
        return oauthHelper.getFullAuthorizationCodeUrl(clientId, scopes, userId, password, longLife);
    }
    
    /**
     * ACCESS TOKEN FUNCTIONS
     * */
    public String getAccessToken(List<String> scopes) throws InterruptedException, JSONException{
        return getAccessToken(getUser1OrcidId(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
    
    public ClientResponse getAccessTokenResponse(String clientId, String clientSecret, String clientRedirectUri, String authorizationCode) {
        return oauthHelper.getClientResponse(clientId, clientSecret, null, clientRedirectUri, authorizationCode);
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
    
    public String getAccessToken(String userName, String userPassword, List<String> scopes, String clientId, String clientSecret, String clientRedirectUri, boolean longLife) throws InterruptedException, JSONException {                
        Collections.sort(scopes);
        String scopesString = StringUtils.join(scopes, " ");
        String accessTokenKey = clientId + ":" + userName + ":" + scopesString;
        if(accessTokens.containsKey(accessTokenKey)) {
            return accessTokens.get(accessTokenKey);
        }
        
        WebDriverHelper webDriverHelper = new WebDriverHelper(getWebDriver(), getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);                        
        String token = oauthHelper.obtainAccessToken(clientId, clientSecret, scopesString, userName, userPassword, clientRedirectUri, longLife);
        accessTokens.put(accessTokenKey, token);
        return token;
    }
    
    public String getNonCachedAccessTokens(String userName, String userPassword, List<String> scopes, String clientId, String clientSecret, String clientRedirectUri) throws JSONException, InterruptedException {
        String scopesString = StringUtils.join(scopes, " ");
        WebDriverHelper webDriverHelper = new WebDriverHelper(getWebDriver(), getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);                        
        return oauthHelper.obtainAccessToken(clientId, clientSecret, scopesString, userName, userPassword, clientRedirectUri);        
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
    
    /**
     * PERSONAL NAMES
     */
    public void openEditPersonalNamesSection() {
        waitForElementVisibility(By.id("open-edit-names"));
        ngAwareClick(findElementById("open-edit-names"));
        waitForAngular();
    }

    public void updatePersonalNamesVisibility(Visibility visibility) {
        By elementLocation = By.xpath(String.format("//div[@id='names-section']//div[@id='privacy-bar']/ul/li[%s]/a", getPrivacyIndex(visibility)));
        waitForElementVisibility(elementLocation);
        WebElement privacyOption = findElement(elementLocation);
        ngAwareClick(privacyOption);
    }    
    
    /**
     *  OTHER NAMES
     * */
    public static void openEditOtherNamesModal() {
        waitForElementVisibility(By.id("open-edit-other-names"));
        ngAwareClick(findElementById("open-edit-other-names"));
        waitForCboxComplete();
    }

    public static void deleteOtherNames() {
        waitForAngular();
        By rowBy = By.xpath("//div[@ng-repeat='otherName in otherNamesForm.otherNames']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement : webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//div[@ng-click='deleteOtherName(otherName)']")));            
        }
    }

    public static void createOtherName(String otherName) {
        By addNew = By.xpath("//a[@ng-click='addNewModal()']/span");
        waitForElementVisibility(addNew);
        waitForAngular();
        ngAwareClick(findElement(addNew));
        waitForAngular();
        By emptyInput = By.xpath("(//input[@ng-model='otherName.content'])[last()]");
        waitForElementVisibility(emptyInput);
        WebElement input = findElement(emptyInput);
        input.sendKeys(otherName);
        waitForAngular();
    }

    public static void saveOtherNamesModal() {
        ngAwareClick(findElementByXpath(SAVE_BUTTON_XPATH));
        waitForNoCboxOverlay();
        waitForElementVisibility(By.id("open-edit-other-names"));
    }

    public static void changeOtherNamesVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String otherNamesVisibilityXpath = "//div[@ng-repeat='otherName in otherNamesForm.otherNames']//ul[@class='privacyToggle']/li[" + index + "]";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(otherNamesVisibilityXpath)), webDriver);

        List<WebElement> visibilityElements = webDriver.findElements(By.xpath(otherNamesVisibilityXpath));
        for (WebElement webElement : visibilityElements) {
            BBBUtil.ngAwareClick(webElement, webDriver);
        }        
    }
    
    /**
     *  KEYWORDS
     * */
    public static void openEditKeywordsModal() {
        waitForElementVisibility(By.id("open-edit-keywords"));
        ngAwareClick(findElementById("open-edit-keywords"));
        waitForCboxComplete();
    }

    public static void saveKeywordsModal() {
        ngAwareClick(findElementByXpath(SAVE_BUTTON_XPATH));  
        waitForNoCboxOverlay();
        waitForElementVisibility(By.id("open-edit-keywords"));        
    }        

    public static void changeKeywordsVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String keywordsVisibilityXpath = "//div[@ng-repeat='keyword in keywordsForm.keywords']//ul[@class='privacyToggle']/li[" + index +"]";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(keywordsVisibilityXpath)), webDriver);
        
        List<WebElement> visibilityElements = webDriver.findElements(By.xpath(keywordsVisibilityXpath));
        for (WebElement webElement : visibilityElements) {
            BBBUtil.ngAwareClick(webElement, webDriver);
        }        
    }
    
    public static void createKeyword(String value) {
        By addNew = By.xpath("//a[@ng-click='addNewModal()']/span");
        waitForElementVisibility(addNew);
        waitForAngular();
        ngAwareClick(findElement(addNew));
        waitForAngular();
        By emptyInput = By.xpath("(//input[@ng-model='keyword.content'])[last()]");
        waitForElementVisibility(emptyInput);
        WebElement input = findElement(emptyInput);
        input.sendKeys(value);
        waitForAngular();
    }  
    
    public static void deleteKeywords() {
        waitForAngular();
        By rowBy = By.xpath("//div[@ng-repeat='keyword in keywordsForm.keywords']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement: webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//div[@ng-click='deleteKeyword(keyword)']")));            
        }
    }
    
    /**
     * Address
     * */
    public static void openEditAddressModal() {
        waitForElementVisibility(By.id("country-open-edit-modal"));
        ngAwareClick(findElementById("country-open-edit-modal"));
        waitForCboxComplete();
    }

    public static void saveEditAddressModal() {
        ngAwareClick(findElementByXpath(SAVE_BUTTON_XPATH));
        waitForNoCboxOverlay();
        waitForElementVisibility(By.id("country-open-edit-modal"));        
    }    

    public static void changeAddressVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        
        String countriesVisibilityXpath = "//div[@ng-repeat='country in countryForm.addresses']//descendant::div[@id='privacy-bar']/ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.xpath(countriesVisibilityXpath)), webDriver);
        
        List<WebElement> visibilityElements = webDriver.findElements(By.xpath(countriesVisibilityXpath));
        for (WebElement webElement : visibilityElements) {
            BBBUtil.ngAwareClick(webElement, webDriver);
        }                
    }                
    
    public static void createAddress(String countryCode) {
        By addNew = By.xpath("//a[@ng-click='addNewModal()']/span");
        waitForElementVisibility(addNew);
        waitForAngular();
        ngAwareClick(findElement(addNew));
        waitForAngular();
        By emptyInput = By.xpath("(//select[@ng-model='country.iso2Country.value'])[last()]//option[@value = " + Quotes.escape(countryCode) + "]");
        waitForElementVisibility(emptyInput);
        WebElement countryElement = findElement(emptyInput);
        BBBUtil.ngAwareClick(countryElement, webDriver);        
    }
    
    public static void deleteAddresses() {
        waitForAngular();
        By rowBy = By.xpath("//div[@ng-repeat='country in countryForm.addresses']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement: webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//div[@ng-click='deleteCountry(country)']")));            
        }
    }        
    
    /**
     * RESEARCHER URLS
     * */
    public static void openEditResearcherUrlsModal() {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("open-edit-websites")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(),webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }
    
    public static void saveResearcherUrlsModal() {
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(SAVE_BUTTON_XPATH)), webDriver);        
        BBBUtil.noCboxOverlay(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-websites")), webDriver);
    }
    
    public static void changeResearcherUrlsVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String researcherUrlsVisibilityXpath = "//div[@ng-repeat='website in websitesForm.websites']//ul[@class='privacyToggle']/li[" + index +"]";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(researcherUrlsVisibilityXpath)), webDriver);
        
        List<WebElement> visibilityElements = webDriver.findElements(By.xpath(researcherUrlsVisibilityXpath));
        for (WebElement webElement : visibilityElements) {
            BBBUtil.ngAwareClick(webElement, webDriver);
        }               
    }
    
    public static void createResearcherUrl(String url) {
        By addNew = By.xpath("//a[@ng-click='addNewModal()']/span");
        waitForElementVisibility(addNew);
        waitForAngular();
        ngAwareClick(findElement(addNew));
        waitForAngular();
        By urlXpath = By.xpath("(//input[@ng-model='website.url'])[last()]");
        waitForElementVisibility(urlXpath);
        WebElement urlInput = findElement(urlXpath);
        urlInput.sendKeys(url);
        waitForAngular();
    }  
    
    public static void deleteResearcherUrls() {
        waitForAngular();
        By rowBy = By.xpath("//div[@id='colorbox']//div[@ng-repeat='website in websitesForm.websites']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement: webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//div[@ng-click='deleteWebsite(website)']")));            
        }
    }
    
    /**
     * EXTERNAL IDENTIFIERS
     */
    public static boolean hasExternalIdentifiers() {
        WebElement openEditElement = findElementById("open-edit-external-identifiers");
        return openEditElement.isDisplayed();
    }

    public static void openEditExternalIdentifiersModal() {
        waitForElementVisibility(By.id("open-edit-external-identifiers"));
        ngAwareClick(findElementById("open-edit-external-identifiers"));
        waitForCboxComplete();
    }
    
    public static void saveExternalIdentifiersModal() {
        ngAwareClick(webDriver.findElement(By.xpath(SAVE_BUTTON_XPATH)));        
        waitForNoCboxOverlay();
    }
    
    public static void deleteAllExternalIdentifiersInModal() {
        waitForAngular();
        By rowBy = By.xpath("//div[@ng-repeat='externalIdentifier in externalIdentifiersForm.externalIdentifiers']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElement(rowBy).findElements(By.xpath("//span[@ng-click='deleteExternalIdentifier(externalIdentifier)']"));
        for (WebElement webElement : webElements) {
            ngAwareClick(webElement);
            waitForAngular();
        }
    }
    
    public static void updateExternalIdentifierVisibility(String extId, Visibility visibility) {
        By elementLocation = By.xpath(String.format(
                "//div[@ng-repeat='externalIdentifier in externalIdentifiersForm.externalIdentifiers'][descendant::a[contains(text(),'%s')]]//div[@id='privacy-bar']/ul/li[%s]/a", extId,
                getPrivacyIndex(visibility)));
        waitForElementVisibility(elementLocation);
        WebElement privacyOption = findElement(elementLocation);
        ngAwareClick(privacyOption);
    }
    
    public static void changeExternalIdentifiersVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String extIdsVisibilityXpath = "//div[@ng-repeat='externalIdentifier in externalIdentifiersForm.externalIdentifiers']//ul[@class='privacyToggle']/li[" + index +"]";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(extIdsVisibilityXpath)), webDriver);
        
        List<WebElement> visibilityElements = webDriver.findElements(By.xpath(extIdsVisibilityXpath));
        for (WebElement webElement : visibilityElements) {
            BBBUtil.ngAwareClick(webElement, webDriver);
        }        
    }
     
    public static void deleteExternalIdentifiers() {
        waitForAngular();
        By rowBy = By.xpath("//div[@id='colorbox']//div[@ng-repeat='externalIdentifier in externalIdentifiersForm.externalIdentifiers']");
        waitForElementVisibility(rowBy);
        List<WebElement> webElements = findElements(rowBy);
        for (WebElement webElement: webElements) {
            ngAwareClick(webElement.findElement(By.xpath("//div[@ng-click='deleteExternalIdentifier(externalIdentifier)']")));
        }          
    }
    
    /**
     * WORKS
     * */
    public static void openAddWorkModal() {
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-work-container")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-work-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-work")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-work")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(),webDriver);
    }
    
    public static void changeWorksVisibility(String title, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String workVisibilityXpath = "//li[@orcid-put-code and descendant::span[text()='" + title + "']]//div[@id='privacy-bar']/ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(workVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(workVisibilityXpath)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }    
    
    public static void createWork(String workTitle) {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//select[@ng-model='editWork.workCategory.value']")), webDriver);
        BBBUtil.ngAwareSendKeys("conference","workCategory", webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//option[text()='Conference paper']")), webDriver);
        BBBUtil.ngAwareSendKeys("string:conference-abstract","workType", webDriver);
        BBBUtil.ngAwareSendKeys("doi","worksIdType0", webDriver);
        BBBUtil.ngAwareSendKeys("10.10/"+System.currentTimeMillis(),"worksIdValue0", webDriver);
        BBBUtil.ngAwareSendKeys(workTitle, "work-title", webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        ((JavascriptExecutor)webDriver).executeScript("$('#save-new-work').click();");
        BBBUtil.noCboxOverlay(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
    }
    
    public static void deleteWork(String workTitle) {
        By byWorkTitle = By.xpath("//span[@ng-bind='work.title.value' and text()='" + workTitle + "']");
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + workTitle + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("orcid-put-code");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element(document.body).injector().get('worksSrvc').deleteWork('" + putCode + "');";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
            }
        BBBUtil.extremeWaitFor(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle)), webDriver);
        assertTrue(0 == webDriver.findElements(byWorkTitle).size());
    }
        
    public void removeAllWorks() {
        waitForAngular();
        List<WebElement> trashCans = findWorksTrashCans();
        while (!trashCans.isEmpty()) {
            for (WebElement trashCan : trashCans) {
                ngAwareClick(trashCan);
                waitForCboxComplete();
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
             
    /**
     * EDUCATIONS
     * */
    public static void openAddEducationModal() {
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-education-container")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-education-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-education")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-education")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(),webDriver);
    }
    
    public static void changeEducationVisibility(String title, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String educationVisibilityXpath = "//li[@education-put-code and descendant::span[text()='" + title + "']]//div[@id='privacy-bar']/ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(educationVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(educationVisibilityXpath)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }    
    
    public static void createEducation(String institutionName) {
        String institutionNameXpath = "//input[@ng-model='editAffiliation.affiliationName.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(institutionNameXpath)), webDriver);
        BBBUtil.ngAwareSendKeys(institutionName, "affiliationName", webDriver);
        
        String cityXpath = "//input[@ng-model='editAffiliation.city.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(cityXpath)), webDriver);
        BBBUtil.ngAwareSendKeys("Test land", "city", webDriver);
        
        String countryXpath = "//select[@ng-model='editAffiliation.country.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(countryXpath)), webDriver);
        WebElement countryInput = findElement(By.xpath(countryXpath));
        Select input = new Select(countryInput);
        input.selectByValue(Iso3166Country.US.value());
        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        ((JavascriptExecutor)webDriver).executeScript("$('#save-affiliation').click();");
        BBBUtil.noCboxOverlay(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
    }
    
    public static void deleteEducation(String institutionName) {
        By byInstitutionName = By.xpath("//span[@ng-bind-html='group.getActive().affiliationName.value' and text()='" + institutionName + "']");
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@education-put-code and descendant::span[text() = '" + institutionName + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("education-put-code");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element(document.body).injector().get('affiliationsSrvc').deleteAffiliation({'putCode': {'value': '" + putCode + "'},'affiliationType': {'value': 'education'}});";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
            }
        BBBUtil.extremeWaitFor(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byInstitutionName)), webDriver);
        assertTrue(0 == webDriver.findElements(byInstitutionName).size());
    }
    
    
    /**
     * EMPLOYMENTS
     * */
    public static void openAddEmploymentModal() {
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-employment-container")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-employment-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-employment")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-employment")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(),webDriver);
    }
    
    public static void changeEmploymentVisibility(String title, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String employmentVisibilityXpath = "//li[@employment-put-code and descendant::span[text()='" + title + "']]//div[@id='privacy-bar']/ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(employmentVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(employmentVisibilityXpath)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }    
    
    public static void createEmployment(String institutionName) {
        String institutionNameXpath = "//input[@ng-model='editAffiliation.affiliationName.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(institutionNameXpath)), webDriver);
        BBBUtil.ngAwareSendKeys(institutionName, "affiliationName", webDriver);
        
        String cityXpath = "//input[@ng-model='editAffiliation.city.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(cityXpath)), webDriver);
        BBBUtil.ngAwareSendKeys("Test land", "city", webDriver);
        
        String countryXpath = "//select[@ng-model='editAffiliation.country.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(countryXpath)), webDriver);
        WebElement countryInput = findElement(By.xpath(countryXpath));
        Select input = new Select(countryInput);
        input.selectByValue(Iso3166Country.US.value());
        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        ((JavascriptExecutor)webDriver).executeScript("$('#save-affiliation').click();");
        BBBUtil.noCboxOverlay(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
    }
    
    public static void deleteEmployment(String institutionName) {
        By byInstitutionName = By.xpath("//span[@ng-bind-html='group.getActive().affiliationName.value' and text()='" + institutionName + "']");
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@employment-put-code and descendant::span[text() = '" + institutionName + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("employment-put-code");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element(document.body).injector().get('affiliationsSrvc').deleteAffiliation({'putCode': {'value': '" + putCode + "'},'affiliationType': {'value': 'employment'}});";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
            }
        BBBUtil.extremeWaitFor(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byInstitutionName)), webDriver);
        assertTrue(0 == webDriver.findElements(byInstitutionName).size());
    }
    
    /**
     * FUNDINGS
     * */
    public static void openAddFundingModal() {
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-funding-container")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-funding-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.id("add-funding")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-funding")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(),webDriver);
    }
    
    public static void changeFundingVisibility(String title, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String fundingVisibilityXpath = "//li[@funding-put-code and descendant::span[text()='" + title + "']]//div[@id='privacy-bar']/ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(fundingVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(fundingVisibilityXpath)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }    
    
    public static void createFunding(String fundingTitle) {
        //Funding type
        String typeXpath = "//select[@ng-model='editFunding.fundingType.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(typeXpath)), webDriver);
        WebElement typeInput = findElement(By.xpath(typeXpath));
        Select input = new Select(typeInput);
        input.selectByValue("award");                
        
        //Country
        String countryXpath = "//select[@ng-model='editFunding.country.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(countryXpath)), webDriver);
        WebElement countryInput = findElement(By.xpath(countryXpath));
        input = new Select(countryInput);
        input.selectByValue(Iso3166Country.US.value());
        
        //Institution name
        String institutionNameXpath = "//input[@ng-model='editFunding.fundingName.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(institutionNameXpath)), webDriver);
        BBBUtil.ngAwareSendKeys(fundingTitle, "fundingName", webDriver);
        
        //City
        String cityXpath = "//input[@ng-model='editFunding.city.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(cityXpath)), webDriver);
        BBBUtil.ngAwareSendKeys("Test land", "city", webDriver);
                      
        //Funding title
        String fundingTitleXpath = "//input[@ng-model='editFunding.fundingTitle.title.value']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(fundingTitleXpath)), webDriver);
        BBBUtil.ngAwareSendKeys(fundingTitle, "fundingTitle", webDriver);
        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);           
        ((JavascriptExecutor)webDriver).executeScript("$('#save-funding').click();");
        BBBUtil.noCboxOverlay(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
    }
    
    public static void deleteFunding(String fundingTitle) {
        By byFundingTitle = By.xpath("//span[@ng-bind='group.getActive().fundingTitle.title.value' and text()='" + fundingTitle + "']");
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("funding-put-code");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element(document.body).injector().get('fundingSrvc').deleteFunding('" + putCode + "');";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
            }
        BBBUtil.extremeWaitFor(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byFundingTitle)), webDriver);
        assertTrue(0 == webDriver.findElements(byFundingTitle).size());
    }
    
    /**
     * PEER REVIEW
     * */
    public static void changePeerReviewVisibility(String groupName, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String peerReviewVisibilityXpath = "//li[@orcid-put-code and descendant::span[text()='" + groupName + "']]//div[@id='privacy-bar']/ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(peerReviewVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(peerReviewVisibilityXpath)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }  
    
    /**
     * ACCOUNT SETTINGS PAGE
     * */
    public void showAccountSettingsPage() {
        webDriver.get(getWebBaseUrl() + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }
    
    public boolean haveDelegate(String delegateOrcid) {
        String delegateXpath = "//div[@id='DelegatesCtrl']/table[@ng-show='delegation.givenPermissionTo.delegationDetails']/tbody/tr//a[text()='" + delegateOrcid + "']";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(delegateXpath)), webDriver);
        return true;
    }
    
    public void removeDelegate(String delegateOrcid) {
        String delegateXpath = "//div[@id='DelegatesCtrl']/table[@ng-show='delegation.givenPermissionTo.delegationDetails']/tbody/tr[descendant::a[text()='" + delegateOrcid + "']]/td[4]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(delegateXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(delegateXpath)));
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.xpath("//form[@ng-submit='revoke()']/button")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//form[@ng-submit='revoke()']/button")));
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);        
    }
    
    public void addDelegate(String delegateOrcid) {
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='DelegatesCtrl']//input[@type='text']")), webDriver);
        WebElement input = webDriver.findElement(By.xpath("//div[@id='DelegatesCtrl']//input[@type='text']"));
        input.sendKeys(delegateOrcid);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='DelegatesCtrl']//input[@type='submit']")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//div[@id='DelegatesCtrl']//input[@type='submit']")));
        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners();
        
        String addButton = "//tr[@ng-repeat='result in results' and descendant::a[text() = '" + delegateOrcid + "']]//span";
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(addButton)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(addButton)));
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(), webDriver);
        
        String confirmAddButton = "//form[@ng-submit='addDelegate()']//button[text()='Add']";
        BBBUtil.extremeWaitFor(ExpectedConditions.elementToBeClickable(By.xpath(confirmAddButton)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(confirmAddButton)));
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }
    
    public void switchUser(String userOrcid) {
        String openSwitchUserMenu = "//div[@ng-controller='SwitchUserCtrl']//a[@ng-click='openMenu($event)']";
        String switchUserLink = "//div[@ng-controller='SwitchUserCtrl' and descendant::a[@ng-click='openMenu($event)']]//a[descendant::li[contains(text(),'" + userOrcid + "')]]";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(openSwitchUserMenu)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(openSwitchUserMenu)));
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(switchUserLink)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(switchUserLink)));
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
    }
    
    /**
     * EMAIL ON ACCOUNT SETTINGS PAGE
     * */
    public void openEditEmailsSectionOnAccountSettingsPage() {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("account-settings-toggle-email-edit")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("account-settings-toggle-email-edit")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
    }
    
    public boolean emailExists(String emailValue) {
        String emailXpath = "//div[@ng-controller='EmailEditCtrl']//tr[@name='email' and descendant::span[text() = '" + emailValue + "']]";
        try {
            BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(emailXpath)), webDriver);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public boolean allowedToAddEmails() {
        String addEmailNotAllowedXpath = "//div[@ng-controller='EmailEditCtrl']//div[@id='addEmailNotAllowed']";
        try {
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(addEmailNotAllowedXpath)), webDriver);
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            return false;
        } catch(Exception e) {
            
        }
        return true;
    }
    
    public void updatePrimaryEmailVisibility(Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String primaryEmailVisibilityXpath = "//div[@ng-controller='EmailEditCtrl']//tr[@name='email' and descendant::td[contains(@class, 'primaryEmail')]]/td[6]//ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(primaryEmailVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(primaryEmailVisibilityXpath)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);    
    }
    
    public void updateEmailVisibility(String emailValue, Visibility visibility) {
        int index = getPrivacyIndex(visibility);
        String emailVisibilityXpath = "//div[@ng-controller='EmailEditCtrl']//tr[@name='email' and descendant::span[text() = '" + emailValue + "']]/td[6]//ul/li[" + index + "]/a";
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath(emailVisibilityXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(emailVisibilityXpath)), webDriver);        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }
    
    public void addEmail(String emailValue, Visibility visibility) {
        String emailFormXpath = "//div[@ng-controller='EmailEditCtrl']//input[@type='email']";
        String saveButtonXpath = "//div[@ng-controller='EmailEditCtrl']//input[@type='email']/following-sibling::span[1]";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(emailFormXpath)), webDriver);
        WebElement emailInputElement = webDriver.findElement(By.xpath(emailFormXpath));
        emailInputElement.sendKeys(emailValue);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(saveButtonXpath)), webDriver);
        updateEmailVisibility(emailValue, visibility);
    }
    
    public void removeEmail(String emailValue) {
        String deleteEmailXpath = "//tr[@name='email' and descendant::span[text() = '" + emailValue + "']]/td[5]/a[@name='delete-email']";
        String confirmDeleteButtonXpath = "//button[@id='confirm-delete-email_" + emailValue + "']";
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(deleteEmailXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(deleteEmailXpath)), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(confirmDeleteButtonXpath)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath(confirmDeleteButtonXpath)), webDriver);
        BBBUtil.noCboxOverlay(webDriver);        
    }  
    
    /**
     * PUBLIC PAGE
     * */
    public void showPublicProfilePage(String userId) {
        webDriver.get(getWebBaseUrl() + "/" + userId);
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);        
        BBBUtil.noSpinners(webDriver);        
    }
    
    public boolean emailAppearsInPublicPage(String emailValue) {
        String publicEmailXpath = "//div[@id='public-emails-div']/div[@name='email' and contains(text(), '" + emailValue + "')]";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicEmailXpath)), webDriver);
        return true;   
    }
    
    public boolean otherNamesAppearsInPublicPage(String otherNameValue) {
        String publicOtherNamesXpath = "//div[@id='public-other-names-div']/span[@name='other-name' and text()='" + otherNameValue + "']";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicOtherNamesXpath)), webDriver);
        return true;
    }
    
    public boolean keywordsAppearsInPublicPage(String keywordValue) {
        String publicOtherNamesXpath = "//div[@id='public-keywords-div']/span[@name='keyword' and text()='" + keywordValue + "']";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicOtherNamesXpath)), webDriver);
        return true;
    }
    
    public boolean addressAppearsInPublicPage(String countryName) {
        String publicOtherNamesXpath = "//div[@id='public-country-div']/span[@name='country' and text()='" + countryName + "']";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicOtherNamesXpath)), webDriver);
        return true;
    }
    
    public boolean researcherUrlAppearsInPublicPage(String rUrlValue) {
        String publicResearcherUrlXpath = "//div[@id='public-researcher-urls-div']/a[text()='" + rUrlValue + "']";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicResearcherUrlXpath)), webDriver);
        return true;
    }
        
    public boolean externalIdentifiersAppearsInPublicPage(String extIdValue) {                                                
        String publicExtenalIdentifiersXpath = "//div[@id='public-external-identifiers-div']/a[text()='test: " + extIdValue + "']";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicExtenalIdentifiersXpath)), webDriver);
        return true;
    }
    
    public boolean workAppearsInPublicPage(String workTitle) {                                                
        String publicWorkXpath = "//li[@orcid-put-code and descendant::span[text()='" + workTitle + "']]";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicWorkXpath)), webDriver);
        return true;
    }
    
    public boolean educationAppearsInPublicPage(String institutionName) {                                                
        String publicEducationXpath = "//li[@education-put-code and descendant::span[text()='" + institutionName + "']]";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicEducationXpath)), webDriver);
        return true;
    }
    
    public boolean employmentAppearsInPublicPage(String institutionName) {                                                
        String publicEmploymentXpath = "//li[@employment-put-code and descendant::span[text()='" + institutionName + "']]";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicEmploymentXpath)), webDriver);
        return true;
    }
    
    public boolean fundingAppearsInPublicPage(String fundingTitle) {                                                
        String publicFundingXpath = "//li[@funding-put-code and descendant::span[text()='" + fundingTitle + "']]";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicFundingXpath)), webDriver);
        return true;
    }
    
    public boolean peerReviewAppearsInPublicPage(String groupName) {                                                
        String publicPeerReviewXpath = "//li[@orcid-put-code and descendant::span[text()='" + groupName + "']]";
        BBBUtil.shortWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(publicPeerReviewXpath)), webDriver);
        return true;
    }
    
    
    /**
     * Create group ids
     * */
    public List<GroupIdRecord> createGroupIds() throws JSONException {
        //Use the existing ones
        if(groupRecords != null && !groupRecords.isEmpty()) 
            return groupRecords;
        
        groupRecords = new ArrayList<GroupIdRecord>();
        
        String token = getClientCredentialsAccessToken(ScopePathType.GROUP_ID_RECORD_UPDATE, getClient1ClientId(), getClient1ClientSecret(), APIRequestType.MEMBER);
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:01" + System.currentTimeMillis());
        g1.setName("Group # 1");
        g1.setType("publisher");
        
        GroupIdRecord g2 = new GroupIdRecord();
        g2.setDescription("Description");
        g2.setGroupId("orcid-generated:02" + System.currentTimeMillis());
        g2.setName("Group # 2");
        g2.setType("publisher");                
        
        ClientResponse r1 = memberV2ApiClient.createGroupIdRecord(g1, token); 
        
        String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0/group-id-record/", "");
        g1.setPutCode(Long.valueOf(r1LocationPutCode));
        groupRecords.add(g1);
        
        ClientResponse r2 = memberV2ApiClient.createGroupIdRecord(g2, token);
        String r2LocationPutCode = r2.getLocation().getPath().replace("/orcid-api-web/v2.0/group-id-record/", "");
        g2.setPutCode(Long.valueOf(r2LocationPutCode));
        groupRecords.add(g2);
        
        return groupRecords;
    }
    
    /**
     * GENERAL FUNCTIONS
     * */
    protected static int getPrivacyIndex(Visibility visibility) {
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
    
    public void removePopOver() {
        Actions a = new Actions(webDriver);
        a.moveByOffset(500, 500).perform();        
    }
    
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public Long getPutCodeFromResponse(ClientResponse response) {
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        return Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
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