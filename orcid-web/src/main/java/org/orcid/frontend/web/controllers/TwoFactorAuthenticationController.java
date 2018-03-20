package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.pojo.TwoFactorAuthQRCodeUrl;
import org.orcid.pojo.TwoFactorAuthStatus;
import org.orcid.pojo.TwoFactorAuthRegistration;
import org.orcid.pojo.TwoFactorAuthSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/2FA" })
public class TwoFactorAuthenticationController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(TwoFactorAuthenticationController.class);

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

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
            return new ModelAndView("redirect:/account");
        }
        return new ModelAndView("2FA_setup");
    }

    @RequestMapping(value = "/disable.json", method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthStatus disable2FA() {
        twoFactorAuthenticationManager.disable2FA(getCurrentUserOrcid());
        return get2FAStatus();
    }

    @RequestMapping("/QRCode.json")
    public @ResponseBody TwoFactorAuthQRCodeUrl get2FAQRCode() {
        TwoFactorAuthQRCodeUrl code = new TwoFactorAuthQRCodeUrl();
        code.setUrl(twoFactorAuthenticationManager.getQRCode(getCurrentUserOrcid()));
        return code;
    }

    @RequestMapping("/register.json")
    public @ResponseBody TwoFactorAuthRegistration getVerificationCode() {
        return new TwoFactorAuthRegistration();
    }
    
    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthRegistration validateVerificationCode(@RequestBody TwoFactorAuthRegistration registration) {
        boolean valid = twoFactorAuthenticationManager.verificationCodeIsValid(registration.getVerificationCode(), getCurrentUserOrcid());
        registration.setValid(valid);
        if (valid) {
            twoFactorAuthenticationManager.enable2FA(getCurrentUserOrcid());
            registration.setBackupCodes(twoFactorAuthenticationManager.getBackupCodes(getCurrentUserOrcid()));
        }
        return registration;
    }
    
    @RequestMapping("/secret.json")
    public @ResponseBody TwoFactorAuthSecret getTwoFactorAuthSecret() {
        TwoFactorAuthSecret secret = new TwoFactorAuthSecret();
        secret.setSecret(twoFactorAuthenticationManager.getSecret(getCurrentUserOrcid()));
        return secret;
    }
}
