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
        URL solrXml = EmbeddedSolrFactory.class.getResource("/solr" + "/solr.xml");
        File solrHomeDir = new File(solrHome.getFile());
        System.setProperty("solr.solr.home", solrHomeDir.getAbsolutePath());
        File solrXmlFile = new File(solrXml.getFile());;
        CoreContainer coreContainer = CoreContainer.createAndLoad(solrHomeDir.getAbsolutePath(), solrXmlFile);
        return coreContainer;
    }
}
