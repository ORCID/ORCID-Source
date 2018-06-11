package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidSocialManager;
import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.v3.AddressManager;
import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.GivenPermissionToManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.RecordNameUtils;
import org.orcid.core.utils.v3.OrcidIdentifierUtils;
import org.orcid.frontend.web.util.CommonPasswords;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Name;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ChangePassword;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.DeprecateProfile;
import org.orcid.pojo.ManageDelegate;
import org.orcid.pojo.ManageSocialAccount;
import org.orcid.pojo.SecurityQuestion;
import org.orcid.pojo.ajaxForm.AddressForm;
import org.orcid.pojo.ajaxForm.AddressesForm;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.Errors;
import org.orcid.pojo.ajaxForm.NamesForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.stereotype.Controller;
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
 * @author Declan Newman (declan) Date: 22/02/2012
 */
@Controller("manageProfileController")
@RequestMapping(value = { "/account", "/manage" })
public class ManageProfileController extends BaseWorkspaceController {

    private static final String IS_SELF = "isSelf";

    private static final String FOUND = "found";   

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private GivenPermissionToManager givenPermissionToManager;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource
    private OrcidSocialManager orcidSocialManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "addressManagerV3")
    private AddressManager addressManager;

    @Resource(name = "biographyManagerV3")
    private BiographyManager biographyManager;
    
    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManager;
    
    @Resource
    private PreferenceManager preferenceManager;
    
    @Resource
    private OrcidIdentifierUtils orcidIdentifierUtils;
    
    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;
    
    @Resource
    private ProfileLastModifiedAspect profileLastModifiedAspect;
    
    private long getLastModified(String orcid) {
        Date lastModified = profileLastModifiedAspect.retrieveLastModifiedDate(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }
    
    @RequestMapping
    public ModelAndView manageProfile() {
        return new ModelAndView("manage");
    }
    
    @RequestMapping(value = "/search-for-delegate-by-email/{email}/")
    public @ResponseBody Map<String, Boolean> searchForDelegateByEmail(@PathVariable String email) {
        Map<String, Boolean> map = new HashMap<>();
        EmailEntity emailEntity = emailManager.findCaseInsensitive(email);
        if (emailEntity == null) {
            map.put(FOUND, Boolean.FALSE);
            return map;
        } else {
            map.put(FOUND, Boolean.TRUE);
            map.put(IS_SELF, emailEntity.getProfile().getId().equals(getCurrentUserOrcid()));
            return map;
        }
    }    

    @RequestMapping(value = "/delegates.json", method = RequestMethod.GET)
    public @ResponseBody List<DelegateForm>  getDelegates() {
        String currentOrcid = getCurrentUserOrcid();
        return givenPermissionToManagerReadOnly.findByGiver(currentOrcid, getLastModified(currentOrcid));
    }

    @RequestMapping(value = "/addDelegate.json")
    public @ResponseBody ManageDelegate addDelegate(@RequestBody ManageDelegate addDelegate) {
        // Check password
        String password = addDelegate.getPassword();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if (orcidSecurityManager.isPasswordConfirmationRequired()
                && (StringUtils.isBlank(password) || !encryptionManager.hashMatches(password, profile.getEncryptedPassword()))) {
            addDelegate.getErrors().add(getMessage("check_password_modal.incorrect_password"));
            return addDelegate;
        }
        
        givenPermissionToManager.create(getCurrentUserOrcid(), addDelegate.getDelegateToManage());
                                    
        return addDelegate;
    }

    @RequestMapping(value = "/addDelegateByEmail.json")
    public @ResponseBody ManageDelegate addDelegateByEmail(@RequestBody ManageDelegate addDelegate) {
        EmailEntity emailEntity = emailManager.findCaseInsensitive(addDelegate.getDelegateEmail());
        addDelegate.setDelegateToManage(emailEntity.getProfile().getId());
        return addDelegate(addDelegate);
    }

    @RequestMapping(value = "/revokeDelegate.json", method = RequestMethod.POST)
    public @ResponseBody ManageDelegate revokeDelegate(@RequestBody ManageDelegate manageDelegate) {
        // Check password
        String password = manageDelegate.getPassword();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if (orcidSecurityManager.isPasswordConfirmationRequired()
                && (StringUtils.isBlank(password) || !encryptionManager.hashMatches(password, profile.getEncryptedPassword()))) {
            manageDelegate.getErrors().add(getMessage("check_password_modal.incorrect_password"));
            return manageDelegate;
        }               
        givenPermissionToManager.remove(getCurrentUserOrcid(), manageDelegate.getDelegateToManage());
        return manageDelegate;
    }

    @RequestMapping(value = "/socialAccounts.json", method = RequestMethod.GET)
    public @ResponseBody List<UserconnectionEntity> getSocialAccountsJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        String orcid = getCurrentUserOrcid();
        List<UserconnectionEntity> userConnectionEntities = userConnectionManager.findByOrcid(orcid);
        return userConnectionEntities;
    }

    @RequestMapping(value = "/revokeSocialAccount.json", method = RequestMethod.POST)
    public @ResponseBody ManageSocialAccount revokeSocialAccount(@RequestBody ManageSocialAccount manageSocialAccount) {
        // Check password
        String password = manageSocialAccount.getPassword();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if (orcidSecurityManager.isPasswordConfirmationRequired()
                && (StringUtils.isBlank(password) || !encryptionManager.hashMatches(password, profile.getEncryptedPassword()))) {
            manageSocialAccount.getErrors().add(getMessage("check_password_modal.incorrect_password"));
            return manageSocialAccount;
        }
        userConnectionManager.remove(getEffectiveUserOrcid(), manageSocialAccount.getIdToManage());
        return manageSocialAccount;
    }

    @RequestMapping(value = "/revoke-application", method = RequestMethod.POST)
    public @ResponseBody boolean revokeApplication(@RequestParam("tokenId") String tokenId) {
        String userOrcid = getCurrentUserOrcid();
        profileEntityManager.disableApplication(Long.valueOf(tokenId), userOrcid);
        return true;
    }

    @RequestMapping(value = "/admin-switch-user", method = RequestMethod.GET)
    public ModelAndView adminSwitchUser(HttpServletRequest request, @RequestParam("orcid") String targetOrcid, RedirectAttributes redirectAttributes) {
        // Redirect to the new way of switching user, which includes admin
        // access
        ModelAndView mav = null;
        if (StringUtils.isNotBlank(targetOrcid))
            targetOrcid = targetOrcid.trim();
        if (profileEntityManager.orcidExists(targetOrcid)) {
            mav = new ModelAndView("redirect:/switch-user?username=" + targetOrcid);
        } else {
            redirectAttributes.addFlashAttribute("invalidOrcid", true);
            mav = new ModelAndView("redirect:/my-orcid");
        }
        return mav;
    }        

    @ModelAttribute("securityQuestions")
    public Map<String, String> getSecurityQuestions() {
        Map<String, String> securityQuestions = securityQuestionManager.retrieveSecurityQuestionsAsInternationalizedMap();
        Map<String, String> securityQuestionsWithMessages = new LinkedHashMap<String, String>();
        for (String key : securityQuestions.keySet()) {
            securityQuestionsWithMessages.put(key, getMessage(securityQuestions.get(key)));
        }
        return securityQuestionsWithMessages;
    }        

    @RequestMapping(value = "/security-question.json", method = RequestMethod.GET)
    public @ResponseBody SecurityQuestion getSecurityQuestion() {
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        SecurityQuestion securityQuestion = new SecurityQuestion();
        
        if(profile.getSecurityQuestion() != null) {
            Long id = Long.valueOf(profile.getSecurityQuestion().getId());
            String encryptedAnswer = profile.getEncryptedSecurityAnswer();
            if(!PojoUtil.isEmpty(encryptedAnswer)) {
                securityQuestion.setSecurityAnswer(encryptionManager.decryptForInternalUse(encryptedAnswer));
            }
            securityQuestion.setSecurityQuestionId(id);
        }                

        return securityQuestion;
    }

    @RequestMapping(value = "/security-question.json", method = RequestMethod.POST)
    public @ResponseBody SecurityQuestion setSecurityQuestion(@RequestBody SecurityQuestion securityQuestion) {
        List<String> errors = new ArrayList<String>();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if (securityQuestion.getSecurityQuestionId() != 0 && (securityQuestion.getSecurityAnswer() == null || securityQuestion.getSecurityAnswer().trim() == ""))
            errors.add(getMessage("manage.pleaseProvideAnAnswer"));

        if (orcidSecurityManager.isPasswordConfirmationRequired()
                && (securityQuestion.getPassword() == null || !encryptionManager.hashMatches(securityQuestion.getPassword(), profile.getEncryptedPassword()))) {
            errors.add(getMessage("check_password_modal.incorrect_password"));
        }

        // If the security question is empty, clean the security answer field
        if (securityQuestion.getSecurityQuestionId() == 0)
            securityQuestion.setSecurityAnswer(new String());

        if (errors.size() == 0) {
            Integer id = Long.valueOf(securityQuestion.getSecurityQuestionId()).intValue();
            profileEntityManager.updateSecurityQuestion(getCurrentUserOrcid(), id, securityQuestion.getSecurityAnswer());
            errors.add(getMessage("manage.securityQuestionUpdated"));
        }

        securityQuestion.setErrors(errors);
        return securityQuestion;
    }

    @RequestMapping(value = "/preferences.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getDefaultPreference(HttpServletRequest request) {
        Map<String, Object> preferences = new HashMap<String, Object>();
        
        ProfileEntity entity = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        preferences.put("default_visibility", entity.getActivitiesVisibilityDefault());
        preferences.put("developer_tools_enabled", entity.getEnableDeveloperTools());
        return preferences;
    }
    
    @RequestMapping(value = "/default_visibility.json", method = RequestMethod.POST)
    public @ResponseBody String setDefaultVisibility(@RequestBody String defaultVisibility) throws IllegalArgumentException {
        try {
            org.orcid.jaxb.model.common_v2.Visibility visibility = org.orcid.jaxb.model.common_v2.Visibility.fromValue(defaultVisibility); 
            if(org.orcid.jaxb.model.common_v2.Visibility.REGISTERED_ONLY.equals(visibility) || org.orcid.jaxb.model.common_v2.Visibility.SYSTEM.equals(visibility)) {
                throw new IllegalArgumentException();
            }
            preferenceManager.updateDefaultVisibility(getCurrentUserOrcid(), visibility);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid visibility provided: " + defaultVisibility);
        }
        
        return "{\"status\": \"" + defaultVisibility + "\"}";
    }
    
    @RequestMapping(value = { "/change-password.json" }, method = RequestMethod.GET)
    public @ResponseBody ChangePassword getChangedPasswordJson(HttpServletRequest request) {
        return new ChangePassword();        
    }

    @RequestMapping(value = { "/change-password.json" }, method = RequestMethod.POST)
    public @ResponseBody ChangePassword changedPasswordJson(HttpServletRequest request, @RequestBody ChangePassword cp) {
        List<String> errors = new ArrayList<String>();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        
        if (cp.getPassword() == null || !cp.getPassword().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            errors.add(getMessage("NotBlank.registrationForm.confirmedPassword"));
        } else if (!cp.getPassword().equals(cp.getRetypedPassword())) {
            errors.add(getMessage("FieldMatch.registrationForm"));
        } 

        if (CommonPasswords.passwordIsCommon(cp.getPassword())) {
            errors.add(getMessage("password.too_common", cp.getPassword()));
        }

        if (cp.getOldPassword() == null || !encryptionManager.hashMatches(cp.getOldPassword(), profile.getEncryptedPassword())) {
            errors.add(getMessage("orcid.frontend.change.password.current_password_incorrect"));
        }

        if (errors.size() == 0) {            
            profileEntityManager.updatePassword(getCurrentUserOrcid(), cp.getPassword());
            cp = new ChangePassword();
            errors.add(getMessage("orcid.frontend.change.password.change.successfully"));
        }
        cp.setErrors(errors);
        return cp;
    }

    @RequestMapping(value = "/deprecate-profile.json")
    public @ResponseBody DeprecateProfile getDeprecateProfile() {
        return new DeprecateProfile();
    }

    @RequestMapping(value = "/validate-deprecate-profile.json", method = RequestMethod.POST)
    public @ResponseBody DeprecateProfile validateDeprecateProfile(@RequestBody DeprecateProfile deprecateProfile) {
        validateFormData(deprecateProfile);
        if (!deprecateProfile.getErrors().isEmpty()) {
            return deprecateProfile;
        }

        String currentUserOrcid = getCurrentUserOrcid();
        ProfileEntity primaryEntity = profileEntityCacheManager.retrieve(currentUserOrcid);
        ProfileEntity deprecatingEntity = getDeprecatingEntity(deprecateProfile);
        
        validateDeprecatingEntity(deprecatingEntity, primaryEntity, deprecateProfile);
        if (deprecateProfile.getErrors() != null && !deprecateProfile.getErrors().isEmpty()) {
            return deprecateProfile;
        }

        validateDeprecateAccountRequest(deprecateProfile, deprecatingEntity);
        if (deprecateProfile.getErrors() != null && !deprecateProfile.getErrors().isEmpty()) {
            return deprecateProfile;
        }

        Emails deprecatingEmails = emailManager.getEmails(deprecatingEntity.getId());
        Emails primaryEmails = emailManager.getEmails(primaryEntity.getId());
                
        String primaryAccountName = RecordNameUtils.getPublicName(primaryEntity.getRecordNameEntity());
        String deprecatingAccountName = RecordNameUtils.getPublicName(deprecatingEntity.getRecordNameEntity());
        deprecateProfile.setPrimaryAccountName(primaryAccountName);
        deprecateProfile.setPrimaryOrcid(currentUserOrcid);
        deprecateProfile.setDeprecatingAccountName(deprecatingAccountName);
        deprecateProfile.setDeprecatingOrcid(deprecatingEntity.getId());

        if (deprecatingEmails != null) {
            deprecateProfile.setDeprecatingEmails(
                    deprecatingEmails.getEmails().stream().map(e -> e.getEmail()).collect(Collectors.toList()));
        }
        if (primaryEmails != null) {
            deprecateProfile.setPrimaryEmails(
                    primaryEmails.getEmails().stream().map(e -> e.getEmail()).collect(Collectors.toList()));
        }
        return deprecateProfile;
    }

    @RequestMapping(value = "/confirm-deprecate-profile.json", method = RequestMethod.POST)
    public @ResponseBody DeprecateProfile confirmDeprecateProfile(@RequestBody DeprecateProfile deprecateProfile) {
        validateFormData(deprecateProfile);
        if (deprecateProfile.getErrors() != null && !deprecateProfile.getErrors().isEmpty()) {
            return deprecateProfile;
        }
        
        ProfileEntity primaryEntity = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        ProfileEntity deprecatingEntity = getDeprecatingEntity(deprecateProfile);
        
        validateDeprecatingEntity(deprecatingEntity, primaryEntity, deprecateProfile);
        if (deprecateProfile.getErrors() != null && !deprecateProfile.getErrors().isEmpty()) {
            return deprecateProfile;
        }
        
        validateDeprecateAccountRequest(deprecateProfile, deprecatingEntity);
        if (deprecateProfile.getErrors() != null && !deprecateProfile.getErrors().isEmpty()) {
            return deprecateProfile;
        }

        boolean deprecated = profileEntityManager.deprecateProfile(deprecatingEntity.getId(), primaryEntity.getId(), ProfileEntity.USER_DRIVEN_DEPRECATION, null);
        if (!deprecated) {
            deprecateProfile.setErrors(Arrays.asList(getMessage("deprecate_orcid.problem_deprecating")));
        }
        return deprecateProfile;
    }

    private void validateDeprecatingEntity(ProfileEntity deprecatingEntity, ProfileEntity primaryEntity, DeprecateProfile deprecateProfile) {
        if (deprecatingEntity == null) {
            deprecateProfile.setErrors(Arrays.asList(getMessage("deprecate_orcid.profile_does_not_exist")));
        } else if (primaryEntity.getId().equals(deprecatingEntity.getId())) {
            deprecateProfile.setErrors(Arrays.asList(getMessage("deprecate_orcid.profile_matches_current")));
        }
    }

    private void validateFormData(DeprecateProfile deprecateProfile) {
        List<String> errors = new ArrayList<>();
        if (deprecateProfile.getDeprecatingPassword() == null || deprecateProfile.getDeprecatingPassword().trim().isEmpty()) {
            errors.add(getMessage("deprecate_orcid.no_password_specified"));
        }
        if (deprecateProfile.getDeprecatingOrcidOrEmail() == null) {
            errors.add(getMessage("deprecate_orcid.no_profile_specified"));
        }
        deprecateProfile.setErrors(errors);
    }

    private void validateDeprecateAccountRequest(DeprecateProfile deprecateProfile, ProfileEntity deprecatingEntity) {
        if (!encryptionManager.hashMatches(deprecateProfile.getDeprecatingPassword(), deprecatingEntity.getEncryptedPassword())) {
            deprecateProfile.setErrors(Arrays.asList(getMessage("check_password_modal.incorrect_password")));
        } else if (deprecatingEntity.getDeprecatedDate() != null) {
            deprecateProfile.setErrors(Arrays.asList(getMessage("deprecate_orcid.already_deprecated", deprecatingEntity.getId())));
        } else if (deprecatingEntity.getDeactivationDate() != null) {
            deprecateProfile.setErrors(Arrays.asList(getMessage("deprecate_orcid.already_deactivated", deprecatingEntity.getId())));
        }
    }

    private ProfileEntity getDeprecatingEntity(DeprecateProfile deprecateProfile) {
        if (deprecateProfile.getDeprecatingOrcidOrEmail().contains("@")) {
            EmailEntity emailEntity = emailManager.findCaseInsensitive(deprecateProfile.getDeprecatingOrcidOrEmail());
            if (emailEntity != null) {
                return emailEntity.getProfile();
            }
        } else {
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(deprecateProfile.getDeprecatingOrcidOrEmail());
            if (profileEntity != null) {
                return profileEntity;
            }
        }
        return null;
    }

    @RequestMapping(value = { "deactivate-orcid", "/view-deactivate-orcid-account" }, method = RequestMethod.GET)
    public ModelAndView viewDeactivateOrcidAccount() {
        return new ModelAndView("deactivate_orcid");
    }

    @RequestMapping(value = "/confirm-deactivate-orcid/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView confirmDeactivateOrcidAccount(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            RedirectAttributes redirectAttributes) throws Exception {
        ModelAndView result = null;
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        String primaryEmail = emailManager.findPrimaryEmail(getCurrentUserOrcid()).getEmail();

        if (decryptedEmail.equals(primaryEmail)) {
            profileEntityManager.deactivateRecord(getCurrentUserOrcid());
            logoutCurrentUser(request, response);
            result = new ModelAndView("redirect:/signin#deactivated");
        } else {
            redirectAttributes.addFlashAttribute("emailDoesntMatch", true);
            return new ModelAndView("redirect:/my-orcid");
        }

        return result;
    }
    
    @RequestMapping(value = "/verifyEmail.json", method = RequestMethod.GET)
    public @ResponseBody Errors verifyEmail(HttpServletRequest request, @RequestParam("email") String email) {  
    	String currentUserOrcid = getCurrentUserOrcid();
        String primaryEmail = emailManager.findPrimaryEmail(currentUserOrcid).getEmail();
        if (primaryEmail.equals(email))
            request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);

        String emailOwner = emailManagerReadOnly.findOrcidIdByEmail(email);
        if(!currentUserOrcid.equals(emailOwner)) {
        	throw new IllegalArgumentException("Invalid email address provided");
        }
        
        notificationManager.sendVerificationEmail(currentUserOrcid, email);
        return new Errors();
    }

    @RequestMapping(value = "/delayVerifyEmail.json", method = RequestMethod.GET)
    public @ResponseBody Errors delayVerifyEmail(HttpServletRequest request) {
        request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
        return new Errors();
    }

    @RequestMapping(value = "/send-deactivate-account.json", method = RequestMethod.GET)
    public @ResponseBody String startDeactivateOrcidAccount(HttpServletRequest request) {
        String currentUserOrcid = getCurrentUserOrcid();
        notificationManager.sendOrcidDeactivateEmail(currentUserOrcid);
        return emailManager.findPrimaryEmail(currentUserOrcid).getEmail();
    }

    @RequestMapping(value = "/emails.json", method = RequestMethod.GET)
    public @ResponseBody org.orcid.pojo.ajaxForm.Emails getEmails(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {                                
        Emails v2Emails = emailManager.getEmails(getCurrentUserOrcid());       
        return org.orcid.pojo.ajaxForm.Emails.valueOf(v2Emails);
    }

    @RequestMapping(value = "/addEmail.json", method = RequestMethod.POST)
    public @ResponseBody org.orcid.pojo.ajaxForm.Email addEmails(HttpServletRequest request, @RequestBody org.orcid.pojo.AddEmail email) {
        List<String> errors = new ArrayList<String>();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        // Check password
        if (orcidSecurityManager.isPasswordConfirmationRequired()
                && (email.getPassword() == null || !encryptionManager.hashMatches(email.getPassword(), profile.getEncryptedPassword()))) {
            errors.add(getMessage("check_password_modal.incorrect_password"));
        }

        // if blank
        if (PojoUtil.isEmpty(email.getValue())) {
            errors.add(getMessage("Email.personalInfoForm.email"));
        }

        MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
        // make sure there are no dups
        validateEmailAddress(email.getValue(), false, false, request, mbr);

        for (ObjectError oe : mbr.getAllErrors()) {
            if (oe.getCode() != null) {
                errors.add(getMessage(oe.getCode(), oe.getArguments()));
            } else {
                errors.add(oe.getDefaultMessage());
            }
        }
        
        if (errors.isEmpty()) {
            // clear errors
            email.setErrors(new ArrayList<String>());
            String currentUserOrcid = getCurrentUserOrcid();            
            emailManager.addEmail(request, currentUserOrcid, email.toV3Email());                            
        } else {
            email.setErrors(errors);
        }
        return email;
    }

    @RequestMapping(value = "/deleteEmail.json", method = RequestMethod.DELETE)
    public @ResponseBody Errors deleteEmailJson(HttpServletRequest request, @RequestParam("email") String email) {
        Errors errors = new Errors();
        String currentUserOrcid = getCurrentUserOrcid();
        
        // if blank
        if (PojoUtil.isEmpty(email)) {
            errors.getErrors().add(getMessage("Email.personalInfoForm.email"));
        }
        
        
        
        String owner = null;
        
        try {
        	owner = emailManagerReadOnly.findOrcidIdByEmail(email);
        } catch(NoResultException nre) {
        	
        }
        
        if(!currentUserOrcid.equals(owner)) {
        	errors.getErrors().add(getMessage("Email.personalInfoForm.email"));
        }
        
        if (emailManager.isPrimaryEmail(currentUserOrcid, email)) {
            errors.getErrors().add(getMessage("manage.email.primaryEmailDeletion"));
        }

        if (errors.getErrors().size() == 0) {            
            emailManager.removeEmail(currentUserOrcid, email);
        }
        return errors;
    }
    
    @RequestMapping(value = "/email/visibility", method = RequestMethod.POST)
    public @ResponseBody org.orcid.pojo.ajaxForm.Email updateEmailVisibility(@RequestBody org.orcid.pojo.ajaxForm.Email email) {
        String orcid = getCurrentUserOrcid();
        String owner = emailManager.findOrcidIdByEmail(email.getValue());
        if(orcid.equals(owner)) {
            if(email.getVisibility() != null) {
                // Updates the visibility on the given email
                emailManager.updateVisibility(orcid, email.getValue(), email.getVisibility());
            }            
        }
        return email;
    }
    
    @RequestMapping(value = "/email/setPrimary", method = RequestMethod.POST)
    public @ResponseBody org.orcid.pojo.ajaxForm.Email setPrimary(HttpServletRequest request, @RequestBody org.orcid.pojo.ajaxForm.Email email) {
        String orcid = getCurrentUserOrcid();
        String owner = emailManager.findOrcidIdByEmail(email.getValue());
        if(orcid.equals(owner)) {            
            // Sets the given user as primary
            emailManager.setPrimary(orcid, email.getValue(), request);   
            // Updates the last modified of the record
            profileEntityManager.updateLastModifed(orcid);
        }
        return email;
    }
    
    @RequestMapping(value = "/countryForm.json", method = RequestMethod.GET)
    public @ResponseBody AddressesForm getProfileCountryJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        Addresses addresses = addressManager.getAddresses(getCurrentUserOrcid());
        AddressesForm form = AddressesForm.valueOf(addresses);
        // Set country name
        if (form != null && form.getAddresses() != null) {
            Map<String, String> countries = retrieveIsoCountries();
            for (AddressForm addressForm : form.getAddresses()) {
                addressForm.setCountryName(countries.get(addressForm.getIso2Country().getValue().name()));
            }
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        org.orcid.jaxb.model.v3.rc1.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc1.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
        Visibility v = Visibility.valueOf(defaultVis);
        
        // Set the default visibility
        if (profile.getActivitiesVisibilityDefault() != null) {
            form.setVisibility(v);
        }

        return form;
    }

    @RequestMapping(value = "/countryForm.json", method = RequestMethod.POST)
    public @ResponseBody AddressesForm setProfileCountryJson(HttpServletRequest request, @RequestBody AddressesForm addressesForm)
            throws NoSuchRequestHandlingMethodException {
        addressesForm.setErrors(new ArrayList<String>());
        Map<String, String> countries = retrieveIsoCountries();
        if (addressesForm != null) {
            if (addressesForm.getAddresses() != null) {
                for (AddressForm form : addressesForm.getAddresses()) {
                    if (form.getIso2Country() == null || form.getIso2Country().getValue() == null) {
                        form.getErrors().add(getMessage("common.invalid_country"));
                    } else {
                        form.setCountryName(countries.get(form.getIso2Country().getValue().name()));
                    }
                    
                    //Validate visibility is not null
                    validateVisibility(form);
                    
                    copyErrors(form, addressesForm);
                    copyErrors(form.getVisibility(), addressesForm);
                }
            }

            if (!addressesForm.getErrors().isEmpty()) {
                return addressesForm;
            }

            Addresses addresses = addressesForm.toAddresses();
            addressManager.updateAddresses(getCurrentUserOrcid(), addresses);
        }
        return addressesForm;
    }

    @RequestMapping(value = "/nameForm.json", method = RequestMethod.GET)
    public @ResponseBody NamesForm getNameForm() throws NoSuchRequestHandlingMethodException {
        String currentOrcid = getCurrentUserOrcid();
        Name name = recordNameManager.getRecordName(currentOrcid);
        return NamesForm.valueOf(name);
    }
    
    @RequestMapping(value = "/nameForm.json", method = RequestMethod.POST)
    public @ResponseBody NamesForm setNameFormJson(@RequestBody NamesForm nf) throws NoSuchRequestHandlingMethodException {
        nf.setErrors(new ArrayList<String>());

        // Strip any html code from names before validating them
        if (!PojoUtil.isEmpty(nf.getFamilyName())) {
            nf.getFamilyName().setValue(OrcidStringUtils.stripHtml(nf.getFamilyName().getValue()));
        }

        if (!PojoUtil.isEmpty(nf.getGivenNames())) {
            nf.getGivenNames().setValue(OrcidStringUtils.stripHtml(nf.getGivenNames().getValue()));
        }

        if (!PojoUtil.isEmpty(nf.getCreditName())) {
            nf.getCreditName().setValue(OrcidStringUtils.stripHtml(nf.getCreditName().getValue()));
        }

        //Validate visibility is not null
        validateVisibility(nf);
                
        if (nf.getGivenNames() == null)
            nf.setGivenNames(new Text());
        givenNameValidate(nf.getGivenNames());
        copyErrors(nf.getGivenNames(), nf);
        copyErrors(nf.getVisibility(), nf);
        if (nf.getErrors().size() > 0)
            return nf;
        Name name = nf.toName();

        String orcid = getCurrentUserOrcid();
        if (recordNameManager.exists(orcid)) {
            recordNameManager.updateRecordName(orcid, name);
        } else {
            recordNameManager.createRecordName(orcid, name);
        }

        return nf;
    }

    @RequestMapping(value = "/biographyForm.json", method = RequestMethod.GET)
    public @ResponseBody BiographyForm getBiographyForm() {
        Biography bio = biographyManager.getBiography(getCurrentUserOrcid());
        BiographyForm form = BiographyForm.valueOf(bio);
        if(form.getVisibility() == null) {
            ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid()); 
            org.orcid.jaxb.model.v3.rc1.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc1.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);          
            form.setVisibility(v);
        }
        return form;
    }

    @RequestMapping(value = "/biographyForm.json", method = RequestMethod.POST)
    public @ResponseBody BiographyForm setBiographyFormJson(@RequestBody BiographyForm bf) {
        bf.setErrors(new ArrayList<String>());
        if (bf.getBiography() != null) {
            validateBiography(bf.getBiography());
            //Validate visibility is not null
            validateVisibility(bf);
            
            copyErrors(bf.getBiography(), bf);
            copyErrors(bf.getVisibility(), bf);
            if (bf.getErrors().size() > 0)
                return bf;

            Biography bio = new Biography();
            if (bf.getBiography() != null) {
                bio.setContent(bf.getBiography().getValue());
            }
            if (bf.getVisibility() != null && bf.getVisibility().getVisibility() != null) {
                org.orcid.jaxb.model.v3.rc1.common.Visibility v = org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(bf.getVisibility().getVisibility().value());
                bio.setVisibility(v);
            }

            String orcid = getCurrentUserOrcid();
            if (biographyManager.exists(orcid)) {
                biographyManager.updateBiography(orcid, bio);
            } else {
                biographyManager.createBiography(orcid, bio);
            }
        }
        return bf;
    }

    /**
     * Check if the user have twitter enabled
     */
    @RequestMapping(value = { "/twitter/check-twitter-status" }, method = RequestMethod.GET)
    public @ResponseBody boolean isTwitterEnabled() {
        return orcidSocialManager.isTwitterEnabled(getEffectiveUserOrcid());
    }

    /**
     * Get a user request to authorize twitter and return the authorization URL
     */
    @RequestMapping(value = { "/twitter" }, method = RequestMethod.POST)
    public @ResponseBody String goToTwitterAuthPage() throws Exception {
        return orcidSocialManager.getTwitterAuthorizationUrl(getEffectiveUserOrcid());        
    }

    /**
     * Get the twitter credentials and enable it on the user profile
     */
    @RequestMapping(value = { "/twitter" }, method = RequestMethod.GET)
    public ModelAndView setTwitterKeyToProfileGET(@RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) throws Exception {      
        ModelAndView mav = new ModelAndView("manage");
        mav.addObject("showPrivacy", true);        
        mav.addObject("activeTab", "profile-tab");
        mav.addObject("securityQuestions", getSecurityQuestions());
        return mav;
    }

    /**
     * Disable twitter access
     */
    @RequestMapping(value = { "/disable-twitter" }, method = RequestMethod.POST)
    public @ResponseBody boolean disableTwitter() throws Exception {
        orcidSocialManager.disableTwitter(getEffectiveUserOrcid());
        return true;
    }

    /**
     * Authorize a delegate request done by an admin
     */
    @RequestMapping(value = { "/authorize-delegates" }, method = RequestMethod.GET)
    public ModelAndView authorizeDelegatesRequest(@RequestParam("key") String key) {
        ModelAndView mav = new ModelAndView("manage");

        try {
            Map<String, String> params = decryptDelegationKey(key);
            if (params.containsKey(AdminManager.MANAGED_USER_PARAM) && params.containsKey(AdminManager.TRUSTED_USER_PARAM)) {
                String managedOrcid = params.get(AdminManager.MANAGED_USER_PARAM);
                String trustedOrcid = params.get(AdminManager.TRUSTED_USER_PARAM);
                // Check if managed user is the same than the logged user
                if (managedOrcid.equals(getEffectiveUserOrcid())) {
                    // Check if the managed user email is verified, if not,
                    // verify it
                    verifyPrimaryEmailIfNeeded(managedOrcid);                    
                    givenPermissionToManager.create(getCurrentUserOrcid(), trustedOrcid);                    
                    mav.addObject("admin_delegate_approved", getMessage("admin.delegate.success", trustedOrcid));
                } else {
                    // Exception, the email was not for you
                    mav.addObject("admin_delegate_not_you", getMessage("wrong_user.Wronguser"));
                }
            } else {
                // Error
                mav.addObject("admin_delegate_failed", getMessage("admin.delegate.error.invalid_link"));
            }
        } catch (UnsupportedEncodingException uee) {
            mav.addObject("admin_delegate_failed", getMessage("admin.delegate.error.invalid_link"));
        }

        return mav;
    }

    @RequestMapping(value = { "/get-trusted-orgs" }, method = RequestMethod.GET)
    public @ResponseBody List<ApplicationSummary> getTrustedOrgs() {
        return profileEntityManager.getApplications(getCurrentUserOrcid());
    }

    /**
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> decryptDelegationKey(String encryptedKey) throws UnsupportedEncodingException {
        String jsonString = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedKey), "UTF-8"));
        Map<String, String> params = (Map<String, String>) JsonUtils.readObjectFromJsonString(jsonString, Map.class);
        return params;
    }

    /**
     * Verify a primary email if it is not verified yet.
     * 
     * @param orcid
     *            The profile id to check
     */
    private void verifyPrimaryEmailIfNeeded(String orcid) {
        if (!emailManager.isPrimaryEmailVerified(orcid)) {
            emailManager.verifyPrimaryEmail(orcid);
        }
    }
    
    @Deprecated
    @RequestMapping(value = "/emails.json", method = RequestMethod.POST)
    public @ResponseBody org.orcid.pojo.ajaxForm.Emails postEmailsJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ajaxForm.Emails emails) {
        org.orcid.pojo.ajaxForm.Email newPrime = null;
        List<String> allErrors = new ArrayList<String>();

        for (org.orcid.pojo.ajaxForm.Email email : emails.getEmails()) {
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

        emails.setErrors(allErrors);
        if (allErrors.size() == 0) {
            emailManager.updateEmails(request, getCurrentUserOrcid(), emails.toV3Emails());
            
        }
        return emails;
    }
}