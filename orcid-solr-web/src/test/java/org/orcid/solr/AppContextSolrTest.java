/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-orcid-solr-config.xml" })
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
