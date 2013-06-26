package org.orcid.frontend.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copyright 2012-2013 ORCID
 * 
 * @author Angel Montenegro Date: 20/06/2013
 */
@Controller("ManageClientsController")
@RequestMapping(value = "/manage-clients")
public class ManageClientsController extends BaseWorkspaceController {

    @RequestMapping
    public ModelAndView manageClients() {
        ModelAndView mav = new ModelAndView("manage-clients");
        return mav;
    }
    
}
