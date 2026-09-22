package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.orcid.authorization.authentication.MFAWebAuthenticationDetails;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RecoveryPhone;
import org.orcid.core.manager.RecoveryPhoneManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSendCodeRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSendCodeResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSigninSendCodeRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSigninSendCodeResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSigninVerifyRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSigninVerifyResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneVerificationService;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.togglz.junit.TogglzRule;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RecoveryPhoneSigninControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String EMAIL = "user@orcid.org";

    private static final String PASSWORD = "correct-horse";

    private static final String STORED_NUMBER = "+441234567890";

    private static final String REMOTE_ADDRESS = "198.51.100.7";

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private RecoveryPhoneManager recoveryPhoneManager;

    @Mock
    private RecoveryPhoneVerificationService recoveryPhoneVerificationService;

    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private RecordEmailSender recordEmailSender;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Spy
    @InjectMocks
    private RecoveryPhoneSigninController controller;

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        request.setRemoteAddr(REMOTE_ADDRESS);
        doReturn(Locale.ENGLISH).when(controller).getLocale();
    }

    private static RecoveryPhoneSigninSendCodeRequest sendCodeRequest(String username) {
        RecoveryPhoneSigninSendCodeRequest form = new RecoveryPhoneSigninSendCodeRequest();
        form.setUsername(username);
        form.setPassword(PASSWORD);
        return form;
    }

    private static RecoveryPhoneSigninVerifyRequest verifyRequest(String username, String code) {
        RecoveryPhoneSigninVerifyRequest form = new RecoveryPhoneSigninVerifyRequest();
        form.setUsername(username);
        form.setPassword(PASSWORD);
        form.setVerificationCode(code);
        return form;
    }

    private static RecoveryPhone storedRecoveryPhone() {
        Date now = new Date();
        return new RecoveryPhone("7890", now, now);
    }

    /** The password is right and the account is using 2FA: the recoverable state. */
    private void passwordIsCorrectAnd2FAIsOn() {
        when(authenticationProvider.authenticate(any(Authentication.class))).thenThrow(new VerificationCodeFor2FARequiredException());
    }

    /** The password is right but the account has nothing to recover from. */
    private void passwordIsCorrectAnd2FAIsOff() {
        when(authenticationProvider.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(ORCID, PASSWORD, Collections.emptyList()));
    }

    private void passwordIsWrong() {
        when(authenticationProvider.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Invalid username or password"));
    }

    /** The mask and the dates come from one read, the number from the other. */
    private void aRecoveryPhoneIsStored() {
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone());
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn(STORED_NUMBER);
    }

    @Test
    public void testSendCodeIsInertWhenTheFeatureIsOff() {
        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.FEATURE_DISABLED, response.getErrorCode());
        verify(authenticationProvider, never()).authenticate(any(Authentication.class));
        verify(recoveryPhoneManager, never()).getRecoveryPhone(anyString());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testVerifyIsInertWhenTheFeatureIsOff() {
        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "123456"));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.FEATURE_DISABLED, response.getErrorCode());
        verify(authenticationProvider, never()).authenticate(any(Authentication.class));
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
    }

    /**
     * A wrong password has to reach the authentication provider, because that is
     * what counts it toward the sign in lockout (R3.2).
     */
    @Test
    public void testAWrongPasswordIsCheckedThroughTheAuthenticationProviderAndSendsNoCode() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsWrong();

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.BAD_CREDENTIALS, response.getErrorCode());
        assertNull(response.getMaskedRecoveryPhoneNumber());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
        verify(recoveryPhoneManager, never()).getRecoveryPhone(anyString());
    }

    /**
     * The token carries the credentials and the caller's address, and no 2FA
     * codes at all, which is what makes the provider answer "2FA required"
     * rather than trying to verify something.
     */
    @Test
    public void testTheAuthenticationTokenCarriesNo2FACodes() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsWrong();

        controller.sendCode(request, sendCodeRequest(ORCID));

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationProvider).authenticate(captor.capture());
        Authentication token = captor.getValue();
        assertEquals(ORCID, token.getName());
        assertEquals(PASSWORD, token.getCredentials());
        MFAWebAuthenticationDetails details = (MFAWebAuthenticationDetails) token.getDetails();
        assertNull(details.getVerificationCode());
        assertNull(details.getRecoveryCode());
        assertEquals(REMOTE_ADDRESS, details.getRemoteAddress());
    }

    @Test
    public void testSendCodeRefusesAnAccountThatIsNotUsing2FA() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOff();

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.TWO_FACTOR_DISABLED, response.getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testSendCodeSaysSoWhenNoNumberIsStored() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(null);

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.NO_RECOVERY_PHONE, response.getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
        // Saying "no recovery phone" costs no decryption
        verify(recoveryPhoneManager, never()).getDecryptedPhoneNumber(anyString());
    }

    /**
     * The row says there is a number but the number cannot be read: the caller
     * is told there is none rather than being texted at null.
     */
    @Test
    public void testSendCodeSaysSoWhenTheStoredNumberCannotBeRead() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone());
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn(null);

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.NO_RECOVERY_PHONE, response.getErrorCode());
        verify(recoveryPhoneVerificationService, never()).sendCode(anyString(), any(RecoveryPhoneSendCodeRequest.class));
    }

    /**
     * The number is read from the record, never taken from the request, and only
     * its last four digits come back.
     */
    @Test
    public void testSendCodeTextsTheStoredNumberAndReturnsTheMask() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertTrue(response.isSuccess());
        assertNull(response.getErrorCode());
        assertEquals(30, response.getResendAfterSeconds());
        assertEquals("***********7890", response.getMaskedRecoveryPhoneNumber());

        ArgumentCaptor<RecoveryPhoneSendCodeRequest> captor = ArgumentCaptor.forClass(RecoveryPhoneSendCodeRequest.class);
        verify(recoveryPhoneVerificationService).sendCode(eq(ORCID), captor.capture());
        assertEquals(STORED_NUMBER, captor.getValue().getPhoneNumber());
        assertEquals("en", captor.getValue().getLocale());
        verify(recoveryPhoneManager).getDecryptedPhoneNumber(ORCID);
    }

    @Test
    public void testSendCodePassesThroughAThrottledResend() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.failure(RecoveryPhoneVerificationService.RESEND_TOO_SOON, 12));

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.RESEND_TOO_SOON, response.getErrorCode());
        assertEquals(12, response.getResendAfterSeconds());
        assertEquals("***********7890", response.getMaskedRecoveryPhoneNumber());
    }

    @Test
    public void testSendCodePassesThroughTheDailySendLimit() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.failure(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED));

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(ORCID));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, response.getErrorCode());
    }

    /** An email address is as good a username here as an iD is. */
    @Test
    public void testAnEmailAddressResolvesToTheRecord() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        when(emailManagerReadOnly.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(EMAIL));

        assertTrue(response.isSuccess());
        verify(emailManagerReadOnly).findOrcidIdByEmail(EMAIL);
        verify(recoveryPhoneManager).getRecoveryPhone(ORCID);
        verify(recoveryPhoneVerificationService).sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class));
    }

    @Test
    public void testAnOrcidIdUsernameIsNotLookedUpAsAnEmail() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.sendCode(eq(ORCID), any(RecoveryPhoneSendCodeRequest.class)))
                .thenReturn(RecoveryPhoneSendCodeResponse.success(30));

        controller.sendCode(request, sendCodeRequest(ORCID));

        verify(emailManagerReadOnly, never()).findOrcidIdByEmail(anyString());
    }

    @Test
    public void testVerifyRejectsAWrongPasswordWithoutDisabling2FA() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsWrong();

        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "123456"));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.BAD_CREDENTIALS, response.getErrorCode());
        assertNull(response.getOrcid());
        verify(recoveryPhoneVerificationService, never()).verifyCode(anyString(), anyString(), anyString());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
    }

    @Test
    public void testVerifySaysSoWhenNoNumberIsStored() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(null);

        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "123456"));

        assertEquals(RecoveryPhoneSigninController.NO_RECOVERY_PHONE, response.getErrorCode());
        verify(recoveryPhoneVerificationService, never()).verifyCode(anyString(), anyString(), anyString());
        verify(recoveryPhoneManager, never()).getDecryptedPhoneNumber(anyString());
    }

    @Test
    public void testVerifySaysSoWhenTheStoredNumberCannotBeRead() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        when(recoveryPhoneManager.getRecoveryPhone(ORCID)).thenReturn(storedRecoveryPhone());
        when(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID)).thenReturn(null);

        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "123456"));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneSigninController.NO_RECOVERY_PHONE, response.getErrorCode());
        verify(recoveryPhoneVerificationService, never()).verifyCode(anyString(), anyString(), anyString());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
    }

    /** A wrong code changes nothing at all about the account. */
    @Test
    public void testVerifyReportsAWrongCodeAndLeaves2FAEnabled() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.verifyCode(ORCID, STORED_NUMBER, "000000"))
                .thenReturn(RecoveryPhoneVerificationService.INVALID_CODE);

        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "000000"));

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.INVALID_CODE, response.getErrorCode());
        assertNull(response.getOrcid());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
        verify(recordEmailSender, never()).send2FADisabledEmail(anyString());
        verify(profileEntityCacheManager, never()).remove(anyString());
    }

    @Test
    public void testVerifyPassesThroughAnExhaustedAttemptCount() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.verifyCode(ORCID, STORED_NUMBER, "000000"))
                .thenReturn(RecoveryPhoneVerificationService.TOO_MANY_ATTEMPTS);

        assertEquals(RecoveryPhoneVerificationService.TOO_MANY_ATTEMPTS,
                controller.verify(request, verifyRequest(ORCID, "000000")).getErrorCode());
        verify(twoFactorAuthenticationManager, never()).disable2FAByRecoveryPhone(anyString());
    }

    /**
     * The good code disables 2FA, evicts the cached profile and only then tells
     * the user by email: the sign in the browser replays next must not meet a
     * cached using2FA=true, so the eviction cannot sit behind anything that can
     * fail (R3.5).
     */
    @Test
    public void testVerifyDisables2FAAndEvictsTheCachedProfileBeforeTheEmail() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.verifyCode(ORCID, STORED_NUMBER, "123456")).thenReturn(null);

        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "123456"));

        assertTrue(response.isSuccess());
        assertNull(response.getErrorCode());
        assertEquals(ORCID, response.getOrcid());

        InOrder inOrder = inOrder(twoFactorAuthenticationManager, profileEntityCacheManager, recordEmailSender);
        inOrder.verify(twoFactorAuthenticationManager).disable2FAByRecoveryPhone(ORCID);
        inOrder.verify(profileEntityCacheManager).remove(ORCID);
        inOrder.verify(recordEmailSender).send2FADisabledEmail(ORCID);
    }

    /**
     * The disable is irreversible, so a failing notification must not report the
     * operation as failed: 2FA is off, the number and the backup codes are gone,
     * and the browser's next sign in has to find the cache already evicted.
     */
    @Test
    public void testAFailing2FADisabledEmailStillLeavesTheRecoverySuccessful() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.verifyCode(ORCID, STORED_NUMBER, "123456")).thenReturn(null);
        doThrow(new RuntimeException("mail server is down")).when(recordEmailSender).send2FADisabledEmail(ORCID);

        RecoveryPhoneSigninVerifyResponse response = controller.verify(request, verifyRequest(ORCID, "123456"));

        assertTrue(response.isSuccess());
        assertNull(response.getErrorCode());
        assertEquals(ORCID, response.getOrcid());
        verify(twoFactorAuthenticationManager).disable2FAByRecoveryPhone(ORCID);
        verify(profileEntityCacheManager).remove(ORCID);
    }

    /** The code is checked against the stored number, not one the caller sent. */
    @Test
    public void testVerifyChecksTheCodeAgainstTheStoredNumber() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        aRecoveryPhoneIsStored();
        when(recoveryPhoneVerificationService.verifyCode(anyString(), anyString(), anyString())).thenReturn(null);

        controller.verify(request, verifyRequest(ORCID, "123456"));

        verify(recoveryPhoneVerificationService).verifyCode(ORCID, STORED_NUMBER, "123456");
    }

    @Test
    public void testBlankCredentialsNeverReachTheAuthenticationProvider() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        RecoveryPhoneSigninSendCodeRequest form = new RecoveryPhoneSigninSendCodeRequest();
        form.setUsername(ORCID);
        form.setPassword("");

        assertEquals(RecoveryPhoneSigninController.BAD_CREDENTIALS, controller.sendCode(request, form).getErrorCode());
        verify(authenticationProvider, never()).authenticate(any(Authentication.class));
    }

    /**
     * The credential check is bound to the provider bean and not to the
     * "authenticationManager" ProviderManager, which publishes an authentication
     * success event: on an account with 2FA off that event would record a sign
     * in that never happened, and run every listener on it, for a caller who
     * only posted a password to a recovery endpoint (R3.2).
     */
    @Test
    public void testTheCredentialCheckIsBoundToTheProviderAndNotToTheManager() throws Exception {
        Field field = RecoveryPhoneSigninController.class.getDeclaredField("authenticationProvider");
        assertEquals(AuthenticationProvider.class, field.getType());
        assertEquals("authenticationProvider", field.getAnnotation(Resource.class).name());
        for (Field declared : RecoveryPhoneSigninController.class.getDeclaredFields()) {
            assertFalse("No authentication manager may be injected here", AuthenticationManager.class.isAssignableFrom(declared.getType()));
        }
    }

    @Test
    public void testAUsernameThatResolvesToNoRecordIsACredentialFailure() {
        togglzRule.enable(Features.TWO_FACTOR_RECOVERY_PHONE);
        passwordIsCorrectAnd2FAIsOn();
        when(emailManagerReadOnly.findOrcidIdByEmail(EMAIL)).thenReturn(null);

        RecoveryPhoneSigninSendCodeResponse response = controller.sendCode(request, sendCodeRequest(EMAIL));

        assertEquals(RecoveryPhoneSigninController.BAD_CREDENTIALS, response.getErrorCode());
        verify(recoveryPhoneManager, never()).getRecoveryPhone(anyString());
    }
}
