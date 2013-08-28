package org.orcid.frontend.web.controllers;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
public class AdminController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    
    @Resource
    ProfileEntityManager profileEntityManager;    
    
    @Resource
    NotificationManager notificationManager;

    @RequestMapping(value = "/deprecate-profile")
    public ModelAndView getDeprecatedProfilesPage() {
        return new ModelAndView("profile_deprecation");
    }

    @RequestMapping(value = { "/deprecate-profile/get-empty-deprecation-request.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDeprecationRequest getPendingDeprecationRequest() {
        return new ProfileDeprecationRequest();
    }

    @RequestMapping(value = { "/deprecate-profile/verify-deprecation-request.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDeprecationRequest verifyDeprecationRequest(@RequestBody ProfileDeprecationRequest profileDeprecationRequest) {
        return null;
    }

    /**
     * TODO
     * */
    @RequestMapping(value = { "/deprecate-profile/deprecate-profile.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDeprecationRequest deprecateProfile(@RequestParam("deprecated") String deprecatedOrcid, @RequestParam("primary") String primaryOrcid) {
        ProfileDeprecationRequest result = new ProfileDeprecationRequest();
        // Check for errors
        if (deprecatedOrcid == null || !OrcidStringUtils.isValidOrcid(deprecatedOrcid)) {
            result.getDeprecatedAccount().getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", deprecatedOrcid));
        } else if (primaryOrcid == null || !OrcidStringUtils.isValidOrcid(primaryOrcid)) {
            result.getPrimaryAccount().getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", primaryOrcid));
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
                // If the primary account is deactivated, return an error
                if (primary.getPrimaryRecord() != null) {
                    result.getErrors().add(getMessage("admin.profile_deprecation.errors.primary_account_is_deprecated", primary.getId()));
                } else {                    
                    // Deprecates the account
                    LOGGER.info("About to deprecate account %s to primary account: %s", deprecated.getId(), primary.getId());
                    boolean wasDeprecated = profileEntityManager.deprecateProfile(deprecated, primary);                    
                    // If it was successfully deprecated
                    if(wasDeprecated) {
                        LOGGER.info("Account %s was deprecated to primary account: %s", deprecated.getId(), primary.getId());
                        // Get the list of emails from the deprecated account
                        Set<EmailEntity> deprecatedAccountEmails = deprecated.getEmails();
                        if(deprecatedAccountEmails != null) {
                            // For each email in the deprecated profile
                            for (EmailEntity email : deprecatedAccountEmails) {
                                // Delete each email from the deprecated profile
                                LOGGER.info("About to delete email %s from profile %s", email.getId(), email.getProfile().getId());
                                emailManager.removeEmail(email.getProfile().getId(), email.getId(), true);
                                // Copy that email to the primary profile                        
                                LOGGER.info("About to add email %s to profile %s", email.getId(), primary.getId());
                                emailManager.addEmail(primary.getId(), email.getId(), email.getVisibility(), email.getSource() == null ? null : email.getSource().getId());
                            }                    
                        }
                                        
                        // Send notifications
                        LOGGER.info("Sending deprecation notifications to %s and %s", deprecated.getId(), primary.getId());
                        notificationManager.sendProfileDeprecationEmail(deprecated, primary);
                        // Update the deprecation request object that will be
                        // returned
                        result.setDeprecatedAccount(new ProfileDetails(deprecated.getId(), deprecated.getGivenNames(), deprecated.getFamilyName(), deprecated
                            .getPrimaryEmail().getId()));
                        result.setPrimaryAccount(new ProfileDetails(primary.getId(), primary.getGivenNames(), primary.getFamilyName(), primary.getPrimaryEmail().getId()));
                        result.setDeprecatedDate(new Date());
                    }
                }
            }
        }

        return result;
    }

    /**
     * TODO
     * */
    @RequestMapping(value = { "/deprecate-profile/check-orcid.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ProfileDetails checkOrcid(@RequestParam("orcid") String orcid) {
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        ProfileDetails profileDetails = new ProfileDetails();
        if (profile != null) {
            if(profile.getPrimaryRecord() != null){
                profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.already_deprecated", orcid));
            } else {
                profileDetails.setOrcid(profile.getId());
                profileDetails.setFamilyName(profile.getFamilyName());
                profileDetails.setGivenNames(profile.getGivenNames());
                profileDetails.setEmail(profile.getPrimaryEmail().getId());
            }
        } else {
            profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.inexisting_orcid", orcid));
        }
        return profileDetails;
    }
}
