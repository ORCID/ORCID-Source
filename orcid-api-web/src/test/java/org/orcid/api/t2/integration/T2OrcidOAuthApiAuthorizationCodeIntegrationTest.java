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
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.AffiliationCity;
import org.orcid.jaxb.model.message.AffiliationCountry;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkTitle;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-t2-client-context.xml" })
/**
 * 
 * @author Will Simpson
 *
 */
public class T2OrcidOAuthApiAuthorizationCodeIntegrationTest extends DBUnitTest {

    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private static final String CLIENT_DETAILS_ID = "4444-4444-4444-4445";

    private WebDriver webDriver;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

    @Resource
    private ProfileDao profileDao;

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }

    @Before
    @Transactional
    public void before() {
        webDriver = new FirefoxDriver();
        redirectUri = webBaseUrl + "/oauth/playground";
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(CLIENT_DETAILS_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientRedirectDao.addClientRedirectUri(CLIENT_DETAILS_ID, redirectUri);
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
        String authorizationCode = obtainAuthorizationCode(scopes);
        String accessToken = obtainAccessToken(authorizationCode, scopes);

        ClientResponse bioResponse1 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4442", accessToken);
        assertEquals(200, bioResponse1.getStatus());
        OrcidMessage orcidMessage1 = bioResponse1.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage1);
        ExternalIdentifiers externalIdentifiers = orcidMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertEquals(Visibility.LIMITED, externalIdentifiers.getVisibility());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());

        ClientResponse bioResponse2 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4443", accessToken);
        assertEquals(403, bioResponse2.getStatus());
        OrcidMessage orcidMessage2 = bioResponse2.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage2);
    }

    @Test
    public void testAddWork() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String authorizationCode = obtainAuthorizationCode(scopes);
        String accessToken = obtainAccessToken(authorizationCode, scopes);

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
        WorkTitle workTitle = new WorkTitle();
        orcidWork.setWorkTitle(workTitle);
        workTitle.setTitle(new Title("Work added by integration test"));

        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddWorkV22() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String authorizationCode = obtainAuthorizationCode(scopes);
        String accessToken = obtainAccessToken(authorizationCode, scopes);

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
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work added by integration test - Version 22"));
        TranslatedTitle translatedTitle = new TranslatedTitle();
        translatedTitle.setContent("Trabajo añadido por una prueba de integración");
        translatedTitle.setLanguageCode("es_CR");
        workTitle.setTranslatedTitle(translatedTitle);

        orcidWork.setWorkTitle(workTitle);
        orcidWork.setJournalTitle(new Title("Journal Title"));
        orcidWork.setLanguageCode("en_US");

        Country country = new Country(Iso3166Country.US);
        orcidWork.setCountry(country);

        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddAffiliation() throws InterruptedException, JSONException {
        String scopes = "/affiliations/create";
        String authorizationCode = obtainAuthorizationCode(scopes);
        String accessToken = obtainAccessToken(authorizationCode, scopes);

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = new Affiliation();
        affiliations.getAffiliation().add(affiliation);
        affiliation.setAffiliationName("Affiliation added by integration test");
        AffiliationAddress affiliationAddress = new AffiliationAddress();
        affiliation.setAffiliationAddress(affiliationAddress);
        affiliationAddress.setAffiliationCity(new AffiliationCity("Edinburgh"));
        affiliationAddress.setAffiliationCountry(new AffiliationCountry(Iso3166Country.GB));

        ClientResponse clientResponse = oauthT2Client.addAffiliationsXml("4444-4444-4444-4442", orcidMessage, accessToken);
        assertEquals(201, clientResponse.getStatus());
    }

    @Test
    public void testAddWorkToWrongProfile() throws InterruptedException, JSONException {
        String scopes = "/orcid-works/create";
        String authorizationCode = obtainAuthorizationCode(scopes);
        String accessToken = obtainAccessToken(authorizationCode, scopes);

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
        WorkTitle workTitle = new WorkTitle();
        orcidWork.setWorkTitle(workTitle);
        workTitle.setTitle(new Title("Work added by integration test"));

        ClientResponse clientResponse = oauthT2Client.addWorksJson("4444-4444-4444-4443", orcidMessage, accessToken);
        assertEquals(403, clientResponse.getStatus());
    }

    private String obtainAccessToken(String authorizationCode, String scopes) throws JSONException {
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
        return accessToken;
    }

    private String obtainAuthorizationCode(String scopes) throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, CLIENT_DETAILS_ID, scopes, redirectUri));
        WebElement userId = webDriver.findElement(By.id("userId"));
        userId.sendKeys("michael@bentine.com");
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
}
