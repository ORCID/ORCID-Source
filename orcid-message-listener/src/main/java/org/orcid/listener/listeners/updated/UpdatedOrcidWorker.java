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

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.clients.LockedRecordException;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.clients.SolrIndexUpdater;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /**
     * Populates the Amazon S3 buckets and updates solr index
     */
    public void onRemoval(RemovalNotification<String, LastModifiedMessage> removal) {
        if (removal.wasEvicted()) {
            LastModifiedMessage m = removal.getValue();
            LOG.info("Removing " + removal.getKey() + " from UpdatedOrcidCacheQueue");
            LOG.info("Processing " + m.getLastUpdated());
            
            try{
                OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(m.getOrcid());
                Record record = orcid20ApiClient.fetchPublicProfile(m.getOrcid());//can we not just transform the above?
                                
                // Phase #1: update S3 
                if(dumpIndexingEnabled) {
                    updateS3(m.getOrcid(), profile, record);    
                }
                
                // Phase # 2 - update solr
                if(solrIndexingEnabled) {
                    updateSolr(m.getOrcid(), record);    
                } 

            }catch(LockedRecordException e){
                Date lastModifiedFromSolr = solrIndexUpdater.retrieveLastModified(m.getOrcid());                // note this is slightly different from existing behaviour
                if (m.getLastUpdated().after(lastModifiedFromSolr))
                    solrIndexUpdater.updateSolrIndexForLockedRecord(m.getOrcid(), m.getLastUpdated());
                //TODO: something with S3
            }
        }

    }
    
    private void updateS3(String orcid,OrcidProfile profile,Record record) {
        // Update API 1.2
        try {
            s3Updater.updateS3(orcid, profile);
        } catch(Exception e) {
            //Unable to update record in S3
            LOG.error("Unable to update S3 bucket for 1.2 API", e);
        } 
        
        // Update API 2.0
        try {
            s3Updater.updateS3(orcid, record);
        } catch(Exception e) {
            //Unable to update record in S3
            LOG.error("Unable to update S3 bucket for 2.0 API", e);
        }
    }
        
    private void updateSolr(String orcid,Record record) {
        Date lastModifiedFromprofile = record.getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        Date lastModifiedFromSolr = solrIndexUpdater.retrieveLastModified(orcid);
        // note this is slightly different from existing behaviour
        if (lastModifiedFromprofile.after(lastModifiedFromSolr))
            solrIndexUpdater.updateSolrIndex(record);

    }
}
