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

import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.common.ExceptionHandler;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class ReIndexListener {

    Logger LOG = LoggerFactory.getLogger(ReIndexListener.class);

    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean isSolrIndexingEnalbed;
    
    @Resource
    private Orcid12APIClient orcid12ApiClient;
    
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    
    @Resource
    private S3Updater s3Updater; 
    
    @Resource
    private ExceptionHandler exceptionHandler;

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
        OrcidMessage profile = null;
        Record record = null;
        
        //If record is locked
        try {
            profile = orcid12ApiClient.fetchPublicProfile(orcid);
        } catch(LockedRecordException lre) {
            try {
                exceptionHandler.handleLockedRecordException(message, lre.getOrcidMessage());                
            } catch (DeprecatedRecordException e) {
                // Should never happen, since it is already locked
            }
            return; 
        } catch (DeprecatedRecordException dre) {
            try {
                exceptionHandler.handleDeprecatedRecordException(message, dre.getOrcidDeprecated());
            } catch (LockedRecordException e) {
                // Should never happen, since it is already deprecated
            }
            return;
        }
             
        if(profile != null) {            
            //Update 1.2 buckets
            s3Updater.updateS3(orcid, profile); 
            
            profile = null;
        }
        
        //Fetch 2.0 record
        try {
            record = orcid20ApiClient.fetchPublicProfile(orcid);
        } catch(Exception e) {
            LOG.warn("Unable to fetch record " + orcid + " from 2.0 API");            
        }
        
        if(record != null) {
            //Update 2.0 buckets          
            s3Updater.updateS3(orcid, record);
            record = null;
        }                
    }         
}
