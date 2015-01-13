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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
public class OauthAuthorizationCodeTest extends DBUnitTest {
    private static final String CLIENT_DETAILS_ID = "4444-4444-4444-4445";
    private static final String DEFAULT = "default";
    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;
    
    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;
        
    @Value("${org.orcid.core.oauth.auth_code.expiration_minutes:1440}")
    private int authorizationCodeExpiration;

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
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
    public void useAuthorizationCodeWithValidScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", new ArrayList<String>(), true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = obtainAccessTokenResponse(CLIENT_DETAILS_ID, authorizationCode, redirectUri, "/orcid-works/create");
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void useAuthorizationCodeWithInalidScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", new ArrayList<String>(), true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = obtainAccessTokenResponse(CLIENT_DETAILS_ID, authorizationCode, redirectUri, "/orcid-works/update");
        assertEquals(401, tokenResponse.getStatus());  
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("OAuth2 problem : Invalid scopes: /orcid-works/update available scopes for this code are: [/orcid-works/create]", result.getErrorDesc().getContent());
    }
    
    @Test
    public void useAuthorizationCodeWithoutScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", new ArrayList<String>(), true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = obtainAccessTokenResponse(CLIENT_DETAILS_ID, authorizationCode, redirectUri, null);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void useClientCredentialsGrantTypeScope() throws InterruptedException, JSONException {
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", new ArrayList<String>(), true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = obtainAccessTokenResponse(CLIENT_DETAILS_ID, authorizationCode, redirectUri, "/orcid-works/create /webhook");
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-works/create", scope);
    }
    
    @Test
    public void authorizationCodeExpiresAfterXMinutesTest() throws InterruptedException, JSONException {
        String authorizationCode = webDriverHelper.obtainAuthorizationCode("/orcid-works/create", CLIENT_DETAILS_ID, "michael@bentine.com", "password", new ArrayList<String>(), true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity = orcidOauth2AuthoriziationCodeDetailDao.find(authorizationCode);
        Date dateCreated = authorizationCodeEntity.getDateCreated();
        Calendar c = Calendar.getInstance();
        c.setTime(dateCreated);
        c.add(Calendar.MINUTE, (-authorizationCodeExpiration - 1) );
        dateCreated = c.getTime();
        authorizationCodeEntity.setDateCreated(dateCreated);
        orcidOauth2AuthoriziationCodeDetailDao.merge(authorizationCodeEntity);
        
        ClientResponse tokenResponse = obtainAccessTokenResponse(CLIENT_DETAILS_ID, authorizationCode, redirectUri, "/orcid-works/create /webhook");
        assertEquals(400, tokenResponse.getStatus());
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("Bad Request : Authorization code has expired", result.getErrorDesc().getContent());
        
    }
    
    private ClientResponse obtainAccessTokenResponse(String clientId, String authorizationCode, String redirectUri, String scopes) throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", "client-secret");        
        params.add("grant_type", "authorization_code");        
        if(scopes != null)
            params.add("scope", scopes);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        return oauthT2Client.obtainOauth2TokenPost("authorization_code", params);        
    }
}
