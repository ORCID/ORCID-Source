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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
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
import org.orcid.integration.blackbox.api.BlackBoxWebDriver;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Amount;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
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
public class T2OrcidOAuthApiClientUpdatePrivateDataIntegrationTest extends DBUnitTest {

    private static final String DEFAULT = "default";

    private static final String READ_PRIVATE_WORKS_CLIENT_ID = "9999-9999-9999-9991";
    private static final String READ_PRIVATE_FUNDING_CLIENT_ID = "9999-9999-9999-9992";
    private static final String READ_PRIVATE_AFFILIATIONS_CLIENT_ID = "9999-9999-9999-9993";
    private static final String READ_ONLY_LIMITED_INFO_CLIENT_ID = "9999-9999-9999-9994";    

    private static final List<String> DATA_FILES = Arrays.asList("/group_client_data/EmptyEntityData.xml", "/group_client_data/SecurityQuestionEntityData.xml",
            "/group_client_data/ProfileEntityData.xml", "/group_client_data/WorksEntityData.xml", "/group_client_data/OrgsEntityData.xml",
            "/group_client_data/ClientDetailsEntityData.xml", "/group_client_data/OrgAffiliationEntityData.xml",
            "/group_client_data/ProfileFundingEntityData.xml");

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;
    
    
    @Resource(name = "t2OAuthClient1_2_rc5")
    private T2OAuthAPIService<ClientResponse> oauthT2Clientv1_2_rc5;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    @Transactional
    public void before() {
        webDriver = BlackBoxWebDriver.getWebDriver();
        redirectUri = webBaseUrl + "/oauth/playground";
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);

        // Set redirect uris if needed
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_WORKS_CLIENT_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_WORKS_CLIENT_ID, redirectUri);
        }        

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_FUNDING_CLIENT_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_FUNDING_CLIENT_ID, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_ONLY_LIMITED_INFO_CLIENT_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_ONLY_LIMITED_INFO_CLIENT_ID, redirectUri);
        }

        webDriver.get(webBaseUrl + "/signout");

        // Update last modified to force cache eviction (because DB unit deletes
        // a load of stuff from the DB, but reinserts profiles with older last
        // modified date)
        for (ProfileEntity profile : profileDao.getAll()) {
            profileDao.updateLastModifiedDateWithoutResult(profile.getId());
        }
    }
    
    /**
     * Check fetching information from a client that should have access to his
     * private works
     * */
    @Test
    public void testGetProfileWithOwnPrivateWorks() throws JSONException, InterruptedException {
        String scopes = "/orcid-works/update";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, READ_PRIVATE_WORKS_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_PRIVATE_WORKS_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Clientv1_2_rc5.viewWorksDetailsXml("9999-9999-9999-9989", accessToken);
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        
        OrcidWork myPrivateWork = null;
        boolean containMyPrivateWork = false;
        
        
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            if (putCode.equals("4")) {
                myPrivateWork = work;
                containMyPrivateWork = true;
                break;
            } 
        }

        if (!containMyPrivateWork)
            fail("Client doesnt have his private work with put code: 4");

        myPrivateWork.setCountry(new Country(Iso3166Country.CR));
        myPrivateWork.getWorkTitle().getTitle().setContent("Updated title");   
        WorkExternalIdentifier extId = new WorkExternalIdentifier();
        WorkExternalIdentifierId extIdId = new WorkExternalIdentifierId("updated-id"); 
        extId.setWorkExternalIdentifierId(extIdId);
        WorkExternalIdentifierType extIdType = WorkExternalIdentifierType.DOI; 
        extId.setWorkExternalIdentifierType(extIdType);
        WorkExternalIdentifiers extIds = new WorkExternalIdentifiers();
        extIds.getWorkExternalIdentifier().add(extId);
        myPrivateWork.setWorkExternalIdentifiers(extIds);        
        Visibility oldVisibility = myPrivateWork.getVisibility();
        myPrivateWork.setVisibility(Visibility.PUBLIC);
        
        oauthT2Clientv1_2_rc5.updateWorksXml("9999-9999-9999-9989", getUpdateOrcidMessage(myPrivateWork), accessToken);
        
        fullResponse1 = oauthT2Clientv1_2_rc5.viewWorksDetailsXml("9999-9999-9999-9989", accessToken);
        
        assertEquals(200, fullResponse1.getStatus());
        orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        
        containMyPrivateWork = false;
        
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            if (putCode.equals("4")) {
                assertEquals("Updated title", work.getWorkTitle().getTitle().getContent());
                assertNotNull(work.getWorkExternalIdentifiers());
                for(WorkExternalIdentifier updatedExtId : work.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                    assertEquals("updated-id", updatedExtId.getWorkExternalIdentifierId().getContent());
                    assertEquals(WorkExternalIdentifierType.DOI, updatedExtId.getWorkExternalIdentifierType());
                }
                //Check the visibility doesnt changed
                assertEquals(oldVisibility, work.getVisibility());
                containMyPrivateWork = true;
                break;
            } 
        }
        
        assertTrue(containMyPrivateWork);
    }
    
    /**
     * Check fetching information from a client that should have access to his
     * private funding
     * */
    @Test
    public void testGetProfileWithOwnPrivateFunding() throws JSONException, InterruptedException {
        String scopes = "/funding/update";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, READ_PRIVATE_FUNDING_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_PRIVATE_FUNDING_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Clientv1_2_rc5.viewFundingDetailsXml("9999-9999-9999-9989", accessToken);
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        
        Funding myPrivateFunding = null;
        boolean containMyPrivateFunding = false;        
        
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            if (putCode.equals("4")) {
                myPrivateFunding = funding;
                containMyPrivateFunding = true;
                break;
            } 
        }

        if (!containMyPrivateFunding)
            fail("Client doesnt have his private work with put code: 4");
        
        Visibility oldVisibility = myPrivateFunding.getVisibility();
        
        //Update funding info
        myPrivateFunding.setAmount(new Amount("123456789", "CRC"));
        myPrivateFunding.setDescription("Updated description");
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("Updated title"));
        myPrivateFunding.setTitle(fundingTitle);
        FundingExternalIdentifier fundingExtId = new FundingExternalIdentifier();
        fundingExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fundingExtId.setValue("funding_ext_id");
        fundingExtId.setUrl(new Url("http://test.com"));
        FundingExternalIdentifiers fundingExtIds = new FundingExternalIdentifiers();
        fundingExtIds.getFundingExternalIdentifier().add(fundingExtId);
        myPrivateFunding.setFundingExternalIdentifiers(fundingExtIds);
        myPrivateFunding.setVisibility(Visibility.PUBLIC);
        
        oauthT2Clientv1_2_rc5.updateFundingXml("9999-9999-9999-9989", getUpdateOrcidMessage(myPrivateFunding), accessToken);
        
        fullResponse1 = oauthT2Clientv1_2_rc5.viewFundingDetailsXml("9999-9999-9999-9989", accessToken);
        
        assertEquals(200, fullResponse1.getStatus());
        orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        
        containMyPrivateFunding = false; 
        
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            if (putCode.equals("4")) {
                containMyPrivateFunding = true;
                assertEquals("123456789", funding.getAmount().getContent());
                assertEquals("Updated description", funding.getDescription());
                assertNotNull(funding.getFundingExternalIdentifiers());
                assertNotNull(funding.getFundingExternalIdentifiers().getFundingExternalIdentifier());
                assertEquals("funding_ext_id", funding.getFundingExternalIdentifiers().getFundingExternalIdentifier().get(0).getValue());
                assertEquals(oldVisibility, funding.getVisibility());
            }
        }
        
        assertTrue(containMyPrivateFunding);
    }
    
    /**
     * Check fetching information from a client that should have access to his
     * private funding
     * */
    @Test
    public void testGetProfileWithOwnPrivateAffiliations() throws JSONException, InterruptedException {
        String scopes = "/affiliations/update";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, READ_PRIVATE_AFFILIATIONS_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, authorizationCode, redirectUri, scopes);
        
        ClientResponse fullResponse1 = oauthT2Clientv1_2_rc5.viewAffiliationDetailsXml("9999-9999-9999-9989", accessToken);
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());
        
        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        
        Affiliation myPrivateAffiliation = null;
        boolean containMyPrivateAffiliation = false;        
        
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            if (putCode.equals("4")) {
                myPrivateAffiliation = affiliation;
                containMyPrivateAffiliation = true;
                break;
            } 
        }

        if (!containMyPrivateAffiliation)
            fail("Client doesnt have his private work with put code: 4");
        
        Visibility oldVisibility = myPrivateAffiliation.getVisibility();
        
        // Update affiliation info
        myPrivateAffiliation.setDepartmentName("Updated dept name");
        myPrivateAffiliation.setRoleTitle("Updated role");
        myPrivateAffiliation.setType(AffiliationType.EDUCATION);
        myPrivateAffiliation.setVisibility(Visibility.PUBLIC);
        
        oauthT2Clientv1_2_rc5.updateAffiliationsXml("9999-9999-9999-9989", getUpdateOrcidMessage(myPrivateAffiliation), accessToken);
        
        fullResponse1 = oauthT2Clientv1_2_rc5.viewAffiliationDetailsXml("9999-9999-9999-9989", accessToken);
        assertEquals(200, fullResponse1.getStatus());
        orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());
        
        affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
                
        containMyPrivateAffiliation = false;    
        
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            if (putCode.equals("4")) {
                containMyPrivateAffiliation = true;
                assertEquals("Updated dept name", affiliation.getDepartmentName());
                assertEquals("Updated role", affiliation.getRoleTitle());
                assertEquals(AffiliationType.EDUCATION, affiliation.getType());
                assertEquals(oldVisibility, affiliation.getVisibility());
                break;
            }
        }
        
        assertTrue(containMyPrivateAffiliation);
    }
    
    private OrcidMessage getUpdateOrcidMessage(OrcidWork work) {        
        //Clear not needed info
        work.setCreatedDate(null);
        work.setLastModifiedDate(null);
        
        //Create orcid message
        OrcidMessage message = getEmptyOrcidMessage(); 
        OrcidWorks works = new OrcidWorks();
        works.getOrcidWork().add(work);
        message.getOrcidProfile().getOrcidActivities().setOrcidWorks(works);
        return message;
    }
    
    private OrcidMessage getUpdateOrcidMessage(Funding funding) {
        //Clear not needed info
        funding.setCreatedDate(null);
        funding.setLastModifiedDate(null);
        
        //Create orcid message
        OrcidMessage message = getEmptyOrcidMessage();        
        FundingList fundingList = new FundingList();
        fundingList.getFundings().add(funding);
        message.getOrcidProfile().getOrcidActivities().setFundings(fundingList);
        return message;
    }

    private OrcidMessage getUpdateOrcidMessage(Affiliation affiliation) {
        //Clear not needed info
        affiliation.setCreatedDate(null);
        affiliation.setLastModifiedDate(null);
        
        //Create orcid message
        OrcidMessage message = getEmptyOrcidMessage();
        Affiliations affiliations = new Affiliations();
        affiliations.getAffiliation().add(affiliation);
        message.getOrcidProfile().getOrcidActivities().setAffiliations(affiliations);
        return message;
    }
    
    private OrcidMessage getEmptyOrcidMessage() {
        OrcidMessage message = new OrcidMessage();   
        message.setMessageVersion("1.2_rc5");
        OrcidProfile profile = new OrcidProfile();
        OrcidActivities activities = new OrcidActivities();
        profile.setOrcidActivities(activities);
        message.setOrcidProfile(profile);
        return message;
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
