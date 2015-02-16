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

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

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
import org.orcid.integration.api.helper.InitializeDataHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
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

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class OauthAuthorizationCodeTest extends IntegrationTestBase {
    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;

    @Value("${org.orcid.core.oauth.auth_code.expiration_minutes:1440}")
    private int authorizationCodeExpiration;
    
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;

    private static String email;
    private static String password;
    private static OrcidProfile user;
    private static Group member;
    private static OrcidClient client; 
    
    @Resource
    private OauthHelper oauthHelper;

    @BeforeClass  
    public static void init() throws Exception {           
        InitializeDataHelper idh = (InitializeDataHelper) context.getBean("initializeDataHelper");
        if(PojoUtil.isEmpty(email))
            email = System.currentTimeMillis() + "@orcid-integration-test.com";
        if(PojoUtil.isEmpty(password))
            password = String.valueOf(System.currentTimeMillis());
        if(user == null)
            user = idh.createProfile(email, password);
        if(member == null)
            member = idh.createMember(GroupType.BASIC);
        if(client == null)
            client = idh.createClient(member.getGroupOrcid().getValue(), getRedirectUri());                
    }    

    @Before
    public void before() {
        webDriver = new FirefoxDriver();        
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, getRedirectUri());
        oauthHelper.setWebDriverHelper(webDriverHelper);
    }
    
    @After
    public void after() {
        webDriver.quit();
    }    
    
    @Test
    public void useAuthorizationCodeWithValidScopesTest() throws InterruptedException, JSONException {        
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/orcid-works/create", email, password, getRedirectUri(), true);        
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void useAuthorizationCodeWithInalidScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client.getClientId(), "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));        
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client.getClientId(), client.getClientSecret(), "/orcid-works/update", getRedirectUri(), authorizationCode);
        assertEquals(401, tokenResponse.getStatus());  
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("OAuth2 problem : Invalid scopes: /orcid-works/update available scopes for this code are: [/orcid-works/create]", result.getErrorDesc().getContent());
    }
    
    @Test
    public void useAuthorizationCodeWithoutScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client.getClientId(), "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client.getClientId(), client.getClientSecret(), null, getRedirectUri(), authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void useClientCredentialsGrantTypeScope() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client.getClientId(), "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client.getClientId(), client.getClientSecret(), "/orcid-works/create /webhook", getRedirectUri(), authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-works/create", scope);
    }
    
    @Test
    public void authorizationCodeExpiresAfterXMinutesTest() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client.getClientId(), "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        
        OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity = orcidOauth2AuthoriziationCodeDetailDao.find(authorizationCode);
        Date dateCreated = authorizationCodeEntity.getDateCreated();
        Calendar c = Calendar.getInstance();
        c.setTime(dateCreated);
        c.add(Calendar.MINUTE, (-authorizationCodeExpiration - 1) );
        dateCreated = c.getTime();
        authorizationCodeEntity.setDateCreated(dateCreated);
        orcidOauth2AuthoriziationCodeDetailDao.merge(authorizationCodeEntity);
        
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client.getClientId(), client.getClientSecret(), "/orcid-works/create /webhook", getRedirectUri(), authorizationCode);
        assertEquals(400, tokenResponse.getStatus());
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("Bad Request : Authorization code has expired", result.getErrorDesc().getContent());
        
    }    
}
