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
package org.orcid.listener.common;

import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

/** Core logic for listeners
 * 
 * @author tom
 *
 */
@Component
public class LastModifiedMessageProcessor implements Consumer<LastModifiedMessage>{

    Logger LOG = LoggerFactory.getLogger(LastModifiedMessageProcessor.class);

    @Value("${org.orcid.persistence.messaging.dump_indexing.enabled}")
    private boolean dumpIndexingEnabled;
    
    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean solrIndexingEnabled;
    
    @Resource
    private Orcid12APIClient orcid12ApiClient;
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    @Resource
    private S3Updater s3Updater;
    @Resource
    private ExceptionHandler exceptionHandler;
    
    /**
     * Populates the Amazon S3 buckets and updates solr index
     */
    public void accept(LastModifiedMessage m) {
            String orcid = m.getOrcid();
            try{
                // Phase #1: update S3 
                if(dumpIndexingEnabled) {
                    updateS3_1_2_API(orcid);
                    updateS3_2_0_API(orcid);
                } 
                
            } catch(LockedRecordException lre) {                
                try {
                    LOG.error("Record " + orcid + " is locked");
                    exceptionHandler.handleLockedRecordException(m, lre.getOrcidMessage());
                } catch (JsonProcessingException | AmazonClientException | JAXBException e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + m.getOrcid(), e1);
                } catch (DeprecatedRecordException e1) {
                    // Should never happen, since it is already locked
                }                
            } catch(DeprecatedRecordException dre) {
                try {
                    LOG.error("Record " + orcid + " is deprecated");
                    exceptionHandler.handleDeprecatedRecordException(m, dre.getOrcidDeprecated());
                } catch (JsonProcessingException | AmazonClientException | JAXBException e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + m.getOrcid(), e1);
                } catch (LockedRecordException e1) {
                    // Should never happen, since it is already deprecated
                } 
            } catch(Exception e) {
                //something else went wrong fetching record from ORCID and threw a runtime exception
                LOG.error("Unable to fetch record " + m.getOrcid() + " so, unable to feed nor S3 nor SOLR");
            }
     
    }
    
    private void updateS3_1_2_API(String orcid) throws LockedRecordException, DeprecatedRecordException {
        OrcidMessage profile = orcid12ApiClient.fetchPublicProfile(orcid);
        // Update API 1.2
        if(profile != null) {
            try {
                s3Updater.updateS3(orcid, profile);
            } catch(Exception e) {
                //Unable to update record in S3
                LOG.error("Unable to update S3 bucket for 1.2 API", e);
            } 
        }                
    }
    
    private void updateS3_2_0_API(String orcid) throws LockedRecordException, DeprecatedRecordException {
        Record record = orcid20ApiClient.fetchPublicProfile(orcid);
        // Update API 2.0
        if(record != null) {
            try {
                s3Updater.updateS3(orcid, record);
            } catch(Exception e) {
                //Unable to update record in S3
                LOG.error("Unable to update S3 bucket for 2.0 API", e);
            }
        }
    }        
}
