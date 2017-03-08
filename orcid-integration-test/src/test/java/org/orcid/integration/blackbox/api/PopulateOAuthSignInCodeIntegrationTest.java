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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.orcid.integration.blackbox.api.BBBUtil.executeJavaScript;
import static org.orcid.integration.blackbox.api.BBBUtil.findElement;
import static org.orcid.integration.blackbox.api.BBBUtil.findElementByXpath;
import static org.orcid.integration.blackbox.api.BBBUtil.getUrl;
import static org.orcid.integration.blackbox.api.BBBUtil.getUrlAndWait;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementPresence;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementVisibility;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author rcpeters
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PopulateOAuthSignInCodeIntegrationTest extends BlackBoxBaseRC2 {   
    
    private String authorizeScreen = null;
    
    @Before    
    public void before() {
        authorizeScreen = this.getWebBaseUrl() + "/oauth/authorize?client_id=" + this.getClient1ClientId() + "&response_type=code&redirect_uri=" + this.getClient1RedirectUri() + "&scope=/activities/read-limited";
    }
    
    @Test
    public void checkNoPrePop() throws JSONException, InterruptedException {
        webDriver.get(authorizeScreen);
        // make sure we are on the page
        By emailElement = By.xpath("//input[@name='email']");
        waitForElementVisibility(emailElement);        
        assertTrue(findElement(emailElement).getAttribute("value").equals(""));
        assertTrue(findElementByXpath("//input[@name='familyNames']").getAttribute("value").equals(""));
        assertTrue(findElementByXpath("//input[@name='givenNames']").getAttribute("value").equals(""));
        // verify we don't populate signin
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(""));
    }

    @Test
    public void emailPrePopulate() throws JSONException, InterruptedException {
        // test populating form with email that doesn't exist
        String url = authorizeScreen + "&email=non_existent@test.com&family_names=test_family_names&given_names=test_given_name";
        getUrlAndWait(url);
        
        By element = By.xpath("//input[@id='register-form-email']");
        waitForElementVisibility(element);       
        assertTrue(findElement(element).getAttribute("value").equals("non_existent@test.com"));
        assertTrue(findElementByXpath("//input[@name='familyNames']").getAttribute("value").equals("test_family_names"));
        assertTrue(findElementByXpath("//input[@name='givenNames']").getAttribute("value").equals("test_given_name"));
        // verify we don't populate signin
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(""));

        // test existing email
        url = authorizeScreen + "&email=" + this.getUser1UserName() + "&family_names=test_family_names&given_names=test_given_name";
        getUrl(url);
        element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);               
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1UserName()));
        // make sure register
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(this.getUser1UserName()));

        // populating check populating orcid
        url = authorizeScreen + "&email=spike@milligan.com&family_names=test_family_names&given_names=test_given_name&orcid=" + this.getUser1OrcidId();
        getUrl(url);
        element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);                
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1OrcidId()));
    }
        
    @Test
    public void emailPrePopulateWithHtmlEncodedEmail() throws JSONException, InterruptedException {        
        String scapedEmail = StringEscapeUtils.escapeHtml4(this.getUser1UserName());
        // test populating form with email that doesn't exist
        String url = authorizeScreen + "&email=non_existent%40test.com&family_names=test_family_names&given_names=test_given_name";                
        getUrlAndWait(url);    
        
        By element = By.xpath("//input[@name='email']");
        waitForElementVisibility(element);
        assertTrue(findElement(element).getAttribute("value").equals("non_existent@test.com"));
        assertTrue(findElementByXpath("//input[@name='familyNames']").getAttribute("value").equals("test_family_names"));
        assertTrue(findElementByXpath("//input[@name='givenNames']").getAttribute("value").equals("test_given_name"));
        // verify we don't populate signin
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(""));
                
        // test existing email
        url = authorizeScreen + "&email=" + scapedEmail + "&family_names=test_family_names&given_names=test_given_name";
        getUrlAndWait(url);
        
        element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);                
        WebElement inputElement = findElement(element);
        assertNotNull(inputElement);
        assertNotNull(inputElement.getAttribute("value"));
        assertEquals(scapedEmail, inputElement.getAttribute("value"));
        // make sure register
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(scapedEmail));

        // populating check populating orcid
        url = authorizeScreen + "&email=" + scapedEmail + "&family_names=test_family_names&given_names=test_given_name&orcid=" + this.getUser1OrcidId();
        getUrlAndWait(url);
        element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);                
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1OrcidId()));
    }       

    @Test
    public void orcidIdPrePopulate() throws JSONException, InterruptedException {
        // populating check populating orcid
        String url = authorizeScreen + "&orcid=" + this.getUser1OrcidId();
        getUrlAndWait(url);
        By element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);                                
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1OrcidId()));

        // populating check populating orcid overwrites populating email
        getUrl(authorizeScreen + "&email=spike@milligan.com&family_names=test_family_names&given_names=test_given_name&orcid=" + this.getUser1OrcidId());
        element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);                                
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1OrcidId()));
    }

    @Test
    public void orcidIdPreopulateWithHtmlEncodedOrcid() throws JSONException, InterruptedException {
        // populating check populating orcid
        String encodedOrcid = StringEscapeUtils.escapeHtml4(this.getUser1OrcidId());
        String url = authorizeScreen + "&orcid=" + encodedOrcid;        
        getUrlAndWait(url);
        By element = By.xpath("//input[@name='userId']");
        waitForElementVisibility(element);        
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1OrcidId()));

        // populating check populating orcid overwrites populating email
        getUrl(authorizeScreen + "&email=spike@milligan.com&family_names=test_family_names&given_names=test_given_name&orcid=" + this.getUser1OrcidId());
        element = By.xpath("//input[@name='userId']");
        waitForElementPresence(element);
        assertTrue(findElement(element).getAttribute("value").equals(this.getUser1OrcidId()));
    }
    
    @Test
    public void givenAndFamilyNamesPrepopulate() throws JSONException, InterruptedException {
        // test populating form family and given names
        String url = authorizeScreen + "&family_names=test_family_names&given_names=test_given_name";
        getUrlAndWait(url);
        
        By element = By.xpath("//input[@name='familyNames']");
        waitForElementPresence(element);     
        assertTrue(findElement(element).getAttribute("value").equals("test_family_names"));
                
        element = By.xpath("//input[@name='givenNames']");
        waitForElementPresence(element);    
        assertTrue(findElement(element).getAttribute("value").equals("test_given_name"));
        
        // verify we don't populate signin
        element = By.xpath("//input[@name='userId']");
        waitForElementPresence(element);              
        assertTrue(findElement(element).getAttribute("value").equals(""));

        // test populating form with family name
        url = authorizeScreen + "&family_names=test_family_names";
        getUrlAndWait(url);
        element = By.xpath("//input[@ng-model='registrationForm.familyNames.value']");
        waitForElementPresence(element);                    
        assertTrue(findElement(element).getAttribute("value").equals("test_family_names"));
        WebElement we = findElement(element);
        // angular doesn't always populate the element value attribute on init. Instead we check to make sure the model is populated
        assertEquals("test_family_names", executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.familyNames.value", we).toString());

        // test populating form with given name
        url = authorizeScreen + "&given_names=testGivenNames";
        getUrlAndWait(url);
        element = By.xpath("//input[@ng-model='registrationForm.givenNames.value']");
        waitForElementPresence(element);
        we = findElement(element);
        // angular doesn't always populate the element value attribute on init. Instead we check to make sure the model is populated
        assertEquals("testGivenNames", executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.givenNames.value", we).toString());
   }

}
