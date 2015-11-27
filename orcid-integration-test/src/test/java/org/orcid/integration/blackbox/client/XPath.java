package org.orcid.integration.blackbox.client;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
