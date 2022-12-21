package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.orcid.persistence.dao.StatisticsDao;
import org.orcid.statistics.jpa.entities.StatisticKeyEntity;

public class StatisticsDaoImpl implements StatisticsDao {

    protected EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public long calculateLiveIds() {
        Query query = entityManager.createNativeQuery("select count(*) from profile where profile_deactivation_date is null and record_locked = false");
        BigInteger numberOfLiveIds = (BigInteger) query.getSingleResult();
        return numberOfLiveIds.longValue();
    }
    
    @Override
    public StatisticKeyEntity createKey() {
        StatisticKeyEntity key = new StatisticKeyEntity();
        key.setGenerationDate(new Date());
        entityManager.persist(key);
        return key;
    }
    
    @Override
    public long getLatestLiveIds() {
        Query query = entityManager.createNativeQuery("select statistic_value from statistic_values where key_id = (SELECT id FROM statistic_key WHERE id IN (SELECT max(key_id) FROM statistic_values) ORDER BY generation_date DESC LIMIT 1) and statistic_name = 'liveIds'");
        BigInteger numberOfLiveIds = (BigInteger) query.getSingleResult();
        return numberOfLiveIds.longValue();
    }
    
}
