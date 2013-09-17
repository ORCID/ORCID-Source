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

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
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
        ModelAndView mav = new ModelAndView("manage_clients");        
        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(getEffectiveUserOrcid());
        mav.addObject("group", group);        
        return mav;
    }
    
    @RequestMapping(value = "/add-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody OrcidClient createClient(HttpServletRequest request, @RequestBody OrcidClient orcidClient) {                
        String groupOrcid = getEffectiveUserOrcid();
        
        return orcidClientGroupManager.createAndPersistClientProfile(groupOrcid, orcidClient);
    }
    
    @RequestMapping(value = "/get-clients.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody List<OrcidClient> getClients(){
        String groupOrcid = getEffectiveUserOrcid();
        
        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(groupOrcid);
                
        return group.getOrcidClient();
    }
    
    @RequestMapping(value = "/edit-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody OrcidClient editClient(HttpServletRequest request, @RequestBody OrcidClient orcidClient){
        String groupOrcid = getEffectiveUserOrcid();
        
        orcidClient = orcidClientGroupManager.updateClientProfile(groupOrcid, orcidClient);
        
        return orcidClient;        
    }
}