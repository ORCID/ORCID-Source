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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
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

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/admin-actions" })
public class AdminController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

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
    EmailManager emailManager;

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
        GroupType [] groupTypes = GroupType.values();
        Map<String, String> groupTypesMap = new TreeMap<String, String>();
        
        for(GroupType groupType : groupTypes){
            String key = groupType.value();
            String value = key.replace('-', ' ');
            groupTypesMap.put(key, value); 
        }
        
        return groupTypesMap;
    }
    
    @RequestMapping
    public ModelAndView getDeprecatedProfilesPage() {
        return new ModelAndView("admin_actions");
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
    ProfileDetails checkOrcid(@RequestParam("orcid") String orcid) {
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

    /**
     * Get an empty group
     * @return an empty group
     * */
    @RequestMapping(value = "/group.json", method = RequestMethod.GET)
    public @ResponseBody
    Group getEmptyGroup() {
        Group group = new Group();
        group.setEmail(Text.valueOf(""));
        group.setGroupName(Text.valueOf(""));
        group.setGroupOrcid(Text.valueOf(""));
        //Set the default type as basic
        group.setType(Text.valueOf(GroupType.BASIC.value()));
        return group;
    }
    
    /**
     * Create a group
     * @param the group to be created
     * @return the group with the orcid information or with the error information
     * */
    @RequestMapping(value = "/create-group.json", method = RequestMethod.POST)
    public @ResponseBody
    Group createGroup(@RequestBody Group group) {
        group.setErrors(new ArrayList<String>());

        validateGroupEmail(group);
        validateGroupName(group);
        validateGroupType(group);

        copyErrors(group.getEmail(), group);
        copyErrors(group.getGroupName(), group);
        copyErrors(group.getType(), group);

        if (group.getErrors().size() == 0) {
            OrcidClientGroup orcidClientGroup = group.toOrcidClientGroup();
            orcidClientGroup = orcidClientGroupManager.createGroup(orcidClientGroup);
            group.setGroupOrcid(Text.valueOf(orcidClientGroup.getGroupOrcid()));
        }

        return group;
    }
   
    private void validateGroupEmail(Group group) {
        group.getEmail().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getEmail())) {
            setError(group.getEmail(), "NotBlank.group.email");
        } else if (!validateEmailAddress(group.getEmail().getValue())) {
            setError(group.getEmail(), "group.email.invalid_email");
        } else if (emailManager.emailExists(group.getEmail().getValue())) {
            setError(group.getEmail(), "group.email.already_used");
        }
    }
    
    private void validateGroupName(Group group) {
        group.getGroupName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getGroupName())) {
            setError(group.getGroupName(), "NotBlank.group.name");
        } else if (group.getGroupName().getValue().length() > 150) {
            setError(group.getGroupName(), "group.name.too_long");
        }
    }
    
    private void validateGroupType(Group group) {
        group.getType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getType())) {
            setError(group.getType(), "NotBlank.group.type");
        } else {
            try {
                GroupType.fromValue(group.getType().getValue());
            } catch (IllegalArgumentException e) {
                setError(group.getType(), "group.type.invalid");
            }
        }
    }
    
    /**
     * Fetch all groups that exists on database
     * 
     * @return the list of groups that exists on database
     * */
    @RequestMapping(value = "/list-groups.json", method = RequestMethod.GET)
    public @ResponseBody
    List<Group> listGroups() {
    	List<ProfileEntity> profileEntities = profileEntityManager.findProfilesByOrcidType(OrcidType.GROUP);
    	List<Group> groups = new ArrayList<Group>();
    	for(ProfileEntity profile : profileEntities){
    		groups.add(Group.fromProfileEntity(profile));
    	}
    	return groups;
    }
}
