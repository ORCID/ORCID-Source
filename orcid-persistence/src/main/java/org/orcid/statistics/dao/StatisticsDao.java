package org.orcid.statistics.dao;

import java.util.List;

import org.orcid.statistics.jpa.entities.StatisticKeyEntity;
import org.orcid.statistics.jpa.entities.StatisticValuesEntity;

public interface StatisticsDao {
    /**
     * Creates a new statistics key
     * 
     * @return the statistic key object
     * */
    public StatisticKeyEntity createKey();

    /**
     * Get the latest statistics key
     * 
     * @return the latest statistics key
     * */
    public StatisticKeyEntity getLatestKey();
        
    /**
     * Get an statistics key
     * 
     * @return the statistics key associated with the given id
     * */
    public StatisticKeyEntity getKey(Long id);

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
    public StatisticValuesEntity persist(StatisticValuesEntity element);

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @return the Statistic value object associated with the id
     * */
    public List<StatisticValuesEntity> getStatistic(long id);

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     * */
    public StatisticValuesEntity getStatistic(long id, String name);

    /** 
     * Get all statistics by statistic name
     * 
     * @param name
     * @return a list in descending date order
     */
    List<StatisticValuesEntity> getStatistic(String name);
}
