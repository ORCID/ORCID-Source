package org.orcid.integration.blackbox.client;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.orcid.integration.blackbox.api.BBBUtil;

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

    public AccountSettingsPage getAccountSettingsPage() {
        return new AccountSettingsPage(baseUri, webDriver);
    }

    public DashboardPage getDashboardPage() {
        return new DashboardPage(baseUri, webDriver); 
    }
    
    public AccountSwitcherSection getAccountSwitcherSection() {
        return new AccountSwitcherSection();
    }

    public class AccountSwitcherSection {

        public void open() {
            BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//div[@ng-controller='SwitchUserCtrl']/a[2]")), webDriver);
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
            BBBUtil.ngAwareClick(localXPath.findElement("a"), webDriver);
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='delegation-mode-warning']")), webDriver);
        }

        public String getAccountId() {
            return localXPath.findElement("a/ul/li[2]").getText().replaceFirst(".*/", "");
        }
    }

}
