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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml" })
public class RegistrationControllerTest {

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
    private HttpServletRequest servletRequest;
    
    @Mock
    private HttpServletResponse servletResponse;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        registrationController.setRegistrationManager(registrationManager);
        registrationController.setOrcidSearchManager(orcidSearchManager);
        registrationController.setOrcidProfileManager(orcidProfileManager);
        registrationController.setEncryptionManager(encryptionManager);
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
        OrcidIdentifier orcid = new OrcidIdentifier("0000-0000-0000-0001");        
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

}
