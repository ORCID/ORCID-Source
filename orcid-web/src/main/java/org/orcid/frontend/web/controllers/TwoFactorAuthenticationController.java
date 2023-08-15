package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.frontend.email.RecordEmailSender;
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

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private BackupCodeManager backupCodeManager;
    
    @Resource 
    private RecordEmailSender recordEmailSender;

    @RequestMapping("/status.json")
    public @ResponseBody TwoFactorAuthStatus get2FAStatus() {
        TwoFactorAuthStatus status = new TwoFactorAuthStatus();
        status.setEnabled(twoFactorAuthenticationManager.userUsing2FA(getCurrentUserOrcid()));
        return status;
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
    public @ResponseBody TwoFactorAuthStatus disable2FA() {
        String orcid = getCurrentUserOrcid();
        twoFactorAuthenticationManager.disable2FA(orcid);
        recordEmailSender.send2FADisabledEmail(orcid);
        return get2FAStatus();
    }

    @RequestMapping("/QRCode.json")
    public @ResponseBody TwoFactorAuthQRCodeUrl get2FAQRCode() {
        TwoFactorAuthQRCodeUrl code = new TwoFactorAuthQRCodeUrl();
        code.setUrl(twoFactorAuthenticationManager.getQRCode(getCurrentUserOrcid()));
        return code;
    }
    
    @RequestMapping(value = "/qr-code.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generateQrCode() {
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
