package org.orcid.frontend.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.utils.OrcidStringUtils;
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
        mav.addObject("memberSlug", OrcidStringUtils.stripHtml(memberSlug.trim()));
        return mav;
    }

    @RequestMapping(value = "/members/members.json", method = RequestMethod.GET)
    public @ResponseBody List<Member> retrieveMembers() {
        return salesForceManager.retrieveMembers();
    }

    @RequestMapping(value = "/members/detailsBySlug.json", method = RequestMethod.GET)
    public @ResponseBody MemberDetails retrieveDetailsBySlug(@RequestParam("memberSlug") String memberSlug) {
        return salesForceManager.retrieveDetailsBySlug(memberSlug, true);
    }

    @RequestMapping(value = "/members/communityTypes.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> retrieveCommunityTypes() {
        return generateCommunityTypeMap();
    }

    @RequestMapping("/consortia")
    public ModelAndView consortiaList() {
        ModelAndView mav = new ModelAndView("consortia-list");
        return mav;
    }

    @RequestMapping(value = "/consortia/consortia.json", method = RequestMethod.GET)
    public @ResponseBody List<Member> retrieveConsortia() {
        return salesForceManager.retrieveConsortia();
    }

    protected Map<String, String> generateCommunityTypeMap() {
        Map<String, String> map = new HashMap<>();
        for (CommunityType communityType : CommunityType.values()) {
            map.put(communityType.name(), getMessage(buildInternationalizationKey(CommunityType.class, communityType.name())));
        }
        return map;
    }

}
