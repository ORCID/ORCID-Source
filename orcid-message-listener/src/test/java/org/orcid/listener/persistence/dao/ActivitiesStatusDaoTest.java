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
import org.orcid.listener.persistence.entities.ActivitiesStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.persistence.util.Constants;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class ActivitiesStatusDaoTest {
    @Resource
    private ActivitiesStatusDao activitiesStatusDao;

    @Test
    @Transactional
    public void createTest() {
        String orcid = "0000-0000-0000-0000";
        assertFalse(activitiesStatusDao.exists(orcid));
        activitiesStatusDao.create(orcid, ActivityType.EDUCATIONS, Constants.OK);
        assertTrue(activitiesStatusDao.exists(orcid));
    }

    @Test
    @Transactional
    public void existsTest() {
        activitiesStatusDao.create("0000-0000-0001", ActivityType.EDUCATIONS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0002", ActivityType.EDUCATIONS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0003", ActivityType.EDUCATIONS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0004", ActivityType.EDUCATIONS, Constants.OK);
        assertTrue(activitiesStatusDao.exists("0000-0000-0001"));
        assertTrue(activitiesStatusDao.exists("0000-0000-0002"));
        assertTrue(activitiesStatusDao.exists("0000-0000-0003"));
        assertTrue(activitiesStatusDao.exists("0000-0000-0004"));
        assertFalse(activitiesStatusDao.exists("0000-0000-0005"));
        assertFalse(activitiesStatusDao.exists("0000-0000-0006"));
        assertFalse(activitiesStatusDao.exists("0000-0000-0007"));
    }

    @Test
    @Transactional
    public void getTest() {
        String orcid = "0000-0000-1000";
        activitiesStatusDao.create(orcid, ActivityType.EDUCATIONS, 100);
        ActivitiesStatusEntity entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(100), entity.getEducationsStatus());
        assertEquals(Integer.valueOf(0), entity.getEmploymentsStatus());
        assertEquals(Integer.valueOf(0), entity.getFundingsStatus());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertNotNull(entity.getEducationsLastIndexed());
        assertNull(entity.getEmploymentsLastIndexed());
        assertNull(entity.getFundingsLastIndexed());
    }

    @Test
    @Transactional
    public void updateStatus1Test() throws InterruptedException {
        String orcid = "0000-0000-1001";
        activitiesStatusDao.create(orcid, ActivityType.EDUCATIONS, Constants.OK);
        assertTrue(activitiesStatusDao.exists(orcid));
        assertTrue(activitiesStatusDao.updateFailCount(orcid, ActivityType.EDUCATIONS));
        assertTrue(activitiesStatusDao.updateFailCount(orcid, ActivityType.EMPLOYMENTS));
        assertFalse(activitiesStatusDao.updateFailCount("0000-0000-0000-2000", ActivityType.EDUCATIONS));
        assertFalse(activitiesStatusDao.updateFailCount("0000-0000-0000-2000", ActivityType.EMPLOYMENTS));
    }

    @Test
    @Transactional
    public void getFailedElementsTest() {
        activitiesStatusDao.create("0000-0000-0001-0000", ActivityType.EDUCATIONS, 1);
        activitiesStatusDao.create("0000-0000-0001-0001", ActivityType.EMPLOYMENTS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0001-0002", ActivityType.FUNDINGS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0001-0003", ActivityType.EDUCATIONS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0001-0004", ActivityType.EMPLOYMENTS, 1);
        activitiesStatusDao.create("0000-0000-0001-0005", ActivityType.FUNDINGS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0001-0006", ActivityType.EDUCATIONS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0001-0007", ActivityType.EMPLOYMENTS, Constants.OK);
        activitiesStatusDao.create("0000-0000-0001-0008", ActivityType.FUNDINGS, 1);
        activitiesStatusDao.create("0000-0000-0001-0009", ActivityType.EDUCATIONS, Constants.OK);

        List<ActivitiesStatusEntity> list = activitiesStatusDao.getFailedElements(100);
        assertEquals(3, list.size());
        ActivitiesStatusEntity e1 = list.get(0);
        assertEquals("0000-0000-0001-0000", e1.getId());
        assertEquals(Integer.valueOf(1), e1.getEducationsStatus());
        assertEquals(Integer.valueOf(0), e1.getEmploymentsStatus());
        assertEquals(Integer.valueOf(0), e1.getFundingsStatus());

        ActivitiesStatusEntity e2 = list.get(1);
        assertEquals("0000-0000-0001-0004", e2.getId());
        assertEquals(Integer.valueOf(0), e2.getEducationsStatus());
        assertEquals(Integer.valueOf(1), e2.getEmploymentsStatus());
        assertEquals(Integer.valueOf(0), e2.getFundingsStatus());

        ActivitiesStatusEntity e3 = list.get(2);
        assertEquals("0000-0000-0001-0008", e3.getId());
        assertEquals(Integer.valueOf(0), e3.getEducationsStatus());
        assertEquals(Integer.valueOf(0), e3.getEmploymentsStatus());
        assertEquals(Integer.valueOf(1), e3.getFundingsStatus());

        list = activitiesStatusDao.getFailedElements(2);
        assertEquals(2, list.size());
    }
}
