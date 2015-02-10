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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AdminChangePassword;
import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/admin-actions" })
public class AdminController extends BaseController {

    private static int RANDOM_STRING_LENGTH = 15;

    @Resource
    ProfileEntityManager profileEntityManager;

    @Resource
    ProfileWorkManager profileWorkManager;

    @Resource
    ExternalIdentifierManager externalIdentifierManager;

    @Resource
    NotificationManager notificationManager;

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private EmailManager emailManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private GroupAdministratorController groupAdministratorController;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private AdminManager adminManager;

    public ProfileEntityManager getProfileEntityManager() {
        return profileEntityManager;
    }

    public void setProfileEntityManager(ProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public OrcidClientGroupManager getOrcidClientGroupManager() {
        return orcidClientGroupManager;
    }

    public void setOrcidClientGroupManager(OrcidClientGroupManager orcidClientGroupManager) {
        this.orcidClientGroupManager = orcidClientGroupManager;
    }

    public EmailManager getEmailManager() {
        return emailManager;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    @ModelAttribute("groupTypes")
    public Map<String, String> retrieveGroupTypes() {
        GroupType[] groupTypes = GroupType.values();
        Map<String, String> groupTypesMap = new TreeMap<String, String>();

        for (GroupType groupType : groupTypes) {
            String key = groupType.value();
            String value = key.replace('-', ' ');
            groupTypesMap.put(key, value);
        }

        return groupTypesMap;
    }

    @RequestMapping
    public ModelAndView getDeprecatedProfilesPage() {
        ModelAndView mav = new ModelAndView("admin_actions");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_ONLY);
        mav.addObject("profile", profile);
        return mav;
    }

    @RequestMapping(value = { "/deprecate-profile/get-empty-deprecation-request.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDeprecationRequest getPendingDeprecationRequest() {
        return new ProfileDeprecationRequest();
    }

    /**
     * Get a deprecate profile request and process it.
     * 
     * @param deprecatedOrcid
     *            Orcid to deprecate
     * @param primaryOrcid
     *            Orcid to use as a primary account
     * */
    @RequestMapping(value = { "/deprecate-profile/deprecate-profile.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDeprecationRequest deprecateProfile(@RequestParam("deprecated") String deprecatedOrcid, @RequestParam("primary") String primaryOrcid) {
        ProfileDeprecationRequest result = new ProfileDeprecationRequest();
        // Check for errors
        if (deprecatedOrcid == null || !OrcidStringUtils.isValidOrcid(deprecatedOrcid)) {
            result.getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", deprecatedOrcid));
        } else if (primaryOrcid == null || !OrcidStringUtils.isValidOrcid(primaryOrcid)) {
            result.getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", primaryOrcid));
        } else if (deprecatedOrcid.equals(primaryOrcid)) {
            result.getErrors().add(getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"));
        } else {            
            try {
                adminManager.deprecateProfile(result, deprecatedOrcid, primaryOrcid);                            
            } catch (Exception e) {
                result.getErrors().add(getMessage("admin.profile_deprecation.errors.internal_error", deprecatedOrcid));
            }                                               
        }

        return result;
    }

    /**
     * Check an orcid on database to see if it exists
     * 
     * @param orcid
     *            The orcid string to check on database
     * @return a ProfileDetails object with the details of the profile or an
     *         error message.
     * */
    @RequestMapping(value = { "/deprecate-profile/check-orcid.json", "/deactivate-profile/check-orcid.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDetails checkOrcidToDeprecate(@RequestParam("orcid") String orcid) {
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        ProfileDetails profileDetails = new ProfileDetails();
        if (profile != null) {
            if (profile.getPrimaryRecord() != null) {
                profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.already_deprecated", orcid));
            } else if (profile.getDeactivationDate() != null) {
                profileDetails.getErrors().add(getMessage("admin.profile_deactivation.errors.already_deactivated", orcid));
            } else {
                profileDetails.setOrcid(profile.getId());
                profileDetails.setFamilyName(profile.getFamilyName());
                profileDetails.setGivenNames(profile.getGivenNames());
                if (profile.getPrimaryEmail() != null)
                    profileDetails.setEmail(profile.getPrimaryEmail().getId());
            }
        } else {
            profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.inexisting_orcid", orcid));
        }
        return profileDetails;
    }

    @RequestMapping(value = "/deactivate-profile", method = RequestMethod.GET)
    public @ResponseBody
    ProfileDetails deactivateOrcidAccount(@RequestParam("orcid") String orcid) {
        OrcidProfile toDeactivate = orcidProfileManager.retrieveOrcidProfile(orcid);
        ProfileDetails result = new ProfileDetails();
        if (toDeactivate == null)
            result.getErrors().add(getMessage("admin.errors.unexisting_orcid"));
        else if (toDeactivate.isDeactivated())
            result.getErrors().add(getMessage("admin.profile_deactivation.errors.already_deactivated"));
        else if (toDeactivate.getOrcidDeprecated() != null)
            result.getErrors().add(getMessage("admin.errors.deprecated_account"));

        if (result.getErrors() == null || result.getErrors().size() == 0)
            orcidProfileManager.deactivateOrcidProfile(toDeactivate);
        return result;
    }

    @RequestMapping(value = "/reactivate-profile", method = RequestMethod.GET)
    public @ResponseBody
    ProfileDetails reactivateOrcidAccount(@RequestParam("orcid") String orcid) {
        OrcidProfile toReactivate = orcidProfileManager.retrieveOrcidProfile(orcid);
        ProfileDetails result = new ProfileDetails();
        if (toReactivate == null)
            result.getErrors().add(getMessage("admin.errors.unexisting_orcid"));
        else if (!toReactivate.isDeactivated())
            result.getErrors().add(getMessage("admin.profile_reactivation.errors.already_active"));
        else if (toReactivate.getOrcidDeprecated() != null)
            result.getErrors().add(getMessage("admin.errors.deprecated_account"));

        if (result.getErrors() == null || result.getErrors().size() == 0)
            orcidProfileManager.reactivateOrcidProfile(toReactivate);
        return result;
    }

    @RequestMapping(value = "/find-id", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> findIdByEmail(@RequestParam("csvEmails") String csvEmails) {
        if (StringUtils.isBlank(csvEmails))
            return new HashMap<String, String>();
        return emailManager.findIdByEmail(csvEmails);
    }

    /**
     * Generate random string
     * */
    @RequestMapping(value = "/generate-random-string.json", method = RequestMethod.GET)
    public @ResponseBody
    String generateRandomString() {
        return RandomStringUtils.random(RANDOM_STRING_LENGTH, OrcidPasswordConstants.getEntirePasswordCharsRange());
    }

    /**
     * Reset password
     * */
    @RequestMapping(value = "/reset-password.json", method = RequestMethod.POST)
    public @ResponseBody
    String resetPassword(HttpServletRequest request, @RequestBody AdminChangePassword form) {
        String orcidOrEmail = form.getOrcidOrEmail();
        String password = form.getPassword();
        String orcid = null;
        if (StringUtils.isNotBlank(password) && password.matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            OrcidProfile currentProfile = getEffectiveProfile();
            if (OrcidType.ADMIN.equals(currentProfile.getType())) {
                if (StringUtils.isNotBlank(orcidOrEmail))
                    orcidOrEmail = orcidOrEmail.trim();
                boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
                // If it is not an orcid, check the value from the emails table
                if (!isOrcid) {
                    Map<String, String> email = findIdByEmail(orcidOrEmail);
                    orcid = email.get(orcidOrEmail);
                } else {
                    orcid = orcidOrEmail;
                }

                if (StringUtils.isNotEmpty(orcid)) {
                    OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                    if (orcidProfile != null) {
                        orcidProfile.setPassword(password);
                        orcidProfileManager.updatePasswordInformation(orcidProfile);
                    } else {
                        return getMessage("admin.errors.unexisting_orcid");
                    }
                } else {
                    return getMessage("admin.errors.unable_to_fetch_info");
                }
            }
        } else {
            return getMessage("admin.reset_password.error.invalid_password");
        }

        return getMessage("admin.reset_password.success");
    }

    /**
     * Remove security question
     * */
    @RequestMapping(value = "/remove-security-question.json", method = RequestMethod.POST)
    public @ResponseBody
    String removeSecurityQuestion(HttpServletRequest request, @RequestBody String orcidOrEmail) {
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmail(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }

        if (StringUtils.isNotEmpty(orcid)) {
            OrcidProfile currentProfile = getEffectiveProfile();
            //Only allow and admin
            if (OrcidType.ADMIN.equals(currentProfile.getType())) {
                String result = adminManager.removeSecurityQuestion(orcid);
                // If the resulting string is not null, it means there was an error
                if(result != null)
                    return result;
            }
        } else {
            return getMessage("admin.errors.unable_to_fetch_info");
        }
        return getMessage("admin.remove_security_question.success");
    }

    /**
     * Admin switch user
     * */
    @RequestMapping(value = "/admin-switch-user", method = RequestMethod.POST)
    public ModelAndView adminSwitchUser(@ModelAttribute("orcidOrEmail") String orcidOrEmail, RedirectAttributes redirectAttributes) {
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmail(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }

        ModelAndView mav = null;

        if (StringUtils.isNotEmpty(orcid)) {
            if (profileEntityManager.orcidExists(orcid)) {
                mav = new ModelAndView("redirect:/switch-user?j_username=" + orcid);
            } else {
                redirectAttributes.addFlashAttribute("invalidOrcid", true);
                mav = new ModelAndView("redirect:/admin-actions");
            }
        } else {
            redirectAttributes.addFlashAttribute("invalidOrcid", true);
            mav = new ModelAndView("redirect:/admin-actions");
        }

        return mav;

    }

    /**
     * Admin verify email
     * */
    @RequestMapping(value = "/admin-verify-email", method = RequestMethod.GET)
    public @ResponseBody
    String adminVerifyEmail(@RequestParam("email") String email) {
        String result = getMessage("admin.verify_email.success", email);
        if (emailManager.emailExists(email)) {
            emailManager.verifyEmail(email);
            Map<String, String> ids = emailManager.findIdByEmail(email);
            orcidProfileManager.updateLastModifiedDate(ids.get(email));
        } else {
            result = getMessage("admin.verify_email.fail", email);
        }
        return result;
    }

    /**
     * Admin starts delegation process
     * */
    @RequestMapping(value = "/admin-delegates", method = RequestMethod.POST)
    public @ResponseBody
    AdminDelegatesRequest startDelegationProcess(@RequestBody AdminDelegatesRequest request) {
        // Clear errors
        request.setErrors(new ArrayList<String>());
        request.getManaged().setErrors(new ArrayList<String>());
        request.getTrusted().setErrors(new ArrayList<String>());
        request.setSuccessMessage(null);

        String trusted = request.getTrusted().getValue();
        String managed = request.getManaged().getValue();
        boolean trustedIsOrcid = matchesOrcidPattern(trusted);
        boolean managedIsOrcid = matchesOrcidPattern(managed);
        boolean haveErrors = false;

        if (!trustedIsOrcid) {
            if (emailManager.emailExists(trusted)) {
                Map<String, String> email = findIdByEmail(trusted);
                trusted = email.get(trusted);
            } else {
                request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                haveErrors = true;
            }
        }
        
        if (!managedIsOrcid) {
            if (emailManager.emailExists(managed)) {
                Map<String, String> email = findIdByEmail(managed);
                managed = email.get(managed);
            } else {
                request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getManaged().getValue()));
                haveErrors = true;
            }
        }
        
        if (haveErrors)
            return request;

        adminManager.startDelegationProcess(request, trusted, managed);
        return request;
    }

    /**
     * Admin starts delegation process
     * */
    @RequestMapping(value = "/admin-delegates/check-claimed-status.json", method = RequestMethod.GET)
    public @ResponseBody
    boolean checkClaimedStatus(@RequestParam("orcidOrEmail") String orcidOrEmail) {
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmail(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            }
        } else {
            orcid = orcidOrEmail;
        }

        if (PojoUtil.isEmpty(orcid))
            return false;

        return profileEntityManager.isProfileClaimed(orcid);
    }    

    private boolean matchesOrcidPattern(String orcid) {
        return OrcidStringUtils.isValidOrcid(orcid);
    }
    
    
    
    
    /**
     * Function to get the info of an account we want to lock
     * @param orcidOrEmail orcid or email of the account we want to lock
     * @return a ProfileDetails object containing either an error message or the info of the account we want to lock
     * */
    @RequestMapping(value = "/check-account-to-lock.json", method = RequestMethod.POST)
    public @ResponseBody
    ProfileDetails checkAccountToLock(@RequestBody String orcidOrEmail) {        
        String orcid = getOrcidFromParam(orcidOrEmail);
        
        if (PojoUtil.isEmpty(orcid)) {
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.lock_profile.error.not_found", orcidOrEmail));
            return result;
        }          
        
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        
        // If the account is already locked
        if(profile.isAccountNonLocked() == false) { 
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.lock_profile.error.already_locked", orcid));
            return result;
        }
                
        return generateProfileDetails(profile);
    }
    
    /**
     * Function to get the info of an account we want to unlock
     * @param orcidOrEmail orcid or email of the account we want to unlock
     * @return a ProfileDetails object containing either an error message or the info of the account we want to unlock
     * */
    @RequestMapping(value = "/check-account-to-unlock.json", method = RequestMethod.POST)
    public @ResponseBody
    ProfileDetails checkAccountToUnlock(@RequestBody String orcidOrEmail) {
        String orcid = getOrcidFromParam(orcidOrEmail);
        
        if (PojoUtil.isEmpty(orcid)) {
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.lock_profile.error.not_found", orcidOrEmail));
            return result;
        }          
        
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        
        // If the account is not locked
        if(profile.isAccountNonLocked()) { 
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.unlock_profile.error.non_locked", orcid));
            return result;
        }
                
        return generateProfileDetails(profile);
    }
    
    private String getOrcidFromParam(String orcidOrEmail) {
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        ProfileDetails result = new ProfileDetails();
        result.setErrors(new ArrayList<String>());
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmail(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            }
        } else {
            orcid = orcidOrEmail;
        }
                
        return orcid;
    }
    
    private ProfileDetails generateProfileDetails(ProfileEntity profileEntity) {
        ProfileDetails result = new ProfileDetails();
        result.setErrors(new ArrayList<String>());
        result.setFamilyName(profileEntity.getFamilyName());
        result.setGivenNames(profileEntity.getGivenNames());
        result.setOrcid(profileEntity.getId());
        result.setEmail(profileEntity.getPrimaryEmail().getId());
        return result;
    }
    
    
    /**
     * Function to lock an account
     * @param orcid The orcid of the account we want to lock
     * @return true if the account was locked, false otherwise
     * */
    @RequestMapping(value = "/lock-account.json", method = RequestMethod.POST)
    public @ResponseBody
    String lockAccount(@RequestBody String orcid) {
        if(profileEntityManager.lockProfile(orcid)) {
            return getMessage("admin.lock_profile.success", orcid);
        }
        return getMessage("admin.lock_profile.error.couldnt_lock_account", orcid);        
    }
    
    /**
     * Function to unlock an account
     * @param orcid The orcid of the account we want to unlock
     * @return true if the account was unlocked, false otherwise
     * */
    @RequestMapping(value = "/unlock-account.json", method = RequestMethod.POST)
    public @ResponseBody
    String unlockAccount(@RequestBody String orcid) {
        if(profileEntityManager.unlockProfile(orcid)) {
            return getMessage("admin.unlock_profile.success", orcid);
        }
        return getMessage("admin.unlock_profile.error.couldnt_unlock_account", orcid);        
    }
}
