package org.orcid.persistence.dao;

import org.orcid.statistics.jpa.entities.StatisticValuesEntity;

public interface StatisticsDao {

    long calculateLiveIds();
    
    Long createKey();

    long getLatestLiveIds();
    
    public void persist(StatisticValuesEntity e);

}
