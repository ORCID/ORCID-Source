package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;

public interface StatisticsDao {
    public StatisticKeyEntity createHistory();
    public StatisticValuesEntity saveStatistic(StatisticValuesEntity statistic);
    public StatisticValuesEntity getStatistic(long id, String name);
}
