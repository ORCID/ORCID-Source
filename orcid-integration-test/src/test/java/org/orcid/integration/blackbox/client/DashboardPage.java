package org.orcid.integration.blackbox.client;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class DashboardPage {
    private String baseUri;
    private WebDriver webDriver;
    private XPath xpath;

    public DashboardPage(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;        
        this.xpath = new XPath(webDriver);
    }
    
    public void visit() {
        webDriver.get(baseUri + "/my-orcid");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }
    
    public OtherNamesSection getOtherNamesSection() {
        return new OtherNamesSection();
    }
    
    public class OtherNamesSection {
        public void toggleEdit() {
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-other-names")), webDriver);
            BBBUtil.ngAwareClick(webDriver.findElement(By.id("open-edit-other-names")), webDriver);
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("aka-popover")), webDriver);
        }
        
        public List<OtherNameElement> getOtherNames() {
            List<WebElement> emailRows = xpath.findElements("//div[@ng-repeat='otherName in otherNamesForm.otherNames']");
            return emailRows.stream().map(OtherNameElement::new).collect(Collectors.toList());
        }
        
        public boolean canAdd() {
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            return xpath.isVisible("//div[id='aka-popover']/descendant::a[@ng-click='addNewModal()']");
        }
        
        public void addOtherName(String otherNameValue) {            
            WebElement addNewButtonElement = xpath.waitToBeClickable("//div[@id='aka-popover']/descendant::a[@ng-click='addNewModal()']/span");
            addNewButtonElement.click();            
            WebElement newElementInput = xpath.waitToBeClickable("//div[@id='aka-popover']/descendant::input[last()]");
            newElementInput.sendKeys(otherNameValue);            
        }
        
        public void saveChanges() {
            xpath.click("//div[@id='aka-popover']/descendant::button[@ng-click='setOtherNamesForm()']");
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("open-edit-other-names")), webDriver);
        }
    }
    
    public class OtherNameElement {
        private LocalXPath localXPath;
        
        private OtherNameElement(WebElement otherNameElement) {
            this.localXPath = new LocalXPath(otherNameElement);
        }
        
        public String getValue() {
            String value = localXPath.findElement("descendant::div[@class='aka']").getText();
            if(PojoUtil.isEmpty(value)) {
                value = localXPath.findElement("descendant::div[@class='aka']/input").getAttribute("value");
            }
            return value;
        }
        
        public Visibility getVisibility() {
            WebElement element = localXPath.findElement("descendant::ul[@class='privacyToggle']/li[not(contains(@class, 'InActive'))]");
            String className = element.getAttribute("class");
            if("publicActive".equalsIgnoreCase(className)) {
                return Visibility.PUBLIC;
            } else if("limitedActive".equalsIgnoreCase(className)) {
                return Visibility.LIMITED;
            } else {
                return Visibility.PRIVATE;
            }
        }
        
        public void changeVisibility(Visibility visibility) {
            int index = 1;
            switch(visibility) {
            case LIMITED:
                index = 2;
                break;
            case PRIVATE:
                index = 3;
                break;
            default:
                index = 1;
                break;
            }            
            WebElement element = localXPath.findElement("descendant::ul/li[" + index + "]/a");
            //Scroll to the element if necessary
            Actions actions = new Actions(webDriver);
            actions.moveToElement(element);
            // TODO: since we have to move to the element position, the privacy
            // popover displays and then we can't click in anything below the
            // popover, so, lets move the mouse to other position and the
            // popover will disappear
            actions.moveByOffset(100, 100);
            actions.perform();
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, 1250)).until(BBBUtil.angularHasFinishedProcessing());
            element.click();
        }
    }
}
