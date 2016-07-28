package org.orcid.listener.listeners.updated;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.clients.SolrIndexUpdater;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Component
public class UpdatedOrcidWorker implements RemovalListener<String, LastModifiedMessage>{

    Logger LOG = LoggerFactory.getLogger(UpdatedOrcidWorker.class);

    @Resource
    private Orcid12APIClient orcid12ApiClient;
    @Resource
    private SolrIndexUpdater solrIndexUpdater;
    @Resource
    private S3Updater s3Updater;

    /** Fetches ORCID profile,
     * compares last modified with SOLR
     * if after SOLR last modified, updates SOLR index
     * TODO: update S3
     * 
     */
    public void onRemoval(RemovalNotification<String, LastModifiedMessage> removal) {
        if (removal.wasEvicted()){
            LastModifiedMessage m = removal.getValue();
            LOG.info("Removing "+ removal.getKey() + " from UpdatedOrcidCacheQueue");
            LOG.info("Processing " +m.getLastUpdated());
            OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(m.getOrcid());
            Date lastModifiedFromprofile = profile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
            Date lastModifiedFromSolr = solrIndexUpdater.retrieveLastModified(m.getOrcid());
            //note this is slightly different from existing behaviour
            if (lastModifiedFromprofile.after(lastModifiedFromSolr)) 
                solrIndexUpdater.updateSolrIndex(profile);
            //now do the same for S3...
            s3Updater.updateS3(profile); 
        }
    }
}
