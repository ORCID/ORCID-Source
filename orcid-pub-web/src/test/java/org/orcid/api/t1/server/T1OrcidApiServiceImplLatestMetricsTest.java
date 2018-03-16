package org.orcid.api.t1.server;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.togglz.junit.TogglzRule;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/orcid-t1-web-context.xml", "classpath:/orcid-t1-security-context.xml" })
public class T1OrcidApiServiceImplLatestMetricsTest {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @Resource
    private T1OrcidApiServiceImplRoot t1OrcidApiService;

    // mock the search results so that we don't need a db setub to check the
    // counter works as expected

    private final Response successResponse = Response.ok().build();

    private OrcidApiServiceDelegator mockServiceDelegator;

    @Before
    public void subvertDelegator() {
        mockServiceDelegator = mock(OrcidApiServiceDelegator.class);
        // view status is always fine
        when(mockServiceDelegator.viewStatusText()).thenReturn(successResponse);
        t1OrcidApiService.setOrcidApiServiceDelegator(mockServiceDelegator);
    }


    @Test
    public void testCounterUnaffectedByViewStatus() {
        Response response = t1OrcidApiService.viewStatusText();
        assertEquals(200, response.getStatus());

    }

    @Test
    public void testViewBioDetailsHtml() {
        when(mockServiceDelegator.findBioDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewBioDetailsHtml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewBioDetailsXml() {
        when(mockServiceDelegator.findBioDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewBioDetailsXml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewBioDetailsJson() {
        when(mockServiceDelegator.findBioDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewBioDetailsJson("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewExternalIdentifiersHtml() {
        when(mockServiceDelegator.findExternalIdentifiersFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewExternalIdentifiersHtml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewExternalIdentifiersXml() {
        when(mockServiceDelegator.findExternalIdentifiersFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewExternalIdentifiersXml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewExternalIdentifiersJson() {
        when(mockServiceDelegator.findExternalIdentifiersFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewExternalIdentifiersJson("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewFullDetailsHtml() {
        when(mockServiceDelegator.findFullDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewFullDetailsHtml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewFullDetailsXml() {
        when(mockServiceDelegator.findFullDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewFullDetailsXml("orcid");
        assertEquals(200, response.getStatus());

    }

    @Test
    public void testViewFullDetailsJson() {
        when(mockServiceDelegator.findFullDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewFullDetailsJson("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewWorksDetailsHtml() {
        when(mockServiceDelegator.findWorksDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewWorksDetailsHtml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewWorksDetailsXml() {
        when(mockServiceDelegator.findWorksDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewWorksDetailsXml("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testViewWorksDetailsJson() {
        when(mockServiceDelegator.findWorksDetailsFromPublicCache(any(String.class))).thenReturn(successResponse);
        Response response = t1OrcidApiService.viewWorksDetailsJson("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testSearchByQueryJSON() {
        UriInfo uriInfo = mock(UriInfo.class);
        t1OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        when(mockServiceDelegator.publicSearchByQuery(queryMaps)).thenReturn(successResponse);
        Response response = t1OrcidApiService.searchByQueryJSON("orcid");
        assertEquals(200, response.getStatus());
    }

    private MultivaluedMap<String, String> queryParams() {

        return new MultivaluedMapImpl();
    }

    @Test
    public void testSearchByQueryXML() {
        UriInfo uriInfo = mock(UriInfo.class);
        t1OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        when(mockServiceDelegator.publicSearchByQuery(queryMaps)).thenReturn(successResponse);
        Response response = t1OrcidApiService.searchByQueryXML("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testSearchByQueryXMLResultsReturned() {
        UriInfo uriInfo = mock(UriInfo.class);
        t1OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        when(mockServiceDelegator.publicSearchByQuery(queryMaps)).thenReturn(orcidWithMultipleResults());
        Response response = t1OrcidApiService.searchByQueryXML("orcid");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testSearchByQueryJSONResultsReturned() {
        UriInfo uriInfo = mock(UriInfo.class);
        t1OrcidApiService.setUriInfo(uriInfo);
        MultivaluedMap<String, String> queryMaps = queryParams();
        when(uriInfo.getQueryParameters()).thenReturn(queryMaps);
        when(mockServiceDelegator.publicSearchByQuery(queryMaps)).thenReturn(orcidWithMultipleResults());
        Response response = t1OrcidApiService.searchByQueryJSON("orcid");
        assertEquals(200, response.getStatus());
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
