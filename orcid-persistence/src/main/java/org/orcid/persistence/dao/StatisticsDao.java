package org.orcid.persistence.dao;

public interface StatisticsDao {

    long calculateLiveIds();
    
    Long createKey();

    long getLatestLiveIds();

}
