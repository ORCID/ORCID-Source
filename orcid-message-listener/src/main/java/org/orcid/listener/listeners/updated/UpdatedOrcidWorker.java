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
package org.orcid.listener.listeners.updated;

import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.clients.SolrIndexUpdater;
import org.orcid.listener.common.ExceptionHandler;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Component
public class UpdatedOrcidWorker implements RemovalListener<String, LastModifiedMessage> {

    Logger LOG = LoggerFactory.getLogger(UpdatedOrcidWorker.class);

    @Value("${org.orcid.persistence.messaging.dump_indexing.enabled}")
    private boolean dumpIndexingEnabled;
    
    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean solrIndexingEnabled;
    
    @Resource
    private Orcid12APIClient orcid12ApiClient;
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    @Resource
    private SolrIndexUpdater solrIndexUpdater;
    @Resource
    private S3Updater s3Updater;
    @Resource
    private ExceptionHandler exceptionHandler;

    /**
     * Populates the Amazon S3 buckets and updates solr index
     */
    public void onRemoval(RemovalNotification<String, LastModifiedMessage> removal) {
        if (removal.wasEvicted()) {
            LastModifiedMessage m = removal.getValue();
            LOG.info("Removing " + removal.getKey() + " from UpdatedOrcidCacheQueue");
            LOG.info("Processing " + m.getLastUpdated());
            
            try{
                String orcid = m.getOrcid();
                
                OrcidMessage profile = fetchPublicProfile(orcid);
                Record record = fetchPublicRecord(orcid);
                
                // Phase #1: update S3 
                if(dumpIndexingEnabled) {
                    updateS3(orcid, profile, record);    
                }
                
                // Phase # 2 - update solr
                if(solrIndexingEnabled) {
                    updateSolr(orcid, record, profile.toString());    
                } 
            } catch(LockedRecordException lre) {                
                try {
                    exceptionHandler.handleLockedRecordException(m, lre.getOrcidMessage());
                } catch (JsonProcessingException | AmazonClientException | JAXBException e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + m.getOrcid(), e1);
                } catch (DeprecatedRecordException e1) {
                    // Should never happen, since it is already locked
                }                
            } catch(DeprecatedRecordException dre) {
                try {
                    exceptionHandler.handleDeprecatedRecordException(m, dre.getOrcidDeprecated());
                } catch (JsonProcessingException | AmazonClientException | JAXBException e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + m.getOrcid(), e1);
                } catch (LockedRecordException e1) {
                    // Should never happen, since it is already deprecated
                } 
            } catch(Exception e) {
                LOG.error("Unable to fetch record " + m.getOrcid() + " so, unable to feed nor S3 nor SOLR");
            }
        }
    }
    
    /**
     * Fetch the public OrcidProfile element from the 1.2 API
     * @param orcid
     *          The record id
     * @return public OrcidProfile or null in case an exception happens
     * @throws LockedRecordException          
     * */
    private OrcidMessage fetchPublicProfile(String orcid) throws Exception {
        try {
            return orcid12ApiClient.fetchPublicProfile(orcid);
        } catch(LockedRecordException lre) {
            LOG.error("Record " + orcid + " is deprecated");
            throw lre;
        } catch (Exception e) {
            // If we can't fetch the record from 1.2, we should throw any
            // exception, since we will not be able to index SOLR nor feed S3
            LOG.error("Unable to fetch OrcidProfile (1.2 API) " + orcid, e);
            throw e;
        }
    }
    
    /**
     * Fetch the public Record element from the 2.0 API
     * @param orcid
     *          The record id
     * @return public Record or null in case an exception happens
     * @throws LockedRecordException          
     * */
    private Record fetchPublicRecord(String orcid) throws LockedRecordException {
        try {
            return orcid20ApiClient.fetchPublicProfile(orcid);
        } catch(LockedRecordException lre) {
            LOG.error("Record " + orcid + " is deprecated");
            throw lre;
        } catch(Exception e) {
            // If we can't fetch the record from 2.0 we can ignore the error for
            // now, since the only thing that will fail is feeding S3, which is
            // still a prototype
            //TODO: What should we do in this case when this actually moves live?
            LOG.error("Unable to fetch Record (2.0 API) " + orcid, e);
        } 
        return null;
    }
    
    private void updateS3(String orcid, OrcidMessage profile, Record record) {
        // Update API 1.2
        if(profile != null) {
            try {
                s3Updater.updateS3(orcid, profile);
            } catch(Exception e) {
                //Unable to update record in S3
                LOG.error("Unable to update S3 bucket for 1.2 API", e);
            } 
        }
        
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
        
    private void updateSolr(String orcid,Record record, String v12profileXML) {
        Date lastModifiedFromprofile = record.getHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        Date lastModifiedFromSolr = solrIndexUpdater.retrieveLastModified(orcid);
        // note this is slightly different from existing behaviour
        if (lastModifiedFromprofile.after(lastModifiedFromSolr))
            solrIndexUpdater.updateSolrIndex(record,v12profileXML);

    }
}
