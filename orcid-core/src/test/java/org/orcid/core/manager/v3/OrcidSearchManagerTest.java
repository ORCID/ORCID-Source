package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.v3.rc2.search.Result;
import org.orcid.jaxb.model.v3.rc2.search.Search;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;

/**
 * Tests for the invocation of Solr retrieval. This class isn't required to have
 * a Solr instance running as it uses Mockito. The purpose of these tests are to
 * check the inner mappings of the search manager return an OrcidMessage
 * instance mapped from a SolrDocument.
 * 
 * @see SolrDao
 * @see SolrDocument
 * @see OrcidMessage
 * 
 * @author jamesb
 * 
 */
public class OrcidSearchManagerTest extends BaseTest {

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private SolrDao mockSolrDao;

    @Mock
    private OrcidSecurityManager mockOrcidSecurityManager;
    
    @Resource
    private SolrDao solrDao;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;
        
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "solrDao", mockSolrDao);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSecurityManager", mockOrcidSecurityManager);
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "solrDao", solrDao);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSecurityManager", orcidSecurityManager);
    }
    
    @Test
    public void testFindOrcidIds() {
        when(mockSolrDao.findByDocumentCriteria(any())).thenReturn(multipleResultsForQuery());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(2, search.getResults().size());
        assertEquals(Long.valueOf(2), search.getNumFound());
        assertEquals("5678", search.getResults().get(0).getOrcidIdentifier().getPath());
        assertEquals("6789", search.getResults().get(1).getOrcidIdentifier().getPath());
    }
    
    @Test
    public void testFindOrcidIdsNoResults() {
        when(mockSolrDao.findByDocumentCriteria(any())).thenReturn(new OrcidSolrResults());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(Long.valueOf(0), search.getNumFound());
        assertEquals(0, search.getResults().size());
    }
    
    @Test
    public void oneOrcidInDbOtherMissing() {
        when(mockSolrDao.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(multipleResultsForQuery());
        doThrow(new OrcidNoResultException()).when(mockOrcidSecurityManager).checkProfile("6789");
        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertNotNull(search.getResults());
        assertTrue(search.getResults().size() == 1);
        Result searchResult = search.getResults().get(0);
        assertEquals("5678", searchResult.getOrcidIdentifier().getPath());
    }

    @Test
    public void orcidMultipleOrcidsIndexed() {
        when(mockSolrDao.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(multipleResultsForQuery());
        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertNotNull(search.getResults());
        assertEquals(2, search.getResults().size());

        Result result = search.getResults().get(0);
        assertEquals("5678", result.getOrcidIdentifier().getPath());
        
        Result result2 = search.getResults().get(1);
        assertEquals("6789", result2.getOrcidIdentifier().getPath());        
    }

    @Test
    public void recordLockedTest() {
        when(mockSolrDao.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(invalidRecordSearchResult());
        doThrow(new LockedException()).when(mockOrcidSecurityManager).checkProfile("0000");
        
        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertTrue(search.getResults().isEmpty());        
    }
    
    @Test
    public void recordDeactivatedTest() {
        when(mockSolrDao.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(invalidRecordSearchResult());
        doThrow(new DeactivatedException()).when(mockOrcidSecurityManager).checkProfile("0000");
        
        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertTrue(search.getResults().isEmpty());
    }
    
    @Test
    public void recordDeprecatedTest() {
        when(mockSolrDao.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(invalidRecordSearchResult());
        doThrow(new OrcidDeprecatedException()).when(mockOrcidSecurityManager).checkProfile("0000");
        
        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertTrue(search.getResults().isEmpty());         
    }
    
    @Test
    public void allFineTest() {
        when(mockSolrDao.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(invalidRecordSearchResult());
        
        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertEquals(1, search.getResults().size());
        assertEquals("0000", search.getResults().get(0).getOrcidIdentifier().getPath());
    }
    
    private OrcidSolrResults invalidRecordSearchResult() {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> resultsList = new ArrayList<>();
        orcidSolrResults.setResults(resultsList);
        resultsList.add(getOrcidSolrResult("0000", new Float(37.2)));        
        orcidSolrResults.setNumFound(2);
        return orcidSolrResults;
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

}