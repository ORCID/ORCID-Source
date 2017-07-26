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
package org.orcid.integration.blackbox.api;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

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
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(FirefoxDriver.PROFILE, fireFoxProfile);
        // Marionette does not allow untrusted certs yet
        capabilities.setCapability(FirefoxDriver.MARIONETTE, false);
        webDriver = new FirefoxDriver(capabilities);
    }

    public static WebDriver getWebDriver() {
        return webDriver;
    }
   
}
