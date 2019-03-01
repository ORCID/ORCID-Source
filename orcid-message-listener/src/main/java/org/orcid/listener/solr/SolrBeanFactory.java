package org.orcid.listener.solr;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrBeanFactory {

    @Value("${org.orcid.persistence.solr.url}") 
    private String solrUrl;
    
    @Value("${org.orcid.persistence.solr.connectionTimeout}")
    private Integer connectionTimeout;
    
    @Value("${org.orcid.listener.persistence.solr.socketTimeout}")
    private Integer socketTimeout;
    
    @Value("${org.orcid.persistence.solr.allowCompression}")
    private Boolean allowCompression;
    
    @Resource(name = "responseParser")
    private XMLResponseParser responseParser;
    
    @Bean(name = "solrClient")
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
    
}
