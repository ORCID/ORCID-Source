package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.*;
import org.springframework.web.servlet.ModelAndView;

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doReturn(ORCID).when(controller).getCurrentUserOrcid();
        doReturn("redirectUrl").when(controller).calculateRedirectUrl(anyString());
        doReturn("redirectUrl").when(controller).calculateRedirectUrl(any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean());
        doAnswer(invocation -> invocation.getArgument(0)).when(controller).getMessage(anyString(), any());
    }

    @Test
    public void testGet2FAStatus() {
        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(true);
        TwoFactorAuthStatus status = controller.get2FAStatus();
        assertTrue(status.isEnabled());

        when(twoFactorAuthenticationManager.userUsing2FA(ORCID)).thenReturn(false);
        status = controller.get2FAStatus();
        assertFalse(status.isEnabled());
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
}
