package org.orcid.integration.blackbox.client;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidUi {

    private String baseUri;
    private WebDriver webDriver;

    public OrcidUi(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
    }

    public SigninPage getSigninPage() {
        return new SigninPage(baseUri, webDriver);
    }

    public AccountSettingsPage getAccountSettingsPage() {
        return new AccountSettingsPage(baseUri, webDriver);
    }

    public AccountSwitcherSection getAccountSwitcherSection() {
        return new AccountSwitcherSection();
    }

    public void quit() {
        webDriver.quit();
    }
    
    private WebDriverWait getWait() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        return wait;
    }

    public class AccountSwitcherSection {
        
        public void open(){
            By openButton = By.xpath("//div[@ng-controller='SwitchUserCtrl']/a[2]");
            getWait().until(ExpectedConditions.elementToBeClickable(openButton));
            webDriver.findElement(openButton).click();
        }

        public List<AccountToSwitchTo> getAccountsToSwitchTo() {
            List<WebElement> accountRows = webDriver.findElements(By.xpath("//div[@ng-controller='SwitchUserCtrl']//ul/li[position() > 2 and position() != last()]"));
            return accountRows.stream().map(AccountToSwitchTo::new).collect(Collectors.toList());
        }
    }

    public class AccountToSwitchTo {

        private WebElement accountElement;

        private AccountToSwitchTo(WebElement accountElement) {
            this.accountElement = accountElement;
        }
        
        public void switchTo(){
            accountElement.findElement(By.xpath("a")).click();
        }
        
        public String getAccountId(){
            return accountElement.findElement(By.xpath("a/ul/li[2]")).getText().replaceFirst(".*/", "");
        }
    }
}
