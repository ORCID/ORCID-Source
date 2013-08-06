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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.utils.SolrFieldWeight;
import org.orcid.core.utils.SolrQueryBuilder;
import org.orcid.frontend.web.forms.AddDelegateForm;
import org.orcid.frontend.web.forms.ChangePasswordForm;
import org.orcid.frontend.web.forms.ChangePersonalInfoForm;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.CurrentAffiliationsForm;
import org.orcid.frontend.web.forms.ManagePasswordOptionsForm;
import org.orcid.frontend.web.forms.PastInstitutionsForm;
import org.orcid.frontend.web.forms.PreferencesForm;
import org.orcid.frontend.web.forms.SearchForDelegatesForm;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.EncryptedSecurityAnswer;
import org.orcid.jaxb.model.message.GivenPermissionBy;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.pojo.ChangePassword;
import org.orcid.pojo.SecurityQuestion;
import org.orcid.pojo.ajaxForm.Emails;
import org.orcid.pojo.ajaxForm.Errors;
import org.orcid.utils.OrcidWebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import schema.constants.SolrConstants;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 22/02/2012
 */
@Controller("manageProfileController")
@RequestMapping(value = { "/account", "/manage" })
public class ManageProfileController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageProfileController.class);

    /*
     * session attribute that is used to see if we should check and notify the
     * user if their primary email ins't verified.
     */
    public static String CHECK_EMAIL_VALIDATED = "CHECK_EMAIL_VALIDATED";

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private ProfileKeywordManager profileKeywordManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }

    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setResearcherUrlManager(ResearcherUrlManager researcherUrlManager) {
        this.researcherUrlManager = researcherUrlManager;
    }

    public void setProfileKeywordManager(ProfileKeywordManager profileKeywordManager) {
        this.profileKeywordManager = profileKeywordManager;
    }

    public void setOtherNameManager(OtherNameManager otherNameManager) {
        this.otherNameManager = otherNameManager;
    }

    @ModelAttribute("visibilities")
    public Map<String, String> retrieveVisibilitiesAsMap() {
        Map<String, String> visibilities = new LinkedHashMap<String, String>();
        visibilities.put(Visibility.PUBLIC.value(), "Public");
        visibilities.put(Visibility.LIMITED.value(), "Limited");
        visibilities.put(Visibility.PRIVATE.value(), "Private");
        return visibilities;
    }

    @ModelAttribute("externalIdentifierRefData")
    public Map<String, String> retrieveExternalIdentifierRefData() {
        Map<String, String> types = new HashMap<String, String>();
        for (WorkExternalIdentifierType type : WorkExternalIdentifierType.values()) {
            types.put(type.value(), buildInternationalizationKey(WorkExternalIdentifierType.class, type.value()));
        }
        return types;
    }

    @RequestMapping
    public ModelAndView manageProfile(@RequestParam(value = "activeTab", required = false) String activeTab) {
        // XXX Use T2 API
        String tab = activeTab == null ? "profile-tab" : activeTab;
        ModelAndView mav = rebuildManageView(tab);
        return mav;
    }

    @RequestMapping(value = "/preferences", method = RequestMethod.POST)
    public ModelAndView updatePreferences(@ModelAttribute("preferencesForm") PreferencesForm preferencesForm, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/account?activeTab=options-tab");
        LOGGER.debug("Got preferences: {}", preferencesForm);
        // XXX Use T2 API
        OrcidProfile updatedProfile = orcidProfileManager.updatePreferences(preferencesForm.getOrcidProfile());
        getCurrentUser().setEffectiveProfile(updatedProfile);
        redirectAttributes.addFlashAttribute("optionsSaved", true);
        return mav;
    }

    @RequestMapping(value = "/affiliations", method = RequestMethod.POST)
    public ModelAndView updateAffiliations(@ModelAttribute("currentAffiliationsForm") @Valid CurrentAffiliationsForm currentAffiliationsForm, BindingResult bindingResult) {

        return buildAffiliationsViewFromUpdate(currentAffiliationsForm, bindingResult);
    }

    @RequestMapping(value = "/delete-affiliations", method = RequestMethod.POST)
    public ModelAndView deleteAffiliations(ModelAndView mav) {
        orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid());

        mav.setViewName("redirect:/account?activeTab=affiliations-tab");
        return mav;
    }

    @RequestMapping(value = "/add-past-institution", method = RequestMethod.POST)
    public ModelAndView addPastAffiliations(@ModelAttribute("pastInstitutionsForm") @Valid PastInstitutionsForm pastInstitutionsForm, BindingResult bindingResult,
            ModelAndView mav, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            mav = rebuildManageView("affiliations-tab");
            mav.addAllObjects(bindingResult.getModel());
            mav.setViewName("manage");
        } else {
            OrcidProfile profile = pastInstitutionsForm.getOrcidProfile();
            profile.setOrcid(new Orcid(getCurrentUserOrcid()));

            mav.setViewName("redirect:/account?activeTab=affiliations-tab");
        }
        return mav;
    }

    @RequestMapping(value = "/update-past-institution-information", method = RequestMethod.POST)
    public ModelAndView updatePastAffiliationInformation(@ModelAttribute("pastInstitutionsForm") PastInstitutionsForm pastInstitutionsForm, @RequestParam String action) {
        ModelAndView mav = new ModelAndView("redirect:/account?activeTab=affiliations-tab");

        if ("remove".equals(action)) {
            deletePastAffiliations(pastInstitutionsForm);
        } else if ("update-past-visiblility".equals(action)) {
            updatePastAffiliationsVisibility(pastInstitutionsForm);
        }

        return mav;
    }

    private void deletePastAffiliations(PastInstitutionsForm pastInstitutionsForm) {

    }

    private void updatePastAffiliationsVisibility(PastInstitutionsForm pastInstitutionsForm) {

    }

    private ModelAndView buildAffiliationsViewFromUpdate(@ModelAttribute @Valid CurrentAffiliationsForm currentAffiliationsForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView defaultView = rebuildManageView("affiliations-tab");
            defaultView.addAllObjects(bindingResult.getModel());
            return defaultView;
        }

        LOGGER.debug("Got current affiliations: {}", currentAffiliationsForm);
        // XXX Use T2 API
        orcidProfileManager.updateAffiliations(currentAffiliationsForm.getOrcidProfile());
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(currentAffiliationsForm.getOrcid());
        getCurrentUser().setEffectiveProfile(profile);
        ModelAndView affiliationsView = manageProfile("affiliations-tab");
        affiliationsView.addObject("affiliationsSuccessfullyUpdated", true);
        return affiliationsView;
    }

    @RequestMapping(value = "/manage-password-info", method = RequestMethod.GET)
    public ModelAndView viewPasswordInfo() {
        ModelAndView defaultView = rebuildManageView("password-tab");
        ManagePasswordOptionsForm passwordOptionForUser = populateManagePasswordFormFromUserInfo();
        defaultView.addObject("passwordOptionsForm", passwordOptionForUser);
        return defaultView;
    }

    @RequestMapping(value = "/manage-password-info", method = RequestMethod.POST)
    public ModelAndView updatePasswordInformation(@ModelAttribute @Valid ManagePasswordOptionsForm passwordOptionsForm, BindingResult bindingResult) {

        // if errors - redirect to the populate view - bind where we can see
        // errors
        if (bindingResult.hasErrors()) {
            ModelAndView erroredView = rebuildManageView("password-tab");
            erroredView.addAllObjects(bindingResult.getModel());
            return erroredView;
        }

        // if not retrieve the user credentials, and set the new security
        // details
        OrcidProfile profile = getCurrentUser().getEffectiveProfile();
        profile.setPassword(passwordOptionsForm.getPassword());
        profile.setVerificationCode(passwordOptionsForm.getVerificationNumber());
        profile.setSecurityQuestionAnswer(passwordOptionsForm.getSecurityQuestionAnswer());
        profile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(passwordOptionsForm.getSecurityQuestionId()));

        orcidProfileManager.updatePasswordInformation(profile);
        // return the view as if an HTTP get now that the info has persisted ok
        ModelAndView mav = rebuildManageView("password-tab");
        mav.addObject("activeTab", "password-tab");
        mav.addObject("passwordOptionsSaved", true);
        return mav;
    }

    @RequestMapping(value = "/search-for-delegates", method = RequestMethod.GET)
    public ModelAndView viewSearchForDelegates() {
        ModelAndView mav = new ModelAndView("search_for_delegates");
        return mav;
    }

    @RequestMapping(value = "/search-for-delegates", method = RequestMethod.POST)
    public ModelAndView searchForDelegates(@RequestParam("searchTerms") String searchTerms) {
        SolrQueryBuilder queryBuilder = new SolrQueryBuilder();
        List<SolrFieldWeight> fields = new ArrayList<SolrFieldWeight>();
        fields.add(new SolrFieldWeight(SolrConstants.GIVEN_NAMES, 2.0f));
        fields.add(new SolrFieldWeight(SolrConstants.FAMILY_NAME, 3.0f));
        fields.add(new SolrFieldWeight(SolrConstants.CREDIT_NAME, 3.0f));
        fields.add(new SolrFieldWeight(SolrConstants.EMAIL_ADDRESS, 4.0f));
        fields.add(new SolrFieldWeight(SolrConstants.TEXT, 0.5f));
        fields.add(new SolrFieldWeight(SolrConstants.AFFILIATE_PRIMARY_INSTITUTION_NAMES, 1.0f));
        queryBuilder.appendEDisMaxQuery(fields);
        queryBuilder.appendValue(searchTerms);
        OrcidProfile orcidProfile = getCurrentUser().getEffectiveProfile();
        List<String> orcidsToExclude = new ArrayList<String>();
        // Exclude myself
        orcidsToExclude.add(orcidProfile.getOrcid().getValue());
        // Exclude profiles I've already delegated to
        Delegation delegation = orcidProfile.getOrcidBio().getDelegation();
        if (delegation != null) {
            GivenPermissionTo givenPermissionTo = delegation.getGivenPermissionTo();
            if (givenPermissionTo != null) {
                for (DelegationDetails delegationDetails : givenPermissionTo.getDelegationDetails()) {
                    orcidsToExclude.add(delegationDetails.getDelegateSummary().getOrcid().getValue());
                }
            }
        }
        queryBuilder.appendNOTCondition(SolrConstants.ORCID, orcidsToExclude);
        // XXX Use T1 API
        OrcidMessage orcidMessage = orcidSearchManager.findOrcidsByQuery(queryBuilder.retrieveQuery());
        OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();
        SearchForDelegatesForm searchForDelegatesForm = new SearchForDelegatesForm(searchResults);
        ModelAndView mav = new ModelAndView("search_for_delegates_results");
        mav.addObject(searchForDelegatesForm);
        return mav;
    }

    @RequestMapping(value = "/confirm-delegate", method = RequestMethod.POST)
    public ModelAndView confirmDelegate(@ModelAttribute("delegateOrcid") String delegateOrcid) {
        OrcidProfile delegateProfile = orcidProfileManager.retrieveOrcidProfile(delegateOrcid);
        ModelAndView mav = new ModelAndView("confirm_delegate");
        mav.addObject("delegateProfile", delegateProfile);
        return mav;
    }

    @RequestMapping(value = "/add-delegate")
    public ModelAndView addDelegate(@ModelAttribute AddDelegateForm addDelegateForm) {
        OrcidProfile profile = addDelegateForm.getOrcidProfile(getCurrentUserOrcid());
        // XXX User T2 API
        getCurrentUser().setEffectiveProfile(orcidProfileManager.addDelegates(profile));
        ModelAndView mav = new ModelAndView("redirect:/account?activeTab=delegation-tab");
        return mav;
    }

    @RequestMapping(value = "/revoke-delegate", method = RequestMethod.POST)
    public ModelAndView revokeDelegate(@RequestParam String receiverOrcid) {
        String giverOrcid = getCurrentUserOrcid();
        getCurrentUser().setEffectiveProfile(orcidProfileManager.revokeDelegate(giverOrcid, receiverOrcid));
        ModelAndView mav = new ModelAndView("redirect:/account?activeTab=delegation-tab");
        return mav;
    }

    @RequestMapping(value = "/revoke-delegate-from-summary-view", method = RequestMethod.GET)
    public ModelAndView revokeDelegateFromSummaryView(@RequestParam("orcid") String receiverOrcid) {
        String giverOrcid = getCurrentUserOrcid();
        getCurrentUser().setEffectiveProfile(orcidProfileManager.revokeDelegate(giverOrcid, receiverOrcid));
        ModelAndView mav = new ModelAndView("redirect:/account/view-account-settings");
        return mav;
    }

    @RequestMapping(value = "/revoke-application")
    public ModelAndView revokeApplication(@RequestParam("applicationOrcid") String applicationOrcid,
            @RequestParam(value = "scopePaths", required = false, defaultValue = "") String[] scopePaths) {
        String userOrcid = getCurrentUserOrcid();
        getCurrentUser().setEffectiveProfile(
                orcidProfileManager.revokeApplication(userOrcid, applicationOrcid, ScopePathType.getScopesFromStrings(Arrays.asList(scopePaths))));
        ModelAndView mav = new ModelAndView("redirect:/account?activeTab=application-tab");
        return mav;
    }

    @RequestMapping(value = "/revoke-application-from-summary-view", method = RequestMethod.GET)
    public ModelAndView revokeApplicationFromSummaryView(@RequestParam("applicationOrcid") String applicationOrcid, @RequestParam("scopePaths") String[] scopePaths) {
        String userOrcid = getCurrentUserOrcid();
        orcidProfileManager.revokeApplication(userOrcid, applicationOrcid, ScopePathType.getScopesFromStrings(Arrays.asList(scopePaths)));
        ModelAndView mav = new ModelAndView("redirect:/account/view-account-settings");
        return mav;
    }

    @RequestMapping(value = "/switch-user", method = RequestMethod.POST)
    public ModelAndView switchUser(HttpServletRequest request, @RequestParam("giverOrcid") String giverOrcid) {
        refreshCurrentUserProfile();
        OrcidProfileUserDetails userDetails = getCurrentUser();
        // Check permissions!
        if (isInDelegationMode()) {
            // If already in delegation mode, check that is switching back to
            // current user
            if (!getRealUserOrcid().equals(giverOrcid)) {
                throw new AccessDeniedException("You are not allowed to switch back to that user");
            }
        } else {
            // If not yet in delegation mode, then check that the real user has
            // permission to become the giver
            if (!hasDelegatePermission(userDetails, giverOrcid)) {
                throw new AccessDeniedException("You are not allowed to switch to that user");
            }
        }
        getCurrentUser().switchDelegationMode(orcidProfileManager.retrieveOrcidProfile(giverOrcid));
        request.getSession().removeAttribute(WORKS_RESULTS_ATTRIBUTE);
        ModelAndView mav = new ModelAndView("redirect:/my-orcid");
        return mav;
    }

    @RequestMapping(value = "/admin-switch-user", method = RequestMethod.GET)
    public ModelAndView adminSwitchUser(HttpServletRequest request, @RequestParam("orcid") String targetOrcid) {
        refreshCurrentUserProfile();
        OrcidProfileUserDetails userDetails = getCurrentUser();
        // Check permissions!
        if (!OrcidType.ADMIN.equals(userDetails.getRealProfile().getType())) {
            throw new AccessDeniedException("You are not allowed to switch to that user");
        }
        getCurrentUser().switchDelegationMode(orcidProfileManager.retrieveOrcidProfile(targetOrcid));
        request.getSession().removeAttribute(WORKS_RESULTS_ATTRIBUTE);
        ModelAndView mav = new ModelAndView("redirect:/my-orcid");
        return mav;
    }

    private boolean hasDelegatePermission(OrcidProfileUserDetails userDetails, String giverOrcid) {
        Delegation delegation = userDetails.getRealProfile().getOrcidBio().getDelegation();
        if (delegation != null) {
            GivenPermissionBy givenPermissionBy = delegation.getGivenPermissionBy();
            if (givenPermissionBy != null) {
                for (DelegationDetails delegationDetails : givenPermissionBy.getDelegationDetails()) {
                    if (delegationDetails.getDelegateSummary().getOrcid().getValue().equals(giverOrcid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected ModelAndView rebuildManageView(String activeTab) {
        ModelAndView mav = new ModelAndView("manage");
        mav.addObject("showPrivacy", true);
        OrcidProfile profile = getCurrentUserAndRefreshIfNecessary().getEffectiveProfile();
        mav.addObject("managePasswordOptionsForm", populateManagePasswordFormFromUserInfo());
        mav.addObject("preferencesForm", new PreferencesForm(profile));
        mav.addObject("profile", profile);
        mav.addObject("activeTab", activeTab);
        mav.addObject("securityQuestions", securityQuestionManager.retrieveSecurityQuestionsAsMap());
        CurrentAffiliationsForm currentAffiliationsForm = new CurrentAffiliationsForm(profile);
        mav.addObject("pastInstitutionsForm", new PastInstitutionsForm(profile));
        mav.addObject("currentAffiliationsForm", currentAffiliationsForm);
        return mav;
    }

    private ManagePasswordOptionsForm populateManagePasswordFormFromUserInfo() {
        OrcidProfileUserDetails currentUserDetails = getCurrentUser();
        OrcidProfile profile = currentUserDetails.getEffectiveProfile();

        // TODO - placeholder just to test the retrieve etc..replace with only
        // fields that we will populate
        // password fields are never populated
        OrcidProfile unecryptedProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcid().getValue());
        ManagePasswordOptionsForm managePasswordOptionsForm = new ManagePasswordOptionsForm();
        managePasswordOptionsForm.setVerificationNumber(unecryptedProfile.getVerificationCode());
        managePasswordOptionsForm.setSecurityQuestionAnswer(unecryptedProfile.getSecurityQuestionAnswer());
        Integer securityQuestionId = null;
        SecurityDetails securityDetails = unecryptedProfile.getOrcidInternal().getSecurityDetails();
        // TODO - confirm that security details aren't null and that we can
        // change schema to be an int for security
        // questions field
        if (securityDetails != null) {
            securityQuestionId = securityDetails.getSecurityQuestionId() != null ? new Integer((int) securityDetails.getSecurityQuestionId().getValue()) : null;
        }

        managePasswordOptionsForm.setSecurityQuestionId(securityQuestionId);

        return managePasswordOptionsForm;
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

    @ModelAttribute("colleagueConfirmedRegistrationMap")
    public Map<String, String> getColleagueConfirmedRegistrationMap() {
        Map<String, String> otherNewsMap = new HashMap<String, String>();
        otherNewsMap.put("sendMe", "");
        return otherNewsMap;
    }

    @ModelAttribute("securityQuestions")
    public Map<String, String> getSecurityQuestions() {
        securityQuestionManager.retrieveSecurityQuestionsAsMap();//
        return securityQuestionManager.retrieveSecurityQuestionsAsMap();
    }

    @RequestMapping(value = "/view-account-settings", method = RequestMethod.GET)
    public String viewAccountSettings() {
        // Defunct page, redirect to main account page in case of bookmarks.
        return "redirect:/account";
    }

    @RequestMapping(value = { "/password", "/change-password" }, method = RequestMethod.GET)
    public ModelAndView viewChangePassword() {
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        ModelAndView changePasswordView = new ModelAndView("change_password");
        changePasswordView.addObject(changePasswordForm);
        return changePasswordView;
    }

    @RequestMapping(value = { "/security-question", "/change-security-question" }, method = RequestMethod.GET)
    public ModelAndView viewChangeSecurityQuestion() {
        return populateChangeSecurityDetailsViewFromUserProfile(new ChangeSecurityQuestionForm());
    }

    @RequestMapping(value = { "/security-question", "/change-security-question" }, method = RequestMethod.POST)
    public ModelAndView updateWithChangedSecurityQuestion(@ModelAttribute("changeSecurityQuestionForm") @Valid ChangeSecurityQuestionForm changeSecurityQuestionForm,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView changeSecurityDetailsView = new ModelAndView("change_security_question");
            changeSecurityDetailsView.addAllObjects(bindingResult.getModel());
            changeSecurityDetailsView.addObject(changeSecurityQuestionForm);
            return changeSecurityDetailsView;
        }
        OrcidProfile profile = getCurrentUser().getEffectiveProfile();
        profile.setSecurityQuestionAnswer(changeSecurityQuestionForm.getSecurityQuestionAnswer());
        profile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(changeSecurityQuestionForm.getSecurityQuestionId()));
        orcidProfileManager.updatePasswordSecurityQuestionsInformation(profile);
        ModelAndView changeSecurityDetailsView = populateChangeSecurityDetailsViewFromUserProfile(changeSecurityQuestionForm);
        changeSecurityDetailsView.addObject("securityQuestionSaved", true);
        return changeSecurityDetailsView;

    }

    @RequestMapping(value = "/security-question.json", method = RequestMethod.GET)
    public @ResponseBody
    SecurityQuestion getSecurityQuestionJson(HttpServletRequest request) {
        OrcidProfile profile = getCurrentUser().getEffectiveProfile();
        SecurityDetails sd = profile.getOrcidInternal().getSecurityDetails();
        SecurityQuestionId securityQuestionId = sd.getSecurityQuestionId();
        EncryptedSecurityAnswer encryptedSecurityAnswer = sd.getEncryptedSecurityAnswer();

        if (securityQuestionId == null) {
            sd.getSecurityQuestionId();
            securityQuestionId = new SecurityQuestionId();
        }

        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setSecurityQuestionId(securityQuestionId.getValue());

        if (encryptedSecurityAnswer != null) {
            securityQuestion.setSecurityAnswer(encryptionManager.decryptForInternalUse(encryptedSecurityAnswer.getContent()));
        }

        return securityQuestion;
    }

    @RequestMapping(value = "/security-question.json", method = RequestMethod.POST)
    public @ResponseBody
    SecurityQuestion setSecurityQuestionJson(HttpServletRequest request, @RequestBody SecurityQuestion securityQuestion) {
        List<String> errors = new ArrayList<String>();
        if (securityQuestion.getSecurityQuestionId() != 0 && (securityQuestion.getSecurityAnswer() == null || securityQuestion.getSecurityAnswer().trim() == ""))
            errors.add(getMessage("manage.pleaseProvideAnAnswer"));
        
        // If the security question is empty, clean the security answer field
        if(securityQuestion.getSecurityQuestionId() == 0)
        	securityQuestion.setSecurityAnswer(new String());
        
        if (errors.size() == 0) {
            OrcidProfile profile = getCurrentUser().getEffectiveProfile();
            if (profile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId() == null)
                profile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId());
            profile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().setValue(securityQuestion.getSecurityQuestionId());

            if (profile.getOrcidInternal().getSecurityDetails().getEncryptedSecurityAnswer() == null)
                profile.getOrcidInternal().getSecurityDetails().setEncryptedSecurityAnswer(new EncryptedSecurityAnswer());
            profile.setSecurityQuestionAnswer(securityQuestion.getSecurityAnswer());
            orcidProfileManager.updatePasswordSecurityQuestionsInformation(profile);
            getCurrentUser().setEffectiveProfile(profile);
            errors.add(getMessage("manage.securityQuestionUpdated"));
        }

        securityQuestion.setErrors(errors);
        return securityQuestion;
    }

    @RequestMapping(value = "/preferences.json", method = RequestMethod.GET)
    public @ResponseBody
    Preferences getDefaultPreference(HttpServletRequest request) {
        OrcidProfile profile = getCurrentUserAndRefreshIfNecessary().getEffectiveProfile();
        profile.getOrcidInternal().getPreferences();
        return profile.getOrcidInternal().getPreferences() != null ? profile.getOrcidInternal().getPreferences() : new Preferences();
    }

    @RequestMapping(value = "/preferences.json", method = RequestMethod.POST)
    public @ResponseBody
    Preferences setDefaultPreference(HttpServletRequest request, @RequestBody Preferences preferences) {
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid());
        profile.getOrcidInternal().setPreferences(preferences);
        OrcidProfile updatedProfile = orcidProfileManager.updateOrcidProfile(profile);
        return updatedProfile.getOrcidInternal().getPreferences();
    }

    private ModelAndView populateChangeSecurityDetailsViewFromUserProfile(ChangeSecurityQuestionForm changeSecurityQuestionForm) {
        ModelAndView changeSecurityDetailsView = new ModelAndView("change_security_question");

        Integer securityQuestionId = null;

        SecurityDetails securityDetails = getCurrentUser().getEffectiveProfile().getOrcidInternal().getSecurityDetails();
        if (securityDetails != null) {
            securityQuestionId = securityDetails.getSecurityQuestionId() != null ? new Integer((int) securityDetails.getSecurityQuestionId().getValue()) : null;

        }

        String encryptedSecurityAnswer = securityDetails != null && securityDetails.getEncryptedSecurityAnswer() != null ? securityDetails.getEncryptedSecurityAnswer()
                .getContent() : null;
        String securityAnswer = StringUtils.isNotBlank(encryptedSecurityAnswer) ? encryptionManager.decryptForInternalUse(encryptedSecurityAnswer) : "";

        changeSecurityQuestionForm.setSecurityQuestionId(securityQuestionId);
        changeSecurityQuestionForm.setSecurityQuestionAnswer(securityAnswer);

        changeSecurityDetailsView.addObject("changeSecurityQuestionForm", changeSecurityQuestionForm);
        return changeSecurityDetailsView;
    }

    @RequestMapping(value = { "/password", "/change-password" }, method = RequestMethod.POST)
    public ModelAndView updateWithChangedPassword(@ModelAttribute @Valid ChangePasswordForm passwordOptionsForm, BindingResult bindingResult) {

        ModelAndView changePasswordView = new ModelAndView("change_password");
        if (bindingResult.hasErrors()) {
            changePasswordView.addAllObjects(bindingResult.getModel());
            return changePasswordView;
        }

        if (!encryptionManager.hashMatches(passwordOptionsForm.getOldPassword(), getCurrentUser().getPassword())) {
            String[] codes = { "orcid.frontend.change.password.current_password_incorrect" };
            bindingResult.addError(new FieldError("oldPassword", "oldPassword", null, false, codes, null, "Old Password Invalid"));
            changePasswordView.addAllObjects(bindingResult.getModel());
            return changePasswordView;

        }

        OrcidProfile profile = getCurrentUser().getEffectiveProfile();
        profile.setPassword(passwordOptionsForm.getPassword());
        orcidProfileManager.updatePasswordInformation(profile);
        // return the view as if an HTTP get now that the info has persisted ok
        changePasswordView.addObject("passwordOptionsSaved", true);
        return changePasswordView;
    }

    @RequestMapping(value = { "/change-password.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ChangePassword getChangedPasswordJson(HttpServletRequest request) {
        ChangePassword p = new ChangePassword();
        return p;
    }

    @RequestMapping(value = { "/change-password.json" }, method = RequestMethod.POST)
    public @ResponseBody
    ChangePassword changedPasswordJson(HttpServletRequest request, @RequestBody ChangePassword cp) {
        List<String> errors = new ArrayList<String>();

        if (cp.getPassword() == null || !cp.getPassword().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            errors.add(getMessage("NotBlank.registrationForm.confirmedPassword"));
        } else if (!cp.getPassword().equals(cp.getRetypedPassword())) {
            errors.add(getMessage("FieldMatch.registrationForm"));
        }

        if (cp.getOldPassword() == null || !encryptionManager.hashMatches(cp.getOldPassword(), getCurrentUser().getPassword())) {
            errors.add(getMessage("orcid.frontend.change.password.current_password_incorrect"));
        }

        if (errors.size() == 0) {
            OrcidProfile profile = getCurrentUser().getEffectiveProfile();
            profile.setPassword(cp.getPassword());
            orcidProfileManager.updatePasswordInformation(profile);
            cp = new ChangePassword();
            errors.add(getMessage("orcid.frontend.change.password.change.successfully"));
        }
        cp.setErrors(errors);
        return cp;
    }

    @RequestMapping(value = { "deactivate-orcid", "/view-deactivate-orcid-account" }, method = RequestMethod.GET)
    public ModelAndView viewDeactivateOrcidAccount() {
        ModelAndView deactivateOrcidView = new ModelAndView("deactivate_orcid");
        return deactivateOrcidView;
    }

    @RequestMapping(value = "/confirm-deactivate-orcid", method = RequestMethod.GET)
    public ModelAndView confirmDeactivateOrcidAccount(HttpServletRequest request) {
        getCurrentUser().setRealProfile(orcidProfileManager.deactivateOrcidProfile(getCurrentUser().getEffectiveProfile()));
        ModelAndView deactivateOrcidView = new ModelAndView("redirect:/signout#deactivated");
        return deactivateOrcidView;
    }

    @RequestMapping(value = "/manage-bio-settings", method = RequestMethod.GET)
    public ModelAndView viewEditBio(HttpServletRequest request) {
        ModelAndView manageBioView = new ModelAndView("manage_bio_settings");
        OrcidProfile profile = getCurrentUserAndRefreshIfNecessary().getEffectiveProfile();
        ChangePersonalInfoForm changePersonalInfoForm = new ChangePersonalInfoForm(profile);
        manageBioView.addObject("changePersonalInfoForm", changePersonalInfoForm);
        return manageBioView;
    }

    @RequestMapping(value = "/verifyEmail.json", method = RequestMethod.GET)
    public @ResponseBody
    Errors verifyEmailJson(HttpServletRequest request, @RequestParam("email") String email) {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        String primaryEmail = currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        if (primaryEmail.equals(email))
            request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);

        URI baseUri = OrcidWebUtils.getServerUriWithContextPath(request);
        notificationManager.sendVerificationEmail(currentProfile, baseUri, email);
        return new Errors();
    }

    @RequestMapping(value = "/delayVerifyEmail.json", method = RequestMethod.GET)
    public @ResponseBody
    Errors delayVerifyEmailJson(HttpServletRequest request) {
        request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
        return new Errors();
    }

    @RequestMapping(value = "/send-deactivate-account.json", method = RequestMethod.GET)
    public @ResponseBody
    Email startDeactivateOrcidAccountJson(HttpServletRequest request) {
        URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        Email email = currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        notificationManager.sendOrcidDeactivateEmail(currentProfile, uri);
        return email;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/emails.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Emails getEmailsJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        Emails emails = new org.orcid.pojo.ajaxForm.Emails();
        emails.setEmails((List<org.orcid.pojo.Email>) (Object) currentProfile.getOrcidBio().getContactDetails().getEmail());
        return emails;
    }

    @RequestMapping(value = "/addEmail.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.Email addEmailsJson(HttpServletRequest request, @RequestBody org.orcid.pojo.Email email) {
        String newPrime = null;
        String oldPrime = null;
        List<String> emailErrors = new ArrayList<String>();

        // clear errros
        email.setErrors(new ArrayList<String>());

        // if blank
        if (email.getValue() == null || email.getValue().trim().equals("")) {
            emailErrors.add(getMessage("Email.personalInfoForm.email"));
        }
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        List<Email> emails = currentProfile.getOrcidBio().getContactDetails().getEmail();

        MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
        // make sure there are no dups
        validateEmailAddress(email.getValue(), false, request, mbr);

        for (ObjectError oe : mbr.getAllErrors()) {
            emailErrors.add(getMessage(oe.getCode(), email.getValue()));
        }
        email.setErrors(emailErrors);

        if (emailErrors.size() == 0) {
            if (email.isPrimary()) {
                for (Email curEmail : emails) {
                    if (curEmail.isPrimary())
                        oldPrime = curEmail.getValue();
                    curEmail.setPrimary(false);
                }
                newPrime = email.getValue();
            }

            emails.add(email);
            currentProfile.getOrcidBio().getContactDetails().setEmail(emails);
            email.setSource(getRealUserOrcid());
            emailManager.addEmail(currentProfile.getOrcid().getValue(), email);

            // send verifcation email for new address
            URI baseUri = OrcidWebUtils.getServerUriWithContextPath(request);
            notificationManager.sendVerificationEmail(currentProfile, baseUri, email.getValue());

            // if primary also send change notification.
            if (newPrime != null && !newPrime.equalsIgnoreCase(oldPrime)) {
                request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
                notificationManager.sendEmailAddressChangedNotification(currentProfile, new Email(oldPrime), baseUri);
            }

        }
        return email;
    }

    @RequestMapping(value = "/deleteEmail.json", method = RequestMethod.DELETE)
    public @ResponseBody
    org.orcid.pojo.Email deleteEmailJson(HttpServletRequest request, @RequestBody org.orcid.pojo.Email email) {
        List<String> emailErrors = new ArrayList<String>();

        // clear errros
        email.setErrors(new ArrayList<String>());

        // if blank
        if (email.getValue() == null || email.getValue().trim().equals("")) {
            emailErrors.add(getMessage("Email.personalInfoForm.email"));
        }
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        List<Email> emails = currentProfile.getOrcidBio().getContactDetails().getEmail();

        if (email.isPrimary()) {
            emailErrors.add(getMessage("manage.email.primaryEmailDeletion"));
        }

        email.setErrors(emailErrors);

        if (emailErrors.size() == 0) {
            Iterator<Email> emailIterator = emails.iterator();
            while (emailIterator.hasNext()) {
                Email nextEmail = emailIterator.next();
                if (nextEmail.getValue().equals(email.getValue())) {
                    emailIterator.remove();
                }
            }
            currentProfile.getOrcidBio().getContactDetails().setEmail(emails);
            emailManager.removeEmail(currentProfile.getOrcid().getValue(), email.getValue());
        }
        return email;
    }

    @RequestMapping(value = "/emails.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Emails postEmailsJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ajaxForm.Emails emails) {
        org.orcid.pojo.Email newPrime = null;
        org.orcid.pojo.Email oldPrime = null;
        List<String> allErrors = new ArrayList<String>();

        for (org.orcid.pojo.Email email : emails.getEmails()) {

            MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
            validateEmailAddress(email.getValue(), request, mbr);
            List<String> emailErrors = new ArrayList<String>();
            for (ObjectError oe : mbr.getAllErrors()) {
                String msg = getMessage(oe.getCode(), email.getValue());
                emailErrors.add(getMessage(oe.getCode(), email.getValue()));
                allErrors.add(msg);
            }
            email.setErrors(emailErrors);
            if (email.isPrimary())
                newPrime = email;
        }

        if (newPrime == null) {
            allErrors.add("A Primary Email Must be selected");
        }

        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        if (currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null)
            oldPrime = new org.orcid.pojo.Email(currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());

        emails.setErrors(allErrors);
        if (allErrors.size() == 0) {
            currentProfile.getOrcidBio().getContactDetails().setEmail((List<Email>) (Object) emails.getEmails());
            emailManager.updateEmails(currentProfile.getOrcid().getValue(), currentProfile.getOrcidBio().getContactDetails().getEmail());
            if (newPrime != null && !newPrime.getValue().equalsIgnoreCase(oldPrime.getValue())) {
                URI baseUri = OrcidWebUtils.getServerUriWithContextPath(request);
                notificationManager.sendEmailAddressChangedNotification(currentProfile, new Email(oldPrime.getValue()), baseUri);
                if (!newPrime.isVerified()) {
                    notificationManager.sendVerificationEmail(currentProfile, baseUri, newPrime.getValue());
                    request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
                }
            }
        }
        return emails;
    }

    @RequestMapping(value = "/save-bio-settings", method = RequestMethod.POST)
    public ModelAndView saveEditedBio(HttpServletRequest request, @Valid @ModelAttribute("changePersonalInfoForm") ChangePersonalInfoForm changePersonalInfoForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView manageBioView = new ModelAndView("redirect:manage-bio-settings");

        for (String keyword : changePersonalInfoForm.getKeywordsAsList()) {
            if (keyword.length() > ChangePersonalInfoForm.KEYWORD_MAX_LEN) {
                bindingResult.rejectValue("keywordsDelimited", "Length.changePersonalInfoForm.keywordsDelimited");
                break;
            }
        }

        if (bindingResult.hasErrors()) {
            ModelAndView erroredView = new ModelAndView("manage_bio_settings");

            // If an error happens and the user doesnt have any website,
            // the privacy selector for websites dissapears.
            // In order to fix this, if the ChangePersonalInfoForm doesnt have
            // any researcher url, we add a new one with an empty list, which
            // is different than null ResearcherUrls
            Map<String, Object> model = bindingResult.getModel();

            if (changePersonalInfoForm.getSavedResearcherUrls() == null) {
                changePersonalInfoForm.setSavedResearcherUrls(new ResearcherUrls());
            }

            model.put("changePersonalInfoForm", changePersonalInfoForm);

            erroredView.addAllObjects(bindingResult.getModel());
            return erroredView;
        }

        OrcidProfile profile = getCurrentUser().getEffectiveProfile();
        // Update profile with values that comes from user request
        changePersonalInfoForm.mergeOrcidBioDetails(profile);

        // Update profile on database
        profileEntityManager.updateProfile(profile);

        String orcid = profile.getOrcid().getValue();

        // Update other names on database
        OtherNames otherNames = profile.getOrcidBio().getPersonalDetails().getOtherNames();
        otherNameManager.updateOtherNames(orcid, otherNames);

        // Update keywords on database
        Keywords keywords = profile.getOrcidBio().getKeywords();
        profileKeywordManager.updateProfileKeyword(orcid, keywords);

        // Update researcher urls on database
        ResearcherUrls researcherUrls = profile.getOrcidBio().getResearcherUrls();
        boolean hasErrors = researcherUrlManager.updateResearcherUrls(orcid, researcherUrls);
        // TODO: The researcherUrlManager will not store any duplicated
        // researcher url on database,
        // however there is no way to tell the controller that some of the
        // researcher urls were not
        // saved, so, if an error occurs, we need to reload researcher ids from
        // database and update
        // cached profile. A more efficient way to fix this might be used.
        if (hasErrors) {
            ResearcherUrls upToDateResearcherUrls = getUpToDateResearcherUrls(orcid, researcherUrls.getVisibility());
            profile.getOrcidBio().setResearcherUrls(upToDateResearcherUrls);
        }

        // Update cached profile
        getCurrentUser().setEffectiveProfile(profile);

        redirectAttributes.addFlashAttribute("changesSaved", true);
        return manageBioView;
    }

    /**
     * Generate an up to date ResearcherUrls object.
     * 
     * @param orcid
     * @param visibility
     * */
    private ResearcherUrls getUpToDateResearcherUrls(String orcid, Visibility visibility) {
        ResearcherUrls upTodateResearcherUrls = new ResearcherUrls();
        upTodateResearcherUrls.setVisibility(visibility);
        List<ResearcherUrlEntity> upToDateResearcherUrls = researcherUrlManager.getResearcherUrls(orcid);

        for (ResearcherUrlEntity researcherUrlEntity : upToDateResearcherUrls) {
            ResearcherUrl newResearcherUrl = new ResearcherUrl();
            newResearcherUrl.setUrl(new Url(researcherUrlEntity.getUrl()));
            newResearcherUrl.setUrlName(new UrlName(researcherUrlEntity.getUrlName()));
            upTodateResearcherUrls.getResearcherUrl().add(newResearcherUrl);
        }

        return upTodateResearcherUrls;
    }
}
