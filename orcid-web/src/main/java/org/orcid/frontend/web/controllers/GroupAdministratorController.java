package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ajaxForm.Client;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copyright 2012-2013 ORCID
 * 
 * @author Angel Montenegro Date: 20/06/2013
 */
@Controller("GroupAdministratorController")
@RequestMapping(value = "/manage-clients")
public class GroupAdministratorController extends BaseWorkspaceController {

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;
    
    @RequestMapping
    public ModelAndView manageClients() {
        ModelAndView mav = new ModelAndView("manage-clients");
        OrcidProfile profile = getCurrentUserAndRefreshIfNecessary().getEffectiveProfile();
        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(profile.getOrcid().getValue());
        mav.addObject("group", group);
        mav.addObject("client-types", RedirectUriType.values());
        return mav;
    }
    
    @RequestMapping(value = "/add-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody Client createClient(HttpServletRequest request, @RequestBody Client client) {        
        System.out.println(client.getDisplayName());
        System.out.println(client.getShortDescription());
        System.out.println(client.getWebsite());
        
        return null;
    }            
}
