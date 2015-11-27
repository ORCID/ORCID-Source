package org.orcid.integration.blackbox.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

/**
 * 
 * @author Will Simpson
 *
 */
public class AccountSettingsPage {

    private String baseUri;
    private WebDriver webDriver;
    private Utils utils = new Utils();

    public AccountSettingsPage(String baseUri, WebDriver webDriver) {
        this.baseUri = baseUri;
        this.webDriver = webDriver;
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

    private WebDriverWait getWait() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        return wait;
    }

    public class EmailsSection {

        public void toggleEdit() {
            WebDriverWait wait = getWait();
            By toggleEmailEdit = By.xpath("//a[@ng-click='toggleEmailEdit()']");
            wait.until(ExpectedConditions.elementToBeClickable(toggleEmailEdit));
            webDriver.findElement(toggleEmailEdit).click();
        }

        public List<Email> getEmails() {
            List<WebElement> emailRows = webDriver.findElements(By.xpath("//td[@ng-controller='EmailEditCtrl']//tr"));
            return emailRows.stream().map(Email::new).collect(Collectors.toList());
        }

        public void addEmail(String emailValue) {
            final int numberOfEmailsBefore = getEmails().size();
            By emailInput = By.xpath("//input[@type='email']");
            getWait().until(ExpectedConditions.elementToBeClickable(emailInput));
            WebElement emailInputElement = webDriver.findElement(emailInput);
            emailInputElement.sendKeys(emailValue);
            By emailAddButton = By.xpath("//input[@type='email']/following-sibling::span[1]");
            webDriver.findElement(emailAddButton).click();
            getWait().until(new Predicate<WebDriver>() {
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
            super();
            this.emailElement = emailElement;
        }

        public String getEmail() {
            return emailElement.findElement(By.xpath("td[1]")).getText();
        }

        public void delete() {
            EmailsSection emailsSection = getEmailsSection();
            final int numberOfEmailsBefore = emailsSection.getEmails().size();
            emailElement.findElement(By.xpath("td[5]/a")).click();
            By confirmDelete = By.xpath("//button[@ng-click='deleteEmail(emailSrvc.delEmail)']");
            getWait().until(ExpectedConditions.elementToBeClickable(confirmDelete));
            webDriver.findElement(confirmDelete).click();
            getWait().until(new Predicate<WebDriver>() {
                @Override
                public boolean apply(WebDriver driver) {
                    return emailsSection.getEmails().size() < numberOfEmailsBefore;
                }
            });
        }
    }

    public class DelegatesSection {

        public List<Delegate> getDelegates() {
            List<WebElement> delegateRows = webDriver.findElements(By
                    .xpath("//div[@id='DelegatesCtrl']/table[@ng-show='delegation.givenPermissionTo.delegationDetails']/tbody/tr"));
            return delegateRows.stream().map(Delegate::new).collect(Collectors.toList());
        }

        public void searchForDelegate(String delegateId) {
            By delegateSearchInput = By.xpath("//div[@id='DelegatesCtrl']//input[@type='text']");
            webDriver.findElement(delegateSearchInput).sendKeys(delegateId);
            By delegateSearchButton = By.xpath("//div[@id='DelegatesCtrl']//input[@type='submit']");
            webDriver.findElement(delegateSearchButton).click();
            By ajaxLoader = By.xpath("//div[@id='DelegatesCtrl']//span[@id='ajax-loader']");
            getWait().until(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(ajaxLoader)));
        }

        public List<DelegateSearchResult> getDelegateSearchResults() {
            List<WebElement> delegateSearchResults = webDriver.findElements(By.xpath("//div[@id='DelegatesCtrl']//tr[@ng-repeat='result in results']"));
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
            getWait().until(ExpectedConditions.elementToBeClickable(confirmRevoke));
            webDriver.findElement(confirmRevoke).click();
            utils.colorBoxIsClosed(getWait());
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
            getWait().until(ExpectedConditions.elementToBeClickable(confirmAdd));
            webDriver.findElement(confirmAdd).click();
            utils.colorBoxIsClosed(getWait());
        }
    }

}
