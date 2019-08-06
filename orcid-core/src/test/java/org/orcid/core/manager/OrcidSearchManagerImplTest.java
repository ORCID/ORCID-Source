package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
import org.orcid.core.solr.OrcidSolrProfileClient;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.search_v2.Search;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;

/**
 * Tests for the invocation of Solr retrieval. This class isn't required to have
 * a Solr instance running as it uses Mockito. The purpose of these tests are to
 * check the inner mappings of the search manager return an OrcidMessage
 * instance mapped from a SolrDocument.
 * 
 * @see SolrDocument
 * @see OrcidMessage
 * 
 * @author jamesb
 * 
 */
public class OrcidSearchManagerImplTest extends BaseTest {

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private OrcidSolrProfileClient mockOrcidSolrProfileClient;

    @Mock
    private OrcidSecurityManager mockOrcidSecurityManager;

    @Resource
    private OrcidSolrProfileClient orcidSolrProfileClient;

    @Resource
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

}