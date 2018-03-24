package org.orcid.integration.blackbox.client;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 
 * @author Will Simpson
 *
 */
public class XPath {

    private WebDriver webDriver;
    private Utils utils;

    XPath(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.utils = new Utils(webDriver);
    }

    public WebElement findElement(String xpath) {
        By by = By.xpath(xpath);
        utils.getWait().until(ExpectedConditions.presenceOfElementLocated(by));
        return webDriver.findElement(by);
    }

    public boolean isPresent(String xpath) {
        try {
            return webDriver.findElement(By.xpath(xpath)) != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isVisible(String xpath) {
        try {
            WebElement element = findElement(xpath);
            return element != null && element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    List<WebElement> findElements(String xpath) {
        By by = By.xpath(xpath);
        return webDriver.findElements(by);
    }

    public WebElement waitToBeClickable(String xpath) {
        By by = By.xpath(xpath);
        utils.getWait().until(ExpectedConditions.elementToBeClickable(by));
        WebElement webElement = webDriver.findElement(by);
        return webElement;
    }

    public void click(String xpath) {
        WebElement webElement = waitToBeClickable(xpath);
        webElement.click();
    }

}
