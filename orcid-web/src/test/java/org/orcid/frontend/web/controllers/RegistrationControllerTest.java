package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.common.manager.EventManager;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.core.security.OrcidRoles;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.web.util.RecaptchaVerifier;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.togglz.junit.TogglzRule;


/**
 * Nothing in this file read the DBUnit rows; every collaborator it asserts on
 * was already a mock. The ones the context used to supply silently are explicit
 * now: orcidUrlManager (behind every calculateRedirectUrl assertion),
 * recaptchaVerifier and eventManager (declared on the controller, off the tested
 * paths), and localeManager, which is answered from the real
 * i18n/messages_en.properties so the two assertions on translated text --
 * "Please choose a default visibility setting." and "Additional email cannot
 * match another email" -- keep meaning what they meant.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RegistrationControllerTest {

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
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @Mock
    private RecordEmailSender recordEmailSender;    
    
    @Mock
    private EncryptionManager encryptionManagerMock;
    
    @Mock
    private EmailManagerReadOnly emailManagerReadOnlyMock;
    
    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Mock
    private RecaptchaVerifier recaptchaVerifier;

    @Mock
    private EventManager eventManager;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void before() {
        registrationController = new RegistrationController();

        ReflectionTestUtils.setField(registrationController, "registrationManager", registrationManager);
        ReflectionTestUtils.setField(registrationController, "encryptionManager", encryptionManagerMock);
        ReflectionTestUtils.setField(registrationController, "authenticationManager", authenticationManagerMock);
        ReflectionTestUtils.setField(registrationController, "profileHistoryEventManager", profileHistoryEventManager);
        ReflectionTestUtils.setField(registrationController, "recordEmailSender", recordEmailSender);
        ReflectionTestUtils.setField(registrationController, "recaptchaVerifier", recaptchaVerifier);
        ReflectionTestUtils.setField(registrationController, "eventManager", eventManager);

        // emailManager, profileEntityManager, localeManager and orcidUrlManager
        // are declared on BaseController.
        ReflectionTestUtils.setField(registrationController, BaseController.class, "emailManager", emailManager, EmailManager.class);
        ReflectionTestUtils.setField(registrationController, BaseController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(registrationController, BaseController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(registrationController, BaseController.class, "orcidUrlManager", orcidUrlManager, OrcidUrlManager.class);

        // RegistrationController re-declares emailManagerReadOnly (line 116)
        // over BaseController's copy; Spring's @Resource fills both, and
        // BaseController.isEmailOkForCurrentUser() reads its own.
        ReflectionTestUtils.setField(registrationController, RegistrationController.class, "emailManagerReadOnly", emailManagerReadOnlyMock,
                EmailManagerReadOnly.class);
        ReflectionTestUtils.setField(registrationController, BaseController.class, "emailManagerReadOnly", emailManagerReadOnlyMock,
                EmailManagerReadOnly.class);

        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> resolveFromEnglishBundle(invocation.getArguments()));
        // The value the test properties carried, which every redirect assertion
        // below is written against.
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://testserver.orcid.org");

        when(servletRequest.getLocale()).thenReturn(Locale.ENGLISH);
        
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        
        when(authenticationManagerMock.authenticate(Mockito.any())).thenAnswer(new Answer<UsernamePasswordAuthenticationToken>() {
            @Override
            public UsernamePasswordAuthenticationToken answer(InvocationOnMock invocation) throws Throwable {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("0000-0000-0000-0000", "pwd", Arrays.asList(new SimpleGrantedAuthority(OrcidRoles.ROLE_USER.name())));
                auth.setDetails(new User("0000-0000-0000-0000", "pwd", List.of()));
                return auth;
            }
        });
        
        doNothing().when(profileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString());
        doNothing().when(recordEmailSender).sendWelcomeEmail(Mockito.anyString(), Mockito.anyString());        
    }

    @After
    public void after() {
        // SecurityContextHolder is static process state and several tests below
        // put a user in it; without this it leaks into the next test class in
        // the same JVM.
        SecurityContextHolder.clearContext();
    }

    /**
     * Answers resolveMessage from the shipped English bundle, so the two
     * assertions on translated text keep testing the same thing they did when a
     * real LocaleManager came out of the Spring context. Codes that are not in
     * the bundle come back unchanged, which is what the assertions on raw error
     * codes expect.
     */
    private static final Properties ENGLISH_MESSAGES = loadEnglishMessages();

    private static Properties loadEnglishMessages() {
        Properties properties = new Properties();
        try (InputStream in = RegistrationControllerTest.class.getResourceAsStream("/i18n/messages_en.properties")) {
            if (in != null) {
                properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to read /i18n/messages_en.properties", e);
        }
        return properties;
    }

    private static String resolveFromEnglishBundle(Object[] arguments) {
        String code = (String) arguments[0];
        String pattern = ENGLISH_MESSAGES.getProperty(code);
        if (pattern == null) {
            return code;
        }
        if (arguments.length < 2) {
            return pattern;
        }
        Object[] params = new Object[arguments.length - 1];
        System.arraycopy(arguments, 1, params, 0, params.length);
        return MessageFormat.format(pattern, params);
    }
    
    @Test
    public void testStripHtmlFromNames() throws UnsupportedEncodingException {
        Text email = Text.valueOf(System.currentTimeMillis() + "@test.orcid.org");
        
        when(registrationManager.createMinimalRegistration(Mockito.any(Registration.class), eq(false), Mockito.any(java.util.Locale.class), Mockito.anyString())).thenAnswer(new Answer<String>(){
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

        Redirect redirect = registrationController.setRegisterConfirm(servletRequest, servletResponse, reg);
        assertTrue(redirect.getUrl().endsWith("?justRegistered"));
        
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
        
        registrationController.additionalEmailsValidateOnRegister(servletRequest, reg);
        
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
        
        registrationController.additionalEmailsValidateOnRegister(servletRequest, reg);
        
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
    	assertEquals("orcid.frontend.verify.unclaimed_email", reg.getEmail().getErrors().get(0));    	
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
        registrationController.additionalEmailsValidateOnRegister(servletRequest, reg);
         
        assertNotNull(reg);
        assertNotNull(reg.getEmailsAdditional());
        for(Text emailAdditionalListItem : reg.getEmailsAdditional()){
            assertNotNull(emailAdditionalListItem.getErrors());
            assertEquals(1, emailAdditionalListItem.getErrors().size());
            assertEquals("orcid.frontend.verify.unclaimed_email", emailAdditionalListItem.getErrors().get(0));
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
        registrationController.additionalEmailsValidateOnRegister(servletRequest, reg);
         
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
        registrationController.additionalEmailsValidateOnRegister(servletRequest, reg);
    	
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
        registrationController.additionalEmailsValidateOnRegister(servletRequest, reg);
        
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
    
    @Test
    public void verifyEmailTest() throws UnsupportedEncodingException {
        togglzRule.enable(Features.SEND_EMAIL_ON_EMAIL_LIST_CHANGE);
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(true);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(emailManager.verifyEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, servletResponse, encodedEmail);
        assertNotNull(mav);
        assertEquals("redirect:https://testserver.orcid.org/my-orcid?emailVerified=true", mav.getViewName());
        verify(emailManager, times(1)).verifyEmail(orcid, email);
        verify(profileEntityManager, times(1)).updateLocale(eq(orcid), eq(AvailableLocales.EN));
        verify(recordEmailSender, times(0)).sendEmailListChangeEmail(eq(orcid), Mockito.any());
    }
        
    @Test
    public void verifyEmail_InvalidEmailTest() throws UnsupportedEncodingException {
        togglzRule.enable(Features.SEND_EMAIL_ON_EMAIL_LIST_CHANGE);
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        // Email doesn't exists
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(false);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(emailManager.verifyEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);

        ModelAndView mav = registrationController.verifyEmail(servletRequest, servletResponse, encodedEmail);
        assertNotNull(mav);
        assertEquals("redirect:https://testserver.orcid.org/signin", mav.getViewName());
        verify(emailManager, times(0)).verifyEmail(Mockito.anyString(), Mockito.anyString());
        verify(recordEmailSender, times(0)).sendEmailListChangeEmail(Mockito.anyString(), Mockito.any());
    }
    
    @Test
    public void verifyEmail_NotVerifiedTest() throws UnsupportedEncodingException {
        togglzRule.enable(Features.SEND_EMAIL_ON_EMAIL_LIST_CHANGE);
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenReturn(email);
        when(emailManagerReadOnlyMock.emailExists(email)).thenReturn(true);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        // For some reason the email wasn't verified
        when(emailManager.verifyEmail(orcid, email)).thenReturn(false);
        when(emailManagerReadOnlyMock.isPrimaryEmail(orcid, email)).thenReturn(true);
        when(emailManagerReadOnlyMock.isPrimaryEmailVerified(orcid)).thenReturn(true);
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, servletResponse, encodedEmail);
        assertNotNull(mav);
        assertEquals("redirect:https://testserver.orcid.org/my-orcid?emailVerified=false", mav.getViewName());
        verify(emailManager, times(1)).verifyEmail(Mockito.anyString(), Mockito.anyString());
        verify(recordEmailSender, times(0)).sendEmailListChangeEmail(Mockito.anyString(), Mockito.any());
    }
    
    @Test
    public void verifyEmail_UnableToDecryptEmailTest() throws UnsupportedEncodingException {
        togglzRule.enable(Features.SEND_EMAIL_ON_EMAIL_LIST_CHANGE);
        String orcid = "0000-0000-0000-0000";
        String email = "user_1@test.orcid.org";
        SecurityContextTestUtils.setupSecurityContextForWebUser(orcid, email);
        String encodedEmail = new String(Base64.encodeBase64(email.getBytes()));
        when(encryptionManagerMock.decryptForExternalUse(Mockito.anyString())).thenThrow(new EncryptionOperationNotPossibleException());
        
        ModelAndView mav = registrationController.verifyEmail(servletRequest, servletResponse, encodedEmail);
        assertNotNull(mav);
        assertEquals("redirect:https://testserver.orcid.org/signin?invalidVerifyUrl=true", mav.getViewName());
        verify(emailManager, times(0)).verifyEmail(Mockito.anyString(), Mockito.anyString());
        verify(recordEmailSender, times(0)).sendEmailListChangeEmail(Mockito.anyString(), Mockito.any());
    }
}
