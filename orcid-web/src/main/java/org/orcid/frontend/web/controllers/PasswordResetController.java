package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialSignInUtils;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.util.CommonPasswords;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
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
    
    @Resource
    private SocialSignInUtils socialSignInUtils;
    
    @Resource
    private RecordEmailSender recordEmailSender;

    private static final List<String> RESET_PASSWORD_PARAMS_WHITELIST = Arrays.asList("_");

    @RequestMapping(value = "/reset-password.json", method = RequestMethod.GET)
    public @ResponseBody EmailRequest getPasswordResetRequest() {
        return new EmailRequest();
    }

    @RequestMapping(value = "/validate-reset-password.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest validateResetPasswordRequest(@RequestBody EmailRequest passwordResetRequest) {
        List<String> errors = new ArrayList<>();
        passwordResetRequest.setErrors(errors);
        passwordResetRequest.setEmail(passwordResetRequest.getEmail().trim());
        if (!validateEmailAddress(passwordResetRequest.getEmail())) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
        }
        return passwordResetRequest;
    }
    
    @RequestMapping(value = "/forgot-id.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest issueForgottenIdRequest(@RequestBody EmailRequest forgotIdRequest) {
        List<String> errors = new ArrayList<>();
        forgotIdRequest.setErrors(errors);
        forgotIdRequest.setEmail(forgotIdRequest.getEmail().trim());
        if (!validateEmailAddress(forgotIdRequest.getEmail())) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
            return forgotIdRequest;
        }
        
        if (emailManager.emailExists(forgotIdRequest.getEmail())) {
            String orcid = emailManager.findOrcidIdByEmail(forgotIdRequest.getEmail());
            if (profileEntityManager.isDeactivated(orcid)) {
                recordEmailSender.sendReactivationEmail(forgotIdRequest.getEmail(), orcid);
            } else if (!profileEntityManager.isProfileClaimedByEmail(forgotIdRequest.getEmail())) {
                recordEmailSender.sendClaimReminderEmail(orcid,0,forgotIdRequest.getEmail());
            } else {
                recordEmailSender.sendForgottenIdEmail(forgotIdRequest.getEmail(), orcid);
            }
        } else {
            Locale locale = localeManager.getLocale();
            recordEmailSender.sendForgottenIdEmailNotFoundEmail(forgotIdRequest.getEmail(), locale);
        }
        forgotIdRequest.setSuccessMessage(getMessage("orcid.frontend.reset.password.successfulReset") + " " + forgotIdRequest.getEmail());
        return forgotIdRequest;
    }

    @RequestMapping(value = "/reset-password.json", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<EmailRequest> issuePasswordResetRequest(HttpServletRequest request, @RequestBody EmailRequest passwordResetRequest) {
        for (String param : request.getParameterMap().keySet()) {
            if (!RESET_PASSWORD_PARAMS_WHITELIST.contains(param)) {
                LOGGER.info("Password reset: Non whitelisted param '{}' for password reset request for email '{}'", param, passwordResetRequest.getEmail());
                // found parameter that has not been white-listed
                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }        
        LOGGER.info("Password reset: processing password reset request for '{}'", passwordResetRequest.getEmail());
        List<String> errors = new ArrayList<>();
        passwordResetRequest.setErrors(errors);
        passwordResetRequest.setEmail(passwordResetRequest.getEmail().trim());
        if (!validateEmailAddress(passwordResetRequest.getEmail())) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
        }
        try {
            if (emailManager.emailExists(passwordResetRequest.getEmail())) {
                String orcid = emailManager.findOrcidIdByEmail(passwordResetRequest.getEmail());
                if (profileEntityManager.isDeactivated(orcid)) {
                    LOGGER.info("Password reset: Reactivation email sent to '{}'", passwordResetRequest.getEmail());
                    recordEmailSender.sendReactivationEmail(passwordResetRequest.getEmail(), orcid);
                } else if (!profileEntityManager.isProfileClaimedByEmail(passwordResetRequest.getEmail())) {
                    LOGGER.info("Password reset: API record creation email sent to '{}'", passwordResetRequest.getEmail());
                    recordEmailSender.sendClaimReminderEmail(orcid,0,passwordResetRequest.getEmail());
                } else {
                    LOGGER.info("Password reset: Reset password email sent to '{}'", passwordResetRequest.getEmail());
                    recordEmailSender.sendPasswordResetEmail(passwordResetRequest.getEmail(), orcid);
                }
            } else {
                Locale locale = localeManager.getLocale();
                LOGGER.info("Password reset: Email not found email sent to '{}' with locale '{}'", passwordResetRequest.getEmail(), locale);
                recordEmailSender.sendPasswordResetNotFoundEmail(passwordResetRequest.getEmail(), locale);
            }
            passwordResetRequest.setSuccessMessage(getMessage("orcid.frontend.reset.password.successfulReset") + " " + passwordResetRequest.getEmail());
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.OK);
        } catch(Exception e) {
            LOGGER.error("Password reset: Unable to reset password for " + passwordResetRequest.getEmail(), e);                
            errors.add(getMessage("Email.resetPasswordForm.error"));
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.BAD_REQUEST);
        }        
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView resetPasswordEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:" + calculateRedirectUrl("/reset-password?expired=true"));
        }
        ModelAndView result = new ModelAndView("password_one_time_reset");
        result.addObject("noIndex", true);
        return result;
    }

    @RequestMapping(value = "/reset-password-form-validate.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm resetPasswordConfirmValidate(@RequestBody OneTimeResetPasswordForm resetPasswordForm) {
        
    	resetPasswordForm.setErrors(new ArrayList<String>());

    	Emails emails = null;
    	if (resetPasswordForm.getEncryptedEmail() != null) {
	    	PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(resetPasswordForm.getEncryptedEmail());
	    	String orcid = emailManagerReadOnly.findOrcidIdByEmail(passwordResetToken.getEmail());
	    	emails = emailManager.getEmails(orcid);
    	} 
    	passwordChecklistValidate(resetPasswordForm.getRetypedPassword(), resetPasswordForm.getPassword(), emails);
    	
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
    	
        OneTimeResetPasswordForm oneTimeResetPasswordForm  = new OneTimeResetPasswordForm();
        passwordChecklistValidate(oneTimeResetPasswordForm.getRetypedPassword(), oneTimeResetPasswordForm.getPassword());
        return oneTimeResetPasswordForm;
    }

    @Deprecated //TODO remove once the card https://trello.com/c/TqSd7ojs/7973-reset-password is stable on prod
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
        
    	String orcid = emailManagerReadOnly.findOrcidIdByEmail(passwordResetToken.getEmail());
    	Emails emails = emailManager.getEmails(orcid);
    	
        passwordChecklistValidate(oneTimeResetPasswordForm.getRetypedPassword(), oneTimeResetPasswordForm.getPassword(), emails);
        if (!oneTimeResetPasswordForm.getPassword().getErrors().isEmpty() || !oneTimeResetPasswordForm.getRetypedPassword().getErrors().isEmpty()) {
            return oneTimeResetPasswordForm;
        }

        profileEntityManager.updatePassword(orcid, oneTimeResetPasswordForm.getPassword().getValue());
        //reset the lock fields
        profileEntityManager.resetSigninLock(orcid);
        profileEntityCacheManager.remove(orcid);
        

        String redirectUrl = calculateRedirectUrl(request, response, false);
        oneTimeResetPasswordForm.setSuccessRedirectLocation(redirectUrl);
        return oneTimeResetPasswordForm;
    }
    
    @RequestMapping(value = "/reset-password-email-v2.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm submitPasswordResetV2(HttpServletRequest request, HttpServletResponse response,
            @RequestBody OneTimeResetPasswordForm oneTimeResetPasswordForm) {
        oneTimeResetPasswordForm.setErrors(new ArrayList<String>());

        PasswordResetToken passwordResetToken;
        
        try {
             passwordResetToken = buildResetTokenFromEncryptedLink(oneTimeResetPasswordForm.getEncryptedEmail());

        } catch (EncryptionOperationNotPossibleException e) {
            oneTimeResetPasswordForm.getErrors().add("invalidPasswordResetToken");
            return oneTimeResetPasswordForm;
        }
        
        if (isTokenExpired(passwordResetToken)) {
            String message = "expiredPasswordResetToken";
            oneTimeResetPasswordForm.getErrors().add(message);
            return oneTimeResetPasswordForm;
        }

        passwordConfirmValidate(oneTimeResetPasswordForm.getRetypedPassword(), oneTimeResetPasswordForm.getPassword());
        
        String orcid = null;
        //check first if valid orcid as the admin portal can send either and email or an orcid
        if(OrcidStringUtils.isValidOrcid(passwordResetToken.getEmail()) ){
            if(profileEntityManager.orcidExists(passwordResetToken.getEmail())) {
                orcid = passwordResetToken.getEmail();
            }
            else {
                String message = "invalidPasswordResetToken";
                oneTimeResetPasswordForm.getErrors().add(message);
                return oneTimeResetPasswordForm;
            }
        }
        else {
            orcid = emailManagerReadOnly.findOrcidIdByEmail(passwordResetToken.getEmail());
        }

        Emails emails = emailManager.getEmails(orcid);
        
        passwordChecklistValidate(oneTimeResetPasswordForm.getRetypedPassword(), oneTimeResetPasswordForm.getPassword(), emails);
        if (!oneTimeResetPasswordForm.getPassword().getErrors().isEmpty() || !oneTimeResetPasswordForm.getRetypedPassword().getErrors().isEmpty()) {
            return oneTimeResetPasswordForm;
        }

        profileEntityManager.updatePassword(orcid, oneTimeResetPasswordForm.getPassword().getValue());
        //reset the lock fields
        profileEntityManager.resetSigninLock(orcid);
        profileEntityCacheManager.remove(orcid);
        String redirectUrl = calculateRedirectUrl(request, response, false);
        oneTimeResetPasswordForm.setSuccessRedirectLocation(redirectUrl);
        return oneTimeResetPasswordForm;
    }
    
    @RequestMapping(value = "/reset-password-email-validate-token.json", method = RequestMethod.POST)
    public @ResponseBody OneTimeResetPasswordForm submitPasswordEmailValidatePassword(HttpServletRequest request, HttpServletResponse response,
            @RequestBody OneTimeResetPasswordForm oneTimeResetPasswordForm) {
        oneTimeResetPasswordForm.setErrors(new ArrayList<String>());

        PasswordResetToken passwordResetToken;
        
        try {
             passwordResetToken = buildResetTokenFromEncryptedLink(oneTimeResetPasswordForm.getEncryptedEmail());

        } catch (EncryptionOperationNotPossibleException e) {
            oneTimeResetPasswordForm.getErrors().add("invalidPasswordResetToken");
            return oneTimeResetPasswordForm;
        }
        
        if (isTokenExpired(passwordResetToken)) {
            String message = "expiredPasswordResetToken";
            oneTimeResetPasswordForm.getErrors().add(message);
            return oneTimeResetPasswordForm;
        } else {
            return oneTimeResetPasswordForm;
        }
        
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
        Date expiryDateOfOneHourFromIssueDate = org.apache.commons.lang.time.DateUtils.addHours(passwordResetToken.getIssueDate(), passwordResetToken.getDurationInHours());
        Date now = new Date();
        return (expiryDateOfOneHourFromIssueDate.getTime() < now.getTime());
    }

    @RequestMapping(value = "/sendReactivation.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> sendReactivation(@RequestParam(name="email", required=false) String email, @RequestParam(name="orcid", required=false) String orcid) throws UnsupportedEncodingException {

        if (email != null) {
            if (!email.contains("@")) {
                email = URLDecoder.decode(email, "UTF-8");
            }
    
            email = OrcidStringUtils.filterEmailAddress(email);
    
            if (!validateEmailAddress(email)) {
                String error = getMessage("Email.personalInfoForm.email");
                return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");
            } else {
                try{
                    orcid = emailManager.findOrcidIdByEmail(email);
                } catch(NoResultException nre) {
                    String error = getMessage("Email.resendClaim.invalidEmail");
                    return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");                
                }
            }
        } else {
            if (!OrcidStringUtils.isValidOrcid(orcid)) {
                String error = getMessage("Email.resetPasswordForm.error");
                return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");
            } else {
                try{
                    email = emailManager.findPrimaryEmail(orcid).getEmail();
                } catch(NoResultException nre) {
                    String error = getMessage("Email.resetPasswordForm.error");
                    return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");                
                }
            }
        }

        ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);
        if (entity.getDeactivationDate() == null) {
            String error = getMessage("orcid.frontend.reactivate.error.already_active");
            return ResponseEntity.ok("{\"sent\":false, \"error\":\"" + error + "\"}");
        }

        recordEmailSender.sendReactivationEmail(email, orcid);
        return ResponseEntity.ok("{\"sent\":true}");
    }

    @RequestMapping(value = "/reactivation/{resetParams}", method = RequestMethod.GET)
    public ModelAndView reactivation(HttpServletRequest request, @PathVariable("resetParams") String resetParams, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("reactivation");
        mav.addObject("noIndex", true);
        return mav;
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
        if(OrcidOauth2Constants.SOCIAL.equals(reg.getLinkType())) {
            Map<String, String> signedInData = socialSignInUtils.getSignedInData(request, response);
            if(signedInData != null && signedInData.containsKey(OrcidOauth2Constants.PROVIDER_ID)) {
                String providerId = signedInData.get(OrcidOauth2Constants.PROVIDER_ID);
                if(OrcidOauth2Constants.FACEBOOK.equals(providerId) || OrcidOauth2Constants.GOOGLE.equals(providerId)) {
                    ajaxAuthenticationSuccessHandlerSocial.linkSocialAccount(request, response);
                }
            }
        } else if (OrcidOauth2Constants.SHIBBOLETH.equals(reg.getLinkType())) {
            ajaxAuthenticationSuccessHandlerShibboleth.linkShibbolethAccount(request, response);
        }
        
        String redirectUrl = calculateRedirectUrl(request, response, false);
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
        // Return a list of email addresses that should be notified by this change
        List<String> emailsToNotify = profileEntityManager.reactivate(orcid, email, reactivation);

        // Notify any new email address
        if (!emailsToNotify.isEmpty()) {
            for (String emailToNotify : emailsToNotify) {
                boolean isPrimaryEmail = emailManager.isPrimaryEmail(orcid, emailToNotify);
                recordEmailSender.sendVerificationEmail(orcid, emailToNotify, isPrimaryEmail);
            }
        }
        
        // Log user in
        registrationController.logUserIn(request, response, orcid, password);

        if (reactivation.getAffiliationForm() != null) {
            registrationManager.createAffiliation(reactivation, orcid);
        }
    }
}
