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
package org.orcid.integration.blackbox.web.works;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PrivacyWorksTest extends BlackBoxBase {
    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;

    private String PRIVACY_WORKS_TEST = "PRIVACY_WORKS_TEST";
    private String _A = "_A";
    private String _B = "_B";
    private String _C = "_C";

    @Before
    public void before() {
        signin();
    }
    
    @After
    public void after() {
        signout();
    }

    @Test
    public void addThreeSimple() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        AddWorksTest.waitWorksLoaded(wait, webDriver);
        // clean up any from previous test
        AddWorksTest.deleteAllByWorkName(PRIVACY_WORKS_TEST+_A, webDriver);
        AddWorksTest.deleteAllByWorkName(PRIVACY_WORKS_TEST+_B, webDriver);
        AddWorksTest.deleteAllByWorkName(PRIVACY_WORKS_TEST+_C, webDriver);
        
        // Test actually begins
        AddWorksTest.addSimple(PRIVACY_WORKS_TEST+_A, webDriver);
        AddWorksTest.addSimple(PRIVACY_WORKS_TEST+_B, webDriver);
        AddWorksTest.addSimple(PRIVACY_WORKS_TEST+_C, webDriver);
        assertEquals(1, webDriver.findElements(AddWorksTest.byWorkTitle(PRIVACY_WORKS_TEST+_A)).size());
        assertEquals(1, webDriver.findElements(AddWorksTest.byWorkTitle(PRIVACY_WORKS_TEST+_B)).size());
        assertEquals(1, webDriver.findElements(AddWorksTest.byWorkTitle(PRIVACY_WORKS_TEST+_C)).size());
        
        
        webDriver.findElement(selectPublicByTitle(PRIVACY_WORKS_TEST+_A)).click();
        webDriver.findElement(selectLimitedByTitle(PRIVACY_WORKS_TEST+_B)).click();
        webDriver.findElement(selectPrivateByTitle(PRIVACY_WORKS_TEST+_C)).click();
        
        AddWorksTest.reloadWorks(webDriver, wait);
        
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(privIsVis(PRIVACY_WORKS_TEST+_A, "PUBLIC")));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(privIsVis(PRIVACY_WORKS_TEST+_B, "LIMITED")));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(privIsVis(PRIVACY_WORKS_TEST+_C, "PRIVATE")));
        
        // clean up after test
        AddWorksTest.deleteAllByWorkName(PRIVACY_WORKS_TEST+_A, webDriver);
        AddWorksTest.deleteAllByWorkName(PRIVACY_WORKS_TEST+_B, webDriver);
        AddWorksTest.deleteAllByWorkName(PRIVACY_WORKS_TEST+_C, webDriver);
    }
    
    public static By selectPublicByTitle(String title) {
        return privSelectByTitle(title,1);
    }

    public static By selectLimitedByTitle(String title) {
        return privSelectByTitle(title,2);
    }

    public static By selectPrivateByTitle(String title) {
        return privSelectByTitle(title,3);
    }
    
    public static By privSelectByTitle(String title, int pos) {
        return By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + title + "']]//ul[@class='privacyToggle']/li[" + pos + "]/a");
    }
    
    public static By privIsVis(String title, String vis) {
        return By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + title + "']]//ul[@class='privacyToggle']/li[@class='" + vis.toLowerCase() + "Active']");
    }
}
