package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;

public interface StatisticsManager {
    public StatisticKeyEntity createHistory();
    public StatisticValuesEntity saveStatistic(StatisticKeyEntity id, String name, long value);
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name);
    public List<StatisticValuesEntity> getLatestStatistics();
}
