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
package org.orcid.integration.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
public class PersistentTokensIntegrationTest extends DBUnitTest {
    private static final String CLIENT_DETAILS_ID = "4444-4444-4444-4445";
    private static final String NO_PERSISTENT_TOKEN_CLIENT_DETAILS_ID = "4444-4444-4444-4444";

    private static final String DEFAULT = "default";        
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    
    @Resource
    private ProfileDao profileDao;

    @Resource
    OrcidOauth2AuthoriziationCodeDetailDao authorizationCodeDetailDao;
    
    @Resource
    OrcidOauth2TokenDetailDao oauth2TokenDetailDao;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;
    
    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    @Transactional
    public void before() {
        webDriver = new FirefoxDriver();
        redirectUri = webBaseUrl + "/oauth/playground";
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);

        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(CLIENT_DETAILS_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(CLIENT_DETAILS_ID, redirectUri);
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
    public void createAuthenticatedTokenToGeneratePersistentTokenTest() throws InterruptedException {
        List<String> items = new ArrayList<String>();
        items.add("enablePersistentToken");
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        
        OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity = authorizationCodeDetailDao.find(authorizationCode);
        assertNotNull(authorizationCodeEntity);
        assertTrue(authorizationCodeEntity.isPersistent());
    }
    
    @Test
    public void createNonPersistentAuthenticatedTokenTest() throws InterruptedException {
        List<String> items = new ArrayList<String>();
        items.add("enablePersistentToken");
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, false);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        
        OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity = authorizationCodeDetailDao.find(authorizationCode);
        assertNotNull(authorizationCodeEntity);
        assertFalse(authorizationCodeEntity.isPersistent());
    }
    
    @Test        
    public void createPersistentToken() throws InterruptedException, JSONException {
        Date beforeCreatingToken = twentyYearsTime();
        List<String> items = new ArrayList<String>();
        items.add("enablePersistentToken");
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        String accessToken = obtainAccessToken(CLIENT_DETAILS_ID, authorizationCode, redirectUri, "/orcid-works/create");
        assertFalse(PojoUtil.isEmpty(accessToken));
        OrcidOauth2TokenDetail tokenEntity = oauth2TokenDetailDao.findByTokenValue(accessToken);
        assertNotNull(tokenEntity);
        assertTrue(tokenEntity.isPersistent());        
        Date tokenExpiration = tokenEntity.getTokenExpiration();
        assertNotNull(tokenExpiration);
        Thread.sleep(2000);
        Date afterCreatingToken = twentyYearsTime();
        
        //confirm the token expires in 20 years
        assertTrue(tokenExpiration.after(beforeCreatingToken));
        assertTrue(tokenExpiration.before(afterCreatingToken));
    }
    
    @Test
    public void createNonPersistentToken() throws InterruptedException, JSONException {
        Date beforeCreatingToken = oneHoursTime();
        List<String> items = new ArrayList<String>();
        items.add("enablePersistentToken");       
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", items, false);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        String accessToken = obtainAccessToken(CLIENT_DETAILS_ID, authorizationCode, redirectUri, "/orcid-works/create");
        assertFalse(PojoUtil.isEmpty(accessToken));
        OrcidOauth2TokenDetail tokenEntity = oauth2TokenDetailDao.findByTokenValue(accessToken);
        assertNotNull(tokenEntity);
        assertFalse(tokenEntity.isPersistent());        
        Date tokenExpiration = tokenEntity.getTokenExpiration();
        assertNotNull(tokenExpiration);
        Thread.sleep(2000);
        Date afterCreatingToken = oneHoursTime();
        
        //confirm the token expires in 1 hour
        assertTrue(tokenExpiration.after(beforeCreatingToken));
        assertTrue(tokenExpiration.before(afterCreatingToken));
    }
    
    @Test
    public void persistentTokenCheckboxNotVisibleWhenPersistentTokensIsDisabledOnClient() {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, NO_PERSISTENT_TOKEN_CLIENT_DETAILS_ID, "/orcid-bio/read-limited", redirectUri));        

        // Switch to the login form
        By scopesUl = By.id("scopes-ul");
        By switchFromLinkLocator = By.id("enablePersistentToken");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(scopesUl));
        
        try {
            webDriver.findElement(switchFromLinkLocator);
            fail("Element enablePersistentToken should not be displayed");
        } catch(NoSuchElementException e) {
            
        }                                           
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
    
    private Date twentyYearsTime() {
        Calendar earliestExpiry = new GregorianCalendar();
        // This is roughly 2 years in seconds - used in the implementation, but
        // not sure how was calculated now.
        earliestExpiry.add(Calendar.SECOND, 631138519);
        return earliestExpiry.getTime();
    }
    
    private Date oneHoursTime() {
        Calendar earliestExpiry = new GregorianCalendar();
        earliestExpiry.add(Calendar.HOUR, 1);
        return earliestExpiry.getTime();
    }
}
