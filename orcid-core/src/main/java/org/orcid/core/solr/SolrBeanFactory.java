package org.orcid.core.solr;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrBeanFactory {

    @Value("${org.orcid.persistence.solr.legacy.url}")
    private String legacySolrMasterUrl;
    
    @Value("${org.orcid.persistence.solr.read.only.url}") 
    private String solrReadOnlyUrl;
    
    @Value("${org.orcid.persistence.solr.connectionTimeout:1000}")
    private Integer connectionTimeout;
    
    @Value("${org.orcid.listener.persistence.solr.socketTimeout:2000}")
    private Integer socketTimeout;
    
    @Value("${org.orcid.persistence.solr.allowCompression:true}")
    private Boolean allowCompression;
    
    @Resource(name = "responseParser")
    private XMLResponseParser responseParser;
    
    private static final String DEFAULT_COLLECTION = "/profile";
    private static final String ORGS_COLLECTION = "/org";
    private static final String FUNDING_SUB_TYPE_COLLECTION = "/fundingSubType";
    
    @Bean(name = "legacyRecordSolrClient")
    public SolrClient legacyRecordSolrClient() {
        return new HttpSolrClient.Builder(legacySolrMasterUrl + DEFAULT_COLLECTION)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
    
    @Bean(name = "legacyOrgsSolrClient")
    public SolrClient legacyOrgsSolrClient() {
        return new HttpSolrClient.Builder(legacySolrMasterUrl + ORGS_COLLECTION)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
    
    @Bean(name = "legacyFundingSubTypeSolrClient")
    public SolrClient legacyFundingSubTypeSolrClient() {
        return new HttpSolrClient.Builder(legacySolrMasterUrl + FUNDING_SUB_TYPE_COLLECTION)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
    
    @Bean(name = "solrReadOnlyProfileClient")
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(solrReadOnlyUrl + DEFAULT_COLLECTION)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
    
    @Bean(name = "solrReadOnlyOrgsClient")
    public SolrClient solrOrgsClient() {
        return new HttpSolrClient.Builder(solrReadOnlyUrl + ORGS_COLLECTION)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
    
    @Bean(name = "solrReadOnlyFundingSubTypeClient")
    public SolrClient solrFundingSubTypeClient() {
        return new HttpSolrClient.Builder(solrReadOnlyUrl + FUNDING_SUB_TYPE_COLLECTION)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .allowCompression(allowCompression)
                .withResponseParser(responseParser)                
                .build();
    }
}
