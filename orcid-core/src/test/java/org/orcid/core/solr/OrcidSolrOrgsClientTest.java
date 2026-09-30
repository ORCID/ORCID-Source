package org.orcid.core.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * No Solr server is involved and none ever was: the {@link SolrClient} was
 * already a mock and the Spring context was booted only to obtain the bean,
 * which this now constructs directly.
 *
 * <p>
 * The query template is a {@code @Value} field, and {@code @InjectMocks} does
 * not populate those, so it is set in {@link #before()} to the value
 * {@code org.orcid.core.orgs.query} carries in
 * {@code orcid-test/.../test-core.properties} -- the same string the Spring run
 * resolved. It is not incidental: the two {@code find*OrgsOnly} tests assert the
 * exact query text that {@code getOrgs} builds out of it, and left null it would
 * throw before building anything. The constant below is the loaded value, not
 * the raw file text, and the two differ twice: {@code Properties.load} skips
 * the whitespace between {@code =} and the value, and it reads the file's
 * {@code \"} as a plain {@code "} (a backslash before any character other
 * than t/r/n/f/u is dropped). Copying the line out of the file verbatim would
 * give a string that does not match.
 *
 * <p>
 * {@code queryResponse.getResults()} is stubbed inside {@code findById} rather
 * than in {@code before}, because that is the only method that calls it and the
 * strict runner reports a stub the other two tests never reach.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrcidSolrOrgsClientTest {

    private static final String ORGS_QUERY = "(org-disambiguated-name:\"%s\") ^100.0  (org-disambiguated-name:%s*) ^10.0";

    @Mock
    private SolrClient solrReadOnlyOrgsClientMock;

    @Mock
    private QueryResponse mockResponse;

    @InjectMocks
    private OrcidSolrOrgsClient orcidSolrOrgsClient = new OrcidSolrOrgsClient();

    @Before
    public void before() throws SolrServerException, IOException {
        ReflectionTestUtils.setField(orcidSolrOrgsClient, "SOLR_ORGS_QUERY", ORGS_QUERY);

        OrgDisambiguatedSolrDocument doc = new OrgDisambiguatedSolrDocument();
        doc.setOrgDisambiguatedId("1");
        doc.setOrgDisambiguatedName("Test org name");
        doc.setOrgDisambiguatedCity("Haywards Heath");
        doc.setOrgDisambiguatedRegion("West Sussex");
        doc.setOrgDisambiguatedCountry("GB");

        when(mockResponse.getBeans(OrgDisambiguatedSolrDocument.class)).thenReturn(Arrays.asList(doc));
        when(solrReadOnlyOrgsClientMock.query(Mockito.any(SolrParams.class))).thenReturn(mockResponse);
    }

    @Test
    public void pesistAndFindByIdTest() throws IOException, SolrServerException {
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        solrDocumentList.add(new SolrDocument());
        when(mockResponse.getResults()).thenReturn(solrDocumentList);

        OrgDisambiguatedSolrDocument result = orcidSolrOrgsClient.findById(1L);
        assertNotNull(result);
        assertEquals("1", result.getOrgDisambiguatedId());
        assertEquals("Test org name", result.getOrgDisambiguatedName());
    }

    @Test
    public void findFundrefOrgsOnlyTest() throws SolrServerException, IOException {
        ArgumentCaptor<SolrQuery> captor = ArgumentCaptor.forClass(SolrQuery.class);
        orcidSolrOrgsClient.getOrgs("xxx", 0, 0, true, false);
        Mockito.verify(solrReadOnlyOrgsClientMock).query(captor.capture());

        SolrQuery query = captor.getValue();
        assertNotNull(query);
        assertEquals("(org-disambiguated-name:\"xxx\") ^100.0  (org-disambiguated-name:xxx*) ^10.0 AND is-funding-org:true", query.getQuery());
    }

    @Test
    public void findNonFundrefOrgsOnlyTest() throws SolrServerException, IOException {
        ArgumentCaptor<SolrQuery> captor = ArgumentCaptor.forClass(SolrQuery.class);
        orcidSolrOrgsClient.getOrgs("xxx", 0, 0, false, false);
        Mockito.verify(solrReadOnlyOrgsClientMock).query(captor.capture());

        SolrQuery query = captor.getValue();
        assertNotNull(query);
        assertEquals("(org-disambiguated-name:\"xxx\") ^100.0  (org-disambiguated-name:xxx*) ^10.0", query.getQuery());
    }
}
