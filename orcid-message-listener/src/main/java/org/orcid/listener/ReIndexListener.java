package org.orcid.listener;

import java.util.Map;
import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.listener.mongo.MongoMessageProcessor;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class ReIndexListener {

    Logger LOG = LoggerFactory.getLogger(ReIndexListener.class);
    
    @Resource
    private S3MessageProcessor s3Processor;

    @Resource
    private SolrMessageProcessor solrProcessor;

    @Resource
    private MongoMessageProcessor mongoProcessor;

    /**
     * Processes messages on receipt.
     * 
     * @param map
     * @throws JsonProcessingException 
     * @throws JAXBException 
     * @throws AmazonClientException 
     */
    @JmsListener(destination = MessageConstants.Queues.REINDEX)
    public void processMessage(final Map<String, String> map) throws JsonProcessingException, AmazonClientException, JAXBException {        
        LastModifiedMessage message = new LastModifiedMessage(map);
        String orcid = message.getOrcid();
        LOG.info("Recieved " + MessageConstants.Queues.REINDEX + " message for orcid " + orcid + " " + message.getLastUpdated());
        s3Processor.accept(message);               
        solrProcessor.accept(message); 
        mongoProcessor.accept(message);
    }         
}
