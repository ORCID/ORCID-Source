package org.orcid.scheduler.indexer;

public interface OrcidRecordIndexer {
    void processProfilesWithReindexFlagAndAddToMessageQueue();

    void processProfilesWithFailedFlagAndAddToMessageQueue();

    void processProfilesWithPendingFlagAndAddToMessageQueue();
    
    void processProfilesWithForceIndexingFlagAndAddToMessageQueue();
    
    void processUnclaimedProfilesToFlagForIndexing();
    
    void processUnclaimedProfilesForReminder();

    void reindexRecordsOnSolr();
    
    void reindexV3RecordsOnS3();
}
