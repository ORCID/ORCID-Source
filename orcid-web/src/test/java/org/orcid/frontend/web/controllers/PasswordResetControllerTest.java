package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.*;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.util.PasswordResetTokenEntry;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.ExpiringLinkService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.togglz.junit.TogglzRule;


/**
 * Every collaborator this controller reaches was already a mock; the Spring
 * context and the DBUnit load were dead weight, and nothing in the file read the
 * seeded rows. The two collaborators the context used to supply silently --
 * localeManager, behind BaseController.getMessage(), and orcidUrlManager, behind
 * calculateRedirectUrl -- are explicit mocks now, with orcidUrlManager answering
 * the same base URL the test properties carried.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PasswordResetControllerTest {

    private PasswordResetController passwordResetController;

    @Mock
    private RegistrationManager registrationManager;    
    
    @Mock
    private EmailManager emailManager;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;
    
    @Mock
    private EmailManagerReadOnly mockEmailManagerReadOnly;
    
    @Mock
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private RecordEmailSender mockRecordEmailSender;

    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Mock
    private BackupCodeManager backupCodeManager;
    
    @Mock
    private ExpiringLinkService expiringLinkService;

    @Mock
    private RedisClient redisClient;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void before() {
        passwordResetController = new PasswordResetController();

        ReflectionTestUtils.setField(passwordResetController, "registrationManager", registrationManager);
        ReflectionTestUtils.setField(passwordResetController, "encryptionManager", encryptionManager);
        ReflectionTestUtils.setField(passwordResetController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(passwordResetController, "recordEmailSender", mockRecordEmailSender);
        ReflectionTestUtils.setField(passwordResetController, "twoFactorAuthenticationManager", twoFactorAuthenticationManager);
        ReflectionTestUtils.setField(passwordResetController, "backupCodeManager", backupCodeManager);
        ReflectionTestUtils.setField(passwordResetController, "expiringLinkService", expiringLinkService);
        ReflectionTestUtils.setField(passwordResetController, "redisClient", redisClient);

        // emailManager, localeManager and orcidUrlManager are declared only on
        // BaseController.
        ReflectionTestUtils.setField(passwordResetController, BaseController.class, "emailManager", emailManager, EmailManager.class);
        ReflectionTestUtils.setField(passwordResetController, BaseController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(passwordResetController, BaseController.class, "orcidUrlManager", orcidUrlManager, OrcidUrlManager.class);

        // PasswordResetController re-declares profileEntityManager (line 84) and
        // emailManagerReadOnly (line 93) over BaseController's copies. Spring's
        // @Resource fills both; anything that fills only the most derived one
        // leaves the inherited helpers pointing at null.
        ReflectionTestUtils.setField(passwordResetController, PasswordResetController.class, "profileEntityManager", profileEntityManager,
                ProfileEntityManager.class);
        ReflectionTestUtils.setField(passwordResetController, BaseController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(passwordResetController, PasswordResetController.class, "emailManagerReadOnly", mockEmailManagerReadOnly,
                EmailManagerReadOnly.class);
        ReflectionTestUtils.setField(passwordResetController, BaseController.class, "emailManagerReadOnly", mockEmailManagerReadOnly,
                EmailManagerReadOnly.class);

        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        // The value the test properties carried, which the redirect assertions
        // below are written against.
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://testserver.orcid.org");

        when(expiringLinkService.verifyToken(any())).thenReturn(ExpiringLinkService.VerificationResult.invalid());
    }
    
    @Test
    public void testPasswordResetActiveClaimedSendsResetEmail() throws DatatypeConfigurationException {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true);
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(true);
        ProfileEntity record = new ProfileEntity();
        when(profileEntityCacheManager.retrieve(orcid)).thenReturn(record);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail(email);

        ResponseEntity<EmailRequest> response = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors().isEmpty());
        // The controller hands the submitted address to the sender, which decides the full
        // recipient set. Nothing about the fan out belongs here.
        verify(mockRecordEmailSender, times(1)).sendPasswordResetEmail(eq(email), eq(orcid));
        verify(mockRecordEmailSender, never()).sendReactivationEmail(anyString(), anyString());
        verify(mockRecordEmailSender, never()).sendPasswordResetNotFoundEmail(anyString(), any());
    }

    @Test
    public void testPasswordResetUnclaimedSendEmail() throws DatatypeConfigurationException {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(email)).thenReturn(false);
        ProfileEntity record= new ProfileEntity();
        when(profileEntityCacheManager.retrieve(orcid)).thenReturn(record);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("email1@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());
        verify(mockRecordEmailSender, times(1)).sendClaimReminderEmail(eq(orcid), eq(0), eq(email));
        // An unclaimed record gets a claim reminder, never a fanned out reset link.
        verify(mockRecordEmailSender, never()).sendPasswordResetEmail(anyString(), anyString());
    }    
    
    @Test
    public void testPasswordResetUserNotFoundSendEmail() {
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("not_in_orcid@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());
        verify(mockRecordEmailSender, times(1)).sendPasswordResetNotFoundEmail(eq("not_in_orcid@test.orcid.org"), any());
        // There is no account here, so there is no verified email set to fan out to.
        verify(mockRecordEmailSender, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    public void testPasswordResetUserDeactivatedSendEmail() throws DatatypeConfigurationException {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(true);
        ProfileEntity record = new ProfileEntity();
        when(profileEntityCacheManager.retrieve(orcid)).thenReturn(record);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("email1@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());
        verify(mockRecordEmailSender, times(1)).sendReactivationEmail(eq(email), eq(orcid));
        // A deactivated record gets a reactivation link, which is a different decision from
        // fanning a password reset link out across the record's addresses.
        verify(mockRecordEmailSender, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    public void testPasswordResetLinkExpired() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString");

        assertEquals("redirect:https://testserver.orcid.org/reset-password?expired=true", modelAndView.getViewName());
    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToConsolidatedScreenDirectly() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString");

        assertEquals("password_one_time_reset", modelAndView.getViewName());
    }

    @Test
    public void testResetPasswordDontFailIfAnyFieldIsEmtpy() {
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setNewPassword(Text.valueOf(""));
        form.setRetypedPassword(null);
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setPassword(null);
        form.setRetypedPassword(Text.valueOf(""));
        passwordResetController.resetPasswordConfirmValidate(form);
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_ValidToken() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("encrypted_string");

        form = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_ExpiredToken() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("encrypted_string");

        form = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);

        assertFalse(form.getErrors().isEmpty());
        assertTrue(form.getErrors().contains("expiredPasswordResetToken"));
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_InvalidToken() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenThrow(new EncryptionOperationNotPossibleException());

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("bad_string");

        form = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);

        assertFalse(form.getErrors().isEmpty());
        assertTrue(form.getErrors().contains("invalidPasswordResetToken"));
    }

    @Test
    public void testSubmitPasswordResetV2_SuccessNo2FA() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(false);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        assertFalse(form.isTwoFactorEnabled());
        verify(profileEntityManager).updatePassword("0000-0000-0000-0000", "Password#123");
        verify(profileEntityManager).resetSigninLock("0000-0000-0000-0000");
    }

    @Test
    public void testSubmitPasswordResetV2_PromptsFor2FA() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        assertTrue(form.isTwoFactorEnabled());
    }

    @Test
    public void testSubmitPasswordResetV2_SuccessWith2FACode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", "0000-0000-0000-0000")).thenReturn(true);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorCode("123456");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        verify(profileEntityManager).updatePassword("0000-0000-0000-0000", "Password#123");
    }

    @Test
    public void testSubmitPasswordResetV2_FailsWithInvalid2FACode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(twoFactorAuthenticationManager.verificationCodeIsValid("999999", "0000-0000-0000-0000")).thenReturn(false);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorCode("999999");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.isInvalidTwoFactorCode());
    }

    @Test
    public void testSubmitPasswordResetV2_SuccessWithBackupCode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(backupCodeManager.verify("0000-0000-0000-0000", "ABCDEF1234")).thenReturn(true);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorRecoveryCode("ABCDEF1234");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        verify(profileEntityManager).updatePassword("0000-0000-0000-0000", "Password#123");
    }

    @Test
    public void testSubmitPasswordResetV2_FailsWithInvalidBackupCode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(backupCodeManager.verify("0000-0000-0000-0000", "BADCODE123")).thenReturn(false);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorRecoveryCode("BADCODE123");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.isInvalidTwoFactorRecoveryCode());
    }

    private void mockValidJWTToken(String token, String orcid) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(orcid).expirationTime(new Date(System.currentTimeMillis() + 3600000)).build();
        when(expiringLinkService.verifyToken(token)).thenReturn(ExpiringLinkService.VerificationResult.valid(claims));
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_ValidJWTToken() throws Exception {
        String token = "valid.jwt.token";
        String orcid = "0000-0000-0000-0000";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        mockValidJWTToken(token, orcid);
        when(redisClient.get("password-reset-token-" + orcid)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);
        assertTrue(returnedForm.getErrors().isEmpty());
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_SupersededJWTToken() throws Exception {
        String token = "superseded.jwt.token";
        String orcid = "0000-0000-0000-0000";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        mockValidJWTToken(token, orcid);
        when(redisClient.get("password-reset-token-" + orcid)).thenReturn(new PasswordResetTokenEntry("a.different.token", false).serialize());

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);
        assertFalse(returnedForm.getErrors().isEmpty());
        assertEquals("expiredPasswordResetToken", returnedForm.getErrors().get(0));
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_MissingRedisEntry() throws Exception {
        String token = "valid.jwt.token";
        String orcid = "0000-0000-0000-0000";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        mockValidJWTToken(token, orcid);
        when(redisClient.get("password-reset-token-" + orcid)).thenReturn(null);

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);
        assertFalse(returnedForm.getErrors().isEmpty());
        assertEquals("expiredPasswordResetToken", returnedForm.getErrors().get(0));
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_UsedJWTToken() throws Exception {
        String token = "used.jwt.token";
        String orcid = "0000-0000-0000-0000";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        mockValidJWTToken(token, orcid);
        when(redisClient.get("password-reset-token-" + orcid)).thenReturn(new PasswordResetTokenEntry(token, true).serialize());

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);
        assertFalse(returnedForm.getErrors().isEmpty());
        assertEquals("alreadyUsedPasswordResetToken", returnedForm.getErrors().get(0));
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_ExpiredJWTTokenDoesNotHitRedis() throws Exception {
        String token = "expired.jwt.token";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);

        when(expiringLinkService.verifyToken(token)).thenReturn(ExpiringLinkService.VerificationResult.expired());

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);
        assertFalse(returnedForm.getErrors().isEmpty());
        assertEquals("expiredPasswordResetToken", returnedForm.getErrors().get(0));
        verifyNoInteractions(redisClient);
    }

    @Test
    public void testSubmitPasswordResetV2_ValidJWTToken() throws Exception {
        String token = "valid.jwt.token";
        String orcid = "0000-0000-0000-0000";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));

        mockValidJWTToken(token, orcid);
        when(redisClient.get("password-reset-token-" + orcid)).thenReturn(new PasswordResetTokenEntry(token, false).serialize());
        when(profileEntityManager.orcidExists(orcid)).thenReturn(true);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);
        assertTrue(returnedForm.getErrors().isEmpty());
        // The entry is kept, flagged as used, so a second click can be told apart from an expired link
        verify(redisClient).set(eq("password-reset-token-" + orcid), eq(new PasswordResetTokenEntry(token, true).serialize()), anyInt());
        verify(redisClient, never()).remove(any(String.class));
    }

    @Test
    public void testSubmitPasswordResetV2_UsedJWTToken() throws Exception {
        String token = "used.jwt.token";
        String orcid = "0000-0000-0000-0000";
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setToken(token);
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));

        mockValidJWTToken(token, orcid);
        when(redisClient.get("password-reset-token-" + orcid)).thenReturn(new PasswordResetTokenEntry(token, true).serialize());

        OneTimeResetPasswordForm returnedForm = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);
        assertFalse(returnedForm.getErrors().isEmpty());
        assertEquals("alreadyUsedPasswordResetToken", returnedForm.getErrors().get(0));
        verify(profileEntityManager, never()).updatePassword(any(String.class), any(String.class));
    }

    @Test
    public void testPasswordResetEmail_ValidToken() throws Exception {
        String encryptedEmail = "encrypted string not expired";
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        
        ModelAndView mav = passwordResetController.resetPasswordEmail(servletRequest, encryptedEmail);
        assertEquals("password_one_time_reset", mav.getViewName());
    }

    @Test
    public void testPasswordResetEmail_ExpiredToken() throws Exception {
        String encryptedEmail = "encrypted string expired";
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");
        
        ModelAndView mav = passwordResetController.resetPasswordEmail(servletRequest, encryptedEmail);
        assertEquals("redirect:https://testserver.orcid.org/reset-password?expired=true", mav.getViewName());
    }
}
