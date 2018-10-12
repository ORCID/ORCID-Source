package org.orcid.integration.blackbox.api.v3.rc2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV3ApiClientImpl;
import org.orcid.integration.blackbox.api.v3.rc2.BlackBoxBaseV3_0_rc2;
import org.orcid.integration.blackbox.api.v3.rc2.MemberV3Rc2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PersonalDetailsTest extends BlackBoxBaseV3_0_rc2 {
    
    
    @Resource(name = "memberV3_0_rc2ApiClient")
    private MemberV3Rc2ApiClientImpl memberV3Rc2ApiClient;
    
    @Resource(name = "publicV3_0_rc2ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClient;
    
    private static String otherName1 = null;
    private static String otherName2 = null;

    private static org.orcid.jaxb.model.v3.rc2.common.Visibility otherNamesLastVisibility = null;

    @BeforeClass
    public static void before() throws Exception {
        // Show the workspace
        signin();

        // Create public other name
        openEditOtherNamesModal();
        deleteOtherNames();
        String otherName1 = "other-name-1-" + System.currentTimeMillis();
        createOtherName(otherName1);
        PersonalDetailsTest.otherName1 = otherName1;

        String otherName2 = "other-name-2-" + System.currentTimeMillis();
        createOtherName(otherName2);
        PersonalDetailsTest.otherName2 = otherName2;

        otherNamesLastVisibility = org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC;
        changeOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());
        saveOtherNamesModal();

        // Set biography to public
        changeBiography(null, org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());

        // Set names to public
        changeNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());
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
        ClientResponse getPersonalDetailsResponse = publicV3ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.v3.rc2.record.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, personalDetails.getBiography().getVisibility());

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // There should be at least one, but all should be public

        boolean found1 = false, found2 = false;

        for (org.orcid.jaxb.model.v3.rc2.record.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);
    }

    @Test
    public void changeToLimitedAndCheckWithPublicAPI() throws Exception {
        // Change names to limited
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED.name());

        ClientResponse getPersonalDetailsResponse = publicV3ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.v3.rc2.record.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // all should be public
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.v3.rc2.record.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Change other names to limited
        setOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV3ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        // Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED.name());

        getPersonalDetailsResponse = publicV3ApiClient.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        ////////////////////////////
        // Rollback to public again//
        ////////////////////////////
        changeNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());
        setOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());
    }

    @Test
    public void testGetWithMemberAPI() throws Exception {
        String accessToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE),
                getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV3Rc2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.v3.rc2.record.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.v3.rc2.record.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.v3.rc2.common.Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());
            }

            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Change all to LIMITED
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED.name());
        setOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);
        changeBiography(null, org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED.name());

        // Verify they are still visible
        getPersonalDetailsResponse = memberV3Rc2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.v3.rc2.record.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.v3.rc2.common.Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());
            }

            if (otherName.getContent().equals(otherName1)) {
                assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, otherName.getVisibility());
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, otherName.getVisibility());
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, personalDetails.getName().getVisibility());

        // Change all to PRIVATE
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PRIVATE.name());
        setOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PRIVATE);
        changeBiography(null, org.orcid.jaxb.model.v3.rc2.common.Visibility.PRIVATE.name());

        // Check nothing is visible
        getPersonalDetailsResponse = memberV3Rc2ApiClient.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        // Change all to PUBLIC
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());
        setOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC.name());
    }
    
    private void setOtherNamesVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility v) throws Exception {
        if (!v.equals(otherNamesLastVisibility)) {
            otherNamesLastVisibility = v;
            openEditOtherNamesModal();
            changeOtherNamesVisibility(v.name());
            saveOtherNamesModal();
        }
    }
}
