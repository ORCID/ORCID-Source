package org.orcid.core.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Resource;

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
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSolrOrgsClientTest {
    
    @Resource
    private OrcidSolrOrgsClient orcidSolrOrgsClient;
    
    @Mock
    private SolrClient mockSolrClient;
    
    @Before
    public void before() throws SolrServerException, IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSolrOrgsClient, "solrReadOnlyOrgsClient", mockSolrClient);
    
        OrgDisambiguatedSolrDocument doc = new OrgDisambiguatedSolrDocument();
        doc.setOrgDisambiguatedId("1");
        doc.setOrgDisambiguatedName("Test org name");
        doc.setOrgDisambiguatedCity("Haywards Heath");
        doc.setOrgDisambiguatedRegion("West Sussex");
        doc.setOrgDisambiguatedCountry("GB");
        
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        solrDocumentList.add(new SolrDocument());
        
        QueryResponse mockResponse = Mockito.mock(QueryResponse.class);
        when(mockResponse.getBeans(OrgDisambiguatedSolrDocument.class)).thenReturn(Arrays.asList(doc));
        when(mockResponse.getResults()).thenReturn(solrDocumentList);
        when(mockSolrClient.query(Mockito.any(SolrParams.class))).thenReturn(mockResponse);
    }       
    
    @Test
    public void testPesistAndFindById() throws IOException, SolrServerException {
        OrgDisambiguatedSolrDocument result = orcidSolrOrgsClient.findById(1L);
        assertNotNull(result);
        assertEquals("1", result.getOrgDisambiguatedId());
        assertEquals("Test org name", result.getOrgDisambiguatedName());                
    }    
}
