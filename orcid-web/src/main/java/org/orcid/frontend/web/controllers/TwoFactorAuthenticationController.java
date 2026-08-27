package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RecoveryPhone;
import org.orcid.core.manager.RecoveryPhoneManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.email.RecordEmailSender;
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
    private static final long RECOVERY_PHONE_MODIFIED_THRESHOLD_MILLIS = 60 * 1000L;

    static final String FEATURE_DISABLED = "FEATURE_DISABLED";

    static final String TWO_FACTOR_DISABLED = "2FA_DISABLED";

    static final String CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED";

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
        String guardFailure = guardRecoveryPhoneRequest(request, orcid);
        if (guardFailure != null) {
            return RecoveryPhoneSendCodeResponse.failure(guardFailure);
        }
        return recoveryPhoneVerificationService.sendCode(orcid, form);
    }

    @RequestMapping(value = "/recoveryPhone/save.json", method = RequestMethod.POST)
    public @ResponseBody RecoveryPhoneSaveResponse saveRecoveryPhone(HttpServletRequest request, @RequestBody RecoveryPhoneSaveRequest form) {
        String orcid = getCurrentUserOrcid();
        String guardFailure = guardRecoveryPhoneRequest(request, orcid);
        if (guardFailure != null) {
            return RecoveryPhoneSaveResponse.failure(guardFailure);
        }

        String verificationFailure = recoveryPhoneVerificationService.verifyCode(orcid, form.getPhoneNumber(), form.getVerificationCode());
        if (verificationFailure != null) {
            return RecoveryPhoneSaveResponse.failure(verificationFailure);
        }

        String phoneE164 = recoveryPhoneVerificationService.normalize(form.getPhoneNumber());
        recoveryPhoneManager.saveRecoveryPhone(orcid, phoneE164);
        request.getSession().removeAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE);

        RecoveryPhoneSaveResponse response = new RecoveryPhoneSaveResponse();
        response.setSuccess(true);
        TwoFactorAuthStatus status = new TwoFactorAuthStatus();
        applyRecoveryPhoneState(orcid, status);
        response.setMaskedRecoveryPhoneNumber(status.getMaskedRecoveryPhoneNumber());
        response.setRecoveryPhoneCreationDate(status.getRecoveryPhoneCreationDate());
        response.setRecoveryPhoneLastModifiedDate(status.getRecoveryPhoneLastModifiedDate());
        response.setRecoveryPhoneModified(status.isRecoveryPhoneModified());
        return response;
    }

    /**
     * @return the error code to report, or null when the request may proceed
     */
    private String guardRecoveryPhoneRequest(HttpServletRequest request, String orcid) {
        if (!Features.TWO_FACTOR_RECOVERY_PHONE.isActive()) {
            return FEATURE_DISABLED;
        }
        if (!twoFactorAuthenticationManager.userUsing2FA(orcid)) {
            return TWO_FACTOR_DISABLED;
        }
        Object elevatedAt = request.getSession().getAttribute(RECOVERY_PHONE_ELEVATION_ATTRIBUTE);
        if (!(elevatedAt instanceof Long) || System.currentTimeMillis() - (Long) elevatedAt > RECOVERY_PHONE_ELEVATION_TTL_MILLIS) {
            return CHALLENGE_REQUIRED;
        }
        return null;
    }

    private void applyRecoveryPhoneState(String orcid, TwoFactorAuthStatus status) {
        RecoveryPhone recoveryPhone = recoveryPhoneManager.getRecoveryPhone(orcid);
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
    public @ResponseBody TwoFactorAuthRegistration validateVerificationCode(@RequestBody TwoFactorAuthRegistration registration) {
        String orcid = getCurrentUserOrcid();
        boolean valid = twoFactorAuthenticationManager.verificationCodeIsValid(registration.getVerificationCode(), orcid);
        registration.setValid(valid);
        if (valid) {
            List<String> backupCodes = twoFactorAuthenticationManager.enable2FA(orcid);
            registration.setBackupCodes(backupCodes);            
            //send email notification
            recordEmailSender.send2FAEnabledEmail(orcid);
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
