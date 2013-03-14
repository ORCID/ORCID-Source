/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.forms.registration.test;

import org.openqa.selenium.WebDriver;

public class WebDriverHelper {

    private String baseUrl;
    private WebDriver webDriver;

    public WebDriverHelper() {

    }

    public WebDriverHelper(String baseUrl, WebDriver webDriver) {
        super();
        this.baseUrl = baseUrl;
        this.webDriver = webDriver;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

}
