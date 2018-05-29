package org.orcid.core.manager.read_only.impl;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.read_only.StatisticsManagerReadOnly;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.orcid.statistics.dao.StatisticsDao;
import org.orcid.statistics.jpa.entities.StatisticKeyEntity;
import org.orcid.statistics.jpa.entities.StatisticValuesEntity;

public class StatisticsManagerReadOnlyImpl implements StatisticsManagerReadOnly {

    @Resource(name = "statisticsDaoReadOnly")
    StatisticsDao statisticsDaoReadOnly;

        /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     */
    @Override
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name) {
        return statisticsDaoReadOnly.getStatistic(id.getId(), name);
    }

    /**
     * Get the list of the latest statistics
     * 
     * @return a list that contains the latest set of statistics
     */
    @Override
    public List<StatisticValuesEntity> getLatestStatistics() {
        StatisticKeyEntity latestKey = statisticsDaoReadOnly.getLatestKey();
        if (latestKey != null)
            return statisticsDaoReadOnly.getStatistic(latestKey.getId());
        return null;
    }

    /**
     * Get the the latest statistics value for the statistics name parameter
     * 
     * @param statisticName
     * @return the latest statistics value for the statistics name parameter
     */
    public StatisticValuesEntity getLatestStatistics(String statisticName) {
        StatisticKeyEntity latestKey = statisticsDaoReadOnly.getLatestKey();
        if (latestKey != null)
            return statisticsDaoReadOnly.getStatistic(latestKey.getId(), statisticName);
        return null;
    }

    /**
     * Get all entries with a given name;
     * 
     * @param statisticName
     * @return all statistics values for the statistics name parameter
     */    
    public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum statisticName) {
        List<StatisticValuesEntity> list = statisticsDaoReadOnly.getStatistic(statisticName.value());
        if (list == null)
            return null;
               
        // convert to model
        StatisticsTimeline timeline = new StatisticsTimeline();
        timeline.setStatisticName(statisticName.value());
        Map<Long, Long> map = new TreeMap<Long, Long>();        
        Map<Long, Date> generationDateMap = new HashMap<Long, Date>();
        
        for (StatisticValuesEntity entry : list) {
            if(!generationDateMap.containsKey(entry.getKey().getId())) {
                StatisticKeyEntity key = statisticsDaoReadOnly.getKey(entry.getKey().getId());
                Long time = key.getGenerationDate().getTime();
                map.put(time, entry.getStatisticValue());
                generationDateMap.put(key.getId(), key.getGenerationDate());
            } else {
                Date date = generationDateMap.get(entry.getKey().getId());
                map.put(date.getTime(), entry.getStatisticValue());
            }                       
        }
        timeline.setTimeline(map);
        return timeline;
    }

    /**
     * Get the list of the latest statistics as a domain model
     * 
     * @return a list that contains the latest set of statistics
     */
    @Override
    public StatisticsSummary getLatestStatisticsModel() {
        StatisticKeyEntity latestKey = statisticsDaoReadOnly.getLatestKey();
        if (latestKey == null)
            return null;

        List<StatisticValuesEntity> list = statisticsDaoReadOnly.getStatistic(latestKey.getId());
        if (list == null || list.size() == 0)
            return null;

        // convert to model
        StatisticsSummary summary = new StatisticsSummary();
        Map<String, Long> map = new TreeMap<String, Long>();
        for (StatisticValuesEntity entry : list) {
            map.put(entry.getStatisticName(), entry.getStatisticValue());
        }
        summary.setStatistics(map);
        summary.setDate(latestKey.getGenerationDate());
        return summary;
    }

    /**
     * Get the the latest live ids statistics
     * 
     * @param locale
     * @return the latest statistics live ids statistics
     */
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
     */
    public StatisticKeyEntity getLatestKey() {
        return statisticsDaoReadOnly.getLatestKey();
    }

}
