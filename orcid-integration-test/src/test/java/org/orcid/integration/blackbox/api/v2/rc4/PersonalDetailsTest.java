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
package org.orcid.integration.blackbox.api.v2.rc4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
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
public class PersonalDetailsTest extends BlackBoxBaseRC4 {
    @Resource(name = "memberV2ApiClient_rc4")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    private static String otherName1 = null;
    private static String otherName2 = null;
    
    @BeforeClass
    public static void before() throws Exception {
        //Show the workspace
        signin();
        
        //Create public other name
        openEditOtherNamesModal();
        String otherName1 = "other-name-1-" + System.currentTimeMillis(); 
        createOtherName(otherName1);
        PersonalDetailsTest.otherName1 = otherName1;
        
        String otherName2 = "other-name-2-" + System.currentTimeMillis(); 
        createOtherName(otherName2);
        PersonalDetailsTest.otherName2 = otherName2;
        
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();
        
        //Set biography to public
        changeBiography(null, org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        
        //Set names to public
        changeNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
    }
    
    @AfterClass
    public static void after() {
        showMyOrcidPage();
        openEditOtherNamesModal();
        deleteOtherNames();
        saveOtherNamesModal();
        signout();
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
        
        boolean found1 = false, found2 = false;
        
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {            
            assertEquals(Visibility.PUBLIC, otherName.getVisibility());
            if(otherName.getContent().equals(otherName1)) {
                found1 = true;                
            } else if(otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2 , found1 && found2);
    }
    
    @Test
    public void changeToLimitedAndCheckWithPublicAPI() throws Exception {
        //Change names to limited
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
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
        //all should be public
        boolean found1 = false, found2 = false;
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {            
            assertEquals(Visibility.PUBLIC, otherName.getVisibility());
            if(otherName.getContent().equals(otherName1)) {
                found1 = true;                
            } else if(otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2 , found1 && found2);
        
        //Change other names to limited
        openEditOtherNamesModal();
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveOtherNamesModal();
        
        getPersonalDetailsResponse = publicV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNull(personalDetails.getOtherNames());        
        
        //Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
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
        changeNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        openEditOtherNamesModal();
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();
        changeBiography(null, org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);        
    }
    
    @Test
    public void testGetWithMemberAPI() throws Exception {
        String accessToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE), getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
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
        boolean found1 = false, found2 = false;
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            //Assert that PRIVATE ones belongs to himself
            if(Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());                
            }
            
            if(otherName.getContent().equals(otherName1)) {
                found1 = true;                
            } else if(otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2 , found1 && found2);
        
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
        
        //Change all to LIMITED
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        openEditOtherNamesModal();
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveOtherNamesModal();
        changeBiography(null, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
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
        found1 = false; 
        found2 = false;
        
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            //Assert that PRIVATE ones belongs to himself
            if(Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());                
            }
            
            if(otherName.getContent().equals(otherName1)) {
                assertEquals(Visibility.LIMITED, otherName.getVisibility());
                found1 = true;                
            } else if(otherName.getContent().equals(otherName2)) {
                assertEquals(Visibility.LIMITED, otherName.getVisibility());
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2 , found1 && found2);
        
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
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
        openEditOtherNamesModal();
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
        saveOtherNamesModal();
        changeBiography(null, org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
        
        //Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);        
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNull(personalDetails.getOtherNames());
        
        //Change all to PUBLIC
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        openEditOtherNamesModal();
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();
        changeBiography(null, org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);  
    }
                              
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE));
    }
}
