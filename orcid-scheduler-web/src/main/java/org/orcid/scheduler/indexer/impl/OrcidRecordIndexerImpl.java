package org.orcid.scheduler.indexer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.messaging.JmsMessageSender;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.scheduler.indexer.OrcidRecordIndexer;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class OrcidRecordIndexerImpl implements OrcidRecordIndexer {

    protected static final Logger LOG = LoggerFactory.getLogger(OrcidRecordIndexerImpl.class);

    @Value("${org.orcid.persistence.messaging.indexing.batch.size:100}")
    private int INDEXING_BATCH_SIZE;

    @Value("${org.orcid.persistence.messaging.updated.solr}")
    private String updateSolrQueueName;
    @Value("${org.orcid.persistence.messaging.updated.summary}")
    private String updateSummaryQueueName;
    @Value("${org.orcid.persistence.messaging.updated.activity}")
    private String updateActivitiesQueueName;
    @Value("${org.orcid.persistence.messaging.reindex.solr}")
    private String reindexSolrQueueName;
    @Value("${org.orcid.persistence.messaging.reindex.summary}")
    private String reindexSummaryQueueName;
    @Value("${org.orcid.persistence.messaging.reindex.activity}")
    private String reindexActivitiesQueueName;
    @Value("${org.orcid.persistence.indexing.delay:5}")
    private Integer indexingDelay;
    @Value("${org.orcid.persistence.messaging.updated.v3:updateV3Record}")
    private String updateV3RecordQueueName;
    @Value("${org.orcid.persistence.messaging.reindex.v3:reindexV3Record}")
    private String reindexV3RecordQueueName;
    @Resource
    private ProfileDao profileDao;

    @Resource
    private ProfileDao profileDaoReadOnly;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource(name = "jmsMessageSender")
    private JmsMessageSender messaging;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource
    private SlackManager slackManager;
    
    private int claimReminderAfterDays = 8;
    
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
    public void processProfilesWithFailedFlagAndAddToMessageQueue() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.FAILED);
    }
    
    @Override
    public void reindexRecordsOnSolr() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.SOLR_UPDATE);
    }
    
    @Override
    public void reindexV3RecordsOnS3() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.S3_V3_REINDEX);
    }
    
    @Override
    synchronized public void processUnclaimedProfilesToFlagForIndexing() {
        LOG.info("About to process unclaimed profiles to flag for indexing");
        List<String> orcidsToFlag = Collections.<String> emptyList();
        do {
            orcidsToFlag = profileDaoReadOnly.findUnclaimedNotIndexedAfterWaitPeriod(claimWaitPeriodDays, claimWaitPeriodDays * 2, INDEXING_BATCH_SIZE, orcidsToFlag);
            LOG.info("Got batch of {} unclaimed profiles to flag for indexing", orcidsToFlag.size());
            for (String orcid : orcidsToFlag) {
                LOG.info("About to flag unclaimed profile for indexing: {}", orcid);
                profileEntityManager.updateLastModifed(orcid);
            }
        } while (!orcidsToFlag.isEmpty());
    }
    
    @Override
    synchronized public void processUnclaimedProfilesForReminder() {
        LOG.info("About to process unclaimed profiles for reminder");
        List<String> orcidsToRemind = Collections.<String> emptyList();
        do {
            orcidsToRemind = profileDaoReadOnly.findUnclaimedNeedingReminder(claimReminderAfterDays, INDEXING_BATCH_SIZE, orcidsToRemind);
            LOG.info("Got batch of {} unclaimed profiles for reminder", orcidsToRemind.size());
            for (final String orcid : orcidsToRemind) {
                processUnclaimedProfileForReminderInTransaction(orcid);
            }
        } while (!orcidsToRemind.isEmpty());
    }

    private void processProfilesWithFlagAndAddToMessageQueue(IndexingStatus status) {
        LOG.info("processing profiles with " + status.name() + " flag.");
        List<Pair<String, IndexingStatus>> orcidsForIndexing = new ArrayList<>();
        boolean connectionIssue = false;
        String solrQueue = (IndexingStatus.REINDEX.equals(status) ? reindexSolrQueueName : updateSolrQueueName);
        String v2SummaryQueue = (IndexingStatus.REINDEX.equals(status) ? reindexSummaryQueueName : updateSummaryQueueName);
        String v2ActivitiesQueue = (IndexingStatus.REINDEX.equals(status) ? reindexActivitiesQueueName : updateActivitiesQueueName);
        String v3Queue = (IndexingStatus.REINDEX.equals(status) ? reindexV3RecordQueueName : updateV3RecordQueueName);
        do {
            
            try {
                if (IndexingStatus.REINDEX.equals(status) || IndexingStatus.S3_V3_REINDEX.equals(status)) {
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
            LOG.info("processing batch of " + orcidsForIndexing.size());

            for (Pair<String, IndexingStatus> p : orcidsForIndexing) {
                String orcid = p.getLeft();

                Date last = profileDaoReadOnly.retrieveLastModifiedDate(orcid);
                LastModifiedMessage mess = new LastModifiedMessage(orcid, last);
                
                if(IndexingStatus.SOLR_UPDATE.equals(status)) {
                    connectionIssue = indexSolr(mess, solrQueue, status);
                } else if(IndexingStatus.S3_V3_REINDEX.equals(orcid)) {
                    connectionIssue = indexV3Record(mess, v3Queue, status);
                } else if(IndexingStatus.DUMP_UPDATE.equals(status)) {
                    connectionIssue = indexSummaries(mess, v2SummaryQueue, status);
                    if(!connectionIssue)
                        connectionIssue = indexActivities(mess, v2ActivitiesQueue, status);
                    if(!connectionIssue)
                        connectionIssue = indexV3Record(mess, v3Queue, status);
                } else {
                    connectionIssue = indexSolr(mess, solrQueue, status);
                    if(!connectionIssue)
                        connectionIssue = indexSummaries(mess, v2SummaryQueue, status);
                    if(!connectionIssue)
                        connectionIssue = indexActivities(mess, v2ActivitiesQueue, status);
                    if(!connectionIssue)
                        connectionIssue = indexV3Record(mess, v3Queue, status);
                }                
                
                profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);
            }
        } while (!connectionIssue && !orcidsForIndexing.isEmpty());
    }
    
    private boolean indexSolr(LastModifiedMessage mess, String solrQueue, IndexingStatus status) {        
        // Send message to solr queue
        if (!messaging.send(mess, solrQueue)) {
            LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + solrQueue);                    
            return true;
        }
        return false;
    }
    
    private boolean indexSummaries(LastModifiedMessage mess, String summaryQueue, IndexingStatus status) {
        // Send message to summary queue
        if (!messaging.send(mess, summaryQueue)) {            
            LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + summaryQueue);                    
            return true;
        }
        return false;
    }
    
    private boolean indexActivities(LastModifiedMessage mess, String activitiesQueue, IndexingStatus status) {
        // Send message to activities queue
        if (!messaging.send(mess, activitiesQueue)) {
            LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + activitiesQueue);                    
            return true;
        }
        return false;
    }
    
    private boolean indexV3Record(LastModifiedMessage mess, String queueName, IndexingStatus status) {
        // Send message to activities queue
        if (!messaging.send(mess, queueName)) {
            LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + queueName);                    
            return true;
        }
        return false;
    }
    
    private void processUnclaimedProfileForReminderInTransaction(final String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                LOG.info("About to process unclaimed profile for reminder: {}", orcid);  
                Email email = emailManagerReadOnly.findPrimaryEmail(orcid);
                if(email != null) 
                    notificationManager.sendClaimReminderEmail(orcid, claimWaitPeriodDays - claimReminderAfterDays, email.getEmail());
            }
        });
    }

}
