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

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdministratorController.class);
	
    @Resource
    OrcidClientGroupManager orcidClientGroupManager;
    
    @RequestMapping
    public ModelAndView manageClients() {
        ModelAndView mav = new ModelAndView("manage_clients");  
        OrcidProfile profile = getEffectiveProfile();
        
        if(profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)){        	
        	LOGGER.warn("Trying to access manage-clients page with user {} which is not a group", profile.getOrcid().getValue());
        	return new ModelAndView("redirect:/my-orcid");
        }
        
        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(profile.getOrcid().getValue());
        mav.addObject("group", group);   
        switch(profile.getGroupType()){
        case BASIC:
        	mav.addObject("clientType", "UPDATER");
        	break;
        case PREMIUM:
        	mav.addObject("clientType", "PREMIUM_UPDATER");
        	break;
        case BASIC_INSTITUTION: 
        	mav.addObject("clientType", "CREATOR");
        	break;
        case PREMIUM_INSTITUTION:
        	mav.addObject("clientType", "PREMIUM_CREATOR");
        	break;        
        }
                
        return mav;
    }
    
    @RequestMapping(value = "/add-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody OrcidClient createClient(HttpServletRequest request, @RequestBody OrcidClient orcidClient) {                
        OrcidProfile profile = getEffectiveProfile();
        String groupOrcid = profile.getOrcid().getValue();
        
        if(profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)){
        	LOGGER.warn("Trying to create client with non group user {}", profile.getOrcid().getValue());
        	throw new OrcidClientGroupManagementException("Your account is not allowed to do this operation.");
        }
        
        OrcidClient result = null;
        
        try {
        	result = orcidClientGroupManager.createAndPersistClientProfile(groupOrcid, orcidClient);
        } catch (OrcidClientGroupManagementException e){
        	LOGGER.error(e.getMessage());
        	result = new OrcidClient();
        	result.setErrors(new ErrorDesc(getMessage("manage_clients.cannot_create_client")));
        }
        
        return result;
    }
    
    @RequestMapping(value = "/get-clients.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody List<OrcidClient> getClients(){
    	OrcidProfile profile = getEffectiveProfile();
    	String groupOrcid = profile.getOrcid().getValue();
        
        if(profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)){
        	LOGGER.warn("Trying to get clients of non group user {}", profile.getOrcid().getValue());
        	throw new OrcidClientGroupManagementException("Your account is not allowed to do this operation.");
        }
        
        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(groupOrcid);
                
        return group.getOrcidClient();
    }
    
    @RequestMapping(value = "/edit-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody OrcidClient editClient(HttpServletRequest request, @RequestBody OrcidClient orcidClient){
    	OrcidProfile profile = getEffectiveProfile();
    	String groupOrcid = profile.getOrcid().getValue();
        
        if(profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)){
        	LOGGER.warn("Trying to edit client with non group user {}", profile.getOrcid().getValue());
        	throw new OrcidClientGroupManagementException("Your account is not allowed to do this operation.");
        }
        
        try {
        	orcidClient = orcidClientGroupManager.updateClientProfile(groupOrcid, orcidClient);
        } catch(OrcidClientGroupManagementException e){
        	LOGGER.error(e.getMessage());
        	OrcidClient error = new OrcidClient();
        	error.setErrors(new ErrorDesc(getMessage("manage_clients.unable_to_update")));
        	return error;
        }
        
        return orcidClient;        
    }
}