package org.orcid.listener;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.xml.bind.JAXBException;

import org.orcid.listener.mongo.MongoMessageProcessor;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ReIndexListener extends BaseListener implements MessageListener {

    Logger LOG = LoggerFactory.getLogger(ReIndexListener.class);

    @Resource
    private S3MessageProcessor s3Processor;

    @Resource
    private SolrMessageProcessor solrProcessor;

    @Resource
    private MongoMessageProcessor mongoProcessor;

    @Value("${org.orcid.persistence.messaging.topic.reindex}")
    private String reindexTopicName;

    private final ExecutorService executor;

    public ReIndexListener(@Value("${org.orcid.message-listener.reindex_orcid.threads:5}") Integer maxThreads) {
        executor = Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * Processes messages on receipt.
     * 
     * @param map
     * @throws JsonProcessingException
     * @throws JAXBException
     * @throws AmazonClientException
     */
    @Override
    public void onMessage(Message message) {
        executor.submit(() -> {
            Map<String, String> map = getMapFromMessage(message);
            LastModifiedMessage lastModifiedMessage = new LastModifiedMessage(map);
            String orcid = lastModifiedMessage.getOrcid();
            LOG.info("Recieved " + reindexTopicName + " message for orcid " + orcid + " " + lastModifiedMessage.getLastUpdated());
            //s3Processor.accept(lastModifiedMessage);
            solrProcessor.accept(lastModifiedMessage);
            mongoProcessor.accept(lastModifiedMessage);
        });

    }
}
