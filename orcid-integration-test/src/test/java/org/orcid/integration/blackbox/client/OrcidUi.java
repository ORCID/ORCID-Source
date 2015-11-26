package org.orcid.integration.blackbox.client;

import org.openqa.selenium.WebDriver;

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

    public SigninPage getSigninPage(){
        return new SigninPage(baseUri, webDriver);
    }
    
    public AccountSettingsPage getAccountSettingsPage(){
        return new AccountSettingsPage(baseUri, webDriver);
    }

    public void quit() {
        webDriver.quit();
    }
}
