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
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml" })
public class RegistrationControllerTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");
    
    @Resource(name = "registrationController")
    RegistrationController registrationController;

    @Mock
    RegistrationManager registrationManager;

    @Mock
    OrcidSearchManager orcidSearchManager;

    @Mock
    OrcidProfileManager orcidProfileManager;

    @Mock
    EncryptionManager encryptionManager;
    
    @Mock
    NotificationManager notificationManager;
    
    @Mock
    private HttpServletRequest servletRequest;
    
    @Mock
    private HttpServletResponse servletResponse;
    
    @Mock
    private EmailManager emailManager;
    
    @Mock 
    private ProfileEntityManager profileEntityManager;
    
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
        TargetProxyHelper.injectIntoProxy(registrationController, "orcidSearchManager", orcidSearchManager);
        TargetProxyHelper.injectIntoProxy(registrationController, "orcidProfileManager", orcidProfileManager);
        TargetProxyHelper.injectIntoProxy(registrationController, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(registrationController, "notificationManager", notificationManager); 
        TargetProxyHelper.injectIntoProxy(registrationController, "emailManager", emailManager); 
        TargetProxyHelper.injectIntoProxy(registrationController, "profileEntityManager", profileEntityManager);                 
    }

    @Test
    public void testPasswordResetInvalidEmailDataProvidedToForm() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        EmailAddressForm resetPasswordForm = new EmailAddressForm();
        // return a mocked profile -
        ModelAndView modelAndView = registrationController.issuePasswordResetRequest(servletRequest, resetPasswordForm, bindingResult);
        assertEquals("reset_password", modelAndView.getViewName());
        verify(registrationManager, times(0)).resetUserPassword(any(String.class), any(OrcidProfile.class));

    }

    @Test
    public void testPasswordResetUserNotFound() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        BindingResult bindingResult = mock(BindingResult.class);
        EmailAddressForm resetPasswordForm = new EmailAddressForm();
        resetPasswordForm.setUserEmailAddress("jimmy");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail("jimmy")).thenReturn(null);

        // return a mocked profile -
        ModelAndView modelAndView = registrationController.issuePasswordResetRequest(servletRequest, resetPasswordForm, bindingResult);
        assertEquals("reset_password", modelAndView.getViewName());
        verify(registrationManager, times(0)).resetUserPassword(any(String.class), any(OrcidProfile.class));
    }

    @Test
    public void testPasswordResetLinkExpired() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView modelAndView = registrationController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("redirect:/reset-password", modelAndView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToConsolidatedScreenDirectlyWhenNoSecurityQuestion() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(new OrcidProfile());
        ModelAndView modelAndView = registrationController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("password_one_time_reset_optional_security_questions", modelAndView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToSecurityQuestionScreenWhenSecurityQuestionPresent() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        ModelAndView modelAndView = registrationController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("password_one_time_reset_optional_security_questions", modelAndView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testStandAloneSecurityQuestionsView() throws Exception {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String encryptedLink = "this is encrypted. No really, it is.";
        String expiredLink = "this link has expired.";
        when(encryptionManager.decryptForExternalUse(eq(new String(Base64.decodeBase64(encryptedLink), "UTF-8")))).thenReturn(
                "email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(encryptionManager.decryptForExternalUse(eq(new String(Base64.decodeBase64(expiredLink), "UTF-8")))).thenReturn(
                "email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());

        ModelAndView modelAndView = registrationController.buildAnswerSecurityQuestionView(encryptedLink, redirectAttributes);
        assertEquals("answer_security_question", modelAndView.getViewName());
        // assertEquals("What is your all-time favorite sports team?",modelAndView.getModel().get("securityQuestionText"));
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);
        modelAndView = registrationController.buildAnswerSecurityQuestionView(expiredLink, redirectAttributes);
        assertEquals("redirect:/reset-password", modelAndView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testStandaloneSecurityQuestionsRedirectsToStandalonePasswordUponSuccess() throws Exception {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");

        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        when(bindingResult.hasErrors()).thenReturn(true);

        ChangeSecurityQuestionForm changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
        changeSecurityQuestionForm.setSecurityQuestionAnswer("Not the answer");

        // try submit with invalid form
        ModelAndView modelAndView = registrationController.submitSecurityAnswer("encrypted", changeSecurityQuestionForm, bindingResult, redirectAttributes);

        assertEquals("answer_security_question", modelAndView.getViewName());
        assertNull(modelAndView.getModel().get("securityQuestionIncorrect"));

        // Now form is valid but won't match the answer on the server
        when(bindingResult.hasErrors()).thenReturn(false);
        modelAndView = registrationController.submitSecurityAnswer("encrypted", changeSecurityQuestionForm, bindingResult, redirectAttributes);
        assertEquals("answer_security_question", modelAndView.getViewName());
        assertEquals(modelAndView.getModel().get("securityQuestionIncorrect"), true);

        // finally correct the form to match the server
        changeSecurityQuestionForm.setSecurityQuestionAnswer("Answer");
        modelAndView = registrationController.submitSecurityAnswer("encrypted", changeSecurityQuestionForm, bindingResult, redirectAttributes);

        assertEquals("redirect:/one-time-password/encrypted", modelAndView.getViewName());
        assertNull(modelAndView.getModel().get("securityQuestionIncorrect"));
    }

    @Test
    public void testSubmitStandalonePasswordResetSuccess() throws Exception {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(orcidWithSecurityQuestion());
        when(bindingResult.hasErrors()).thenReturn(true);
        PasswordTypeAndConfirmForm passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        ModelAndView failedView = registrationController.confirmPasswordOneTimeResetView(servletRequest, servletResponse, "encrypted link", passwordTypeAndConfirmForm, bindingResult, redirectAttributes);
        verify(orcidProfileManager, times(0)).updatePasswordInformation(orcidWithSecurityQuestion());
        assertEquals("password_one_time_reset", failedView.getViewName());

        // check success flow

        when(bindingResult.hasErrors()).thenReturn(false);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        ModelAndView successView = registrationController
                .confirmPasswordOneTimeResetView(servletRequest, servletResponse, "encrypted link", passwordTypeAndConfirmForm, bindingResult, redirectAttributes);
        verify(orcidProfileManager, times(1)).updatePasswordInformation(orcidWithSecurityQuestion());
        assertTrue(successView.getViewName().equals("redirect:http://testserver.orcid.org/my-orcid") || successView.getViewName().equals("redirect:https://localhost:8443/orcid-web/my-orcid"));
    }

    @Test
    public void testSubmitConsolidatedPasswordReset() throws Exception {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        BindingResult bindingResult = mock(BindingResult.class);

        OneTimeResetPasswordForm oneTimeResetPasswordForm = new OneTimeResetPasswordForm();

        // check validation failure rebuilds the one time view without a
        // redirect
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(bindingResult.hasErrors()).thenReturn(true);
        ModelAndView validationFailedView = registrationController.submitPasswordReset(servletRequest, servletResponse, "encrypted string not expired", oneTimeResetPasswordForm, bindingResult,
                redirectAttributes);
        assertEquals("password_one_time_reset_optional_security_questions", validationFailedView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

        // check success flow
        oneTimeResetPasswordForm.setPassword(Text.valueOf("password"));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        ModelAndView successView = registrationController
                .submitPasswordReset(servletRequest, servletResponse, "encrypted string not expired", oneTimeResetPasswordForm, bindingResult, redirectAttributes);
        assertTrue(successView.getViewName().equals("redirect:http://testserver.orcid.org/my-orcid") || successView.getViewName().equals("redirect:https://localhost:8443/orcid-web/my-orcid"));
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);
        // finally check expiry works

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView expiredView = registrationController.submitPasswordReset(servletRequest, servletResponse, "encrypted string that's expired", oneTimeResetPasswordForm, bindingResult,
                redirectAttributes);
        assertEquals("redirect:/reset-password", expiredView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

    }
    
    @Test
    public void testResendEmailFailIfTheProfileIsAlreadyClaimed() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail("billie@holiday.com")).thenReturn(getOrcidToTestClaimResend(true));
        EmailAddressForm emailAddressForm = new EmailAddressForm();
        //Testing with profile 4444-4444-4444-4446
        emailAddressForm.setUserEmailAddress("billie@holiday.com");
        ModelAndView mav = registrationController.resendClaimEmail(servletRequest, emailAddressForm, bindingResult);
        assertNotNull(mav);
        assertNotNull(mav.getModel());
        assertTrue(mav.getModel().containsKey("alreadyClaimed"));
        assertTrue((Boolean) mav.getModel().get("alreadyClaimed"));
    }

    @Test
    public void testResendClaimEmail() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail("billie@holiday.com")).thenReturn(getOrcidToTestClaimResend(false));
        EmailAddressForm emailAddressForm = new EmailAddressForm();
        //Testing with profile 4444-4444-4444-4446
        emailAddressForm.setUserEmailAddress("billie@holiday.com");
        ModelAndView mav = registrationController.resendClaimEmail(servletRequest, emailAddressForm, bindingResult);
        assertNotNull(mav);
        assertNotNull(mav.getModel());
        assertFalse(mav.getModel().containsKey("alreadyClaimed"));
        assertTrue(mav.getModel().containsKey("claimResendSuccessful"));
        assertTrue((Boolean) mav.getModel().get("claimResendSuccessful"));
    }
    
    @Test
    public void testResetPasswordDontFailIfAnyFieldIsEmtpy() {
        PasswordTypeAndConfirmForm form = new PasswordTypeAndConfirmForm();        
        registrationController.resetPasswordConfirmValidate(form);
        form.setPassword(new Text());
        form.setRetypedPassword(null);
        registrationController.resetPasswordConfirmValidate(form);
        form.setPassword(null);
        form.setRetypedPassword(new Text());
        registrationController.resetPasswordConfirmValidate(form);               
    }
    
    @Test
    @Transactional
    public void testClaim() {
        String email = "public_0000-0000-0000-0001@test.orcid.org";
        SecurityContextHolder.getContext().setAuthentication(null);
        when(orcidProfileManager.retrieveOrcidProfileByEmail(any(String.class))).thenReturn(getOrcidToTestClaim(false));
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn(email);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).thenReturn(null);
        when(request.getLocale()).thenReturn(java.util.Locale.US);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(email, "0000-0000-0000-0001");
        when(emailManager.findOricdIdsByCommaSeparatedEmails(email)).thenReturn(data);       
        when(profileEntityManager.claimProfileAndUpdatePreferences(any(String.class), any(String.class), any(Locale.class), any(Claim.class))).thenReturn(true);
        
        Claim claim = new Claim();
        claim.setActivitiesVisibilityDefault(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.PRIVATE));
        claim.setPassword(Text.valueOf("passwordTest1"));
        claim.setPasswordConfirm(Text.valueOf("passwordTest1"));
        Checkbox checked = new Checkbox();
        checked.setValue(true);
        claim.setSendChangeNotifications(checked);
        claim.setSendOrcidNews(checked);
        claim.setTermsOfUse(checked);
        try {
            claim = registrationController.submitClaimJson(request, response, email, claim);
            assertNotNull(claim);
            assertTrue(claim.getErrors().isEmpty());
            assertTrue("Value was: " + claim.getUrl(), claim.getUrl().endsWith("/my-orcid?recordClaimed"));
        } catch (NoSuchRequestHandlingMethodException e) {
            fail();
        } catch (UnsupportedEncodingException e) {
            fail();
        }
    }
    
    @Test
    public void testStripHtmlFromNames() throws UnsupportedEncodingException {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        Text email = Text.valueOf(System.currentTimeMillis() + "@test.orcid.org");
        
        when(registrationManager.createMinimalRegistration(Matchers.any(OrcidProfile.class),eq(false))).thenAnswer(new Answer<OrcidProfile>(){
            @Override
            public OrcidProfile answer(InvocationOnMock invocation) throws Throwable {
                OrcidProfile orcidProfile = new OrcidProfile();
                orcidProfile.setOrcidIdentifier("0000-0000-0000-0000");
                OrcidBio bio = new OrcidBio();
                ContactDetails contactDetails = new ContactDetails();
                Email newEmail = new Email();
                newEmail.setPrimary(true);
                newEmail.setValue(email.getValue());
                List<Email> emails = new ArrayList<Email>();
                emails.add(newEmail);
                contactDetails.setEmail(emails);
                bio.setContactDetails(contactDetails);
                orcidProfile.setOrcidBio(bio);        
                return orcidProfile;
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
        
        ArgumentCaptor<OrcidProfile> argument = ArgumentCaptor.forClass(OrcidProfile.class);
        ArgumentCaptor<Boolean> argument2 = ArgumentCaptor.forClass(Boolean.class);
        verify(registrationManager).createMinimalRegistration(argument.capture(), argument2.capture());
        assertNotNull(argument.getValue());
        OrcidProfile profile = argument.getValue();
        assertNotNull(profile.getOrcidBio());
        assertNotNull(profile.getOrcidBio().getPersonalDetails());
        assertNotNull(profile.getOrcidBio().getPersonalDetails().getGivenNames());
        assertNotNull(profile.getOrcidBio().getPersonalDetails().getFamilyName());
        
        assertEquals("Given Names", profile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals("Family Name", profile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());        
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
    	assertEquals("email1@test.orcid.org already exists in our system as an unclaimed record. Would you like to <a href=\"http://testserver.orcid.org/resend-claim?email=email1%40test.orcid.org\">resend the claim email</a>?", reg.getEmail().getErrors().get(0));    	
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
    	assertTrue(reg.getEmail().getErrors().get(0).startsWith("email1@test.orcid.org already exists in our system. Would you like to"));    	
    }
    
    private OrcidProfile orcidWithSecurityQuestion() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setSecurityQuestionAnswer("Answer");
        OrcidInternal orcidInternal = new OrcidInternal();
        SecurityDetails securityDetails = new SecurityDetails();
        securityDetails.setSecurityQuestionId(new SecurityQuestionId(3));
        orcidInternal.setSecurityDetails(securityDetails);
        orcidProfile.setOrcidInternal(orcidInternal);
        return orcidProfile;
    }
    
    private OrcidProfile getOrcidToTestClaimResend(boolean claimed) {
        OrcidProfile orcidProfile = new OrcidProfile();
        OrcidIdentifier orcid = new OrcidIdentifier("0000-0000-0000-000X");        
        orcidProfile.setOrcidIdentifier(orcid);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(claimed));
        orcidProfile.setOrcidHistory(orcidHistory);
        OrcidBio orcidBio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        List<Email> emails = new ArrayList<Email>();
        Email email = new Email("billie@holiday.com");
        email.setPrimary(true);
        emails.add(email);
        contactDetails.setEmail(emails);
        orcidBio.setContactDetails(contactDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }    
    
    private OrcidProfile getOrcidToTestClaim(boolean claimed) {
        OrcidProfile orcidProfile = new OrcidProfile();
        OrcidIdentifier orcid = new OrcidIdentifier("0000-0000-0000-0001");        
        orcidProfile.setOrcidIdentifier(orcid);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(claimed));
        orcidProfile.setOrcidHistory(orcidHistory);
        OrcidBio orcidBio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        List<Email> emails = new ArrayList<Email>();
        Email email = new Email("public_0000-0000-0000-0001@test.orcid.org");
        email.setPrimary(true);
        emails.add(email);
        contactDetails.setEmail(emails);
        orcidBio.setContactDetails(contactDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
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
}
