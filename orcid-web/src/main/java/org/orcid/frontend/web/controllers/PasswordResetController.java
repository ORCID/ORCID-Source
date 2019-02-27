package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.util.CommonPasswords;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ReactivationData;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetController.class);

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Autowired
    private SocialContext socialContext;

    @Resource
    private SocialAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerSocial;

    @Resource
    private ShibbolethAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerShibboleth;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private RegistrationController registrationController;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    private static final List<String> RESET_PASSWORD_PARAMS_WHITELIST = Arrays.asList("_");

    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public ModelAndView resetPassword() {
        return new ModelAndView("reset_password");
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
    public @ResponseBody ResponseEntity<EmailRequest> issuePasswordResetRequest(HttpServletRequest request, @RequestBody EmailRequest passwordResetRequest) {
        for (String param : request.getParameterMap().keySet()) {
            if (!RESET_PASSWORD_PARAMS_WHITELIST.contains(param)) {
                // found parameter that has not been white-listed
                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        List<String> errors = new ArrayList<>();
        passwordResetRequest.setErrors(errors);
        if (!validateEmailAddress(passwordResetRequest.getEmail())) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
        }
        if (Features.RESET_PASSWORD_EMAIL.isActive()) {
            if (emailManager.emailExists(passwordResetRequest.getEmail())) {
                String orcid = emailManager.findOrcidIdByEmail(passwordResetRequest.getEmail());
                if (profileEntityManager.isDeactivated(orcid)) {
                    notificationManager.sendReactivationEmail(passwordResetRequest.getEmail(), orcid);
                } else if (!profileEntityManager.isProfileClaimedByEmail(passwordResetRequest.getEmail())) {
                    notificationManager.sendApiRecordCreationEmail(passwordResetRequest.getEmail(), orcid);
                } else {
                    notificationManager.sendPasswordResetEmail(passwordResetRequest.getEmail(), orcid);
                }
            } else {
                Locale locale = localeManager.getLocale();
                notificationManager.sendPasswordResetNotFoundEmail(passwordResetRequest.getEmail(), locale);
            }
            passwordResetRequest.setSuccessMessage(getMessage("orcid.frontend.reset.password.successfulReset") + " " + passwordResetRequest.getEmail());
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
        } else {
            try {
                String orcid = emailManager.findOrcidIdByEmail(passwordResetRequest.getEmail());
                ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
                if (profile == null) {
                    String message = getMessage("orcid.frontend.reset.password.email_not_found_1") + " " + passwordResetRequest.getEmail() + " "
                            + getMessage("orcid.frontend.reset.password.email_not_found_2");
                    message += "<a href=\"mailto:support@orcid.org\">";
                    message += getMessage("orcid.frontend.reset.password.email_not_found_3");
                    message += "</a>";
                    message += getMessage("orcid.frontend.reset.password.email_not_found_4");
                    errors.add(message);
                    return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
                }

                if (profile.getDeactivationDate() != null) {
                    /*
                     * String message = getMessage(
                     * "orcid.frontend.reset.password.disabled_account_1");
                     * message += "<a href=\"/help/contact-us\">"; message +=
                     * getMessage(
                     * "orcid.frontend.reset.password.disabled_account_2");
                     * message += "</a>"; errors.add(message);
                     */
                    errors.add("orcid.frontend.security.orcid_deactivated");
                    return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
                }

                registrationManager.resetUserPassword(passwordResetRequest.getEmail(), orcid, profile.getClaimed());
                passwordResetRequest.setSuccessMessage(getMessage("orcid.frontend.reset.password.successfulReset") + " " + passwordResetRequest.getEmail());
            } catch (NoResultException nre) {
                errors.add(getMessage("Email.resetPasswordForm.error"));
            }
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView resetPasswordEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password?expired=true");
        }
        ModelAndView result = new ModelAndView("password_one_time_reset_optional_security_questions");
        result.addObject("noIndex", true);
        return result;
    }

    @RequestMapping(value = "/reset-password-form-validate.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm resetPasswordConfirmValidate(@RequestBody OneTimeResetPasswordForm resetPasswordForm) {
        resetPasswordForm.setErrors(new ArrayList<String>());
        if (resetPasswordForm.getPassword().getValue() == null || !resetPasswordForm.getPassword().getValue().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(resetPasswordForm, "Pattern.registrationForm.password");
        }

        if (resetPasswordForm.getRetypedPassword() != null && !resetPasswordForm.getRetypedPassword().equals(resetPasswordForm.getPassword())) {
            setError(resetPasswordForm, "FieldMatch.registrationForm");
        }

        if (CommonPasswords.passwordIsCommon(resetPasswordForm.getPassword().getValue())) {
            setError(resetPasswordForm, "password.too_common", resetPasswordForm.getPassword());
        }
        return resetPasswordForm;
    }

    @RequestMapping(value = "/password-reset.json", method = RequestMethod.GET)
    public @ResponseBody OneTimeResetPasswordForm getResetPassword() {
        return new OneTimeResetPasswordForm();
    }

    @RequestMapping(value = "/reset-password-email.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm submitPasswordReset(HttpServletRequest request, HttpServletResponse response,
            @RequestBody OneTimeResetPasswordForm oneTimeResetPasswordForm) {
        oneTimeResetPasswordForm.setErrors(new ArrayList<String>());

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(oneTimeResetPasswordForm.getEncryptedEmail());
        if (isTokenExpired(passwordResetToken)) {
            String message = getMessage("orcid.frontend.reset.password.resetLinkExpired_1");
            message += "<a href='/reset-password'>";
            message += getMessage("orcid.frontend.reset.password.resetLinkExpired_2");
            message += "</a>";
            oneTimeResetPasswordForm.getErrors().add(message);
            return oneTimeResetPasswordForm;
        }

        passwordConfirmValidate(oneTimeResetPasswordForm.getRetypedPassword(), oneTimeResetPasswordForm.getPassword());
        passwordValidate(oneTimeResetPasswordForm.getRetypedPassword(), oneTimeResetPasswordForm.getPassword());
        if (!oneTimeResetPasswordForm.getPassword().getErrors().isEmpty() || !oneTimeResetPasswordForm.getRetypedPassword().getErrors().isEmpty()) {
            return oneTimeResetPasswordForm;
        }

        String orcid = emailManagerReadOnly.findOrcidIdByEmail(passwordResetToken.getEmail());
        profileEntityManager.updatePassword(orcid, oneTimeResetPasswordForm.getPassword().getValue());

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

    @RequestMapping(value = "/sendReactivation.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> sendReactivation(@RequestParam("email") String email) throws UnsupportedEncodingException {
        email = URLDecoder.decode(email, "UTF-8");
        String orcid = null;
        if (!email.contains("@")) {
            String error = getMessage("Email.personalInfoForm.email");
            return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");
        } else {
            orcid = emailManager.findOrcidIdByEmail(email);
        }

        ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);
        if (entity.getDeactivationDate() == null) {
            String error = getMessage("orcid.frontend.reactivate.error.already_active");
            return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");
        }

        notificationManager.sendReactivationEmail(email, orcid);
        return ResponseEntity.ok("{\"sent\":true}");
    }

    @RequestMapping(value = "/reactivation/{resetParams}", method = RequestMethod.GET)
    public ModelAndView reactivation(HttpServletRequest request, @PathVariable("resetParams") String resetParams, RedirectAttributes redirectAttributes) {
        return new ModelAndView("reactivation");
    }

    @RequestMapping(value = "/reactivationData.json", method = RequestMethod.GET)
    public @ResponseBody ReactivationData getResetPassword(@RequestParam(value = "params") String resetParams) {
        ReactivationData reactivationData = new ReactivationData();
        try {
            PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(resetParams);
            reactivationData.setReactivationLinkExpired(isTokenExpired(passwordResetToken));
            reactivationData.setEmail(passwordResetToken.getEmail());
            reactivationData.setTokenValid(true);
        } catch (EncryptionOperationNotPossibleException e) {
            reactivationData.setTokenValid(false);
            return reactivationData;
        }
        reactivationData.setResetParams(resetParams);
        return reactivationData;
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

        activitiesVisibilityDefaultValidate(reg.getActivitiesVisibilityDefault());
        givenNameValidate(reg.getGivenNames());
        passwordValidate(reg.getPasswordConfirm(), reg.getPassword());
        passwordConfirmValidate(reg.getPasswordConfirm(), reg.getPassword());
        termsOfUserValidate(reg.getTermsOfUse());

        copyErrors(reg.getActivitiesVisibilityDefault(), reg);
        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);

        // validate email addresses are available
        if (reg.getEmailsAdditional() != null && !reg.getEmailsAdditional().isEmpty()) {
            regEmailAdditionalValidate(request, reg);
        }
    }

    @RequestMapping(value = "/reactivateAdditionalEmailsValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration regEmailAdditionalValidate(HttpServletRequest request, @RequestBody Registration reg) {
        String orcid = emailManagerReadOnly.findOrcidIdByEmail(reg.getEmail().getValue());
        Iterator<Text> it = reg.getEmailsAdditional().iterator();
        while (it.hasNext()) {
            Text email = it.next();
            if (PojoUtil.isEmpty(email)) {
                it.remove();
            } else {
                additionalEmailValidateOnReactivate(request, reg, email, orcid);
                copyErrors(email, reg);
            }
        }
        return reg;
    }

    private void reactivateAndLogUserIn(HttpServletRequest request, HttpServletResponse response, Reactivation reactivation) {
        PasswordResetToken resetParams = buildResetTokenFromEncryptedLink(reactivation.getResetParams());
        String email = resetParams.getEmail();
        String orcid = emailManager.findOrcidIdByEmail(email);
        String password = reactivation.getPassword().getValue();

        // Reactivate the user
        profileEntityManager.reactivate(orcid, email, reactivation);

        // Log user in
        registrationController.logUserIn(request, response, orcid, password);
    }
}
