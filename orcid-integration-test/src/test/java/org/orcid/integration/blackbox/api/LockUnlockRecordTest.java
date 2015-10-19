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
package org.orcid.integration.blackbox.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.integration.blackbox.web.SigninTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class LockUnlockRecordTest extends BlackBoxBase {

	private WebDriver webDriver;

    @Value("${org.orcid.web.adminUser.username}")
    public String adminUserName;
    @Value("${org.orcid.web.adminUser.password}")
    public String adminPassword;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1Username;
    
    private boolean isLockedAlready = false;

    @Test
    public void lockUnlockTest() throws InterruptedException {
    	checkIfAlreadyLocked();
    	if(isLockedAlready) {
    		lockedToUnLocked();
    		unlockedToLocked();
    	} else {
    		unlockedToLocked();
    		lockedToUnLocked();
    	}
    }
    
    private void checkIfAlreadyLocked() {
    	webDriver = new FirefoxDriver();
        webDriver.get(webBaseUrl + "/" + user1OrcidId);
        
        if(webDriver.findElements(By.id("error_locked")).size() != 0) {
        	isLockedAlready = true;
        }
        webDriver.quit();
    }
	
    private void unlockedToLocked() {
    	adminSignIn();
    	WebDriverWait wait = new WebDriverWait(webDriver, 10);
		WebElement lockProfileLink = webDriver.findElement(By.linkText("Lock profile"));
		lockProfileLink.click();
		WebElement lockProfileOrcidId = webDriver.findElement(By.id("orcid_to_lock"));
		lockProfileOrcidId.sendKeys(user1OrcidId);
		WebElement lockButton = webDriver.findElement(By.id("bottom-confirm-lock-profile"));
		lockButton.click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btn-lock")));
		WebElement confirmLockButton = webDriver.findElement(By.id("btn-lock"));
		confirmLockButton.click();
		
		webDriver.quit();
    }

	private void lockedToUnLocked() {
		adminSignIn();
		WebDriverWait wait = new WebDriverWait(webDriver, 10);
		WebElement unLockProfileLink = webDriver.findElement(By.linkText("Unlock profile"));
		unLockProfileLink.click();
		WebElement unLockProfileOrcidId = webDriver.findElement(By.id("orcid_to_unlock"));
		unLockProfileOrcidId.sendKeys(user1OrcidId);
		WebElement unLockButton = webDriver.findElement(By.id("bottom-confirm-unlock-profile"));
		unLockButton.click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btn-unlock")));
		WebElement confirmUnLockButton = webDriver.findElement(By.id("btn-unlock"));
		confirmUnLockButton.click();
		
		webDriver.quit();
    }
	
    private void adminSignIn() {
    	webDriver = new FirefoxDriver();
    	webDriver.get(webBaseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(webBaseUrl + "/admin-actions");
        SigninTest.signIn(webDriver, adminUserName, adminPassword);
        SigninTest.dismissVerifyEmailModal(webDriver);
	}
}
