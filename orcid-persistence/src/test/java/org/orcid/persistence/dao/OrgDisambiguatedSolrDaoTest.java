package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class OrgDisambiguatedSolrDaoTest {

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;
    
    @Test
    public void testPesistAndFindById() {
        OrgDisambiguatedSolrDocument doc = new OrgDisambiguatedSolrDocument();
        doc.setOrgDisambiguatedId(1L);
        doc.setOrgDisambiguatedName("Test org name");
        doc.setOrgDisambiguatedCity("Haywards Heath");
        doc.setOrgDisambiguatedRegion("West Sussex");
        doc.setOrgDisambiguatedCountry("GB");
        List<String> orgNames = new ArrayList<>();
        orgNames.add("Test org name");
        orgNames.add("Test organisation name");
        doc.setOrgNames(orgNames);

        orgDisambiguatedSolrDao.persist(doc);

        OrgDisambiguatedSolrDocument result = orgDisambiguatedSolrDao.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getOrgDisambiguatedId().longValue());
        assertEquals("Test org name", result.getOrgDisambiguatedName());
    }
    
    @Test
    public void testGetOrgs() throws SolrServerException {
        SolrServer mockSolrServerReadOnly = Mockito.mock(SolrServer.class);
        SolrServer defaultSolrServer = (SolrServer) ReflectionTestUtils.getField(orgDisambiguatedSolrDao, "solrServerReadOnly");
        ReflectionTestUtils.setField(orgDisambiguatedSolrDao, "solrServerReadOnly", mockSolrServerReadOnly);

        ArgumentCaptor<SolrQuery> captor = ArgumentCaptor.forClass(SolrQuery.class);
        Mockito.when(mockSolrServerReadOnly.query(captor.capture())).thenReturn(Mockito.mock(QueryResponse.class));

        orgDisambiguatedSolrDao.getOrgs("test", 0, 10, false);
        
        ReflectionTestUtils.setField(orgDisambiguatedSolrDao, "solrServerReadOnly", defaultSolrServer);
    }

}
