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
package org.orcid.persistence.dao;

import java.io.File;
import java.net.URL;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

/**
 * Factory method to wire in an embedded solr server to the SolrDao for testing
 * 
 * @author jamesb
 * 
 */
class EmbeddedSolrFactory {

    public static SolrServer createInstance() throws Exception {

        URL solrHome = EmbeddedSolrFactory.class.getResource("/solr");
        File solrHomeDir = new File(solrHome.getFile());
        System.setProperty("solr.solr.home", solrHomeDir.getAbsolutePath());
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");
        return server;
    }
}
