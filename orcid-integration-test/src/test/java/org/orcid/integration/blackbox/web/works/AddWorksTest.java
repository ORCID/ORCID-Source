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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.orcid.integration.blackbox.web.SigninTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class AddWorksTest extends BlackBoxBase {
    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;

    private String ADD_WORK_TEST = "ADD_WORK_TEST";
    private String _A ="_A";
    private String _B ="_B";
    private String _C ="_C";

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
        String workNameA = ADD_WORK_TEST + "_" + _A;
        String workNameB = ADD_WORK_TEST + "_" + _B;
        String workNameC = ADD_WORK_TEST + "_" + _C;
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        waitWorksLoaded(wait, webDriver);
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
        
        // clean up any from previous test
        deleteAllByWorkName(workNameA, webDriver);
        deleteAllByWorkName(workNameB, webDriver);
        deleteAllByWorkName(workNameC, webDriver);
    }        

    public static void addSimple(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        
        waitWorksLoaded(wait, webDriver);
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        // Selenium is having issues finding this element, I supect do to CSS transformations
        // Run the function directly
        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=WorkCtrl]').scope().addWorkModal()");
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//select[@ng-model='editWork.workCategory.value']")), webDriver);
        BBBUtil.ngAwareSendKeys("conference","workCategory", webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//option[text()='Conference paper']")), webDriver);
        BBBUtil.ngAwareSendKeys("string:conference-abstract","workType", webDriver);
        BBBUtil.ngAwareSendKeys("doi","worksIdType0", webDriver);
        BBBUtil.ngAwareSendKeys("10.10/"+System.currentTimeMillis(),"worksIdValue0", webDriver);
        BBBUtil.ngAwareSendKeys(workName,"work-title", webDriver);
        
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//button[@id='save-new-work']")), webDriver);
        
        SigninTest.colorBoxIsClosed(wait);
        waitWorksLoaded(wait, webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName)), webDriver);
    }

    public static void deleteAllByWorkName(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        waitWorksLoaded(wait, webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + workName + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("orcid-put-code");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element(document.body).injector().get('worksSrvc').deleteWork('" + putCode + "');";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                waitWorksLoaded(wait, webDriver);
            }
        BBBUtil.extremeWaitFor(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName))), webDriver);
        assertTrue(0 == webDriver.findElements(byWorkTitle(workName)).size());
    }

    public static By byWorkTitle(String workName) {
        return By.xpath("//span[@ng-bind='work.title.value' and text()='" + workName + "']");
    }
    
    
    public static String firstPutCodeByTitle(String title, WebDriver webDriver) {
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + title + "']]"));
        return wList.get(0).getAttribute("orcid-put-code");
    }
    
    public static void reloadWorks(WebDriver webDriver, WebDriverWait wait) {
        ((JavascriptExecutor) webDriver).executeScript("angular.element(document.body).injector().get('worksSrvc').loadAbbrWorks()");
        waitWorksLoaded(wait, webDriver);
    }
    
    public static void waitWorksLoading(WebDriverWait wait, WebDriver webDriver) {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='workSpinner']")), webDriver);
    }

    
    public static void waitWorksLoaded(WebDriverWait wait, WebDriver webDriver) {
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications' and @orcid-loaded='true']")), webDriver);
    }

    public void bulkVisToggle() {

    }
}
