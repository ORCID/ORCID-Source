package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.util.PasswordResetTokenEntry;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ReactivationData;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Reactivation;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.ExpiringLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nimbusds.jwt.JWTClaimsSet;

@RunWith(MockitoJUnitRunner.class)
public class PasswordResetControllerTest {

    private static final String ORCID = "0000-0000-0000-0000";
    private static final String EMAIL = "email1@test.orcid.org";
    private static final String BASE_URL = "https://testserver.orcid.org";

    private PasswordResetController controller;

    @Mock
    private RegistrationManager registrationManager;
    @Mock
    private EncryptionManager encryptionManager;
    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;
    @Mock
    private BackupCodeManager backupCodeManager;
    @Mock
    private ProfileEntityManager profileEntityManager;
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;
    @Mock
    private RecordEmailSender recordEmailSender;
    @Mock
    private ExpiringLinkService expiringLinkService;
    @Mock
    private RedisClient redisClient;
    @Mock
    private ProfileHistoryEventManager profileHistoryEventManager;
    @Mock
    private EmailManager emailManager;
    @Mock
    private OrcidUrlManager orcidUrlManager;
    private RegistrationController registrationController;

    @Before
    public void setUp() {
        controller = new PasswordResetController();
        registrationController = new NoOpRegistrationController();

        inject(PasswordResetController.class, "registrationManager", registrationManager);
        inject(PasswordResetController.class, "encryptionManager", encryptionManager);
        inject(PasswordResetController.class, "twoFactorAuthenticationManager", twoFactorAuthenticationManager);
        inject(PasswordResetController.class, "backupCodeManager", backupCodeManager);
        inject(PasswordResetController.class, "profileEntityManager", profileEntityManager);
        inject(PasswordResetController.class, "registrationController", registrationController);
        inject(PasswordResetController.class, "profileEntityCacheManager", profileEntityCacheManager);
        inject(PasswordResetController.class, "emailManagerReadOnly", emailManagerReadOnly);
        inject(PasswordResetController.class, "recordEmailSender", recordEmailSender);
        inject(PasswordResetController.class, "expiringLinkService", expiringLinkService);
        inject(PasswordResetController.class, "redisClient", redisClient);
        inject(PasswordResetController.class, "profileHistoryEventManager", profileHistoryEventManager);

        inject(BaseController.class, "emailManager", emailManager);
        inject(BaseController.class, "emailManagerReadOnly", emailManagerReadOnly);
        inject(BaseController.class, "profileEntityManager", profileEntityManager);
        inject(BaseController.class, "orcidUrlManager", orcidUrlManager);
        inject(BaseController.class, "localeManager", new PassThroughLocaleManager());

        when(orcidUrlManager.getBaseUrl()).thenReturn(BASE_URL);
        when(orcidUrlManager.determineFullTargetUrlFromSavedRequest(any(), any())).thenReturn(null);
        when(expiringLinkService.verifyToken(anyString())).thenReturn(ExpiringLinkService.VerificationResult.invalid());
    }

    @Test
    public void getPasswordResetRequestReturnsEmptyRequest() {
        assertNotNull(controller.getPasswordResetRequest());
    }

    @Test
    public void validateResetPasswordRequestInvalidEmailAddsError() {
        EmailRequest request = new EmailRequest();
        request.setEmail("bad-email");

        EmailRequest result = controller.validateResetPasswordRequest(request);

        assertEquals(1, result.getErrors().size());
        assertEquals("Email.resetPasswordForm.invalidEmail", result.getErrors().get(0));
    }

    @Test
    public void validateResetPasswordRequestValidEmailTrimsInput() {
        EmailRequest request = new EmailRequest();
        request.setEmail("  test@orcid.org  ");

        EmailRequest result = controller.validateResetPasswordRequest(request);

        assertTrue(result.getErrors().isEmpty());
        assertEquals("test@orcid.org", result.getEmail());
    }

    @Test
    public void issueForgottenIdRequestInvalidEmailReturnsError() {
        EmailRequest request = new EmailRequest();
        request.setEmail("not-an-email");

        EmailRequest result = controller.issueForgottenIdRequest(request);

        assertEquals(1, result.getErrors().size());
        verifyNoInteractions(recordEmailSender);
    }

    @Test
    public void issueForgottenIdRequestDeactivatedSendsReactivation() {
        EmailRequest request = new EmailRequest();
        request.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManager.isDeactivated(ORCID)).thenReturn(true);

        EmailRequest result = controller.issueForgottenIdRequest(request);

        assertTrue(result.getErrors().isEmpty());
        verify(recordEmailSender).sendReactivationEmail(EMAIL, ORCID);
    }

    @Test
    public void issueForgottenIdRequestUnclaimedSendsClaimReminder() {
        EmailRequest request = new EmailRequest();
        request.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManager.isDeactivated(ORCID)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(EMAIL)).thenReturn(false);

        EmailRequest result = controller.issueForgottenIdRequest(request);

        assertTrue(result.getErrors().isEmpty());
        verify(recordEmailSender).sendClaimReminderEmail(ORCID, 0, EMAIL);
    }

    @Test
    public void issueForgottenIdRequestClaimedSendsForgottenId() {
        EmailRequest request = new EmailRequest();
        request.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManager.isDeactivated(ORCID)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(EMAIL)).thenReturn(true);

        EmailRequest result = controller.issueForgottenIdRequest(request);

        assertTrue(result.getErrors().isEmpty());
        verify(recordEmailSender).sendForgottenIdEmail(EMAIL, ORCID);
    }

    @Test
    public void issueForgottenIdRequestNotFoundSendsNotFoundEmail() {
        EmailRequest request = new EmailRequest();
        request.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(false);

        EmailRequest result = controller.issueForgottenIdRequest(request);

        assertTrue(result.getErrors().isEmpty());
        verify(recordEmailSender).sendForgottenIdEmailNotFoundEmail(eq(EMAIL), eq(Locale.ENGLISH));
    }

    @Test
    public void issuePasswordResetRequestRejectsNonWhitelistedParameters() {
        MockHttpServletRequest request = newRequest();
        request.addParameter("unexpected", "x");
        EmailRequest body = new EmailRequest();
        body.setEmail(EMAIL);

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    public void issuePasswordResetRequestInvalidEmailReturnsOkWithError() {
        MockHttpServletRequest request = newRequest();
        EmailRequest body = new EmailRequest();
        body.setEmail("bad");

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email.resetPasswordForm.invalidEmail", response.getBody().getErrors().get(0));
    }

    @Test
    public void issuePasswordResetRequestClaimedSendsResetLink() {
        MockHttpServletRequest request = newRequest();
        EmailRequest body = new EmailRequest();
        body.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManager.isDeactivated(ORCID)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(EMAIL)).thenReturn(true);

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recordEmailSender).sendPasswordResetEmail(EMAIL, ORCID);
    }

    @Test
    public void issuePasswordResetRequestUnclaimedSendsClaimReminder() {
        MockHttpServletRequest request = newRequest();
        EmailRequest body = new EmailRequest();
        body.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManager.isDeactivated(ORCID)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(EMAIL)).thenReturn(false);

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recordEmailSender).sendClaimReminderEmail(ORCID, 0, EMAIL);
    }

    @Test
    public void issuePasswordResetRequestDeactivatedSendsReactivation() {
        MockHttpServletRequest request = newRequest();
        EmailRequest body = new EmailRequest();
        body.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManager.isDeactivated(ORCID)).thenReturn(true);

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recordEmailSender).sendReactivationEmail(EMAIL, ORCID);
    }

    @Test
    public void issuePasswordResetRequestUnknownEmailSendsNotFound() {
        MockHttpServletRequest request = newRequest();
        EmailRequest body = new EmailRequest();
        body.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenReturn(false);

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recordEmailSender).sendPasswordResetNotFoundEmail(eq(EMAIL), eq(Locale.ENGLISH));
    }

    @Test
    public void issuePasswordResetRequestExceptionReturnsBadRequest() {
        MockHttpServletRequest request = newRequest();
        EmailRequest body = new EmailRequest();
        body.setEmail(EMAIL);
        when(emailManager.emailExists(EMAIL)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<EmailRequest> response = controller.issuePasswordResetRequest(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email.resetPasswordForm.error", response.getBody().getErrors().get(0));
    }

    @Test
    public void resetPasswordEmailValidTokenReturnsResetView() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");

        ModelAndView mav = controller.resetPasswordEmail(newRequest(), "token");

        assertEquals("password_one_time_reset", mav.getViewName());
        assertEquals(Boolean.TRUE, mav.getModel().get("noIndex"));
    }

    @Test
    public void resetPasswordEmailExpiredTokenRedirectsToExpiredUrl() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView mav = controller.resetPasswordEmail(newRequest(), "token");

        assertEquals("redirect:https://testserver.orcid.org/reset-password?expired=true", mav.getViewName());
    }

    @Test
    public void resetPasswordConfirmValidateHandlesEmptyFields() {
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setNewPassword(Text.valueOf(""));
        form.setRetypedPassword(Text.valueOf(""));

        OneTimeResetPasswordForm result = controller.resetPasswordConfirmValidate(form);

        assertNotNull(result.getErrors());
        assertNotNull(result.getNewPassword().getErrors());
    }

    @Test
    public void getResetPasswordReturnsForm() {
        OneTimeResetPasswordForm form = controller.getResetPassword();
        assertNotNull(form);
        assertNotNull(form.getNewPassword());
        assertNotNull(form.getRetypedPassword());
    }

    @Test
    public void submitPasswordEmailValidatePasswordValidJwtAndCurrentToken() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void submitPasswordEmailValidatePasswordUsedJwtTokenReturnsUsedError() {
        String token = "used.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, true).serialize());
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("alreadyUsedPasswordResetToken", result.getErrors().get(0));
    }

    @Test
    public void submitPasswordEmailValidatePasswordSupersededTokenReturnsExpiredError() {
        String token = "superseded.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry("another.jwt", false).serialize());
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("expiredPasswordResetToken", result.getErrors().get(0));
    }

    @Test
    public void submitPasswordEmailValidatePasswordExpiredJwtDoesNotQueryRedis() {
        String token = "expired.jwt";
        when(expiringLinkService.verifyToken(token)).thenReturn(ExpiringLinkService.VerificationResult.expired());
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("expiredPasswordResetToken", result.getErrors().get(0));
        verifyNoInteractions(redisClient);
    }

    @Test
    public void submitPasswordEmailValidatePasswordLegacyValidTokenPasses() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("legacy");

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void submitPasswordEmailValidatePasswordLegacyExpiredTokenReturnsExpiredError() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("legacy");

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("expiredPasswordResetToken", result.getErrors().get(0));
    }

    @Test
    public void submitPasswordEmailValidatePasswordLegacyInvalidTokenReturnsInvalidError() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenThrow(new EncryptionOperationNotPossibleException());
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("legacy");

        OneTimeResetPasswordForm result = controller.submitPasswordEmailValidatePassword(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("invalidPasswordResetToken", result.getErrors().get(0));
    }

    @Test
    public void submitPasswordResetV2ValidJwtSuccessRecordsHistoryEvent() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(false);

        OneTimeResetPasswordForm form = strongForm(token);
        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.getErrors().isEmpty());
        assertEquals(BASE_URL + "/my-orcid", result.getSuccessRedirectLocation());
        verify(profileEntityManager).updatePassword(ORCID, "Password#123");
        verify(profileHistoryEventManager).recordResetPasswordEvent(ORCID, "127.0.0.1");
        verify(redisClient).set(eq("password-reset-token-" + ORCID), eq(new PasswordResetTokenEntry(token, true).serialize()), anyInt());
    }

    @Test
    public void submitPasswordResetV2ValidJwtUsedTokenReturnsError() {
        String token = "used.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, true).serialize());
        OneTimeResetPasswordForm form = strongForm(token);

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("alreadyUsedPasswordResetToken", result.getErrors().get(0));
        verify(profileEntityManager, never()).updatePassword(anyString(), anyString());
        verify(profileHistoryEventManager, never()).recordResetPasswordEvent(anyString(), anyString());
    }

    @Test
    public void submitPasswordResetV2ExpiredJwtReturnsError() {
        String token = "expired.jwt";
        when(expiringLinkService.verifyToken(token)).thenReturn(ExpiringLinkService.VerificationResult.expired());
        OneTimeResetPasswordForm form = strongForm(token);

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("expiredPasswordResetToken", result.getErrors().get(0));
    }

    @Test
    public void submitPasswordResetV2TwoFactorPromptWhenNoCodesProvided() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        OneTimeResetPasswordForm form = strongForm(token);

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.isTwoFactorEnabled());
        verify(profileEntityManager, never()).updatePassword(anyString(), anyString());
    }

    @Test
    public void submitPasswordResetV2TwoFactorBlankCodeAndBlankRecoveryReturnsInvalidCode() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);

        OneTimeResetPasswordForm form = strongForm(token);
        form.setTwoFactorCode("");
        form.setTwoFactorRecoveryCode("");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.isInvalidTwoFactorCode());
    }

    @Test
    public void submitPasswordResetV2TwoFactorInvalidCodeReturnsInvalidCode() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        when(twoFactorAuthenticationManager.verificationCodeIsValid("999999", ORCID)).thenReturn(false);

        OneTimeResetPasswordForm form = strongForm(token);
        form.setTwoFactorCode("999999");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.isInvalidTwoFactorCode());
    }

    @Test
    public void submitPasswordResetV2TwoFactorValidCodeAllowsSuccess() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", ORCID)).thenReturn(true);

        OneTimeResetPasswordForm form = strongForm(token);
        form.setTwoFactorCode("123456");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.getErrors().isEmpty());
        verify(profileHistoryEventManager).recordResetPasswordEvent(ORCID, "127.0.0.1");
    }

    @Test
    public void submitPasswordResetV2TwoFactorInvalidRecoveryCodeReturnsInvalidRecoveryCode() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        when(backupCodeManager.verify(ORCID, "BAD")).thenReturn(false);

        OneTimeResetPasswordForm form = strongForm(token);
        form.setTwoFactorRecoveryCode("BAD");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.isInvalidTwoFactorRecoveryCode());
    }

    @Test
    public void submitPasswordResetV2TwoFactorValidRecoveryCodeAllowsSuccess() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        when(backupCodeManager.verify(ORCID, "REC")).thenReturn(true);

        OneTimeResetPasswordForm form = strongForm(token);
        form.setTwoFactorRecoveryCode("REC");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.getErrors().isEmpty());
        verify(profileHistoryEventManager).recordResetPasswordEvent(ORCID, "127.0.0.1");
    }

    @Test
    public void submitPasswordResetV2WeakPasswordStopsBeforeUpdate() {
        String token = "valid.jwt";
        mockValidJwt(token, ORCID);
        when(redisClient.get("password-reset-token-" + ORCID)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(emailManager.getEmails(ORCID)).thenReturn(new Emails());

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);
        form.setNewPassword(Text.valueOf("short"));
        form.setRetypedPassword(Text.valueOf("different"));

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertFalse(result.getNewPassword().getErrors().isEmpty());
        verify(profileEntityManager, never()).updatePassword(anyString(), anyString());
    }

    @Test
    public void submitPasswordResetV2LegacyTokenWithInvalidOrcidFails() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=0000-0000-0000-9999&issueDate=2070-05-29T17:04:27");
        when(profileEntityManager.orcidExists("0000-0000-0000-9999")).thenReturn(false);
        OneTimeResetPasswordForm form = strongForm("legacy");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertEquals("invalidPasswordResetToken", result.getErrors().get(0));
    }

    @Test
    public void submitPasswordResetV2LegacyTokenWithValidOrcidSucceeds() {
        String legacyOrcid = "0000-0000-0000-1234";
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=" + legacyOrcid + "&issueDate=2070-05-29T17:04:27");
        when(profileEntityManager.orcidExists(legacyOrcid)).thenReturn(true);
        when(emailManager.getEmails(legacyOrcid)).thenReturn(new Emails());
        when(twoFactorAuthenticationManager.userUsing2FA(legacyOrcid)).thenReturn(false);
        OneTimeResetPasswordForm form = strongForm("legacy");

        OneTimeResetPasswordForm result = controller.submitPasswordResetV2(newRequest(), new MockHttpServletResponse(), form);

        assertTrue(result.getErrors().isEmpty());
        verify(profileHistoryEventManager).recordResetPasswordEvent(legacyOrcid, "127.0.0.1");
        verify(redisClient, never()).set(anyString(), anyString(), anyInt());
    }

    @Test
    public void sendReactivationInvalidEmailReturnsValidationError() throws Exception {
        ResponseEntity<?> response = controller.sendReactivation("bad-email", null);
        assertTrue(response.getBody().toString().contains("Email.personalInfoForm.email"));
    }

    @Test
    public void sendReactivationUnknownEmailReturnsInvalidEmailError() throws Exception {
        when(emailManager.findOrcidIdByEmail("nobody@orcid.org")).thenThrow(new NoResultException());

        ResponseEntity<?> response = controller.sendReactivation("nobody@orcid.org", null);

        assertTrue(response.getBody().toString().contains("Email.resendClaim.invalidEmail"));
    }

    @Test
    public void sendReactivationInvalidOrcidReturnsError() throws Exception {
        ResponseEntity<?> response = controller.sendReactivation(null, "bad");
        assertTrue(response.getBody().toString().contains("Email.resetPasswordForm.error"));
    }

    @Test
    public void sendReactivationOrcidWithoutPrimaryEmailReturnsError() throws Exception {
        when(emailManager.findPrimaryEmail(ORCID)).thenThrow(new NoResultException());

        ResponseEntity<?> response = controller.sendReactivation(null, ORCID);

        assertTrue(response.getBody().toString().contains("Email.resetPasswordForm.error"));
    }

    @Test
    public void sendReactivationAlreadyActiveReturnsAlreadyActiveError() throws Exception {
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        ProfileEntity profile = new ProfileEntity();
        profile.setDeactivationDate(null);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        ResponseEntity<?> response = controller.sendReactivation(EMAIL, null);

        assertTrue(response.getBody().toString().contains("orcid.frontend.reactivate.error.already_active"));
    }

    @Test
    public void sendReactivationDeactivatedByEmailSendsEmail() throws Exception {
        when(emailManager.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        ProfileEntity profile = new ProfileEntity();
        profile.setDeactivationDate(new Date());
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        ResponseEntity<?> response = controller.sendReactivation(EMAIL, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"sent\":true}", response.getBody());
        verify(recordEmailSender).sendReactivationEmail(EMAIL, ORCID);
    }

    @Test
    public void sendReactivationDeactivatedByOrcidSendsEmail() throws Exception {
        Email primary = new Email();
        primary.setEmail(EMAIL);
        when(emailManager.findPrimaryEmail(ORCID)).thenReturn(primary);
        ProfileEntity profile = new ProfileEntity();
        profile.setDeactivationDate(new Date());
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        ResponseEntity<?> response = controller.sendReactivation(null, ORCID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"sent\":true}", response.getBody());
        verify(recordEmailSender).sendReactivationEmail(EMAIL, ORCID);
    }

    @Test
    public void reactivationReturnsViewWithNoIndex() {
        ModelAndView mav = controller.reactivation(newRequest(), "params", mock(RedirectAttributes.class));
        assertEquals("reactivation", mav.getViewName());
        assertEquals(Boolean.TRUE, mav.getModel().get("noIndex"));
    }

    @Test
    public void getReactivationDataValidAndNotExpired() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");

        ReactivationData data = controller.getResetPassword("params");

        assertTrue(data.isTokenValid());
        assertFalse(data.isReactivationLinkExpired());
        assertEquals("any@orcid.org", data.getEmail());
        assertEquals("params", data.getResetParams());
    }

    @Test
    public void getReactivationDataValidButExpired() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ReactivationData data = controller.getResetPassword("params");

        assertTrue(data.isTokenValid());
        assertTrue(data.isReactivationLinkExpired());
    }

    @Test
    public void getReactivationDataInvalidToken() {
        when(encryptionManager.decryptForExternalUse(anyString())).thenThrow(new EncryptionOperationNotPossibleException());

        ReactivationData data = controller.getResetPassword("params");

        assertFalse(data.isTokenValid());
    }

    @Test
    public void validateReactivationFieldsAddsErrorsForMissingRequiredData() {
        Registration reg = new Registration();
        reg.getEmail().setValue("any@orcid.org");

        controller.validateReactivationFields(newRequest(), reg);

        assertFalse(reg.getErrors().isEmpty());
    }

    @Test
    public void regEmailAdditionalValidateRemovesBlankEntries() {
        Registration reg = new Registration();
        reg.getEmail().setValue("any@orcid.org");
        reg.setEmailsAdditional(new ArrayList<>(Arrays.asList(new Text(), Text.valueOf("bad-email"))));
        when(emailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn(ORCID);

        Registration result = controller.regEmailAdditionalValidate(newRequest(), reg);

        assertEquals(1, result.getEmailsAdditional().size());
        assertFalse(result.getEmailsAdditional().get(0).getErrors().isEmpty());
    }

    @Test
    public void setReactivationConfirmWithValidationErrorsReturnsErrorRedirect() throws Exception {
        Reactivation reg = new Reactivation();
        Redirect redirect = controller.setReactivationConfirm(newRequest(), new MockHttpServletResponse(), reg);
        assertFalse(redirect.getErrors().isEmpty());
    }

    @Test
    public void setReactivationConfirmWithInvalidValidationNumbersRedirectsToRegister() throws Exception {
        Reactivation reg = validReactivation();
        reg.setValNumServer(10);
        reg.setValNumClient(9);

        Redirect redirect = controller.setReactivationConfirm(newRequest(), new MockHttpServletResponse(), reg);

        assertEquals(BASE_URL + "/register", redirect.getUrl());
    }

    @Test
    public void setReactivationConfirmWithAffiliationCreatesAffiliation() throws Exception {
        Reactivation reg = validReactivation();
        reg.setAffiliationForm(new AffiliationForm());

        Redirect redirect = controller.setReactivationConfirm(newRequest(), new MockHttpServletResponse(), reg);

        assertEquals(BASE_URL + "/my-orcid", redirect.getUrl());
        verify(registrationManager).createAffiliation(reg, ORCID);
    }

    private void mockValidJwt(String token, String orcid) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(orcid).expirationTime(new Date(System.currentTimeMillis() + 3600000)).build();
        when(expiringLinkService.verifyToken(token)).thenReturn(ExpiringLinkService.VerificationResult.valid(claims));
    }

    private OneTimeResetPasswordForm strongForm(String token) {
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        return form;
    }

    private Reactivation validReactivation() {
        Reactivation reg = new Reactivation();
        reg.getEmail().setValue("any@orcid.org");
        reg.getGivenNames().setValue("Given");
        reg.getFamilyNames().setValue("Family");
        reg.getPassword().setValue("Password#123");
        reg.getPasswordConfirm().setValue("Password#123");
        reg.getTermsOfUse().setValue(true);
        reg.getActivitiesVisibilityDefault().setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
        reg.setEmailsAdditional(new ArrayList<Text>());
        reg.setValNumServer(10);
        reg.setValNumClient(5);
        setField(reg, Reactivation.class, "resetParams", "legacy-token");

        when(encryptionManager.decryptForExternalUse(anyString())).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(emailManager.findOrcidIdByEmail("any@orcid.org")).thenReturn(ORCID);
        when(profileEntityManager.reactivate(eq(ORCID), eq("any@orcid.org"), eq(reg))).thenReturn(new ArrayList<String>());
        return reg;
    }

    private MockHttpServletRequest newRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.setSession(new MockHttpSession());
        return request;
    }

    private void inject(Class<?> declaringClass, String fieldName, Object value) {
        setField(controller, declaringClass, fieldName, value);
    }

    private static void setField(Object target, Class<?> declaringClass, String fieldName, Object value) {
        try {
            Field field = declaringClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Unable to set field " + fieldName + " on " + declaringClass.getName(), e);
        }
    }

    private static class PassThroughLocaleManager implements LocaleManager {
        @Override
        public Locale getLocale() {
            return Locale.ENGLISH;
        }

        @Override
        public Locale getLocaleFromOrcidProfile(org.orcid.jaxb.model.message.OrcidProfile orcidProfile) {
            return Locale.ENGLISH;
        }

        @Override
        public String resolveMessage(String messageCode, Object... messageParams) {
            return messageCode;
        }

        @Override
        public String resolveMessage(String messageCode, Locale locale, Object... messageParams) {
            return messageCode;
        }

        @Override
        public org.orcid.pojo.Local getJavascriptMessages(Locale locale) {
            return null;
        }

        @Override
        public Map<String, String> getCountries(Locale locale) {
            return new HashMap<>();
        }
    }

    private static class NoOpRegistrationController extends RegistrationController {
        @Override
        public void logUserIn(HttpServletRequest request, HttpServletResponse response, String orcidId, String password) {
            // no-op for unit tests
        }
    }
}
