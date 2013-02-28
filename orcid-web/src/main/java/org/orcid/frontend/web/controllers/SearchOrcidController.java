/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.frontend.web.controllers.helper.SearchOrcidSolrCriteria;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

@Controller("searchOrcidController")
@RequestMapping(value = "/orcid-search")
public class SearchOrcidController extends BaseController {

    final static Counter FRONTEND_WEB_SEARCH_REQUESTS = Metrics.newCounter(SearchOrcidController.class, "FRONTEND-WEB-SEARCH-REQUESTS");
    final static Counter FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND = Metrics.newCounter(SearchOrcidController.class, "FRONTEND-WEB-SEARCH-RESULTS-NONE-FOUND");
    final static Counter FRONTEND_WEB_SEARCH_RESULTS_FOUND = Metrics.newCounter(SearchOrcidController.class, "FRONTEND-WEB-SEARCH-RESULTS-FOUND");

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
        mav.addObject("searchOrcidForm", new SearchOrcidBioForm());
        mav.setViewName("orcid_search");
        return mav;
    }

    @RequestMapping(value = "/search-for-orcid")
    public ModelAndView searchByOrcid(@ModelAttribute("searchOrcidForm") @Valid SearchOrcidBioForm searchOrcidForm, BindingResult bindingResult) {

        ModelAndView mav = new ModelAndView("orcid_search");
        mav.addObject("activeTab", "OrcidBioSearch");

        if (bindingResult.hasErrors()) {
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }

        String orcid = searchOrcidForm.getOrcid();
        OrcidMessage orcidMessage;

        // no need to use other criteria if we're searching by a single orcid
        if (!StringUtils.isBlank(orcid)) {
            orcidMessage = orcidSearchManager.findOrcidSearchResultsById(orcid);
            FRONTEND_WEB_SEARCH_REQUESTS.inc();
        } else {
            SearchOrcidSolrCriteria orcidSolrQuery = new SearchOrcidSolrCriteria();
            orcidSolrQuery.setFamilyName(searchOrcidForm.getFamilyName());
            orcidSolrQuery.setGivenName(searchOrcidForm.getGivenName());
            orcidSolrQuery.setInstitutionName(searchOrcidForm.getInstitutionName());
            orcidSolrQuery.setIncludeOtherNames(searchOrcidForm.isOtherNamesSearchable());
            orcidSolrQuery.setPastInstitutionsSearchable(searchOrcidForm.isPastInstitutionsSearchable());
            orcidSolrQuery.setKeyword(searchOrcidForm.getKeyword());
            String query = orcidSolrQuery.deriveQueryString();
            orcidMessage = orcidSearchManager.findOrcidsByQuery(query);
            FRONTEND_WEB_SEARCH_REQUESTS.inc();
        }

        if (!orcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty()) {
            incrementSearchMetrics(orcidMessage.getOrcidSearchResults().getOrcidSearchResult());
            mav.addObject("searchResults", orcidMessage.getOrcidSearchResults().getOrcidSearchResult());

        } else {
            incrementSearchMetrics(null);
            mav.addObject("noResultsFound", true);
        }

        return mav;
    }

    @RequestMapping(value = "/quick-search")
    public ModelAndView quickSearch(@ModelAttribute("searchQuery") String searchQuery) {
        ModelAndView mav = new ModelAndView("quick_search");
        if (StringUtils.isBlank(searchQuery)) {
            incrementSearchMetrics(null);
            mav.addObject("noResultsFound", true);
            return mav;
        }
        SearchOrcidSolrCriteria query = new SearchOrcidSolrCriteria();
        searchQuery = searchQuery.trim();
        if (OrcidStringUtils.isValidOrcid(searchQuery)) {
            query.setOrcid(searchQuery);
        } else {
            query.setText(searchQuery);
        }
        OrcidMessage orcidMessage = orcidSearchManager.findOrcidsByQuery(query.deriveQueryString());
        FRONTEND_WEB_SEARCH_REQUESTS.inc();
        if (!orcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty()) {
            incrementSearchMetrics(orcidMessage.getOrcidSearchResults().getOrcidSearchResult());
            mav.addObject("searchResults", orcidMessage.getOrcidSearchResults().getOrcidSearchResult());
        } else {
            incrementSearchMetrics(null);
            mav.addObject("noResultsFound", true);
        }
        return mav;
    }

    private void incrementSearchMetrics(List<OrcidSearchResult> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.inc();
            return;
        }

        FRONTEND_WEB_SEARCH_RESULTS_FOUND.inc(searchResults.size());
    }

}
