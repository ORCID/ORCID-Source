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
package org.orcid.integration.blackbox.client;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 
 * @author Will Simpson
 *
 */
public class SigninPage {

    private String baseUri;
    private WebDriver webDriver;
    private Utils utils;
    private XPath xpath;
    
    SigninPage(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
        this.utils = new Utils(webDriver);
        this.xpath = new XPath(webDriver);
    }
    
    public void visit(){
        webDriver.get(baseUri + "/signin");
    }

    public void signIn(String username, String password){
        WebElement emailEl = xpath.findElement("//input[@name='userId']");
        emailEl.sendKeys(username);
        WebElement passwordEl = xpath.findElement("//input[@name='password']");
        passwordEl.sendKeys(password);
        xpath.click("//button[@id='form-sign-in-button']");
        utils.getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Sign out']")));
        dismissVerifyEmailModal();
    }
    
    public void dismissVerifyEmailModal() {
        List<WebElement> weList = webDriver.findElements(By.xpath("//div[@ng-controller='VerifyEmailCtrl']"));
        if (weList.size() > 0) {// we need to wait for the color box to appear
            utils.getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@ng-controller='VerifyEmailCtrl' and @orcid-loading='false']")));
            ((JavascriptExecutor) webDriver).executeScript("$.colorbox.close();");
            utils.colorBoxIsClosed();
        }
    }
    
}
