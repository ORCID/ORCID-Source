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
package org.orcid.activitiesindexer.listener;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.xml.bind.JAXBException;

import org.orcid.activitiesindexer.s3.S3MessageProcessor;
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
    @Override
    public void onMessage(Message message) {
        Map<String, String> map = getMapFromMessage(message);
        LastModifiedMessage lastModifiedMessage = new LastModifiedMessage(map);
        String orcid = lastModifiedMessage.getOrcid();
        LOG.info("Recieved " + reindexTopicName + " message for orcid " + orcid + " " + lastModifiedMessage.getLastUpdated());
        s3Processor.accept(lastModifiedMessage);               
    }         
}
