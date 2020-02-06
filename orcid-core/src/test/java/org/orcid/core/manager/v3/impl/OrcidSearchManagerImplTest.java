package org.orcid.core.manager.v3.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.solr.OrcidSolrProfileClient;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedResult;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;

public class OrcidSearchManagerImplTest extends BaseTest {

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private OrcidSolrProfileClient mockOrcidSolrProfileClient;

    @Mock
    private OrcidSecurityManager mockOrcidSecurityManager;

    @Resource
    private OrcidSolrProfileClient orcidSolrProfileClient;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSolrProfileClient", mockOrcidSolrProfileClient);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSecurityManager", mockOrcidSecurityManager);
    }

    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSolrProfileClient", orcidSolrProfileClient);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSecurityManager", orcidSecurityManager);
    }

    @Test
    public void testFindOrcidIds() {
        when(mockOrcidSolrProfileClient.findByDocumentCriteria(any())).thenReturn(multipleResultsForQuery());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(2, search.getResults().size());
        assertEquals(Long.valueOf(2), search.getNumFound());
        assertEquals("5678", search.getResults().get(0).getOrcidIdentifier().getPath());
        assertEquals("6789", search.getResults().get(1).getOrcidIdentifier().getPath());
    }
    
    @Test
    public void testExpandedSearch() {
        when(mockOrcidSolrProfileClient.findExpandedByDocumentCriteria(any())).thenReturn(multipleExpandedResultsForQuery());
        ExpandedSearch search = orcidSearchManager.expandedSearch(new HashMap<>());
        assertNotNull(search);
        assertEquals(3, search.getResults().size());
        assertEquals(Long.valueOf(3), search.getNumFound());
        
        ExpandedResult result1 = search.getResults().get(0);
        ExpandedResult result2 = search.getResults().get(1);
        ExpandedResult result3 = search.getResults().get(2);
        
        assertEquals("orcid1", result1.getOrcidId());
        assertEquals("person1", result1.getGivenNames());
        assertEquals("familyName1", result1.getFamilyNames());
        assertEquals("creditName1", result1.getCreditName());
        
        String[] otherNames = result1.getOtherNames();
        assertEquals("other1", otherNames[0]);
        assertEquals("name1", otherNames[1]);
        
        String[] institutionNames = result1.getInstitutionNames();
        assertEquals("institution1", institutionNames[0]);
        assertEquals("institution2", institutionNames[1]);
        
        assertEquals("one@one.com", result1.getEmail());
        
        assertEquals("orcid2", result2.getOrcidId());
        assertEquals("person2", result2.getGivenNames());
        assertEquals("familyName2", result2.getFamilyNames());
        assertEquals("creditName2", result2.getCreditName());
        
        otherNames = result2.getOtherNames();
        assertEquals("other2", otherNames[0]);
        assertEquals("name2", otherNames[1]);
        
        institutionNames = result2.getInstitutionNames();
        assertEquals("institution3", institutionNames[0]);
        assertEquals("institution4", institutionNames[1]);
        
        assertEquals("two@two.com", result2.getEmail());
        
        assertEquals("orcid3", result3.getOrcidId());
        assertEquals("person3", result3.getGivenNames());
        assertEquals("familyName3", result3.getFamilyNames());
        assertEquals("creditName3", result3.getCreditName());
        
        otherNames = result3.getOtherNames();
        assertEquals("other3", otherNames[0]);
        assertEquals("name3", otherNames[1]);
        
        institutionNames = result3.getInstitutionNames();
        assertEquals("institution5", institutionNames[0]);
        assertEquals("institution6", institutionNames[1]);
        
        assertEquals("three@three.com", result3.getEmail());
    }

    @Test
    public void testFindOrcidIdsNoResults() {
        when(mockOrcidSolrProfileClient.findByDocumentCriteria(any())).thenReturn(new OrcidSolrResults());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(Long.valueOf(0), search.getNumFound());
        assertEquals(0, search.getResults().size());
    }

    private OrcidSolrResult getOrcidSolrResult(String orcid, Float relevancy) {
        OrcidSolrResult solrResult = new OrcidSolrResult();
        solrResult.setOrcid(orcid);
        solrResult.setRelevancyScore(relevancy);
        return solrResult;
    }

    private OrcidSolrResults multipleResultsForQuery() {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> resultsList = new ArrayList<>();
        orcidSolrResults.setResults(resultsList);
        resultsList.add(getOrcidSolrResult("5678", new Float(37.2)));
        resultsList.add(getOrcidSolrResult("6789", new Float(52.2)));
        orcidSolrResults.setNumFound(2);
        return orcidSolrResults;
    }
    
    private OrcidSolrResults multipleExpandedResultsForQuery() {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> resultsList = new ArrayList<>();
        orcidSolrResults.setResults(resultsList);
        resultsList.add(getExpandedOrcidSolrResult("orcid1", "person1", "familyName1", "creditName1", new String[] { "other1", "name1" }, new String[] { "institution1", "institution2" }, "one@one.com"));
        resultsList.add(getExpandedOrcidSolrResult("orcid2", "person2", "familyName2", "creditName2", new String[] { "other2", "name2" }, new String[] { "institution3", "institution4" }, "two@two.com"));
        resultsList.add(getExpandedOrcidSolrResult("orcid3", "person3", "familyName3", "creditName3", new String[] { "other3", "name3" }, new String[] { "institution5", "institution6" }, "three@three.com"));
        orcidSolrResults.setNumFound(3);
        return orcidSolrResults;
    }

    private OrcidSolrResult getExpandedOrcidSolrResult(String orcid, String name, String familyName, String creditName, String[] otherNames, String[] institutionAffiliationNames, String email) {
        OrcidSolrResult solrResult = new OrcidSolrResult();
        solrResult.setOrcid(orcid);
        solrResult.setGivenNames(name);
        solrResult.setFamilyName(familyName);
        solrResult.setCreditName(creditName);
        solrResult.setOtherNames(Arrays.asList(otherNames));
        solrResult.setInstitutionAffiliationNames(Arrays.asList(institutionAffiliationNames));
        solrResult.setEmail(email);
        return solrResult;
    }
    
}