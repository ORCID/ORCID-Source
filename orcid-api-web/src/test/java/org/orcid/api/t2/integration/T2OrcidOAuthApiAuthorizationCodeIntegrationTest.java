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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ClientRedirectDao;
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

    @Resource
    private WebDriver webDriver;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    protected T2OAuthAPIService<ClientResponse> oauthT2Client;

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
        redirectUri = webBaseUrl + "/oauth/playground";
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(CLIENT_DETAILS_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientRedirectDao.addClientRedirectUri(CLIENT_DETAILS_ID, redirectUri);
        }
    }

    @Test
    public void testGetAuthorizationCode() throws JSONException {
        String authorizationCode = obtainAuthorizationCode();
        String accessToken = obtainAccessToken(authorizationCode);

        ClientResponse bioResponse1 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4442", accessToken);
        assertEquals(200, bioResponse1.getStatus());
        OrcidMessage orcidMessage1 = bioResponse1.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage1);
        ExternalIdentifiers externalIdentifiers = orcidMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertEquals(Visibility.LIMITED, externalIdentifiers.getVisibility());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());

        ClientResponse bioResponse2 = oauthT2Client.viewBioDetailsJson("4444-4444-4444-4443", accessToken);
        assertEquals(200, bioResponse2.getStatus());
        OrcidMessage orcidMessage2 = bioResponse2.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage2);
        assertNull("Shouldn't be able to see external identifiers for other profile", orcidMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
    }

    private String obtainAccessToken(String authorizationCode) throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", CLIENT_DETAILS_ID);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "authorization_code");
        params.add("scope", "/orcid-bio/read-limited");
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

    private String obtainAuthorizationCode() {
        webDriver.get(webBaseUrl + "/oauth/authorize?client_id=4444-4444-4444-4445&response_type=code&scope=/orcid-bio/read-limited&redirect_uri=" + redirectUri);
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
