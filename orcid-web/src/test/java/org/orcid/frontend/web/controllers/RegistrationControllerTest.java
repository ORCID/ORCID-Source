package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.togglz.junit.TogglzRule;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml", "classpath:statistics-core-context.xml" })
public class RegistrationControllerTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");
    
    @Resource(name = "registrationController")
    RegistrationController registrationController;

    @Mock
    RegistrationManager registrationManager;
    
    @Mock
    private HttpServletRequest servletRequest;
    
    @Mock
    private HttpServletResponse servletResponse;
    
    @Mock
    private EmailManager emailManager;
    
    @Mock
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private OrcidProfileManager orcidProfileManager;
    
    @Mock
    private EncryptionManager encryptionManagerMock;
    
    @Mock
    private EmailManagerReadOnly emailManagerReadOnlyMock;
    
    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(registrationController, "registrationManager", registrationManager);        
        TargetProxyHelper.injectIntoProxy(registrationController, "emailManager", emailManager); 
        TargetProxyHelper.injectIntoProxy(registrationController, "profileEntityManager", profileEntityManager);
        TargetProxyHelper.injectIntoProxy(registrationController, "orcidProfileManager", orcidProfileManager);
        TargetProxyHelper.injectIntoProxy(registrationController, "encryptionManager", encryptionManagerMock);
        TargetProxyHelper.injectIntoProxy(registrationController, "emailManagerReadOnly", emailManagerReadOnlyMock);
        
        when(servletRequest.getLocale()).thenReturn(Locale.ENGLISH);
        
        // Disable all features by default
        togglzRule.disableAll();
    }
    
    @Test
    public void testStripHtmlFromNames() throws UnsupportedEncodingException {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        Text email = Text.valueOf(System.currentTimeMillis() + "@test.orcid.org");
        
        when(registrationManager.createMinimalRegistration(Matchers.any(Registration.class), eq(false), Matchers.any(java.util.Locale.class), Matchers.anyString())).thenAnswer(new Answer<String>(){
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "0000-0000-0000-0000";                
            }
        });
        Registration reg = new Registration();
        org.orcid.pojo.ajaxForm.Visibility fv = new org.orcid.pojo.ajaxForm.Visibility();
        fv.setVisibility(Visibility.PUBLIC);
        reg.setActivitiesVisibilityDefault(fv);        
        reg.setEmail(email);
        reg.setEmailConfirm(email);
        reg.setFamilyNames(Text.valueOf("<button onclick=\"alert('hello')\">Family Name</button>"));
        reg.setGivenNames(Text.valueOf("<button onclick=\"alert('hello')\">Given Names</button>"));
        reg.setPassword(Text.valueOf("1234abcd"));
        reg.setPasswordConfirm(Text.valueOf("1234abcd"));
        reg.setValNumClient(2L);
        reg.setValNumServer(4L);
        Checkbox c = new Checkbox();
        c.setValue(true);
        reg.setTermsOfUse(c);
        reg.setCreationType(Text.valueOf(CreationMethod.API.value()));
        registrationController.setRegisterConfirm(servletRequest, servletResponse, reg);
        
        ArgumentCaptor<Registration> argument1 = ArgumentCaptor.forClass(Registration.class);
        ArgumentCaptor<Boolean> argument2 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Locale> argument3 = ArgumentCaptor.forClass(Locale.class);
        ArgumentCaptor<String> argument4 = ArgumentCaptor.forClass(String.class);        
        verify(registrationManager).createMinimalRegistration(argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertNotNull(argument1.getValue());
        Registration form = argument1.getValue();
        assertEquals("Given Names", form.getGivenNames().getValue());
        assertEquals("Family Name", form.getFamilyNames().getValue());        
    }
    @Test
    public void regActivitiesVisibilityDefaultIsNotNull() {
        Registration reg = new Registration();
        reg.getActivitiesVisibilityDefault().setVisibility(null);
        assertNull(reg.getActivitiesVisibilityDefault().getVisibility());
        reg = registrationController.registerActivitiesVisibilityDefaultValidate(reg);
        assertNotNull(reg);
        assertNotNull(reg.getActivitiesVisibilityDefault().getErrors());
        assertEquals(1, reg.getActivitiesVisibilityDefault().getErrors().size());
        assertEquals("Please choose a default visibility setting.", reg.getActivitiesVisibilityDefault().getErrors().get(0));
    }
    @Test
    public void regEmailsAdditonalValidateNotSameAsOtherAdditional() {
        String additionalEmail = "email1@test.orcid.org";

        Registration reg = new Registration();
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional01 = new Text();
        Text emailAdditional02 = new Text();
        emailAdditional01.setValue(additionalEmail);
        emailAdditional02.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional01);
        emailsAdditionalList.add(emailAdditional02);
        reg.setEmailsAdditional(emailsAdditionalList);
        
        reg = registrationController.regEmailsAdditionalValidate(servletRequest, reg, false, true);
        
        assertNotNull(reg);
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertEquals(1, emailAdditionalListItem.getErrors().size());
            assertEquals("Additional email cannot match another email", emailAdditionalListItem.getErrors().get(0));
        }
    }
    
    @Test
    public void regEmailValidateUnclaimedAccountTest() {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(false);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        when(emailManager.isAutoDeprecateEnableForEmail(email)).thenReturn(true);
        
        Registration reg = new Registration();
        reg.setEmail(Text.valueOf("email1@test.orcid.org"));
        reg.setEmailConfirm(Text.valueOf("email1@test.orcid.org"));
        reg = registrationController.regEmailValidate(servletRequest, reg, false, true);
        
        assertNotNull(reg);
        assertNotNull(reg.getEmail());
        assertNotNull(reg.getEmail().getErrors());
        //No errors, since the account can be auto deprecated
        assertTrue(reg.getEmail().getErrors().isEmpty());       
    }
    
    @Test
    public void regEmailsAdditonalValidateUnclaimedAccountTest() {
        String additionalEmail = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(additionalEmail)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(additionalEmail)).thenReturn(orcid);
        when(profileEntityManager.isProfileClaimedByEmail(additionalEmail)).thenReturn(false);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        when(emailManager.isAutoDeprecateEnableForEmail(additionalEmail)).thenReturn(true);
        
        Registration reg = new Registration();
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional);
        reg.setEmailsAdditional(emailsAdditionalList);
        
        reg = registrationController.regEmailsAdditionalValidate(servletRequest, reg, false, true);
        
        assertNotNull(reg);
        //No errors, since the account can be auto deprecated
        assertTrue(reg.getEmail().getErrors().isEmpty());     
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertTrue(emailAdditionalListItem.getErrors().isEmpty());
        }
    }
    
    @Test
    public void regEmailValidateUnclaimedAccountButEnableAutoDeprecateDisableOnClientTest() {
    	String email = "email1@test.orcid.org";
    	String orcid = "0000-0000-0000-0000";
    	when(emailManager.emailExists(email)).thenReturn(true); 
    	when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
    	when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(false);
    	when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
    	//Set enable auto deprecate off
    	when(emailManager.isAutoDeprecateEnableForEmail(email)).thenReturn(false);
    	when(servletRequest.getScheme()).thenReturn("http");    	
    	
    	Registration reg = new Registration();
    	reg.setEmail(Text.valueOf("email1@test.orcid.org"));
    	reg.setEmailConfirm(Text.valueOf("email1@test.orcid.org"));
    	reg = registrationController.regEmailValidate(servletRequest, reg, false, true);
    	
    	assertNotNull(reg);
    	assertNotNull(reg.getEmail());
    	assertNotNull(reg.getEmail().getErrors());
    	assertEquals(1, reg.getEmail().getErrors().size());
    	assertEquals("email1@test.orcid.org already exists in our system as an unclaimed record. Would you like to <a href='http://testserver.orcid.org/resend-claim?email=email1%40test.orcid.org'>resend the claim email</a>?", reg.getEmail().getErrors().get(0));    	
    }
    
    @Test
    public void regEmailsAdditionalValidateUnclaimedAccountButEnableAutoDeprecateDisableOnClientTest() {
        String additionalEmail = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(additionalEmail)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(additionalEmail)).thenReturn(orcid);
        when(profileEntityManager.isProfileClaimedByEmail(additionalEmail)).thenReturn(false);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        when(emailManager.isAutoDeprecateEnableForEmail(additionalEmail)).thenReturn(false);
        when(servletRequest.getScheme()).thenReturn("http");            
        
        Registration reg = new Registration();
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional);
        reg.setEmailsAdditional(emailsAdditionalList);
        reg = registrationController.regEmailsAdditionalValidate(servletRequest, reg, false, true);
         
        assertNotNull(reg);
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertEquals(1, emailAdditionalListItem.getErrors().size());
            assertEquals("email1@test.orcid.org already exists in our system as an unclaimed record. Would you like to <a href='http://testserver.orcid.org/resend-claim?email=email1%40test.orcid.org'>resend the claim email</a>?", emailAdditionalListItem.getErrors().get(0));
        }
    }
    
    @Test
    public void regEmailValidateDeactivatedAccountTest() {
    	String email = "email1@test.orcid.org";
    	String orcid = "0000-0000-0000-0000";
    	when(emailManager.emailExists(email)).thenReturn(true); 
    	when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
    	when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(false);
    	//Set it as deactivated
    	when(profileEntityManager.isDeactivated(orcid)).thenReturn(true);
    	    	
    	Registration reg = new Registration();
    	reg.setEmail(Text.valueOf("email1@test.orcid.org"));
    	reg.setEmailConfirm(Text.valueOf("email1@test.orcid.org"));
    	reg = registrationController.regEmailValidate(servletRequest, reg, false, true);
    	
    	assertNotNull(reg);
    	assertNotNull(reg.getEmail());
    	assertNotNull(reg.getEmail().getErrors());
    	assertEquals(1, reg.getEmail().getErrors().size());
    	assertTrue(reg.getEmail().getErrors().get(0).startsWith("orcid.frontend.verify.deactivated_email"));
    }
    
    @Test
    public void regEmailsAdditionalValidateDeactivatedAccountTest() {
        String additionalEmail = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(additionalEmail)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(additionalEmail)).thenReturn(orcid);
        when(profileEntityManager.isProfileClaimedByEmail(additionalEmail)).thenReturn(false);
        //Set it as deactivated
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(true);
                
        Registration reg = new Registration();
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional);
        reg.setEmailsAdditional(emailsAdditionalList);
        reg = registrationController.regEmailsAdditionalValidate(servletRequest, reg, false, true);
         
        assertNotNull(reg);
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertEquals(1, emailAdditionalListItem.getErrors().size());
            assertTrue(emailAdditionalListItem.getErrors().get(0).startsWith("orcid.frontend.verify.deactivated_email"));
        }
    }
    
    @Test
    public void regEmailsAdditionalValidateDeactivatedAndUnclaimedAccountTest() {
        String additionalEmail = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(additionalEmail)).thenReturn(true);
        //Set it as unclaimed
        when(emailManager.findOrcidIdByEmail(additionalEmail)).thenReturn(orcid);
        when(profileEntityManager.isProfileClaimedByEmail(additionalEmail)).thenReturn(false);
        //And set it as deactivated
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(true);
    	when(emailManager.isAutoDeprecateEnableForEmail(additionalEmail)).thenReturn(true);
    	
    	Registration reg = new Registration();
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional);
        reg.setEmailsAdditional(emailsAdditionalList);
        reg = registrationController.regEmailsAdditionalValidate(servletRequest, reg, false, true);
    	
    	assertNotNull(reg);
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertEquals(1, emailAdditionalListItem.getErrors().size());
            assertTrue(emailAdditionalListItem.getErrors().get(0).startsWith("orcid.frontend.verify.deactivated_email"));
        }
    }
    
    @Test
    public void regEmailValidateDeactivatedAndUnclaimedAccountTest() {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        //Set it as unclaimed
        when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(false);
        //And set it as deactivated
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(true);
        when(emailManager.isAutoDeprecateEnableForEmail(email)).thenReturn(true);
        
        Registration reg = new Registration();
        reg.setEmail(Text.valueOf("email1@test.orcid.org"));
        reg.setEmailConfirm(Text.valueOf("email1@test.orcid.org"));
        reg = registrationController.regEmailValidate(servletRequest, reg, false, true);
        
        assertNotNull(reg);
        assertNotNull(reg.getEmail());
        assertNotNull(reg.getEmail().getErrors());
        assertEquals(1, reg.getEmail().getErrors().size());
        assertTrue(reg.getEmail().getErrors().get(0).startsWith("orcid.frontend.verify.deactivated_email"));
    }
    
    @Test
    public void regEmailsAdditionalValidateClaimedAccountTest() {
        String additionalEmail = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(additionalEmail)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(additionalEmail)).thenReturn(orcid);
        //Set it as claimed
        when(profileEntityManager.isProfileClaimedByEmail(additionalEmail)).thenReturn(true);
        //And set it as active
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        
        Registration reg = new Registration();
        List<Text> emailsAdditionalList = new ArrayList<Text>();
        Text emailAdditional = new Text();
        emailAdditional.setValue(additionalEmail);
        emailsAdditionalList.add(emailAdditional);
        reg.setEmailsAdditional(emailsAdditionalList);
        reg = registrationController.regEmailsAdditionalValidate(servletRequest, reg, false, true);
        
        assertNotNull(reg);
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertEquals(1, emailAdditionalListItem.getErrors().size());
            assertTrue(emailAdditionalListItem.getErrors().get(0).startsWith("orcid.frontend.verify.duplicate_email"));
        }     
    }
    
    @Test
    public void regEmailValidateClaimedAccountTest() {
    	String email = "email1@test.orcid.org";
    	String orcid = "0000-0000-0000-0000";
    	when(emailManager.emailExists(email)).thenReturn(true); 
    	when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
    	//Set it as claimed
    	when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(true);
    	//And set it as active
    	when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
    	
    	Registration reg = new Registration();
    	reg.setEmail(Text.valueOf("email1@test.orcid.org"));
    	reg.setEmailConfirm(Text.valueOf("email1@test.orcid.org"));
    	reg = registrationController.regEmailValidate(servletRequest, reg, false, true);
    	
    	assertNotNull(reg);
    	assertNotNull(reg.getEmail());
    	assertNotNull(reg.getEmail().getErrors());
    	assertEquals(1, reg.getEmail().getErrors().size());
    	assertTrue(reg.getEmail().getErrors().get(0).startsWith("orcid.frontend.verify.duplicate_email"));    	
    }             
    
    protected OrcidProfile createBasicProfile() {
        OrcidProfile profile = new OrcidProfile();
        profile.setPassword("password");
        profile.setVerificationCode("1234");
        profile.setSecurityQuestionAnswer("random answer");

        OrcidBio bio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("will@semantico.com"));
        bio.setContactDetails(contactDetails);
        profile.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();
        bio.setPersonalDetails(personalDetails);
        personalDetails.setGivenNames(new GivenNames("Will"));
        personalDetails.setFamilyName(new FamilyName("Simpson"));        
        bio.setBiography(new Biography("Will is a software developer"));        
        ResearcherUrls researcherUrls = new ResearcherUrls();
        bio.setResearcherUrls(researcherUrls);
        researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url("http://www.wjrs.co.uk"),null));
        OrcidWorks orcidWorks = new OrcidWorks();
        profile.setOrcidWorks(orcidWorks);        
        return profile;
    }
    
    @Test
    public void verifyEmailTest() throws UnsupportedEncodingException {
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(true);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(emailManager.verifyEmail(email, orcid)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);
        
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, encodedEmail, ra);
        assertNotNull(mav);
        assertEquals("redirect:/my-orcid", mav.getViewName());
        assertTrue(ra.getFlashAttributes().containsKey("emailVerified"));
        assertTrue((Boolean) ra.getFlashAttributes().get("emailVerified"));
        assertFalse(ra.getFlashAttributes().containsKey("primaryEmailUnverified"));
        verify(emailManager, times(1)).verifyEmail(email, orcid);
    }
    
    @Test
    public void verifyEmail_InvalidUserTest() throws UnsupportedEncodingException {
        String orcid = "0000-0000-0000-0000";
        String otherOrcid = "0000-0000-0000-0001";
        String email = "user_1@test.orcid.org";
        String otherEmail = "user_2@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(otherOrcid, otherEmail);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(true);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(emailManager.verifyEmail(email, orcid)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);
        
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, encodedEmail, ra);
        assertNotNull(mav);
        assertEquals("wrong_user", mav.getViewName());
        assertFalse(ra.getFlashAttributes().containsKey("emailVerified"));
        assertFalse(ra.getFlashAttributes().containsKey("primaryEmailUnverified"));
        verify(emailManager, times(0)).verifyEmail(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    public void verifyEmail_InvalidEmailTest() throws UnsupportedEncodingException {
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        // Email doesn't exists
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(false);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(emailManager.verifyEmail(email, orcid)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);
        
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, encodedEmail, ra);
        assertNotNull(mav);
        assertEquals("redirect:/my-orcid", mav.getViewName());
        assertFalse(ra.getFlashAttributes().containsKey("emailVerified"));
        assertFalse(ra.getFlashAttributes().containsKey("primaryEmailUnverified"));
        verify(emailManager, times(0)).verifyEmail(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    public void verifyEmail_NotVerifiedTest() throws UnsupportedEncodingException {
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(true);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        // For some reason the email wasn't verified
        when(emailManager.verifyEmail(email, orcid)).thenReturn(false);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);
        
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, encodedEmail, ra);
        assertNotNull(mav);
        assertEquals("redirect:/my-orcid", mav.getViewName());
        assertTrue(ra.getFlashAttributes().containsKey("emailVerified"));
        assertFalse((Boolean) ra.getFlashAttributes().get("emailVerified"));
        assertFalse(ra.getFlashAttributes().containsKey("primaryEmailUnverified"));
        verify(emailManager, times(1)).verifyEmail(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    public void verifyEmail_UnableToDecryptEmailTest() throws UnsupportedEncodingException {
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenThrow(new EncryptionOperationNotPossibleException());
        
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, encodedEmail, ra);
        assertNotNull(mav);
        assertEquals("redirect:/my-orcid", mav.getViewName());
        assertTrue(ra.getFlashAttributes().containsKey("invalidVerifyUrl"));
        assertTrue((Boolean) ra.getFlashAttributes().get("invalidVerifyUrl"));
        verify(emailManager, times(0)).verifyEmail(Mockito.anyString(), Mockito.anyString());
    }
}
