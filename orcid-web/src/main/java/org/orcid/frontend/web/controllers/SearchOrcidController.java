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

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrcidSearchManager;
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

    @Resource
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
    public ModelAndView quickSearch(@ModelAttribute("searchQuery") String queryFromUser, @ModelAttribute("solrQuery") String solrQuery) {
        ModelAndView mav = new ModelAndView("quick_search");
        if (StringUtils.isBlank(queryFromUser) && StringUtils.isBlank(solrQuery)) {
            incrementSearchMetrics(null);
            mav.addObject("noResultsFound", true);
            return mav;
        }
        return mav;
    }

    private void incrementSearchMetrics(List<OrcidSearchResult> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            return;
        }
    }

}
