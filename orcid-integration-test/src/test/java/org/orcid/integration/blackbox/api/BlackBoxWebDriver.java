package org.orcid.integration.blackbox.api;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class BlackBoxWebDriver {
   
    final Thread shutdownHook = new Thread()
    {
        @Override
        public void run()
        {
            webDriver.quit();
        }
    };
    
    public BlackBoxWebDriver () {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
    
    private static WebDriver webDriver;
    static {
        FirefoxProfile fireFoxProfile = new FirefoxProfile();
        fireFoxProfile.setAcceptUntrustedCertificates(true);
        FirefoxOptions options = new FirefoxOptions();
        options.setCapability(FirefoxDriver.PROFILE, fireFoxProfile);
        // Marionette does not allow untrusted certs yet
        options.setCapability(FirefoxDriver.MARIONETTE, false);
        webDriver = new FirefoxDriver(options);
    }

    public static WebDriver getWebDriver() {
        return webDriver;
    }
   
}
