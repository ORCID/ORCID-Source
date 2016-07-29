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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
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
public class PersonalDetailsTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;
    boolean allSet = false;
    Long publicOtherNameId = null;
    Long limitedOtherNameId = null;

    @Before
    public void setUpData() throws Exception {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        if(!allSet) {
            //Create public other name
            changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
            Long putCode1 = createOtherName("other-name-" + System.currentTimeMillis(), getUser1OrcidId(), accessToken);
            publicOtherNameId = putCode1;
            newOtherNames.add(putCode1);
            
            //Create limited other name
            changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
            Long putCode2 = createOtherName("other-name-" + System.currentTimeMillis(), getUser1OrcidId(), accessToken);
            limitedOtherNameId = putCode2;
            newOtherNames.add(putCode2);
        }
        //Show the workspace 
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        
        //Set biography to public
        changeBiography(null, org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        
        //Set names to public
        changeNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
    }
    
    @After
    public void after() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);        
        if(!newOtherNames.isEmpty()) {
            for(Long putCodeToDelete : newOtherNames) {
                ClientResponse response = memberV2ApiClient.deleteOtherName(getUser1OrcidId(), putCodeToDelete, accessToken);
                assertNotNull(response);
                assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            }
        }
    }
    
    @Test
    public void testGetWithPublicAPI() {
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getBiography().getVisibility());
        
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
        
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        //There should be at least one, but all should be public
        boolean found = false;
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {            
            assertEquals(Visibility.PUBLIC, otherName.getVisibility());
            if(otherName.getPutCode().equals(publicOtherNameId)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void changeToLimitedAndCheckWithPublicAPI() throws Exception {
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        webDriver.get(getWebBaseUrl() + "/signin");
        SigninTest.signIn(webDriver, getUser1OrcidId(), getUser1Password());
        SigninTest.dismissVerifyEmailModal(webDriver);    
        //Change names to limited
        changeNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        //There should be at least one, but all should be public
        boolean found = false;
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {            
            assertEquals(Visibility.PUBLIC, otherName.getVisibility());
            if(otherName.getPutCode().equals(publicOtherNameId)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        
        //Change other names to limited
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        
        getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNull(personalDetails.getOtherNames());        
        
        //Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        
        getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNull(personalDetails.getOtherNames());
        
        ////////////////////////////
        //Rollback to public again//
        ////////////////////////////                
        changeNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        
        //Test that the testGetWithPublicAPI test pass
        testGetWithPublicAPI();
    }
    
    @Test
    public void testGetWithMemberAPI() throws Exception {
        String accessToken = getAccessToken(getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);        
        assertNotNull(getPersonalDetailsResponse);
        PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);        
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        boolean foundPublic = false;
        boolean foundLimited = false;
        
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            //Assert that PRIVATE ones belongs to himself
            if(Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());                
            }
            
            if(otherName.getPutCode().equals(publicOtherNameId)) {
                foundPublic = true;
            }
            
            if(otherName.getPutCode().equals(limitedOtherNameId)) {
                foundLimited = true;
            }
        }
        
        assertTrue(foundPublic);
        assertTrue(foundLimited);
        
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
        
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        webDriver.get(getWebBaseUrl() + "/signin");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, getUser1OrcidId(), getUser1Password());
        SigninTest.dismissVerifyEmailModal(webDriver);
        
        //Change all to LIMITED
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        changeNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        changeBiography(null, org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        
        //Verify they are still visible
        getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);        
        assertNotNull(getPersonalDetailsResponse);        
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);        
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        foundPublic = false;
        foundLimited = false;
        
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            //Assert that PRIVATE ones belongs to himself
            if(Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());                
            }
            
            if(otherName.getPutCode().equals(publicOtherNameId)) {
                foundPublic = true;
            }
            
            if(otherName.getPutCode().equals(limitedOtherNameId)) {
                foundLimited = true;
            }
        }
        
        assertTrue(foundPublic);
        assertTrue(foundLimited);
        
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.LIMITED, personalDetails.getName().getVisibility());
        
        //Change all to PRIVATE
        changeNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE);
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE);
        changeBiography(null, org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE);
        
        //Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);        
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNull(personalDetails.getOtherNames());
        
        //Change all to PUBLIC
        changeNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);  
    }
                              
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value() + " " + ScopePathType.PERSON_READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
