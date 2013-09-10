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
package org.orcid.persistence.dao;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class StatisticsDaoTest extends DBUnitTest {

    @Resource
    StatisticsDao statisticsDao;

    @Resource
    StatisticsGeneratorDao statisticsGeneratorDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(
                Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml"),
                null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(
                Arrays.asList("/data/ProfileWorksEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"),
                null);
    }

    @Before
    public void beforeRunning() {
        assertNotNull(statisticsDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testStatistics() {
        long s1 = statisticsGeneratorDao.getAccountsWithVerifiedEmails();
        long s2 = statisticsGeneratorDao.getAccountsWithWorks();
        long s3 = statisticsGeneratorDao.getLiveIds();
        long s4 = statisticsGeneratorDao.getNumberOfWorks();
        long s5 = statisticsGeneratorDao.getNumberOfWorksWithDOIs();
        long s6 = 0;
        long s7 = 0;

        assertEquals(5, s1);
        assertEquals(2, s2);
        assertEquals(6, s3);
        assertEquals(3, s4);
        assertEquals(0, s5);

        StatisticKeyEntity key = statisticsDao.createKey();

        StatisticValuesEntity os1 = new StatisticValuesEntity(key, "s1", s1);
        StatisticValuesEntity os2 = new StatisticValuesEntity(key, "s2", s2);
        StatisticValuesEntity os3 = new StatisticValuesEntity(key, "s3", s3);
        StatisticValuesEntity os4 = new StatisticValuesEntity(key, "s4", s4);
        StatisticValuesEntity os5 = new StatisticValuesEntity(key, "s5", s5);
        StatisticValuesEntity os6 = new StatisticValuesEntity(key, "s6", s6);
        StatisticValuesEntity os7 = new StatisticValuesEntity(null, "s7", s7);
        
        

        statisticsDao.saveStatistic(os1);
        statisticsDao.saveStatistic(os2);
        statisticsDao.saveStatistic(os3);
        statisticsDao.saveStatistic(os4);
        statisticsDao.saveStatistic(os5);

        StatisticKeyEntity latestKey = statisticsDao.getLatestKey();

        assertEquals(key, latestKey);

        List<StatisticValuesEntity> statistics = statisticsDao.getStatistic(latestKey.getId());

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
