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
package org.orcid.listener.persistence.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.listener.persistence.dao.ActivitiesStatusDao;
import org.orcid.listener.persistence.entities.ActivitiesStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class ActivitiesStatusManagerTest {
    @Resource
    private ActivitiesStatusManager activitiesStatusManager;

    @Resource
    private ActivitiesStatusDao activitiesStatusDao;

    @Test
    public void markAsSentTest() {
        String orcid = "0000-0000-0000-0001";
        assertFalse(activitiesStatusDao.exists(orcid));
        activitiesStatusManager.markAsSent(orcid, ActivityType.EDUCATIONS);
        assertTrue(activitiesStatusDao.exists(orcid));
        ActivitiesStatusEntity entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(0), entity.getEducationsStatus());
    }

    @Test
    public void markAsFailedTest() {
        String orcid = "0000-0000-0000-0002";
        assertFalse(activitiesStatusDao.exists(orcid));
        activitiesStatusManager.markAsFailed(orcid, ActivityType.EDUCATIONS);
        assertTrue(activitiesStatusDao.exists(orcid));
        ActivitiesStatusEntity entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(1), entity.getEducationsStatus());
        activitiesStatusManager.markAsFailed(orcid, ActivityType.EDUCATIONS);
        entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(2), entity.getEducationsStatus());
        activitiesStatusManager.markAsFailed(orcid, ActivityType.EDUCATIONS);
        entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(3), entity.getEducationsStatus());
    }

    @Test
    public void setSentThenFailedThenSentAgainTest() {
        String orcid = "0000-0000-0000-0003";
        // First mark it as sent
        assertFalse(activitiesStatusDao.exists(orcid));
        activitiesStatusManager.markAsSent(orcid, ActivityType.EDUCATIONS);
        assertTrue(activitiesStatusDao.exists(orcid));
        ActivitiesStatusEntity entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(0), entity.getEducationsStatus());

        // Then make it fail 3 times
        activitiesStatusManager.markAsFailed(orcid, ActivityType.EDUCATIONS);
        activitiesStatusManager.markAsFailed(orcid, ActivityType.EDUCATIONS);
        activitiesStatusManager.markAsFailed(orcid, ActivityType.EDUCATIONS);
        // Verify it's status is 3
        entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(3), entity.getEducationsStatus());

        // Then mark it as sent
        activitiesStatusManager.markAsSent(orcid, ActivityType.EDUCATIONS);
        // Verify it's status was cleared
        entity = activitiesStatusDao.get(orcid);
        assertNotNull(entity);
        assertEquals(orcid, entity.getId());
        assertEquals(Integer.valueOf(0), entity.getEducationsStatus());
    }
}
