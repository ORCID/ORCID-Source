package org.orcid.frontend.web.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.SalesForceManager;
import org.orcid.pojo.SalesForceIntegration;
import org.orcid.pojo.SalesForceMember;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/{memberId}/integrations.json", method = RequestMethod.GET)
    public @ResponseBody List<SalesForceIntegration> retrieveIntegrations(@PathVariable("memberId") String memberId) {
        return salesForceManager.retrieveIntegrations(memberId);
    }

}
