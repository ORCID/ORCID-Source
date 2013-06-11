package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;

public interface StatisticsDao {
    public StatisticKeyEntity createHistory();
    public StatisticKeyEntity getLatestKey();
    public StatisticValuesEntity saveStatistic(StatisticValuesEntity statistic);
    public List<StatisticValuesEntity> getStatistic(long id);
    public StatisticValuesEntity getStatistic(long id, String name);    
}
