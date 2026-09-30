package org.orcid.core.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * No Solr server is involved and none ever was: the {@link SolrClient} was
 * already a mock and the Spring context was booted only to obtain the bean,
 * which this now constructs directly.
 *
 * <p>
 * The class javadoc used to call these "integration tests ... used to test that
 * query strings return the Orcids that are expected from SOLR". That was
 * already untrue of the one surviving method -- with a mocked SolrClient
 * nothing is matched, scored or returned by Solr -- so the claim is dropped
 * rather than carried forward. What {@code searchByOrcid} actually proves is
 * the mapping half of {@code findByOrcid}: that the first document of the
 * response is read, and that its {@code score} and {@code orcid} fields land on
 * the right properties of {@link OrcidSolrResult}. Whether the query string it
 * builds retrieves the right documents can only be shown against a real index.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrcidSolrProfileClientTest {

    private final String ORCID = "0000-0000-0000-0000";

    @Mock
    private SolrClient solrReadOnlyProfileClient;

    @InjectMocks
    private OrcidSolrProfileClient orcidSolrProfileClient = new OrcidSolrProfileClient();

    @Before
    public void before() throws SolrServerException, IOException {
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("score", 0.0f);
        solrDocument.setField("orcid", ORCID);
        solrDocumentList.add(solrDocument);

        QueryResponse mockResponse = Mockito.mock(QueryResponse.class);
        when(mockResponse.getResults()).thenReturn(solrDocumentList);
        when(solrReadOnlyProfileClient.query(Mockito.any(SolrParams.class))).thenReturn(mockResponse);
    }

    @Test
    public void searchByOrcid() throws Exception {
        OrcidSolrResult result = orcidSolrProfileClient.findByOrcid(ORCID);
        assertEquals(ORCID, result.getOrcid());
        assertTrue(0.0f == result.getRelevancyScore());
    }
}
