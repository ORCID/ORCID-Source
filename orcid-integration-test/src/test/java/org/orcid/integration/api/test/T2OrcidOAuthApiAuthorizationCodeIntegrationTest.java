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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
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
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.integration.api.helper.TokenStore;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Amount;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
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
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OrganizationDefinedFundingSubType;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
/**
 * 
 * @author Will Simpson
 *
 */
public class T2OrcidOAuthApiAuthorizationCodeIntegrationTest extends DBUnitTest {

    protected static String CLIENT_DETAILS_ID = "4444-4444-4444-4445";

    public static final String FUNDING_TITLE = "Grant Title # 1";
    public static final String FUNDING_TITLE_2 = "Grant Title # 2";
    public static final String GRANT_DESCRIPTION = "A short description";
    public static final String GRANT_URL = "http://myurl.com";
    public static final String ORG_NAME = "My Org";
    public static final String ORG_CITY = "My City";
    public static final String EXT_ID_TYPE = "grant_number";
    public static final String EXT_ID_URL = "http://ext.id.url";
    public static final String EXT_ID_VALUE = "ext id value";
    public static final String CONTRIBUTOR_CREDIT_NAME = "My Credit Name";
    public static final String CONTRIBUTOR_EMAIL = "my.email@contributor.com";

    public static final String USER = "michael@bentine.com";
    public static final String PASSWORD = "password";

    private static final String DEFAULT = "default";

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

    @Resource(name = "t2OAuthClient1_0_22")
    private T2OAuthAPIService<ClientResponse> oauthT2Client1_0_22;

    @Resource(name = "t2OAuthClient1_2_rc2")
    private T2OAuthAPIService<ClientResponse> oauthT2Client1_2_rc2;

    @Resource(name = "t2OAuthClient1_2_rc5")
    private T2OAuthAPIService<ClientResponse> oauthT2Client1_2_rc5;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private WorkDao workDao;

    @Resource
    OrcidSSOManager ssoManager;

    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");

    public List<TokenStore> tokens = new ArrayList<TokenStore>();

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
    public void testGetBioReadLimited() throws JSONException, InterruptedException {
        String scopes = "/orcid-bio/read-limited";
        String accessToken = obtainAccessToken(scopes);

        ClientResponse bioResponse1 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4442", accessToken);
        assertEquals(200, bioResponse1.getStatus());
        OrcidMessage orcidMessage1 = bioResponse1.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage1);
        ExternalIdentifiers externalIdentifiers = orcidMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertEquals(Visibility.LIMITED, externalIdentifiers.getVisibility());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());

        ClientResponse bioResponse2 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4443", accessToken);
        assertEquals(403, bioResponse2.getStatus());
        OrcidMessage orcidMessage2 = bioResponse2.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage2);
    }

    @Test
    public void testGetBioReadLimitedWhenAlreadySignedIn() throws JSONException, InterruptedException {
        String scopes = "/orcid-bio/read-limited";
        webDriverHelper.signIn("michael@bentine.com", "password");
        String accessToken = obtainAccessToken(scopes, true);

        ClientResponse bioResponse1 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4442", accessToken);
        assertEquals(200, bioResponse1.getStatus());
        OrcidMessage orcidMessage1 = bioResponse1.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage1);
        ExternalIdentifiers externalIdentifiers = orcidMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertEquals(Visibility.LIMITED, externalIdentifiers.getVisibility());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());

        ClientResponse bioResponse2 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4443", accessToken);
        assertEquals(403, bioResponse2.getStatus());
        OrcidMessage orcidMessage2 = bioResponse2.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage2);
    }

    @Test
    public void testGetAuthenticate() throws JSONException, InterruptedException {
        String scopes = "/authenticate";
        String accessToken = obtainAccessToken(scopes);
        assertNotNull(accessToken);
        assertTrue(StringUtils.isNotBlank(accessToken));
    }

    @Test
    public void testInvalidCodesFail() throws JSONException, InterruptedException {
        String scopes = "/orcid-bio/update";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, CLIENT_DETAILS_ID, USER, PASSWORD);
        String wrongScope = "/myscope";
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", CLIENT_DETAILS_ID);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "authorization_code");
        params.add("scope", wrongScope);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        ClientResponse tokenResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(401, tokenResponse.getStatus());
        OrcidMessage errorMessage = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals(
                "OAuth2 problem : One of the provided scopes is not allowed. Please refer to the list of allowed scopes at: http://support.orcid.org/knowledgebase/articles/120162-orcid-scopes.",
                errorMessage.getErrorDesc().getContent());
    }

    @Test
    public void dontFailWithGrantScopes() throws JSONException, InterruptedException {
        String scopes = "/funding/read-limited";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, CLIENT_DETAILS_ID, USER, PASSWORD);
        String wrongScope = "/orcid-grants/read-limited";
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", CLIENT_DETAILS_ID);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "authorization_code");
        params.add("scope", wrongScope);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        ClientResponse tokenResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, tokenResponse.getStatus());
    }

    @Test
    public void testAddWork() throws InterruptedException, JSONException {
        String orcid = "4444-4444-4444-4442";
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

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
        WorkTitle workTitle = new WorkTitle();
        orcidWork.setWorkTitle(workTitle);
        String title = "Work added by integration test " + System.currentTimeMillis();
        workTitle.setTitle(new Title(title));

        orcidWork.setWorkExternalIdentifiers(generateWorkExternalIdentifiers());
        
        ClientResponse clientResponse = oauthT2Client.addWorksJson(orcid, orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
        List<MinimizedWorkEntity> works = workDao.findWorks(orcid, 0L);
        assertNotNull(works);
        long workId = -1;
        for (MinimizedWorkEntity work : works) {
            if (work.getTitle().equals(title)) {
                workId = work.getId();
            }
        }

        assertFalse(workId == -1);
        WorkEntity work = workDao.find(workId);
        assertNotNull(work);
        assertNotNull(work.getProfile());
        assertEquals(orcid, work.getProfile().getId());
        assertNotNull(work.getSource());
        assertEquals(CLIENT_DETAILS_ID, work.getSource().getSourceId());
        assertNotNull(work.getVisibility());
        assertNotNull(work.getAddedToProfileDate());
    }

    @Test
    public void testAddWorkWithTranslatedTitleJournalTitleAndLanguageCode() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.0.22");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        orcidWork.setWorkType(WorkType.JOURNAL_ARTICLE);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work added by integration test - Version 22"));
        TranslatedTitle translatedTitle = new TranslatedTitle();
        translatedTitle.setContent("Trabajo añadido por una prueba de integración");
        translatedTitle.setLanguageCode("es_CR");
        workTitle.setTranslatedTitle(translatedTitle);

        orcidWork.setWorkTitle(workTitle);
        orcidWork.setJournalTitle(new Title("Journal Title"));
        orcidWork.setLanguageCode("en_US");
        orcidWork.setCountry(new Country(Iso3166Country.US));
        
        orcidWork.setWorkExternalIdentifiers(generateWorkExternalIdentifiers());        
        
        Country country = new Country(Iso3166Country.US);
        orcidWork.setCountry(country);

        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddWorkWithNewWorkTypesForV1_1() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.1");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work added by integration test - Version 23"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);

        orcidWork.setWorkExternalIdentifiers(generateWorkExternalIdentifiers());
        
        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddWorkWithEmptyTitle() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWork.setWorkType(WorkType.JOURNAL_ARTICLE);
        orcidWorks.getOrcidWork().add(orcidWork);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(""));

        orcidWork.setWorkTitle(workTitle);

        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(400, clientResponse.getStatus());
        OrcidMessage errorMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals("Bad Request: Invalid incoming message: org.orcid.core.exception.OrcidValidationException: Invalid Title: title cannot be null nor emtpy",
                errorMessage.getErrorDesc().getContent());
    }

    @Test
    public void testAddWorkWithoutExtIdsFor1_2rc5() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc5");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work added by integration test - Version 1.2_rc5"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);

        ClientResponse clientResponse = oauthT2Client1_2_rc5.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(400, clientResponse.getStatus());
        OrcidMessage errorMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals(
                "Bad Request: Invalid incoming message: org.orcid.core.exception.OrcidValidationException: Invalid work: Works added using message version 1.2_rc5 or greater must contain at least one external identifier",
                errorMessage.getErrorDesc().getContent());
    }

    @Test
    public void testAddWorkWithoutExtIdsForLowerThan1_2rc5() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc2");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work added by integration test - Version 1.2_rc2"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);

        ClientResponse clientResponse = oauthT2Client1_2_rc2.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddWorkWithExtIdsFor1_2rc5() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc5");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work added by integration test - Version 1.2_rc5"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        WorkExternalIdentifier extId = new WorkExternalIdentifier();
        extId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("extId"));
        extId.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue("isbn"));
        WorkExternalIdentifiers extIds = new WorkExternalIdentifiers();
        extIds.getWorkExternalIdentifier().add(extId);
        orcidWork.setWorkExternalIdentifiers(extIds);

        ClientResponse clientResponse = oauthT2Client1_2_rc5.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddAffiliation() throws InterruptedException, JSONException {
        String scopes = "/affiliations/create /affiliations/read-limited";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc2");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = new Affiliation();
        affiliations.getAffiliation().add(affiliation);
        affiliation.setType(AffiliationType.EDUCATION);
        Organization organization = new Organization();
        affiliation.setOrganization(organization);
        organization.setName("Affiliation added by integration test");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);

        ClientResponse clientResponse = oauthT2Client1_2_rc2.addAffiliationsXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());

        ClientResponse resultResponse = oauthT2Client1_2_rc2.viewAffiliationDetailsXml("4444-4444-4444-4442", accessToken);
        assertEquals(200, resultResponse.getStatus());
        OrcidMessage resultMessage = resultResponse.getEntity(OrcidMessage.class);
        assertNotNull(resultMessage);
        Affiliation resultAffiliation = resultMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().get(0);
        assertEquals(CLIENT_DETAILS_ID, resultAffiliation.retrieveSourcePath());
    }

    @Test
    public void testAddFunding() throws InterruptedException, JSONException {
        String scopes = "/funding/create /funding/read-limited";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc2");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(FUNDING_TITLE));
        funding.setTitle(title);
        funding.setType(FundingType.SALARY_AWARD);
        funding.setVisibility(Visibility.PUBLIC);
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1,250,000");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription(GRANT_DESCRIPTION);
        funding.setUrl(new Url(GRANT_URL));
        Organization org = new Organization();
        org.setName(ORG_NAME);
        OrganizationAddress add = new OrganizationAddress();
        add.setCity(ORG_CITY);
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingExternalIdentifier extIdentifier = new FundingExternalIdentifier();
        extIdentifier.setType(FundingExternalIdentifierType.fromValue(EXT_ID_TYPE));
        extIdentifier.setUrl(new Url(EXT_ID_URL));
        extIdentifier.setValue(EXT_ID_VALUE);
        FundingExternalIdentifiers extIdentifiers = new FundingExternalIdentifiers();
        extIdentifiers.getFundingExternalIdentifier().add(extIdentifier);
        funding.setFundingExternalIdentifiers(extIdentifiers);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName(CONTRIBUTOR_CREDIT_NAME));
        contributor.setContributorEmail(new ContributorEmail(CONTRIBUTOR_EMAIL));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);

        ClientResponse clientResponse = oauthT2Client1_2_rc2.addFundingXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());

        ClientResponse resultResponse = oauthT2Client1_2_rc2.viewFundingDetailsXml("4444-4444-4444-4442", accessToken);
        assertEquals(200, resultResponse.getStatus());
        OrcidMessage resultMessage = resultResponse.getEntity(OrcidMessage.class);
        assertNotNull(resultMessage);
        Funding resultFunding = resultMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().get(0);
        assertEquals(CLIENT_DETAILS_ID, resultFunding.retrieveSourcePath());
    }

    @Test
    public void testAddFundingWithOrganizationDefinedFundingType() throws InterruptedException, JSONException {
        String scopes = "/funding/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc5");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(FUNDING_TITLE_2));
        funding.setTitle(title);
        funding.setType(FundingType.SALARY_AWARD);
        funding.setOrganizationDefinedFundingType(new OrganizationDefinedFundingSubType("My Org Type"));
        funding.setVisibility(Visibility.PUBLIC);
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1,250,000");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription(GRANT_DESCRIPTION);
        funding.setUrl(new Url(GRANT_URL));
        Organization org = new Organization();
        org.setName(ORG_NAME);
        OrganizationAddress add = new OrganizationAddress();
        add.setCity(ORG_CITY);
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingExternalIdentifier extIdentifier = new FundingExternalIdentifier();
        extIdentifier.setType(FundingExternalIdentifierType.fromValue(EXT_ID_TYPE));
        extIdentifier.setUrl(new Url(EXT_ID_URL));
        extIdentifier.setValue(EXT_ID_VALUE);
        FundingExternalIdentifiers extIdentifiers = new FundingExternalIdentifiers();
        extIdentifiers.getFundingExternalIdentifier().add(extIdentifier);
        funding.setFundingExternalIdentifiers(extIdentifiers);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName(CONTRIBUTOR_CREDIT_NAME));
        contributor.setContributorEmail(new ContributorEmail(CONTRIBUTOR_EMAIL));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);

        ClientResponse clientResponse = oauthT2Client1_2_rc5.addFundingXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddFundingWithIncorrectFormat() throws InterruptedException, JSONException {
        String scopes = "/funding/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc2");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(FUNDING_TITLE));
        funding.setTitle(title);
        funding.setType(FundingType.SALARY_AWARD);
        funding.setVisibility(Visibility.PUBLIC);
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1.250.000");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription(GRANT_DESCRIPTION);
        funding.setUrl(new Url(GRANT_URL));
        Organization org = new Organization();
        org.setName(ORG_NAME);
        OrganizationAddress add = new OrganizationAddress();
        add.setCity(ORG_CITY);
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingExternalIdentifier extIdentifier = new FundingExternalIdentifier();
        extIdentifier.setType(FundingExternalIdentifierType.fromValue(EXT_ID_TYPE));
        extIdentifier.setUrl(new Url(EXT_ID_URL));
        extIdentifier.setValue(EXT_ID_VALUE);
        FundingExternalIdentifiers extIdentifiers = new FundingExternalIdentifiers();
        extIdentifiers.getFundingExternalIdentifier().add(extIdentifier);
        funding.setFundingExternalIdentifiers(extIdentifiers);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName(CONTRIBUTOR_CREDIT_NAME));
        contributor.setContributorEmail(new ContributorEmail(CONTRIBUTOR_EMAIL));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);

        ClientResponse clientResponse = oauthT2Client1_2_rc2.addFundingXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(400, clientResponse.getStatus());
        OrcidMessage errorMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals("Bad Request : The amount: 1.250.000 doesn'n have the right format, it should use the format: 1,234,567.89", errorMessage.getErrorDesc()
                .getContent());
    }

    @Test
    public void testAddFundingWithExtIdsFor1_2rc5() throws InterruptedException, JSONException {
        String scopes = "/funding/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc3");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(FUNDING_TITLE));
        funding.setTitle(title);
        funding.setType(FundingType.SALARY_AWARD);
        funding.setVisibility(Visibility.PUBLIC);
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription(GRANT_DESCRIPTION);
        funding.setUrl(new Url(GRANT_URL));
        Organization org = new Organization();
        org.setName(ORG_NAME);
        OrganizationAddress add = new OrganizationAddress();
        add.setCity(ORG_CITY);
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingExternalIdentifier extIdentifier = new FundingExternalIdentifier();
        extIdentifier.setType(FundingExternalIdentifierType.fromValue(EXT_ID_TYPE));
        extIdentifier.setUrl(new Url(EXT_ID_URL));
        extIdentifier.setValue(EXT_ID_VALUE);
        FundingExternalIdentifiers extIdentifiers = new FundingExternalIdentifiers();
        extIdentifiers.getFundingExternalIdentifier().add(extIdentifier);
        funding.setFundingExternalIdentifiers(extIdentifiers);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName(CONTRIBUTOR_CREDIT_NAME));
        contributor.setContributorEmail(new ContributorEmail(CONTRIBUTOR_EMAIL));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);

        ClientResponse clientResponse = oauthT2Client1_2_rc5.addFundingXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddFundingWithoutExtIdsFor1_2rc5() throws InterruptedException, JSONException {
        String scopes = "/funding/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc3");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(FUNDING_TITLE));
        funding.setTitle(title);
        funding.setType(FundingType.SALARY_AWARD);
        funding.setVisibility(Visibility.PUBLIC);
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription(GRANT_DESCRIPTION);
        funding.setUrl(new Url(GRANT_URL));
        Organization org = new Organization();
        org.setName(ORG_NAME);
        OrganizationAddress add = new OrganizationAddress();
        add.setCity(ORG_CITY);
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName(CONTRIBUTOR_CREDIT_NAME));
        contributor.setContributorEmail(new ContributorEmail(CONTRIBUTOR_EMAIL));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);

        ClientResponse clientResponse = oauthT2Client1_2_rc5.addFundingXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(400, clientResponse.getStatus());
        OrcidMessage errorMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals(
                "Bad Request: Invalid incoming message: org.orcid.core.exception.OrcidValidationException: Invalid funding: Funding added using message version 1.2_rc5 or greater must contain at least one external identifier",
                errorMessage.getErrorDesc().getContent());
    }

    @Test
    public void testAddFundingWithoutExtIdsForLowerThan1_2rc5() throws InterruptedException, JSONException {
        String scopes = "/funding/create";
        String accessToken = obtainAccessToken(scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc2");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(FUNDING_TITLE));
        funding.setTitle(title);
        funding.setType(FundingType.SALARY_AWARD);
        funding.setVisibility(Visibility.PUBLIC);
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription(GRANT_DESCRIPTION);
        funding.setUrl(new Url(GRANT_URL));
        Organization org = new Organization();
        org.setName(ORG_NAME);
        OrganizationAddress add = new OrganizationAddress();
        add.setCity(ORG_CITY);
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName(CONTRIBUTOR_CREDIT_NAME));
        contributor.setContributorEmail(new ContributorEmail(CONTRIBUTOR_EMAIL));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);

        ClientResponse clientResponse = oauthT2Client1_2_rc2.addFundingXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddWorkToWrongProfile() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String accessToken = obtainAccessToken(scopes);

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
        WorkTitle workTitle = new WorkTitle();
        orcidWork.setWorkTitle(workTitle);
        workTitle.setTitle(new Title("Work added by integration test"));

        orcidWork.setWorkExternalIdentifiers(generateWorkExternalIdentifiers());
        
        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4443", orcidMessage, accessToken);
        assertEquals(403, clientResponse.getStatus());
    }

    private String obtainAccessToken(String scopes) throws JSONException, InterruptedException {
        return obtainAccessToken(scopes, false);
    }
    
    private String obtainAccessToken(String scopes, boolean isLoggedIn) throws JSONException, InterruptedException {

        for (TokenStore token : tokens) {
            if (token.getScope().equals(scopes) && token.getUser().equals(USER) && token.getClientId().equals(CLIENT_DETAILS_ID)) {
                return token.getToken();
            }
        }

        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, CLIENT_DETAILS_ID, USER, PASSWORD, isLoggedIn);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", CLIENT_DETAILS_ID);
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

        TokenStore newToken = new TokenStore(CLIENT_DETAILS_ID, USER, scopes, accessToken);

        tokens.add(newToken);

        return accessToken;
    }

    private WorkExternalIdentifiers generateWorkExternalIdentifiers() {
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("ext-id-" + System.currentTimeMillis()));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        WorkExternalIdentifiers wExtIds = new WorkExternalIdentifiers();
        wExtIds.getWorkExternalIdentifier().add(wExtId);        
        return wExtIds;
    }
}
