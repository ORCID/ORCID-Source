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
package org.orcid.api.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class OauthAuthorizationPageHelper {

    public static String authorizationScreenUrl = "%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s";
    
    public static String loginAndAuthorize(String baseUrl, String clientId, String redirectUri, String scopes, String stateParam, String userId, String password, WebDriver webDriver) {
        String formattedAuthorizationScreen = String.format(authorizationScreenUrl, baseUrl, clientId, scopes, redirectUri);
        
        if(!PojoUtil.isEmpty(stateParam)) {
            formattedAuthorizationScreen += "&state=" + stateParam;
        }
        
        formattedAuthorizationScreen += "#show_login";
        webDriver.get(formattedAuthorizationScreen);
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.documentReady());
        BlackBoxBase.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='userId']")), webDriver);
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
        
        loginOnOauthorizationScreen(webDriver, userId, password);
        
        String result = webDriver.getCurrentUrl();
        return result;
    }
    
    public static String authorizeOnAlreadyLoggedInUser(final WebDriver loggedInDriver, String baseUrl, String clientId, String redirectUri, String scopes, String stateParam) {
        String formattedAuthorizationScreen = String.format(authorizationScreenUrl, baseUrl, clientId, scopes, redirectUri);
        
        if(!PojoUtil.isEmpty(stateParam)) {
            formattedAuthorizationScreen += "&state=" + stateParam;
        }
        
        loggedInDriver.get(formattedAuthorizationScreen);
        
        (new WebDriverWait(loggedInDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
        
        clickAuthorizeOnAuthorizeScreen(loggedInDriver);
        
        String result = loggedInDriver.getCurrentUrl();        
        return result;
    }
    
    public static void loginOnOauthorizationScreen(final WebDriver webDriver, String userId, String password) {
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.documentReady());
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(userId);
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(password);
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS, BlackBoxBase.SLEEP_MILLISECONDS)).until(BlackBoxBase.angularHasFinishedProcessing());
        BlackBoxBase.ngAwareClick(webDriver.findElement(By.id("login-authorize-button")),webDriver);

        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
    }
    
    private static void clickAuthorizeOnAuthorizeScreen(final WebDriver webDriver) {
        By userIdElementLocator = By.id("authorize");
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement authorizeButton = webDriver.findElement(By.id("authorize"));
        authorizeButton.click();
        (new WebDriverWait(webDriver, BlackBoxBase.TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
    }    
}
