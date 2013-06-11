package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.persistence.dao.StatisticsDao;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.springframework.transaction.annotation.Transactional;

public class StatisticsManagerImpl implements StatisticsManager {

    @Resource 
    StatisticsDao statisticsDao;
    
    @Override
    @Transactional
    public StatisticKeyEntity createHistory() {
        return statisticsDao.createHistory();
    }

    @Override
    @Transactional
    public StatisticValuesEntity saveStatistic(StatisticKeyEntity id, String name, float value) {
        StatisticValuesEntity statisticEntity = new StatisticValuesEntity(id, name, value);
        return statisticsDao.saveStatistic(statisticEntity);
    }

    @Override
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name) {        
        return statisticsDao.getStatistic(id.getId(), name);
    }

}
