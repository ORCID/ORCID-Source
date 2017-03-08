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
package org.orcid.integration.blackbox.api.v2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
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
public class PersonalDetailsTest extends BlackBoxBaseV2Release {
    @Resource(name = "memberV2ApiClient_rc2")
    private org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl memberV2ApiClient_rc2;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient_rc2;

    @Resource(name = "memberV2ApiClient_rc3")
    private org.orcid.integration.blackbox.api.v2.rc3.MemberV2ApiClientImpl memberV2ApiClient_rc3;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient_rc3;

    @Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient_rc4;

    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient_release;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient_release;
    
    private static String otherName1 = null;
    private static String otherName2 = null;

    private static org.orcid.jaxb.model.common_v2.Visibility otherNamesLastVisibility = null;

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

        otherNamesLastVisibility = org.orcid.jaxb.model.common_v2.Visibility.PUBLIC;
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveOtherNamesModal();

        // Set biography to public
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        // Set names to public
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    @AfterClass
    public static void after() {
        showMyOrcidPage();
        openEditOtherNamesModal();
        deleteOtherNames();
        saveOtherNamesModal();
        signout();
    }

    /**
     * 
     * RC2
     * 
     */
    @Test
    public void testGetWithPublicAPI_rc2() {
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc2.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, personalDetails.getBiography().getVisibility());

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // There should be at least one, but all should be public

        boolean found1 = false, found2 = false;

        for (org.orcid.jaxb.model.record_rc2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);
    }

    @Test
    public void changeToLimitedAndCheckWithPublicAPI_rc2() throws Exception {
        // Change names to limited
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc2.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // all should be public
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_rc2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Change other names to limited
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getOtherNames());
        assertNull(personalDetails.getOtherNames().getOtherNames());

        // Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertNull(personalDetails.getOtherNames().getOtherNames());

        ////////////////////////////
        // Rollback to public again//
        ////////////////////////////
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    @Test
    public void testGetWithMemberAPI_rc2() throws Exception {
        String accessToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE),
                getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc2.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_rc2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE.equals(otherName.getVisibility())) {
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
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Change all to LIMITED
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        // Verify they are still visible
        getPersonalDetailsResponse = memberV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());
            }

            if (otherName.getContent().equals(otherName1)) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, otherName.getVisibility());
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, otherName.getVisibility());
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
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, personalDetails.getName().getVisibility());

        // Change all to PRIVATE
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);

        // Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient_rc2.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertNull(personalDetails.getOtherNames().getOtherNames());

        // Change all to PUBLIC
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    /**
     * 
     * RC3
     * 
     */
    @Test
    public void testGetWithPublicAPI_rc3() {
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc3.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, personalDetails.getBiography().getVisibility());

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // There should be at least one, but all should be public

        boolean found1 = false, found2 = false;

        for (org.orcid.jaxb.model.record_rc3.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);
    }

    @Test
    public void changeToLimitedAndCheckWithPublicAPI_rc3() throws Exception {
        // Change names to limited
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc3.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // all should be public
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_rc3.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Change other names to limited
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getOtherNames());
        assertNull(personalDetails.getOtherNames().getOtherNames());

        // Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertNull(personalDetails.getOtherNames().getOtherNames());

        ////////////////////////////
        // Rollback to public again//
        ////////////////////////////
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    @Test
    public void testGetWithMemberAPI_rc3() throws Exception {
        String accessToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE),
                getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc3.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_rc3.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE.equals(otherName.getVisibility())) {
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
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Change all to LIMITED
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        // Verify they are still visible
        getPersonalDetailsResponse = memberV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc3.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());
            }

            if (otherName.getContent().equals(otherName1)) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, otherName.getVisibility());
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, otherName.getVisibility());
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
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, personalDetails.getName().getVisibility());

        // Change all to PRIVATE
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);

        // Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient_rc3.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertNull(personalDetails.getOtherNames().getOtherNames());

        // Change all to PUBLIC
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    /**
     * 
     * RC4
     * 
     */
    @Test
    public void testGetWithPublicAPI_rc4() {
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc4.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, personalDetails.getBiography().getVisibility());

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // There should be at least one, but all should be public

        boolean found1 = false, found2 = false;

        for (org.orcid.jaxb.model.record_rc4.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);
    }

    @Test
    public void changeToLimitedAndCheckWithPublicAPI_rc4() throws Exception {
        // Change names to limited
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc4.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // all should be public
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_rc4.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Change other names to limited
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        // Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        ////////////////////////////
        // Rollback to public again//
        ////////////////////////////
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    @Test
    public void testGetWithMemberAPI_rc4() throws Exception {
        String accessToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE),
                getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_rc4.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_rc4.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE.equals(otherName.getVisibility())) {
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
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Change all to LIMITED
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        // Verify they are still visible
        getPersonalDetailsResponse = memberV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc4.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());
            }

            if (otherName.getContent().equals(otherName1)) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, otherName.getVisibility());
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, otherName.getVisibility());
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
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, personalDetails.getName().getVisibility());

        // Change all to PRIVATE
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);

        // Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient_rc4.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());        

        // Change all to PUBLIC
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }
    
    /**
     * 
     * Release
     * 
     */
    @Test
    public void testGetWithPublicAPI_release() {
        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_v2.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, personalDetails.getBiography().getVisibility());

        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals(getUser1GivenName(), personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), personalDetails.getName().getFamilyName().getContent());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals(getUser1CreditName(), personalDetails.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // There should be at least one, but all should be public

        boolean found1 = false, found2 = false;

        for (org.orcid.jaxb.model.record_v2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);
    }

    @Test
    public void changeToLimitedAndCheckWithPublicAPI_release() throws Exception {
        // Change names to limited
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        ClientResponse getPersonalDetailsResponse = publicV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_v2.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        // all should be public
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_v2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }
        assertTrue("found1: " + found1 + " found2: " + found2, found1 && found2);

        // Change other names to limited
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        // Change bio to limited
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        getPersonalDetailsResponse = publicV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId());
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        ////////////////////////////
        // Rollback to public again//
        ////////////////////////////
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    @Test
    public void testGetWithMemberAPI_release() throws Exception {
        String accessToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE),
                getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getPersonalDetailsResponse = memberV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        org.orcid.jaxb.model.record_v2.PersonalDetails personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        boolean found1 = false, found2 = false;
        for (org.orcid.jaxb.model.record_v2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.equals(otherName.getVisibility())) {
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
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, personalDetails.getName().getVisibility());

        // Change all to LIMITED
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        // Verify they are still visible
        getPersonalDetailsResponse = memberV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals(getUser1Bio(), personalDetails.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.value(), personalDetails.getBiography().getVisibility().value());
        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());

        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_v2.OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            // Assert that PRIVATE ones belongs to himself
            if (org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.equals(otherName.getVisibility())) {
                assertEquals(getClient2ClientId(), otherName.getSource().retrieveSourcePath());
            }

            if (otherName.getContent().equals(otherName1)) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, otherName.getVisibility());
                found1 = true;
            } else if (otherName.getContent().equals(otherName2)) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, otherName.getVisibility());
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
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, personalDetails.getName().getVisibility());

        // Change all to PRIVATE
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);

        // Check nothing is visible
        getPersonalDetailsResponse = memberV2ApiClient_release.viewPersonalDetailsXML(getUser1OrcidId(), accessToken);
        assertNotNull(getPersonalDetailsResponse);
        personalDetails = getPersonalDetailsResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());

        // Change all to PUBLIC
        showMyOrcidPage();
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }
    
    private void setOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility v) throws Exception {
        if (!v.equals(otherNamesLastVisibility)) {
            otherNamesLastVisibility = v;
            openEditOtherNamesModal();
            changeOtherNamesVisibility(v);
            saveOtherNamesModal();
        }
    }
}
