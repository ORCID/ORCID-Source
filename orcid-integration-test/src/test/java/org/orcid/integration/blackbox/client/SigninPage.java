package org.orcid.integration.blackbox.client;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Will Simpson
 *
 */
public class SigninPage {

    private String baseUri;
    private WebDriver webDriver;
    private Utils utils = new Utils();
    
    SigninPage(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
    }
    
    public void visit(){
        webDriver.get(baseUri + "/signin");
    }

    public void signIn(String username, String password){
        WebElement emailEl = webDriver.findElement(By.xpath("//input[@name='userId']"));
        emailEl.sendKeys(username);
        WebElement passwordEl = webDriver.findElement(By.xpath("//input[@name='password']"));
        passwordEl.sendKeys(password);
        WebElement buttonEl = webDriver.findElement(By.xpath("//button[@id='form-sign-in-button']"));
        buttonEl.click();
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Sign out']")));
        dismissVerifyEmailModal();
    }
    
    public void dismissVerifyEmailModal() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        List<WebElement> weList = webDriver.findElements(By.xpath("//div[@ng-controller='VerifyEmailCtrl']"));
        if (weList.size() > 0) {// we need to wait for the color box to appear
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@ng-controller='VerifyEmailCtrl' and @orcid-loading='false']")));
            ((JavascriptExecutor) webDriver).executeScript("$.colorbox.close();");
            utils.colorBoxIsClosed(wait);
        }
    }
    
}
