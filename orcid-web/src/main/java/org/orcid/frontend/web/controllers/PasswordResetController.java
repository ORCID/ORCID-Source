/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Reactivation;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetController.class);

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;

    @Autowired
    private SocialContext socialContext;

    @Resource
    private SocialAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerSocial;

    @Resource
    private ShibbolethAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerShibboleth;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private RegistrationController registrationController;

    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public ModelAndView resetPassword(@RequestParam(value = "expired", required = false) boolean expired) {
        ModelAndView mav = new ModelAndView("reset_password");
        mav.addObject("tokenExpired", expired);
        return mav;
    }

    @RequestMapping(value = "/reset-password.json", method = RequestMethod.GET)
    public @ResponseBody EmailRequest getPasswordResetRequest() {
        return new EmailRequest();
    }

    @RequestMapping(value = "/validate-reset-password.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest validateResetPasswordRequest(@RequestBody EmailRequest passwordResetRequest) {
        List<String> errors = new ArrayList<>();
        passwordResetRequest.setErrors(errors);
        if (!validateEmailAddress(passwordResetRequest.getEmail())) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
        }
        return passwordResetRequest;
    }

    @RequestMapping(value = "/reset-password.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest issuePasswordResetRequest(@RequestBody EmailRequest passwordResetRequest) {
        List<String> errors = new ArrayList<>();
        passwordResetRequest.setErrors(errors);
        if (!validateEmailAddress(passwordResetRequest.getEmail())) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
            return passwordResetRequest;
        }

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetRequest.getEmail(), LoadOptions.BIO_ONLY);
        if (profile == null) {
            errors.add(getMessage("orcid.frontend.reset.password.email_not_found", passwordResetRequest.getEmail()));
            return passwordResetRequest;
        }

        if (profile.isDeactivated()) {
            errors.add(getMessage("orcid.frontend.reset.password.disabled_account", passwordResetRequest.getEmail()));
            return passwordResetRequest;
        }

        registrationManager.resetUserPassword(passwordResetRequest.getEmail(), profile);
        passwordResetRequest.setSuccessMessage(getMessage("orcid.frontend.reset.password.successfulReset") + " " + passwordResetRequest.getEmail());
        return passwordResetRequest;
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView resetPasswordEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password?expired=true");
        }
        return new ModelAndView("password_one_time_reset_optional_security_questions");
    }

    @RequestMapping(value = "/reset-password-form-validate.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm resetPasswordConfirmValidate(@RequestBody OneTimeResetPasswordForm resetPasswordForm) {
        resetPasswordForm.setErrors(new ArrayList<String>());
        if (resetPasswordForm.getPassword() == null || !resetPasswordForm.getPassword().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(resetPasswordForm, "Pattern.registrationForm.password");
        }

        if (resetPasswordForm.getRetypedPassword() != null && !resetPasswordForm.getRetypedPassword().equals(resetPasswordForm.getPassword())) {
            setError(resetPasswordForm, "FieldMatch.registrationForm");
        }
        
        if (registrationManager.passwordIsCommon(resetPasswordForm.getPassword())) {
            setError(resetPasswordForm, "password.too_common", resetPasswordForm.getPassword());
        }
        return resetPasswordForm;
    }

    @RequestMapping(value = "/password-reset.json", method = RequestMethod.GET)
    public @ResponseBody OneTimeResetPasswordForm getResetPassword() {
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setSecurityQuestionId(0);
        return form;
    }

    @RequestMapping(value = "/reset-password-email.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm submitPasswordReset(HttpServletRequest request, HttpServletResponse response,
            @RequestBody OneTimeResetPasswordForm oneTimeResetPasswordForm) {
        oneTimeResetPasswordForm.setErrors(new ArrayList<String>());
        
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(oneTimeResetPasswordForm.getEncryptedEmail());
        if (isTokenExpired(passwordResetToken)) {
            setError(oneTimeResetPasswordForm, "orcid.frontend.reset.password.resetLinkExpired");
            return oneTimeResetPasswordForm;
        }

        if (oneTimeResetPasswordForm.getPassword() == null || !oneTimeResetPasswordForm.getPassword().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(oneTimeResetPasswordForm, "Pattern.registrationForm.password");
            return oneTimeResetPasswordForm;
        }

        OrcidProfile profileToUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);
        profileToUpdate.setPassword(oneTimeResetPasswordForm.getPassword());
        if (oneTimeResetPasswordForm.isSecurityDetailsPopulated()) {
            profileToUpdate.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(oneTimeResetPasswordForm.getSecurityQuestionId()));
            profileToUpdate.setSecurityQuestionAnswer(oneTimeResetPasswordForm.getSecurityQuestionAnswer());
        }

        orcidProfileManager.updatePasswordInformation(profileToUpdate);
        String redirectUrl = calculateRedirectUrl(request, response);
        oneTimeResetPasswordForm.setSuccessRedirectLocation(redirectUrl);
        return oneTimeResetPasswordForm;
    }

    private PasswordResetToken buildResetTokenFromEncryptedLink(String encryptedLink) {
        try {
            String paramsString = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedLink), "UTF-8"));
            return new PasswordResetToken(paramsString);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Could not decrypt " + encryptedLink);
            throw new RuntimeException(getMessage("web.orcid.decrypt_passwordreset.exception"));
        }
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        Date expiryDateOfOneHourFromIssueDate = org.apache.commons.lang.time.DateUtils.addHours(passwordResetToken.getIssueDate(), 4);
        Date now = new Date();
        return (expiryDateOfOneHourFromIssueDate.getTime() < now.getTime());
    }

    @RequestMapping(value = "/sendReactivation.json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void sendReactivation(@RequestParam("email") String email) {
        OrcidProfile orcidProfile = orcidProfileCacheManager.retrieve(emailManager.findOrcidIdByEmail(email));
        notificationManager.sendReactivationEmail(email, orcidProfile);
    }

    @RequestMapping(value = "/reactivation/{resetParams}", method = RequestMethod.GET)
    public ModelAndView reactivation(HttpServletRequest request, @PathVariable("resetParams") String resetParams, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(resetParams);
        ModelAndView mav = new ModelAndView("reactivation");
        if (isTokenExpired(passwordResetToken)) {
            mav.addObject("reactivationLinkExpired", true);
        }
        mav.addObject("resetParams", resetParams);
        return mav;
    }

    @RequestMapping(value = { "/reactivationConfirm.json", "/shibboleth/reactivationConfirm.json" }, method = RequestMethod.POST)
    public @ResponseBody Object setReactivationConfirm(HttpServletRequest request, HttpServletResponse response, @RequestBody Reactivation reg)
            throws UnsupportedEncodingException {
        Redirect r = new Redirect();

        // Strip any html code from names before validating them
        if (!PojoUtil.isEmpty(reg.getFamilyNames())) {
            reg.getFamilyNames().setValue(OrcidStringUtils.stripHtml(reg.getFamilyNames().getValue()));
        }

        if (!PojoUtil.isEmpty(reg.getGivenNames())) {
            reg.getGivenNames().setValue(OrcidStringUtils.stripHtml(reg.getGivenNames().getValue()));
        }

        // make sure validation still passes
        validateReactivationFields(request, reg);
        if (reg.getErrors() != null && reg.getErrors().size() > 0) {
            return reg;
        }

        if (reg.getValNumServer() == 0 || reg.getValNumClient() != reg.getValNumServer() / 2) {
            r.setUrl(getBaseUri() + "/register");
            return r;
        }

        reactivateAndLogUserIn(request, response, reg);
        if ("social".equals(reg.getLinkType()) && socialContext.isSignedIn(request, response) != null) {
            ajaxAuthenticationSuccessHandlerSocial.linkSocialAccount(request, response);
        } else if ("shibboleth".equals(reg.getLinkType())) {
            ajaxAuthenticationSuccessHandlerShibboleth.linkShibbolethAccount(request, response);
        }
        String redirectUrl = calculateRedirectUrl(request, response);
        r.setUrl(redirectUrl);
        return r;
    }

    public void validateReactivationFields(HttpServletRequest request, Registration reg) {
        reg.setErrors(new ArrayList<String>());

        givenNameValidate(reg.getGivenNames());
        passwordValidate(reg.getPasswordConfirm(), reg.getPassword());
        passwordConfirmValidate(reg.getPasswordConfirm(), reg.getPassword());
        termsOfUserValidate(reg.getTermsOfUse());

        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);
    }

    public void reactivateAndLogUserIn(HttpServletRequest request, HttpServletResponse response, Reactivation reactivation) {
        PasswordResetToken resetParams = buildResetTokenFromEncryptedLink(reactivation.getResetParams());
        String email = resetParams.getEmail();
        String orcid = emailManager.findOrcidIdByEmail(email);
        LOGGER.info("About to reactivate record, orcid={}, email={}", orcid, email);
        String password = reactivation.getPassword().getValue();
        // Reactivate user
        profileEntityManager.reactivate(orcid, reactivation.getGivenNames().getValue(), reactivation.getFamilyNames().getValue(), password,
                reactivation.getActivitiesVisibilityDefault().getVisibility());
        // Verify email used to reactivate
        emailManager.verifyEmail(email);
        registrationController.logUserIn(request, response, orcid, password);
    }
}
