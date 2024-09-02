package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbNotificationAdapterTest {

    @Resource(name = "jpaJaxbNotificationAdapterV3")
    private JpaJaxbNotificationAdapter jpaJaxbNotificationAdapter;

    @Test
    public void testToNotificationCustomEntity() {
        NotificationCustom notification = new NotificationCustom();
        notification.setNotificationType(NotificationType.CUSTOM);
        notification.setSubject("Test subject");

        NotificationEntity notificationEntity = jpaJaxbNotificationAdapter.toNotificationEntity(notification);

        assertNotNull(notificationEntity);
        assertEquals(org.orcid.jaxb.model.notification_v2.NotificationType.CUSTOM.name(), notificationEntity.getNotificationType());
        assertEquals("Test subject", notification.getSubject());
    }

    @Test
    public void testCustomEntityToNotification() throws IllegalAccessException {
        NotificationCustomEntity notificationEntity = new NotificationCustomEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(notificationEntity, DateUtils.convertToDate("2014-01-01T09:17:56"));
        notificationEntity.setId(123L);
        notificationEntity.setNotificationType(org.orcid.jaxb.model.notification_v2.NotificationType.CUSTOM.name());
        notificationEntity.setSubject("Test subject");        
        notificationEntity.setReadDate(DateUtils.convertToDate("2014-03-04T17:43:06"));
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        DateFieldsOnBaseEntityUtils.setDateFields(notificationEntity, date);


        Notification notification = jpaJaxbNotificationAdapter.toNotification(notificationEntity);

        assertNotNull(notification);
        assertTrue(notification instanceof NotificationCustom);
        NotificationCustom notificationCustom = (NotificationCustom) notification;
        assertNotNull(notificationCustom.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(notificationCustom.getCreatedDate()));
        assertEquals(NotificationType.CUSTOM, notification.getNotificationType());
        assertEquals("Test subject", notificationCustom.getSubject());
        assertTrue(notification.getCreatedDate().toXMLFormat().startsWith("2015-06-05T10:15:20.000"));
        assertTrue(notification.getReadDate().toXMLFormat().startsWith("2014-03-04T17:43:06.000"));
    }

    @Test
    public void testToNotificationPermissionEntity() {
        NotificationPermission notification = new NotificationPermission();
        notification.setNotificationType(NotificationType.PERMISSION);
        notification.setCreatedDate(DateUtils.convertToXMLGregorianCalendar(new Date()));
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
        extId.setUrl(new Url("https://example.com/1234/abc123"));
        extId.setRelationship(Relationship.SELF);

        NotificationEntity notificationEntity = jpaJaxbNotificationAdapter.toNotificationEntity(notification);

        assertTrue(notificationEntity instanceof NotificationAddItemsEntity);
        NotificationAddItemsEntity addActivitiesEntity = (NotificationAddItemsEntity) notificationEntity;
        
        assertNotNull(notificationEntity);
        assertNull(notificationEntity.getDateCreated());
        assertNull(notificationEntity.getLastModified());
        assertEquals(org.orcid.jaxb.model.notification_v2.NotificationType.PERMISSION.name(), notificationEntity.getNotificationType());
        assertEquals(authorizationUrlString, addActivitiesEntity.getAuthorizationUrl());
        assertEquals(notification.getNotificationIntro(), notificationEntity.getNotificationIntro());
        assertEquals(notification.getNotificationSubject(),notificationEntity.getNotificationSubject());
        // Source
        assertNull(notificationEntity.getSourceId());        
        assertNull(notificationEntity.getClientSourceId());        
        assertNull(notificationEntity.getElementSourceId());

        Set<NotificationItemEntity> activityEntities = addActivitiesEntity.getNotificationItems();
        assertNotNull(activityEntities);
        assertEquals(1, activityEntities.size());
        NotificationItemEntity activityEntity = activityEntities.iterator().next();
        assertEquals(org.orcid.jaxb.model.notification.permission_v2.ItemType.WORK.name(), activityEntity.getItemType());
        assertEquals("Latest Research Article", activityEntity.getItemName());
        assertEquals("DOI", activityEntity.getExternalIdType());
        assertEquals("1234/abc123", activityEntity.getExternalIdValue());
        assertEquals("https://example.com/1234/abc123", activityEntity.getExternalIdUrl());
        assertEquals("SELF", activityEntity.getExternalIdRelationship());
    }

    @Test
    public void testToNotificationAmendedEntity() {
        NotificationAmended notification = new NotificationAmended();
        notification.setNotificationType(NotificationType.AMENDED);
        notification.setCreatedDate(DateUtils.convertToXMLGregorianCalendar(new Date()));
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
        assertNull(notificationEntity.getDateCreated());
        assertNull(notificationEntity.getLastModified());
        assertTrue(notificationEntity instanceof NotificationAmendedEntity);
        NotificationAmendedEntity notificationAmendedEntity = (NotificationAmendedEntity) notificationEntity;

        assertNotNull(notificationEntity);
        assertEquals(org.orcid.jaxb.model.notification_v2.NotificationType.AMENDED.name(), notificationEntity.getNotificationType());   
        
        // Source
        assertNull(notificationAmendedEntity.getSourceId());        
        assertNull(notificationAmendedEntity.getClientSourceId());        
        assertNull(notificationAmendedEntity.getElementSourceId());        
    }
}
