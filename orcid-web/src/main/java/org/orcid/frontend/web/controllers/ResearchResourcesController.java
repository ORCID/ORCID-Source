package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.orcid.pojo.ajaxForm.ResearchResourceForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("researchResourceController")
@RequestMapping(value = { "/research-resources" })
public class ResearchResourcesController extends BaseWorkspaceController {

    /**
     * Returns a blank research resource form
     * */
    @RequestMapping(value = "/research-resource.json", method = RequestMethod.GET)
    public @ResponseBody ResearchResourceForm getEmptyresearchResource() {
        return null;        
    }
    
    /**
     * List fundings associated with a profile
     * */
    @RequestMapping(value = "/research-resource-ids.json", method = RequestMethod.GET)
    public @ResponseBody List<String> getresearchResourceIdsJson(HttpServletRequest request) {
        return null;
    }
    
    /**
     * List research resources associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-research-resources.json", method = RequestMethod.GET)
    public @ResponseBody List<ResearchResourceForm> getresearchResourcesJson(HttpServletRequest request, @RequestParam(value = "researchResourceIds") String researchResourceIdsStr) {
        return null;
    }
     
    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/research-resource.json", method = RequestMethod.POST)
    public @ResponseBody ResearchResourceForm postresearchResource(@RequestBody ResearchResourceForm researchResource) {
        return null;
    }
    
    /**
     * List research resources associated with a profile
     * */
    @RequestMapping(value = "/get-research-resource.json", method = RequestMethod.GET)
    public @ResponseBody ResearchResourceForm getresearchResourceJson(@RequestParam(value = "researchResourceId") Long researchResourceId) {
        return null;
    }
    
    /**
     * Deletes a research resource
     * */
    @RequestMapping(value = "/research-resource.json", method = RequestMethod.DELETE)
    public @ResponseBody ResearchResourceForm deleteresearchResourceJson(@RequestBody ResearchResourceForm researchResource) {
        return null;
    }
    
    @RequestMapping(value = "/{researchResourceIdsStr}", method = RequestMethod.DELETE)
    public @ResponseBody List<String> removeresearchResources(@PathVariable("researchResourceIdsStr") String researchResourceIdsStr) {
        return null;
    }
    
    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(HttpServletRequest request, @RequestParam(value = "putCode") Long putCode) {
        return false;
    }
    
    /**
     * updates visibility of a research resource
     * */
    @RequestMapping(value = "/{researchResourceIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateVisibilitys(@PathVariable("researchResourceIdsStr") String researchResourceIdsStr, @PathVariable("visibilityStr") String visibilityStr) {
        return null;
    }
    
    //validation
    
    //autocomplete
    
    
    //what does it need to do?
    //list of groups
    //Return a paginated list of groups like works: 
    //affiliation for comparisson: https://github.com/ORCID/ORCID-Source/tree/CreatingActivityGroup
    //READ ONLY
}
