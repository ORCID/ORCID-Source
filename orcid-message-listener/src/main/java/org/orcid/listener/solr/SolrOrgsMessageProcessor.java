package org.orcid.listener.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrOrgsMessageProcessor {

    Logger LOG = LoggerFactory.getLogger(SolrMessageProcessor.class);
    
    @Value("${org.orcid.persistence.messaging.solr_org_indexing.enabled:true}")
    private boolean isSolrIndexingEnabled;
}
