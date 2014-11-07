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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
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
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
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

    public static String MANAGED_USER_PARAM = "managed";
    public static String TRUSTED_USER_PARAM = "trusted";

    public static String AUTHORIZE_DELEGATION_ACTION = "/manage/authorize-delegates";

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
    EmailManager emailManager;

    @Resource
    ClientDetailsManager clientDetailsManager;

    @Resource
    private GroupAdministratorController groupAdministratorController;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

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
            // If there are no errors, process with the deprecation
            // Get deprecated profile
            ProfileEntity deprecated = profileEntityManager.findByOrcid(deprecatedOrcid);
            // Get primary profile
            ProfileEntity primary = profileEntityManager.findByOrcid(primaryOrcid);
            // If both users exists
            if (deprecated != null && primary != null) {
                // If account is already deprecated
                if (deprecated.getPrimaryRecord() != null) {
                    result.getErrors().add(getMessage("admin.profile_deprecation.errors.already_deprecated", deprecatedOrcid));
                } else if (primary.getPrimaryRecord() != null) {
                    // If primary is deprecated
                    result.getErrors().add(getMessage("admin.profile_deprecation.errors.primary_account_deprecated", primaryOrcid));
                } else {
                    // If the primary account is deactivated, return an error
                    if (primary.getDeactivationDate() != null) {
                        result.getErrors().add(getMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", primaryOrcid));
                    } else {
                        // Deprecates the account
                        LOGGER.info("About to deprecate account {} to primary account: {}", deprecated.getId(), primary.getId());
                        boolean wasDeprecated = profileEntityManager.deprecateProfile(deprecated, primary);
                        // If it was successfully deprecated
                        if (wasDeprecated) {
                            LOGGER.info("Account {} was deprecated to primary account: {}", deprecated.getId(), primary.getId());
                            // 1. Update deprecated profile
                            deprecated.setDeactivationDate(new Date());
                            deprecated.setGivenNames("Given Names Deactivated");
                            deprecated.setFamilyName("Family Name Deactivated");
                            deprecated.setOtherNamesVisibility(Visibility.PRIVATE);
                            deprecated.setCreditNameVisibility(Visibility.PRIVATE);
                            deprecated.setExternalIdentifiersVisibility(Visibility.PRIVATE);
                            deprecated.setBiographyVisibility(Visibility.PRIVATE);
                            deprecated.setKeywordsVisibility(Visibility.PRIVATE);
                            deprecated.setResearcherUrlsVisibility(Visibility.PRIVATE);
                            deprecated.setProfileAddressVisibility(Visibility.PRIVATE);
                            deprecated.setBiography(new String());
                            deprecated.setIso2Country(null);
                            profileEntityManager.updateProfile(deprecated);

                            // 2. Remove works
                            if (deprecated.getProfileWorks() != null) {
                                for (ProfileWorkEntity profileWork : deprecated.getProfileWorks()) {
                                    profileWorkManager.removeWork(deprecated.getId(), String.valueOf(profileWork.getWork().getId()));
                                }
                            }

                            // 3. Remove external identifiers
                            if (deprecated.getExternalIdentifiers() != null) {
                                for (ExternalIdentifierEntity externalIdentifier : deprecated.getExternalIdentifiers()) {
                                    externalIdentifierManager.removeExternalIdentifier(deprecated.getId(), externalIdentifier.getExternalIdReference());
                                }
                            }

                            // 4. Move all emails to the primary email
                            Set<EmailEntity> deprecatedAccountEmails = deprecated.getEmails();
                            if (deprecatedAccountEmails != null) {
                                // For each email in the deprecated profile
                                for (EmailEntity email : deprecatedAccountEmails) {
                                    // Delete each email from the deprecated
                                    // profile
                                    LOGGER.info("About to delete email {} from profile {}", email.getId(), email.getProfile().getId());
                                    emailManager.removeEmail(email.getProfile().getId(), email.getId(), true);
                                    // Copy that email to the primary profile
                                    LOGGER.info("About to add email {} to profile {}", email.getId(), primary.getId());
                                    emailManager.addEmail(primary.getId(), email);
                                }
                            }

                            // Send notifications
                            LOGGER.info("Sending deprecation notifications to {} and {}", deprecated.getId(), primary.getId());

                            // TODO: Currently we dont want to send the
                            // notifications, but in a near future, when we want
                            // to send them, just uncomment the following line:
                            // notificationManager.sendProfileDeprecationEmail(deprecated,
                            // primary);

                            // Update the deprecation request object that will
                            // be
                            // returned
                            result.setDeprecatedAccount(new ProfileDetails(deprecated.getId(), deprecated.getGivenNames(), deprecated.getFamilyName(), deprecated
                                    .getPrimaryEmail().getId()));
                            result.setPrimaryAccount(new ProfileDetails(primary.getId(), primary.getGivenNames(), primary.getFamilyName(), primary.getPrimaryEmail()
                                    .getId()));
                            result.setDeprecatedDate(new Date());
                        }
                    }
                }
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
    ProfileDetails confirmDeactivateOrcidAccount(@RequestParam("orcid") String orcid) {
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
    ProfileDetails confirmReactivateOrcidAccount(@RequestParam("orcid") String orcid) {
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

        if (StringUtils.isNotEmpty(orcid) && profileEntityManager.orcidExists(orcid)) {
            OrcidProfile currentProfile = getEffectiveProfile();
            if (OrcidType.ADMIN.equals(currentProfile.getType())) {
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                if (orcidProfile != null) {
                    orcidProfile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(null);
                    orcidProfile.setSecurityQuestionAnswer(null);
                    orcidProfileManager.updateSecurityQuestionInformation(orcidProfile);
                } else {
                    return getMessage("admin.errors.unexisting_orcid");
                }
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
        boolean managedIsOrcid = matchesOrcidPattern(managed);
        if (!managedIsOrcid) {
            if (emailManager.emailExists(managed)) {
                Map<String, String> email = findIdByEmail(managed);
                managed = email.get(managed);
            } else {
                request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getManaged().getValue()));
                haveErrors = true;
            }
        }

        // The permission should not already exist
        if (givenPermissionToDao.findByGiverAndReceiverOrcid(managed, trusted) != null) {
            request.getErrors().add(getMessage("admin.delegate.error.permission_already_exists", trusted, managed));
            haveErrors = true;
        }

        if (haveErrors)
            return request;

        // Restriction #1: Both accounts must be claimed
        boolean isTrustedClaimed = profileEntityManager.isProfileClaimed(trusted);
        boolean isManagedClaimed = profileEntityManager.isProfileClaimed(managed);

        if (!isTrustedClaimed || !isManagedClaimed) {
            if (!isTrustedClaimed && !isManagedClaimed) {
                request.getErrors().add(getMessage("admin.delegate.error.not_claimed.both", trusted, request.getManaged().getValue()));
            } else if (!isTrustedClaimed) {
                request.getTrusted().getErrors().add(getMessage("admin.delegate.error.not_claimed", request.getTrusted().getValue()));
            } else {
                request.getManaged().getErrors().add(getMessage("admin.delegate.error.not_claimed", request.getManaged().getValue()));
            }
            haveErrors = true;
        }

        // Restriction #2: Trusted individual must have a verified primary email
        // address
        if (!emailManager.isPrimaryEmailVerified(trusted)) {
            request.getTrusted().getErrors().add(getMessage("admin.delegate.error.primary_email_not_verified", request.getTrusted().getValue()));
            haveErrors = true;
        }

        // Restriction #3: They cant be the same account
        if (trusted.equalsIgnoreCase(managed)) {
            request.getErrors().add(getMessage("admin.delegate.error.cant_be_the_same", request.getTrusted().getValue(), request.getManaged().getValue()));
            haveErrors = true;
        }

        if (haveErrors)
            return request;

        // Generate link
        String link = generateEncryptedLink(trusted, managed);
        // Get primary emails
        OrcidProfile managedOrcidProfile = orcidProfileManager.retrieveClaimedOrcidProfile(managed);
        OrcidProfile trustedOrcidProfile = orcidProfileManager.retrieveClaimedOrcidProfile(trusted);
        // Send email to managed account
        notificationManager.sendDelegationRequestEmail(managedOrcidProfile, trustedOrcidProfile, link);

        request.setSuccessMessage(getMessage("admin.delegate.admin.success", managedOrcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue()));

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

    /**
     * Encrypts a string
     * 
     * @param unencrypted
     * @return the string encrypted
     * */
    private String generateEncryptedLink(String trusted, String managed) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(TRUSTED_USER_PARAM, trusted);
        params.put(MANAGED_USER_PARAM, managed);
        String paramsString = JSON.toString(params);
        if (StringUtils.isNotBlank(paramsString)) {
            String encryptedParams = encryptionManager.encryptForExternalUse(paramsString);
            String base64EncodedParams = Base64.encodeBase64URLSafeString(encryptedParams.getBytes());
            String url = getBaseUri() + AUTHORIZE_DELEGATION_ACTION + "?key=" + base64EncodedParams;
            return url;
        } else {
            return null;
        }
    }

    private boolean matchesOrcidPattern(String orcid) {
        return OrcidStringUtils.isValidOrcid(orcid);
    }
}
