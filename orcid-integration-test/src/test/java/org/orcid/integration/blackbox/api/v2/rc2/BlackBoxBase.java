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
package org.orcid.integration.blackbox.api.v2.rc2;

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
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class BlackBoxBase {

    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    protected String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    protected String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    protected String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    protected String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    protected String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    protected String client2ClientSecret;    
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;    
    @Value("${org.orcid.web.testUser1.username}")
    protected String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    protected String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String user1OrcidId;
    @Resource(name = "t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient;
    @Resource(name = "memberV2ApiClient_rc2")
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
            } else if(ResearcherUrl.class.equals(type)) {
                result = (ResearcherUrl) obj;
            } else if(PersonalDetails.class.equals(type)) {
                result = (PersonalDetails) obj;
            } else if(OtherName.class.equals(type)) {
                result = (OtherName) obj;
            } else if(PersonExternalIdentifier.class.equals(type)) {
                result = (PersonExternalIdentifier) obj;
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
            // Read the names of the property files
            String propertyFiles = System.getProperty("org.orcid.config.file");
            String[] files = propertyFiles.split(",");

            for (String file : files) {
                file = file.replace("classpath:", "");
                // For each config file, iterate and load the properties
                InputStream inputStream = BlackBoxBase.class.getClassLoader().getResourceAsStream(file);
                prop.load(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String userName = prop.getProperty("org.orcid.web.testUser1.username");
        String password = prop.getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "https://localhost:8443/orcid-web";
        if (!PojoUtil.isEmpty(prop.getProperty("org.orcid.web.base.url"))) {
            baseUrl = prop.getProperty("org.orcid.web.base.url");
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
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btn-unlock")));
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
