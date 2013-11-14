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
import java.io.FileNotFoundException;
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
    
    private static CoreContainer coreContainer;
    
    public static SolrServer createInstance() throws Exception {
        return createInstance("");
    }

    public static SolrServer createInstance(String coreName) throws Exception {
        if(coreContainer == null){
            coreContainer = createCoreContainer();
        }
        EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, coreName);
        return server;
    }

    private static CoreContainer createCoreContainer() throws FileNotFoundException {
        URL solrHome = EmbeddedSolrFactory.class.getResource("/solr");
        File solrHomeDir = new File(solrHome.getFile());
        System.setProperty("solr.solr.home", solrHomeDir.getAbsolutePath());
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        return coreContainer;
    }
}
