package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;

public interface StatisticsManager {
    public StatisticKeyEntity createHistory();
    public StatisticValuesEntity saveStatistic(StatisticKeyEntity id, String name, float value);
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name);
}
