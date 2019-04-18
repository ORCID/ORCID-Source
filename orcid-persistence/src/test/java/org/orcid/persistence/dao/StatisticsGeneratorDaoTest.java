package org.orcid.persistence.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class StatisticsGeneratorDaoTest extends DBUnitTest {

    @Resource
    StatisticsGeneratorDao statisticsGeneratorDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/WorksEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WorksEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(statisticsGeneratorDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testStatistics() {
        assertEquals(13, statisticsGeneratorDao.getAccountsWithVerifiedEmails());
        assertEquals(6, statisticsGeneratorDao.getAccountsWithWorks());
        assertEquals(20, statisticsGeneratorDao.getLiveIds());
        assertEquals(17, statisticsGeneratorDao.getNumberOfWorks());
        //TODO: Restore this test when we know how to make it work on HSQLDB
        //assertEquals(0, statisticsGeneratorDao.getNumberOfUniqueDOIs());
    }
}
