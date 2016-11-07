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
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.listener.common.LastModifiedMessageProcessor;
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
    private LastModifiedMessageProcessor processor;

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
        processor.accept(message);               
    }         
}
