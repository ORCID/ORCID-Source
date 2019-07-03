package org.orcid.core.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class OrcidSolrOrgsClientTest {
    
    @Resource(name = "solrClientTest")
    private SolrClient solrClientTest;
    
    @Resource
    private OrcidSolrOrgsClient orcidSolrOrgsClient;
    
    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(orcidSolrOrgsClient, "solrReadOnlyOrgsClient", solrClientTest);
    }
    
    @Test
    public void testPesistAndFindById() throws IOException, SolrServerException {
        OrgDisambiguatedSolrDocument doc = new OrgDisambiguatedSolrDocument();
        doc.setOrgDisambiguatedId("1");
        doc.setOrgDisambiguatedName("Test org name");
        doc.setOrgDisambiguatedCity("Haywards Heath");
        doc.setOrgDisambiguatedRegion("West Sussex");
        doc.setOrgDisambiguatedCountry("GB");
        List<String> orgNames = new ArrayList<>();
        orgNames.add("Test org name");
        orgNames.add("Test organisation name");
        doc.setOrgNames(orgNames);

        solrClientTest.addBean(doc);
        solrClientTest.commit();

        OrgDisambiguatedSolrDocument result = orcidSolrOrgsClient.findById(1L);
        assertNotNull(result);
        assertEquals("1", result.getOrgDisambiguatedId());
        assertEquals("Test org name", result.getOrgDisambiguatedName());                
    }    
}
