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

import javax.annotation.Resource;

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
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class LockRecordTest extends IntegrationTestBase {

    private static String email;
    private static String password;
    private static OrcidProfile user;
    private static Member member;
    private static OrcidClient client; 
    
    @Resource(name="t2OAuthClient1_2")
    protected T2OAuthAPIService<ClientResponse> oauthT2Client;

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
            member = idh.createMember(MemberType.BASIC);
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
    
    @Test
    public void unlockedToLockedTest() throws Exception {
        String webBaseUrl = (String) context.getBean("webBaseUrl");
        String userOrcid = user.getOrcidIdentifier().getPath();
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/orcid-profile/read-limited /activities/read-limited /activities/update", email, password, getRedirectUri(), true);
        addWork(userOrcid, accessToken);
        addFunding(userOrcid, accessToken);
        
        //Unlock profile
        InitializeDataHelper idh = (InitializeDataHelper) context.getBean("initializeDataHelper");
        idh.unlockProfile(userOrcid);
        
        //Check the full details contains the info
        ClientResponse response = oauthT2Client.viewFullDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        
        //Check the public page doesnt show the locked error
        assertFalse(oauthHelper.elementExists(webBaseUrl + '/' + userOrcid, "error_locked"));
        
        //Lock profile        
        idh.lockProfile(userOrcid);
        
        //Check the full details returns error
        response = oauthT2Client.viewFullDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(409, response.getStatus());
        
        //Check the public page doesnt show the locked error
        assertTrue(oauthHelper.elementExists(webBaseUrl + '/' + userOrcid, "error_locked"));
    }
    
    @Test
    public void lockedToUnlockedTest() throws Exception {
        String webBaseUrl = (String) context.getBean("webBaseUrl");
        String userOrcid = user.getOrcidIdentifier().getPath();
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/orcid-profile/read-limited /activities/read-limited /activities/update", email, password, getRedirectUri(), true);
        String workTitle = addWork(userOrcid, accessToken);
        String fundingTitle = addFunding(userOrcid, accessToken);                
        
        //Lock profile
        InitializeDataHelper idh = (InitializeDataHelper) context.getBean("initializeDataHelper");
        idh.lockProfile(userOrcid);
        
        //Check the locked record return error
        ClientResponse response = oauthT2Client.viewFullDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(409, response.getStatus());
        
        //Check the public page show the locked error
        assertTrue(oauthHelper.elementExists(webBaseUrl + '/' + userOrcid, "error_locked"));
        
        //Unlock profile
        idh.unlockProfile(userOrcid);
        
        //Check the unlocked error returns the full details
        response = oauthT2Client.viewFullDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());        
        OrcidMessage orcidMessageWithNewWork = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertFalse(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().isEmpty());
        
        boolean workFound = false;
        for(OrcidWork work : orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork()) {
            assertNotNull(work.getWorkTitle());
            assertNotNull(work.getWorkTitle().getTitle());
            if(work.getWorkTitle().getTitle().getContent().equals(workTitle)) {
                workFound = true;
                break;
            }
        }
        
        assertTrue(workFound);
        
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertFalse(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings().isEmpty());  
        
        boolean fundingFound = false;
        for(Funding funding : orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings()) {
            assertNotNull(funding.getTitle());
            assertNotNull(funding.getTitle().getTitle());
            if(funding.getTitle().getTitle().getContent().equals(fundingTitle)) {
                fundingFound = true;
                break;
            }
        }
        
        assertTrue(fundingFound);
        
        //Check the public page doesnt display the locked error message
        assertFalse(oauthHelper.elementExists(webBaseUrl + '/' + userOrcid, "error_locked"));
    }
    
    @Test
    public void lockedProfileReturnErrorOnRead() throws Exception {        
        String userOrcid = user.getOrcidIdentifier().getPath();
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/person/read-limited /activities/read-limited", email, password, getRedirectUri(), true);
        
        InitializeDataHelper idh = (InitializeDataHelper) context.getBean("initializeDataHelper");
        idh.lockProfile(userOrcid);
        
        //Check bio details returns error
        ClientResponse response = oauthT2Client.viewBioDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(409, response.getStatus());
        
        //Check work details returns error
        response = oauthT2Client.viewWorksDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(409, response.getStatus());
        
        //Check affiliation details returns error
        response = oauthT2Client.viewAffiliationDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(409, response.getStatus());
        
        //Check funding details returns error
        response = oauthT2Client.viewFundingDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(409, response.getStatus());
    }
    
}
