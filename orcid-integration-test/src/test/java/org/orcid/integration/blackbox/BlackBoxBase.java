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
package org.orcid.integration.blackbox;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.record_rc1.Activity;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    protected String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    protected String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    protected String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    protected String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    protected String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    protected String client2ClientSecret;
    @Value("${org.orcid.web.testUser1.username}")
    protected String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    protected String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String user1OrcidId;
    @Resource(name = "t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient;
    @Resource
    protected MemberV2ApiClientImpl memberV2ApiClient;

    protected WebDriver webDriver;

    protected WebDriverHelper webDriverHelper;

    @Resource
    protected OauthHelper oauthHelper;
           
    public String getAccessToken(String scopes, String clientId, String clientSecret) throws InterruptedException, JSONException {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
        String accessToken = oauthHelper.obtainAccessToken(clientId, clientSecret, scopes, user1UserName, user1Password, redirectUri);
        webDriver.quit();
        return accessToken;
    }

    public Activity unmarshallFromPath(String path, Class<? extends Activity> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Activity result = null;
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

    public Object unmarshall(Reader reader, Class<? extends Activity> type) {
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

        String userName = System.getProperty("org.orcid.web.testUser1.username");
        String password = System.getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "http://localhost:8080/orcid-web";
        if (!PojoUtil.isEmpty(System.getProperty("org.orcid.web.base.url"))) {
            baseUrl = System.getProperty("org.orcid.web.base.url");
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
}
