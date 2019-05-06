package org.orcid.solr;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-solr-config.xml" })
public class AppContextSolrTest {

    @Resource(name = "solrServer")
    private SolrServer solrServer;

    @Before
    public void init() {
        assertNotNull(solrServer);
    }

    @Test
    @Ignore
    public void testServerRunning() throws Exception {
        SolrQuery solrQuery = new SolrQuery().setQuery("carberry");
        QueryResponse response = solrServer.query(solrQuery);
        assertNotNull(response);
    }
}
