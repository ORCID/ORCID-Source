package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
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
    public @ResponseBody ProfileDeprecationRequest deprecateProfile(@RequestBody ProfileDeprecationRequest profileDeprecationRequest) {
        return null;        
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
