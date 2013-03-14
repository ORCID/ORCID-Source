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
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.HearAboutManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.RegistrationRoleManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.manager.SolrAndDBSearchManager;
import org.orcid.core.manager.SponsorManager;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.frontend.web.controllers.helper.SearchOrcidSolrCriteria;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.ClaimForm;
import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.frontend.web.forms.RegistrationForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Will Simpson
 */
@Controller
public class RegistrationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @Resource
    private HearAboutManager hearAboutManager;

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private CountryManager countryManager;

    @Resource
    private SponsorManager sponsorManager;

    @Resource
    private SecurityQuestionManager securityQuestionManager;

    @Resource
    private RegistrationRoleManager registrationRoleManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private SolrAndDBSearchManager searchManager;

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

    public void setSearchManager(SolrAndDBSearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public SolrAndDBSearchManager getSearchManager() {
        return searchManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    @ModelAttribute("hearAbouts")
    public Map<String, String> retrieveHearAboutsAsMap() {
        return hearAboutManager.retrieveHearAboutsAsMap();
    }

    @ModelAttribute("yesNo")
    public Map<String, String> retrieveYesNoMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("true", "Yes");
        map.put("false", "No");
        return map;
    }

    @ModelAttribute("changeNotificationsMap")
    public Map<String, String> getChangeNotificationsMap() {
        Map<String, String> changeNotificationsMap = new HashMap<String, String>();
        changeNotificationsMap.put("sendMe", "");
        return changeNotificationsMap;
    }

    @ModelAttribute("orcidNewsMap")
    public Map<String, String> getOrcidNewsMap() {
        Map<String, String> orcidNewsMap = new HashMap<String, String>();
        orcidNewsMap.put("sendMe", "");
        return orcidNewsMap;
    }

    public Map<String, String> retrieveCountries() {
        Map<String, String> countriesWithId = new LinkedHashMap<String, String>();
        List<String> countries = new ArrayList<String>();
        countries.add("Select a country");
        countries.addAll(countryManager.retrieveCountries());
        for (String countryName : countries) {
            countriesWithId.put(countryName, countryName);
        }
        return countriesWithId;
    }

    @ModelAttribute("sponsors")
    public Map<String, String> retrieveSelectableSponsorsAsMap() {
        Map<String, String> sponsors = new LinkedHashMap<String, String>();
        sponsors.put("0", "Select a Sponsor");
        sponsors.putAll(sponsorManager.retrieveSponsorsAsMap());
        return sponsors;
    }

    @ModelAttribute("securityQuestions")
    public Map<String, String> retrieveSecurityQuestionsAsMap() {
        return securityQuestionManager.retrieveSecurityQuestionsAsMap();
    }

    public Map<String, String> retrieveRegistrationRolesAsMap() {
        Map<String, String> registrationRoles = new LinkedHashMap<String, String>();
        registrationRoles.put("", "Select a role");
        registrationRoles.putAll(registrationRoleManager.retrieveRegistrationRolesAsMap());
        return registrationRoles;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("register");
        RegistrationForm registrationForm = new RegistrationForm();
        mav.addObject(registrationForm);
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        LOGGER.debug("Saved url before registration is: " + (savedRequest != null ? savedRequest.getRedirectUrl() : " no saved request"));
        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView submitRegistration(HttpServletRequest request, @ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm,
            BindingResult bindingResult) {
        String email = registrationForm.getEmail();
        String sessionId = request.getSession() == null ? null : request.getSession().getId();
        LOGGER.info("User with email={}, sessionid={} has asked to register", email, sessionId);
        validateEmailAddress(email, false, request, bindingResult);
        if (bindingResult.hasErrors()) {
            LOGGER.info("Failed validation on registration page for email={}, sessionid={}, with errors={}", new Object[] { email, sessionId,
                    bindingResult.getAllErrors() });
            ModelAndView erroredView = new ModelAndView("register");
            erroredView.addAllObjects(bindingResult.getModel());
            return erroredView;
        } else {
            LOGGER.info("Passed validation on registration page for email={}, sessionid={}", email, sessionId);
            // search for duplicates
            // if they exist, put them in the view, along with the original reg
            // form and hear about which we could need later
            ModelAndView registrationView = new ModelAndView();
            List<OrcidProfile> potentialDuplicates = findPotentialDuplicatesByFirstNameLastName(registrationForm.getGivenNames(), registrationForm.getFamilyName());
            if (!potentialDuplicates.isEmpty()) {
                // stash the original form in case none of the duplicates are
                // the current registrar
                request.getSession().setAttribute("registrationForm", registrationForm);
                registrationView.addObject("potentialDuplicates", potentialDuplicates);
                registrationView.setViewName("duplicate_researcher");
                return registrationView;
            } else {
                return createConfirmRegistration(request, registrationForm);
            }

        }
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
            URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
            registrationManager.resetUserPassword(profile, uri);
            mav.addObject("passwordResetSuccessful", true);
            return mav;
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

    @RequestMapping(value = "/progress-to-confirm-registration-details", method = RequestMethod.POST)
    public ModelAndView progressToConfirmRegistration(HttpServletRequest request) {
        // restore the valid form to be saved
        RegistrationForm registrationForm = (RegistrationForm) request.getSession().getAttribute("registrationForm");
        return createConfirmRegistration(request, registrationForm);
    }

    @RequestMapping(value = "/confirm-registration-details", method = RequestMethod.POST)
    private ModelAndView createConfirmRegistration(HttpServletRequest request, RegistrationForm registrationForm) {

        ModelAndView confirmRegView = new ModelAndView("redirect:/my-orcid");
        createMinimalRegistrationAndLogUserIn(request, registrationForm.toOrcidProfile());
        request.getSession().setAttribute("registrationForm", null);
        return confirmRegView;
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
            // Already claimed so send to sign in page
            redirectAttributes.addFlashAttribute("alreadyClaimed", true);
            return new ModelAndView("redirect:/signin");
        }
        ModelAndView mav = new ModelAndView("claim");
        ClaimForm claimForm = new ClaimForm();
        mav.addObject(claimForm);
        mav.addObject("profile", profileToClaim);
        return mav;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView submitClaim(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, @ModelAttribute @Valid ClaimForm claimForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        if (!isEmailOkForCurrentUser(decryptedEmail)) {
            return new ModelAndView("wrong_user");
        }
        OrcidProfile profileToClaim = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
        if (profileToClaim.getOrcidHistory().isClaimed()) {
            // Already claimed so send to sign in page
            redirectAttributes.addFlashAttribute("alreadyClaimed", true);
            return new ModelAndView("redirect:/signin");
        }
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("claim");
            mav.addAllObjects(bindingResult.getModel());
            mav.addObject("profile", profileToClaim);
            return mav;
        }
        OrcidProfile orcidProfile = confirmEmailAndClaim(decryptedEmail, profileToClaim, claimForm, request);
        orcidProfile.setPassword(claimForm.getPassword());
        orcidProfileManager.updatePasswordInformation(orcidProfile);
        automaticallyLogin(request, claimForm.getPassword(), orcidProfile);
        redirectAttributes.addFlashAttribute("recordClaimed", true);
        return new ModelAndView("redirect:/my-orcid");
    }

    private ModelAndView buildVerificationView(HttpServletRequest request, String encryptedEmail, RedirectAttributes redirectAttributes)
            throws UnsupportedEncodingException, NoSuchRequestHandlingMethodException {
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        if (!isEmailOkForCurrentUser(decryptedEmail)) {
            return new ModelAndView("wrong_user");
        }
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
        confirmEmailAndClaim(decryptedEmail, orcidProfile, null, request);
        redirectAttributes.addFlashAttribute("emailVerified", true);
        return new ModelAndView("redirect:/my-orcid");
    }

    private OrcidProfile confirmEmailAndClaim(String decryptedEmail, OrcidProfile orcidProfile, ClaimForm claimForm, HttpServletRequest request)
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
        if (claimForm != null) {
            Preferences preferences = orcidProfile.getOrcidInternal().getPreferences();
            if (preferences == null) {
                preferences = new Preferences();
            }
            preferences.setSendChangeNotifications(new SendChangeNotifications(claimForm.isSendOrcidChangeNotifcations()));
            preferences.setSendOrcidNews(new SendOrcidNews(claimForm.isSendOrcidNews()));
            preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(Visibility.fromValue(claimForm.getWorkVisibilityDefault())));
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
        OrcidMessage visibleProfiles = searchManager.findFilteredOrcidsBasedOnQuery(query);
        if (visibleProfiles.getOrcidSearchResults() != null) {
            for (OrcidSearchResult searchResult : visibleProfiles.getOrcidSearchResults().getOrcidSearchResult()) {
                orcidProfiles.add(searchResult.getOrcidProfile());
            }
        }
        LOGGER.info("Found {} potential duplicates during registration for first name={}, last name={}", new Object[] { orcidProfiles.size(), firstName, lastName });
        return orcidProfiles;

    }

    @RequestMapping(value = "/oauth-signup", method = RequestMethod.POST)
    public ModelAndView sendOAuthRegistration(HttpServletRequest request, HttpServletResponse response, LoginForm loginForm,
            @ModelAttribute("oAuthRegistrationForm") @Valid RegistrationForm oAuthRegistrationForm, BindingResult bindingResult) {
        String email = oAuthRegistrationForm.getEmail();
        String sessionId = request.getSession() == null ? null : request.getSession().getId();
        LOGGER.info("User with email={}, sessionid={} has asked to register during oauth", email, sessionId);
        validateEmailAddress(email, request, bindingResult);
        if (bindingResult.hasErrors()) {
            LOGGER.info("Failed validation on registration page during oauth for email={}, sessionid={}, with errors={}", new Object[] { email, sessionId,
                    bindingResult.getAllErrors() });
            ModelAndView erroredView = new ModelAndView("oauth_login");
            erroredView.addObject("loginForm", loginForm);
            erroredView.addAllObjects(bindingResult.getModel());
            return erroredView;
        }

        request.getSession().setAttribute("password", oAuthRegistrationForm.getPassword());
        List<OrcidProfile> potentialDuplicates = findPotentialDuplicatesByFirstNameLastName(oAuthRegistrationForm.getGivenNames(), oAuthRegistrationForm.getFamilyName());
        if (!potentialDuplicates.isEmpty()) {
            ModelAndView duplicateView = new ModelAndView("oauth_duplicate_researcher");
            duplicateView.addObject("potentialDuplicates", potentialDuplicates);
            duplicateView.addObject("oAuthRegistrationForm", oAuthRegistrationForm);
            return duplicateView;
        }

        return completeOAuthRegistration(request, response, oAuthRegistrationForm);
    }

    @RequestMapping(value = "/oauth-complete-signup", method = RequestMethod.POST)
    public ModelAndView completeOAuthRegistration(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("oAuthRegistrationForm") RegistrationForm oAuthRegistrationForm) {

        OrcidProfile orcidProfile = oAuthRegistrationForm.toOrcidProfile();
        orcidProfile.setPassword((String) request.getSession().getAttribute("password"));
        createMinimalRegistrationAndLogUserIn(request, orcidProfile);
        request.getSession().setAttribute("password", null);
        // Flash attribute doesn't seem to work here, so use session directly
        request.getSession().setAttribute("justRegistered", true);

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String redirectUrl = (savedRequest != null ? savedRequest.getRedirectUrl() : null);
        LOGGER.debug("Saved url after OAuth registration is: " + redirectUrl);
        return new ModelAndView(redirectUrl == null ? "redirect:/my-orcid" : "redirect:" + redirectUrl);
    }

    private void createMinimalRegistrationAndLogUserIn(HttpServletRequest request, OrcidProfile profileToSave) {
        URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
        String password = profileToSave.getPassword();
        String sessionId = request.getSession() == null ? null : request.getSession().getId();
        UsernamePasswordAuthenticationToken token = null;
        String email = profileToSave.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        try {
            LOGGER.info("About to create profile from registration email={}, sessionid={}", email, sessionId);
            profileToSave = registrationManager.createMinimalRegistration(profileToSave, uri);
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
