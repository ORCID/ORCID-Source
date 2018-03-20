package org.orcid.listener;

import javax.annotation.Resource;

import org.orcid.listener.mongo.MongoMessageProcessor;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Component
public class UpdatedOrcidWorker implements RemovalListener<String, LastModifiedMessage> {

    Logger LOG = LoggerFactory.getLogger(UpdatedOrcidWorker.class);

    @Resource
    private S3MessageProcessor s3Processor;

    @Resource
    private SolrMessageProcessor solrProcessor;

    @Resource
    private MongoMessageProcessor mongoProcessor;

    /**
     * Fires when the queue evicts after an inactivity period.
     * Populates the Amazon S3 buckets and updates solr index
     */
    public void onRemoval(RemovalNotification<String, LastModifiedMessage> removal) {
        if (removal.wasEvicted()) {
            LastModifiedMessage m = removal.getValue();
            LOG.info("Removing " + removal.getKey() + " from UpdatedOrcidCacheQueue '" + m.getLastUpdated() + "' Removal cause " + removal.getCause() );            
            s3Processor.accept(m);
            solrProcessor.accept(m);
            mongoProcessor.accept(m);
        }
    }
}
