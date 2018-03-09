package org.orcid.integration.blackbox.api;

import static org.orcid.integration.blackbox.api.BlackBoxWebDriver.getWebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.helper.SystemPropertiesHelper;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.paulhammant.ngwebdriver.NgWebDriver;

@RunWith(SpringJUnit4ClassRunner.class)
public class BBBUtil {
    
    private static String jQueryWaitScript;
    static {
        try {
            jQueryWaitScript = IOUtils.toString(BBBUtil.class.getResourceAsStream("jqueryWait.js"));
        } catch (IOException e) {
            throw new RuntimeException("Error reading jquery wait script", e);
        }
    }

    public static String getProperty(String key) {
        Properties prop = SystemPropertiesHelper.getProperties();
        return prop.getProperty(key);
    }
    
    public static void logUserOut(String baseUrl, WebDriver webDriver) {
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
    }

    public static void revokeApplicationsAccess(WebDriver webDriver) {
        List<String> clientIds = new ArrayList<String>();
        Properties prop = SystemPropertiesHelper.getProperties();
        String clientId1 = prop.getProperty("org.orcid.web.testClient1.clientId");
        if (!PojoUtil.isEmpty(clientId1)) {
            clientIds.add(clientId1);
        }

        String clientId2 = prop.getProperty("org.orcid.web.testClient2.clientId");
        if (!PojoUtil.isEmpty(clientId2)) {
            clientIds.add(clientId2);
        }

        String userName = prop.getProperty("org.orcid.web.testUser1.username");
        String password = prop.getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "https://localhost:8443/orcid-web";
        if (!PojoUtil.isEmpty(prop.getProperty("org.orcid.web.baseUri"))) {
            baseUrl = prop.getProperty("org.orcid.web.baseUri");
        }

        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(baseUrl + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());

        signIn(webDriver, userName, password);

        // Switch to accounts settings page
        By accountSettingsMenuLink = By.id("accountSettingMenuLink");
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(accountSettingsMenuLink), webDriver);
        webDriver.get(baseUrl + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);

        try {
            boolean lookAgain = false;
            do {
                // Look for each revoke app button
                By revokeAppBtn = By.id("revokeAppBtn");
                BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
                List<WebElement> appsToRevoke = webDriver.findElements(revokeAppBtn);
                boolean elementFound = false;
                // Iterate on them and delete the ones created by the specified
                // client id
                for (WebElement appElement : appsToRevoke) {
                    String nameAttribute = appElement.getAttribute("name");
                    if (clientIds.contains(nameAttribute)) {
                        BBBUtil.ngAwareClick(appElement, webDriver);
                        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
                        // Wait for the revoke button
                        By confirmRevokeAppBtn = By.id("confirmRevokeAppBtn");
                        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(confirmRevokeAppBtn), webDriver);
                        BBBUtil.ngAwareClick(webDriver.findElement(confirmRevokeAppBtn), webDriver);
                        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
                        BBBUtil.noCboxOverlay(webDriver);
                        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
                        // may need to put sleep back here
                        elementFound = true;
                        break;
                    }
                }
                if (elementFound) {
                    lookAgain = true;
                } else {
                    lookAgain = false;
                }
            } while (lookAgain);
        } catch (Exception e) {
            // If it fail is because it couldn't find any other application
        } finally {
            logUserOut(baseUrl, webDriver);
        }
    }    
    
    public static void ngAwareClick(WebElement webElement){
        ngAwareClick(webElement, getWebDriver());
    }
    
    public static void ngAwareClick(WebElement webElement, WebDriver webDriver) {
        waitForAngular(webDriver);
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement).perform();        
        actions.click(webElement).perform();
        waitForAngular(webDriver);
    }
    
    public static void ngAwareSendKeys(String keys, String id, WebDriver webDriver) {
        waitForAngular();
        ((JavascriptExecutor) webDriver).executeScript(
                "" + "angular.element('#" + id + "').triggerHandler('focus');" + "angular.element('#" + id + "').val('" + keys + "');" + "angular.element('#" + id
                        + "').triggerHandler('change');" + "angular.element('#" + id + "').triggerHandler('blur');" + "angular.element('#" + id + "').scope().$apply();");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }

    public static void noSpinners() {
        noSpinners(getWebDriver());
    }

    public static void noSpinners(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("i.glyphicon-refresh")));
    }
    
    public static void waitForNoCboxOverlay() {
        noCboxOverlay(getWebDriver());
        waitForAngular();
    }
    

    public static void noCboxOverlay(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='cboxOverlay']")));
    }
    
    public static void waitForCboxComplete(){
        extremeWaitFor(cboxComplete(), getWebDriver());
        waitForAngular();
    }

    public static void shortWaitFor(ExpectedCondition<?> expectedCondition, WebDriver webDriver) {
        int wait = 5;
        int pollingInterval = 250;
        waitFor(wait, pollingInterval, expectedCondition, webDriver, false);
    }
    
    public static void extremeWaitFor(ExpectedCondition<?> expectedCondition, WebDriver webDriver) {
        int wait = 15;
        int pollingInterval = 250;
        waitFor(wait, pollingInterval, expectedCondition, webDriver, true);
    }
    
    private static void waitFor(int wait, int pollingInterval, ExpectedCondition<?> expectedCondition, WebDriver webDriver, boolean retry) {
        try {
            (new WebDriverWait(webDriver, wait, pollingInterval)).until(expectedCondition);
        } catch (Exception e) {
            if(retry) {
                ((JavascriptExecutor) webDriver).executeScript("$(window).trigger('resize');");
                (new WebDriverWait(webDriver, wait, pollingInterval)).until(expectedCondition);
            } else {
                throw e;
            }
        }
    }

    public static void waitForAngular() {
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), getWebDriver());
    }
    
    public static void waitForAngular(WebDriver webDriver) {
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }

    public static void waitForElementVisibility(By elementLocatedBy) {
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(elementLocatedBy), getWebDriver());
    }

    public static void waitForElementPresence(By elementLocatedBy) {
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(elementLocatedBy), getWebDriver());
    }

    public static ExpectedCondition<Boolean> documentReady() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            }
        };
    }

    public static ExpectedCondition<Boolean> angularHasFinishedProcessing() {
        /*
         * Getting complex. 1. We want to make sure Angular is done. So you call
         * the rootScope apply 2. We want to make sure the browser is done
         * rendering the DOM so we call $timeout
         * http://blog.brunoscopelliti.com/run-a-directive-after-the-dom-has-
         * finished-rendering/ 3. make sure there are no pending AJAX request,
         * if so start over
         */
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                ((JavascriptExecutor) driver).executeScript(jQueryWaitScript);
                Object obj = ((JavascriptExecutor) driver).executeScript("" + "return window._selenium_jquery_done;");
                Boolean jqueryDone = (obj == null ? false : Boolean.valueOf(obj.toString()));
                if (jqueryDone) {
                    new NgWebDriver((JavascriptExecutor) driver).waitForAngularRequestsToFinish();
                }
                return jqueryDone;
            }
        };
    }

    public static ExpectedCondition<Boolean> cboxComplete() {
        /*
         * Getting complex. 1. We want to make sure Angular is done. So you call
         * the rootScope apply 2. We want to make sure the browser is done
         * rendering the DOM so we call $timeout
         * http://blog.brunoscopelliti.com/run-a-directive-after-the-dom-has-
         * finished-rendering/ 3. make sure there are no pending AJAX request,
         * if so start over
         */
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("" + "return window.cbox_complete").toString());
            }
        };
    }
    
    public static boolean elementExists(By elementLocatedBy) {
        try {
            WebElement element = findElement(elementLocatedBy);
            return element != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static WebElement findElement(By elementLocatedBy) {
        return getWebDriver().findElement(elementLocatedBy);
    }
    
    public static WebElement findElementById(String id) {
        return findElement(By.id(id));
    }

    public static WebElement findElementByXpath(String xpath) {
        return findElement(By.xpath(xpath));
    }
    
    public static List<WebElement> findElements(By elementsLocatedBy) {
        return getWebDriver().findElements(elementsLocatedBy);
    }
    
    public static List<WebElement> findElementsByXpath(String xpath) {
        return getWebDriver().findElements(By.xpath(xpath));
    }

    public static void getUrl(String url) {
        getWebDriver().get(url);
    }
    
    public static void getUrlAndWait(String url) {
        getWebDriver().get(url);
        documentReady();
        waitForAngular();
        noSpinners();
    }

    public static String executeJavaScript(String javaScript, WebElement webElement, WebDriver driver) {
        JavascriptExecutor je = (JavascriptExecutor) driver;
        Object result = je.executeScript(javaScript, webElement);
        return result == null ? null : result.toString();
    }
    
    public static String executeJavaScript(String javaScript, WebElement webElement) {
        JavascriptExecutor je = (JavascriptExecutor) getWebDriver();
        Object result = je.executeScript(javaScript, webElement);
        return result == null ? null : result.toString();
    }

    public static final int TIMEOUT_SECONDS = 10;
    public static final int SLEEP_MILLISECONDS = 100;

    /**
     * Assumes the user is already logged in and in the /my-orcid page 
     * */
    public static void changeDefaultUserVisibility(WebDriver webDriver, Visibility visibility) {
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        By privacyPreferenceToggle = By.id("privacyPreferencesToggle");
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(privacyPreferenceToggle), webDriver);
        WebElement toggle = webDriver.findElement(privacyPreferenceToggle);
        BBBUtil.ngAwareClick(toggle, webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        String clickXPathStr = "//div[@id='privacy-settings' and contains(text(),'By default, who should')]//a[contains(@ng-click,'" + visibility.value().toUpperCase() + "')]";
        String clickWorkedStr =  "//div[@id='privacy-settings' and contains(text(),'By default, who should ')]//li[@class='" +visibility.value().toLowerCase() + "Active']//a[contains(@ng-click,'" + visibility.value().toUpperCase() + "')]";

        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ByXPath.xpath(clickXPathStr)), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(ByXPath.xpath(clickXPathStr)), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ByXPath.xpath(clickWorkedStr)), webDriver);
        // this is really evil, suggest JPA isn't flushing/persisting as quick as we would like
        try {Thread.sleep(500);} catch(Exception e) {};
    }
    
    public static void signIn(WebDriver webDriver, String username, String password) {
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='userId']")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        WebElement emailEl = webDriver.findElement(By.xpath("//input[@name='userId']"));
        emailEl.sendKeys(username);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        WebElement passwordEl = webDriver.findElement(By.xpath("//input[@name='password']"));
        passwordEl.sendKeys(password);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        WebElement buttonEl = webDriver.findElement(By.xpath("//button[@id='form-sign-in-button']"));
        BBBUtil.ngAwareClick(buttonEl, webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Sign out']")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }
    
    public static void dismissVerifyEmailModal() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), 10);
        List<WebElement> weList = getWebDriver().findElements(By.xpath("//div[@ng-controller='VerifyEmailCtrl']"));
        if (weList.size() > 0) {// we need to wait for the color box to appear
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@ng-controller='VerifyEmailCtrl' and @orcid-loading='false']")));
            ((JavascriptExecutor) getWebDriver()).executeScript("$.colorbox.close();");
            colorBoxIsClosed(wait);
        }
    }
    
    public static void colorBoxIsClosed(WebDriverWait wait) {
        wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='colorbox']"))));
    }
}