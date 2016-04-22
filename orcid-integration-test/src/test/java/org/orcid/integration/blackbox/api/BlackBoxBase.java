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
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
    
    protected WebDriver webDriver;
    protected WebDriverHelper webDriverHelper;    
    
    public String getAccessToken(String scopes, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String accessToken = oauthHelper.obtainAccessToken(clientId, clientSecret, scopes, getUser1UserName(), getUser1Password(), clientRedirectUri);
        webDriver.quit();
        return accessToken;
    }
    
    public String getAccessToken(String userName, String userPassword, String scopes, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, this.getWebBaseUrl(), clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String accessToken = oauthHelper.obtainAccessToken(clientId, clientSecret, scopes, userName, userPassword, clientRedirectUri);
        webDriver.quit();
        return accessToken;
    }
    
    public void adminSignIn(String adminUserName, String adminPassword) {
        webDriver = new FirefoxDriver();
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
            (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("unlockProfileDiv")));
            WebElement unLockProfileLink = webDriver.findElement(By.xpath("//div[@id='unlockProfileDiv']/p[1]/a[2]"));
            unLockProfileLink.click();
            WebElement unLockProfileOrcidId = webDriver.findElement(By.id("orcid_to_unlock"));
            unLockProfileOrcidId.sendKeys(orcidToUnlock);
                    
            WebElement unLockButton = webDriver.findElement(By.id("bottom-confirm-unlock-profile"));
            unLockButton.click();
            (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.elementToBeClickable(By.id("btn-unlock")));            
            WebElement confirmUnLockButton = webDriver.findElement(By.id("btn-unlock"));
            confirmUnLockButton.click();
        } catch(TimeoutException t) {
            //Account might be already unlocked
        } finally {
            webDriver.quit();
        }
        
    }

    public void adminLockAccount(String adminUserName, String adminPassword, String orcidToLock) {
        adminSignIn(adminUserName, adminPassword);
        try {
            // Lock the account
            (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("lockProfileDiv")));
            WebElement lockProfileLink = webDriver.findElement(By.xpath("//div[@id='lockProfileDiv']/p[1]/a[2]"));
            lockProfileLink.click();
            WebElement lockProfileOrcidId = webDriver.findElement(By.id("orcid_to_lock"));
            lockProfileOrcidId.sendKeys(orcidToLock);
            WebElement lockButton = webDriver.findElement(By.id("bottom-confirm-lock-profile"));
            lockButton.click();

            (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.elementToBeClickable(By.id("btn-lock")));
            WebElement confirmLockButton = webDriver.findElement(By.id("btn-lock"));
            confirmLockButton.click();
        } catch (TimeoutException t) {
            // Account might be already locked
        } finally {
            webDriver.quit();
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

        WebDriver webDriver = new FirefoxDriver();

        int timeout = 4;
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUrl + "/my-orcid");
        SigninTest.signIn(webDriver, userName, password);

        // Switch to accounts settings page
        By accountSettingsMenuLink = By.id("accountSettingMenuLink");
        (new WebDriverWait(webDriver, timeout)).until(ExpectedConditions.presenceOfElementLocated(accountSettingsMenuLink));
        WebElement menuItem = webDriver.findElement(accountSettingsMenuLink);
        menuItem.click();

        try {
            boolean lookAgain = false;
            do {
                // Look for each revoke app button
                By revokeAppBtn = By.id("revokeAppBtn");
                (new WebDriverWait(webDriver, timeout)).until(ExpectedConditions.presenceOfElementLocated(revokeAppBtn));
                List<WebElement> appsToRevoke = webDriver.findElements(revokeAppBtn);
                boolean elementFound = false;
                // Iterate on them and delete the ones created by the specified
                // client id
                for (WebElement appElement : appsToRevoke) {
                    String nameAttribute = appElement.getAttribute("name");
                    if (clientIds.contains(nameAttribute)) {
                        appElement.click();
                        Thread.sleep(1000);
                        // Wait for the revoke button
                        By confirmRevokeAppBtn = By.id("confirmRevokeAppBtn");
                        (new WebDriverWait(webDriver, timeout)).until(ExpectedConditions.presenceOfElementLocated(confirmRevokeAppBtn));
                        WebElement trash = webDriver.findElement(confirmRevokeAppBtn);
                        trash.click();
                        Thread.sleep(2000);
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
            // If it fail is because it couldnt find any other application
        } finally {
            webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
            webDriver.quit();
        }
    }

    public static void changeDefaultUserVisibility(WebDriver webDriver, Visibility visibility) {
        Properties prop = SystemPropertiesHelper.getProperties();
        String userName = prop.getProperty("org.orcid.web.testUser1.username");
        String password = prop.getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "https://localhost:8443/orcid-web";
        if (!PojoUtil.isEmpty(prop.getProperty("org.orcid.web.baseUri"))) {
            baseUrl = prop.getProperty("org.orcid.web.baseUri");
        }

        int timeout = 10;
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUrl + "/account");
        SigninTest.signIn(webDriver, userName, password);
        
        By privacyPreferenceToggle = By.id("privacyPreferencesToggle");
        (new WebDriverWait(webDriver, timeout)).until(ExpectedConditions.visibilityOfElementLocated(privacyPreferenceToggle));
        WebElement toggle = webDriver.findElement(privacyPreferenceToggle);
        toggle.click();
                
        By privacySettingsDiv = By.id("privacy-settings");
        (new WebDriverWait(webDriver, timeout)).until(ExpectedConditions.visibilityOfElementLocated(privacySettingsDiv));
        WebElement privacySettings = webDriver.findElement(privacySettingsDiv);
        
        int visibilityIndex = Visibility.PUBLIC.equals(visibility) ? 1 : (Visibility.LIMITED.equals(visibility) ? 2 : 3);
        
        WebElement visibilityToClick = privacySettings.findElement(ByXPath.xpath(".//div[@id='privacy-bar']//ul//li[" + visibilityIndex + "]"));
        visibilityToClick.click();
        
        try {Thread.sleep(500);} catch(Exception e) {};        
    }
    
    public static void ngAwareClick(WebElement webElement, WebDriver webDriver) {
        angularHasFinishedProcessing();
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement).click().perform();
        angularHasFinishedProcessing();
    }
    
    public void noSpinners(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100))
        .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("i.glyphicon-refresh")));
    }

    public void noCboxOverlay(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100))
        .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='cboxOverlay']")));
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
                        + "_seleniumAngularDone();");
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript(""
                        + "return window._selenium_angular_done;").toString());
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

    public WebDriverHelper getWebDriverHelper() {
        return webDriverHelper;
    }


}