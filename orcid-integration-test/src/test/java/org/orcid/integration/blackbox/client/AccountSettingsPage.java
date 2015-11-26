package org.orcid.integration.blackbox.client;

import java.util.ArrayList;
import java.util.List;

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

        public List<Email> getEmails(){
            List<WebElement> emailRows = webDriver.findElements(By.xpath("//td[@ng-controller='EmailEditCtrl']//tr"));
            List<Email> emails = new ArrayList<>(emailRows.size());
            for(WebElement emailRow : emailRows){
                Email email = new Email(emailRow);
                emails.add(email);
            }
            return emails;
        }
        
        public void addEmail(String emailValue){
            final int numberOfEmailsBefore = getEmails().size();
            By emailInput = By.xpath("//input[@type='email']");
            getWait().until(ExpectedConditions.elementToBeClickable(emailInput));
            WebElement emailInputElement = webDriver.findElement(emailInput);
            emailInputElement.sendKeys(emailValue);
            By emailAddButton = By.xpath("//input[@type='email']/following-sibling::span[1]");
            webDriver.findElement(emailAddButton).click();
            getWait().until(new Predicate<WebDriver>() {
                @Override public boolean apply(WebDriver driver) {
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
        
        public void delete(){
            EmailsSection emailsSection = getEmailsSection();
            final int numberOfEmailsBefore = emailsSection.getEmails().size();
            emailElement.findElement(By.xpath("td[5]/a")).click();
            By confirmDelete = By.xpath("//button[@ng-click='deleteEmail(emailSrvc.delEmail)']");
            getWait().until(ExpectedConditions.elementToBeClickable(confirmDelete));
            webDriver.findElement(confirmDelete).click();
            getWait().until(new Predicate<WebDriver>() {
                @Override public boolean apply(WebDriver driver) {
                    return emailsSection.getEmails().size() < numberOfEmailsBefore;
                }
            });
        }
    }

}
