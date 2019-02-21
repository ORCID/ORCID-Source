package org.orcid.frontend.web.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ModelAndView buildSearchView(@RequestParam(value = "activeTab", required = false) String activeTab) {
        ModelAndView mav = new ModelAndView();
        String tab = activeTab == null ? "OrcidBioSearch" : activeTab;
        mav.addObject("activeTab", tab);
        mav.setViewName("advanced_search");
        return mav;
    }

    @RequestMapping(value = "/quick-search")
    public ModelAndView quickSearch() {
        return new ModelAndView("quick_search");
    }

    private void incrementSearchMetrics(List<OrcidSearchResult> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            return;
        }
    }

}
