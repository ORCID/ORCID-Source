package org.orcid.listener;

import java.util.Map;

import javax.annotation.Resource;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.clients.SolrIndexUpdater;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ReIndexListener {

    Logger LOG = LoggerFactory.getLogger(ReIndexListener.class);
        
    @Resource
    private Orcid12APIClient orcid12ApiClient;
    @Resource
    private SolrIndexUpdater solrIndexUpdater;
    @Resource
    private S3Updater s3Updater;
    
    /** Processes messages on receipt.
     * 
     * @param map
     */
    @JmsListener(destination=MessageConstants.Queues.REINDEX)
    public void processMessage(final Map<String,String> map) {
        LastModifiedMessage message = new LastModifiedMessage(map);
        LOG.info("Recieved "+MessageConstants.Queues.REINDEX+" message for orcid "+message.getOrcid() + " "+message.getLastUpdated()); 
        OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(message.getOrcid());
        solrIndexUpdater.updateSolrIndex(profile);
        s3Updater.updateS3(profile);        
    }
}
