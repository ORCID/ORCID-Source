/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.frontend.web.forms.RegistrationForm;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "/orcid-core-context.xml" })
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

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        registrationController.setRegistrationManager(registrationManager);
        registrationController.setOrcidSearchManager(orcidSearchManager);
        registrationController.setOrcidProfileManager(orcidProfileManager);
        registrationController.setEncryptionManager(encryptionManager);
    }

    @Test
    public void testMultipleResearchersFoundByFirstNameLastNameThenProceed() throws Exception {

        HttpSession session = new MockHttpSession();
        assertNull(session.getAttribute("registrationForm"));
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        BindingResult bindingResult = mock(BindingResult.class);
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPassword("password");
        registrationForm.setFamilyName("Bass");
        registrationForm.setGivenNames("Teddy");
        when(servletRequest.getSession()).thenReturn(session);
        // don't forget query builder converts name criteria to lower-case
        when(
                orcidSearchManager.findOrcidsByQuery("given-names:teddy* AND family-name:bass*", RegistrationController.DUP_SEARCH_START,
                        RegistrationController.DUP_SEARCH_ROWS, false)).thenReturn(orcidMessageDetailingRecordsFoundForTeddyBass());

        ModelAndView modelAndView = registrationController.submitRegistration(servletRequest, registrationForm, bindingResult);
        assertEquals("duplicate_researcher", modelAndView.getViewName());
        assertNotNull(modelAndView.getModelMap().get("potentialDuplicates"));
        assertEquals("Registration Form stashed in session", session.getAttribute("registrationForm"), registrationForm);

        when(registrationManager.createMinimalRegistration(any(OrcidProfile.class), any(URI.class))).thenReturn(orcidWithIdentifierOnly());
        ModelAndView redirectToWorkspace = registrationController.progressToConfirmRegistration(servletRequest);
        assertEquals("redirect:/my-orcid", redirectToWorkspace.getViewName());
        assertNull("Registration Form should not be in session", session.getAttribute("registrationForm"));

    }

    @Test
    public void testMultipleResearchersFoundInOAuthFlow() throws Exception {

        HttpSession session = new MockHttpSession();
        assertNull("Password should not be stashed in session", session.getAttribute("password"));
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);

        BindingResult bindingResult = mock(BindingResult.class);
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPassword("password");
        registrationForm.setFamilyName("Bass");
        registrationForm.setGivenNames("Teddy");
        when(servletRequest.getSession()).thenReturn(session);
        // don't forget query builder converts name criteria to lower-case
        when(
                orcidSearchManager.findOrcidsByQuery("given-names:teddy* AND family-name:bass*", RegistrationController.DUP_SEARCH_START,
                        RegistrationController.DUP_SEARCH_ROWS, false)).thenReturn(orcidMessageDetailingRecordsFoundForTeddyBass());

        LoginForm nullLoginForm = null;
        ModelAndView modelAndView = registrationController.sendOAuthRegistration(servletRequest, servletResponse, nullLoginForm, registrationForm, bindingResult);
        assertEquals("oauth_duplicate_researcher", modelAndView.getViewName());
        assertNotNull(modelAndView.getModelMap().get("potentialDuplicates"));
        assertEquals("Password stashed in session", session.getAttribute("password"), "password");

        when(registrationManager.createMinimalRegistration(any(OrcidProfile.class), any(URI.class))).thenReturn(orcidWithIdentifierOnly());
        ModelAndView redirectToWorkspace = registrationController.completeOAuthRegistration(servletRequest, servletResponse, registrationForm);
        assertEquals("redirect:/my-orcid", redirectToWorkspace.getViewName());
        assertNull("Password should not be stashed in session", session.getAttribute("password"));

    }

    private OrcidProfile orcidWithIdentifierOnly() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("orcid");
        return orcidProfile;

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
        verify(registrationManager, times(0)).resetUserPassword(any(OrcidProfile.class), any(URI.class));

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
        verify(registrationManager, times(0)).resetUserPassword(any(OrcidProfile.class), any(URI.class));
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
        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(new OrcidProfile());
        ModelAndView modelAndView = registrationController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("password_one_time_reset_optional_security_questions", modelAndView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToSecurityQuestionScreenWhenSecurityQuestionPresent() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(orcidWithSecurityQuestion());
        ModelAndView modelAndView = registrationController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("redirect:/answer-security-question/randomString", modelAndView.getViewName());
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

        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(orcidWithSecurityQuestion());

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

        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(orcidWithSecurityQuestion());
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
        ModelAndView failedView = registrationController.confirmPasswordOneTimeResetView("encrypted link", passwordTypeAndConfirmForm, bindingResult, redirectAttributes);
        verify(orcidProfileManager, times(0)).updatePasswordInformation(orcidWithSecurityQuestion());
        assertEquals("password_one_time_reset", failedView.getViewName());

        // check success flow

        when(bindingResult.hasErrors()).thenReturn(false);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(orcidWithSecurityQuestion());
        passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        ModelAndView successView = registrationController
                .confirmPasswordOneTimeResetView("encrypted link", passwordTypeAndConfirmForm, bindingResult, redirectAttributes);
        verify(orcidProfileManager, times(1)).updatePasswordInformation(orcidWithSecurityQuestion());
        assertEquals("redirect:/account", successView.getViewName());

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
        ModelAndView validationFailedView = registrationController.submitPasswordReset("encrypted string not expired", oneTimeResetPasswordForm, bindingResult,
                redirectAttributes);
        assertEquals("password_one_time_reset_optional_security_questions", validationFailedView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

        // check success flow
        oneTimeResetPasswordForm.setPassword("password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail("any@orcid.org")).thenReturn(orcidWithSecurityQuestion());
        ModelAndView successView = registrationController
                .submitPasswordReset("encrypted string not expired", oneTimeResetPasswordForm, bindingResult, redirectAttributes);
        assertEquals("redirect:/account", successView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);
        // finally check expiry works

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView expiredView = registrationController.submitPasswordReset("encrypted string that's expired", oneTimeResetPasswordForm, bindingResult,
                redirectAttributes);
        assertEquals("redirect:/reset-password", expiredView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

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

    private OrcidMessage orcidMessageDetailingRecordsFoundForTeddyBass() {
        OrcidMessage orcidMessage = new OrcidMessage();
        List<OrcidProfile> orcids = new ArrayList<OrcidProfile>();
        OrcidProfile orcidProfile1 = new OrcidProfile();
        orcidProfile1.setOrcid("1234X");

        orcids.add(orcidProfile1);
        OrcidSearchResult orcidSearchResult = new OrcidSearchResult();
        orcidSearchResult.setOrcidProfile(orcidProfile1);
        OrcidSearchResults searchResults = new OrcidSearchResults();
        searchResults.getOrcidSearchResult().add(orcidSearchResult);
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;
    }

}
