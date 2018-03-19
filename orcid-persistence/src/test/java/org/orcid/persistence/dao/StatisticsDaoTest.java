package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.statistics.dao.StatisticsDao;
import org.orcid.statistics.jpa.entities.StatisticKeyEntity;
import org.orcid.statistics.jpa.entities.StatisticValuesEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:statistics-persistence-context.xml" })
public class StatisticsDaoTest {

    @Resource
    StatisticsDao statisticsDao;

    @Test    
    @Transactional
    public void testStatistics() {
        StatisticKeyEntity key = statisticsDao.createKey();        
        StatisticKeyEntity latestKey = statisticsDao.createKey();
        
        StatisticValuesEntity os1 = new StatisticValuesEntity(latestKey, "s1", 11);
        StatisticValuesEntity os2 = new StatisticValuesEntity(latestKey, "s2", 3);
        StatisticValuesEntity os3 = new StatisticValuesEntity(latestKey, "s3", 12);
        StatisticValuesEntity os4 = new StatisticValuesEntity(latestKey, "s4", 7);
        StatisticValuesEntity os5 = new StatisticValuesEntity(latestKey, "s5", 0);
        StatisticValuesEntity os6 = new StatisticValuesEntity(key, "s6", 0);
        StatisticValuesEntity os7 = new StatisticValuesEntity(key, "s7", 0);        

        statisticsDao.persist(os1);
        statisticsDao.persist(os2);
        statisticsDao.persist(os3);
        statisticsDao.persist(os4);
        statisticsDao.persist(os5);
        statisticsDao.persist(os6);
        statisticsDao.persist(os7);

        StatisticKeyEntity latestKeyFromDB = statisticsDao.getLatestKey();

        assertEquals(latestKey, latestKeyFromDB);

        List<StatisticValuesEntity> statistics = statisticsDao.getStatistic(latestKeyFromDB.getId());

        assertNotNull(statistics);
        assertEquals(statistics.size(), 5);
        assertTrue(statistics.contains(os1));
        assertTrue(statistics.contains(os2));
        assertTrue(statistics.contains(os3));
        assertTrue(statistics.contains(os4));
        assertTrue(statistics.contains(os5));
        assertFalse(statistics.contains(os6));
        assertFalse(statistics.contains(os7));
    }
}
