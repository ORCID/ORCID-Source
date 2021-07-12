package org.orcid.scheduler.indexer;

public interface OrcidRecordIndexer {
    void processProfilesWithReindexFlagAndAddToMessageQueue();

    void processProfilesWithFailedFlagAndAddToMessageQueue();

    void processProfilesWithPendingFlagAndAddToMessageQueue();
    
    void processUnindexableRecords();
    
    void reindexRecordsOnSolr();
    
    void reindexV3RecordsOnS3();
}
