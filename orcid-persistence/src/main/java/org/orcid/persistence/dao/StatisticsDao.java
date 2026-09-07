package org.orcid.persistence.dao;

import org.orcid.statistics.jpa.entities.StatisticValuesEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface StatisticsDao {

    long calculateLiveIds();
    
    @Transactional(propagation = Propagation.REQUIRED)
    Long createKey();

    long getLatestLiveIds();
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void persist(StatisticValuesEntity e);

}
