/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class T2OrcidOAuthApiClientReadPrivateDataIntegrationTest extends DBUnitTest {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");
    
    private static final String READ_PRIVATE_WORKS_CLIENT_ID = "9999-9999-9999-9991";
    private static final String READ_PRIVATE_AFFILIATIONS_CLIENT_ID = "9999-9999-9999-9992";
    private static final String READ_PRIVATE_FUNDING_CLIENT_ID = "9999-9999-9999-9993";
    private static final String READ_ONLY_LIMITED_INFO_CLIENT_ID = "9999-9999-9999-9994";    

    private static final List<String> DATA_FILES = Arrays.asList("/group_client_data/EmptyEntityData.xml", "/group_client_data/ProfileEntityData.xml", "/group_client_data/WorksEntityData.xml",
            "/group_client_data/OrgsEntityData.xml", "/group_client_data/ClientDetailsEntityData.xml", "/group_client_data/ProfileWorksEntityData.xml", "/group_client_data/OrgAffiliationEntityData.xml", "/group_client_data/ProfileFundingEntityData.xml");

    private WebDriver webDriver;
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    
    private String redirectUri;
    
    @Resource
    private ClientRedirectDao clientRedirectDao;
    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }
    
    @Before
    @Transactional
    public void before() {
        webDriver = new FirefoxDriver();
        redirectUri = webBaseUrl + "/oauth/playground";        
        
        //Set redirect uris if needed
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_WORKS_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientRedirectDao.addClientRedirectUri(READ_PRIVATE_WORKS_CLIENT_ID, redirectUri);
            clientDetailsDao.updateLastModified(READ_PRIVATE_WORKS_CLIENT_ID);
        }
        
        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientRedirectDao.addClientRedirectUri(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, redirectUri);
            clientDetailsDao.updateLastModified(READ_PRIVATE_AFFILIATIONS_CLIENT_ID);
        }
        
        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_FUNDING_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientRedirectDao.addClientRedirectUri(READ_PRIVATE_FUNDING_CLIENT_ID, redirectUri);
            clientDetailsDao.updateLastModified(READ_PRIVATE_FUNDING_CLIENT_ID);
        }
        
        clientRedirectUriPk = new ClientRedirectUriPk(READ_ONLY_LIMITED_INFO_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientRedirectDao.addClientRedirectUri(READ_ONLY_LIMITED_INFO_CLIENT_ID, redirectUri);
            clientDetailsDao.updateLastModified(READ_ONLY_LIMITED_INFO_CLIENT_ID);
        }
        
        webDriver.get(webBaseUrl + "/signout");
        
        // Update last modified to force cache eviction (because DB unit deletes
        // a load of stuff from the DB, but reinserts profiles with older last
        // modified date)
        for (ProfileEntity profile : profileDao.getAll()) {
            profileDao.updateLastModifiedDateWithoutResult(profile.getId());
        }
    }

    @After
    public void after() {
        webDriver.quit();
    }
    
    @Test
    public void testGetProfileWithOnlyReadLimitedScope() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_ONLY_LIMITED_INFO_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_ONLY_LIMITED_INFO_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken);
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        
        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for(OrcidWork work : works) {
            String putCode = work.getPutCode();
            if(putCode.equals("1") || putCode.equals("4")){
                fail("This client should not have access to work: " + putCode);
            }
        }                
    }
    
    private String obtainAuthorizationCode(String scopes, String orcid) throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));
        return obtainAuthorizationCode(orcid, scopes, redirectUri);
    }
    
    private String obtainAuthorizationCode(String orcid, String scopes, String redirectUri) throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));
        WebElement userId = webDriver.findElement(By.id("userId"));
        userId.sendKeys("user_to_test@user.com");
        WebElement password = webDriver.findElement(By.id("password"));
        password.sendKeys("password");
        password.submit();
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                if (d.getCurrentUrl().contains("authorize")) {
                    WebElement authorizeButton = d.findElement(By.name("authorize"));
                    if (authorizeButton != null) {
                        return true;
                    }
                }
                return false;
            }
        });
        WebElement authorizeButton = webDriver.findElement(By.name("authorize"));
        Thread.sleep(3000);
        authorizeButton.submit();
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertNotNull(authorizationCode);
        return authorizationCode;
    }
    
    private String obtainAccessToken(String clientId, String authorizationCode, String redirectUri, String scopes) throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "authorization_code");
        params.add("scope", scopes);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        ClientResponse tokenResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        return accessToken;
    }

}
