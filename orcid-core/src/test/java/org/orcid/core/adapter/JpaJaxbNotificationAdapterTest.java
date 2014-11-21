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
package org.orcid.core.adapter;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.notification.generic.Notification;
import org.orcid.jaxb.model.notification.generic.NotificationType;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbNotificationAdapterTest {

    @Resource
    private JpaJaxbNotificationAdapter jpaJaxbNotificationAdapter;

    @Test
    public void testToNotificationEntity() {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationType.RECORD_UPDATED_BY_MEMBER);
        notification.setSubject("Test subject");

        NotificationEntity notificationEntity = jpaJaxbNotificationAdapter.toNotificationEntity(notification);

        assertNotNull(notificationEntity);
        assertEquals(NotificationType.RECORD_UPDATED_BY_MEMBER, notificationEntity.getNotificationType());
        assertEquals("Test subject", notification.getSubject());
    }

    @Test
    public void testToNotification() {
        NotificationCustomEntity notificationEntity = new NotificationCustomEntity();
        notificationEntity.setId(123L);
        notificationEntity.setNotificationType(NotificationType.RECORD_UPDATED_BY_MEMBER);
        notificationEntity.setSubject("Test subject");
        notificationEntity.setDateCreated(DateUtils.convertToDate("2014-01-01T09:17:56"));
        notificationEntity.setReadDate(DateUtils.convertToDate("2014-03-04T17:43:06"));

        Notification notification = jpaJaxbNotificationAdapter.toNotification(notificationEntity);

        assertNotNull(notification);
        assertEquals(NotificationType.RECORD_UPDATED_BY_MEMBER, notification.getNotificationType());
        assertEquals("Test subject", notification.getSubject());
        assertEquals("2014-01-01T09:17:56.000Z", notification.getCreatedDate().toXMLFormat());
        assertEquals("2014-03-04T17:43:06.000Z", notification.getReadDate().toXMLFormat());
    }

}
