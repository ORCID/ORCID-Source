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
public class PublicOauthClientTest extends DBUnitTest {

    private static final String CLIENT_DETAILS_ID = "4444-4444-4444-4498";

    private static final String DEFAULT = "default";

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", 
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml");

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

    @Test
    public void testPublicClient() throws JSONException, InterruptedException {
        String scopes = "/authenticate";
        String authorizationCode = webDriverHelper.obtainAuthorizationCode(scopes, CLIENT_DETAILS_ID, "michael@bentine.com", "password");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", CLIENT_DETAILS_ID);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "authorization_code");
        params.add("scope", scopes);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        ClientResponse clientResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        // Should get a 400 since public client should not use the members API
        assertEquals(400, clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String error = (String) jsonObject.get("error");
        String errorDescription = (String) jsonObject.get("error_description");
        assertEquals("invalid_request", error);
        assertEquals("Public members are not allowed to use the Members API", errorDescription);

    }

}
