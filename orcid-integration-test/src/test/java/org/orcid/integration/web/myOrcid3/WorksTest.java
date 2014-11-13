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
package org.orcid.integration.web.myOrcid3;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.web.SigninTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-web-context.xml" })
public class WorksTest {

    private WebDriver webDriver;

    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    
    private String WORK_TEST_PREFIX = "WORK_TEST";

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        webDriver.get(baseUri + "/signin");
        SigninTest.signIn(webDriver, user1UserName, user1Password, true);
        webDriver.get(baseUri + "/my-orcid3");
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void addSimple() {
        WebElement linkEl = webDriver.findElement(By.xpath("//a[@ng-click='addWorkModal()']"));
        linkEl.click();
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        Select catSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        catSel.selectByVisibleText("Conference");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[text()='Conference paper']")));
        Select typeSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workType.value']")));
        typeSel.selectByVisibleText("Conference paper");
        WebElement title = webDriver.findElement(By.xpath("//input[@ng-model='editWork.workTitle.title.value']"));
        title.sendKeys(WORK_TEST_PREFIX + "_A");
        WebElement buttonEl = webDriver.findElement(By.xpath("//button[@id='save-new-work']"));
        buttonEl.click();
    }
}

