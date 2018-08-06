package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.orcid.core.togglz.Features;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import static org.orcid.integration.blackbox.api.BBBUtil.executeJavaScript;
import static org.orcid.integration.blackbox.api.BBBUtil.findElement;
import static org.orcid.integration.blackbox.api.BBBUtil.findElementByXpath;
import static org.orcid.integration.blackbox.api.BBBUtil.getUrl;
import static org.orcid.integration.blackbox.api.BBBUtil.getUrlAndWait;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementPresence;
import static org.orcid.integration.blackbox.api.BBBUtil.waitForElementVisibility;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author rcpeters
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PopulateOAuthSignInCodeIntegrationTest extends BlackBoxBase {   
    
    private String authorizeScreen = null;
    
    
    @Before    
    public void before() {
        authorizeScreen = this.getWebBaseUrl() + "/oauth/authorize?client_id=" + this.getClient1ClientId() + "&response_type=code&redirect_uri=" + this.getClient1RedirectUri() + "&scope=/activities/read-limited";
    }
    
    @Test
    public void checkNoPrePop() throws JSONException, InterruptedException {
        signout();
        getUrlAndWait(authorizeScreen);
        switchToRegisterForm();
        // make sure we are on the page
        By emailElement= By.xpath("//input[@name='emailprimary234']");
        waitForElementVisibility(emailElement);        
        assertTrue(findElement(emailElement).getAttribute("value").equals(""));
        assertTrue(findElementByXpath("//input[@name='familyNames']").getAttribute("value").equals(""));
        assertTrue(findElementByXpath("//input[@name='givenNames']").getAttribute("value").equals(""));
        // verify we don't populate signin
        switchToLoginForm();
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(""));
        getUrlAndWait(authorizeScreen);
        switchToRegisterForm();
    }

    @Test
    public void emailPrePopulate() throws JSONException, InterruptedException {
        signout();
        // test populating form with email that doesn't exist
        String url = authorizeScreen + "&email=non_existent@test.com&family_names=test_family_names&given_names=test_given_name";
        getUrlAndWait(url);        
        
        By element = By.xpath("//input[@name='emailprimary234']");
        waitForElementVisibility(element);       
        assertTrue(findElement(element).getAttribute("value").equals("non_existent@test.com"));
        assertTrue(findElementByXpath("//input[@name='familyNames']").getAttribute("value").equals("test_family_names"));
        assertTrue(findElementByXpath("//input[@name='givenNames']").getAttribute("value").equals("test_given_name"));
        // verify we don't populate signin
        switchToLoginForm();
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(""));
    }
        
    @Test
    public void emailPrePopulateWithHtmlEncodedEmail() throws JSONException, InterruptedException {
        signout();
        // test populating form with email that doesn't exist
        String url = authorizeScreen + "&email=non_existent%40test.com&family_names=test_family_names&given_names=test_given_name";                
        getUrlAndWait(url);
        
        By element = By.xpath("//input[@name='emailprimary234']");
        waitForElementVisibility(element);
        assertTrue(findElement(element).getAttribute("value").equals("non_existent@test.com"));
        assertTrue(findElementByXpath("//input[@name='familyNames']").getAttribute("value").equals("test_family_names"));
        assertTrue(findElementByXpath("//input[@name='givenNames']").getAttribute("value").equals("test_given_name"));
        // verify we don't populate signin
        switchToLoginForm();
        assertTrue(findElementByXpath("//input[@name='userId']").getAttribute("value").equals(""));
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
        switchToRegisterForm();
        By element = By.xpath("//input[@name='familyNames']");
        waitForElementPresence(element);     
        assertTrue(findElement(element).getAttribute("value").equals("test_family_names"));
                
        element = By.xpath("//input[@name='givenNames']");
        waitForElementPresence(element);    
        assertTrue(findElement(element).getAttribute("value").equals("test_given_name"));
        
        // verify we don't populate signin
        switchToLoginForm();
        element = By.xpath("//input[@name='userId']");
        waitForElementPresence(element);              
        assertTrue(findElement(element).getAttribute("value").equals(""));

        // test populating form with family name
        url = authorizeScreen + "&family_names=test_family_names";
        getUrlAndWait(url);
        switchToRegisterForm();
        element = By.xpath("//input[@name='familyNames']");
        waitForElementPresence(element);                    
        assertTrue(findElement(element).getAttribute("value").equals("test_family_names"));
        WebElement we = findElement(element);

        // test populating form with given name
        url = authorizeScreen + "&given_names=testGivenNames";
        getUrlAndWait(url);
        switchToRegisterForm();
        element = By.xpath("//input[@name='givenNames']");
        waitForElementPresence(element);
        we = findElement(element);
        // angular doesn't always populate the element value attribute on init. Instead we check to make sure the model is populated
        assertEquals("testGivenNames", we.getAttribute("value"));
   }

}
