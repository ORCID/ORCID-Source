package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.SalesForceManagerLegacy;
import org.orcid.core.manager.SalesforceManager;
import org.orcid.core.salesforce.model.Badge;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.OrcidStringUtils;
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

    @Resource(name = "salesForceManagerLegacy")
    private SalesForceManagerLegacy salesForceManagerLegacy;
    
    @Resource(name = "salesForceConsortiumLeadIdsCache")
    private Cache<String, List<String>> salesForceConsortiumLeadIdsCache;
    
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

    @RequestMapping("/members/{memberSlug}")
    public ModelAndView memberPage(@PathVariable("memberSlug") String memberSlug) {
        ModelAndView mav = new ModelAndView("member-page");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

    @RequestMapping(value = "/members/members.json", method = RequestMethod.GET)
    public @ResponseBody List<Member> retrieveMembers() {
        List<Member> members = new ArrayList<>();
        if (Features.SALESFORCE_MICROSERVICE.isActive()) {
            members = salesforceManager.retrieveMembers();
        } else {
            List<String> consortiumLeadIds = salesForceConsortiumLeadIdsCache.get("");
            List<Member> allMembers = salesForceManagerLegacy.retrieveMembers();

            members = allMembers.stream().filter(e -> ((!consortiumLeadIds.contains(e.getRecordTypeId())
                    || (consortiumLeadIds.contains(e.getRecordTypeId()) && Boolean.TRUE.equals(e.getIsConsortiaMember()))))).collect(Collectors.toList());
        }
        return members;
    }

    @RequestMapping(value = "/members/detailsBySlug.json", method = RequestMethod.GET)
    public @ResponseBody MemberDetails retrieveDetailsBySlug(@RequestParam("memberSlug") String memberSlug) {
        MemberDetails details;
        if (Features.SALESFORCE_MICROSERVICE.isActive()) {
            details = salesforceManager.retrieveMemberDetails(memberSlug);
        } else {
            details = salesForceManagerLegacy.retrieveDetailsBySlug(memberSlug, true);
        }
        return details;
    }

    @RequestMapping(value = "/members/communityTypes.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> retrieveCommunityTypes() {
        return generateCommunityTypeMap();
    }

    @RequestMapping(value = "/members/badges.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, Badge> retrieveBadges() {
        return salesForceManagerLegacy.retrieveBadgesMap();
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
        List<Member> consortiaListMembers;
        if (Features.SALESFORCE_MICROSERVICE.isActive()) {
            consortiaListMembers = salesforceManager.retrieveConsortiaList();            
        } else {
            consortiaListMembers = salesForceManagerLegacy.retrieveConsortia();    
        }
        return consortiaListMembers;
    }

    protected Map<String, String> generateCommunityTypeMap() {
        Map<String, String> map = new HashMap<>();
        for (CommunityType communityType : CommunityType.values()) {
            map.put(communityType.name(), getMessage(buildInternationalizationKey(CommunityType.class, communityType.name())));
        }
        return map;
    }

}
