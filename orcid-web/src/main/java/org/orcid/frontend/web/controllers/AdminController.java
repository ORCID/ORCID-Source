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
    
    @Resource
    ProfileEntityManager profileEntityManager;
    
    @Resource
    NotificationManager notificationManager;
    
    @RequestMapping(value = "/deprecate-profile")
    public ModelAndView getDeprecatedProfilesPage(){
        return new ModelAndView("profile_deprecation");
    }
    
    @RequestMapping(value = { "/deprecate-profile/get-empty-deprecation-request.json" }, method = RequestMethod.GET)
    public @ResponseBody ProfileDeprecationRequest getPendingDeprecationRequest() {
        return new ProfileDeprecationRequest(); 
    }
    
    @RequestMapping(value = { "/deprecate-profile/verify-deprecation-request.json" }, method = RequestMethod.GET)
    public @ResponseBody ProfileDeprecationRequest verifyDeprecationRequest(@RequestBody ProfileDeprecationRequest profileDeprecationRequest) {
        return null;
    }
    
    @RequestMapping(value = { "/deprecate-profile/deprecate-profile.json" }, method = RequestMethod.GET)
    public @ResponseBody ProfileDeprecationRequest deprecateProfile(@RequestParam("deprecated") String deprecatedOrcid, @RequestParam("primary") String primaryOrcid) {
        ProfileDeprecationRequest result = new ProfileDeprecationRequest();
        //Check for errors
        if(deprecatedOrcid == null || !OrcidStringUtils.isValidOrcid(deprecatedOrcid)){
            result.getDeprecatedAccount().getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", deprecatedOrcid));
        } else if(primaryOrcid == null || !OrcidStringUtils.isValidOrcid(primaryOrcid)) {
            result.getPrimaryAccount().getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", primaryOrcid));
        } else if(deprecatedOrcid.equals(primaryOrcid)){
            result.getErrors().add(getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"));
        } else {         
        	//If there are no errros, process with the deprecation
            //Get deprecated profile
        	ProfileEntity deprecated = profileEntityManager.findByOrcid(deprecatedOrcid);
        	//Get primary profile
            ProfileEntity primary = profileEntityManager.findByOrcid(primaryOrcid);
            //Get the list of emails from the deprecated account            
            Set<EmailEntity> deprecatedAccountEmails = deprecated.getEmails();
            // For each email in the deprecated profile
            for(EmailEntity email : deprecatedAccountEmails){
            	// Delete each email from the deprecated profile
            	emailManager.removeEmail(email.getProfile().getId(), email.getId());
                // Copy that email to the primary profile
            	emailManager.addEmail(primary.getId(), email.getId(), email.getVisibility(), email.getSource().getId());
            }
            //Update the deprecated profile            
            deprecated.setPrimaryRecord(primary);
            deprecated.setDeprecationDate(new Date());            
            profileEntityManager.updateProfile(deprecated);
            //Send notifications
            notificationManager.sendProfileDeprecationEmail(deprecated, primary);
            //Update the deprecation request object that will be returned 
            result.setDeprecatedAccount(new ProfileDetails(deprecated.getId(), deprecated.getGivenNames(), deprecated.getFamilyName()));                        
            result.setPrimaryAccount(new ProfileDetails(primary.getId(), primary.getGivenNames(), primary.getFamilyName()));
            result.setDeprecatedDate(new Date());
        }
        
        return result;        
    }
    
    @RequestMapping(value = { "/deprecate-profile/check-orcid.json" }, method = RequestMethod.GET)
    public @ResponseBody ProfileDetails checkOrcid(@RequestParam("orcid") String orcid) {
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        ProfileDetails profileDetails = new ProfileDetails();
        if(profile != null){            
            profileDetails.setOrcid(profile.getId());
            profileDetails.setFamilyName(profile.getFamilyName());
            profileDetails.setGivenNames(profile.getGivenNames());
        } else {
            profileDetails.getErrors().add(getMessage("admin.profile_deprecation.errors.inexisting_orcid", orcid));
        }                       
        return profileDetails;        
    }
}
