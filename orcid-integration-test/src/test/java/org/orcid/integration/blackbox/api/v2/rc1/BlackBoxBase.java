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
package org.orcid.integration.blackbox.api.v2.rc1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class BlackBoxBase {

 // Admin user
    @Value("${org.orcid.web.adminUser.username}")
    protected String adminUserName;
    @Value("${org.orcid.web.adminUser.password}")
    protected String adminPassword;
    @Value("${org.orcid.web.adminUser.orcidId}")
    protected String adminOrcidId;
    @Value("${org.orcid.web.adminUser.names.given_name}")
    protected String adminGivenName;
    @Value("${org.orcid.web.adminUser.names.family_names}")
    protected String adminFamilyNames;
    @Value("${org.orcid.web.adminUser.names.credit_name}")
    protected String adminCreditName;
    @Value("${org.orcid.web.adminUser.bio}")
    protected String adminBio;

    // User # 1
    @Value("${org.orcid.web.testUser1.username}")
    protected String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    protected String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String user1OrcidId;
    @Value("${org.orcid.web.testUser1.names.given_name}")
    protected String user1GivenName;
    @Value("${org.orcid.web.testUser1.names.family_names}")
    protected String user1FamilyNames;
    @Value("${org.orcid.web.testUser1.names.credit_name}")
    protected String user1CreditName;
    @Value("${org.orcid.web.testUser1.bio}")
    protected String user1Bio;

    // User # 2
    @Value("${org.orcid.web.testUser2.username}")
    protected String user2UserName;
    @Value("${org.orcid.web.testUser2.password}")
    protected String user2Password;
    @Value("${org.orcid.web.testUser2.orcidId}")
    protected String user2OrcidId;
    @Value("${org.orcid.web.testUser2.names.given_name}")
    protected String user2GivenName;
    @Value("${org.orcid.web.testUser2.names.family_names}")
    protected String user2FamilyNames;
    @Value("${org.orcid.web.testUser2.names.credit_name}")
    protected String user2CreditName;
    @Value("${org.orcid.web.testUser2.bio}")
    protected String user2Bio;

    // Public client
    @Value("${org.orcid.web.publicClient1.clientId}")
    protected String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    protected String publicClientSecret;
    @Value("${org.orcid.web.publicClient1.name}")
    protected String publicClientName;
    @Value("${org.orcid.web.publicClient1.redirectUri}")
    protected String publicClientRedirectUri;
    @Value("${org.orcid.web.publicClient1.description}")
    protected String publicClientDescription;
    @Value("${org.orcid.web.publicClient1.website}")
    protected String publicClientWebsite;
    // Lets assume testUser1 is also the owner of the public client
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String publicClientUserOwner;

    // Member # 1
    @Value("${org.orcid.web.member.id}")
    protected String member1Orcid;
    @Value("${org.orcid.web.member.email}")
    protected String member1Email;
    @Value("${org.orcid.web.member.password}")
    protected String member1Password;
    @Value("${org.orcid.web.member.type}")
    protected String member1Type;
    @Value("${org.orcid.web.member.name}")
    protected String member1Name;

    // Client # 1
    @Value("${org.orcid.web.testClient1.clientId}")
    protected String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    protected String client1ClientSecret;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    protected String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.name}")
    protected String client1Name;
    @Value("${org.orcid.web.testClient1.description}")
    protected String client1Description;
    @Value("${org.orcid.web.testClient1.website}")
    protected String client1Website;

    // Client # 2
    @Value("${org.orcid.web.testClient2.clientId}")
    protected String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    protected String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;
    @Value("${org.orcid.web.testClient2.name}")
    protected String client2Name;
    @Value("${org.orcid.web.testClient2.description}")
    protected String client2Description;
    @Value("${org.orcid.web.testClient2.website}")
    protected String client2Website;

    // Member # 2 - Locked
    @Value("${org.orcid.web.locked.member.id}")
    protected String lockedMemberOrcid;
    @Value("${org.orcid.web.locked.member.email}")
    protected String lockedMemberEmail;
    @Value("${org.orcid.web.locked.member.password}")
    protected String lockedMemberPassword;
    @Value("${org.orcid.web.locked.member.name}")
    protected String lockedMemberName;
    @Value("${org.orcid.web.locked.member.type}")
    protected String lockedMemberType;

    // Member # 2 - Client
    @Value("${org.orcid.web.locked.member.client.id}")
    protected String lockedMemberClient1ClientId;
    @Value("${org.orcid.web.locked.member.client.secret}")
    protected String lockedMemberClient1ClientSecret;
    @Value("${org.orcid.web.locked.member.client.ruri}")
    protected String lockedMemberClient1RedirectUri;
    @Value("${org.orcid.web.locked.member.client.name}")
    protected String lockedMemberClient1Name;
    @Value("${org.orcid.web.locked.member.client.description}")
    protected String lockedMemberClient1Description;
    @Value("${org.orcid.web.locked.member.client.website}")
    protected String lockedMemberClient1Website;
    
    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    protected String webBaseUrl;    
    @Resource(name = "t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient;
    @Resource(name = "memberV2ApiClient_rc1")
    protected MemberV2ApiClientImpl memberV2ApiClient;

    protected WebDriver webDriver;

    protected WebDriverHelper webDriverHelper;

    @Resource
    protected OauthHelper oauthHelper;
           
    public String getAccessToken(String scopes, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, clientRedirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String accessToken = oauthHelper.obtainAccessToken(clientId, clientSecret, scopes, user1UserName, user1Password, clientRedirectUri);
        webDriver.quit();
        return accessToken;
    }

    public Object unmarshallFromPath(String path, Class<?> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Object result = null;
            if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Funding.class.equals(type)) {
                result = (Funding) obj;
            } else if (Work.class.equals(type)) {
                result = (Work) obj;
            } else if (PeerReview.class.equals(type)) {
                result = (PeerReview) obj;
            } 
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public Object unmarshall(Reader reader, Class<?> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

    public static void revokeApplicationsAccess(String... clientIdsParam) {
        // Nothing to remove
        if (clientIdsParam == null) {
            return;
        }
        List<String> clientIds = new ArrayList<String>();
        for (String clientId : clientIdsParam) {
            if (!PojoUtil.isEmpty(clientId)) {
                clientIds.add(clientId);
            }
        }

        Properties prop = new Properties();
        

        try {
            //Read the names of the property files
            String propertyFiles = System.getProperty("org.orcid.config.file");
            //XXX: for each one, iterate and load the properties
          InputStream inputStream = 
            BlackBoxBase.class.getClassLoader().getResourceAsStream("config.properties");
                                
          prop.load(inputStream);
          //TODO then check the properties we need
                
        } catch (IOException e) {
                e.printStackTrace();
        }

        return filePath;

  }

        
        ApplicationContext context = new ClassPathXmlApplicationContext("test-memberV2-context.xml");
        String userName = context.getEnvironment().getProperty("org.orcid.web.testUser1.username");
        String password = context.getEnvironment().getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "https://localhost:8443/orcid-web";
        if (!PojoUtil.isEmpty(context.getEnvironment().getProperty("org.orcid.web.base.url"))) {
            baseUrl = context.getEnvironment().getProperty("org.orcid.web.base.url");
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

        } catch (Exception e) {
            // If it fail is because it couldnt find any other application
        } finally {
            webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
            webDriver.quit();
        }
    }
    
    public void adminSignIn(String adminUserName, String adminPassword) {
        webDriver = new FirefoxDriver();
        webDriver.get(webBaseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(webBaseUrl + "/admin-actions");
        SigninTest.signIn(webDriver, adminUserName, adminPassword);
        SigninTest.dismissVerifyEmailModal(webDriver);
    }
    
    public void adminUnlockAccount(String adminUserName, String adminPassword, String orcidToUnlock) {
        // Login Admin
        adminSignIn(adminUserName, adminPassword);
        // Unlock the account
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        WebElement unLockProfileLink = webDriver.findElement(By.linkText("Unlock profile"));
        unLockProfileLink.click();
        WebElement unLockProfileOrcidId = webDriver.findElement(By.id("orcid_to_unlock"));
        unLockProfileOrcidId.sendKeys(orcidToUnlock);
        WebElement unLockButton = webDriver.findElement(By.id("bottom-confirm-unlock-profile"));
        unLockButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-unlock")));
        WebElement confirmUnLockButton = webDriver.findElement(By.id("btn-unlock"));
        confirmUnLockButton.click();
        webDriver.quit();
    }
    
    public void adminLockAccount(String adminUserName, String adminPassword, String orcidToLock) {
        adminSignIn(adminUserName, adminPassword);
        // Lock the account
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        WebElement lockProfileLink = webDriver.findElement(By.linkText("Lock profile"));
        lockProfileLink.click();
        WebElement lockProfileOrcidId = webDriver.findElement(By.id("orcid_to_lock"));
        lockProfileOrcidId.sendKeys(orcidToLock);
        WebElement lockButton = webDriver.findElement(By.id("bottom-confirm-lock-profile"));
        lockButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-lock")));
        WebElement confirmLockButton = webDriver.findElement(By.id("btn-lock"));
        confirmLockButton.click();
        webDriver.quit();
    }
}
