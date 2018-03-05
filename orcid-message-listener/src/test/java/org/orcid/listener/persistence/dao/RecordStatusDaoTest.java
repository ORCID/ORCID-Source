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
package org.orcid.listener.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.listener.persistence.util.Constants;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class RecordStatusDaoTest {
    @Resource
    private RecordStatusDao recordStatusDao;

    @Test
    @Transactional
    public void createTest() {
        String orcid = "0000-0000-0000-0000";
        assertFalse(recordStatusDao.exists(orcid));
        recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        assertTrue(recordStatusDao.exists(orcid));
    }

    @Test
    @Transactional
    public void existsTest() {
        recordStatusDao.create("0000-0000-0001", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        recordStatusDao.create("0000-0000-0002", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        recordStatusDao.create("0000-0000-0003", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        recordStatusDao.create("0000-0000-0004", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        assertTrue(recordStatusDao.exists("0000-0000-0001"));
        assertTrue(recordStatusDao.exists("0000-0000-0002"));
        assertTrue(recordStatusDao.exists("0000-0000-0003"));
        assertTrue(recordStatusDao.exists("0000-0000-0004"));
        assertFalse(recordStatusDao.exists("0000-0000-0005"));
        assertFalse(recordStatusDao.exists("0000-0000-0006"));
        assertFalse(recordStatusDao.exists("0000-0000-0007"));
    }

    @Test
    @Transactional
    public void getTest() {
        String orcid = "0000-0000-1000";
        recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, 100);
        RecordStatusEntity entity = recordStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(100), entity.getDumpStatus12Api());
        assertEquals(Integer.valueOf(0), entity.getDumpStatus20Api());
        assertEquals(Integer.valueOf(0), entity.getSolrStatus20Api());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertNotNull(entity.getLastIndexedDump12Api());
        assertNull(entity.getLastIndexedDump20ActivitiesApi());
        assertNull(entity.getLastIndexedDump20Api());
        assertNull(entity.getLastIndexedSolr20Api());
    }

    @Test
    @Transactional
    public void updateStatus1Test() throws InterruptedException {
        String orcid = "0000-0000-1001";
        recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        assertTrue(recordStatusDao.exists(orcid));
        assertTrue(recordStatusDao.updateFailCount(orcid, AvailableBroker.DUMP_STATUS_1_2_API));
        assertTrue(recordStatusDao.updateFailCount(orcid, AvailableBroker.DUMP_STATUS_2_0_API));
        assertFalse(recordStatusDao.updateFailCount("0000-0000-0000-2000", AvailableBroker.DUMP_STATUS_1_2_API));
        assertFalse(recordStatusDao.updateFailCount("0000-0000-0000-2000", AvailableBroker.DUMP_STATUS_2_0_API));
    }

    @Test
    @Transactional
    public void getFailedElementsTest() {
        recordStatusDao.create("0000-0000-0001-0000", AvailableBroker.DUMP_STATUS_1_2_API, 1);
        recordStatusDao.create("0000-0000-0001-0001", AvailableBroker.DUMP_STATUS_2_0_API, Constants.OK);
        recordStatusDao.create("0000-0000-0001-0002", AvailableBroker.SOLR, Constants.OK);
        recordStatusDao.create("0000-0000-0001-0003", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        recordStatusDao.create("0000-0000-0001-0004", AvailableBroker.DUMP_STATUS_2_0_API, 1);
        recordStatusDao.create("0000-0000-0001-0005", AvailableBroker.SOLR, Constants.OK);
        recordStatusDao.create("0000-0000-0001-0006", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);
        recordStatusDao.create("0000-0000-0001-0007", AvailableBroker.DUMP_STATUS_2_0_API, Constants.OK);
        recordStatusDao.create("0000-0000-0001-0008", AvailableBroker.SOLR, 1);
        recordStatusDao.create("0000-0000-0001-0009", AvailableBroker.DUMP_STATUS_1_2_API, Constants.OK);

        List<RecordStatusEntity> list = recordStatusDao.getFailedElements(100);
        assertEquals(3, list.size());
        RecordStatusEntity e1 = list.get(0);
        assertEquals("0000-0000-0001-0000", e1.getId());
        assertEquals(Integer.valueOf(1), e1.getDumpStatus12Api());
        assertEquals(Integer.valueOf(0), e1.getDumpStatus20Api());
        assertEquals(Integer.valueOf(0), e1.getSolrStatus20Api());

        RecordStatusEntity e2 = list.get(1);
        assertEquals("0000-0000-0001-0004", e2.getId());
        assertEquals(Integer.valueOf(0), e2.getDumpStatus12Api());
        assertEquals(Integer.valueOf(1), e2.getDumpStatus20Api());
        assertEquals(Integer.valueOf(0), e2.getSolrStatus20Api());

        RecordStatusEntity e3 = list.get(2);
        assertEquals("0000-0000-0001-0008", e3.getId());
        assertEquals(Integer.valueOf(0), e3.getDumpStatus12Api());
        assertEquals(Integer.valueOf(0), e3.getDumpStatus20Api());
        assertEquals(Integer.valueOf(1), e3.getSolrStatus20Api());
        
        list = recordStatusDao.getFailedElements(2);
        assertEquals(2, list.size());
    }    
}
