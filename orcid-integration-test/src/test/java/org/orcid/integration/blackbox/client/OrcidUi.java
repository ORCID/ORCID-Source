package org.orcid.integration.blackbox.client;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidUi {

    private String baseUri;
    private WebDriver webDriver;
    private XPath xpath;

    public OrcidUi(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
        this.xpath = new XPath(webDriver);
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

    public class AccountSwitcherSection {

        public void open() {
            xpath.click("//div[@ng-controller='SwitchUserCtrl']/a[2]");
        }

        public List<AccountToSwitchTo> getAccountsToSwitchTo() {
            List<WebElement> accountRows = xpath.findElements("//div[@ng-controller='SwitchUserCtrl']//ul/li[position() > 2 and position() != last()]");
            return accountRows.stream().map(AccountToSwitchTo::new).collect(Collectors.toList());
        }
    }

    public class AccountToSwitchTo {

        private LocalXPath localXPath;

        private AccountToSwitchTo(WebElement accountElement) {
            this.localXPath = new LocalXPath(accountElement);
        }

        public void switchTo() {
            localXPath.click("a");
        }

        public String getAccountId() {
            return localXPath.findElement("a/ul/li[2]").getText().replaceFirst(".*/", "");
        }
    }
    
}
