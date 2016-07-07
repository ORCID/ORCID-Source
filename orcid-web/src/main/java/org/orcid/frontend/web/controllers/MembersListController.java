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
import org.springframework.web.bind.annotation.PathVariable;
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
public class MembersListController extends BaseController {

    @Resource
    private SalesForceManager salesForceManager;

    @RequestMapping("/members")
    public ModelAndView membersList() {
        ModelAndView mav = new ModelAndView("members-list");
        return mav;
    }
    
    @RequestMapping("/members/{memberSlug}")
    public ModelAndView memberPage(@PathVariable("memberSlug") String memberSlug) {
        ModelAndView mav = new ModelAndView("member-page");
        mav.addObject("memberName", memberSlug);
        return mav;
    }
    
    @RequestMapping(value = "/members/members.json", method = RequestMethod.GET)
    public @ResponseBody List<SalesForceMember> retrieveMembers() {
        return salesForceManager.retrieveMembers();
    }

    @RequestMapping(value = "/members/details.json", method = RequestMethod.GET)
    public @ResponseBody SalesForceDetails retrieveDetails(@RequestParam("memberId") String memberId,
            @RequestParam(value = "consortiumLeadId", required = false) String consortiumLeadId) {
        return salesForceManager.retrieveDetails(memberId, consortiumLeadId);
    }

    @RequestMapping("/consortia")
    public ModelAndView consortiaList() {
        ModelAndView mav = new ModelAndView("consortia-list");
        return mav;
    }
    
    @RequestMapping(value = "/consortia/consortia.json", method = RequestMethod.GET)
    public @ResponseBody List<SalesForceMember> retrieveConsortia() {
        return salesForceManager.retrieveConsortia();
    }

}
