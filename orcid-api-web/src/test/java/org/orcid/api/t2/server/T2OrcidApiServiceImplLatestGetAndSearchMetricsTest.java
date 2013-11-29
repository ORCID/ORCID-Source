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
package org.orcid.api.t2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-t2-web-context.xml" })
public class T2OrcidApiServiceImplLatestGetAndSearchMetricsTest {

    @Resource
    private T2OrcidApiServiceImplRoot t2OrcidApiService;

    // mock the search results so that we don't need a db setub to check the
    // counter works as expected

    private final Response successResponse = Response.ok().build();

    private T2OrcidApiServiceDelegator mockServiceDelegator;

    @Before
    public void setupMocks() {
        mockServiceDelegator = mock(T2OrcidApiServiceDelegator.class);
        // view status is always fine
        t2OrcidApiService.setServiceDelegator(mockServiceDelegator);
        when(mockServiceDelegator.viewStatusText()).thenReturn(successResponse);

    }

    @After
    @Before
    public void resetVals() {
        T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.clear();
        T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.clear();
        T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.clear();
        T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.clear();

    }

    @Test
    public void testCounterUnaffectedByViewStatus() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewStatusText();
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);

    }

    @Test
    public void testViewBioDetailsHtml() {

        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findBioDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewBioDetailsHtml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewBioDetailsXml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findBioDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewBioDetailsXml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewBioDetailsJson() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findBioDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewBioDetailsJson("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewExternalIdentifiersHtml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findExternalIdentifiers(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewExternalIdentifiersHtml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewExternalIdentifiersXml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findExternalIdentifiers(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewExternalIdentifiersXml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewExternalIdentifiersJson() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findExternalIdentifiers(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewExternalIdentifiersJson("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewFullDetailsHtml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findFullDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewFullDetailsHtml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewFullDetailsXml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findFullDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewFullDetailsXml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);

    }

    @Test
    public void testViewFullDetailsJson() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findFullDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewFullDetailsJson("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewWorksDetailsHtml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findWorksDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewWorksDetailsHtml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewWorksDetailsXml() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findWorksDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewWorksDetailsXml("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testViewWorksDetailsJson() {
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        when(mockServiceDelegator.findWorksDetails(any(String.class))).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.viewWorksDetailsJson("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
    }

    @Test
    public void testSearchByQueryJSONNoResults() {
        UriInfo uriInfo = mock(UriInfo.class);
        t2OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        when(mockServiceDelegator.searchByQuery(queryMaps)).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.searchByQueryJSON("orcid");
        assertTrue(200 == response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 0);
    }

    @Test
    public void testSearchByQueryJSONResultsReturned() {
        UriInfo uriInfo = mock(UriInfo.class);
        t2OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 0);
        when(mockServiceDelegator.searchByQuery(queryMaps)).thenReturn(orcidWithMultipleResults());
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.searchByQueryJSON("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 3);
    }

    private MultivaluedMap<String, String> queryParams() {

        return new MultivaluedMapImpl();
    }

    @Test
    public void testSearchByQueryXMLNoResults() {
        UriInfo uriInfo = mock(UriInfo.class);
        t2OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 0);
        when(mockServiceDelegator.searchByQuery(queryMaps)).thenReturn(successResponse);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.searchByQueryXML("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_GET_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 0);
    }

    @Test
    public void testSearchByQueryXMLResultsReturned() {
        UriInfo uriInfo = mock(UriInfo.class);
        t2OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 0);
        when(mockServiceDelegator.searchByQuery(queryMaps)).thenReturn(orcidWithMultipleResults());
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 0);
        Response response = t2OrcidApiService.searchByQueryXML("orcid");
        assertEquals(200, response.getStatus());
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_NONE_FOUND.count() == 0);
        assertTrue(T2OrcidApiServiceImplRoot.T2_SEARCH_RESULTS_FOUND.count() == 3);
    }

    private Response orcidWithMultipleResults() {

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
        return Response.ok(orcidMessage).build();

    }

}
