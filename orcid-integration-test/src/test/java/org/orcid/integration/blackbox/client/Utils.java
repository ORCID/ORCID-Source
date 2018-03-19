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
