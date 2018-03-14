/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.listener;

import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.listener.mongo.MongoMessageProcessor;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${org.orcid.persistence.messaging.topic.reindex}")
    private String reindexTopicName;

    /**
     * Processes messages on receipt.
     * 
     * @param map
     * @throws JsonProcessingException 
     * @throws JAXBException 
     * @throws AmazonClientException 
     */
    public void processMessage(final Map<String, String> map) throws JsonProcessingException, AmazonClientException, JAXBException {        
        LastModifiedMessage message = new LastModifiedMessage(map);
        String orcid = message.getOrcid();
        LOG.info("Recieved " + reindexTopicName + " message for orcid " + orcid + " " + message.getLastUpdated());
        s3Processor.accept(message);               
        solrProcessor.accept(message); 
        mongoProcessor.accept(message);
    }         
}
