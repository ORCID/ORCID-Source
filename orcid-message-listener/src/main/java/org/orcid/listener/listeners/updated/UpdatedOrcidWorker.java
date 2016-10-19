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
            String orcid = m.getOrcid();
            
            // Phase #1: update S3 
            if(dumpIndexingEnabled) {
                updateS3(orcid);    
            }
            
            // Phase # 2 - update solr
            if(solrIndexingEnabled) {
                updateSolr(orcid);    
            }                        
        }
    }
    
    private void updateS3(String orcid) {
        // Update API 1.2
        try {
            OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(orcid);                
            s3Updater.updateS3(orcid, profile);
        } catch(Exception e) {
            //Unable to update record in S3
            LOG.error("Unable to update S3 bucket for 1.2 API", e);
        } 
        
        // Update API 2.0
        try {
            Record record = orcid20ApiClient.fetchPublicProfile(orcid);
            s3Updater.updateS3(orcid, record);
        } catch(Exception e) {
            //Unable to update record in S3
            LOG.error("Unable to update S3 bucket for 2.0 API", e);
        }
    }
        
    private void updateSolr(String orcid) {
        OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(orcid);                
        Date lastModifiedFromprofile = profile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        Date lastModifiedFromSolr = solrIndexUpdater.retrieveLastModified(orcid);
        // note this is slightly different from existing behaviour
        if (lastModifiedFromprofile.after(lastModifiedFromSolr))
            solrIndexUpdater.updateSolrIndex(profile);
    }
}
