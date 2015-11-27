package org.orcid.integration.blackbox.client;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.base.Predicate;

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
    }

    public EmailsSection getEmailsSection() {
        return new EmailsSection();
    }

    public DelegatesSection getDelegatesSection() {
        return new DelegatesSection();
    }

    public class EmailsSection {

        public void toggleEdit() {
            xpath.click("//a[@ng-click='toggleEmailEdit()']");
        }

        public List<Email> getEmails() {
            List<WebElement> emailRows = xpath.findElements("//td[@ng-controller='EmailEditCtrl']//tr");
            return emailRows.stream().map(Email::new).collect(Collectors.toList());
        }

        public void addEmail(String emailValue) {
            final int numberOfEmailsBefore = getEmails().size();
            WebElement emailInputElement = xpath.waitToBeClickable("//input[@type='email']");
            emailInputElement.sendKeys(emailValue);
            xpath.click("//input[@type='email']/following-sibling::span[1]");
            utils.getWait().until(new Predicate<WebDriver>() {
                @Override
                public boolean apply(WebDriver driver) {
                    return getEmails().size() > numberOfEmailsBefore;
                }
            });
        }
    }

    public class Email {
        private WebElement emailElement;

        private Email(WebElement emailElement) {
            this.emailElement = emailElement;
        }

        public String getEmail() {
            return emailElement.findElement(By.xpath("td[1]")).getText();
        }

        public void delete() {
            EmailsSection emailsSection = getEmailsSection();
            final int numberOfEmailsBefore = emailsSection.getEmails().size();
            emailElement.findElement(By.xpath("td[5]/a")).click();
            xpath.click("//button[@ng-click='deleteEmail(emailSrvc.delEmail)']");
            utils.getWait().until(new Predicate<WebDriver>() {
                @Override
                public boolean apply(WebDriver driver) {
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

        private WebElement delegateElement;

        private Delegate(WebElement delegateElement) {
            this.delegateElement = delegateElement;
        }

        public String getDelegateId() {
            return delegateElement.findElement(By.xpath("td[2]/a")).getText();
        }

        public void revoke() {
            delegateElement.findElement(By.xpath("(td[4]/a)")).click();
            By confirmRevoke = By.xpath("//form[@ng-submit='revoke()']/button");
            utils.getWait().until(ExpectedConditions.elementToBeClickable(confirmRevoke));
            webDriver.findElement(confirmRevoke).click();
            utils.colorBoxIsClosed();
        }
    }

    public class DelegateSearchResult {

        private WebElement delegateElement;

        private DelegateSearchResult(WebElement delegateElement) {
            this.delegateElement = delegateElement;
        }

        public String getDelegateId() {
            return delegateElement.findElement(By.xpath("(td/a)[2]")).getText();
        }

        public void add() {
            delegateElement.findElement(By.xpath("td[3]/span/span")).click();
            By confirmAdd = By.xpath("//form[@ng-submit='addDelegate()']/button");
            utils.getWait().until(ExpectedConditions.elementToBeClickable(confirmAdd));
            webDriver.findElement(confirmAdd).click();
            utils.colorBoxIsClosed();
        }
    }

}
