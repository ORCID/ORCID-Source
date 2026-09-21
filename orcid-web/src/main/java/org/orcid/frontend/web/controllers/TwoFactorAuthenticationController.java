package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RecoveryPhone;
import org.orcid.core.manager.RecoveryPhoneManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.recoveryphone.RecoveryPhoneChallengeSendCodeResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneChallengeVerifyRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSaveRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSaveResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSendCodeRequest;
import org.orcid.frontend.recoveryphone.RecoveryPhoneSendCodeResponse;
import org.orcid.frontend.recoveryphone.RecoveryPhoneVerificationService;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.glxn.qrgen.QRCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = { "/2FA" })
public class TwoFactorAuthenticationController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(TwoFactorAuthenticationController.class);

    private static final String RECOVERY_PHONE_ELEVATION_ATTRIBUTE = "RECOVERY_PHONE_ELEVATION_TS";

    /** How long a passed authentication challenge lets the user keep working. */
    private static final long RECOVERY_PHONE_ELEVATION_TTL_MILLIS = 15 * 60 * 1000L;

    /** Fixed length mask, so the mask does not leak how long the number is. */
    private static final String RECOVERY_PHONE_MASK = "***********";

    /** Ignore the sub second gap between the insert and its last_modified. */
    // A row's two timestamps are stamped by the same lifecycle callback when it is created, so they
    // differ by at most a few milliseconds until the number is actually changed. A minute here would
    // read a number corrected right after it was added as never modified, which the panel then dates
    // from the wrong event.
    private static final long RECOVERY_PHONE_MODIFIED_THRESHOLD_MILLIS = 1000L;

    static final String FEATURE_DISABLED = "FEATURE_DISABLED";

    static final String TWO_FACTOR_DISABLED = "2FA_DISABLED";

    static final String CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED";

    /** The account has no recovery phone number to send a code to. */
    static final String NO_RECOVERY_PHONE = "NO_RECOVERY_PHONE";

    /** The password given with a challenge is not the account's password. */
    static final String INVALID_PASSWORD = "INVALID_PASSWORD";

    /**
     * Where the recovery phone form is being shown. The context decides which
     * proof of identity the request is accepted on; it never widens what the
     * request may do, and every context is still ROLE_USER on the signed in
     * record.
     */
    static final String CONTEXT_SETTINGS = "SETTINGS";

    static final String CONTEXT_ONBOARDING = "ONBOARDING";

    static final String CONTEXT_INTERSTITIAL = "INTERSTITIAL";

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Resource
    private RecoveryPhoneManager recoveryPhoneManager;

    @Resource
    private RecoveryPhoneVerificationService recoveryPhoneVerificationService;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private BackupCodeManager backupCodeManager;
    
    @Resource 
    private RecordEmailSender recordEmailSender;

    @Resource
    private EncryptionManager encryptionManager;

    @RequestMapping("/status.json")
    public @ResponseBody TwoFactorAuthStatus get2FAStatus() {
        TwoFactorAuthStatus status = new TwoFactorAuthStatus();
        String orcid = getCurrentUserOrcid();
        status.setEnabled(twoFactorAuthenticationManager.userUsing2FA(orcid));
        if (status.isEnabled()) {
            java.util.Date creationDate = backupCodeManager.getBackupCodesCreationDate(orcid);
            if (creationDate != null) {
                status.setTwoFactorCreationDate(org.orcid.pojo.ajaxForm.Date.valueOf(creationDate));
                status.setRecoveryCodeCreationDate(org.orcid.pojo.ajaxForm.Date.valueOf(creationDate));
            }
            if (Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
                applyRecoveryPhoneState(orcid, status);
            }
        }
        return status;
    }

    /**
     * Verifies the user before they add or change their recovery phone number.
     *
     * The number has to be confirmed by text, which takes longer than a TOTP
     * code stays valid, so a passed challenge elevates the session for a short
     * window instead of being replayed on the final save.
     */
    @RequestMapping(value = "/recoveryPhone/verifyAuthChallenge.json", method = RequestMethod.POST)
    public @ResponseBody AuthChallenge verifyRecoveryPhoneAuthChallenge(HttpServletRequest request, @RequestBody AuthChallenge form) {
        String orcid = getCurrentUserOrcid();
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive() || !twoFactorAuthenticationManager.userUsing2FA(orcid)) {
            form.setSuccess(false);
            return form;
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (form.getPassword() == null || !encryptionManager.hashMatches(form.getPassword(), profile.getEncryptedPassword())) {
            form.setInvalidPassword(true);
            return form;
        }
        if (!twoFactorAuthenticationManager.validateTwoFactorAuthForm(orcid, form)) {
            return form;
        }

        request.getSession().setAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE, System.currentTimeMillis());
        form.setSuccess(true);
        return form;
    }

    @RequestMapping(value = "/recoveryPhone/sendCode.json", method = RequestMethod.POST)
    public @ResponseBody RecoveryPhoneSendCodeResponse sendRecoveryPhoneCode(HttpServletRequest request,
            @RequestBody RecoveryPhoneSendCodeRequest form) {
        String orcid = getCurrentUserOrcid();
        String guardFailure = guardRecoveryPhoneRequest(request, orcid, form.getContext());
        if (guardFailure != null) {
            return RecoveryPhoneSendCodeResponse.failure(guardFailure);
        }
        return recoveryPhoneVerificationService.sendCode(orcid, form);
    }

    @RequestMapping(value = "/recoveryPhone/save.json", method = RequestMethod.POST)
    public @ResponseBody RecoveryPhoneSaveResponse saveRecoveryPhone(HttpServletRequest request, @RequestBody RecoveryPhoneSaveRequest form) {
        String orcid = getCurrentUserOrcid();
        String guardFailure = guardRecoveryPhoneRequest(request, orcid, form.getContext());
        if (guardFailure != null) {
            return RecoveryPhoneSaveResponse.failure(guardFailure);
        }

        String verificationFailure = recoveryPhoneVerificationService.verifyCode(orcid, form.getPhoneNumber(), form.getVerificationCode());
        if (verificationFailure != null) {
            return RecoveryPhoneSaveResponse.failure(verificationFailure);
        }

        String phoneE164 = recoveryPhoneVerificationService.normalize(form.getPhoneNumber());
        RecoveryPhone saved = recoveryPhoneManager.saveRecoveryPhone(orcid, phoneE164);
        request.getSession().removeAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE);

        // The answer is built from the row the save wrote, not read back: a read goes to
        // the read-only pool, and on a deployed environment that is a replica which can
        // still be answering with the previous number, or with none, for a moment after
        // the primary has committed
        RecoveryPhoneSaveResponse response = new RecoveryPhoneSaveResponse();
        response.setSuccess(true);
        TwoFactorAuthStatus status = new TwoFactorAuthStatus();
        applyRecoveryPhoneState(saved, status);
        response.setMaskedRecoveryPhoneNumber(status.getMaskedRecoveryPhoneNumber());
        response.setRecoveryPhoneCreationDate(status.getRecoveryPhoneCreationDate());
        response.setRecoveryPhoneLastModifiedDate(status.getRecoveryPhoneLastModifiedDate());
        response.setRecoveryPhoneModified(status.isRecoveryPhoneModified());
        return response;
    }

    /**
     * Sends a code to the number already stored on the record, as the first
     * half of passing an authentication challenge with the recovery phone.
     *
     * This endpoint deliberately does not ask for the session elevation the
     * other recovery phone endpoints need: it is the challenge. Anyone who
     * could pass the ordinary challenge would have no reason to be here, since
     * they reach this route precisely because they have lost their
     * authentication app and their recovery codes (R5.1). What it costs an
     * attacker is a text sent to a number they do not hold.
     */
    @RequestMapping(value = "/recoveryPhone/challenge/sendCode.json", method = RequestMethod.POST)
    public @ResponseBody RecoveryPhoneChallengeSendCodeResponse sendRecoveryPhoneChallengeCode() {
        String orcid = getCurrentUserOrcid();
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
            return RecoveryPhoneChallengeSendCodeResponse.failure(FEATURE_DISABLED);
        }
        if (!twoFactorAuthenticationManager.userUsing2FA(orcid)) {
            return RecoveryPhoneChallengeSendCodeResponse.failure(TWO_FACTOR_DISABLED);
        }
        RecoveryPhone recoveryPhone = recoveryPhoneManager.getRecoveryPhone(orcid);
        if (recoveryPhone == null) {
            // Tell the user there is no number rather than leave them waiting
            // for a text that is never sent (R3.4)
            return RecoveryPhoneChallengeSendCodeResponse.failure(NO_RECOVERY_PHONE);
        }

        // The user never types a number here: the code goes to the stored one,
        // and only the mask comes back out. The number is asked for separately,
        // since RecoveryPhone carries the mask and the dates only
        String phoneNumber = recoveryPhoneManager.getDecryptedPhoneNumber(orcid);
        if (phoneNumber == null) {
            // A stored recovery phone always carries a number, so this is only
            // reachable if the number went away between the two reads
            return RecoveryPhoneChallengeSendCodeResponse.failure(NO_RECOVERY_PHONE);
        }

        RecoveryPhoneSendCodeRequest sendCodeRequest = new RecoveryPhoneSendCodeRequest();
        sendCodeRequest.setPhoneNumber(phoneNumber);
        Locale locale = getLocale();
        if (locale != null) {
            sendCodeRequest.setLocale(locale.toString());
        }
        RecoveryPhoneSendCodeResponse sendCodeResponse = recoveryPhoneVerificationService.sendCode(orcid, sendCodeRequest);
        return RecoveryPhoneChallengeSendCodeResponse.from(sendCodeResponse, RECOVERY_PHONE_MASK + recoveryPhone.getLastFour());
    }

    /**
     * Passes an authentication challenge with the recovery phone number, which
     * costs the user their 2FA: the number is a one time way back in, and
     * using it disables 2FA and resets every 2FA backup option (R5.3).
     *
     * It answers with {@link AuthChallenge}, as
     * {@link #verifyRecoveryPhoneAuthChallenge} does, so the frontend's
     * challenge component keeps one response shape across both ways of
     * passing the same challenge.
     */
    @RequestMapping(value = "/recoveryPhone/challenge/verify.json", method = RequestMethod.POST)
    public @ResponseBody AuthChallenge verifyRecoveryPhoneChallengeCode(HttpServletRequest request, @RequestBody RecoveryPhoneChallengeVerifyRequest form) {
        String orcid = getCurrentUserOrcid();
        AuthChallenge result = new AuthChallenge();
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
            result.setSuccess(false);
            result.getErrors().add(FEATURE_DISABLED);
            return result;
        }
        if (!twoFactorAuthenticationManager.userUsing2FA(orcid)) {
            result.setSuccess(false);
            result.getErrors().add(TWO_FACTOR_DISABLED);
            return result;
        }

        // The password is checked before the code, so someone without the
        // password can neither spend the code's attempts nor learn anything
        // about it
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (form.getPassword() == null || !encryptionManager.hashMatches(form.getPassword(), profile.getEncryptedPassword())) {
            result.setInvalidPassword(true);
            result.getErrors().add(INVALID_PASSWORD);
            return result;
        }

        // Nothing here wants the mask, so the number comes from the one path
        // that decrypts it; a record with no number stored answers null
        String phoneE164 = recoveryPhoneManager.getDecryptedPhoneNumber(orcid);
        if (phoneE164 == null) {
            result.setSuccess(false);
            result.getErrors().add(NO_RECOVERY_PHONE);
            return result;
        }

        String verificationFailure = recoveryPhoneVerificationService.verifyCode(orcid, phoneE164, form.getVerificationCode());
        if (verificationFailure != null) {
            // Nothing is disabled unless the code was right
            result.setSuccess(false);
            result.getErrors().add(verificationFailure);
            return result;
        }

        twoFactorAuthenticationManager.disable2FAByRecoveryPhone(orcid);
        // The cache is evicted as soon as the record changes and before
        // anything that can fail, so nothing downstream keeps answering that
        // 2FA is on after it has been turned off
        profileEntityCacheManager.remove(orcid);
        try {
            recordEmailSender.send2FADisabledEmail(orcid);
        } catch (RuntimeException e) {
            // The notification must not be able to fail the operation it is
            // reporting: 2FA is already off, the number and the backup codes
            // are already gone, and reporting a failed challenge would send the
            // user back through a recovery they no longer need
            LOG.error("Unable to send the 2FA disabled email for: " + orcid, e);
        }
        // 2FA is off, so the action this challenge was guarding proceeds on the
        // password alone; an elevation granted earlier has nothing left to guard
        request.getSession().removeAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE);
        result.setSuccess(true);
        return result;
    }

    /**
     * @param context
     *            where the form is being shown, which decides what counts as
     *            recent proof of identity
     * @return the error code to report, or null when the request may proceed
     */
    private String guardRecoveryPhoneRequest(HttpServletRequest request, String orcid, String context) {
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
            return FEATURE_DISABLED;
        }
        if (!twoFactorAuthenticationManager.userUsing2FA(orcid)) {
            return TWO_FACTOR_DISABLED;
        }
        if (sessionIsElevated(request) || interstitialIsElevatedByRecentLogin(orcid, context)) {
            return null;
        }
        return CHALLENGE_REQUIRED;
    }

    /** A challenge passed on this session within the elevation window. */
    private boolean sessionIsElevated(HttpServletRequest request) {
        Object elevatedAt = request.getSession().getAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE);
        if (!(elevatedAt instanceof Long)) {
            return false;
        }
        return System.currentTimeMillis() - (Long) elevatedAt <= RECOVERY_PHONE_ELEVATION_TTL_MILLIS;
    }

    /**
     * The add-a-number interstitial is let in on a recent sign in rather than
     * on a challenge of its own. The user completed 2FA seconds earlier to
     * reach it, so a fresh login is the same proof a challenge would collect,
     * and an interstitial has nowhere to put a password challenge: it is a
     * dialog the user cannot dismiss, sitting between them and their record.
     * The window is the same 15 minutes, so a session left open on the
     * interstitial goes cold exactly as an elevated session does (R6.3).
     *
     * The context is posted in the request body, so it is a claim about where
     * the form is being shown and never a mode the client may switch on: the
     * conditions the interstitial is actually shown under are checked again
     * here, server side. With all of them, the most a stolen session can do
     * without the password is add a first recovery number, and even that needs
     * the code texted to that number before anything is saved. What it does
     * not close is a stolen session used inside the same fifteen minutes as
     * the real user's sign in, which is the trade R6.3 makes deliberately.
     */
    private boolean interstitialIsElevatedByRecentLogin(String orcid, String context) {
        if (!CONTEXT_INTERSTITIAL.equals(resolveContext(context))) {
            return false;
        }
        if (!Features.LOGIN_RECOVERY_PHONE_INTERSTITIAL.isActive()) {
            // The relaxed path dies with the interstitial that justifies it (R7.4)
            return false;
        }
        if (recoveryPhoneManager.getRecoveryPhone(orcid) != null) {
            // The interstitial is only ever offered when no number is stored,
            // so it can add a first one and never replace one. A replacement
            // without a challenge would let a hijacked session point the
            // recovery number at a phone it holds, and that number then turns
            // 2FA off at the next sign in (R6.1)
            return false;
        }
        if (!orcid.equals(getRealUserOrcid())) {
            // A delegate or an admin switched into the record is not the
            // account owner, and their own sign in is no proof of this one (R6.1)
            return false;
        }
        // last_login is read from the database, not from the profile cache: the
        // cached entity is loaded while the user is being authenticated, before
        // the success handler writes last_login, so it still carries the
        // previous sign in. The database is also where the authorization server
        // puts it when it is the one serving the sign in
        java.util.Date lastLogin = profileEntityManager.getLastLogin(orcid);
        if (lastLogin == null) {
            // Nothing to date the sign in by, so this is not proof of anything
            return false;
        }
        return System.currentTimeMillis() - lastLogin.getTime() <= RECOVERY_PHONE_ELEVATION_TTL_MILLIS;
    }

    /** SETTINGS is what an unstated context means: the strictest of the three. */
    private static String resolveContext(String context) {
        return StringUtils.isBlank(context) ? CONTEXT_SETTINGS : context.trim();
    }

    private void applyRecoveryPhoneState(String orcid, TwoFactorAuthStatus status) {
        applyRecoveryPhoneState(recoveryPhoneManager.getRecoveryPhone(orcid), status);
    }

    private void applyRecoveryPhoneState(RecoveryPhone recoveryPhone, TwoFactorAuthStatus status) {
        if (recoveryPhone == null) {
            return;
        }
        status.setMaskedRecoveryPhoneNumber(RECOVERY_PHONE_MASK + recoveryPhone.getLastFour());
        if (recoveryPhone.getDateCreated() != null) {
            status.setRecoveryPhoneCreationDate(org.orcid.pojo.ajaxForm.Date.valueOf(recoveryPhone.getDateCreated()));
        }
        if (recoveryPhone.getLastModified() != null) {
            status.setRecoveryPhoneLastModifiedDate(org.orcid.pojo.ajaxForm.Date.valueOf(recoveryPhone.getLastModified()));
        }
        // The dates we hand out only carry a day, so whether the number has ever
        // been changed is decided here from the full timestamps
        if (recoveryPhone.getDateCreated() != null && recoveryPhone.getLastModified() != null) {
            long deltaMillis = recoveryPhone.getLastModified().getTime() - recoveryPhone.getDateCreated().getTime();
            status.setRecoveryPhoneModified(deltaMillis > RECOVERY_PHONE_MODIFIED_THRESHOLD_MILLIS);
        }
    }

    @RequestMapping("/setup")
    public ModelAndView get2FASetupPage() {
        TwoFactorAuthStatus status = get2FAStatus();
        if (status.isEnabled()) {
            LOG.warn("2FA setup page requested for user who is already using 2FA");
            return new ModelAndView("redirect:" + calculateRedirectUrl("/account"));
        }
        return new ModelAndView("2FA_setup");
    }

    @RequestMapping(value = "/disable.json", method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthStatus disable2FA(HttpServletRequest request, @RequestBody TwoFactorAuthStatus form) {
        String orcid = getCurrentUserOrcid();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());

        if (form.getPassword() == null || !encryptionManager.hashMatches(form.getPassword(), profile.getEncryptedPassword())) {
            form.setInvalidPassword(true);
            return form;
        }
        if (!twoFactorAuthenticationManager.validateTwoFactorAuthForm(getCurrentUserOrcid(), form)) {
            return form;
        }

        twoFactorAuthenticationManager.disable2FA(orcid);
        recordEmailSender.send2FADisabledEmail(orcid);
        form.setSuccess(true);
        return form;
    }

    @RequestMapping("/QRCode.json")
    public @ResponseBody TwoFactorAuthQRCodeUrl get2FAQRCode() {
        TwoFactorAuthQRCodeUrl code = new TwoFactorAuthQRCodeUrl();
        code.setUrl(twoFactorAuthenticationManager.getQRCode(getCurrentUserOrcid()));
        return code;
    }
    
    @RequestMapping(value = "/qr-code.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] generateQrCode(HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        return QRCode.from(twoFactorAuthenticationManager.getQRCode(getCurrentUserOrcid())).withSize(250, 250).stream().toByteArray();
    }

    @RequestMapping("/register.json")
    public @ResponseBody TwoFactorAuthRegistration getVerificationCode() {
        return new TwoFactorAuthRegistration();
    }
    
    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthRegistration validateVerificationCode(HttpServletRequest request, @RequestBody TwoFactorAuthRegistration registration) {
        String orcid = getCurrentUserOrcid();
        boolean valid = twoFactorAuthenticationManager.verificationCodeIsValid(registration.getVerificationCode(), orcid);
        registration.setValid(valid);
        if (valid) {
            // Read before enable2FA, which is what makes this true for everyone
            boolean wasAlreadyUsing2FA = twoFactorAuthenticationManager.userUsing2FA(orcid);
            List<String> backupCodes = twoFactorAuthenticationManager.enable2FA(orcid);
            registration.setBackupCodes(backupCodes);
            //send email notification
            recordEmailSender.send2FAEnabledEmail(orcid);
            if (!wasAlreadyUsing2FA) {
                // Step 1 of 2FA setup elevates the session for the recovery phone
                // step that follows it: the user has just typed a live time based
                // code, and asking for their password one screen later is the
                // wrong trade (R2.6). Only setup gets that: on an account that
                // already has 2FA on there is no step 2 to carry, and letting a
                // time based code elevate there would put it in the place of the
                // password challenge that guards changing a recovery number
                request.getSession().setAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE, System.currentTimeMillis());
            }
        }
        return registration;
    }
    
    @RequestMapping("/secret.json")
    public @ResponseBody TwoFactorAuthSecret getTwoFactorAuthSecret() {
        TwoFactorAuthSecret secret = new TwoFactorAuthSecret();
        secret.setSecret(twoFactorAuthenticationManager.getSecret(getCurrentUserOrcid()));
        return secret;
    }

    @RequestMapping(value = { "/authenticationCode.json" }, method = RequestMethod.GET)
    public @ResponseBody
    TwoFactorAuthenticationCodes getTwoFactorCodeWrapper() {
        return new TwoFactorAuthenticationCodes();
    }

    @RequestMapping(value = { "/submitCode.json" }, method = RequestMethod.POST)
    public @ResponseBody
    TwoFactorAuthenticationCodes post2FAVerificationCode(@RequestBody TwoFactorAuthenticationCodes codes, HttpServletRequest request,
                                                         HttpServletResponse response) {
        String orcid = codes.getOrcid();
        validate2FACodes(orcid, codes);
        if (!codes.getErrors().isEmpty()) {
            return codes;
        }
        codes.setRedirectUrl(calculateRedirectUrl(request, response, false));

        return codes;
    }

    private void validate2FACodes(String orcid, TwoFactorAuthenticationCodes codes) {
        codes.setErrors(new ArrayList<>());
        if (codes.getRecoveryCode() != null && !codes.getRecoveryCode().isEmpty()) {
            if (!backupCodeManager.verify(orcid, codes.getRecoveryCode())) {
                codes.getErrors().add(getMessage("2FA.recoveryCode.invalid"));
            }
            return;
        }

        if (codes.getVerificationCode() == null || codes.getVerificationCode().isEmpty()
                || !twoFactorAuthenticationManager.verificationCodeIsValid(codes.getVerificationCode(), orcid)) {
            codes.getErrors().add(getMessage("2FA.verificationCode.invalid"));
        }
    }
}
