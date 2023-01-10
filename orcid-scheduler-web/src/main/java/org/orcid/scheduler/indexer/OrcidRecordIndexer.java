package org.orcid.scheduler.indexer;

public interface OrcidRecordIndexer {
    void processProfilesWithReindexFlagAndAddToMessageQueue();

    void processProfilesWithPendingFlagAndAddToMessageQueue();        
    
    void reindexRecordsOnSolr();
    
    void reindexRecordsOnS3();
}
