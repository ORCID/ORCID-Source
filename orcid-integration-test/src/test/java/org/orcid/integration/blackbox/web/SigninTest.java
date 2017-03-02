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
package org.orcid.integration.blackbox.web;

import java.util.List;

import org.junit.After;
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
public class SigninTest extends BlackBoxBase {    

    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;

    @After
    public void after() {
        signout();
    }

    @Test
    public void signinTest() {
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/my-orcid");
        signIn(webDriver, user1UserName, user1Password);
        dismissVerifyEmailModal(webDriver);
    }
    
    @Test
    public void signinVariousUsernameFormats() {
    	String user1FrmtSpaces = user1OrcidId.replace('-', ' ');
    	verifySignIn(user1FrmtSpaces);
    	String user1FrmtNoSpNoHyp = user1OrcidId.replace("-", "");
    	verifySignIn(user1FrmtNoSpNoHyp);
    	String user1FrmtProfLink = new StringBuffer(baseUri).append("/").append(user1OrcidId).toString();
    	verifySignIn(user1FrmtProfLink);
    }
    
    private void verifySignIn(String userName) {
    	webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/my-orcid");
        signIn(webDriver, userName, user1Password);
        dismissVerifyEmailModal(webDriver);
    }

	// Make this available to other classes
    static public void signIn(WebDriver webDriver, String username, String password) {
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(),webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(),webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='userId']")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(),webDriver);
        WebElement emailEl = webDriver.findElement(By.xpath("//input[@name='userId']"));
        emailEl.sendKeys(username);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(),webDriver);
        WebElement passwordEl = webDriver.findElement(By.xpath("//input[@name='password']"));
        passwordEl.sendKeys(password);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(),webDriver);
        WebElement buttonEl = webDriver.findElement(By.xpath("//button[@id='form-sign-in-button']"));
        BBBUtil.ngAwareClick(buttonEl, webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Sign out']")),webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(),webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(),webDriver);
    }

    public static void dismissVerifyEmailModal(WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        List<WebElement> weList = webDriver.findElements(By.xpath("//div[@ng-controller='VerifyEmailCtrl']"));
        if (weList.size() > 0) {// we need to wait for the color box to appear
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@ng-controller='VerifyEmailCtrl' and @orcid-loading='false']")));
            ((JavascriptExecutor) webDriver).executeScript("$.colorbox.close();");
            colorBoxIsClosed(wait);
        }
    }

    public static void colorBoxIsClosed(WebDriverWait wait) {
        wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='colorbox']"))));
    }

}
