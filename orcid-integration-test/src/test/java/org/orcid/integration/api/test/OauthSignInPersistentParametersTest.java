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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class OauthSignInPersistentParametersTest extends DBUnitTest {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final String CLIENT_ID = "9999-9999-9999-9994";
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");
    private static final Pattern STATE_PATTERN = Pattern.compile("state=(.+)");
    private static final Pattern OTHER_PATTERN = Pattern.compile("other=(.+)");
    private static final Pattern MADE_UP_PATTERN = Pattern.compile("made_up_param_not_passed=(.+)");
    private static final String DEFAULT = "default";

    private WebDriver webDriver;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ProfileDao profileDao;

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    private static final List<String> DATA_FILES = Arrays.asList("/group_client_data/EmptyEntityData.xml", "/group_client_data/SecurityQuestionEntityData.xml",
            "/group_client_data/ProfileEntityData.xml", "/group_client_data/WorksEntityData.xml", "/group_client_data/OrgsEntityData.xml",
            "/group_client_data/ClientDetailsEntityData.xml", "/group_client_data/ProfileWorksEntityData.xml", "/group_client_data/OrgAffiliationEntityData.xml",
            "/group_client_data/ProfileFundingEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    @Transactional
    public void before() {
        webDriver = new FirefoxDriver();
        redirectUri = webBaseUrl + "/oauth/playground";

        // Set redirect uris if needed
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(CLIENT_ID, redirectUri, DEFAULT);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(CLIENT_ID, redirectUri);
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
    public void stateParamIsPersistentAndReturnedTest() throws InterruptedException {
        webDriver
                .get(String
                        .format("%s/oauth/authorize?client_id=9999-9999-9999-9994&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=%s&state=MyState&made_up_param_not_passed=true&other=present",
                                webBaseUrl, redirectUri));
        // Switch to the login form
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys("user_to_test@user.com");
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys("password");
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        // Check the auth code is present
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertNotNull(authorizationCode);
        // Check the state param is present
        matcher = STATE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String stateParam = matcher.group(1);
        assertNotNull(stateParam);
        assertEquals("MyState", stateParam);

        matcher = OTHER_PATTERN.matcher(currentUrl);
        assertFalse(matcher.find());
        matcher = MADE_UP_PATTERN.matcher(currentUrl);
        assertFalse(matcher.find());
    }
}
