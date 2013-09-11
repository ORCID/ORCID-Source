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

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class StatisticsGeneratorDaoTest extends DBUnitTest {

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
        assertNotNull(statisticsGeneratorDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testStatistics() {
        assertEquals(6, statisticsGeneratorDao.getAccountsWithVerifiedEmails());
        assertEquals(1, statisticsGeneratorDao.getAccountsWithWorks());
        assertEquals(7, statisticsGeneratorDao.getLiveIds());
        assertEquals(2, statisticsGeneratorDao.getNumberOfWorks());
        assertEquals(0, statisticsGeneratorDao.getNumberOfWorksWithDOIs());
    }
}
