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

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.LastModifiedMessage;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class ExceptionHandler {

    @Resource
    private Orcid12APIClient orcid12ApiClient;
    
    @Resource
    private Orcid20APIClient orcid20ApiClient;        
    
    @Resource
    private S3Updater s3Updater; 
    
    /**
     * If the record is locked:
     * - blank it in 1.2 bucket
     * - blank it in 2.0 bucket
     * @throws JAXBException 
     * @throws AmazonClientException 
     * @throws JsonProcessingException 
     * @throws DeprecatedRecordException 
     * */
    public void handleLockedRecordException(LastModifiedMessage message, OrcidMessage errorMessage) throws JsonProcessingException, AmazonClientException, JAXBException, DeprecatedRecordException {
        //Update 1.2 buckets        
        s3Updater.updateS3(message.getOrcid(), errorMessage);
        //Update 2.0 buckets
        try {
            orcid20ApiClient.fetchPublicProfile(message.getOrcid()); 
        } catch(LockedRecordException lre) {
            s3Updater.updateS3(message.getOrcid(), lre.getOrcidError());
        } 
    }
    
    /**
     * If the record is deprecated:
     *
     * - blank it in 1.2 bucket
     * - blank it in 2.0 bucket
     * @throws JAXBException 
     * @throws AmazonClientException 
     * @throws JsonProcessingException 
     * @throws DeprecatedRecordException 
     * @throws LockedRecordException 
     * */
    public void handleDeprecatedRecordException(LastModifiedMessage message, OrcidDeprecated errorMessage) throws JsonProcessingException, AmazonClientException, JAXBException, LockedRecordException {
        //Update 1.2 buckets        
        s3Updater.updateS3(message.getOrcid(), errorMessage);
        //Update 2.0 buckets
        try {
            orcid20ApiClient.fetchPublicProfile(message.getOrcid()); 
        } catch(DeprecatedRecordException lre) {
            s3Updater.updateS3(message.getOrcid(), lre.getOrcidError());
        } 
    }
}
