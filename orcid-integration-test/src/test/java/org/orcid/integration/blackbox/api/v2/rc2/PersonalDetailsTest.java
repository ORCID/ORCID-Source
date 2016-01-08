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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PersonalDetailsTest extends BlackBoxBase {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();

    private static int WAIT = 10;
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;            
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;    
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.publicClient1.clientId}")
    public String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    public String publicClientSecret;
    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

    @Resource
    private OauthHelper oauthHelper;
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetWithPublicAPI() {
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(user1OrcidId);
        assertNotNull(getPersonalDetailsResponse);
        PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals("This is my bio", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getBiography().getVisibility());
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals("One", personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals("User", personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertThat(personalDetails.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(personalDetails.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.PUBLIC, personalDetails.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, personalDetails.getOtherNames().getOtherNames().get(1).getVisibility());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void changeToLimitedAndCheckWithPublicAPI() throws Exception {
        webDriver = new FirefoxDriver();
        webDriver.get(webBaseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(webBaseUrl + "/signin");
        SigninTest.signIn(webDriver, user1OrcidId, user1Password);
        SigninTest.dismissVerifyEmailModal(webDriver);    
        //Change names to limited
        changeNamesVisibility(Visibility.LIMITED);
        
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(user1OrcidId);
        assertNotNull(getPersonalDetailsResponse);
        PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals("This is my bio", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertThat(personalDetails.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(personalDetails.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
                        
        //Change other names to limited
        changeOtherNamesVisibility(Visibility.LIMITED);
        
        getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(user1OrcidId);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals("This is my bio", personalDetails.getBiography().getContent());
        assertNull(personalDetails.getOtherNames());        
        
        //Change bio to limited
        changeBioVisibility(Visibility.LIMITED);
        
        getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(user1OrcidId);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNull(personalDetails.getOtherNames());
        
        ////////////////////////////
        //Rollback to public again//
        ////////////////////////////                
        changeNamesVisibility(Visibility.PUBLIC);
        changeOtherNamesVisibility(Visibility.PUBLIC);
        changeBioVisibility(Visibility.PUBLIC);
        
        //Test that the testGetWithPublicAPI test pass
        testGetWithPublicAPI();
        webDriver.close();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetWithMemberAPI() throws Exception {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(this.user1OrcidId, accessToken);        
        assertNotNull(getPersonalDetailsResponse);
        PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);        
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals("This is my bio", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertThat(personalDetails.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(personalDetails.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getOtherNames().getOtherNames().get(0).getVisibility().value());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getOtherNames().getOtherNames().get(1).getVisibility().value());
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals("One", personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals("User", personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
        
        webDriver = new FirefoxDriver();
        webDriver.get(webBaseUrl + "/userStatus.json?logUserOut=true");
        webDriver.get(webBaseUrl + "/signin");
        SigninTest.signIn(webDriver, user1OrcidId, user1Password);
        SigninTest.dismissVerifyEmailModal(webDriver);
        
        //Change all to LIMITED
        changeNamesVisibility(Visibility.LIMITED);
        changeOtherNamesVisibility(Visibility.LIMITED);
        changeBioVisibility(Visibility.LIMITED);
        
        //Verify they are still visible
        getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(this.user1OrcidId, accessToken);        
        assertNotNull(getPersonalDetailsResponse);        
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);        
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals("This is my bio", personalDetails.getBiography().getContent());
        assertEquals(Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertThat(personalDetails.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(personalDetails.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.LIMITED.value(), personalDetails.getOtherNames().getOtherNames().get(0).getVisibility().value());
        assertEquals(Visibility.LIMITED.value(), personalDetails.getOtherNames().getOtherNames().get(1).getVisibility().value());
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals("One", personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals("User", personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.LIMITED, personalDetails.getName().getVisibility());
        
        //Change all to PRIVATE
        changeNamesVisibility(Visibility.PRIVATE);
        changeOtherNamesVisibility(Visibility.PRIVATE);
        changeBioVisibility(Visibility.PRIVATE);
        
        //Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(this.user1OrcidId, accessToken);        
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNull(personalDetails.getOtherNames());
        
        //Change all to PUBLIC
        changeNamesVisibility(Visibility.PUBLIC);
        changeOtherNamesVisibility(Visibility.PUBLIC);
        changeBioVisibility(Visibility.PUBLIC);
                
        webDriver.close();
    }
    
    private void changeNamesVisibility(Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        
        try {                                    
            By openEditNames = By.xpath("//div[@id = 'names-section']//span[@id = 'open-edit-names']"); 
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(openEditNames));            
            WebElement openEditNamesElement = webDriver.findElement(openEditNames);
            openEditNamesElement.click();
            
            By namesVisibility = By.xpath("//div[@id = 'names-section']//ul[@class='privacyToggle']/li[" + privacyIndex + "]/a");
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(namesVisibility));
            WebElement namesVisibilityElement = webDriver.findElement(namesVisibility);
            namesVisibilityElement.click();
            
            By saveButton = By.xpath("//div[@id = 'names-section']//ul[@class='workspace-section-toolbar']//li[1]//button");
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(saveButton));
            WebElement button = webDriver.findElement(saveButton);
            button.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Unable to find names-visibility-limited element");
            e.printStackTrace();
            throw e;
        }
    }
    
    private void changeOtherNamesVisibility(Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        
        try {
            By openEditOtherNames = By.xpath("//div[@id = 'other-names-section']//span[@id = 'open-edit-other-names']"); 
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(openEditOtherNames));            
            WebElement openEditOtherNamesElement = webDriver.findElement(openEditOtherNames);
            openEditOtherNamesElement.click();
            
            By namesVisibility = By.xpath("//div[@id = 'other-names-section']//ul[@class='privacyToggle']/li[" + privacyIndex + "]/a");
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(namesVisibility));
            WebElement namesVisibilityElement = webDriver.findElement(namesVisibility);
            namesVisibilityElement.click();
            
            By saveButton = By.xpath("//div[@id = 'other-names-section']//ul[@class='workspace-section-toolbar']//li[2]//button");
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(saveButton));
            WebElement button = webDriver.findElement(saveButton);
            button.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Unable to find biography-visibility-limited element");
            e.printStackTrace();
            throw e;
        }
    }
    
    private void changeBioVisibility(Visibility changeTo) throws Exception {
        int privacyIndex = getPrivacyIndex(changeTo);
        
        try {
            By bioOPrivacySelectorLimited = By.xpath("//div[@id = 'bio-section']//ul[@class='privacyToggle']/li[" + privacyIndex + "]/a"); 
            (new WebDriverWait(webDriver, WAIT)).until(ExpectedConditions.presenceOfElementLocated(bioOPrivacySelectorLimited));            
            WebElement bioOPrivacySelectorLimitedElement = webDriver.findElement(bioOPrivacySelectorLimited);
            bioOPrivacySelectorLimitedElement.click();  
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Unable to find nother-names-visibility-limited element");
            e.printStackTrace();
            throw e;
        }
    } 

    private int getPrivacyIndex(Visibility visibility) {
        switch(visibility) {
        case PUBLIC:
            return 1;
        case LIMITED:
            return 2;
        case PRIVATE:
            return 3;
        default:
            return 1;
        }
    }

    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
