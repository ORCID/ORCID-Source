package org.orcid.integration.blackbox.web;

import static org.junit.Assert.assertEquals;
import static org.orcid.integration.blackbox.api.BBBUtil.executeJavaScript;
import static org.orcid.integration.blackbox.api.BBBUtil.findElement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.orcid.core.togglz.Features;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class RegisterTest extends BlackBoxBase {

    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    
    @BeforeClass
    public static void beforeClass() {
        // turn recaptcha off
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.DISABLE_RECAPTCHA, true);
    }

    @AfterClass
    public static void afterClass() {
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.DISABLE_RECAPTCHA, false);
    }

    @Test
    public void testActivitiesVisibilityOnSigninPage() {
        webDriver.get(baseUri + "/signin");
        webDriver.findElement(By.id("switch-to-register-form")).click();
        
        By element = By.xpath("//div[@class='visibilityDefault']");
        WebElement we = findElement(element);
        assertEquals(null, executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.activitiesVisibilityDefault.visibility", we));
    }

    @Test
    public void testActivitiesVisibilityOnRegisterPage() {
        webDriver.get(baseUri + "/register");
        By element = By.xpath("//div[@class='visibilityDefault']");
        WebElement we = findElement(element);
        assertEquals(null, executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.activitiesVisibilityDefault.visibility", we));
    }

    @Test
    public void testSubmitRegistrationFormWithNullDefaultVisibilityFromRegisterPage() {
        webDriver.get(baseUri + "/register");
        
        findElement(By.id("register-form-given-names")).sendKeys("test-first-name");
        findElement(By.id("register-form-family-name")).sendKeys("test-last-name");
        findElement(By.name("emailprimary234")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-password")).sendKeys("password123");
        findElement(By.id("register-form-confirm-password")).sendKeys("password123");

        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility=null;");
        assertEquals(null, ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility"));
        
        findElement(By.id("register-form-term-box")).click();
        findElement(By.id("register-authorize-button")).click();
        
        assertEquals(baseUri + "/register", webDriver.getCurrentUrl());
        assertEquals("Please choose a default visibility setting.", findElement(By.xpath("//div[h4[text()='Visibility settings']]/span[@class='orcid-error']")).getText());
    }
    
    @Test
    public void testSubmitRegistrationFormWithNullDefaultVisibilityFromSigninPage() {
        webDriver.get(baseUri + "/signin");
        webDriver.findElement(By.id("switch-to-register-form")).click();
        
        findElement(By.id("register-form-given-names")).sendKeys("test-first-name");
        findElement(By.id("register-form-family-name")).sendKeys("test-last-name");
        findElement(By.name("emailprimary234")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-password")).sendKeys("password123");
        findElement(By.id("register-form-confirm-password")).sendKeys("password123");

        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility=null;");
        assertEquals(null, ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility"));
        
        findElement(By.id("register-form-term-box")).click();
        findElement(By.id("register-authorize-button")).click();
        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        assertEquals(baseUri + "/signin", webDriver.getCurrentUrl());
        assertEquals("Please choose a default visibility setting.", findElement(By.xpath("//div[h4[text()='Visibility settings']]/span[@class='orcid-error']")).getText());
    }

}
