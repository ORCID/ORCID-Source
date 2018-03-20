package org.orcid.integration.blackbox.client;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.jaxb.model.common_v2.Visibility;

/**
 * 
 * @author Will Simpson
 *
 */
public class AccountSettingsPage {

    private String baseUri;
    private WebDriver webDriver;
    private Utils utils;
    private XPath xpath;

    public AccountSettingsPage(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
        this.utils = new Utils(webDriver);
        this.xpath = new XPath(webDriver);
    }

    public void visit() {
        webDriver.get(baseUri + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }

    public EmailsSection getEmailsSection() {
        return new EmailsSection();
    }

    public DelegatesSection getDelegatesSection() {
        utils.getWait().until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return !xpath.isVisible("id('delegates-spinner')");
            }
        });
        return new DelegatesSection();
    }

    public class EmailsSection {
        public void toggleEdit() {
            BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.id("account-settings-toggle-email-edit")), webDriver);
            BBBUtil.ngAwareClick(webDriver.findElement(By.id("account-settings-toggle-email-edit")), webDriver);
        }

        public List<Email> getEmails() {
            List<WebElement> emailRows = xpath.findElements("//emails-form-ng2//tr");
            return emailRows.stream().map(Email::new).collect(Collectors.toList());
        }

        public boolean canAddEmail() {
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            return xpath.isVisible("//input[@type='email']") && !xpath.isVisible("id('addEmailNotAllowed')");
        }

        public void addEmail(String emailValue) {
            final int numberOfEmailsBefore = getEmails().size();
            WebElement emailInputElement = xpath.waitToBeClickable("//input[@type='email']");
            emailInputElement.sendKeys(emailValue);
            xpath.click("//input[@type='email']/following-sibling::span[1]");
            utils.getWait().until(new Function<WebDriver, Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return getEmails().size() > numberOfEmailsBefore;
                }
            });
        }
        
        public void removeEmail(String emailValue) {
            String xpathEmailId = "//tr[@name = 'email' and descendant::td[text() = '" + emailValue + "']]/td[5]/a[@name='delete-email']";
            if(xpath.isPresent(xpathEmailId)) {                
                xpath.click(xpathEmailId);
                String xpathConfirmDeleteEmail = "id('confirm-delete-email_" + emailValue + "')";
                utils.getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathConfirmDeleteEmail)));
                xpath.click(xpathConfirmDeleteEmail);
            }            
        }                
    }

    public class Email {
        private LocalXPath localXPath;

        private Email(WebElement emailElement) {
            this.localXPath = new LocalXPath(emailElement);
        }

        public String getEmail() {
            return localXPath.findElement("td[1]").getText();
        }

        public boolean isPrimary() {
            return localXPath.findElement("td[2]").getText().equalsIgnoreCase("Primary Email");
        }
        
        public Visibility getVisibility() {
            WebElement element = localXPath.findElement("td[6]/descendant::ul/li[not(contains(@class, 'InActive'))]");
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
            
            WebElement element = localXPath.findElement("td[6]/descendant::ul/li[" + index + "]/a");
            element.click();
        }
        
        public void delete() {
            EmailsSection emailsSection = getEmailsSection();
            final int numberOfEmailsBefore = emailsSection.getEmails().size();
            localXPath.click("td[5]/a");
            xpath.click("//button[@ng-click='deleteEmail(emailSrvc.delEmail)']");
            utils.getWait().until(new Function<WebDriver, Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return emailsSection.getEmails().size() < numberOfEmailsBefore;
                }
            });
        }
    }

    public class DelegatesSection {

        public List<Delegate> getDelegates() {
            List<WebElement> delegateRows = xpath.findElements("//div[@id='DelegatesCtrl']/table[@ng-show='delegation.givenPermissionTo.delegationDetails']/tbody/tr");
            return delegateRows.stream().map(Delegate::new).collect(Collectors.toList());
        }

        public void searchForDelegate(String delegateId) {
            xpath.findElement("//div[@id='DelegatesCtrl']//input[@type='text']").sendKeys(delegateId);
            xpath.click("//div[@id='DelegatesCtrl']//input[@type='submit']");
            By ajaxLoader = By.xpath("//div[@id='DelegatesCtrl']//span[@id='ajax-loader']");
            utils.getWait().until(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(ajaxLoader)));
        }

        public List<DelegateSearchResult> getDelegateSearchResults() {
            List<WebElement> delegateSearchResults = xpath.findElements("//div[@id='DelegatesCtrl']//tr[@ng-repeat='result in results']");
            return delegateSearchResults.stream().map(DelegateSearchResult::new).collect(Collectors.toList());
        }
    }

    public class Delegate {
        private LocalXPath localXPath;

        private Delegate(WebElement delegateElement) {
            this.localXPath = new LocalXPath(delegateElement);
        }

        public String getDelegateId() {
            return localXPath.findElement("td[2]/a").getText();
        }

        public void revoke() {
            localXPath.click("(td[4]/a)");
            xpath.click("//form[@ng-submit='revoke()']/button");
            utils.colorBoxIsClosed();
        }
    }

    public class DelegateSearchResult {
        private LocalXPath localXPath;

        private DelegateSearchResult(WebElement delegateElement) {
            this.localXPath = new LocalXPath(delegateElement);
        }

        public String getDelegateId() {
            return localXPath.findElement("(td/a)[2]").getText();
        }

        public void add() {
            int numOfDelegates = 0;
            try {
                numOfDelegates = getDelegatesSection().getDelegates().size();
            } catch (Exception e) {
                // There are no
            }

            final int numberOfDelegatesBefore = numOfDelegates;
            localXPath.click("td[3]/span/span");
            xpath.click("//form[@ng-submit='addDelegate()']/button");
            utils.colorBoxIsClosed();
            utils.getWait().until(new Function<WebDriver, Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return getDelegatesSection().getDelegates().size() > numberOfDelegatesBefore;
                }
            });
        }
    }

}
