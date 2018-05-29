package org.orcid.scheduler.indexer;

public interface OrcidRecordIndexer {
    void processProfilesWithReindexFlagAndAddToMessageQueue();

    void processProfilesWithFailedFlagAndAddToMessageQueue();

    void processProfilesWithPendingFlagAndAddToMessageQueue();

}
