/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.RegistrationRoleManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.frontend.web.controllers.helper.SearchOrcidSolrCriteria;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.pojo.DupicateResearcher;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidWebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Will Simpson
 */
@Controller
public class RegistrationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    final static Integer DUP_SEARCH_START = 0;

    final static Integer DUP_SEARCH_ROWS = 25;

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SecurityQuestionManager securityQuestionManager;

    @Resource
    private RegistrationRoleManager registrationRoleManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    public void setRegistrationManager(RegistrationManager registrationManager) {
        this.registrationManager = registrationManager;
    }

    public void setOrcidSearchManager(OrcidSearchManager orcidSearchManager) {
        this.orcidSearchManager = orcidSearchManager;
    }

    public OrcidSearchManager getOrcidSearchManager() {
        return orcidSearchManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    @ModelAttribute("securityQuestions")
    public Map<String, String> retrieveSecurityQuestionsAsMap() {
        return securityQuestionManager.retrieveSecurityQuestionsAsMap();
    }

    @RequestMapping(value = "/register.json", method = RequestMethod.GET)
    public @ResponseBody
    Registration getRegister(HttpServletRequest request) {
        Registration reg = new Registration();

        reg.getEmail().setRequired(true);

        reg.getEmailConfirm().setRequired(true);

        reg.getPassword();
        reg.getPasswordConfirm();
        reg.getEmail();

        reg.getFamilyNames().setRequired(false);

        reg.getGivenNames().setRequired(true);

        reg.getSendChangeNotifications().setValue(true);
        reg.getSendOrcidNews().setValue(true);
        reg.getTermsOfUse().setValue(false);
        setError(reg.getTermsOfUse(), "AssertTrue.registrationForm.acceptTermsAndConditions");
        return reg;
    }

    private static OrcidProfile toProfile(Registration reg) {
        OrcidProfile profile = new OrcidProfile();
        OrcidBio bio = new OrcidBio();

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(reg.getEmail().getValue()));
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(reg.getSendChangeNotifications().getValue()));
        preferences.setSendOrcidNews(new SendOrcidNews(reg.getSendOrcidNews().getValue()));
        preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(Visibility.fromValue(reg.getWorkVisibilityDefault().getVisibility().value())));

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName(reg.getFamilyNames().getValue()));
        personalDetails.setGivenNames(new GivenNames(reg.getGivenNames().getValue()));

        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidBio(bio);
        profile.setOrcidInternal(internal);

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.WEBSITE);

        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        profile.setPassword(reg.getPassword().getValue());

        return profile;

    }

    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration setRegister(HttpServletRequest request, @RequestBody Registration reg) {
        reg.setErrors(new ArrayList<String>());

        registerGivenNameValidate(reg);
        registerPasswordValidate(reg);
        registerPasswordConfirmValidate(reg);
        regEmailValidate(request, reg);
        registerTermsOfUseValidate(reg);

        copyErrors(reg.getEmailConfirm(), reg);
        copyErrors(reg.getEmail(), reg);
        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);

        return reg;
    }

    @RequestMapping(value = "/registerConfirm.json", method = RequestMethod.POST)
    public @ResponseBody
    Redirect setRegisterConfirm(HttpServletRequest request, HttpServletResponse response, @RequestBody Registration reg) {
        Redirect r = new Redirect();

        // make sure validation still passes
        reg = setRegister(request, reg);
        if (reg.getErrors() != null && reg.getErrors().size() > 0) {
            r.getErrors().add("Please revalidate at /register.json");
            return r;
        }

        createMinimalRegistrationAndLogUserIn(request, toProfile(reg));
        String redirectUrl = calculateRedirectUrl(request, response);
        r.setUrl(redirectUrl);
        return r;
    }

    private String calculateRedirectUrl(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            String savedUrl = savedRequest.getRedirectUrl();
            if (savedUrl != null) {
                try {
                    String path = new URL(savedUrl).getPath();
                    if (path != null && path.contains("/oauth/")) {
                        // This redirect url is OK
                        return savedUrl;
                    }
                } catch (MalformedURLException e) {
                    LOGGER.debug("Malformed saved redirect url: {}", savedUrl);
                }
            }
        }
        return getBaseUri() + "/my-orcid";
    }

    @RequestMapping(value = "/registerPasswordConfirmValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration registerPasswordConfirmValidate(@RequestBody Registration reg) {
        passwordConfirmValidate(reg.getPasswordConfirm(), reg.getPassword());
        return reg;
    }

    @RequestMapping(value = "/registerPasswordValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration registerPasswordValidate(@RequestBody Registration reg) {
        passwordValidate(reg.getPasswordConfirm(), reg.getPassword());
        return reg;
    }

    @RequestMapping(value = "/claimPasswordConfirmValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Claim claimPasswordConfirmValidate(@RequestBody Claim claim) {
        passwordConfirmValidate(claim.getPasswordConfirm(), claim.getPassword());
        return claim;
    }

    @RequestMapping(value = "/claimPasswordValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Claim claimPasswordValidate(@RequestBody Claim claim) {
        passwordValidate(claim.getPasswordConfirm(), claim.getPassword());
        return claim;
    }

    private void passwordConfirmValidate(Text passwordConfirm, Text password) {
        passwordConfirm.setErrors(new ArrayList<String>());
        // validate passwords match
        if (passwordConfirm.getValue() == null || !passwordConfirm.getValue().equals(password.getValue())) {
            setError(passwordConfirm, "FieldMatch.registrationForm");
        }
    }

    private void passwordValidate(Text passwordConfirm, Text password) {
        password.setErrors(new ArrayList<String>());
        // validate password regex
        if (password.getValue() == null || !password.getValue().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(password, "Pattern.registrationForm.password");
        }

        if (passwordConfirm.getValue() != null) {
            passwordConfirmValidate(passwordConfirm, password);
        }
    }

    @RequestMapping(value = "/registerTermsOfUseValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration registerTermsOfUseValidate(@RequestBody Registration reg) {
        termsOfUserValidate(reg.getTermsOfUse());
        return reg;
    }

    @RequestMapping(value = "/claimTermsOfUseValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Claim claimTermsOfUseValidate(@RequestBody Claim claim) {
        termsOfUserValidate(claim.getTermsOfUse());
        return claim;
    }

    private void termsOfUserValidate(Checkbox termsOfUser) {
        termsOfUser.setErrors(new ArrayList<String>());
        if (termsOfUser.getValue() != true) {
            setError(termsOfUser, "AssertTrue.registrationForm.acceptTermsAndConditions");
        }
    }

    @RequestMapping(value = "/registerGivenNamesValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration registerGivenNameValidate(@RequestBody Registration reg) {
        // validate given name isn't blank
        reg.getGivenNames().setErrors(new ArrayList<String>());
        if (reg.getGivenNames().getValue() == null || reg.getGivenNames().getValue().trim().isEmpty()) {
            setError(reg.getGivenNames(), "NotBlank.registrationForm.givenNames");
        }
        return reg;
    }

    @RequestMapping(value = "/registerEmailValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration regEmailValidate(HttpServletRequest request, @RequestBody Registration reg) {
        reg.getEmail().setErrors(new ArrayList<String>());
        if (reg.getEmail().getValue() == null || reg.getEmail().getValue().trim().isEmpty()) {
            setError(reg.getEmail(), "Email.registrationForm.email");
        }
        // validate email
        MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
        // make sure there are no dups
        validateEmailAddress(reg.getEmail().getValue(), false, request, mbr);

        for (ObjectError oe : mbr.getAllErrors()) {
            reg.getEmail().getErrors().add(getMessage(oe.getCode(), reg.getEmail().getValue()));
        }

        // validate confirm if already field out
        if (reg.getEmailConfirm().getValue() != null) {
            regEmailConfirmValidate(reg);
        }

        return reg;
    }

    @RequestMapping(value = "/registerEmailConfirmValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Registration regEmailConfirmValidate(@RequestBody Registration reg) {
        reg.getEmailConfirm().setErrors(new ArrayList<String>());
        if (reg.getEmail().getValue() == null || !reg.getEmailConfirm().getValue().equalsIgnoreCase(reg.getEmail().getValue())) {
            setError(reg.getEmailConfirm(), "StringMatchIgnoreCase.registrationForm");
        }

        return reg;
    }

    private static void copyErrors(ErrorsInterface from, ErrorsInterface into) {
        for (String s : from.getErrors()) {
            into.getErrors().add(s);
        }
    }

    private void setError(ErrorsInterface ei, String msg) {
        ei.getErrors().add(getMessage(msg));
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("register");
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        LOGGER.debug("Saved url before registration is: " + (savedRequest != null ? savedRequest.getRedirectUrl() : " no saved request"));
        return mav;
    }

    @RequestMapping(value = "/dupicateResearcher.json", method = RequestMethod.GET)
    public @ResponseBody
    List<DupicateResearcher> getDupicateResearcher(@RequestParam("givenNames") String givenNames, @RequestParam("familyNames") String familyNames) {
        List<DupicateResearcher> drList = new ArrayList<DupicateResearcher>();

        List<OrcidProfile> potentialDuplicates = findPotentialDuplicatesByFirstNameLastName(givenNames, familyNames);
        for (OrcidProfile op : potentialDuplicates) {
            DupicateResearcher dr = new DupicateResearcher();
            if (op.getOrcidBio() != null) {
                if (op.getOrcidBio().getContactDetails() != null) {
                    if (op.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null) {
                        dr.setEmail(op.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
                    }
                }
                dr.setFamilyNames(op.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
                dr.setGivenNames(op.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
                dr.setInstitution(null);
            }
            dr.setOrcid(op.getOrcid().getValue());
            drList.add(dr);
        }

        return drList;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public ModelAndView resetPassword() {
        ModelAndView mav = new ModelAndView("reset_password");
        EmailAddressForm resetPasswordForm = new EmailAddressForm();
        mav.addObject(resetPasswordForm);
        return mav;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView issuePasswordResetRequest(HttpServletRequest request, @ModelAttribute @Valid EmailAddressForm resetPasswordForm, BindingResult bindingResult) {

        String userEmailAddress = resetPasswordForm.getUserEmailAddress();

        ModelAndView mav = new ModelAndView("reset_password");

        // if the email doesn't exist, or any other form errors.. don't bother
        // hitting db
        if (bindingResult.hasErrors()) {
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(userEmailAddress);
        // if the email can't be found on the system, then add to errors
        if (profile == null) {

            String[] codes = { "orcid.frontend.reset.password.email_not_found" };
            String[] args = { userEmailAddress };
            bindingResult.addError(new FieldError("userEmailAddress", "userEmailAddress", userEmailAddress, false, codes, args, "Email not found"));
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }

        else {
            if (profile.isDeactivated()) {
                mav.addObject("disabledAccount", true);
                return mav;
            } else {
                URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
                registrationManager.resetUserPassword(profile, uri);
                mav.addObject("passwordResetSuccessful", true);
                return mav;
            }
        }
    }

    @RequestMapping(value = "/resend-claim", method = RequestMethod.GET)
    public ModelAndView viewResendClaimEmail(@RequestParam(value = "email", required = false) String email) {
        ModelAndView mav = new ModelAndView("resend_claim");
        EmailAddressForm emailAddressForm = new EmailAddressForm();
        emailAddressForm.setUserEmailAddress(email);
        mav.addObject(emailAddressForm);
        return mav;
    }

    @RequestMapping(value = "/resend-claim", method = RequestMethod.POST)
    public ModelAndView resendClaimEmail(HttpServletRequest request, @ModelAttribute @Valid EmailAddressForm emailAddressForm, BindingResult bindingResult) {
        String userEmailAddress = emailAddressForm.getUserEmailAddress();
        ModelAndView mav = new ModelAndView("resend_claim");
        // if the email doesn't exist, or any other form errors.. don't bother
        // hitting db
        if (bindingResult.hasErrors()) {
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(userEmailAddress);
        // if the email can't be found on the system, then add to errors
        if (profile == null) {

            String[] codes = { "orcid.frontend.reset.password.email_not_found" };
            String[] args = { userEmailAddress };
            bindingResult.addError(new FieldError("userEmailAddress", "userEmailAddress", userEmailAddress, false, codes, args, "Email not found"));
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        } else {
            notificationManager.sendApiRecordCreationEmail(profile);
            mav.addObject("claimResendSuccessful", true);
            return mav;
        }
    }

    private void automaticallyLogin(HttpServletRequest request, String password, OrcidProfile orcidProfile) {
        UsernamePasswordAuthenticationToken token = null;
        try {
            token = new UsernamePasswordAuthenticationToken(orcidProfile.getOrcid().getValue(), password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            // this should never happen
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.warn("User {0} should have been logged-in, but we unable to due to a problem", e, (token != null ? token.getPrincipal() : "empty principle"));
        }
    }

    @RequestMapping(value = "/verify-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        return buildVerificationView(request, encryptedEmail, redirectAttributes);
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView resetPasswordEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail());
        if (StringUtils.isNotBlank(retrievedProfile.getSecurityQuestionAnswer())) {

            // if have a answer without a question something has gone wrong
            // anyway!!
            return new ModelAndView("redirect:/answer-security-question/" + encryptedEmail);
        }
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setSecurityQuestionId(0);
        // otherwise, straight to the screen with the option to set a security
        // question
        return buildPasswordScreenWithOptionalSecurityQuestion(form);
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
        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail());

        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildAnswerSecurityQuestionView(encryptedEmail, redirectAttributes);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;
        }

        String securityAnswer = retrievedProfile.getSecurityQuestionAnswer();
        if (!changeSecurityQuestionForm.getSecurityQuestionAnswer().equalsIgnoreCase(securityAnswer)) {
            ModelAndView errorView = buildAnswerSecurityQuestionView(encryptedEmail, redirectAttributes);
            errorView.addObject("securityQuestionIncorrect", true);
            return errorView;
        }
        // build password standalone view
        return new ModelAndView("redirect:/one-time-password/" + encryptedEmail);
    }

    @RequestMapping(value = "/one-time-password/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView buildPasswordOneTimeResetView(@PathVariable("encryptedEmail") String encryptedEmail) {
        ModelAndView mav = new ModelAndView("password_one_time_reset");
        PasswordTypeAndConfirmForm passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        mav.addObject("passwordTypeAndConfirmForm", passwordTypeAndConfirmForm);
        return mav;
    }

    @RequestMapping(value = "/one-time-password/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView confirmPasswordOneTimeResetView(@PathVariable("encryptedEmail") String encryptedEmail,
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

        OrcidProfile passwordOnlyProfileUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail());
        passwordOnlyProfileUpdate.setPassword(passwordTypeAndConfirmForm.getPassword());
        return updatePasswordAndGoToAccountsPage(passwordOnlyProfileUpdate);
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView submitPasswordReset(@PathVariable("encryptedEmail") String encryptedEmail, @Valid OneTimeResetPasswordForm oneTimeResetPasswordForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

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
        OrcidProfile profileToUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail());
        profileToUpdate.setPassword(oneTimeResetPasswordForm.getPassword());
        if (oneTimeResetPasswordForm.isSecurityDetailsPopulated()) {
            profileToUpdate.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(oneTimeResetPasswordForm.getSecurityQuestionId()));
            profileToUpdate.setSecurityQuestionAnswer(oneTimeResetPasswordForm.getSecurityQuestionAnswer());
        }

        return updatePasswordAndGoToAccountsPage(profileToUpdate);

    }

    private ModelAndView updatePasswordAndGoToAccountsPage(OrcidProfile updatedProfile) {

        orcidProfileManager.updatePasswordInformation(updatedProfile);
        return new ModelAndView("redirect:/account");
    }

    private Integer deriveSecurityIdByEmailForUser(String email) {
        Integer securityQuestionVal = null;
        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(email);
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
            throw new RuntimeException("Decryption Error occured during email password reset");
        }

    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {

        Date expiryDateOfOneHourFromIssueDate = org.apache.commons.lang.time.DateUtils.addHours(passwordResetToken.getIssueDate(), 1);
        Date now = new Date();
        return (expiryDateOfOneHourFromIssueDate.getTime() < now.getTime());
    }

    @RequestMapping(value = "/claim/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyClaim(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        if (!isEmailOkForCurrentUser(decryptedEmail)) {
            return new ModelAndView("wrong_user");
        }
        OrcidProfile profileToClaim = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
        if (profileToClaim.getOrcidHistory().isClaimed()) {
            return new ModelAndView("redirect:/signin?alreadyClaimed");
        }
        ModelAndView mav = new ModelAndView("claim");
        return mav;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}.json", method = RequestMethod.GET)
    public @ResponseBody
    Claim verifyClaimJson(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        Claim c = new Claim();
        c.getSendChangeNotifications().setValue(true);
        c.getSendOrcidNews().setValue(true);
        c.getTermsOfUse().setValue(false);
        claimTermsOfUseValidate(c);
        return c;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}.json", method = RequestMethod.POST)
    public @ResponseBody
    Claim submitClaimJson(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, @RequestBody Claim claim)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        claim.setErrors(new ArrayList<String>());
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        if (!isEmailOkForCurrentUser(decryptedEmail)) {
            claim.setUrl(getBaseUri() + "/claim/wrong_user");
            return claim;
        }
        OrcidProfile profileToClaim = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
        if (profileToClaim.getOrcidHistory().isClaimed()) {
            // Already claimed so send to sign in page
            claim.setUrl(getBaseUri() + "/signin?alreadyClaimed");
            return claim;
        }

        claimPasswordValidate(claim);
        claimPasswordConfirmValidate(claim);
        claimTermsOfUseValidate(claim);

        copyErrors(claim.getPassword(), claim);
        copyErrors(claim.getPasswordConfirm(), claim);
        copyErrors(claim.getTermsOfUse(), claim);

        if (claim.getErrors().size() > 0) {
            return claim;
        }
        OrcidProfile orcidProfile = confirmEmailAndClaim(decryptedEmail, profileToClaim, claim, request);
        orcidProfile.setPassword(claim.getPassword().getValue());
        orcidProfileManager.updatePasswordInformation(orcidProfile);
        automaticallyLogin(request, claim.getPassword().getValue(), orcidProfile);
        claim.setUrl(getBaseUri() + "/my-orcid?recordClaimed");
        return claim;
    }

    @RequestMapping(value = "/claim/wrong_user", method = RequestMethod.GET)
    public ModelAndView claimWrongUser(HttpServletRequest request) {
        return new ModelAndView("wrong_user");
    }

    private ModelAndView buildVerificationView(HttpServletRequest request, String encryptedEmail, RedirectAttributes redirectAttributes)
            throws UnsupportedEncodingException, NoSuchRequestHandlingMethodException {
    	try {
	        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
	        if (!isEmailOkForCurrentUser(decryptedEmail)) {
	            return new ModelAndView("wrong_user");
	        }
	        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
	        confirmEmailAndClaim(decryptedEmail, orcidProfile, null, request);
	        redirectAttributes.addFlashAttribute("emailVerified", true);
    	} catch (EncryptionOperationNotPossibleException eonpe){
    		
    	}
        return new ModelAndView("redirect:/my-orcid");
    }

    private OrcidProfile confirmEmailAndClaim(String decryptedEmail, OrcidProfile orcidProfile, Claim claim, HttpServletRequest request)
            throws NoSuchRequestHandlingMethodException {
        if (orcidProfile == null) {
            throw new NoSuchRequestHandlingMethodException(request);
        }
        Email email = orcidProfile.getOrcidBio().getContactDetails().getEmailByString(decryptedEmail);
        email.setVerified(true);
        email.setCurrent(true);
        OrcidHistory orcidHistory = orcidProfile.getOrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        CompletionDate completionDate = orcidHistory.getCompletionDate();
        if (completionDate == null) {
            orcidHistory.setCompletionDate(new CompletionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        }
        if (claim != null) {
            Preferences preferences = orcidProfile.getOrcidInternal().getPreferences();
            if (preferences == null) {
                preferences = new Preferences();
            }
            preferences.setSendChangeNotifications(new SendChangeNotifications(claim.getSendChangeNotifications().getValue()));
            preferences.setSendOrcidNews(new SendOrcidNews(claim.getSendOrcidNews().getValue()));
            preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(claim.getWorkVisibilityDefault().getVisibility()));
        }
        return orcidProfileManager.updateOrcidProfile(orcidProfile);
    }

    private List<OrcidProfile> findPotentialDuplicatesByFirstNameLastName(String firstName, String lastName) {
        LOGGER.info("About to search for potential duplicates during registration for first name={}, last name={}", firstName, lastName);
        List<OrcidProfile> orcidProfiles = new ArrayList<OrcidProfile>();
        SearchOrcidSolrCriteria queryForm = new SearchOrcidSolrCriteria();

        queryForm.setGivenName(firstName);
        queryForm.setFamilyName(lastName);

        String query = queryForm.deriveQueryString();
        OrcidMessage visibleProfiles = orcidSearchManager.findOrcidsByQuery(query, DUP_SEARCH_START, DUP_SEARCH_ROWS);
        if (visibleProfiles.getOrcidSearchResults() != null) {
            for (OrcidSearchResult searchResult : visibleProfiles.getOrcidSearchResults().getOrcidSearchResult()) {
                orcidProfiles.add(searchResult.getOrcidProfile());
            }
        }
        LOGGER.info("Found {} potential duplicates during registration for first name={}, last name={}", new Object[] { orcidProfiles.size(), firstName, lastName });
        return orcidProfiles;

    }

    private void createMinimalRegistrationAndLogUserIn(HttpServletRequest request, OrcidProfile profileToSave) {
        URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
        String password = profileToSave.getPassword();
        String sessionId = request.getSession() == null ? null : request.getSession().getId();
        UsernamePasswordAuthenticationToken token = null;
        String email = profileToSave.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        try {
            LOGGER.info("About to create profile from registration email={}, sessionid={}", email, sessionId);
            profileToSave = registrationManager.createMinimalRegistration(profileToSave);
            notificationManager.sendVerificationEmail(profileToSave, uri, profileToSave.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
            request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
            LOGGER.info("Created profile from registration orcid={}, email={}, sessionid={}", new Object[] { profileToSave.getOrcid().getValue(), email, sessionId });
            token = new UsernamePasswordAuthenticationToken(profileToSave.getOrcid().getValue(), password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            // this should never happen
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.warn("User {0} should have been logged-in, but we unable to due to a problem", e, (token != null ? token.getPrincipal() : "empty principle"));
        }

    }

}
