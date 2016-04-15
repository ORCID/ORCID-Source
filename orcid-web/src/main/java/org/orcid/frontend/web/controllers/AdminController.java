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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.Email;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    
    private static int RANDOM_STRING_LENGTH = 15;

    @Resource
    ProfileEntityManager profileEntityManager;

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

    private static final String INP_STRING_SEPARATOR = " \n\r\t,";
    private static final String OUT_STRING_SEPARATOR = "		";
    private static final String OUT_NOT_AVAILABLE = "N/A";
    private static final String OUT_NEW_LINE = "\n";

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;

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
        MemberType[] groupTypes = MemberType.values();
        Map<String, String> groupTypesMap = new TreeMap<String, String>();

        for (MemberType groupType : groupTypes) {
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
    public @ResponseBody ProfileDeprecationRequest getPendingDeprecationRequest() {
        return new ProfileDeprecationRequest();
    }

    /**
     * Get a deprecate profile request and process it.
     * 
     * @param deprecatedOrcid
     *            Orcid to deprecate
     * @param primaryOrcid
     *            Orcid to use as a primary account
     */
    @RequestMapping(value = { "/deprecate-profile/deprecate-profile.json" }, method = RequestMethod.GET)
    public @ResponseBody ProfileDeprecationRequest deprecateProfile(@RequestParam("deprecated") String deprecatedOrcid, @RequestParam("primary") String primaryOrcid) {
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
                boolean wasDeprecated = adminManager.deprecateProfile(result, deprecatedOrcid, primaryOrcid);
                if (wasDeprecated) {
                    ProfileEntity deprecated = profileEntityCacheManager.retrieve(deprecatedOrcid);
                    ProfileEntity primary = profileEntityCacheManager.retrieve(primaryOrcid);

                    ProfileDetails deprecatedDetails = new ProfileDetails();
                    deprecatedDetails.setOrcid(deprecatedOrcid);

                    if(deprecated.getRecordNameEntity() != null) {
                        deprecatedDetails.setFamilyName(deprecated.getRecordNameEntity().getFamilyName());
                        deprecatedDetails.setGivenNames(deprecated.getRecordNameEntity().getGivenNames());
                    } else {
                        deprecatedDetails.setFamilyName(deprecated.getFamilyName());
                        deprecatedDetails.setGivenNames(deprecated.getGivenNames());
                    }

                    ProfileDetails primaryDetails = new ProfileDetails();
                    primaryDetails.setOrcid(primaryOrcid);
                    if(primary.getRecordNameEntity() != null) {
                        primaryDetails.setFamilyName(primary.getRecordNameEntity().getFamilyName());
                        primaryDetails.setGivenNames(primary.getRecordNameEntity().getGivenNames());
                    } else {
                        primaryDetails.setFamilyName(primary.getFamilyName());
                        primaryDetails.setGivenNames(primary.getGivenNames());
                    }

                    result.setDeprecatedAccount(deprecatedDetails);
                    result.setPrimaryAccount(primaryDetails);
                    result.setDeprecatedDate(new Date());                                        
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
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
     */
    @RequestMapping(value = { "/deprecate-profile/check-orcid.json" }, method = RequestMethod.GET)
    public @ResponseBody ProfileDetails checkOrcidToDeprecate(@RequestParam("orcid") String orcid) {
        ProfileDetails profileDetails = new ProfileDetails();
        try {
            OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(orcid, LoadOptions.BIO_ONLY);
            if (profile != null) {
                if (profile.getOrcidDeprecated() != null && profile.getOrcidDeprecated().getDate() != null) {
                    profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.already_deprecated", orcid));
                } else if (profile.isDeactivated()) {
                    profileDetails.getErrors().add(getMessage("admin.profile_deactivation.errors.already_deactivated", orcid));
                } else {
                    profileDetails.setOrcid(orcid);
                    if (profile != null && profile.getOrcidBio().getPersonalDetails() != null) {
                        boolean hasName = false;
                        if (profile.getOrcidBio().getPersonalDetails().getFamilyName() != null) {
                            profileDetails.setFamilyName(profile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
                            hasName = true;
                        }
                        if (profile.getOrcidBio().getPersonalDetails().getGivenNames() != null) {
                            profileDetails.setGivenNames(profile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
                            hasName = true;
                        }

                        if (!hasName) {
                            profileDetails.setGivenNames(profile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
                        }
                    }
                    if (profile.getOrcidBio().getContactDetails() != null && profile.getOrcidBio().getContactDetails().getEmail() != null) {
                        for (Email email : profile.getOrcidBio().getContactDetails().getEmail()) {
                            if (email.isPrimary()) {
                                profileDetails.setEmail(email.getValue());
                                break;
                            }
                        }
                    }
                }
            } else {
                profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.inexisting_orcid", orcid));
            }
        } catch (IllegalArgumentException iae) {
            profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.inexisting_orcid", orcid));
        }

        return profileDetails;
    }

    @RequestMapping(value = "/reactivate-profile", method = RequestMethod.GET)
    public @ResponseBody ProfileDetails reactivateOrcidAccount(@RequestParam("orcid") String orcid) {
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

    @RequestMapping(value = "/find-id.json", method = RequestMethod.POST)
    public @ResponseBody List<ProfileDetails> findIdByEmail(@RequestBody String csvEmails) {
        Map<String, String> emailMap = findIdByEmailHelper(csvEmails);
        List<ProfileDetails> profileDetList = new ArrayList<ProfileDetails>();
        ProfileDetails tempObj;
        for (Map.Entry<String, String> entry : emailMap.entrySet()) {
            tempObj = new ProfileDetails();
            tempObj.setOrcid(entry.getValue());
            tempObj.setEmail(entry.getKey());
            if (!profileEntityManager.isProfileClaimed(entry.getValue())) {
                tempObj.setStatus("Unclaimed");
            } else if (profileEntityManager.isLocked(entry.getValue())) {
                tempObj.setStatus("Locked");
            } else if (profileEntityManager.isDeactivated(entry.getValue())) {
                tempObj.setStatus("Deactivated");
            } else {
                tempObj.setStatus("Claimed");
            }
            profileDetList.add(tempObj);
        }
        return profileDetList;
    }

    @RequestMapping(value = "/lookup-id-or-emails.json", method = RequestMethod.POST)
    public @ResponseBody String lookupIdOrEmails(@RequestBody String csvIdOrEmails) {
        List<String> idEmailList = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(csvIdOrEmails)) {
            StringTokenizer tokenizer = new StringTokenizer(csvIdOrEmails, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                idEmailList.add(tokenizer.nextToken());
            }

            for (String idEmail : idEmailList) {
                idEmail = idEmail.trim();
                boolean isOrcid = matchesOrcidPattern(idEmail);
                String orcid = idEmail;
                if (!isOrcid) {
                    Map<String, String> email = findIdByEmailHelper(idEmail);
                    orcid = email.get(idEmail);
                }

                OrcidProfile profile = null;
                if (orcid != null) {
                    profile = orcidProfileManager.retrieveOrcidProfile(orcid);
                }
                if (profile != null) {
                    if (profile.getOrcidBio() != null && profile.getOrcidBio().getContactDetails() != null
                            && profile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null) {
                        builder.append(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
                    } else {
                        builder.append(OUT_NOT_AVAILABLE);
                    }
                    builder.append(OUT_STRING_SEPARATOR).append(orcid);
                } else {
                    if (isOrcid) {
                        builder.append(OUT_NOT_AVAILABLE).append(OUT_STRING_SEPARATOR).append(idEmail);
                    } else {
                        builder.append(idEmail).append(OUT_STRING_SEPARATOR).append(OUT_NOT_AVAILABLE);
                    }
                }
                builder.append(OUT_NEW_LINE);
            }
        }

        return builder.toString();
    }

    @RequestMapping(value = "/deactivate-profiles.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> deactivateOrcidAccount(@RequestBody String orcidIds) {
        Set<String> deactivatedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String orcid = tokenizer.nextToken();
                OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(orcid);
                if (profile == null) {
                    notFoundIds.add(orcid);
                } else {
                    if (profile.isDeactivated()) {
                        deactivatedIds.add(orcid);
                    } else {
                        orcidProfileManager.deactivateOrcidProfile(profile);
                        successIds.add(orcid);
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFoundList", notFoundIds);
        resendIdMap.put("deactivateSuccessfulList", successIds);
        resendIdMap.put("alreadyDeactivatedList", deactivatedIds);
        return resendIdMap;
    }

    public Map<String, String> findIdByEmailHelper(String csvEmails) {
        if (StringUtils.isBlank(csvEmails))
            return new HashMap<String, String>();
        return emailManager.findIdByEmail(csvEmails);
    }

    /**
     * Generate random string
     */
    @RequestMapping(value = "/generate-random-string.json", method = RequestMethod.GET)
    public @ResponseBody String generateRandomString() {
        return RandomStringUtils.random(RANDOM_STRING_LENGTH, OrcidPasswordConstants.getEntirePasswordCharsRange());
    }

    /**
     * Reset password
     */
    @RequestMapping(value = "/reset-password.json", method = RequestMethod.POST)
    public @ResponseBody String resetPassword(HttpServletRequest request, @RequestBody AdminChangePassword form) {
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
                    Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
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
     */
    @RequestMapping(value = "/remove-security-question.json", method = RequestMethod.POST)
    public @ResponseBody String removeSecurityQuestion(HttpServletRequest request, @RequestBody String orcidOrEmail) {
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }

        if (StringUtils.isNotEmpty(orcid)) {
            OrcidProfile currentProfile = getEffectiveProfile();
            // Only allow and admin
            if (OrcidType.ADMIN.equals(currentProfile.getType())) {
                String result = adminManager.removeSecurityQuestion(orcid);
                // If the resulting string is not null, it means there was an
                // error
                if (result != null)
                    return result;
            }
        } else {
            return getMessage("admin.errors.unable_to_fetch_info");
        }
        return getMessage("admin.remove_security_question.success");
    }

    /**
     * Admin switch user
     */
    @RequestMapping(value = "/admin-switch-user", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> adminSwitchUser(@ModelAttribute("orcidOrEmail") String orcidOrEmail, RedirectAttributes redirectAttributes) {
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }

        Map<String, String> mapOrcid = null;
        if (StringUtils.isNotEmpty(orcid)) {
            if (profileEntityManager.orcidExists(orcid)) {
                mapOrcid = new HashMap<String, String>();
                if (!profileEntityManager.isProfileClaimed(orcid)) {
                    mapOrcid.put("errorMessg", new StringBuffer("Account for user ").append(orcidOrEmail).append(" is unclaimed.").toString());
                } else if (profileEntityManager.isDeactivated(orcid)) {
                    mapOrcid.put("errorMessg", new StringBuffer("Account for user ").append(orcidOrEmail).append(" is deactivated.").toString());
                } else if (profileEntityManager.isLocked(orcid)) {
                    mapOrcid.put("errorMessg", new StringBuffer("Account for user ").append(orcidOrEmail).append(" is locked.").toString());
                } else {
                    mapOrcid.put("errorMessg", null);
                }
                mapOrcid.put("orcid", orcid);
            }
        }
        return mapOrcid;
    }

    /**
     * Admin verify email
     */
    @RequestMapping(value = "/admin-verify-email.json", method = RequestMethod.POST)
    public @ResponseBody String adminVerifyEmail(@RequestBody String email) {
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
     */
    @RequestMapping(value = "/admin-delegates", method = RequestMethod.POST)
    public @ResponseBody AdminDelegatesRequest startDelegationProcess(@RequestBody AdminDelegatesRequest request) {
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
                Map<String, String> email = findIdByEmailHelper(trusted);
                trusted = email.get(trusted);
            } else {
                request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                haveErrors = true;
            }
        }

        if (!managedIsOrcid) {
            if (emailManager.emailExists(managed)) {
                Map<String, String> email = findIdByEmailHelper(managed);
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
     */
    @RequestMapping(value = "/admin-delegates/check-claimed-status.json", method = RequestMethod.GET)
    public @ResponseBody boolean checkClaimedStatus(@RequestParam("orcidOrEmail") String orcidOrEmail) {
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
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
     * 
     * @param orcidOrEmail
     *            orcid or email of the account we want to lock
     * @return a ProfileDetails object containing either an error message or the
     *         info of the account we want to lock
     */
    @RequestMapping(value = "/check-account-to-lock.json", method = RequestMethod.POST)
    public @ResponseBody ProfileDetails checkAccountToLock(@RequestBody String orcidOrEmail) {
        String orcid = getOrcidFromParam(orcidOrEmail);

        if (PojoUtil.isEmpty(orcid)) {
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.lock_profile.error.not_found", orcidOrEmail));
            return result;
        }
        return generateProfileDetails(orcid, 1);
    }

    /**
     * Function to get the info of an account we want to unlock
     * 
     * @param orcidOrEmail
     *            orcid or email of the account we want to unlock
     * @return a ProfileDetails object containing either an error message or the
     *         info of the account we want to unlock
     */
    @RequestMapping(value = "/check-account-to-unlock.json", method = RequestMethod.POST)
    public @ResponseBody ProfileDetails checkAccountToUnlock(@RequestBody String orcidOrEmail) {
        String orcid = getOrcidFromParam(orcidOrEmail);

        if (PojoUtil.isEmpty(orcid)) {
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.lock_profile.error.not_found", orcidOrEmail));
            return result;
        }

        return generateProfileDetails(orcid, 2);
    }

    private String getOrcidFromParam(String orcidOrEmail) {
        boolean isOrcid = matchesOrcidPattern(orcidOrEmail);
        String orcid = null;
        ProfileDetails result = new ProfileDetails();
        result.setErrors(new ArrayList<String>());
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            }
        } else {
            orcid = orcidOrEmail;
        }

        return orcid;
    }

    /**
     * @param flag
     *            1 : lock 2 : unlock 3 : review 4 : unreview
     */
    private ProfileDetails generateProfileDetails(String orcid, int flag) {
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(orcid, LoadOptions.BIO_ONLY);
        if (flag == 1) {
            if (profile.isLocked()) {
                ProfileDetails result = new ProfileDetails();
                result.setErrors(new ArrayList<String>());
                result.getErrors().add(getMessage("admin.lock_profile.error.already_locked", orcid));
                return result;
            } else if (profile.isReviewed()) {
                ProfileDetails result = new ProfileDetails();
                result.setErrors(new ArrayList<String>());
                result.getErrors().add(getMessage("admin.lock_reviewed_profile.error", orcid));
                return result;
            }
        } else if (flag == 2) {
            if (!profile.isLocked()) {
                ProfileDetails result = new ProfileDetails();
                result.setErrors(new ArrayList<String>());
                result.getErrors().add(getMessage("admin.unlock_profile.error.non_locked", orcid));
                return result;
            }
        } else if (flag == 3) {
            if (profile.isReviewed()) {
                ProfileDetails result = new ProfileDetails();
                result.setErrors(new ArrayList<String>());
                result.getErrors().add(getMessage("admin.review_profile.error.already_reviewed", orcid));
                return result;
            }
        } else if (flag == 4) {
            if (!profile.isReviewed()) {
                ProfileDetails result = new ProfileDetails();
                result.setErrors(new ArrayList<String>());
                result.getErrors().add(getMessage("admin.unreview_profile.error.non_reviewed", orcid));
                return result;
            }
        }

        ProfileDetails profileDetails = new ProfileDetails();

        profileDetails.setOrcid(orcid);
        if (profile != null && profile.getOrcidBio().getPersonalDetails() != null) {
            boolean hasName = false;
            if (profile.getOrcidBio().getPersonalDetails().getFamilyName() != null) {
                profileDetails.setFamilyName(profile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
                hasName = true;
            }
            if (profile.getOrcidBio().getPersonalDetails().getGivenNames() != null) {
                profileDetails.setGivenNames(profile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
                hasName = true;
            }

            if (!hasName) {
                profileDetails.setGivenNames(profile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
            }
        }
        if (profile.getOrcidBio().getContactDetails() != null && profile.getOrcidBio().getContactDetails().getEmail() != null) {
            for (Email email : profile.getOrcidBio().getContactDetails().getEmail()) {
                if (email.isPrimary()) {
                    profileDetails.setEmail(email.getValue());
                    break;
                }
            }
        }

        return profileDetails;
    }

    /**
     * Function to lock an account
     * 
     * @param orcid
     *            The orcid of the account we want to lock
     * @return true if the account was locked, false otherwise
     */
    @RequestMapping(value = "/lock-account.json", method = RequestMethod.POST)
    public @ResponseBody String lockAccount(@RequestBody String orcid) {
        if (profileEntityManager.lockProfile(orcid)) {
            return getMessage("admin.lock_profile.success", orcid);
        }
        return getMessage("admin.lock_profile.error.couldnt_lock_account", orcid);
    }

    /**
     * Function to unlock an account
     * 
     * @param orcid
     *            The orcid of the account we want to unlock
     * @return true if the account was unlocked, false otherwise
     */
    @RequestMapping(value = "/unlock-account.json", method = RequestMethod.POST)
    public @ResponseBody String unlockAccount(@RequestBody String orcid) {
        if (profileEntityManager.unlockProfile(orcid)) {
            return getMessage("admin.unlock_profile.success", orcid);
        }
        return getMessage("admin.unlock_profile.error.couldnt_unlock_account", orcid);
    }

    @RequestMapping(value = "/unreview-account.json", method = RequestMethod.POST)
    public @ResponseBody String unreviewAccount(@RequestBody String orcid) {
        if (profileEntityManager.unreviewProfile(orcid)) {
            return getMessage("admin.unreview_profile.success", orcid);
        }
        return getMessage("admin.unreview_profile.error.couldnt_unreview_account", orcid);
    }

    @RequestMapping(value = "/review-account.json", method = RequestMethod.POST)
    public @ResponseBody String reviewAccount(@RequestBody String orcid) {
        if (profileEntityManager.reviewProfile(orcid)) {
            return getMessage("admin.review_profile.success", orcid);
        }
        return getMessage("admin.review_profile.error.couldnt_review_account", orcid);
    }

    @RequestMapping(value = "/check-account-to-review.json", method = RequestMethod.POST)
    public @ResponseBody ProfileDetails checkAccountToReview(@RequestBody String orcidOrEmail) {
        String orcid = getOrcidFromParam(orcidOrEmail);

        if (PojoUtil.isEmpty(orcid)) {
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.review_profile.error.not_found", orcidOrEmail));
            return result;
        }
        return generateProfileDetails(orcid, 3);
    }

    @RequestMapping(value = "/check-account-to-unreview.json", method = RequestMethod.POST)
    public @ResponseBody ProfileDetails checkAccountToUnreview(@RequestBody String orcidOrEmail) {
        String orcid = getOrcidFromParam(orcidOrEmail);

        if (PojoUtil.isEmpty(orcid)) {
            ProfileDetails result = new ProfileDetails();
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(getMessage("admin.review_profile.error.not_found", orcidOrEmail));
            return result;
        }

        return generateProfileDetails(orcid, 4);
    }

    @RequestMapping(value = "/resend-claim.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, List<String>> resendClaimEmail(@RequestBody String emailsOrOrcids) {
        List<String> emailOrOrcidList = new ArrayList<String>();
        if (StringUtils.isNotBlank(emailsOrOrcids)) {
            StringTokenizer tokenizer = new StringTokenizer(emailsOrOrcids, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                emailOrOrcidList.add(tokenizer.nextToken());
            }
        }

        List<String> claimedIds = new ArrayList<String>();
        List<String> successIds = new ArrayList<String>();
        List<String> notFoundIds = new ArrayList<String>();
        for (String emailOrOrcid : emailOrOrcidList) {
            String orcid = getOrcidFromParam(emailOrOrcid);
            if (orcid == null) {
                notFoundIds.add(emailOrOrcid);
            } else {
                OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(orcid);
                if (profile == null) {
                    notFoundIds.add(emailOrOrcid);
                } else {
                    if (profile.getOrcidHistory() != null && profile.getOrcidHistory().isClaimed()) {
                        claimedIds.add(emailOrOrcid);
                    } else {
                        notificationManager.sendApiRecordCreationEmail(emailOrOrcid, profile);
                        successIds.add(emailOrOrcid);
                    }
                }
            }
        }
        Map<String, List<String>> resendIdMap = new HashMap<String, List<String>>();
        resendIdMap.put("notFoundList", notFoundIds);
        resendIdMap.put("claimResendSuccessfulList", successIds);
        resendIdMap.put("alreadyClaimedList", claimedIds);
        return resendIdMap;
    }

}
