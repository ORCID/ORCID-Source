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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
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
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class PerActivityAPIScope_Activities_Update_Test {

    private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-oauth-orcid-api-client-context.xml");
    
    private static String email;
    private static String password;
    private static OrcidProfile user;
    private static Group member;
    private static OrcidClient client;    
    
    @Resource
    private OauthHelper oauthHelper;
    
    @Resource
    OrcidOauth2TokenDetailDao oauth2TokenDetailDao;

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
        String webBaseUrl = (String) context.getBean("webBaseUrl");
        WebDriver webDriver = new FirefoxDriver();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, getRedirectUri());
        oauthHelper.setWebDriverHelper(webDriverHelper);
    }
    
    @After
    public void after() {
        oauthHelper.closeWebDriver();
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        InitializeDataHelper idh = (InitializeDataHelper) context.getBean("initializeDataHelper");
        idh.deleteClient(client.getClientId());
        idh.deleteProfile(member.getGroupOrcid().getValue());
        idh.deleteProfile(user.getOrcidIdentifier().getPath());     
    }

    @Test
    public void createNonPersistentTokenTest() throws InterruptedException, JSONException {
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/activities/update", email, password, getRedirectUri());
        assertFalse(PojoUtil.isEmpty(accessToken));
        OrcidOauth2TokenDetail tokenEntity = oauth2TokenDetailDao.findByTokenValue(accessToken);
        assertNotNull(tokenEntity);
        assertFalse(tokenEntity.isPersistent());
    }
    
    @Test
    public void createPersistentTokenTest() throws InterruptedException, JSONException {
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/activities/update", email, password, getRedirectUri(), true);
        assertFalse(PojoUtil.isEmpty(accessToken));
        OrcidOauth2TokenDetail tokenEntity = oauth2TokenDetailDao.findByTokenValue(accessToken);
        assertNotNull(tokenEntity);
        assertTrue(tokenEntity.isPersistent());
    }

    private static String getRedirectUri() {
        String webBaseUrl = (String) context.getBean("webBaseUrl");
        return webBaseUrl + "/oauth/playground";
    }    
}
