package org.orcid.integration.blackbox.client;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Will Simpson
 *
 */
public class Utils {

    Utils() {
    }
    
    public void colorBoxIsClosed(WebDriverWait wait) {
        wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='colorbox']"))));
    }

}
