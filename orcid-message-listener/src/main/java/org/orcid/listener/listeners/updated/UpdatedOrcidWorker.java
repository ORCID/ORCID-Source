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

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.clients.SolrIndexUpdater;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Component
public class UpdatedOrcidWorker implements RemovalListener<String, LastModifiedMessage> {

    Logger LOG = LoggerFactory.getLogger(UpdatedOrcidWorker.class);

    @Resource
    private Orcid12APIClient orcid12ApiClient;
    @Resource
    private SolrIndexUpdater solrIndexUpdater;
    @Resource
    private S3Updater s3Updater;

    /**
     * Fetches ORCID profile, compares last modified with SOLR if after SOLR
     * last modified, updates SOLR index TODO: update S3
     * 
     */
    public void onRemoval(RemovalNotification<String, LastModifiedMessage> removal) {
        if (removal.wasEvicted()) {
            LastModifiedMessage m = removal.getValue();
            LOG.info("Removing " + removal.getKey() + " from UpdatedOrcidCacheQueue");
            LOG.info("Processing " + m.getLastUpdated());
            OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(m.getOrcid());
            // update solr
            //updateSolr(profile);
            // now do the same for S3...
            try {
                s3Updater.updateS3(profile);
            } catch(JsonProcessingException jpe) {
                //Unable to update record in S3
            } catch (AmazonClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void updateSolr(OrcidProfile profile) {
        String orcidId = profile.getOrcidIdentifier().getPath();
        Date lastModifiedFromprofile = profile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        Date lastModifiedFromSolr = solrIndexUpdater.retrieveLastModified(orcidId);
        // note this is slightly different from existing behaviour
        if (lastModifiedFromprofile.after(lastModifiedFromSolr))
            solrIndexUpdater.updateSolrIndex(profile);
    }
}
