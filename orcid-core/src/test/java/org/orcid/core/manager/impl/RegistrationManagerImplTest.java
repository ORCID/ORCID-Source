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
package org.orcid.core.manager.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class RegistrationManagerImplTest extends DBUnitTest {

    private static final String CLIENT_ID_AUTODEPRECATE_ENABLED = "APP-5555555555555555";
    private static final String CLIENT_ID_AUTODEPRECATE_DISABLED = "APP-5555555555555556";    
    
    @Resource
    RegistrationManager registrationManager;

    @Resource
    EmailManager emailManager;
    
    @Resource
    OrcidProfileManager orcidProfileManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }       
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Test
    public void testCreateMinimalRegistration() {
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
        Registration form = createRegistrationForm(email, true);        
        String userOrcid = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertNotNull(userOrcid);
        assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
        Map<String, String> map = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map);
        assertEquals(userOrcid, map.get(email));
    }

    @Test
    public void testCreateMinimalRegistrationWithExistingClaimedEmail() {
        //Create the user
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
        Registration form = createRegistrationForm(email, true);        
        String userOrcid = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertNotNull(userOrcid);
        assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
        
        //Then try to create it again
        form = createRegistrationForm(email, true);  
        try {
            registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            fail();
        } catch(InvalidRequestException e) {
            assertEquals("Unable to register user due: Email " + email + " already exists and is claimed, so, it can't be used again", e.getMessage());
        } catch(Exception e) {
            fail();
        }        
    }
    
    @Test
    public void testCreateMinimalRegistrationWithExistingUnclaimedEmailNotAutoDeprecatable() {
        //Create the user, but set it as unclaimed
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
                
        //Create a record by a member
        OrcidProfile orcidProfile = createBasicProfile(email, false, CLIENT_ID_AUTODEPRECATE_DISABLED);
        orcidProfile = orcidProfileManager.createOrcidProfile(orcidProfile, true, true);
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidIdentifier());
        assertNotNull(orcidProfile.getOrcidIdentifier().getPath());
        
        try {
            Registration form = createRegistrationForm(email, true);
            registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            fail();
        } catch(InvalidRequestException e) {
            assertEquals("Unable to register user due: Autodeprecate is not enabled for " + email, e.getMessage());
        } catch(Exception e) {
            fail();
        }     
    }
    
    @Test
    public void testCreateMinimalRegistrationWithExistingEmailThatCanBeAutoDeprecated() {
        //Create the user, but set it as unclaimed
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
        
        //Create a record by a member
        OrcidProfile orcidProfile = createBasicProfile(email, false, CLIENT_ID_AUTODEPRECATE_ENABLED);
        orcidProfile = orcidProfileManager.createOrcidProfile(orcidProfile, true, true);
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidIdentifier());
        assertNotNull(orcidProfile.getOrcidIdentifier().getPath());
        String orcidBefore = orcidProfile.getOrcidIdentifier().getPath();
        
        Map<String, String> map1 = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map1);
        assertEquals(orcidBefore, map1.get(email));  
        
        //Then try to create it again, this time claimed and without source
        Registration form = createRegistrationForm(email, true);
        String orcidAfter = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertTrue(OrcidStringUtils.isValidOrcid(orcidAfter));
        
        assertThat(orcidAfter, is(not(equalTo(orcidBefore))));
        
        Map<String, String> map2 = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map2);
        assertEquals(orcidAfter, map2.get(email));  
    }
    
    @Test
    public void testPasswordIsCommon() {
        assertTrue(registrationManager.passwordIsCommon("baseball"));
        assertTrue(registrationManager.passwordIsCommon("dragon"));
        assertTrue(registrationManager.passwordIsCommon("football"));
        assertTrue(registrationManager.passwordIsCommon("monkey"));
        assertTrue(registrationManager.passwordIsCommon("shadow"));
        assertTrue(registrationManager.passwordIsCommon("password"));
        
        assertFalse(registrationManager.passwordIsCommon("132871384164578961349"));
        assertFalse(registrationManager.passwordIsCommon("advkuwAFdaAdf387922"));
        assertFalse(registrationManager.passwordIsCommon("%@@$£&£$%^!@SSDFgwjhsad"));
    }
    
    private Registration createRegistrationForm(String email, boolean claimed) {
        Registration registration = new Registration();
        registration.setPassword(Text.valueOf("password"));
        registration.setEmail(Text.valueOf(email));
        registration.setFamilyNames(Text.valueOf("User"));
        registration.setGivenNames(Text.valueOf("New"));
        registration.setCreationType(Text.valueOf(CreationMethod.DIRECT.value()));                       
        return registration;
    }
        
    private OrcidProfile createBasicProfile(String email, boolean claimed, String sourceId) {
        OrcidProfile profile = new OrcidProfile();
        profile.setPassword("password");
        profile.setVerificationCode("1234");

        OrcidBio bio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email(email));
        bio.setContactDetails(contactDetails);
        profile.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();
        bio.setPersonalDetails(personalDetails);
        personalDetails.setGivenNames(new GivenNames("New"));
        personalDetails.setFamilyName(new FamilyName("User"));

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(claimed));
        orcidHistory.setCreationMethod(CreationMethod.DIRECT);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile.setOrcidHistory(orcidHistory);
        
        //Set the source
        profile.getOrcidHistory().setSource(new Source(sourceId));
        
        return profile;
    }
    
}
