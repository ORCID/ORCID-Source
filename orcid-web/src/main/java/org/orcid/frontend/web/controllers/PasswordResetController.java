package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Reactivation;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ModelAndView resetPassword() {
        ModelAndView mav = new ModelAndView("reset_password");
        EmailAddressForm resetPasswordForm = new EmailAddressForm();
        mav.addObject(resetPasswordForm);
        return mav;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView issuePasswordResetRequest(HttpServletRequest request, @ModelAttribute @Valid EmailAddressForm resetPasswordForm, BindingResult bindingResult) {
        String submittedEmail = resetPasswordForm.getUserEmailAddress();

        ModelAndView mav = new ModelAndView("reset_password");

        // if the email doesn't exist, or any other form errors.. don't bother
        // hitting db
        if (bindingResult.hasErrors()) {
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(submittedEmail, LoadOptions.BIO_ONLY);
        // if the email can't be found on the system, then add to errors
        if (profile == null) {

            String[] codes = { "orcid.frontend.reset.password.email_not_found" };
            String[] args = { submittedEmail };
            bindingResult.addError(new FieldError("userEmailAddress", "userEmailAddress", submittedEmail, false, codes, args, "Email not found"));
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        } else {
            if (profile.isDeactivated()) {
                mav.addObject("disabledAccount", true);
                return mav;
            } else {
                registrationManager.resetUserPassword(submittedEmail, profile);
                mav.addObject("passwordResetSuccessful", true);
                return mav;
            }
        }
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView resetPasswordEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setSecurityQuestionId(0);
        // otherwise, straight to the screen with the option to set a security
        // question
        return buildPasswordScreenWithOptionalSecurityQuestion(form);
    }

    @RequestMapping(value = "/reset-password-form-validate.json", method = RequestMethod.POST)
    public @ResponseBody PasswordTypeAndConfirmForm resetPasswordConfirmValidate(@RequestBody PasswordTypeAndConfirmForm resetPasswordForm) {
        resetPasswordValidateFields(resetPasswordForm.getPassword(), resetPasswordForm.getRetypedPassword());
        return resetPasswordForm;
    }

    private void resetPasswordValidateFields(Text password, Text retypedPassword) {
        if (password == null) {
            password = new Text();
        }

        if (retypedPassword == null) {
            retypedPassword = new Text();
        }
        password.setErrors(new ArrayList<String>());
        retypedPassword.setErrors(new ArrayList<String>());

        // validate password regex
        if (password.getValue() == null || !password.getValue().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(password, "Pattern.registrationForm.password");
        }

        // validate passwords match
        if (retypedPassword.getValue() != null && !retypedPassword.getValue().equals(password.getValue())) {
            setError(retypedPassword, "FieldMatch.registrationForm");
        }
    }

    @RequestMapping(value = "/password-reset.json", method = RequestMethod.GET)
    public @ResponseBody PasswordTypeAndConfirmForm getResetPassword(HttpServletRequest request) {
        PasswordTypeAndConfirmForm form = new PasswordTypeAndConfirmForm();
        form.setPassword(new Text());
        form.setRetypedPassword(new Text());
        return form;
    }

    private ModelAndView buildPasswordScreenWithOptionalSecurityQuestion(OneTimeResetPasswordForm oneTimeResetPasswordForm) {
        ModelAndView combinedView = new ModelAndView("password_one_time_reset_optional_security_questions");
        combinedView.addObject(oneTimeResetPasswordForm);
        return combinedView;
    }

    @RequestMapping(value = "/answer-security-question/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView buildAnswerSecurityQuestionView(@PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        int securityQuestionId = deriveSecurityIdByEmailForUser(passwordResetToken.getEmail());
        ModelAndView answerSecurityQuestionView = new ModelAndView("answer_security_question");
        ChangeSecurityQuestionForm changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
        changeSecurityQuestionForm.setSecurityQuestionId(securityQuestionId);
        String securityQuestion = retrieveSecurityQuestionsAsMap().get(String.valueOf(securityQuestionId));
        answerSecurityQuestionView.addObject("securityQuestionText", securityQuestion);
        answerSecurityQuestionView.addObject(changeSecurityQuestionForm);
        return answerSecurityQuestionView;
    }

    @RequestMapping(value = "/answer-security-question/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView submitSecurityAnswer(@PathVariable("encryptedEmail") String encryptedEmail, @Valid ChangeSecurityQuestionForm changeSecurityQuestionForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);

        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildAnswerSecurityQuestionView(encryptedEmail, redirectAttributes);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;
        }

        String securityAnswer = retrievedProfile.getSecurityQuestionAnswer();
        if (!changeSecurityQuestionForm.getSecurityQuestionAnswer().trim().equalsIgnoreCase(securityAnswer.trim())) {
            ModelAndView errorView = buildAnswerSecurityQuestionView(encryptedEmail, redirectAttributes);
            errorView.addObject("securityQuestionIncorrect", true);
            return errorView;
        }
        // build password standalone view
        return new ModelAndView("redirect:/one-time-password/" + encryptedEmail);
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView submitPasswordReset(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            @Valid OneTimeResetPasswordForm oneTimeResetPasswordForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        // double check link not expired - someone could send a curl request..
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        // validate the form and redirect back to the reset view
        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildPasswordScreenWithOptionalSecurityQuestion(oneTimeResetPasswordForm);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;

        }
        // update password, and optionally question and answer
        OrcidProfile profileToUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);
        profileToUpdate.setPassword(oneTimeResetPasswordForm.getPassword().getValue());
        if (oneTimeResetPasswordForm.isSecurityDetailsPopulated()) {
            profileToUpdate.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(oneTimeResetPasswordForm.getSecurityQuestionId()));
            profileToUpdate.setSecurityQuestionAnswer(oneTimeResetPasswordForm.getSecurityQuestionAnswer());
        }

        return updatePasswordAndGoToAccountsPage(request, response, profileToUpdate);
    }

    private ModelAndView updatePasswordAndGoToAccountsPage(HttpServletRequest request, HttpServletResponse response, OrcidProfile updatedProfile) {
        orcidProfileManager.updatePasswordInformation(updatedProfile);
        String redirectUrl = calculateRedirectUrl(request, response);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    private Integer deriveSecurityIdByEmailForUser(String email) {
        Integer securityQuestionVal = null;
        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(email, LoadOptions.INTERNAL_ONLY);
        if (retrievedProfile != null) {
            SecurityQuestionId securityQuestion = retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId();
            securityQuestionVal = securityQuestion != null ? (int) retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue() : null;

        }
        return securityQuestionVal;
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

    @RequestMapping(value = "/one-time-password/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView buildPasswordOneTimeResetView(@PathVariable("encryptedEmail") String encryptedEmail) {
        ModelAndView mav = new ModelAndView("password_one_time_reset");
        PasswordTypeAndConfirmForm passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        mav.addObject("passwordTypeAndConfirmForm", passwordTypeAndConfirmForm);
        return mav;
    }

    @RequestMapping(value = "/one-time-password/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView confirmPasswordOneTimeResetView(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            @Valid PasswordTypeAndConfirmForm passwordTypeAndConfirmForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildPasswordOneTimeResetView(encryptedEmail);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;
        }

        OrcidProfile passwordOnlyProfileUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);
        passwordOnlyProfileUpdate.setPassword(passwordTypeAndConfirmForm.getPassword() == null ? null : passwordTypeAndConfirmForm.getPassword().getValue());
        return updatePasswordAndGoToAccountsPage(request, response, passwordOnlyProfileUpdate);
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
