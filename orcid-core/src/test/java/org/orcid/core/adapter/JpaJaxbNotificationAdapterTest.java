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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc2.Source;
import org.orcid.jaxb.model.common_rc2.SourceClientId;
import org.orcid.jaxb.model.notification.amended_rc2.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_rc2.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_rc2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.ItemType;
import org.orcid.jaxb.model.notification.permission_rc2.Items;
import org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission;
import org.orcid.jaxb.model.notification_rc2.Notification;
import org.orcid.jaxb.model.notification_rc2.NotificationType;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbNotificationAdapterTest {

    @Resource
    private JpaJaxbNotificationAdapter jpaJaxbNotificationAdapter;

    @Test
    public void testToNotificationCustomEntity() {
        NotificationCustom notification = new NotificationCustom();
        notification.setNotificationType(NotificationType.CUSTOM);
        notification.setSubject("Test subject");

        NotificationEntity notificationEntity = jpaJaxbNotificationAdapter.toNotificationEntity(notification);

        assertNotNull(notificationEntity);
        assertEquals(NotificationType.CUSTOM, notificationEntity.getNotificationType());
        assertEquals("Test subject", notification.getSubject());
    }

    @Test
    public void testCustomEntityToNotification() {
        NotificationCustomEntity notificationEntity = new NotificationCustomEntity();
        notificationEntity.setId(123L);
        notificationEntity.setNotificationType(NotificationType.CUSTOM);
        notificationEntity.setSubject("Test subject");
        notificationEntity.setDateCreated(DateUtils.convertToDate("2014-01-01T09:17:56"));
        notificationEntity.setReadDate(DateUtils.convertToDate("2014-03-04T17:43:06"));

        Notification notification = jpaJaxbNotificationAdapter.toNotification(notificationEntity);

        assertNotNull(notification);
        assertTrue(notification instanceof NotificationCustom);
        NotificationCustom notificationCustom = (NotificationCustom) notification;
        assertEquals(NotificationType.CUSTOM, notification.getNotificationType());
        assertEquals("Test subject", notificationCustom.getSubject());
        assertEquals("2014-01-01T09:17:56.000Z", notification.getCreatedDate().toXMLFormat());
        assertEquals("2014-03-04T17:43:06.000Z", notification.getReadDate().toXMLFormat());
    }

    @Test
    public void testToNotificationPermissionEntity() {
        NotificationPermission notification = new NotificationPermission();
        notification.setNotificationType(NotificationType.PERMISSION);
        String authorizationUrlString = "https://orcid.org/oauth/authorize?client_id=APP-U4UKCNSSIM1OCVQY&amp;response_type=code&amp;scope=/orcid-works/create&amp;redirect_uri=http://somethirdparty.com";
        AuthorizationUrl url = new AuthorizationUrl();
        notification.setAuthorizationUrl(url);
        notification.setNotificationIntro("This is the intro");
        notification.setNotificationSubject("This is the subject");
        Source source = new Source();
        notification.setSource(source);
        SourceClientId clientId = new SourceClientId();
        source.setSourceClientId(clientId);
        clientId.setPath("APP-5555-5555-5555-5555");
        url.setUri(authorizationUrlString);
        Items activities = new Items();
        notification.setItems(activities);
        Item activity = new Item();
        activities.getItems().add(activity);
        activity.setItemType(ItemType.WORK);
        activity.setItemName("Latest Research Article");
        ExternalID extId = new ExternalID();
        activity.setExternalIdentifier(extId);
        extId.setType("doi");
        extId.setValue("1234/abc123");

        NotificationEntity notificationEntity = jpaJaxbNotificationAdapter.toNotificationEntity(notification);

        assertTrue(notificationEntity instanceof NotificationAddItemsEntity);
        NotificationAddItemsEntity addActivitiesEntity = (NotificationAddItemsEntity) notificationEntity;
        
        assertNotNull(notificationEntity);
        assertEquals(NotificationType.PERMISSION, notificationEntity.getNotificationType());
        assertEquals(authorizationUrlString, addActivitiesEntity.getAuthorizationUrl());
        assertEquals(notification.getNotificationIntro(), notificationEntity.getNotificationIntro());
        assertEquals(notification.getNotificationSubject(),notificationEntity.getNotificationSubject());
        assertNotNull(addActivitiesEntity.getSource());
        assertEquals("APP-5555-5555-5555-5555", addActivitiesEntity.getSource().getSourceId());
        Set<NotificationItemEntity> activityEntities = addActivitiesEntity.getNotificationItems();
        assertNotNull(activityEntities);
        assertEquals(1, activityEntities.size());
        NotificationItemEntity activityEntity = activityEntities.iterator().next();
        assertEquals(ItemType.WORK, activityEntity.getItemType());
        assertEquals("Latest Research Article", activityEntity.getItemName());
        assertEquals("DOI", activityEntity.getExternalIdType());
        assertEquals("1234/abc123", activityEntity.getExternalIdValue());
        
    }

    @Test
    public void testToNotificationAmendedEntity() {
        NotificationAmended notification = new NotificationAmended();
        notification.setNotificationType(NotificationType.AMENDED);
        Source source = new Source();
        notification.setSource(source);
        SourceClientId clientId = new SourceClientId();
        source.setSourceClientId(clientId);
        clientId.setPath("APP-5555-5555-5555-5555");
        Items activities = new Items();
        notification.setItems(activities);
        Item activity = new Item();
        activities.getItems().add(activity);
        activity.setItemType(ItemType.WORK);
        activity.setItemName("Latest Research Article");
        ExternalID extId = new ExternalID();
        activity.setExternalIdentifier(extId);
        extId.setType("doi");
        extId.setValue("1234/abc123");

        NotificationEntity notificationEntity = jpaJaxbNotificationAdapter.toNotificationEntity(notification);

        assertTrue(notificationEntity instanceof NotificationAmendedEntity);
        NotificationAmendedEntity notificationAmendedEntity = (NotificationAmendedEntity) notificationEntity;

        assertNotNull(notificationEntity);
        assertEquals(NotificationType.AMENDED, notificationEntity.getNotificationType());
        assertNotNull(notificationAmendedEntity.getSource());
        assertEquals("APP-5555-5555-5555-5555", notificationAmendedEntity.getSource().getSourceId());
    }

}
