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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.notification.NotificationType;
import org.orcid.persistence.jpa.entities.NotificationActivityEntity;
import org.orcid.persistence.jpa.entities.NotificationAddActivitiesEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.utils.DateUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
@Transactional
public class NotificationDaoTest extends DBUnitTest {

    @Resource
    private NotificationDao notificationDao;

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml", "/data/NotificationEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testFindCustomNotification() {
        NotificationEntity notification = notificationDao.find(1L);
        assertNotNull(notification);
        assertTrue(notification instanceof NotificationCustomEntity);
        assertEquals(NotificationType.CUSTOM, notification.getNotificationType());
    }
    
    @Test
    public void testFindAddActivitiesNotification() {
        NotificationEntity notification = notificationDao.find(5L);
        assertNotNull(notification);
        assertTrue(notification instanceof NotificationAddActivitiesEntity);
        assertEquals(NotificationType.ADD_ACTIVITIES, notification.getNotificationType());
        NotificationAddActivitiesEntity addActsNotification = (NotificationAddActivitiesEntity) notification;
        Set<NotificationActivityEntity> acts = addActsNotification.getNotificationActivities();
        assertNotNull(acts);
        assertEquals(2, acts.size());
    }

    @Test
    @Rollback(true)
    public void testFindOrcidsWithNotificationsToSend() {
        List<String> orcids = notificationDao.findOrcidsWithNotificationsToSend();
        assertNotNull(orcids);
        assertEquals(1, orcids.size());
        assertEquals("4444-4444-4444-4441", orcids.get(0));
    }

    @Test
    @Rollback(true)
    public void testFindOrcidsWithNotificationsToSendWhenTooSoon() {
        List<String> orcids = notificationDao.findOrcidsWithNotificationsToSend(DateUtils.convertToDate("2014-07-16T12:00:00"));
        assertNotNull(orcids);
        assertEquals(0, orcids.size());
    }

}
