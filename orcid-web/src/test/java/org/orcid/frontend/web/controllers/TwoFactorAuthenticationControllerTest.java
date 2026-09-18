package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.recoveryphone.RecoveryPhoneChallengeSendCodeResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneChallengeVerifyRequest;
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
import java.util.Locale;

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
    private ProfileEntityManager profileEntityManager;

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
        // The session is the account owner's own unless a test says otherwise
        doReturn(ORCID).when(controller).getRealUserOrcid();
        doReturn("redirectUrl").when(controller).calculateRedirectUrl(anyString());
        doReturn("redirectUrl").when(controller).calculateRedirectUrl(any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean());
        doAnswer(invocation -> invocation.getArgument(0)).when(controller).getMessage(anyString(), any());
        doReturn(Locale.ENGLISH).when(controller).getLocale();
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

        TwoFactorAuthRegistration result = controller.validateVerificationCode(request, registration);
        assertTrue(result.isValid());
        assertEquals(backupCodes, result.getBackupCodes());
    }

    @Test
    public void testValidateVerificationCode_Invalid() {
        TwoFactorAuthRegistration registration = new TwoFactorAuthRegistration();
        registration.setVerificationCode("654321");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("654321", ORCID)).thenReturn(false);

        TwoFactorAuthRegistration result = controller.validateVerificationCode(request, registration);
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

    /** Both flags: the interstitial carries a second one of its own (R7.4). */
    private void enableInterstitialFeature() {
        enableRecoveryPhoneFeature();
        togglzRule.enable(Features.LOGIN_RECOVERY_PHONE_INTERSTITIAL);
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

    /**
     * What the record carries for a stored number: the mask and the dates.
     *
     * Built with the real constructor rather than mocked. RecoveryPhone is an
     * immutable value object, so there is nothing to fake; and a helper that
     * stubs a mock cannot be called from inside another when(...), which is
     * where every call below sits. Mockito rejects that with
     * UnfinishedStubbingException, and its message points at the outer stub
     * rather than at the nested one.
     */
    private static RecoveryPhone storedRecoveryPhone(String lastFour, java.util.Date dateCreated, java.util.Date lastModified) {
        return new RecoveryPhone(lastFour, dateCreated, lastModified);
    }

    /**
     * Puts a profile in the cache and records a last login of the given age in
     * the database, or no last login at all when millisAgo is null.
     *
     * The cached profile deliberately carries a sign in from a day ago, out of
     * every window: the cache is loaded while the user is being authenticated,
     * before the success handler writes last_login, so what it holds is always
     * the previous sign in. Only the database read may be believed.
     */
    private ProfileEntity profileWithLastLogin(Long millisAgo) {
        ProfileEntity profile = new ProfileEntity();
        profile.setEncryptedPassword("hashed");
        profile.setLastLogin(new java.util.Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000L)));
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);
        if (millisAgo != null) {
            when(profileEntityManager.getLastLogin(ORCID)).thenReturn(new java.util.Date(System.currentTimeMillis() - millisAgo));
        }
        return profile;
    }

    private static RecoveryPhoneChallengeVerifyRequest challengeVerifyRequest(String password, String verificationCode) {
        RecoveryPhoneChallengeVerifyRequest form = new RecoveryPhoneChallengeVerifyRequest();
        form.setPassword(password);
        form.setVerificationCode(verificationCode);
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
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", created, created));

        TwoFactorAuthStatus status = controller.get2FAStatus();

        assertEquals("***********7890", status.getMaskedRecoveryPhoneNumber());
        assertNotNull(status.getRecoveryPhoneCreationDate());
        assertFalse(status.isRecoveryPhoneModified());
    }

    @Test
    public void testStatusReportsANumberChangedSecondsAfterItWasAddedAsModified() {
        // A user who adds a number and corrects it straight away has changed it; the panel must date
        // the row from the change, not from the first entry.
        enableRecoveryPhoneFeature();
        java.util.Date created = new java.util.Date(1_600_000_000_000L);
        java.util.Date modified = new java.util.Date(1_600_000_000_000L + (5 * 1000L));
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", created, modified));

        assertTrue(controller.get2FAStatus().isRecoveryPhoneModified());
    }

    @Test
    public void testStatusReportsANumberThatHasBeenChangedAsModified() {
        enableRecoveryPhoneFeature();
        java.util.Date created = new java.util.Date(1_600_000_000_000L);
        java.util.Date modified = new java.util.Date(1_600_000_000_000L + (10 * 60 * 1000L));
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", created, modified));

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
    public void testSendCodePassesThroughTheDailySendLimit() {
        enableRecoveryPhoneFeature();
        elevateSession();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.failure(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED));

        RecoveryPhoneSendCodeResponse response = controller.sendRecoveryPhoneCode(request, sendCodeRequest());

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, response.getErrorCode());
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
        when(recoveryPhoneManager.saveRecoveryPhone(ORCID, "+441234567890")).thenReturn(storedRecoveryPhone("7890", now, now));

        RecoveryPhoneSaveResponse response = controller.saveRecoveryPhone(request, saveRequest());

        assertTrue(response.isSuccess());
        assertEquals("***********7890", response.getMaskedRecoveryPhoneNumber());
        assertFalse(response.isRecoveryPhoneModified());
        verify(recoveryPhoneManager).saveRecoveryPhone(ORCID, "+441234567890");
        // Answered from the row the save wrote. A read after the write would go to the
        // read-only pool, a replica on a deployed environment, and could still carry the
        // previous number or none at all
        verify(recoveryPhoneManager, never()).getRecoveryPhone(anyString());
        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }

    // Onboarding: the live 2FA code posted to register.json stands in for a
    // challenge on the recovery phone step that follows it (R2.6)

    @Test
    public void testOnboardingElevatesTheSessionForTheRecoveryPhoneStep() {
        TwoFactorAuthRegistration registration = new TwoFactorAuthRegistration();
        registration.setVerificationCode("123456");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", ORCID)).thenReturn(true);
        when(twoFactorAuthenticationManager.enable2FA(ORCID)).thenReturn(Arrays.asList("code1", "code2"));

        assertTrue(controller.validateVerificationCode(request, registration).isValid());
        assertNotNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));

        // and that elevation is what carries step 2 of setup, with no second
        // challenge in between
        enableRecoveryPhoneFeature();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_ONBOARDING);

        assertTrue(controller.sendRecoveryPhoneCode(request, form).isSuccess());
    }

    @Test
    public void testOnboardingDoesNotElevateOnAnInvalidCode() {
        TwoFactorAuthRegistration registration = new TwoFactorAuthRegistration();
        registration.setVerificationCode("654321");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("654321", ORCID)).thenReturn(false);

        assertFalse(controller.validateVerificationCode(request, registration).isValid());

        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
        verify(twoFactorAuthenticationManager, never()).enable2FA(anyString());
    }

    @Test
    public void testRegisterDoesNotElevateWhen2FAWasAlreadyOn() {
        // The elevation belongs to step 1 of setup. On an account that is
        // already using 2FA there is no step 2 to carry, and a time based code
        // there must not stand in for the password challenge that guards
        // changing a recovery number (R2.6)
        enableRecoveryPhoneFeature();
        TwoFactorAuthRegistration registration = new TwoFactorAuthRegistration();
        registration.setVerificationCode("123456");
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", ORCID)).thenReturn(true);
        when(twoFactorAuthenticationManager.enable2FA(ORCID)).thenReturn(Arrays.asList("code1", "code2"));

        assertTrue(controller.validateVerificationCode(request, registration).isValid());
        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));

        // so the recovery phone endpoints still ask for the challenge
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_ONBOARDING);
        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, form).getErrorCode());
    }

    // Interstitial: a recent sign in stands in for a challenge, since the user
    // completed 2FA to reach it and it has nowhere to put one (R6.3). The
    // context is a claim the client makes, so every condition the interstitial
    // is shown under is checked again here (R6.1, R7.4)

    @Test
    public void testSendCodeAcceptsAFreshLastLoginFromTheInterstitial() {
        enableInterstitialFeature();
        profileWithLastLogin(60 * 1000L);
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        RecoveryPhoneSendCodeResponse response = controller.sendRecoveryPhoneCode(request, form);

        assertTrue(response.isSuccess());
        // The cached profile says a day ago and the database says a minute ago:
        // only the database has the sign in the user has just completed (R6.3)
        verify(profileEntityManager).getLastLogin(ORCID);
        // No challenge was passed: the sign in is what let this request in
        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }

    @Test
    public void testSendCodeRefusesAStaleLastLoginFromTheInterstitial() {
        enableInterstitialFeature();
        profileWithLastLogin(16 * 60 * 1000L);
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, form).getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSendCodeRefusesTheInterstitialWithNoRecordedLastLogin() {
        enableInterstitialFeature();
        profileWithLastLogin(null);
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, form).getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSendCodeRefusesTheInterstitialWhenItsOwnFeatureIsOff() {
        // TWO_FACTOR_RECOVERY_PHONE on its own is not enough: the relaxed path
        // belongs to the interstitial and goes away with its flag (R7.4)
        enableRecoveryPhoneFeature();
        profileWithLastLogin(60 * 1000L);
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, form).getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSendCodeRefusesTheInterstitialWhenANumberIsAlreadyStored() {
        // The interstitial is only offered when there is no number, so it can
        // add a first one and never replace one. Replacing without the password
        // would let a stolen session point the number at a phone it holds, and
        // that number turns 2FA off at the next sign in (R6.1)
        enableInterstitialFeature();
        profileWithLastLogin(60 * 1000L);
        java.util.Date now = new java.util.Date();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", now, now));
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, form).getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSaveRefusesTheInterstitialWhenANumberIsAlreadyStored() {
        enableInterstitialFeature();
        profileWithLastLogin(60 * 1000L);
        java.util.Date now = new java.util.Date();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", now, now));
        RecoveryPhoneSaveRequest form = saveRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED, controller.saveRecoveryPhone(request, form).getErrorCode());
        verify(recoveryPhoneManager, never()).saveRecoveryPhone(anyString(), anyString());
    }

    @Test
    public void testSendCodeRefusesTheInterstitialForAnImpersonatedSession() {
        // A delegate or an admin switched into the record: their sign in is
        // not the account owner's, and the interstitial is never shown to
        // them in the first place (R6.1)
        enableInterstitialFeature();
        profileWithLastLogin(60 * 1000L);
        doReturn("0000-0000-0000-0002").when(controller).getRealUserOrcid();
        RecoveryPhoneSendCodeRequest form = sendCodeRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, form).getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSendCodeStillDemandsTheChallengeOutsideTheInterstitial() {
        enableInterstitialFeature();
        // A sign in this fresh would let the interstitial through
        profileWithLastLogin(60 * 1000L);

        RecoveryPhoneSendCodeRequest settings = sendCodeRequest();
        settings.setContext(TwoFactorAuthenticationController.CONTEXT_SETTINGS);
        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, settings).getErrorCode());

        // An unstated context is settings, the strictest of the three
        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, sendCodeRequest()).getErrorCode());

        // Onboarding has its own elevation and does not ride on the sign in
        RecoveryPhoneSendCodeRequest onboarding = sendCodeRequest();
        onboarding.setContext(TwoFactorAuthenticationController.CONTEXT_ONBOARDING);
        assertEquals(TwoFactorAuthenticationController.CHALLENGE_REQUIRED,
                controller.sendRecoveryPhoneCode(request, onboarding).getErrorCode());

        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSaveAcceptsAFreshLastLoginFromTheInterstitial() {
        enableInterstitialFeature();
        profileWithLastLogin(60 * 1000L);
        java.util.Date now = new java.util.Date();
        when(recoveryPhoneVerificationService.verifyCode(eq(ORCID), anyString(), anyString())).thenReturn(null);
        when(recoveryPhoneVerificationService.normalize("+441234567890")).thenReturn("+441234567890");
        // Nothing stored while the guard looks; the save answers with what it wrote
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(null);
        when(recoveryPhoneManager.saveRecoveryPhone(ORCID, "+441234567890")).thenReturn(storedRecoveryPhone("7890", now, now));
        RecoveryPhoneSaveRequest form = saveRequest();
        form.setContext(TwoFactorAuthenticationController.CONTEXT_INTERSTITIAL);

        assertTrue(controller.saveRecoveryPhone(request, form).isSuccess());
        verify(recoveryPhoneManager).saveRecoveryPhone(ORCID, "+441234567890");
    }

    // The recovery phone as the authentication challenge itself (R5)

    @Test
    public void testChallengeSendCodeUsesTheStoredNumberAndReturnsTheMask() {
        enableRecoveryPhoneFeature();
        java.util.Date now = new java.util.Date();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", now, now));
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn("+441234567890");
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));

        // No elevation on this session: this endpoint is the challenge (R5.1)
        RecoveryPhoneChallengeSendCodeResponse response = controller.sendRecoveryPhoneChallengeCode();

        assertTrue(response.isSuccess());
        assertEquals(30, response.getResendAfterSeconds());
        assertEquals("***********7890", response.getMaskedRecoveryPhoneNumber());

        ArgumentCaptor<RecoveryPhoneSendCodeRequest> sentRequest = ArgumentCaptor.forClass(RecoveryPhoneSendCodeRequest.class);
        verify(recoveryPhoneVerificationService).sendCode(eq(ORCID), sentRequest.capture());
        assertEquals("+441234567890", sentRequest.getValue().getPhoneNumber());
        // The locale is the server's, not something the client can set here
        assertEquals("en", sentRequest.getValue().getLocale());
    }

    @Test
    public void testChallengeSendCodePassesThroughTheDailySendLimit() {
        enableRecoveryPhoneFeature();
        java.util.Date now = new java.util.Date();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone("7890", now, now));
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn("+441234567890");
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.failure(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED));

        RecoveryPhoneChallengeSendCodeResponse response = controller.sendRecoveryPhoneChallengeCode();

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, response.getErrorCode());
        // the mask still comes back: the user is looking at a number they own
        assertEquals("***********7890", response.getMaskedRecoveryPhoneNumber());
    }

    @Test
    public void testChallengeSendCodeIsRefusedWhenNoNumberIsStored() {
        enableRecoveryPhoneFeature();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(null);

        RecoveryPhoneChallengeSendCodeResponse response = controller.sendRecoveryPhoneChallengeCode();

        assertFalse(response.isSuccess());
        assertEquals(TwoFactorAuthenticationController.NO_RECOVERY_PHONE, response.getErrorCode());
        assertNull(response.getMaskedRecoveryPhoneNumber());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testChallengeSendCodeIsRefusedWhenTheFeatureIsOff() {
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);

        RecoveryPhoneChallengeSendCodeResponse response = controller.sendRecoveryPhoneChallengeCode();

        assertFalse(response.isSuccess());
        assertEquals(TwoFactorAuthenticationController.FEATURE_DISABLED, response.getErrorCode());
        verify(recoveryPhoneManager, never()).getRecoveryPhone(anyString());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testChallengeSendCodeIsRefusedWhen2FAIsOff() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(false);

        assertEquals(TwoFactorAuthenticationController.TWO_FACTOR_DISABLED,
                controller.sendRecoveryPhoneChallengeCode().getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testChallengeVerifyRejectsAWrongPasswordAndDisablesNothing() {
        enableRecoveryPhoneFeature();
        profileWithLastLogin(60 * 1000L);
        when(encryptionManager.hashMatches("nope", "hashed")).thenReturn(false);

        AuthChallenge result = controller.verifyRecoveryPhoneChallengeCode(request, challengeVerifyRequest("nope", "123456"));

        assertFalse(result.isSuccess());
        assertTrue(result.isInvalidPassword());
        assertTrue(result.getErrors().contains(TwoFactorAuthenticationController.INVALID_PASSWORD));
        // The code is never looked at, so a wrong password cannot spend its attempts
        verify(recoveryPhoneVerificationService, never()).verifyCode(anyString(), anyString(), anyString());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
        verify(recordEmailSender, never()).send2FADisabledEmail(anyString());
    }

    @Test
    public void testChallengeVerifyRejectsAWrongCodeAndDisablesNothing() {
        enableRecoveryPhoneFeature();
        profileWithLastLogin(60 * 1000L);
        when(encryptionManager.hashMatches("correct", "hashed")).thenReturn(true);
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn("+441234567890");
        when(recoveryPhoneVerificationService.verifyCode(ORCID, "+441234567890", "000000"))
                .thenReturn(RecoveryPhoneVerificationService.INVALID_CODE);

        AuthChallenge result = controller.verifyRecoveryPhoneChallengeCode(request, challengeVerifyRequest("correct", "000000"));

        assertFalse(result.isSuccess());
        assertFalse(result.isInvalidPassword());
        assertTrue(result.getErrors().contains(RecoveryPhoneVerificationService.INVALID_CODE));
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
        verify(recordEmailSender, never()).send2FADisabledEmail(anyString());
        verify(profileEntityCacheManager, never()).remove(anyString());
    }

    @Test
    public void testChallengeVerifyIsRefusedWhenNoNumberIsStored() {
        enableRecoveryPhoneFeature();
        profileWithLastLogin(60 * 1000L);
        when(encryptionManager.hashMatches("correct", "hashed")).thenReturn(true);
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn(null);

        AuthChallenge result = controller.verifyRecoveryPhoneChallengeCode(request, challengeVerifyRequest("correct", "123456"));

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains(TwoFactorAuthenticationController.NO_RECOVERY_PHONE));
        verify(recoveryPhoneVerificationService, never()).verifyCode(anyString(), anyString(), anyString());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
    }

    @Test
    public void testChallengeVerifyIsRefusedWhenTheFeatureIsOff() {
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);

        AuthChallenge result = controller.verifyRecoveryPhoneChallengeCode(request, challengeVerifyRequest("correct", "123456"));

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains(TwoFactorAuthenticationController.FEATURE_DISABLED));
        verify(encryptionManager, never()).hashMatches(anyString(), anyString());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
    }

    @Test
    public void testChallengeVerifyDisables2FAOnAGoodCode() {
        enableRecoveryPhoneFeature();
        // An elevation from an earlier challenge has nothing left to guard
        elevateSession();
        profileWithLastLogin(60 * 1000L);
        when(encryptionManager.hashMatches("correct", "hashed")).thenReturn(true);
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn("+441234567890");
        when(recoveryPhoneVerificationService.verifyCode(ORCID, "+441234567890", "123456")).thenReturn(null);

        AuthChallenge result = controller.verifyRecoveryPhoneChallengeCode(request, challengeVerifyRequest("correct", "123456"));

        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
        // The code is checked against the stored number, never one a client sent
        verify(recoveryPhoneVerificationService).verifyCode(ORCID, "+441234567890", "123456");
        verify(twoFactorAuthenticationManager).disable2FAByRecoveryPhone(ORCID);
        verify(recordEmailSender).send2FADisabledEmail(ORCID);
        verify(profileEntityCacheManager).remove(ORCID);
        assertNull(session.getAttribute("RECOVERY_PHONE_ELEVATION_TS"));
    }

    @Test
    public void testChallengeVerifySucceedsWhenTheEmailCannotBeSent() {
        // The email only reports something that has already happened and
        // cannot be undone. Telling the user the challenge failed while their
        // 2FA is off, and leaving the cache saying it is still on, is worse
        // than a missing notification (R5.3)
        enableRecoveryPhoneFeature();
        profileWithLastLogin(60 * 1000L);
        when(encryptionManager.hashMatches("correct", "hashed")).thenReturn(true);
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn("+441234567890");
        when(recoveryPhoneVerificationService.verifyCode(ORCID, "+441234567890", "123456")).thenReturn(null);
        doThrow(new RuntimeException("the mail server is down")).when(recordEmailSender).send2FADisabledEmail(ORCID);

        AuthChallenge result = controller.verifyRecoveryPhoneChallengeCode(request, challengeVerifyRequest("correct", "123456"));

        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
        verify(twoFactorAuthenticationManager).disable2FAByRecoveryPhone(ORCID);
        verify(profileEntityCacheManager).remove(ORCID);
    }
}
