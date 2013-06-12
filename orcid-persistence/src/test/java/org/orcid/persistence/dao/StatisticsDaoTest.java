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

        assertEquals(5, s1);
        assertEquals(1, s2);
        assertEquals(6, s3);
        assertEquals(2, s4);
        assertEquals(0, s5);

        StatisticKeyEntity key = statisticsDao.createKey();

        StatisticValuesEntity os1 = new StatisticValuesEntity(key, "s1", s1);
        StatisticValuesEntity os2 = new StatisticValuesEntity(key, "s2", s2);
        StatisticValuesEntity os3 = new StatisticValuesEntity(key, "s3", s3);
        StatisticValuesEntity os4 = new StatisticValuesEntity(key, "s4", s4);
        StatisticValuesEntity os5 = new StatisticValuesEntity(key, "s5", s5);

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
    }
}
