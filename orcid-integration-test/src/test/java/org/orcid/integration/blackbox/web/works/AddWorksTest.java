package org.orcid.integration.blackbox.web.works;

import static org.junit.Assert.assertEquals;

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
        deleteWork(workNameA);
        deleteWork(workNameB);
        deleteWork(workNameC);
        
        // Test actually begins
        addSimple(workNameA, webDriver);
        addSimple(workNameB, webDriver);
        addSimple(workNameC, webDriver);
        assertEquals(1, webDriver.findElements(byWorkTitle(workNameA)).size());
        assertEquals(1, webDriver.findElements(byWorkTitle(workNameB)).size());
        assertEquals(1, webDriver.findElements(byWorkTitle(workNameC)).size());
    }        

    public static void addSimple(String workName, WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        waitWorksLoaded(wait, webDriver);
        openAddWorkModal();
        createWork(workName);
        waitWorksLoaded(wait, webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(byWorkTitle(workName)), webDriver);
    }

    public static By byWorkTitle(String workName) {
        return By.xpath("//h3[@class='workspace-title']//span[text()='" + workName + "']");
    }
    
    
    public static String firstPutCodeByTitle(String title, WebDriver webDriver) {
        List<WebElement> wList = webDriver.findElements(By.xpath("//*[@orcid-put-code and descendant::span[text() = '" + title + "']]"));
        return wList.get(0).getAttribute("orcid-put-code");
    }
    
    public static void reloadWorks(WebDriver webDriver, WebDriverWait wait) {
        ((JavascriptExecutor) webDriver).executeScript("angular.element(document.body).injector().get('worksSrvc').addAbbrWorksToScope()");
        waitWorksLoaded(wait, webDriver);
    }
    
    public static void waitWorksLoading(WebDriverWait wait, WebDriver webDriver) {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='workSpinner']")), webDriver);
    }

    
    public static void waitWorksLoaded(WebDriverWait wait, WebDriver webDriver) {
        BBBUtil.waitForAngular();
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='workspace-publications']")), webDriver);
    }

    public void bulkVisToggle() {

    }
}
