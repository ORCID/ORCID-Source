package org.orcid.core.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration tests for Solr Daos. In particular these are used to test that
 * query strings return the Orcids that are expected from SOLR. You may need to
 * compare the queries given in the test methods below with those of the
 * SearchOrcidFormToQueryMapperTest.
 * 
 * @author jamesb
 * @See SearchOrcidFormToQueryMapperTest
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSolrProfileClientTest {

    private final String ORCID = "0000-0000-0000-0000";

    @Mock
    private SolrClient mockSolrClient;

    @Resource
    private OrcidSolrProfileClient orcidSolrProfileClient;

    @Before
    public void before() throws SolrServerException, IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSolrProfileClient, "solrReadOnlyProfileClient", mockSolrClient);

        SolrDocumentList solrDocumentList = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("score", 0.0f);
        solrDocument.setField("orcid", ORCID);
        solrDocumentList.add(solrDocument);

        QueryResponse mockResponse = Mockito.mock(QueryResponse.class);
        when(mockResponse.getResults()).thenReturn(solrDocumentList);
        when(mockSolrClient.query(Mockito.any(SolrParams.class))).thenReturn(mockResponse);
    }

    @Test
    public void searchByOrcid() throws Exception {
        OrcidSolrResult result = orcidSolrProfileClient.findByOrcid(ORCID);
        assertEquals(ORCID, result.getOrcid());
        assertTrue(0.0f == result.getRelevancyScore());
    }
}
