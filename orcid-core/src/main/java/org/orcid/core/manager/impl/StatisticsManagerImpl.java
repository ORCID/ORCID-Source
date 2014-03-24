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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.persistence.dao.StatisticsDao;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.springframework.context.MessageSource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class StatisticsManagerImpl implements StatisticsManager {

    @Resource
    MessageSource messageSource;

    @Resource
    StatisticsDao statisticsDao;

    /**
     * Creates a new statistics key
     * 
     * @return the statistic key object
     * */
    @Override
    @Transactional
    public StatisticKeyEntity createKey() {
        return statisticsDao.createKey();
    }

    /**
     * Save an statistics record on database
     * 
     * @param id
     * @param name
     *            the name of the statistic
     * @param value
     *            the statistic value
     * @return the statistic value object
     * */
    @Override
    @Transactional
    public StatisticValuesEntity saveStatistic(StatisticKeyEntity id, String name, long value) {
        StatisticValuesEntity statisticEntity = new StatisticValuesEntity(id, name, value);
        return statisticsDao.saveStatistic(statisticEntity);
    }

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     * */
    @Override
    @Cacheable("statistics")
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name) {
        return statisticsDao.getStatistic(id.getId(), name);
    }

    /**
     * Get the list of the latest statistics
     * 
     * @return a list that contains the latest set of statistics
     * */
    @Override
    @Cacheable("statistics")
    public List<StatisticValuesEntity> getLatestStatistics() {
        StatisticKeyEntity latestKey = statisticsDao.getLatestKey();
        if(latestKey != null)
            return statisticsDao.getStatistic(latestKey.getId());
        return null;
    }
    
    /**
     * Get the the latest statistics value for the statistics name parameter
     * @param statisticName
     * @return the latest statistics value for the statistics name parameter
     * */
    @Cacheable("statistics")
    public StatisticValuesEntity getLatestStatistics(String statisticName){
        StatisticKeyEntity latestKey = statisticsDao.getLatestKey();
        if(latestKey != null)
            return statisticsDao.getStatistic(latestKey.getId(), statisticName);
        return null;
    }

    /**
     * Get the the latest live ids statistics 
     * @param locale
     * @return the latest statistics live ids statistics
     * */
    @Cacheable("statistics")
    public String getLiveIds(Locale locale) {
        StatisticValuesEntity entity = getLatestStatistics(StatisticsEnum.KEY_LIVE_IDS.value());        
        long amount = entity == null ? 0 : entity.getStatisticValue();        
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(amount);
    }

    
    /**
     * Get the last statistics key
     * 
     * @return the last statistics key
     * */
    public StatisticKeyEntity getLatestKey(){
        return statisticsDao.getLatestKey();
    }
}
