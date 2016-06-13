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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.helper.SystemPropertiesHelper;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class BlackBoxBase {
    public static final int TIMEOUT_SECONDS = 10;
    public static final int SLEEP_MILLISECONDS = 100;
        
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

    // Member # 2 - Locked
    @Value("${org.orcid.web.locked.member.id}")
    private String lockedMemberOrcid;
    @Value("${org.orcid.web.locked.member.email}")
    private String lockedMemberEmail;
    @Value("${org.orcid.web.locked.member.password}")
    private String lockedMemberPassword;
    @Value("${org.orcid.web.locked.member.name}")
    private String lockedMemberName;
    @Value("${org.orcid.web.locked.member.type}")
    private String lockedMemberType;

    // Member # 2 - Client
    @Value("${org.orcid.web.locked.member.client.id}")
    private String lockedMemberClient1ClientId;
    @Value("${org.orcid.web.locked.member.client.secret}")
    private String lockedMemberClient1ClientSecret;
    @Value("${org.orcid.web.locked.member.client.ruri}")
    private String lockedMemberClient1RedirectUri;
    @Value("${org.orcid.web.locked.member.client.name}")
    private String lockedMemberClient1Name;
    @Value("${org.orcid.web.locked.member.client.description}")
    private String lockedMemberClient1Description;
    @Value("${org.orcid.web.locked.member.client.website}")
    private String lockedMemberClient1Website;

    @Value("${org.orcid.web.baseUri:https://localhost:8443/orcid-web}")
    private String webBaseUrl;
    @Resource
    protected OauthHelper oauthHelper;
    
    protected static WebDriver webDriver = (new BlackBoxWebDriver()).getWebDriver();
    
    public String getAccessToken(String scopes, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String accessToken = oauthHelper.obtainAccessToken(clientId, clientSecret, scopes, getUser1UserName(), getUser1Password(), clientRedirectUri);
        return accessToken;
    }
    
    public String getAccessToken(String userName, String userPassword, String scopes, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String accessToken = oauthHelper.obtainAccessToken(clientId, clientSecret, scopes, userName, userPassword, clientRedirectUri);
        return accessToken;
    }
    
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
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='unlockProfileDiv']/p[1]/a[2]")));
            ngAwareClick(webDriver.findElement(By.xpath("//div[@id='unlockProfileDiv']/p[1]/a[2]")), webDriver);

            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
            WebElement unLockProfileOrcidId = webDriver.findElement(By.id("orcid_to_unlock"));
            unLockProfileOrcidId.sendKeys(orcidToUnlock);
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());

            (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("bottom-confirm-unlock-profile")));
            ngAwareClick(webDriver.findElement(By.id("bottom-confirm-unlock-profile")), webDriver);
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());

            (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-unlock")));
            ngAwareClick(webDriver.findElement(By.id("btn-unlock")), webDriver);
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());

        } catch(TimeoutException t) {
            //Account might be already unlocked
        } 
    }
    
    public void logUserOut() {
        logUserOut(getWebBaseUrl(), webDriver);
    }
    
    public static void logUserOut(String baseUrl, WebDriver webDriver) {
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        BlackBoxBase.extremeWaitFor(BlackBoxBase.documentReady(),webDriver);
    }

    public void adminLockAccount(String adminUserName, String adminPassword, String orcidToLock) {
        adminSignIn(adminUserName, adminPassword);
        try {
            // Lock the account
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='lockProfileDiv']/p[1]/a[2]")));
            ngAwareClick(webDriver.findElement(By.xpath("//div[@id='lockProfileDiv']/p[1]/a[2]")), webDriver);

            (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
            extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("bottom-confirm-lock-profile")), webDriver);

            WebElement lockProfileOrcidId = webDriver.findElement(By.id("orcid_to_lock"));
            lockProfileOrcidId.sendKeys(orcidToLock);
            (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
            extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("bottom-confirm-lock-profile")), webDriver);
            ngAwareClick(webDriver.findElement(By.id("bottom-confirm-lock-profile")), webDriver);

            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-lock")));
            ngAwareClick(webDriver.findElement(By.id("btn-lock")), webDriver);
            (new WebDriverWait(webDriver, TIMEOUT_SECONDS, SLEEP_MILLISECONDS)).until(angularHasFinishedProcessing());
        } catch (TimeoutException t) {
            // Account might be already locked
        } 
    }

    public static void revokeApplicationsAccess() {
        List<String> clientIds = new ArrayList<String>();        
        Properties prop = SystemPropertiesHelper.getProperties();        
        String clientId1 = prop.getProperty("org.orcid.web.testClient1.clientId");     
        if (!PojoUtil.isEmpty(clientId1)) {
            clientIds.add(clientId1);
        }
                
        String clientId2 = prop.getProperty("org.orcid.web.testClient2.clientId");
        if (!PojoUtil.isEmpty(clientId2)) {
            clientIds.add(clientId2);
        }
        
        String userName = prop.getProperty("org.orcid.web.testUser1.username");
        String password = prop.getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "https://localhost:8443/orcid-web";
        if (!PojoUtil.isEmpty(prop.getProperty("org.orcid.web.baseUri"))) {
            baseUrl = prop.getProperty("org.orcid.web.baseUri");
        }
        
        
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.documentReady());
        webDriver.get(baseUrl + "/my-orcid");
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.documentReady());
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
               
        SigninTest.signIn(webDriver, userName, password);

        // Switch to accounts settings page
        By accountSettingsMenuLink = By.id("accountSettingMenuLink");
        extremeWaitFor(ExpectedConditions.presenceOfElementLocated(accountSettingsMenuLink), webDriver);
        webDriver.get(baseUrl + "/account");
        extremeWaitFor(documentReady(), webDriver);
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
        
        try {
            boolean lookAgain = false;
            do {
                // Look for each revoke app button
                By revokeAppBtn = By.id("revokeAppBtn");
                extremeWaitFor(angularHasFinishedProcessing(), webDriver);
                List<WebElement> appsToRevoke = webDriver.findElements(revokeAppBtn);
                boolean elementFound = false;
                // Iterate on them and delete the ones created by the specified
                // client id
                for (WebElement appElement : appsToRevoke) {
                    String nameAttribute = appElement.getAttribute("name");
                    if (clientIds.contains(nameAttribute)) {
                        ngAwareClick(appElement, webDriver);
                        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
                        // Wait for the revoke button
                        By confirmRevokeAppBtn = By.id("confirmRevokeAppBtn");
                        extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(confirmRevokeAppBtn), webDriver);
                        ngAwareClick(webDriver.findElement(confirmRevokeAppBtn), webDriver);
                        extremeWaitFor(BlackBoxBase.angularHasFinishedProcessing(),webDriver);
                        noCboxOverlay(webDriver);
                        extremeWaitFor(BlackBoxBase.angularHasFinishedProcessing(),webDriver);
                        // may need to put sleep back here
                        elementFound = true;
                        break;
                    }
                }
                if (elementFound) {
                    lookAgain = true;
                } else {
                    lookAgain = false;
                }
            } while (lookAgain);
        } catch (Exception e){
            // If it fail is because it couldn't find any other application
        } finally {
            BlackBoxBase.logUserOut(baseUrl, webDriver);
        }
    }

    public void changeDefaultUserVisibility(WebDriver webDriver, Visibility visibility) {
        logUserOut(getWebBaseUrl(),webDriver);
        webDriver.get(getWebBaseUrl() + "/account");
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
        
        SigninTest.signIn(webDriver, getUser1UserName(), getUser1Password());
        noSpinners(webDriver);
        extremeWaitFor(angularHasFinishedProcessing(),webDriver);
        
        By privacyPreferenceToggle = By.id("privacyPreferencesToggle");
        extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(privacyPreferenceToggle), webDriver);
        WebElement toggle = webDriver.findElement(privacyPreferenceToggle);
        ngAwareClick(toggle, webDriver);
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
        
        String clickXPathStr = "//div[@id='privacy-settings' and contains(text(),'By default, who should')]//a[contains(@ng-click,'" + visibility.value().toUpperCase() + "')]";
        String clickWorkedStr =  "//div[@id='privacy-settings' and contains(text(),'By default, who should ')]//li[@class='" +visibility.value().toLowerCase() + "Active']//a[contains(@ng-click,'" + visibility.value().toUpperCase() + "')]";

        extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ByXPath.xpath(clickXPathStr)), webDriver);
        ngAwareClick(webDriver.findElement(ByXPath.xpath(clickXPathStr)), webDriver);
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
        extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ByXPath.xpath(clickWorkedStr)), webDriver);
        // this is really evil, suggest JPA isn't flushing/persisting as quick as we would like
        try {Thread.sleep(500);} catch(Exception e) {};
    }
    
    static public void ngAwareClick(WebElement webElement, WebDriver webDriver) {
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement).perform();
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
        actions.click(webElement).perform();
    }

    static public void ngAwareSendKeys(String keys, String id, WebDriver webDriver) {
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);          
        ((JavascriptExecutor)webDriver).executeScript(""
           + "angular.element('#" + id + "').triggerHandler('focus');"
           + "angular.element('#" + id + "').val('" + keys + "');"
           + "angular.element('#" + id + "').triggerHandler('change');"
           + "angular.element('#" + id + "').triggerHandler('blur');"
           + "angular.element('#" + id + "').scope().$apply();");
        extremeWaitFor(angularHasFinishedProcessing(), webDriver);
    }

    static public void noSpinners(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100))
        .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("i.glyphicon-refresh")));
    }

    static public void noCboxOverlay(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100))
        .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='cboxOverlay']")));
    }
    
    static public void extremeWaitFor(ExpectedCondition<?> expectedCondition, WebDriver webDriver) {
        int wait = 20;
        int pollingInternval = 250;
        try {
            (new WebDriverWait(webDriver, wait, pollingInternval))
            .until(expectedCondition);
        } catch (Exception e) {
            ((JavascriptExecutor)webDriver).executeScript("$(window).trigger('resize');");
            (new WebDriverWait(webDriver, wait, pollingInternval))
            .until(expectedCondition);            
        }        
    }

    public static ExpectedCondition<Boolean> documentReady() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            }
        };
    }

    public static ExpectedCondition<Boolean> angularHasFinishedProcessing() {
        /*
         * Getting complex.
         * 1. We want to make sure Angular is done. So you call the rootScope apply
         * 2. We want to make sure the browser is done rendering the DOM so we call $timeout
         *    http://blog.brunoscopelliti.com/run-a-directive-after-the-dom-has-finished-rendering/
         * 3. make sure there are no pending AJAX request, if so start over
         */
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                ((JavascriptExecutor) driver).executeScript(""
                        + "window._selenium_angular_done = false;"
                        + "function _seleniumAngularDone() { "
                        + "   angular.element(document.documentElement).scope().$root.$apply("
                        + "      function(){"
                        + "        setTimeout(function(){ "
                        + "            if ($.active > 0)"
                        + "               _seleniumAngularDone();"
                        + "            else"
                        + "               window._selenium_angular_done = true;"
                        + "         }, 0);"
                        + "   });"
                        + "};"
                        + "try { _seleniumAngularDone(); } catch(err) { /* do nothing */ }");
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript(""
                        + "return window._selenium_angular_done;").toString());
            }
        };
    }

    public static ExpectedCondition<Boolean> cboxComplete() {
        /*
         * Getting complex.
         * 1. We want to make sure Angular is done. So you call the rootScope apply
         * 2. We want to make sure the browser is done rendering the DOM so we call $timeout
         *    http://blog.brunoscopelliti.com/run-a-directive-after-the-dom-has-finished-rendering/
         * 3. make sure there are no pending AJAX request, if so start over
         */
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript(""
                        + "return window.cbox_complete").toString());
            }
        };
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

    public String getLockedMemberOrcid() {
        return lockedMemberOrcid;
    }

    public String getLockedMemberEmail() {
        return lockedMemberEmail;
    }

    public String getLockedMemberPassword() {
        return lockedMemberPassword;
    }

    public String getLockedMemberName() {
        return lockedMemberName;
    }

    public String getLockedMemberType() {
        return lockedMemberType;
    }

    public String getLockedMemberClient1ClientId() {
        return lockedMemberClient1ClientId;
    }

    public String getLockedMemberClient1ClientSecret() {
        return lockedMemberClient1ClientSecret;
    }

    public String getLockedMemberClient1RedirectUri() {
        return lockedMemberClient1RedirectUri;
    }

    public String getLockedMemberClient1Name() {
        return lockedMemberClient1Name;
    }

    public String getLockedMemberClient1Description() {
        return lockedMemberClient1Description;
    }

    public String getLockedMemberClient1Website() {
        return lockedMemberClient1Website;
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