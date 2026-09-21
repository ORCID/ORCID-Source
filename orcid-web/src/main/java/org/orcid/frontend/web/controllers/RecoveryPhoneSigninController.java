package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.orcid.authorization.authentication.MFAWebAuthenticationDetails;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RecoveryPhone;
import org.orcid.core.manager.RecoveryPhoneManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
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
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

/**
 * Lets a user who still knows their password, but has lost both their
 * authentication app and their 2FA recovery codes, receive a text on the
 * recovery number stored on their record and get back in.
 *
 * These are the only recovery phone endpoints reachable without a session, so
 * everything they do is driven by the credentials in the body: they are used
 * from the 2FA step of the ordinary sign in screen and, unchanged, from the
 * OAuth sign in screen (R4.1). Confirming a code turns 2FA off, after which the
 * browser replays the ordinary sign in with no code at all.
 */
@Controller
@RequestMapping(value = { "/signin/recoveryPhone" })
public class RecoveryPhoneSigninController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryPhoneSigninController.class);

    /**
     * Fixed length mask, so the mask does not leak how long the number is. The
     * 2FA controller masks the same way; the constant is repeated rather than
     * shared so neither controller has to widen its own internals.
     */
    private static final String RECOVERY_PHONE_MASK = "***********";

    static final String FEATURE_DISABLED = "FEATURE_DISABLED";

    static final String BAD_CREDENTIALS = "BAD_CREDENTIALS";

    /** Wire value kept in step with the signed in recovery phone endpoints. */
    static final String TWO_FACTOR_DISABLED = "2FA_DISABLED";

    static final String NO_RECOVERY_PHONE = "NO_RECOVERY_PHONE";

    /**
     * The provider bean, deliberately not the "authenticationManager"
     * ProviderManager: Spring Security wires that manager with an event
     * publisher, so a return that is not an exception publishes an
     * authentication success event. On an account whose 2FA is off that would
     * put a sign in that never happened in the audit trail, and run every
     * listener on that event, for a caller who only posted a password to a
     * recovery endpoint. The provider does the identical lockout accounting
     * R3.2 depends on and publishes nothing, so do not "simplify" this back to
     * the manager.
     */
    @Resource(name = "authenticationProvider")
    private AuthenticationProvider authenticationProvider;

    @Resource
    private RecoveryPhoneManager recoveryPhoneManager;

    @Resource
    private RecoveryPhoneVerificationService recoveryPhoneVerificationService;

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private RecordEmailSender recordEmailSender;

    /**
     * Sends a fresh code to the recovery number stored on the account the given
     * credentials belong to (R3.2).
     */
    @RequestMapping(value = "/sendCode.json", method = RequestMethod.POST)
    public @ResponseBody RecoveryPhoneSigninSendCodeResponse sendCode(HttpServletRequest request,
            @RequestBody RecoveryPhoneSigninSendCodeRequest form) {
        // Checked before anything else so the endpoint is inert, rather than a
        // credential check, while the feature is off
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
            return RecoveryPhoneSigninSendCodeResponse.failure(FEATURE_DISABLED);
        }

        String credentialsFailure = verifyCredentials(request, form.getUsername(), form.getPassword());
        if (credentialsFailure != null) {
            return RecoveryPhoneSigninSendCodeResponse.failure(credentialsFailure);
        }

        String orcid = resolveOrcid(form.getUsername());
        if (orcid == null) {
            return RecoveryPhoneSigninSendCodeResponse.failure(BAD_CREDENTIALS);
        }

        RecoveryPhone recoveryPhone = recoveryPhoneManager.getRecoveryPhone(orcid);
        if (recoveryPhone == null) {
            // Say so rather than leaving the user waiting for a text (R3.4)
            return RecoveryPhoneSigninSendCodeResponse.failure(NO_RECOVERY_PHONE);
        }

        // The number comes from the record, never from the request, so nobody
        // can point the text at a phone of their own, and this is the only
        // point in the flow that needs it in the clear
        String phoneNumber = recoveryPhoneManager.getDecryptedPhoneNumber(orcid);
        if (phoneNumber == null) {
            // A stored recovery phone always carries a number, so this is only
            // reachable if the number went away between the two reads
            return RecoveryPhoneSigninSendCodeResponse.failure(NO_RECOVERY_PHONE);
        }

        RecoveryPhoneSendCodeRequest sendCodeRequest = new RecoveryPhoneSendCodeRequest();
        sendCodeRequest.setPhoneNumber(phoneNumber);
        sendCodeRequest.setLocale(requestLocale());

        RecoveryPhoneSendCodeResponse sendCodeResponse = recoveryPhoneVerificationService.sendCode(orcid, sendCodeRequest);

        RecoveryPhoneSigninSendCodeResponse response = new RecoveryPhoneSigninSendCodeResponse();
        response.setSuccess(sendCodeResponse.isSuccess());
        response.setErrorCode(sendCodeResponse.getErrorCode());
        response.setResendAfterSeconds(sendCodeResponse.getResendAfterSeconds());
        // The mask goes back on a refused resend too, so the screen can keep
        // naming the number the user is waiting on
        response.setMaskedRecoveryPhoneNumber(RECOVERY_PHONE_MASK + recoveryPhone.getLastFour());
        return response;
    }

    /**
     * Confirms the code that was texted to the recovery number and, on success,
     * turns 2FA off so the ordinary sign in can proceed on the password alone
     * (R3.5).
     */
    @RequestMapping(value = "/verify.json", method = RequestMethod.POST)
    public @ResponseBody RecoveryPhoneSigninVerifyResponse verify(HttpServletRequest request,
            @RequestBody RecoveryPhoneSigninVerifyRequest form) {
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
            return RecoveryPhoneSigninVerifyResponse.failure(FEATURE_DISABLED);
        }

        String credentialsFailure = verifyCredentials(request, form.getUsername(), form.getPassword());
        if (credentialsFailure != null) {
            return RecoveryPhoneSigninVerifyResponse.failure(credentialsFailure);
        }

        String orcid = resolveOrcid(form.getUsername());
        if (orcid == null) {
            return RecoveryPhoneSigninVerifyResponse.failure(BAD_CREDENTIALS);
        }

        // Answered from the read that costs no decryption, as in sendCode
        if (recoveryPhoneManager.getRecoveryPhone(orcid) == null) {
            return RecoveryPhoneSigninVerifyResponse.failure(NO_RECOVERY_PHONE);
        }

        String phoneNumber = recoveryPhoneManager.getDecryptedPhoneNumber(orcid);
        if (phoneNumber == null) {
            return RecoveryPhoneSigninVerifyResponse.failure(NO_RECOVERY_PHONE);
        }

        String verificationFailure = recoveryPhoneVerificationService.verifyCode(orcid, phoneNumber, form.getVerificationCode());
        if (verificationFailure != null) {
            // Nothing about the account changes until the code is right
            return RecoveryPhoneSigninVerifyResponse.failure(verificationFailure);
        }

        twoFactorAuthenticationManager.disable2FAByRecoveryPhone(orcid);
        // Load bearing, and before anything that can throw: the browser
        // re-submits the ordinary sign in straight away, and a cached profile
        // still carrying using2FA=true would demand a 2FA code that no longer
        // exists
        profileEntityCacheManager.remove(orcid);
        try {
            recordEmailSender.send2FADisabledEmail(orcid);
        } catch (RuntimeException e) {
            // The notification must not be able to fail the operation it is
            // reporting: 2FA is already off, the number and the backup codes are
            // already gone, and telling the caller it failed would only send
            // them back through a recovery they no longer need
            LOG.error("Unable to send the 2FA disabled email for: " + orcid, e);
        }
        LOG.info("2FA disabled through the recovery phone number for: " + orcid);

        return RecoveryPhoneSigninVerifyResponse.success(orcid);
    }

    /**
     * Proves the password without establishing a session.
     *
     * The check deliberately goes through the real authentication provider
     * rather than comparing the hash here: a wrong password on these endpoints
     * then counts toward the same sign in lockout as a wrong password on the
     * sign in form (R3.2), instead of handing out an unthrottled password
     * oracle beside a throttled one. The security context is never touched, no
     * success handler runs and no authentication event is published, so nothing
     * here logs anyone in or says that anyone did.
     *
     * @return null when the password is right and the account is using 2FA,
     *         otherwise the error code to report
     */
    private String verifyCredentials(HttpServletRequest request, String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return BAD_CREDENTIALS;
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        HttpSession session = request.getSession(false);
        // No 2FA codes: the whole point is that the user has none to give
        token.setDetails(new MFAWebAuthenticationDetails(request.getRemoteAddr(), session != null ? session.getId() : null, null, null));

        try {
            Authentication result = authenticationProvider.authenticate(token);
            if (result != null && result.isAuthenticated()) {
                // The password is right but there is no 2FA to recover from
                return TWO_FACTOR_DISABLED;
            }
            return BAD_CREDENTIALS;
        } catch (VerificationCodeFor2FARequiredException e) {
            // The password was accepted and the account is using 2FA: the one
            // state these endpoints exist to serve
            return null;
        } catch (AuthenticationException e) {
            // Covers a wrong password, a locked account and the bad 2FA code
            // exceptions, none of which we distinguish for an anonymous caller
            return BAD_CREDENTIALS;
        }
    }

    private String resolveOrcid(String username) {
        if (OrcidStringUtils.isValidOrcid(username)) {
            return username;
        }
        try {
            return emailManagerReadOnly.findOrcidIdByEmail(username);
        } catch (NoResultException e) {
            // The password was just accepted for this username, so there should
            // be a record behind it; report it as a credential failure rather
            // than failing the request
            LOG.warn("No record found for a username that just authenticated");
            return null;
        }
    }

    private String requestLocale() {
        Locale locale = getLocale();
        return locale == null ? null : locale.toLanguageTag();
    }

}
