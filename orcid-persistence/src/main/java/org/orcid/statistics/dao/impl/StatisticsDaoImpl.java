package org.orcid.statistics.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.orcid.statistics.dao.StatisticsDao;
import org.orcid.statistics.jpa.entities.StatisticKeyEntity;
import org.orcid.statistics.jpa.entities.StatisticValuesEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class StatisticsDaoImpl implements StatisticsDao {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsDaoImpl.class);
    
    protected EntityManager statisticsEntityManager;

    public void setStatisticsEntityManager(EntityManager statisticsEntityManager) {
        this.statisticsEntityManager = statisticsEntityManager;
    }
    
    /**
     * Creates a new statistics key
     * 
     * @return the statistic key object
     */
    @Override
    @Transactional
    public StatisticKeyEntity createKey() {
        StatisticKeyEntity key = new StatisticKeyEntity();
        key.setGenerationDate(new Date());
        statisticsEntityManager.persist(key);
        return key;
    }

    /**
     * Get the latest statistics key
     * 
     * @return the latest statistics key
     */
    @Override    
    public StatisticKeyEntity getLatestKey() {
        try {
            return (StatisticKeyEntity) statisticsEntityManager
                    .createNativeQuery("SELECT * FROM statistic_key WHERE id IN (SELECT max(key_id) FROM statistic_values) ORDER BY generation_date DESC LIMIT 1;",
                            StatisticKeyEntity.class)
                    .getSingleResult();
        } catch (NoResultException nre) {
            LOG.warn("Couldnt find any statistics key, the cron job needs to run for the first time.");
        }
        return null;
    }

    /**
     * Get an statistics key
     * 
     * @return the statistics key associated with the given id
     * */
    @Cacheable(value = "statistics-key", key = "#id")
    public StatisticKeyEntity getKey(Long id) {
        try {
            return (StatisticKeyEntity) statisticsEntityManager
                    .createNativeQuery("SELECT * FROM statistic_key WHERE id=:id",
                            StatisticKeyEntity.class).setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            LOG.warn("Couldnt find any statistics key, the cron job needs to run for the first time.");
        }
        return null;
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
     */
    @Override
    @Transactional
    public StatisticValuesEntity persist(StatisticValuesEntity statistic) {
        statisticsEntityManager.persist(statistic);
        return statistic;
    }

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @return the Statistic value object associated with the id
     */
    @Override
    public List<StatisticValuesEntity> getStatistic(long id) {
        TypedQuery<StatisticValuesEntity> query = statisticsEntityManager.createQuery("FROM StatisticValuesEntity WHERE key.id = :id", StatisticValuesEntity.class);
        query.setParameter("id", id);
        List<StatisticValuesEntity> results = null;
        try {
            results = query.getResultList();
        } catch (NoResultException nre) {
            LOG.warn("Couldnt find any statistics for the statistic key {}, the cron job might be running.", id);
        }

        return results;
    }

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     */
    @Override
    public StatisticValuesEntity getStatistic(long id, String name) {
        TypedQuery<StatisticValuesEntity> query = statisticsEntityManager.createQuery("FROM StatisticValuesEntity WHERE key.id = :id AND statisticName = :name",
                StatisticValuesEntity.class);
        query.setParameter("id", id);
        query.setParameter("name", name);
        List<StatisticValuesEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     */
    @Override
    public List<StatisticValuesEntity> getStatistic(String name) {
        TypedQuery<StatisticValuesEntity> query = statisticsEntityManager.createQuery("FROM StatisticValuesEntity WHERE statisticName = :name", StatisticValuesEntity.class);
        query.setParameter("name", name);
        List<StatisticValuesEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }
}
