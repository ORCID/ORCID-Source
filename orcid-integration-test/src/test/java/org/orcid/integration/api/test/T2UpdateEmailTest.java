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
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
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
public class T2UpdateEmailTest extends DBUnitTest {        
    private static final String DEFAULT = "default";
    private static final String UPDATE_BIO_CLIENT_ID = "9999-9999-9999-9997";
    private static final String USER_TO_TEST = "9999-9999-9999-9989";
    
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
    
    
    @Resource(name = "t2OAuthClient1_2_rc6")
    private T2OAuthAPIService<ClientResponse> oauthT2Clientv1_2_rc6;

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
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(UPDATE_BIO_CLIENT_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(UPDATE_BIO_CLIENT_ID, redirectUri);
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
     * Test update email for a specific user
     * */
    @Test
    public void updateEmailsTest() throws JSONException, InterruptedException {
        String scopes = "/orcid-bio/update";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, UPDATE_BIO_CLIENT_ID);
        String accessToken = obtainAccessToken(UPDATE_BIO_CLIENT_ID, authorizationCode, redirectUri, scopes);
                        
        long time = System.currentTimeMillis();
        String emailName = time + "@update.com";
        ContactDetails contactDetails = new ContactDetails();        
        contactDetails.getEmail().add(new Email(emailName));
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setContactDetails(contactDetails);
        
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setType(OrcidType.USER);
        orcidProfile.setOrcidBio(orcidBio);
        
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        orcidMessage.setOrcidProfile(orcidProfile);
        
        ClientResponse clientResponse = oauthT2Clientv1_2_rc6.updateBioDetailsXml(USER_TO_TEST, orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
                
        clientResponse = oauthT2Clientv1_2_rc6.viewBioDetailsXml(USER_TO_TEST, accessToken);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage result = clientResponse.getEntity(OrcidMessage.class);
        
        // Check returning message
        assertNotNull(result);
        assertNotNull(result.getOrcidProfile());
        assertNotNull(result.getOrcidProfile().getOrcidBio());
        assertNotNull(result.getOrcidProfile().getOrcidBio().getContactDetails());
        assertNotNull(result.getOrcidProfile().getOrcidBio().getContactDetails().getEmail());
        assertEquals(1, result.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().size());        
    }
    
    /**
     * Test update email using already existing email
     * */
    @Test
    public void updateEmailsUsingAlreadyExistingEmailTest() throws JSONException, InterruptedException {
        String scopes = "/orcid-bio/update";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, UPDATE_BIO_CLIENT_ID);
        String accessToken = obtainAccessToken(UPDATE_BIO_CLIENT_ID, authorizationCode, redirectUri, scopes);
               
        //Email already used by 9999-9999-9999-9990
        String emailName = "group@user.com";
        ContactDetails contactDetails = new ContactDetails();        
        contactDetails.getEmail().add(new Email(emailName));
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setContactDetails(contactDetails);
        
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setType(OrcidType.USER);
        orcidProfile.setOrcidBio(orcidBio);
        
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        orcidMessage.setOrcidProfile(orcidProfile);
        
        ClientResponse clientResponse = oauthT2Clientv1_2_rc6.updateBioDetailsXml(USER_TO_TEST, orcidMessage, accessToken);
        assertEquals(400, clientResponse.getStatus());        
        OrcidMessage errorMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals("Bad Request: Invalid incoming message: Email group@user.com belongs to other user", errorMessage.getErrorDesc().getContent());
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
