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
import java.util.concurrent.TimeUnit;

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
import org.orcid.integration.blackbox.web.SigninTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-web-context.xml" })
public class AddWorksTest {

    private WebDriver webDriver;

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
        String workNameA = ADD_WORK_TEST + "_" + _A;
        String workNameB = ADD_WORK_TEST + "_" + _B;
        String workNameC = ADD_WORK_TEST + "_" + _C;
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
        
        // clean up any from previous test
        deleteAllByWorkName(workNameA, webDriver);
        deleteAllByWorkName(workNameB, webDriver);
        deleteAllByWorkName(workNameC, webDriver);
    }
    
    public void addComplete() {
        
    }

    public static void addSimple(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        ;
        waitWorksLoaded(wait);
        // Selenium is having issues finding this element, I supect do to CSS transformations
        // Run the function directly
        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=WorkCtrl]').scope().addWorkModal()");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        Select catSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workCategory.value']")));
        catSel.selectByVisibleText("Conference");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[text()='Conference paper']")));
        Select typeSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editWork.workType.value']")));
        typeSel.selectByVisibleText("Conference paper");
        
        Select idTypeSel = new Select(webDriver.findElement(By.xpath("//select[@ng-model='workExternalIdentifier.workExternalIdentifierType.value']")));
        idTypeSel.selectByVisibleText("doi: Digital object identifier");
        WebElement idValue = webDriver.findElement(By.xpath("//input[@ng-model='workExternalIdentifier.workExternalIdentifierId.value']"));
        idValue.sendKeys("10.10/"+System.currentTimeMillis());
        
        WebElement title = webDriver.findElement(By.xpath("//input[@ng-model='editWork.title.value']"));
        title.sendKeys(workName);
        
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement buttonEl = webDriver.findElement(By.xpath("//button[@id='save-new-work']"));
        buttonEl.click();
        SigninTest.colorBoxIsClosed(wait);
        waitWorksLoaded(wait);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName)));
    }

    public static void deleteAllByWorkName(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        waitWorksLoaded(wait);
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + workName + "']]"));
        if (wList.size() > 0)
            for (WebElement we : wList) {
                String putCode = we.getAttribute("orcid-put-code");
                putCode = "" + putCode;
                String deleteJsStr = "angular.element('*[ng-app]').injector().get('worksSrvc').deleteWork('" + putCode + "');";
                ((JavascriptExecutor) webDriver).executeScript(deleteJsStr);
                waitWorksLoaded(wait);
            }
        wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName))));
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
        ((JavascriptExecutor) webDriver).executeScript("angular.element('*[ng-app]').injector().get('worksSrvc').loadAbbrWorks()");
        waitWorksLoaded(wait);
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
