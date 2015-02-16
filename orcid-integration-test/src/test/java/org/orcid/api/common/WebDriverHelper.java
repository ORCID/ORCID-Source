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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByName;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Will Simpson
 * 
 */
public class WebDriverHelper {

    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");

    private WebDriver webDriver;

    private String webBaseUrl;

    private String redirectUri;

    public WebDriverHelper(WebDriver webDriver, String webBaseUrl, String redirectUri) {
        super();
        this.webDriver = webDriver;
        this.webBaseUrl = webBaseUrl;
        this.redirectUri = redirectUri;
    }

    public String obtainAuthorizationCode(String scopes, String orcid, String userId, String password) throws InterruptedException {
        String url = String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri);        
        webDriver.get(url);

        // Switch to the login form
        try {
            By switchFromLinkLocator = By.id("in-register-switch-form");
            (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
            WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
            switchFromLink.click();
        } catch (Exception e) {
            System.out.println("Unable to load URL: " + url);
            e.printStackTrace();
            throw e;
        }

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(userId);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertNotNull(authorizationCode);
        return authorizationCode;
    }

    public void signIn(String userId, String password) throws InterruptedException {
        webDriver.get(String.format("%s/signin", webBaseUrl));

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(userId);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(password);
        WebElement submitButton = webDriver.findElement(By.id("form-sign-in-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(By.linkText("SIGN OUT")));
    }

    public String obtainAuthorizationCodeWhenAlreadySignedIn(String scopes, String orcid) throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));

        By authorizeButtonLocator = ByName.name("authorize");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(authorizeButtonLocator));
        WebElement submitButton = webDriver.findElement(authorizeButtonLocator);
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertNotNull(authorizationCode);
        return authorizationCode;
    }

    public String obtainAuthorizationCode(String scopes, String orcid, String userId, String password, List<String> inputIdsToCheck, boolean markAsSelected)
            throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));

        // Switch to the login form
        By switchFromLinkLocator = By.id("in-register-switch-form");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(switchFromLinkLocator));
        WebElement switchFromLink = webDriver.findElement(switchFromLinkLocator);
        switchFromLink.click();

        // Check the given inputs
        if (inputIdsToCheck != null && !inputIdsToCheck.isEmpty()) {
            for (String id : inputIdsToCheck) {
                By input = By.id(id);
                (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(input));
                WebElement inputElement = webDriver.findElement(input);
                if (markAsSelected) {
                    if (!inputElement.isSelected())
                        inputElement.click();
                } else {
                    if (inputElement.isSelected())
                        inputElement.click();
                }
            }
        }

        // Fill the form
        By userIdElementLocator = By.id("userId");
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(userIdElementLocator));
        WebElement userIdElement = webDriver.findElement(userIdElementLocator);
        userIdElement.sendKeys(userId);
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        passwordElement.sendKeys(password);
        WebElement submitButton = webDriver.findElement(By.id("authorize-button"));
        submitButton.click();

        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertNotNull(authorizationCode);
        return authorizationCode;
    }

    public String obtainAuthorizationCode(String scopes, String orcid) throws InterruptedException {
        return obtainAuthorizationCode(scopes, orcid, "user_to_test@user.com", "password");
    }
    
    public boolean elementExists(String page, String elementId) {
        //Open the page
        webDriver.get(page);        

        //Find this element
        By switchFromLinkLocator = By.id(elementId);
        
        try {
            if(webDriver.findElement(switchFromLinkLocator) != null)
                return true;            
        } catch(NoSuchElementException e) {
            return false;
        }                                           
        
        return false;
    }
    
    public void close() {
        webDriver.close();
    }

}