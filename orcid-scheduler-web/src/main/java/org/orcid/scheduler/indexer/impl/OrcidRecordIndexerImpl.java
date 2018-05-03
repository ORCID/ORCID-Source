package org.orcid.scheduler.indexer.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.scheduler.indexer.OrcidRecordIndexer;
import org.orcid.scheduler.messaging.JmsMessageSender;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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
    
    @Resource
    private ProfileDao profileDao;

    @Resource
    private ProfileDao profileDaoReadOnly;
    
    @Resource(name = "jmsMessageSender")
    private JmsMessageSender messaging;
    
    @Override
    public void processProfilesWithPendingFlagAndAddToMessageQueue() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.PENDING, updateSolrQueueName, updateSummaryQueueName, updateActivitiesQueueName);
    }

    @Override
    public void processProfilesWithReindexFlagAndAddToMessageQueue() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.REINDEX, reindexSolrQueueName, reindexSummaryQueueName, reindexActivitiesQueueName);
    }

    @Override
    public void processProfilesWithFailedFlagAndAddToMessageQueue() {
        this.processProfilesWithFlagAndAddToMessageQueue(IndexingStatus.FAILED, updateSolrQueueName, updateSummaryQueueName, updateActivitiesQueueName);
    }

    private void processProfilesWithFlagAndAddToMessageQueue(IndexingStatus status, String solrQueue, String summaryQueue, String activitiesQueue) {
        LOG.info("processing profiles with " + status.name() + " flag.");
        List<Pair<String, IndexingStatus>> orcidsForIndexing = new ArrayList<>();
        List<IndexingStatus> indexingStatuses = new ArrayList<IndexingStatus>(1);
        indexingStatuses.add(status);
        boolean connectionIssue = false;
        do {
            orcidsForIndexing = profileDaoReadOnly.findOrcidsByIndexingStatus(indexingStatuses, INDEXING_BATCH_SIZE, new ArrayList<String>());
            LOG.info("processing batch of " + orcidsForIndexing.size());
            for (Pair<String, IndexingStatus> p : orcidsForIndexing) {
                String orcid = p.getLeft();
                Date last = profileDaoReadOnly.retrieveLastModifiedDate(orcid);
                LastModifiedMessage mess = new LastModifiedMessage(orcid, last);                
                // Send message to solr queue
                if (!messaging.send(mess, solrQueue)) {
                    connectionIssue = true;
                    LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + solrQueue);
                    continue;
                }

                // Send message to summary queue
                if (!messaging.send(mess, summaryQueue)) {
                    connectionIssue = true;
                    LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + summaryQueue);
                    continue;
                }
                // Send message to activities queue
                if (!messaging.send(mess, activitiesQueue)) {
                    connectionIssue = true;
                    LOG.warn("ABORTED processing profiles with " + status.name() + " flag. sending to " + activitiesQueue);
                    continue;
                }

                profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);

            }
        } while (!connectionIssue && !orcidsForIndexing.isEmpty());        
    }
}
