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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Will Simpson
 *
 */
public class Utils {

    private WebDriver webDriver;
    
    Utils(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
    
    WebDriverWait getWait() {
        return new WebDriverWait(webDriver, 10);
    }

    public void colorBoxIsClosed() {
        getWait().until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='colorbox']"))));
    }

}
