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
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidUi {

    private String baseUri;
    private WebDriver webDriver;
    private XPath xpath;
    private Utils utils;

    public OrcidUi(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
        this.xpath = new XPath(webDriver);
        this.utils = new Utils(webDriver);
    }

    public AccountSettingsPage getAccountSettingsPage() {
        return new AccountSettingsPage(baseUri, webDriver);
    }

    public AccountSwitcherSection getAccountSwitcherSection() {
        return new AccountSwitcherSection();
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
            
            utils.getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='delegation-mode-warning']")));
        }

        public String getAccountId() {
            return localXPath.findElement("a/ul/li[2]").getText().replaceFirst(".*/", "");
        }
    }

}
