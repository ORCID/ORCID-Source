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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.pojo.PasswordResetRequest;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml" })
public class PasswordResetControllerTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    @Resource(name = "passwordResetController")
    private PasswordResetController passwordResetController;

    @Mock
    private RegistrationManager registrationManager;

    @Mock
    private OrcidProfileManager orcidProfileManager;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

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
        TargetProxyHelper.injectIntoProxy(passwordResetController, "registrationManager", registrationManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "orcidProfileManager", orcidProfileManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "encryptionManager", encryptionManager);
    }

    @Test
    public void testPasswordResetUserNotFound() {
        when(orcidProfileManager.retrieveOrcidProfileByEmail(Mockito.anyString(), Mockito.any(LoadOptions.class))).thenReturn(null);
        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest = passwordResetController.issuePasswordResetRequest(resetRequest);
        assertNotNull(resetRequest.getErrors());
        assertFalse(resetRequest.getErrors().isEmpty());
    }

    @Test
    public void testPasswordResetUserDeactivated() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setDeactivationDate(new DeactivationDate(date));

        OrcidProfile deactivatedProfile = new OrcidProfile();
        deactivatedProfile.setOrcidHistory(orcidHistory);
        
        when(orcidProfileManager.retrieveOrcidProfileByEmail(Mockito.anyString(), Mockito.any(LoadOptions.class))).thenReturn(deactivatedProfile);
        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest = passwordResetController.issuePasswordResetRequest(resetRequest);
        assertNotNull(resetRequest.getErrors());
        assertFalse(resetRequest.getErrors().isEmpty());
    }

    @Test
    public void testPasswordResetLinkExpired() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("redirect:/reset-password", modelAndView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToConsolidatedScreenDirectlyWhenNoSecurityQuestion() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(new OrcidProfile());
        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("password_one_time_reset_optional_security_questions", modelAndView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToSecurityQuestionScreenWhenSecurityQuestionPresent() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("password_one_time_reset_optional_security_questions", modelAndView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testStandAloneSecurityQuestionsView() throws Exception {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String encryptedLink = "this is encrypted. No really, it is.";
        String expiredLink = "this link has expired.";
        when(encryptionManager.decryptForExternalUse(eq(new String(Base64.decodeBase64(encryptedLink), "UTF-8"))))
                .thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(encryptionManager.decryptForExternalUse(eq(new String(Base64.decodeBase64(expiredLink), "UTF-8"))))
                .thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());

        ModelAndView modelAndView = passwordResetController.buildAnswerSecurityQuestionView(encryptedLink, redirectAttributes);
        assertEquals("answer_security_question", modelAndView.getViewName());
        // assertEquals("What is your all-time favorite sports
        // team?",modelAndView.getModel().get("securityQuestionText"));
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);
        modelAndView = passwordResetController.buildAnswerSecurityQuestionView(expiredLink, redirectAttributes);
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
        ModelAndView modelAndView = passwordResetController.submitSecurityAnswer("encrypted", changeSecurityQuestionForm, bindingResult, redirectAttributes);

        assertEquals("answer_security_question", modelAndView.getViewName());
        assertNull(modelAndView.getModel().get("securityQuestionIncorrect"));

        // Now form is valid but won't match the answer on the server
        when(bindingResult.hasErrors()).thenReturn(false);
        modelAndView = passwordResetController.submitSecurityAnswer("encrypted", changeSecurityQuestionForm, bindingResult, redirectAttributes);
        assertEquals("answer_security_question", modelAndView.getViewName());
        assertEquals(modelAndView.getModel().get("securityQuestionIncorrect"), true);

        // finally correct the form to match the server
        changeSecurityQuestionForm.setSecurityQuestionAnswer("Answer");
        modelAndView = passwordResetController.submitSecurityAnswer("encrypted", changeSecurityQuestionForm, bindingResult, redirectAttributes);

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
        ModelAndView failedView = passwordResetController.confirmPasswordOneTimeResetView(servletRequest, servletResponse, "encrypted link", passwordTypeAndConfirmForm,
                bindingResult, redirectAttributes);
        verify(orcidProfileManager, times(0)).updatePasswordInformation(orcidWithSecurityQuestion());
        assertEquals("password_one_time_reset", failedView.getViewName());

        // check success flow

        when(bindingResult.hasErrors()).thenReturn(false);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        ModelAndView successView = passwordResetController.confirmPasswordOneTimeResetView(servletRequest, servletResponse, "encrypted link", passwordTypeAndConfirmForm,
                bindingResult, redirectAttributes);
        verify(orcidProfileManager, times(1)).updatePasswordInformation(orcidWithSecurityQuestion());
        assertTrue(successView.getViewName().equals("redirect:http://testserver.orcid.org/my-orcid")
                || successView.getViewName().equals("redirect:https://localhost:8443/orcid-web/my-orcid"));
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
        ModelAndView validationFailedView = passwordResetController.submitPasswordReset(servletRequest, servletResponse, "encrypted string not expired",
                oneTimeResetPasswordForm, bindingResult, redirectAttributes);
        assertEquals("password_one_time_reset_optional_security_questions", validationFailedView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

        // check success flow
        oneTimeResetPasswordForm.setPassword(Text.valueOf("password"));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(orcidWithSecurityQuestion());
        ModelAndView successView = passwordResetController.submitPasswordReset(servletRequest, servletResponse, "encrypted string not expired", oneTimeResetPasswordForm,
                bindingResult, redirectAttributes);
        assertTrue(successView.getViewName().equals("redirect:http://testserver.orcid.org/my-orcid")
                || successView.getViewName().equals("redirect:https://localhost:8443/orcid-web/my-orcid"));
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);
        // finally check expiry works

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView expiredView = passwordResetController.submitPasswordReset(servletRequest, servletResponse, "encrypted string that's expired",
                oneTimeResetPasswordForm, bindingResult, redirectAttributes);
        assertEquals("redirect:/reset-password", expiredView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testResetPasswordDontFailIfAnyFieldIsEmtpy() {
        PasswordTypeAndConfirmForm form = new PasswordTypeAndConfirmForm();
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setPassword(new Text());
        form.setRetypedPassword(null);
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setPassword(null);
        form.setRetypedPassword(new Text());
        passwordResetController.resetPasswordConfirmValidate(form);
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
}
