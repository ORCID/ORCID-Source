package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RecoveryPhone;
import org.orcid.core.manager.RecoveryPhoneManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSaveRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSaveResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSendCodeRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSendCodeResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneVerificationService;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.togglz.junit.TogglzRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TwoFactorAuthenticationControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private BackupCodeManager backupCodeManager;

    @Mock
    private RecoveryPhoneManager recoveryPhoneManager;

    @Mock
    private RecoveryPhoneVerificationService recoveryPhoneVerificationService;

    @Mock
    private RecordEmailSender recordEmailSender;

    @Mock
    private EncryptionManager encryptionManager;

    @Spy
    @InjectMocks
    private TwoFactorAuthenticationController controller;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final MockHttpSession session = new MockHttpSession();

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(request.getSession()).thenReturn(session);
        doReturn(ORCID).when(controller).getCurrentUserOrcid();
        doReturn("redirectUrl").when(controller).calculateRedirectUrl(anyString());
        doReturn("redirectUrl").when(controller).calculateRedirectUrl(any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean());
        doAnswer(invocation -> invocation.getArgument(0)).when(controller).getMessage(anyString(), any());
    }

    @Test
    public void testGet2FAStatus() {
        java.util.Date now = new java.util.Date();
        org.orcid.pojo.ajaxForm.Date expectedDate = org.orcid.pojo.ajaxForm.Date.valueOf(now);
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        when(backupCodeManager.getBackupCodesCreationDate(ORCID)).thenReturn(now);
        TwoFactorAuthStatus status = controller.get2FAStatus();
        assertTrue(status.isEnabled());
        assertEquals(expectedDate.getYear(), status.getTwoFactorCreationDate().getYear());
        assertEquals(expectedDate.getMonth(), status.getTwoFactorCreationDate().getMonth());
        assertEquals(expectedDate.getDay(), status.getTwoFactorCreationDate().getDay());
        assertEquals(expectedDate.getYear(), status.getRecoveryCodeCreationDate().getYear());
        assertEquals(expectedDate.getMonth(), status.getRecoveryCodeCreationDate().getMonth());
        assertEquals(expectedDate.getDay(), status.getRecoveryCodeCreationDate().getDay());

        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(false);
        status = controller.get2FAStatus();
        assertFalse(status.isEnabled());
        assertNull(status.getTwoFactorCreationDate());
        assertNull(status.getRecoveryCodeCreationDate());
    }

    @Test
    public void testGet2FASetupPage() {
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        ModelAndView mav = controller.get2FASetupPage();
        assertEquals("redirect:redirectUrl", mav.getViewName());

        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(false);
        mav = controller.get2FASetupPage();
        assertEquals("2FA_setup", mav.getViewName());
    }

    @Test
    public void testDisable2FA_InvalidPassword() {
        TwoFactorAuthStatus form = new TwoFactorAuthStatus();
        form.setPassword("wrong");
        ProfileEntity profile = new ProfileEntity();
        profile.setEncryptedPassword("encrypted");
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);
        when(encryptionManager.hashMatches("wrong", "encrypted")).thenReturn(false);

        TwoFactorAuthStatus result = controller.disable2FA(request, form);
        assertTrue(result.isInvalidPassword());
        verify(twoFactorAuthenticationManager, never()).disable2FA(anyString());
    }

    @Test
    public void testDisable2FA_InvalidForm() {
        TwoFactorAuthStatus form = new TwoFactorAuthStatus();
        form.setPassword("correct");
        ProfileEntity profile = new ProfileEntity();
        profile.setEncryptedPassword("encrypted");
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);
        when(encryptionManager.hashMatches("correct", "encrypted")).thenReturn(true);
        when(twoFactorAuthenticationManager.validateTwoFactorAuthForm(eq(ORCID), any(TwoFactorAuthStatus.class))).thenReturn(false);

        TwoFactorAuthStatus result = controller.disable2FA(request, form);
        assertFalse(result.isSuccess());
        verify(twoFactorAuthenticationManager, never()).disable2FA(anyString());
    }

    @Test
    public void testDisable2FA_Success() {
        TwoFactorAuthStatus form = new TwoFactorAuthStatus();
        form.setPassword("correct");
        ProfileEntity profile = new ProfileEntity();
        profile.setEncryptedPassword("encrypted");
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);
        when(encryptionManager.hashMatches("correct", "encrypted")).thenReturn(true);
        when(twoFactorAuthenticationManager.validateTwoFactorAuthForm(eq(ORCID), any(TwoFactorAuthStatus.class))).thenReturn(true);

        TwoFactorAuthStatus result = controller.disable2FA(request, form);
        assertTrue(result.isSuccess());
        verify(twoFactorAuthenticationManager).disable2FA(ORCID);
        verify(recordEmailSender).send2FADisabledEmail(ORCID);
    }

    @Test
    public void testGet2FAQRCode() {
        when(twoFactorAuthenticationManager.getQRCode(ORCID)).thenReturn("qr-url");
        TwoFactorAuthQRCodeUrl result = controller.get2FAQRCode();
        assertEquals("qr-url", result.getUrl());
    }

    @Test
    public void testGenerateQrCode() {
        when(twoFactorAuthenticationManager.getQRCode(ORCID)).thenReturn("otpauth://totp/ORCID:0000-0000-0000-0001?secret=ABC&issuer=ORCID");
        byte[] qrCode = controller.generateQrCode(response);
        assertNotNull(qrCode);
        assertTrue(qrCode.length > 0);
    }

    @Test
    public void testGetVerificationCode() {
        TwoFactorAuthRegistration result = controller.getVerificationCode();
        assertNotNull(result);
    }

    @Test
    public void testValidateVerificationCode_Valid() {
        TwoFactorAuthRegistration registration = new TwoFactorAuthRegistration();
        registration.setVerificationCode("123456");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", ORCID)).thenReturn(true);
        List<String> backupCodes = Arrays.asList("code1", "code2");
        when(twoFactorAuthenticationManager.enable2FA(ORCID)).thenReturn(backupCodes);

        TwoFactorAuthRegistration result = controller.validateVerificationCode(registration);
        assertTrue(result.isValid());
        assertEquals(backupCodes, result.getBackupCodes());
    }

    @Test
    public void testValidateVerificationCode_Invalid() {
        TwoFactorAuthRegistration registration = new TwoFactorAuthRegistration();
        registration.setVerificationCode("654321");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("654321", ORCID)).thenReturn(false);

        TwoFactorAuthRegistration result = controller.validateVerificationCode(registration);
        assertFalse(result.isValid());
        assertNull(result.getBackupCodes());
    }

    @Test
    public void testGetTwoFactorAuthSecret() {
        when(twoFactorAuthenticationManager.getSecret(ORCID)).thenReturn("secret-key");
        TwoFactorAuthSecret result = controller.getTwoFactorAuthSecret();
        assertEquals("secret-key", result.getSecret());
    }

    @Test
    public void testGetTwoFactorCodeWrapper() {
        TwoFactorAuthenticationCodes result = controller.getTwoFactorCodeWrapper();
        assertNotNull(result);
    }

    @Test
    public void testPost2FAVerificationCode_SuccessVerification() {
        TwoFactorAuthenticationCodes codes = new TwoFactorAuthenticationCodes();
        codes.setOrcid(ORCID);
        codes.setVerificationCode("123456");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", ORCID)).thenReturn(true);

        TwoFactorAuthenticationCodes result = controller.post2FAVerificationCode(codes, request, response);
        assertEquals("redirectUrl", result.getRedirectUrl());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void testPost2FAVerificationCode_SuccessRecovery() {
        TwoFactorAuthenticationCodes codes = new TwoFactorAuthenticationCodes();
        codes.setOrcid(ORCID);
        codes.setRecoveryCode("recovery-123");
        when(backupCodeManager.verify(ORCID, "recovery-123")).thenReturn(true);

        TwoFactorAuthenticationCodes result = controller.post2FAVerificationCode(codes, request, response);
        assertEquals("redirectUrl", result.getRedirectUrl());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void testPost2FAVerificationCode_Invalid() {
        TwoFactorAuthenticationCodes codes = new TwoFactorAuthenticationCodes();
        codes.setOrcid(ORCID);
        codes.setVerificationCode("wrong");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("wrong", ORCID)).thenReturn(false);

        TwoFactorAuthenticationCodes result = controller.post2FAVerificationCode(codes, request, response);
        assertNull(result.getRedirectUrl());
        assertEquals(1, result.getErrors().size());
        assertEquals("2FA.verificationCode.invalid", result.getErrors().get(0));
    }

    // Recovery phone number

    private void enableRecoveryPhoneFeature() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
    }

    private void elevateSession() {
        session.setAttribute("RECOVERY_PHONE_ELEVATION_TS", System.currentTimeMillis());
    }

    private static RecoveryPhoneSendCodeRequest sendCodeRequest() {
        RecoveryPhoneSendCodeRequest form = new RecoveryPhoneSendCodeRequest();
        form.setPhoneNumber("+441234567890");
        return form;
    }

    private static RecoveryPhoneSaveRequest saveRequest() {
        RecoveryPhoneSaveRequest form = new RecoveryPhoneSaveRequest();
        form.setPhoneNumber("+441234567890");
        form.setVerificationCode("123456");
        return form;
    }

    @Test
    public void testStatusOmitsRecoveryPhoneWhenFeatureIsOff() {
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);

        TwoFactorAuthStatus status = controller.get2FAStatus();

        assertNull(status.getMaskedRecoveryPhoneNumber());
        verify(recoveryPhoneManager, never()).getRecoveryPhone(anyString());
    }

    @Test
    public void testStatusMasksTheRecoveryPhoneNumber() {
        enableRecoveryPhoneFeature();
        java.util.Date created = new java.util.Date();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(new RecoveryPhone("7890", created, created));

        TwoFactorAuthStatus status = controller.get2FAStatus();

        assertEquals("***********7890", status.getMaskedRecoveryPhoneNumber());
        assertNotNull(status.getRecoveryPhoneCreationDate());
        assertFalse(status.isRecoveryPhoneModified());
    }

    @Test
    public void testStatusReportsANumberThatHasBeenChangedAsModified() {
        enableRecoveryPhoneFeature();
        java.util.Date created = new java.util.Date(1_600_000_000_000L);
        java.util.Date modified = new java.util.Date(1_600_000_000_000L + (10 * 60 * 1000L));
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(new RecoveryPhone("7890", created, modified));

        assertTrue(controller.get2FAStatus().isRecoveryPhoneModified());
    }

    @Test
    public void testAuthChallengeRejectsAWrongPassword() {
        enableRecoveryPhoneFeature();
        ProfileEntity profile = new ProfileEntity();
        profile.setEncryptedPassword("hashed");
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);
        when(encryptionManager.hashMatches("nope", "hashed")).thenReturn(false);

        AuthChallenge form = new AuthChallenge();
        form.setPassword("nope");
        AuthChallenge result = controller.verifyRecoveryPhoneAuthChallenge(request, form);

        assertTrue(result.isInvalidPassword());
        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }

    @Test
    public void testAuthChallengeElevatesTheSessionOnSuccess() {
        enableRecoveryPhoneFeature();
        ProfileEntity profile = new ProfileEntity();
        profile.setEncryptedPassword("hashed");
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);
        when(encryptionManager.hashMatches("correct", "hashed")).thenReturn(true);
        when(twoFactorAuthenticationManager.validateTwoFactorAuthForm(eq(ORCID), any(AuthChallenge.class))).thenReturn(true);

        AuthChallenge form = new AuthChallenge();
        form.setPassword("correct");
        AuthChallenge result = controller.verifyRecoveryPhoneAuthChallenge(request, form);

        assertTrue(result.isSuccess());
        assertNotNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }

    @Test
    public void testSendCodeNeedsAPassedChallenge() {
        enableRecoveryPhoneFeature();

        RecoveryPhoneSendCodeResponse response = controller.sendRecoveryPhoneCode(request, sendCodeRequest());

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED, response.getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSendCodeRefusesAnExpiredChallenge() {
        enableRecoveryPhoneFeature();
        session.setAttribute("RECOVERY_PHONE_ELEVATION_TS", System.currentTimeMillis() - (16 * 60 * 1000L));

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, sendCodeRequest()).getErrorCode());
    }

    @Test
    public void testSendCodeIsRefusedWhenTheFeatureIsOff() {
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        elevateSession();

        assertEquals(TwoFactorAuthenticationController.FEATURE_DISABLED,
                controller.sendRecoveryPhoneCode(request, sendCodeRequest()).getErrorCode());
    }

    @Test
    public void testSendCodeIsRefusedWhen2FAIsOff() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(false);
        elevateSession();

        assertEquals(TwoFactorAuthenticationController.TWO_FACTOR_DISABLED,
                controller.sendRecoveryPhoneCode(request, sendCodeRequest()).getErrorCode());
    }

    @Test
    public void testSendCodeDelegatesOnceElevated() {
        enableRecoveryPhoneFeature();
        elevateSession();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));

        RecoveryPhoneSendCodeResponse response = controller.sendRecoveryPhoneCode(request, sendCodeRequest());

        assertTrue(response.isSuccess());
        assertEquals(30, response.getResendAfterSeconds());
    }

    @Test
    public void testSaveReportsAFailedCodeAndKeepsTheElevation() {
        enableRecoveryPhoneFeature();
        elevateSession();
        when(recoveryPhoneVerificationService.verifyCode(eq(ORCID), anyString(), anyString())).thenReturn("INVALID_CODE");

        RecoveryPhoneSaveResponse response = controller.saveRecoveryPhone(request, saveRequest());

        assertFalse(response.isSuccess());
        assertEquals("INVALID_CODE", response.getErrorCode());
        verify(recoveryPhoneManager, never()).saveRecoveryPhone(anyString(), anyString());
        assertNotNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }

    @Test
    public void testSaveStoresTheNumberAndClearsTheElevation() {
        enableRecoveryPhoneFeature();
        elevateSession();
        java.util.Date now = new java.util.Date();
        when(recoveryPhoneVerificationService.verifyCode(eq(ORCID), anyString(), anyString())).thenReturn(null);
        when(recoveryPhoneVerificationService.normalize("+441234567890")).thenReturn("+441234567890");
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(new RecoveryPhone("7890", now, now));

        RecoveryPhoneSaveResponse response = controller.saveRecoveryPhone(request, saveRequest());

        assertTrue(response.isSuccess());
        assertEquals("***********7890", response.getMaskedRecoveryPhoneNumber());
        verify(recoveryPhoneManager).saveRecoveryPhone(ORCID, "+441234567890");
        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }
}
