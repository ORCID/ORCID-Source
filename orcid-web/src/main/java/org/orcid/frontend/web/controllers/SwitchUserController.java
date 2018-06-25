package org.orcid.frontend.web.controllers;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.GivenPermissionToManager;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SwitchUserController extends BaseController {

    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;
    
    @Resource
    private ProfileLastModifiedAspect profileLastModifiedAspect;
    
    private long getLastModified(String orcid) {
        Date lastModified = profileLastModifiedAspect.retrieveLastModifiedDate(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }
    
    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getListOfAccountsToSwitch() {
        
        givenPermissionToManagerReadOnly.findByReceiver(getCurrentUserOrcid(), lastModified);
    }
}
