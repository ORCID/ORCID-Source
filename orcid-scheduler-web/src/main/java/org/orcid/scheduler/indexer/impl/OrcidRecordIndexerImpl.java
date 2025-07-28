package org.orcid.scheduler.indexer.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.messaging.JmsMessageSender;
import org.orcid.core.utils.listener.LastModifiedMessage;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.scheduler.indexer.OrcidRecordIndexer;
import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class OrcidRecordIndexerImpl implements OrcidRecordIndexer {

    protected static final Logger LOG = LoggerFactory.getLogger(OrcidRecordIndexerImpl.class);

    @Value("${org.orcid.persistence.messaging.indexing.batch.size:100000}")
    private int INDEXING_BATCH_SIZE;
    
    @Value("${org.orcid.persistence.messaging.indexing.batch.max.count:3}")
    private int INDEXING_BATCH_MAX_COUNT;

    @Value("${org.orcid.persistence.indexing.delay:5}")
    private Integer indexingDelay;
    
    @Value("${org.orcid.messaging.updated.solr:updateSOLR}")
    private String updateSolrQueueName;
    @Value("${org.orcid.messaging.reindex.solr:reindexSOLR}")
    private String reindexSolrQueueName;
    
    @Value("${org.orcid.messaging.updated.v2:updateV2Record}")
    private String updateV2RecordQueueName;
    @Value("${org.orcid.messaging.reindex.v2:reindexV2Record}")
    private String reindexV2RecordQueueName;
    
    @Value("${org.orcid.messaging.updated.v3:updateV3Record}")
    private String updateV3RecordQueueName;
    @Value("${org.orcid.messaging.reindex.v3:reindexV3Record}")
    private String reindexV3RecordQueueName;
    
    @Resource
    private ProfileDao profileDao;

    @Resource
    private ProfileDao profileDaoReadOnly;
    
    @Resource
    private ProfileLastModifiedDao profileLastModifiedDaoReadOnly;
    
    @Resource(name = "jmsMessageSender")
    private JmsMessageSender messaging;
    
    @Resource
    private SlackManager slackManager;
    
    protected int claimWaitPeriodDays = 10;
    
    @Value("${org.orcid.indexer.slack.notification.interval:10}")
    private Integer slackIntervalMinutes;
    
    private Date lastSlackNotification;

    public void setClaimWaitPeriodDays(int claimWaitPeriodDays) {
        this.claimWaitPeriodDays = claimWaitPeriodDays;
    }
    
    @Override
    public void processProfilesWithPendingFlagAndAddToMessageQueue() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.PENDING);
    }

    @Override
    public void processProfilesWithReindexFlagAndAddToMessageQueue() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.REINDEX);
    }    
    
    @Override
    public void reindexRecordsOnSolr() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.SOLR_UPDATE);
    }
    
    @Override
    public void reindexRecordsOnS3() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.S3_UPDATE);
    }    
    
    private void processProfilesWithFlagAndAddToMessageQueue(IndexingStatus status) {
        LOG.info("Processing profiles with " + status.name() + " flag. Batch size: " + INDEXING_BATCH_SIZE + ",Batch Max count: " + INDEXING_BATCH_MAX_COUNT);
        for (int i = 0; i < INDEXING_BATCH_MAX_COUNT; i++) {
            boolean batchProcessed = processProfilesForIndexing(status);
            if (batchProcessed) {               
                LOG.info("Processed batch " + i + " with " + status.name() + "h flag.");
            } else {
                LOG.info("No more records found with " + status.name() + " flag or connection issues, stopping processing.");
                break;
            }
        }
    }
    
    private boolean index(LastModifiedMessage mess, String queue) {
        if (!messaging.send(mess, queue)) {
            LOG.warn("ABORTED - couldnt send messages to queue ' " + queue + "'");                    
            return true;
        }
        return false;
    }  
    
    
    private boolean processProfilesForIndexing(IndexingStatus status) {
        boolean connectionIssue = false;
        String solrQueue = (IndexingStatus.REINDEX.equals(status) ? reindexSolrQueueName : updateSolrQueueName);
        String v2Queue = (IndexingStatus.REINDEX.equals(status) ? reindexV2RecordQueueName : updateV2RecordQueueName);
        String v3Queue = (IndexingStatus.REINDEX.equals(status) ? reindexV3RecordQueueName : updateV3RecordQueueName);
        List<String> orcidsForIndexing = new ArrayList<>();            
            try {
                if (IndexingStatus.REINDEX.equals(status) || IndexingStatus.S3_UPDATE.equals(status)) {
                    orcidsForIndexing = profileDaoReadOnly.findOrcidsByIndexingStatus(status, INDEXING_BATCH_SIZE, 0);
                } else {
                    orcidsForIndexing = profileDaoReadOnly.findOrcidsByIndexingStatus(status, INDEXING_BATCH_SIZE, indexingDelay);
                }
                lastSlackNotification = null;
            } catch(Exception e) {
                LOG.error("Exception fetching records to index", e);
                // Send a slack notification every 'slackIntervalMinutes' minutes
                if(lastSlackNotification == null || System.currentTimeMillis() > (lastSlackNotification.getTime() + (slackIntervalMinutes * 60 * 1000))) {
                    String message = String.format("Unable to fetch records with indexing status: %s, this causes that SOLR and S3 might be falling behind. For troubleshooting please refere to https://github.com/ORCID/ORCID-Internal/wiki/Problems-with-record-indexing-in-the-scheduler", status);                
                    slackManager.sendSystemAlert(message);
                    lastSlackNotification = new Date();
                }                
            }
            LOG.info(status.name() + " - processing batch of " + orcidsForIndexing.size());
            if (orcidsForIndexing.isEmpty()) {
                LOG.info("No records found with indexing status " + status.name());
                return false;
            }

            for (String orcid : orcidsForIndexing) {
                // TODO: Why do we need this? We should be able to fetch orcid + last_modified in the previous query
                Date last = profileLastModifiedDaoReadOnly.retrieveLastModifiedDate(orcid);
                LastModifiedMessage mess = new LastModifiedMessage(orcid, last);
                
                if(IndexingStatus.SOLR_UPDATE.equals(status)) {
                    connectionIssue = index(mess, solrQueue);
                } else if(IndexingStatus.S3_UPDATE.equals(orcid)) {
                    connectionIssue = index(mess, v3Queue);                
                    if(!connectionIssue)
                        connectionIssue = index(mess, v2Queue);
                } else {
                    connectionIssue = index(mess, solrQueue);
                    if(!connectionIssue)
                        connectionIssue = index(mess, v3Queue);
                    if(!connectionIssue)
                        connectionIssue = index(mess, v2Queue);
                }                
                
                if(!connectionIssue) {
                    try {
                        profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);
                    } catch(Exception e) {
                        LOG.error("Exception updating indexing status for record " + orcid, e);
                        // Send a slack notification every 'slackIntervalMinutes' minutes
                        if(lastSlackNotification == null || System.currentTimeMillis() > (lastSlackNotification.getTime() + (slackIntervalMinutes * 60 * 1000))) {
                            String message = "Unable to update indexing status for record: " + orcid + ", error: " + e.getMessage() + "\nThis causes that SOLR and S3 might be falling behind. For troubleshooting please refere to https://github.com/ORCID/orcid-devops/wiki/Troubleshooting#indexing-status";
                            slackManager.sendSystemAlert(message);
                            lastSlackNotification = new Date();
                        }                
                    }
                }
            }
        
        // returns true if there wasn't a connection issue and the indexing batch size was reached meaning a new batch should be processed if batch count was not reached
        return !connectionIssue && orcidsForIndexing.size() == INDEXING_BATCH_SIZE ;
    }
}
