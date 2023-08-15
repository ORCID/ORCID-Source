package org.orcid.frontend.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.frontend.salesforce.manager.SalesforceManager;
import org.orcid.frontend.salesforce.model.Member;
import org.orcid.frontend.salesforce.model.MemberDetails;
import org.orcid.frontend.salesforce.model.SlugUtils;
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
    private SalesforceManager salesforceManager;

    @RequestMapping("/members")
    public ModelAndView membersList() {
        ModelAndView mav = new ModelAndView("members-list");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

    @RequestMapping("/members/{memberId}")
    public ModelAndView memberPage(@PathVariable("memberId") String memberId) {
        // Does it have slug on it? then redirect to the page with just the ID
        if(SlugUtils.containsSlug(memberId)) {
            String sanitizedId = SlugUtils.extractIdFromSlug(memberId);
            return new ModelAndView("redirect:" + calculateRedirectUrl("/members/" + sanitizedId));
        }
        ModelAndView mav = new ModelAndView("member-page");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

    @RequestMapping(value = "/members/members.json", method = RequestMethod.GET)
    public @ResponseBody List<Member> retrieveMembers() {        
        return salesforceManager.retrieveMembers();
    }

    @RequestMapping(value = "/members/details.json", method = RequestMethod.GET)
    public @ResponseBody MemberDetails retrieveDetailsById(@RequestParam("memberId") String memberId) {        
        return salesforceManager.retrieveMemberDetails(memberId);
    }

    @RequestMapping(value = "/members/communityTypes.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> retrieveCommunityTypes() {
        return generateCommunityTypeMap();
    }    

    @RequestMapping("/consortia")
    public ModelAndView consortiaList() {
        ModelAndView mav = new ModelAndView("consortia-list");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

    @RequestMapping(value = "/consortia/consortia.json", method = RequestMethod.GET)
    public @ResponseBody List<Member> retrieveConsortia() {        
        return salesforceManager.retrieveConsortiaList();
    }

    protected Map<String, String> generateCommunityTypeMap() {
        Map<String, String> map = new HashMap<>();
        for (CommunityType communityType : CommunityType.values()) {
            map.put(communityType.name(), getMessage(buildInternationalizationKey(CommunityType.class, communityType.name())));
        }
        return map;
    }

}
