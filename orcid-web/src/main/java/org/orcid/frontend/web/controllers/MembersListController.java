/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import org.orcid.core.manager.SalesForceManager;
import org.orcid.pojo.SalesForceDetails;
import org.orcid.pojo.SalesForceMember;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
@RequestMapping("/members-list")
public class MembersListController extends BaseController {

    @Resource
    private SalesForceManager salesForceManager;

    @RequestMapping
    public ModelAndView membersList() {
        ModelAndView mav = new ModelAndView("members-list");
        return mav;
    }

    @RequestMapping(value = "/members.json", method = RequestMethod.GET)
    public @ResponseBody List<SalesForceMember> retrieveMembers() {
        return salesForceManager.retrieveMembers();
    }

    @RequestMapping(value = "/details.json", method = RequestMethod.GET)
    public @ResponseBody SalesForceDetails retrieveDetails(@RequestParam("memberId") String memberId,
            @RequestParam(value = "consortiumLeadId", required = false) String consortiumLeadId) {
        return salesForceManager.retrieveDetails(memberId, consortiumLeadId);
    }

}
