package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.SpamManager;
import org.orcid.pojo.Spam;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Daniel Palafox
 */
@Controller("spamController")
@RequestMapping(value = { "/spam" })
public class SpamController extends BaseController {  
    
    @Resource(name = "spamManager")
    SpamManager spamManager;     

    @RequestMapping(value = "/get-spam.json", method = RequestMethod.GET)
    public @ResponseBody Spam getSpam(@RequestParam("orcid") String orcid) {        
        org.orcid.jaxb.model.v3.release.record.Spam r = spamManager.getSpam(orcid);
        return Spam.fromValue(r);
    }
    
    @RequestMapping(value = "/report-spam.json", method = RequestMethod.POST)
    public @ResponseBody boolean reportSpam(@RequestBody String orcid) {
        if(OrcidStringUtils.isValidOrcid(orcid)) {
            return spamManager.createOrUpdateSpam(orcid);               
        }         
        return false;        
    }
    
    @RequestMapping(value = "/remove-spam.json", method = RequestMethod.DELETE)
    public @ResponseBody boolean removeSpam(@RequestBody String orcid) {
        if(OrcidStringUtils.isValidOrcid(orcid)) {
            return spamManager.removeSpam(orcid);
        }         
        return false;    
    }
   
}
