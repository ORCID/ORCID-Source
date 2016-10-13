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

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.clients.LockedRecordException;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
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
    private Orcid20APIClient orcid20ApiClient;
    @Resource
    private SolrIndexUpdater solrIndexUpdater;
    @Resource
    private S3Updater s3Updater; 

    /**
     * Processes messages on receipt.
     * 
     * @param map
     */
    @JmsListener(destination = MessageConstants.Queues.REINDEX)
    public void processMessage(final Map<String, String> map) {
        LastModifiedMessage message = new LastModifiedMessage(map);
        LOG.info("Recieved " + MessageConstants.Queues.REINDEX + " message for orcid " + message.getOrcid() + " " + message.getLastUpdated());
        try{
            OrcidProfile profile = orcid12ApiClient.fetchPublicProfile(message.getOrcid());
            Record record = orcid20ApiClient.fetchPublicProfile(message.getOrcid());//can we not just transform the above?
            solrIndexUpdater.updateSolrIndex(record);
            s3Updater.updateS3(profile);            
        }catch (LockedRecordException e){
            //if the record is locked then 'blank' it in Solr.
            solrIndexUpdater.updateSolrIndexForLockedRecord(message.getOrcid(),message.getLastUpdated());
        }
    }
}
