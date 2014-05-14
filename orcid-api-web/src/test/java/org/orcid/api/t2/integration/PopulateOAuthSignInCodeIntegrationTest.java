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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.t2.T2OAuthAPIService;
import org.orcid.core.manager.impl.OrcidSSOManagerImpl;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
/**
 * 
 * @author rcpeters
 *
 */
public class PopulateOAuthSignInCodeIntegrationTest extends DBUnitTest {

    private static final String CLIENT_DETAILS_ID = "4444-4444-4444-4445";

    private WebDriver webDriver;

    @Resource
    private ProfileDao profileDao;

    @Resource
    OrcidSSOManagerImpl ssoManager;

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

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
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

    String getBaseUrl() {
        return String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, CLIENT_DETAILS_ID, "/orcid-bio/read-limited",
                redirectUri);
    }

    @Test
    public void checkNoPrePop() throws JSONException, InterruptedException {
        webDriver.get(getBaseUrl());
        // make sure we are on the page
        assertTrue(webDriver.findElements(By.xpath("//h3[contains(.,\"Don't have an iD? Register\")]")).size() > 0);
        assertTrue(webDriver.findElement(By.xpath("//input[@name='email']")).getAttribute("value").equals(""));
        assertTrue(webDriver.findElement(By.xpath("//input[@name='familyNames']")).getAttribute("value").equals(""));
        assertTrue(webDriver.findElement(By.xpath("//input[@name='givenNames']")).getAttribute("value").equals(""));
        // verify we don't populate signin
        assertTrue(webDriver.findElement(By.xpath("//input[@name='userId']")).getAttribute("value").equals(""));
    }

    @Test
    public void emailPrePopulate() throws JSONException, InterruptedException {
        // test populating form with email that doesn't exist
        webDriver.get(getBaseUrl() + "&email=non_existent@test.com&family_names=test_family_names&given_names=test_given_name");
        assertTrue(webDriver.findElement(By.xpath("//input[@name='email']")).getAttribute("value").equals("non_existent@test.com"));
        assertTrue(webDriver.findElement(By.xpath("//input[@name='familyNames']")).getAttribute("value").equals("test_family_names"));
        assertTrue(webDriver.findElement(By.xpath("//input[@name='givenNames']")).getAttribute("value").equals("test_given_name"));
        // verify we don't populate signin
        assertTrue(webDriver.findElement(By.xpath("//input[@name='userId']")).getAttribute("value").equals(""));

        // test exisitng email
        webDriver.get(getBaseUrl() + "&email=spike@milligan.com&family_names=test_family_names&given_names=test_given_name");
        assertTrue(webDriver.findElement(By.xpath("//input[@name='userId']")).getAttribute("value").equals("spike@milligan.com"));
        // make sure register
        assertTrue(webDriver.findElement(By.xpath("//input[@name='email']")).getAttribute("value").equals(""));

        // populating check populating orcid
        webDriver.get(getBaseUrl() + "&email=spike@milligan.com&family_names=test_family_names&given_names=test_given_name&orcid=4444-4444-4444-4441");
        assertTrue(webDriver.findElement(By.xpath("//input[@name='userId']")).getAttribute("value").equals("4444-4444-4444-4441"));
    }

    @Test
    public void orcidIdPreopulate() throws JSONException, InterruptedException {
        // populating check populating orcid
        webDriver.get(getBaseUrl() + "&orcid=4444-4444-4444-4441");
        assertTrue(webDriver.findElement(By.xpath("//input[@name='userId']")).getAttribute("value").equals("4444-4444-4444-4441"));

        // populating check populating orcid overwrites populating email
        webDriver.get(getBaseUrl() + "&email=spike@milligan.com&family_names=test_family_names&given_names=test_given_name&orcid=4444-4444-4444-4441");
        assertTrue(webDriver.findElement(By.xpath("//input[@name='userId']")).getAttribute("value").equals("4444-4444-4444-4441"));
    }

}
