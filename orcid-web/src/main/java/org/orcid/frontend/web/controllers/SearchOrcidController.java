package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.OrcidSearchManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller("searchOrcidController")
@RequestMapping(value = "/orcid-search")
public class SearchOrcidController extends BaseController {

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManager orcidSearchManager;

    public void setOrcidSearchManager(OrcidSearchManager orcidSearchManager) {
        this.orcidSearchManager = orcidSearchManager;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView buildSearchView() {
        ModelAndView mav = new ModelAndView("advanced_search");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

    @RequestMapping(value = "/quick-search")
    public ModelAndView quickSearch() {
        ModelAndView mav = new ModelAndView("quick_search");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

}
