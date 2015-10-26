/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.orcid.persistence.dao.StatisticsDao;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.springframework.context.MessageSource;
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
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name) {
        return statisticsDao.getStatistic(id.getId(), name);
    }

    /**
     * Get the list of the latest statistics
     * 
     * @return a list that contains the latest set of statistics
     * */
    @Override
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
    public StatisticValuesEntity getLatestStatistics(String statisticName){
        StatisticKeyEntity latestKey = statisticsDao.getLatestKey();
        if(latestKey != null)
            return statisticsDao.getStatistic(latestKey.getId(), statisticName);
        return null;
    }
    
    /**
     * Get all entries with a given name;
     * @param statisticName
     * @return all statistics values for the statistics name parameter
     * */
    public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum statisticName){
        List<StatisticValuesEntity> list = statisticsDao.getStatistic(statisticName.value());
        if (list == null)
            return null;
        
        //convert to model
        StatisticsTimeline timeline = new StatisticsTimeline();
        timeline.setStatisticName(statisticName.value());
        Map<Date,Long> map = new TreeMap<Date,Long>();
        for (StatisticValuesEntity entry: list){
            map.put(entry.getKey().getGenerationDate(), entry.getStatisticValue());
        }
        timeline.setTimeline(map);
        return timeline;        
    }
    
    /**
     * Get the list of the latest statistics as a domain model
     * 
     * @return a list that contains the latest set of statistics
     * */
    @Override
    public StatisticsSummary getLatestStatisticsModel() {
        StatisticKeyEntity latestKey = statisticsDao.getLatestKey();
        if(latestKey == null)
            return null;

        List<StatisticValuesEntity> list =  statisticsDao.getStatistic(latestKey.getId());
        if (list == null || list.size()==0)
            return null;
        
        //convert to model
        StatisticsSummary summary = new StatisticsSummary();
        Map<String,Long> map = new TreeMap<String,Long>();
        for (StatisticValuesEntity entry: list){
            map.put(entry.getStatisticName(), entry.getStatisticValue());
        }
        summary.setStatistics(map);
        summary.setDate(list.get(0).getKey().getGenerationDate());
        return summary;
    }

    /**
     * Get the the latest live ids statistics 
     * @param locale
     * @return the latest statistics live ids statistics
     * */
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
