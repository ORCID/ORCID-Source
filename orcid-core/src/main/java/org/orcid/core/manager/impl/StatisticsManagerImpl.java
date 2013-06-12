/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.persistence.dao.StatisticsDao;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.springframework.transaction.annotation.Transactional;

public class StatisticsManagerImpl implements StatisticsManager {

    @Resource 
    StatisticsDao statisticsDao;
    
    /**
     * TODO
     * */
    @Override
    @Transactional
    public StatisticKeyEntity createKey() {
        return statisticsDao.createKey();
    }

    /**
     * TODO
     * */
    @Override
    @Transactional
    public StatisticValuesEntity saveStatistic(StatisticKeyEntity id, String name, long value) {
        StatisticValuesEntity statisticEntity = new StatisticValuesEntity(id, name, value);
        return statisticsDao.saveStatistic(statisticEntity);
    }

    /**
     * TODO
     * */
    @Override
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name) {        
        return statisticsDao.getStatistic(id.getId(), name);
    }
    
    /**
     * TODO
     * */
    @Override
    public List<StatisticValuesEntity> getLatestStatistics(){
        StatisticKeyEntity latestKey = statisticsDao.getLatestKey();
        return statisticsDao.getStatistic(latestKey.getId());
    }
}
