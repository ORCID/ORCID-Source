package org.orcid.core.manager.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class RegistrationManagerImplTest extends DBUnitTest {

    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_ID_AUTODEPRECATE_ENABLED = "APP-5555555555555555";
    private static final String CLIENT_ID_AUTODEPRECATE_DISABLED = "APP-5555555555555556";    
    
    @Resource
    RegistrationManager registrationManager;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    EmailManagerReadOnly emailManager;    

    @Mock
    SourceManager mockSourceManager;

    @Mock
    org.orcid.core.manager.v3.SourceManager mockSourceManagerV3;

    @Mock
    EmailFrequencyManager mockEmailFrequencyManager;
    
    @Mock
    private ProfileHistoryEventManager mockProfileHistoryEventManager;
    
    @Resource
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @Mock
    private org.orcid.core.manager.v3.NotificationManager mockV3NotificationManager;
    
    @Resource
    ProfileDao profileDao;
    
    @Resource(name = "notificationManagerV3")
    org.orcid.core.manager.v3.NotificationManager notificationV3Manager;

    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/OrgsEntityData.xml"));
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(registrationManager, "emailFrequencyManager", mockEmailFrequencyManager);
        TargetProxyHelper.injectIntoProxy(affiliationsManager, "sourceManager", mockSourceManagerV3);
        TargetProxyHelper.injectIntoProxy(affiliationsManager, "notificationManager", mockV3NotificationManager);
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_ENABLED)));
        when(mockSourceManagerV3.retrieveActiveSource()).thenReturn(new Source("4444-4444-4444-4441"));
        when(mockV3NotificationManager.sendAmendEmail(Mockito.anyString(), Mockito.any(), Mockito.anyList())).thenReturn(null);
        when(mockEmailFrequencyManager.createOnRegister(Mockito.anyString(), Mockito.any(SendEmailFrequency.class), Mockito.any(SendEmailFrequency.class), Mockito.any(SendEmailFrequency.class), Mockito.anyBoolean())).thenReturn(true);

        TargetProxyHelper.injectIntoProxy(registrationManager, "notificationManager", mockV3NotificationManager);
        doNothing().when(mockV3NotificationManager).sendAutoDeprecateNotification(Mockito.anyString(), Mockito.anyString());               
        
        TargetProxyHelper.injectIntoProxy(profileEntityManager, "profileHistoryEventManager", mockProfileHistoryEventManager);
        Mockito.doNothing().when(mockProfileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString(), Mockito.anyString());
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml"));
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
        TargetProxyHelper.injectIntoProxy(registrationManager, "notificationManager", notificationV3Manager);
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
        ProfileEntity entity = profileDao.find(userOrcid);
        assertNotNull(entity);
        assertEquals("EN", entity.getLocale());
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
        String email = "NEW_user_" + System.currentTimeMillis() + "@test.orcid.org";
        Registration form = createRegistrationForm(email, true);        
        String userOrcid = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
        assertNotNull(userOrcid);
        assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
        
        // Then try to create it again
        form = createRegistrationForm(email, true);  
        try {
            registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            fail();
        } catch(InvalidRequestException e) {
            assertEquals("Unable to register user due: Email " + email + " already exists and is claimed, so, it can't be used again", e.getMessage());
        } catch(Exception e) {
            fail();
        }        
        
        // Then try to create it again with a lower cased email
        form = createRegistrationForm(email.toLowerCase(), true);  
        try {
            registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            fail();
        } catch(InvalidRequestException e) {
            assertEquals("Unable to register user due: Email " + email.toLowerCase() + " already exists and is claimed, so, it can't be used again", e.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        // Then try again with lowercased email with spaces
        String spacedEmail = "   " + email.toLowerCase() + "   ";
        form = createRegistrationForm(spacedEmail, true);  
        try {
            registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            fail();
        } catch(InvalidRequestException e) {
            assertEquals("Unable to register user due: Email " + spacedEmail + " already exists and is claimed, so, it can't be used again", e.getMessage());
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
        try {
            Registration form = createRegistrationForm(email, true);
            String orcidId = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            ProfileEntity entity = profileDao.find(orcidId);
            entity.setClaimed(false);
            entity.setSource(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_DISABLED)));
            profileDao.merge(entity);
        } catch(InvalidRequestException e) {
            fail();
        } catch(Exception e) {
            fail();
        } 
        
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
        try {
            Registration form = createRegistrationForm(email, true);
            String orcidId = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            ProfileEntity entity = profileDao.find(orcidId);
            entity.setClaimed(false);
            entity.setSource(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_DISABLED)));
            profileDao.merge(entity);
        } catch(InvalidRequestException e) {
            fail();
        } catch(Exception e) {
            fail();
        } 
        
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
        String orcidBefore = null;
        try {
            Registration form = createRegistrationForm(email, true);
            orcidBefore = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            ProfileEntity entity = profileDao.find(orcidBefore);
            entity.setClaimed(false);
            entity.setSource(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_ENABLED)));
            profileDao.merge(entity);
        } catch(InvalidRequestException e) {
            fail();
        } catch(Exception e) {
            fail();
        } 
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
        String orcidBefore = null;
        try {
            Registration form = createRegistrationForm(email, true);
            orcidBefore = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            ProfileEntity entity = profileDao.find(orcidBefore);
            entity.setClaimed(false);
            entity.setSource(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_ENABLED)));
            profileDao.merge(entity);
        } catch(InvalidRequestException e) {
            fail();
        } catch(Exception e) {
            fail();
        }
        
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
        String orcid1 = null;
        try {
            Registration form = createRegistrationForm(email, true);
            orcid1 = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            ProfileEntity entity = profileDao.find(orcid1);
            entity.setClaimed(false);
            entity.setSource(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_ENABLED)));
            profileDao.merge(entity);
        } catch(InvalidRequestException e) {
            fail();
        } catch(Exception e) {
            fail();
        }
        
        Map<String, String> map1 = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map1);
        assertEquals(orcid1, map1.get(email));
        
        //Create another user, but set it as unclaimed
        String email2 = "new_user2_" + System.currentTimeMillis() + "@test.orcid.org";
        
        //Create another record by a member
        String orcid2 = null;
        try {
            Registration form = createRegistrationForm(email2, true);
            orcid2 = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            ProfileEntity entity = profileDao.find(orcid2);
            entity.setClaimed(false);
            entity.setSource(new SourceEntity(new ClientDetailsEntity(CLIENT_ID_AUTODEPRECATE_ENABLED)));
            profileDao.merge(entity);
        } catch(InvalidRequestException e) {
            fail();
        } catch(Exception e) {
            fail();
        }
        
        Map<String, String> map2 = emailManager.findOricdIdsByCommaSeparatedEmails(email2);
        assertNotNull(map2);
        assertEquals(orcid2, map2.get(email2)); 
        
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
    
    @Test
    public void testRegisterCleanSpaceCharsOnEmailsTest() {
        char[] chars = { ' ', '\n', '\t', '\u00a0', '\u0020', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008',
                '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' };

        for (char c : chars) {
            long now = System.currentTimeMillis();
            String email = now + "name" + c + "1@test.orcid.org";
            String fixedEmail = now + "name1@test.orcid.org";
            Registration form = createRegistrationForm(email, true);        
            String userOrcid = registrationManager.createMinimalRegistration(form, true, java.util.Locale.ENGLISH, "0.0.0.0");
            assertNotNull(userOrcid);
            assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
            Emails emails = emailManager.getEmails(userOrcid);
            assertEquals(1, emails.getEmails().size());
            assertEquals(fixedEmail, emails.getEmails().get(0).getEmail());
            assertTrue(emailManager.emailExists(email));
            assertTrue(emailManager.emailExists(fixedEmail));
            System.out.println("'" + emails.getEmails().get(0).getEmail() + "': " +  emails.getEmails().get(0).getPath());
        }
               
    }

    @Test
    public void testRegisterWithAffiliationTest() {
        String email = "new_user_" + System.currentTimeMillis() + "@test.orcid.org";
        Registration registrationForm = createRegistrationForm(email, true);
        registrationForm.setAffiliationForm(getAffiliationForm());

        String userOrcid = registrationManager.createMinimalRegistration(registrationForm, true, java.util.Locale.ENGLISH, "0.0.0.0");
        registrationManager.createAffiliation(registrationForm, userOrcid);
        assertNotNull(userOrcid);
        assertTrue(OrcidStringUtils.isValidOrcid(userOrcid));
        List<EmploymentSummary> employmentSummaryList = affiliationsManager.getEmploymentSummaryList(userOrcid);
        assertNotNull(employmentSummaryList);
        assertEquals(1, employmentSummaryList.size());
        // Cleanup
        affiliationsManager.removeAffiliation(userOrcid, employmentSummaryList.get(0).getPutCode());
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

    protected AffiliationForm getAffiliationForm() {
        Date created = new Date();

        AffiliationForm form = new AffiliationForm();
        form.setAffiliationType(Text.valueOf(AffiliationType.EMPLOYMENT.value()));
        created.setDay(String.valueOf(created.getDay()));
        created.setMonth(String.valueOf(created.getMonth()));
        created.setYear(String.valueOf(created.getYear()));
        form.setCreatedDate(created);

        Date lastModified = new Date();
        lastModified.setDay(String.valueOf(created.getDay()));
        lastModified.setMonth(String.valueOf(created.getMonth()));
        lastModified.setYear(String.valueOf(created.getYear()));
        form.setLastModified(lastModified);

        form.setDepartmentName(Text.valueOf("department-name"));

        form.setRoleTitle(Text.valueOf("role-title"));

        Date startDate = new Date();
        startDate.setDay("31");
        startDate.setMonth("12");
        startDate.setYear("2019");
        form.setStartDate(startDate);

        org.orcid.pojo.ajaxForm.Visibility v = new org.orcid.pojo.ajaxForm.Visibility();
        v.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PRIVATE);
        form.setVisibility(v);

        form.setSource(CLIENT_1_ID);

        form.setCity(Text.valueOf("city"));
        form.setCountry(Text.valueOf("US"));
        form.setRegion(Text.valueOf("region"));
        form.setAffiliationName(Text.valueOf("org-1"));
        form.setOrgDisambiguatedId(Text.valueOf("1"));

        return form;
    }
}
