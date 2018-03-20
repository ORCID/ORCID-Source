package org.orcid.core.manager.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.SourceManager;
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
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
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
    
    @Resource
    SourceManager sourceManager;
    
    @Mock
    SourceManager mockSourceManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }       
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", mockSourceManager);        
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_ENABLED)));
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @After
    public void after() {
        MockitoAnnotations.initMocks(this);
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
    public void testCreateMinimalRegistrationMultipleEmails() {
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
        String additionalEmail = "new_user_addl_" + System.currentTimeMillis() + "@test.orcid.org";
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional);
        Registration form = createRegistrationFormMultipleEmails(email, emailsAdditionalList, true);        
        String userOrcid = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertNotNull(userOrcid);
        assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
        Map<String, String> map = emailManager.findOricdIdsByCommaSeparatedEmails(email + "," + additionalEmail);
        assertNotNull(map);
        assertEquals(userOrcid, map.get(email));
        assertEquals(userOrcid, map.get(additionalEmail));
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
    public void testCreateMinimalRegistrationWithExistingClaimedEmailAdditional() {
        //Create the user
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
        Registration form = createRegistrationForm(email, true);        
        String userOrcid = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertNotNull(userOrcid);
        assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
        
        //Then try to create it again, with additional email set to primary email of existing user
        String email2 = "new_user2_" + System.currentTimeMillis() + "@test.orcid.org";
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(email);
        emailsAdditionalList.add(emailAdditional);
        form = createRegistrationFormMultipleEmails(email2, emailsAdditionalList, true);  
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
    public void testCreateMinimalRegistrationWithExistingUnclaimedEmailAdditionalNotAutoDeprecatable() {
        //Create the user, but set it as unclaimed
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
                
        //Create a record by a member
        OrcidProfile orcidProfile = createBasicProfile(email, false, CLIENT_ID_AUTODEPRECATE_DISABLED);
        orcidProfile = orcidProfileManager.createOrcidProfile(orcidProfile, true, true);
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidIdentifier());
        assertNotNull(orcidProfile.getOrcidIdentifier().getPath());
        
        try {
            String email2 = "new_user2_" + System.currentTimeMillis() + "@test.orcid.org";
            List<Text> emailsAdditionalList = new ArrayList<Text>();
            Text emailAdditional = new Text();
            emailAdditional.setValue(email);
            emailsAdditionalList.add(emailAdditional);
            Registration form = createRegistrationFormMultipleEmails(email2, emailsAdditionalList, true);  
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
    public void testCreateMinimalRegistrationWithExistingEmailAdditionalThatCanBeAutoDeprecated() {
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
        
        //Then try to create it again, with additional email set to primary email of existing user
        String email2 = "new_user2_" + System.currentTimeMillis() + "@test.orcid.org";
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(email);
        emailsAdditionalList.add(emailAdditional);
        Registration form = createRegistrationFormMultipleEmails(email2, emailsAdditionalList, true);
        String orcidAfter = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertTrue(OrcidStringUtils.isValidOrcid(orcidAfter));
        
        assertThat(orcidAfter, is(not(equalTo(orcidBefore))));
        
        Map<String, String> map2 = emailManager.findOricdIdsByCommaSeparatedEmails(email + "," + email2);
        assertNotNull(map2);
        assertEquals(orcidAfter, map2.get(email));  
        assertEquals(orcidAfter, map2.get(email2));
    }
    
    @Test
    public void testCreateMinimalRegistrationWithExistingTwoEmailsAdditionalThatCanBeAutoDeprecated() {
        //Create a user, but set it as unclaimed
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
        
        //Create another user, but set it as unclaimed
        String email2 = "new_user2_" + System.currentTimeMillis() + "@test.orcid.org";
        
        //Create another record by a member
        OrcidProfile orcidProfile2 = createBasicProfile(email2, false, CLIENT_ID_AUTODEPRECATE_ENABLED);
        orcidProfile2 = orcidProfileManager.createOrcidProfile(orcidProfile2, true, true);
        assertNotNull(orcidProfile2);
        assertNotNull(orcidProfile.getOrcidIdentifier());
        assertNotNull(orcidProfile2.getOrcidIdentifier().getPath());
        String orcidBefore2 = orcidProfile2.getOrcidIdentifier().getPath();
        
        Map<String, String> map2 = emailManager.findOricdIdsByCommaSeparatedEmails(email2);
        assertNotNull(map2);
        assertEquals(orcidBefore2, map2.get(email2)); 
        
        try {
            String email3 = "new_user3_" + System.currentTimeMillis() + "@test.orcid.org";
            List<Text> emailsAdditionalList = new ArrayList<Text>();
            Text emailAdditional = new Text();
            emailAdditional.setValue(email);
            emailsAdditionalList.add(emailAdditional);
            Text emailAdditional2 = new Text();
            emailAdditional2.setValue(email2);
            emailsAdditionalList.add(emailAdditional2);
            Registration form = createRegistrationFormMultipleEmails(email3, emailsAdditionalList, true);
            registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            fail();
        } catch(InvalidRequestException e) {
            assertEquals("Unable to register user due: More than 2 duplicate emails", e.getMessage());
        } catch(Exception e) {
            fail();
        } 
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
    
    private Registration createRegistrationFormMultipleEmails(String email, List<Text> emailsAdditionalList, boolean claimed) {
        Registration registration = new Registration();
        registration.setPassword(Text.valueOf("password"));
        registration.setEmail(Text.valueOf(email));
        registration.setEmailsAdditional(emailsAdditionalList);
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
