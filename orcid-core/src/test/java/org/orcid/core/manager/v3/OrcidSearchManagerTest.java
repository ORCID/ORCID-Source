package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.impl.OrcidSearchManagerImpl;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.RecordManagerReadOnlyImpl;
import org.orcid.core.solr.CSVSolrClient;
import org.orcid.core.solr.OrcidSolrProfileClient;
import org.orcid.core.solr.OrcidSolrResult;
import org.orcid.core.solr.OrcidSolrResults;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.v3.release.search.Result;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests for the invocation of Solr retrieval. This class isn't required to have
 * a Solr instance running as it uses Mockito. The purpose of these tests are to
 * check the inner mappings of the search manager return an OrcidMessage
 * instance mapped from a SolrDocument.
 *
 * <p>
 * That was already true, which is why this no longer boots the Spring context:
 * the Solr client was mocked and the context was serving the bean and nothing
 * else. The class under test is now built directly.
 *
 * <p>
 * {@link RecordManagerReadOnly} is a real {@link RecordManagerReadOnlyImpl},
 * not a mock, because {@code setSearchResults} maps each Solr hit into a
 * {@link Result} by calling {@code getOrcidIdentifier} on it, and every
 * assertion below reads the path off that identifier. Against a mock the paths
 * would be whatever this test stubbed and the mapping would not be exercised at
 * all. The real method touches no database -- it builds an
 * {@code OrcidIdentifier} from the orcid string and the base URL -- so only
 * {@link OrcidUrlManager} has to be supplied, with the {@code
 * org.orcid.core.baseUri} the test properties carry.
 *
 * @see SolrDocument
 * @see OrcidMessage
 * 
 * @author jamesb
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class OrcidSearchManagerTest {

    @Mock
    private OrcidSolrProfileClient orcidSolrProfileClient;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private CSVSolrClient csvSolrClient;

    @InjectMocks
    private OrcidSearchManagerImpl orcidSearchManager = new OrcidSearchManagerImpl();

    @Before
    public void initMocks() {
        OrcidUrlManager orcidUrlManager = new OrcidUrlManager();
        orcidUrlManager.setBaseUrl("https://testserver.orcid.org");

        RecordManagerReadOnlyImpl recordManagerReadOnly = new RecordManagerReadOnlyImpl();
        ReflectionTestUtils.setField(recordManagerReadOnly, "orcidUrlManager", orcidUrlManager);

        ReflectionTestUtils.setField(orcidSearchManager, "recordManagerReadOnly", recordManagerReadOnly);
    }

    @Test
    public void testFindOrcidIds() throws ParseException {
        when(orcidSolrProfileClient.findByDocumentCriteria(any())).thenReturn(multipleResultsForQuery());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(2, search.getResults().size());
        assertEquals(Long.valueOf(2), search.getNumFound());
        assertEquals("5678", search.getResults().get(0).getOrcidIdentifier().getPath());
        assertEquals("6789", search.getResults().get(1).getOrcidIdentifier().getPath());
    }

    @Test
    public void testFindOrcidIdsNoResults() throws ParseException {
        when(orcidSolrProfileClient.findByDocumentCriteria(any())).thenReturn(new OrcidSolrResults());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(Long.valueOf(0), search.getNumFound());
        assertEquals(0, search.getResults().size());
    }

    @Test
    public void orcidMultipleOrcidsIndexed() throws ParseException {
        when(orcidSolrProfileClient.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(multipleResultsForQuery());
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
    public void allFineTest() throws ParseException {
        when(orcidSolrProfileClient.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(invalidRecordSearchResult());

        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertEquals(1, search.getResults().size());
        assertEquals("0000", search.getResults().get(0).getOrcidIdentifier().getPath());
    }

    @Test
    public void numFoundTest() throws ParseException {
        OrcidSolrResults osr = multipleResultsForQuery();
        osr.setNumFound(500);
        when(orcidSolrProfileClient.findByDocumentCriteria("rndQuery", 0, 0)).thenReturn(osr);

        Search search = orcidSearchManager.findOrcidsByQuery("rndQuery", 0, 0);
        assertNotNull(search);
        assertEquals(Long.valueOf(500), search.getNumFound());
        assertEquals(2, search.getResults().size());
        assertEquals("5678", search.getResults().get(0).getOrcidIdentifier().getPath());
        assertEquals("6789", search.getResults().get(1).getOrcidIdentifier().getPath());
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
