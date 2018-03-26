package org.orcid.api.common;

import static org.orcid.integration.blackbox.api.BBBUtil.TIMEOUT_SECONDS;
import static org.orcid.integration.blackbox.api.BBBUtil.findElementById;
import static org.orcid.integration.blackbox.api.BBBUtil.getUrlAndWait;
import static org.orcid.integration.blackbox.api.BBBUtil.ngAwareSendKeys;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForAngular;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementPresence;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementVisibility;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class OauthAuthorizationPageHelper {

    public static final String authorizationScreenUrl = "%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s";
    public static final String authorizationScreenUrlWithCode = "%s/oauth/authorize?client_id=%s&response_type=%s&scope=%s&redirect_uri=%s";
    public static final String RESPONSE_TYPE_CODE = "code";
    
    public static String loginAndAuthorize(String baseUrl, String clientId, String redirectUri, String scopes, String stateParam, String userId, String password, boolean longLife, WebDriver webDriver) {
        return loginAndAuthorize(baseUrl, clientId,  redirectUri, scopes, stateParam, userId, password, longLife, null, webDriver,RESPONSE_TYPE_CODE);
    }
    
    public static String loginAndAuthorize(String baseUrl, String clientId, String redirectUri, String scopes, String stateParam, String userId, String password,
            boolean longLife, Map<String, String> params, WebDriver webDriver, String responseType) {
        String formattedAuthorizationScreen = String.format(authorizationScreenUrlWithCode, baseUrl, clientId, responseType, scopes, redirectUri);
        if (!PojoUtil.isEmpty(stateParam)) {
            formattedAuthorizationScreen += "&state=" + stateParam;
        }
        if (params != null) {
            for (String key : params.keySet()) {
                formattedAuthorizationScreen += "&" + key + "=" + params.get(key);
            }
        }
        getUrlAndWait(formattedAuthorizationScreen);
        waitForSignin(webDriver);
        
        String currentUrl = webDriver.getCurrentUrl();
        if (isPlayground(currentUrl)) {
            // Probably got an error with the auth request and went straight to playground
            return currentUrl;
        }

        By userIdElementLocator = By.id("userId");
        waitForElementPresence(userIdElementLocator);
        ngAwareSendKeys(userId, "userId", webDriver);

        waitForElementPresence(By.id("password"));
        ngAwareSendKeys(password, "password", webDriver);

        waitForElementPresence((By.id("form-sign-in-button")));
        WebElement signInButton = findElementById("form-sign-in-button");
        signInButton.click();
        waitForAuth(webDriver);

        currentUrl = webDriver.getCurrentUrl();
        if (isPlayground(currentUrl)) {
            // Permission had previously been granted, so the authorization
            // screen was skipped
            return currentUrl;
        }
        
        waitForElementVisibility(By.xpath("//p[contains(text(),'has asked for the following access to your ORCID Record')]"));
        waitForAngular();

        if (longLife == false) {
            // disablePersistentToken
            WebElement persistentElement = webDriver.findElement(By.id("enablePersistentToken"));
            if (persistentElement.isDisplayed()) {
                if (persistentElement.isSelected()) {
                    persistentElement.click();
                }
            }
        }
        waitForAngular();

        By authorizeElementLocator = By.id("authorize");
        waitForElementVisibility(authorizeElementLocator);
        WebElement authorizeButton = findElementById("authorize");
        authorizeButton.click();

        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });

        return webDriver.getCurrentUrl();
    }
    
    private static void waitForSignin(WebDriver webDriver) {
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                String currentUrl = d.getCurrentUrl();
                return isPlayground(currentUrl) || currentUrl.contains("/signin?");
            }
        });
    }

    private static void waitForAuth(WebDriver webDriver) {
        (new WebDriverWait(webDriver, TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                String currentUrl = d.getCurrentUrl();
                return isPlayground(currentUrl) || currentUrl.contains("/oauth/authorize?");
            }
        });
    }

    private static boolean isPlayground(String currentUrl) {
        return currentUrl.contains("/oauth/playground#") || currentUrl.contains("/oauth/playground?");
    }
    
    public static String authorizeOnAlreadyLoggedInUser(final WebDriver loggedInDriver, String baseUrl, String clientId, String redirectUri, String scopes, String stateParam) {
        String formattedAuthorizationScreen = String.format(authorizationScreenUrl, baseUrl, clientId,scopes, redirectUri);
        
        if(!PojoUtil.isEmpty(stateParam)) {
            formattedAuthorizationScreen += "&state=" + stateParam;
        }
        
        loggedInDriver.get(formattedAuthorizationScreen);
        
        try {
            BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[contains(text(),'has asked for the following access to your ORCID Record')]")), loggedInDriver);
            By authorizeElementLocator = By.id("authorize");
            (new WebDriverWait(loggedInDriver, BBBUtil.TIMEOUT_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(authorizeElementLocator));
            WebElement authorizeButton = loggedInDriver.findElement(By.id("authorize"));
            authorizeButton.click();
            (new WebDriverWait(loggedInDriver, BBBUtil.TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return d.getTitle().equals("ORCID Playground");
                }
            });
        } catch(TimeoutException e) {
           //It might be the case that we are already in the ORCID Playground page, so, lets check for that case
           (new WebDriverWait(loggedInDriver, BBBUtil.TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return d.getTitle().equals("ORCID Playground");
                }
            });
        } 

        String result = loggedInDriver.getCurrentUrl();        
        return result;
    }
        
   public static void clickAuthorizeOnAuthorizeScreen(final WebDriver webDriver, boolean longLife) {
        if (webDriver.getTitle().equals("ORCID Playground")){
            return;
        } else {
                try {
                    BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[contains(text(),'has asked for the following access to your ORCID Record')]")), webDriver);
                    if (longLife == false) {
                        //disablePersistentToken
                        WebElement persistentElement = webDriver.findElement(By.id("enablePersistentToken"));
                        if(persistentElement.isDisplayed()) {
                            if (persistentElement.isSelected()) {
                                persistentElement.click();
                            }
                        }            
                    }
                    WebElement authorizeButton = webDriver.findElement(By.id("authorize"));
                    authorizeButton.click();
                } catch(TimeoutException e) {
                    //It might be the case that we are already in the ORCID Playground page, so, lets check for that case
                    (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
                        public Boolean apply(WebDriver d) {
                            return d.getTitle().equals("ORCID Playground");
                        }
                    });
                }
                
            }
        }
}
