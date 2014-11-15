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

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
    private String SIMPLE_A ="SIMPLE_A";
    private String SIMPLE_B ="SIMPLE_B";
    private String SIMPLE_C ="SIMPLE_C";

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/my-orcid3");
        SigninTest.signIn(webDriver, user1UserName, user1Password);
        SigninTest.dismissVerifyEmailModal(webDriver);
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void addThreeSimple() {
        String workNameA = WORK_TEST_PREFIX + "_" + SIMPLE_A;
        String workNameB = WORK_TEST_PREFIX + "_" + SIMPLE_B;
        String workNameC = WORK_TEST_PREFIX + "_" + SIMPLE_C;
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        waitWorksLoaded(wait);
        // clean up any from previous test
        deleteAllByWorkName(workNameA, webDriver);
        deleteAllByWorkName(workNameB, webDriver);
        deleteAllByWorkName(workNameC, webDriver);
        
        // Test actually begins
        addSimple(workNameA, webDriver);
        addSimple(workNameB, webDriver);
        addSimple(workNameC, webDriver);
        assertEquals(1, webDriver.findElements(byWorkTitle(workNameA)).size());
        assertEquals(1, webDriver.findElements(byWorkTitle(workNameB)).size());
        assertEquals(1, webDriver.findElements(byWorkTitle(workNameC)).size());
        
        
    }
    
    public void addComplete() {
        
    }

    public static void addSimple(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        ;
        waitWorksLoaded(wait);
        WebElement linkEl = webDriver.findElement(By.xpath("//a[@ng-click='addWorkModal()']"));
        linkEl.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        Select catSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        catSel.selectByVisibleText("Conference");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[text()='Conference paper']")));
        Select typeSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workType.value']")));
        typeSel.selectByVisibleText("Conference paper");
        WebElement title = webDriver.findElement(By.xpath("//input[@ng-model='editWork.workTitle.title.value']"));
        title.sendKeys(workName);
        WebElement buttonEl = webDriver.findElement(By.xpath("//button[@id='save-new-work']"));
        buttonEl.click();
        SigninTest.colorBoxIsClosed(wait);
        waitWorksLoaded(wait);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName)));
    }

    public static void deleteAllByWorkName(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        waitWorksLoaded(wait);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@orcid-putCode and descendant::strong[text() = '" + workName + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("orcid-putCode");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element('*[ng-app]').injector().get('worksSrvc').deleteWork('" + putCode + "');";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                waitWorksLoaded(wait);
            }
        wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName))));
        assertTrue(0 == webDriver.findElements(byWorkTitle(workName)).size());
    }

    public static By byWorkTitle(String workName) {
        return By.xpath("//strong[@ng-bind='work.workTitle.title.value' and text()='" + workName + "']");
    }
    
    public static void waitWorksLoading(WebDriverWait wait) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='workSpinner']")));
    }

    
    public static void waitWorksLoaded(WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")));
    }

    public void bulkVisToggle() {

    }
}
