package org.orcid.scheduler.indexer;

public interface OrgDisambiguatedIndexer {
    void processOrgsForIndexing();
    
    void markOrgsForIndexingAsGroup();

    void processOrgsWithIncorrectPopularity();
}
