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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "/orcid-core-context.xml" })
public class SearchOrcidControllerControllerTest {

    @Resource(name = "searchOrcidController")
    SearchOrcidController searchOrcidController;

    private OrcidSearchManager orcidSearchManager;

    @Before
    public void setupDependencies() throws Exception {
        SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.clear();
        SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.clear();
        SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_FOUND.clear();
        orcidSearchManager = mock(OrcidSearchManager.class);
        searchOrcidController.setOrcidSearchManager(orcidSearchManager);
    }

    @Test
    public void testSearchByOrcidResultsReturned() {
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);

        when(orcidSearchManager.findOrcidSearchResultsById(any(String.class), eq(false))).thenReturn(orcidWithMultipleResults());
        SearchOrcidBioForm orcidBioForm = new SearchOrcidBioForm();
        orcidBioForm.setOrcid("oid");
        searchOrcidController.searchByOrcid(orcidBioForm, mock(BindingResult.class));
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 1);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_FOUND.count() == 3);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);

    }

    @Test
    public void testSearchByOrcidNoResultsReturned() {
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);

        when(orcidSearchManager.findOrcidSearchResultsById(any(String.class), eq(false))).thenReturn(orcidWithNoResults());
        SearchOrcidBioForm orcidBioForm = new SearchOrcidBioForm();
        orcidBioForm.setOrcid("oid");
        searchOrcidController.searchByOrcid(orcidBioForm, mock(BindingResult.class));
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 1);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_FOUND.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 1);

    }

    @Test
    public void testSearchByFormResultsReturned() {
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);

        when(orcidSearchManager.findOrcidsByQuery(any(String.class), eq(false))).thenReturn(orcidWithMultipleResults());
        SearchOrcidBioForm orcidBioForm = new SearchOrcidBioForm();
        searchOrcidController.searchByOrcid(orcidBioForm, mock(BindingResult.class));
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 1);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_FOUND.count() == 3);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);

    }

    @Test
    public void testSearchByFormNoResultsReturned() {
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 0);

        when(orcidSearchManager.findOrcidsByQuery(any(String.class), eq(false))).thenReturn(orcidWithNoResults());
        SearchOrcidBioForm orcidBioForm = new SearchOrcidBioForm();
        searchOrcidController.searchByOrcid(orcidBioForm, mock(BindingResult.class));
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.count() == 1);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_FOUND.count() == 0);
        assertTrue(SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.count() == 1);

    }

    private OrcidMessage orcidWithNoResults() {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults orcidSearchResults = new OrcidSearchResults();
        orcidSearchResults.getOrcidSearchResult();
        orcidMessage.setOrcidSearchResults(orcidSearchResults);
        return orcidMessage;
    }

    private OrcidMessage orcidWithMultipleResults() {

        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidProfile orcidProfile1 = new OrcidProfile();
        OrcidProfile orcidProfile2 = new OrcidProfile();
        OrcidProfile orcidProfile3 = new OrcidProfile();

        OrcidSearchResult orcidSearchResult1 = new OrcidSearchResult();
        OrcidSearchResult orcidSearchResult2 = new OrcidSearchResult();
        OrcidSearchResult orcidSearchResult3 = new OrcidSearchResult();

        orcidSearchResult1.setOrcidProfile(orcidProfile1);
        orcidSearchResult2.setOrcidProfile(orcidProfile2);
        orcidSearchResult3.setOrcidProfile(orcidProfile3);

        List<OrcidSearchResult> searchResults = new ArrayList<OrcidSearchResult>();
        searchResults.add(orcidSearchResult1);
        searchResults.add(orcidSearchResult2);
        searchResults.add(orcidSearchResult3);

        OrcidSearchResults orcidSearchResults = new OrcidSearchResults();
        orcidSearchResults.getOrcidSearchResult().addAll(searchResults);
        orcidMessage.setOrcidSearchResults(orcidSearchResults);
        return orcidMessage;

    }

}
