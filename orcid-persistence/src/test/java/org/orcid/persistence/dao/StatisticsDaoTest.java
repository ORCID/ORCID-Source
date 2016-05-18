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
package org.orcid.persistence.dao;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class StatisticsDaoTest extends DBUnitTest {

    @Resource
    StatisticsDao statisticsDao;

    @Resource
    StatisticsGeneratorDao statisticsGeneratorDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/WorksEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(statisticsDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testStatistics() {
        StatisticKeyEntity key = statisticsDao.createKey();
        List<StatisticValuesEntity> entities = new ArrayList<StatisticValuesEntity>();

        StatisticValuesEntity os1 = new StatisticValuesEntity(key, "s1", 11);
        StatisticValuesEntity os2 = new StatisticValuesEntity(key, "s2", 3);
        StatisticValuesEntity os3 = new StatisticValuesEntity(key, "s3", 12);
        StatisticValuesEntity os4 = new StatisticValuesEntity(key, "s4", 7);
        StatisticValuesEntity os5 = new StatisticValuesEntity(key, "s5", 0);
        StatisticValuesEntity os6 = new StatisticValuesEntity(key, "s6", 0);
        StatisticValuesEntity os7 = new StatisticValuesEntity(null, "s7", 0);
        entities.add(os1);
        entities.add(os2);
        entities.add(os3);
        entities.add(os4);
        entities.add(os5);

        statisticsDao.saveStatistics(entities);

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
