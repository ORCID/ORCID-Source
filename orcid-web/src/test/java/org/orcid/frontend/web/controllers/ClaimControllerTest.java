package org.orcid.frontend.web.controllers;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ClaimControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";
    private static final String EMAIL = "test@orcid.org";
    private static final String ENCRYPTED_EMAIL = Base64.encodeBase64String("encrypted-test@orcid.org".getBytes());

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private RecordEmailSender recordEmailSender;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private RegistrationController registrationController;

    @Mock
    private EmailManager emailManager;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ClaimController claimController = new ClaimController() {
        @Override
        public String getMessage(String messageCode, Object... messageParams) {
            return messageCode;
        }

        @Override
        public boolean isEmailOkForCurrentUser(String decryptedEmail) {
            return EMAIL.equals(decryptedEmail);
        }

        @Override
        public String calculateRedirectUrl(String destination) {
            return "redirect-url" + destination;
        }

        @Override
        public String getBaseUri() {
            return "http://localhost";
        }

        @Override
        public void passwordValidate(Text passwordConfirm, Text password) {}

        @Override
        public void passwordConfirmValidate(Text passwordConfirm, Text password) {}

        @Override
        public void termsOfUserValidate(Checkbox termsOfUser) {}

        @Override
        public void activitiesVisibilityDefaultValidate(Visibility activitiesVisibilityDefault) {}
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void claimPasswordConfirmValidateTest() {
        Claim claim = new Claim();
        Claim result = claimController.claimPasswordConfirmValidate(claim);
        assertEquals(claim, result);
    }

    @Test
    public void claimPasswordValidateTest() {
        Claim claim = new Claim();
        Claim result = claimController.claimPasswordValidate(claim);
        assertEquals(claim, result);
    }

    @Test
    public void claimTermsOfUseValidateTest() {
        Claim claim = new Claim();
        Claim result = claimController.claimTermsOfUseValidate(claim);
        assertEquals(claim, result);
    }

    @Test
    public void verifyClaimJsonTest() throws UnsupportedEncodingException {
        Claim result = claimController.verifyClaimJson(request, ENCRYPTED_EMAIL, redirectAttributes);
        assertNotNull(result);
        assertTrue(result.getSendChangeNotifications().getValue());
    }

    @Test
    public void verifyClaimTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn(EMAIL);
        when(profileEntityManager.isProfileClaimedByEmail(EMAIL)).thenReturn(false);

        ModelAndView mav = claimController.verifyClaim(request, ENCRYPTED_EMAIL, redirectAttributes);
        assertEquals("claim", mav.getViewName());
        assertTrue((Boolean) mav.getModel().get("noIndex"));
    }

    @Test
    public void verifyClaim_wrongUserTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("wrong@orcid.org");

        ModelAndView mav = claimController.verifyClaim(request, ENCRYPTED_EMAIL, redirectAttributes);
        assertEquals("wrong_user", mav.getViewName());
    }

    @Test
    public void verifyClaim_alreadyClaimedTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn(EMAIL);
        when(profileEntityManager.isProfileClaimedByEmail(EMAIL)).thenReturn(true);

        ModelAndView mav = claimController.verifyClaim(request, ENCRYPTED_EMAIL, redirectAttributes);
        assertEquals("redirect:redirect-url/signin?alreadyClaimed", mav.getViewName());
    }

    @Test
    public void verifyClaim_decryptionFailureTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenThrow(new EncryptionOperationNotPossibleException());

        ModelAndView mav = claimController.verifyClaim(request, ENCRYPTED_EMAIL, redirectAttributes);
        assertEquals("redirect:redirect-url/signin?invalidClaimUrl", mav.getViewName());
    }

    @Test
    public void submitClaimJsonTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn(EMAIL);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(new ProfileEntity());
        when(profileEntityManager.claimProfileAndUpdatePreferences(eq(ORCID), eq(EMAIL), any(), any())).thenReturn(true);
        when(request.getLocale()).thenReturn(Locale.US);

        Claim claim = new Claim();
        claim.getPassword().setValue("password");
        Claim result = claimController.submitClaimJson(request, response, ENCRYPTED_EMAIL, claim);

        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        assertEquals("http://localhost/my-orcid?recordClaimed", result.getUrl());
        verify(profileEntityManager).claimProfileAndUpdatePreferences(eq(ORCID), eq(EMAIL), any(), eq(claim));
        verify(registrationController).logUserIn(request, response, ORCID, "password");
        verify(notificationManager).sendAmendEmail(eq(ORCID), eq(AmendedSection.UNKNOWN), isNull());
    }

    @Test
    public void submitClaimJson_wrongUserTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("wrong@orcid.org");

        Claim claim = new Claim();
        Claim result = claimController.submitClaimJson(request, response, ENCRYPTED_EMAIL, claim);

        assertEquals("http://localhost/claim/wrong_user", result.getUrl());
    }

    @Test(expected = OrcidBadRequestException.class)
    public void submitClaimJson_orcidNotFoundTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn(EMAIL);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(null);

        claimController.submitClaimJson(request, response, ENCRYPTED_EMAIL, new Claim());
    }

    @Test
    public void submitClaimJson_alreadyClaimedTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn(EMAIL);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        ProfileEntity profile = new ProfileEntity();
        profile.setClaimed(true);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        Claim claim = new Claim();
        Claim result = claimController.submitClaimJson(request, response, ENCRYPTED_EMAIL, claim);

        assertEquals("http://localhost/signin?alreadyClaimed", result.getUrl());
    }

    @Test(expected = IllegalStateException.class)
    public void submitClaimJson_claimFailureTest() throws UnsupportedEncodingException {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn(EMAIL);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(new ProfileEntity());
        when(profileEntityManager.claimProfileAndUpdatePreferences(eq(ORCID), eq(EMAIL), any(), any())).thenReturn(false);
        when(request.getLocale()).thenReturn(Locale.US);

        claimController.submitClaimJson(request, response, ENCRYPTED_EMAIL, new Claim());
    }

    @Test
    public void claimWrongUserTest() {
        ModelAndView mav = claimController.claimWrongUser(request);
        assertEquals("wrong_user", mav.getViewName());
    }

    @Test
    public void viewResendClaimEmailTest() {
        ModelAndView mav = claimController.viewResendClaimEmail(EMAIL);
        assertEquals("resend_claim", mav.getViewName());
    }

    @Test
    public void resendClaimEmailTest() {
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        ProfileEntity profile = new ProfileEntity(ORCID);
        profile.setClaimed(false);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        EmailRequest resendClaimRequest = new EmailRequest();
        resendClaimRequest.setEmail(EMAIL);

        EmailRequest result = claimController.resendClaimEmail(resendClaimRequest);

        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        assertEquals("resend_claim.successful_resend", result.getSuccessMessage());
        verify(recordEmailSender).sendClaimReminderEmail(eq(ORCID), anyInt(), eq(EMAIL));
    }

    @Test
    public void resendClaimEmail_byOrcidTest() {
        org.orcid.jaxb.model.v3.release.record.Email primaryEmail = new org.orcid.jaxb.model.v3.release.record.Email();
        primaryEmail.setEmail(EMAIL);
        when(emailManager.findPrimaryEmail(ORCID)).thenReturn(primaryEmail);
        ProfileEntity profile = new ProfileEntity(ORCID);
        profile.setClaimed(false);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        EmailRequest resendClaimRequest = new EmailRequest();
        resendClaimRequest.setEmail(ORCID);

        EmailRequest result = claimController.resendClaimEmail(resendClaimRequest);

        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        assertEquals("resend_claim.successful_resend", result.getSuccessMessage());
        verify(recordEmailSender).sendClaimReminderEmail(eq(ORCID), anyInt(), eq(EMAIL));
    }

    @Test
    public void resendClaimEmail_invalidEmailTest() {
        EmailRequest resendClaimRequest = new EmailRequest();
        resendClaimRequest.setEmail("invalid-email");

        EmailRequest result = claimController.resendClaimEmail(resendClaimRequest);

        assertFalse(result.getErrors().isEmpty());
        assertEquals("Email.resetPasswordForm.invalidEmail", result.getErrors().get(0));
    }

    @Test
    public void resendClaimEmail_alreadyClaimedTest() {
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        ProfileEntity profile = new ProfileEntity(ORCID);
        profile.setClaimed(true);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        EmailRequest resendClaimRequest = new EmailRequest();
        resendClaimRequest.setEmail(EMAIL);

        EmailRequest result = claimController.resendClaimEmail(resendClaimRequest);

        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().get(0).contains("orcid.frontend.security.already_claimed_with_link_1"));
    }

    @Test
    public void resendClaimEmail_notFoundTest() {
        when(emailManager.emailExists(EMAIL)).thenReturn(false);

        EmailRequest resendClaimRequest = new EmailRequest();
        resendClaimRequest.setEmail(EMAIL);

        EmailRequest result = claimController.resendClaimEmail(resendClaimRequest);

        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().get(0).contains("orcid.frontend.reset.password.email_not_found_1"));
    }
}
