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
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Amount;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributor;
import org.orcid.jaxb.model.message.FundingContributorAttributes;
import org.orcid.jaxb.model.message.FundingContributorRole;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class PerActivityAPINewScopesTest {

    private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-oauth-orcid-api-client-context.xml");
    
    private static String email;
    private static String password;
    private static OrcidProfile user;
    private static Group member;
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
        idh.deleteProfile(user.getOrcidIdentifier().getPath());
        idh.deleteClient(client.getClientId());
        idh.deleteProfile(member.getGroupOrcid().getValue());             
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

    @Test
    public void addWorkTest() throws Exception {
        String userOrcid = user.getOrcidIdentifier().getPath();
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/orcid-works/read-limited /activities/update", email, password, getRedirectUri(), true);
        String workTitle = addWork(userOrcid, accessToken);
        
        ClientResponse response = oauthT2Client.viewWorksDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", response.getType().toString());
        OrcidMessage orcidMessageWithNewWork = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(1, orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle());
        assertEquals(workTitle, orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent());
        
    }
    
    @Test
    public void addFundingTest() throws InterruptedException, JSONException {
        String userOrcid = user.getOrcidIdentifier().getPath(); 
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/funding/read-limited /activities/update", email, password, getRedirectUri(), true);
        String fundingTitle = addFunding(userOrcid, accessToken);
        
        ClientResponse response = oauthT2Client.viewFundingDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", response.getType().toString());
        OrcidMessage orcidMessageWithNewWork = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(1, orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings().get(0).getTitle());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings().get(0).getTitle().getTitle());
        assertEquals(fundingTitle, orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getFundings().getFundings().get(0).getTitle().getTitle().getContent());
    }
        
    @Test
    public void addAffiliationTest() throws InterruptedException, JSONException {
        String userOrcid = user.getOrcidIdentifier().getPath();
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/affiliations/read-limited /activities/update", email, password, getRedirectUri(), true);
        String orgName = addAffiliation(userOrcid, accessToken);
        
        ClientResponse response = oauthT2Client.viewAffiliationDetailsXml(userOrcid, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", response.getType().toString());
        OrcidMessage orcidMessageWithNewWork = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(1, orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().get(0).getOrganization());
        assertEquals(orgName, orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().get(0).getOrganization().getName());
    }
    
    @Test
    public void personUpdateTest() throws InterruptedException, JSONException {
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/person/update /orcid-bio/read-limited", email, password, getRedirectUri(), true);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidBio orcidBio = new OrcidBio();
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setGivenNames(new GivenNames("My given name"));
        personalDetails.setFamilyName(new FamilyName("My family name"));
        CreditName creditName = new CreditName("My credit name");
        creditName.setVisibility(Visibility.LIMITED);
        personalDetails.setCreditName(creditName);
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);
        
        ClientResponse clientResponse = oauthT2Client.updateBioDetailsXml(user.getOrcidIdentifier().getPath(), orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
        ClientResponse response = oauthT2Client.viewBioDetailsXml(user.getOrcidIdentifier().getPath(), accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage orcidMessageWithBio = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithBio);
        assertNotNull(orcidMessageWithBio.getOrcidProfile());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames());
        assertEquals("My given name", orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName());
        assertEquals("My family name", orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName());
        assertEquals("My credit name", orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());        
    }
    
    @Test
    public void externalIdentifiersUpdateTest() throws InterruptedException, JSONException {
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/person/update /orcid-bio/read-limited", email, password, getRedirectUri(), true);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();        
        OrcidBio orcidBio = new OrcidBio();
        ExternalIdentifier extId = new ExternalIdentifier();
        extId.setExternalIdCommonName(new ExternalIdCommonName("ext-id-common-name"));
        extId.setExternalIdReference(new ExternalIdReference("ext-id-reference"));
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        extIds.getExternalIdentifier().add(extId);
        orcidBio.setExternalIdentifiers(extIds);
        orcidProfile.setOrcidBio(orcidBio);
        orcidMessage.setOrcidProfile(orcidProfile);
        ClientResponse clientResponse = oauthT2Client.addExternalIdentifiersXml(user.getOrcidIdentifier().getPath(), orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
        ClientResponse response = oauthT2Client.viewBioDetailsXml(user.getOrcidIdentifier().getPath(), accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage orcidMessageWithExtIds = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithExtIds);
        assertNotNull(orcidMessageWithExtIds.getOrcidProfile());
        assertNotNull(orcidMessageWithExtIds.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessageWithExtIds.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
        assertEquals(1, orcidMessageWithExtIds.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        ExternalIdentifier extIdFromProfile = orcidMessageWithExtIds.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(extIdFromProfile);
        assertNotNull(extIdFromProfile.getExternalIdCommonName());
        assertEquals("ext-id-common-name", extIdFromProfile.getExternalIdCommonName().getContent());
        assertNotNull(extIdFromProfile.getExternalIdReference());
        assertEquals("ext-id-reference", extIdFromProfile.getExternalIdReference().getContent());
    }
    
    @Test
    public void activitiesReadLimitedTest() throws InterruptedException, JSONException {
        String userOrcid = user.getOrcidIdentifier().getPath();
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), client.getClientSecret(), "/activities/read-limited /activities/update", email, password, getRedirectUri(), true);
        String workTitle = addWork(userOrcid, accessToken);
        String fundingTitle = addFunding(userOrcid, accessToken);
        String orgName = addAffiliation(userOrcid, accessToken);
        
        ClientResponse worksResponse = oauthT2Client.viewWorksDetailsXml(user.getOrcidIdentifier().getPath(), accessToken);
        assertNotNull(worksResponse);
        assertEquals(200, worksResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", worksResponse.getType().toString());
        OrcidMessage orcidMessageWithNewWork = worksResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertTrue(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size() > 0);
        
        boolean workFound = false;
        
        for(OrcidWork work : orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork()) {
            assertNotNull(work.getWorkTitle());
            assertNotNull(work.getWorkTitle().getTitle());
            if(workTitle.equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
                workFound = true;
            }
        }
         
        assertTrue(workFound);
        
        ClientResponse fundingResponse = oauthT2Client.viewFundingDetailsXml(userOrcid, accessToken);
        assertNotNull(fundingResponse);
        assertEquals(200, fundingResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", fundingResponse.getType().toString());
        OrcidMessage orcidMessageWithNewFunding = fundingResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewFunding);
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile());
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertTrue(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size() > 0);
        
        boolean fundingFound = false;
        
        for(Funding funding : orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings().getFundings()) {
            assertNotNull(funding.getTitle());
            assertNotNull(funding.getTitle().getTitle());
            if(fundingTitle.equals(funding.getTitle().getTitle().getContent())) {
                assertEquals(Visibility.LIMITED, funding.getVisibility());
                fundingFound = true;
            }
        }
        
        assertTrue(fundingFound);
        
        ClientResponse affiliationResponse = oauthT2Client.viewAffiliationDetailsXml(userOrcid, accessToken);
        assertNotNull(affiliationResponse);
        assertEquals(200, affiliationResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", affiliationResponse.getType().toString());
        OrcidMessage orcidMessageWithNewAffiliation = affiliationResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewAffiliation);
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile());
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertTrue(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size() > 0);
        
        boolean affiliationFound = false;
        
        for(Affiliation affiliation : orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation()) {
            if(orgName.equals(affiliation.getOrganization().getName())) {
                assertEquals(Visibility.LIMITED, affiliation.getVisibility());
                affiliationFound = true;
            }
        }
        
        assertTrue(affiliationFound);
    }
    
    private String addWork(String userOrcid, String token) {
        String id = String.valueOf(System.currentTimeMillis());
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        orcidWork.setWorkType(WorkType.JOURNAL_ARTICLE);
        orcidWork.setVisibility(Visibility.LIMITED);
        WorkTitle workTitle = new WorkTitle();
        orcidWork.setWorkTitle(workTitle);
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId(id));
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        WorkExternalIdentifiers extIds = new WorkExternalIdentifiers();
        extIds.getWorkExternalIdentifier().add(wei);
        orcidWork.setWorkExternalIdentifiers(extIds);
        workTitle.setTitle(new Title("Work added by integration test: " + id));
        ClientResponse clientResponse = oauthT2Client.addWorksXml(userOrcid, orcidMessage, token);
        assertEquals(201, clientResponse.getStatus());
        return workTitle.getTitle().getContent();
    }
    
    private String addFunding(String userOrcid, String token) {
        String id = String.valueOf(System.currentTimeMillis());
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        funding.setVisibility(Visibility.LIMITED);
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("Funding added by integration test: " + id));
        funding.setTitle(fundingTitle);
        funding.setType(FundingType.SALARY_AWARD);        
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1,250,000");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription("My Grant description");
        funding.setUrl(new Url("http://url.com"));
        Organization org = new Organization();
        org.setName("Orcid Integration Test Org");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("My City");
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingExternalIdentifier extIdentifier = new FundingExternalIdentifier();
        extIdentifier.setType(FundingExternalIdentifierType.fromValue("grant_number"));
        extIdentifier.setUrl(new Url("http://url.com"));
        extIdentifier.setValue("My value");
        FundingExternalIdentifiers extIdentifiers = new FundingExternalIdentifiers();
        extIdentifiers.getFundingExternalIdentifier().add(extIdentifier);
        funding.setFundingExternalIdentifiers(extIdentifiers);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName("My Credit Name"));
        contributor.setContributorEmail(new ContributorEmail("my.email@orcid-integration-test.com"));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);
        
        ClientResponse clientResponse = oauthT2Client.addFundingXml(userOrcid, orcidMessage, token);
        assertEquals(201, clientResponse.getStatus());
        return fundingTitle.getTitle().getContent();
    }
    
    private String addAffiliation(String userOrcid, String token) {
        String id = String.valueOf(System.currentTimeMillis());
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = new Affiliation();
        affiliation.setVisibility(Visibility.LIMITED);
        affiliations.getAffiliation().add(affiliation);
        affiliation.setType(AffiliationType.EDUCATION);
        Organization organization = new Organization();
        affiliation.setOrganization(organization);
        organization.setName("Affiliation added by integration test: " + id);
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);
        
        ClientResponse clientResponse = oauthT2Client.addAffiliationsXml(userOrcid, orcidMessage, token);
        assertEquals(201, clientResponse.getStatus());
        return organization.getName();
    }
    
    private static String getRedirectUri() {
        String webBaseUrl = (String) context.getBean("webBaseUrl");
        return webBaseUrl + "/oauth/playground";
    }    
}
