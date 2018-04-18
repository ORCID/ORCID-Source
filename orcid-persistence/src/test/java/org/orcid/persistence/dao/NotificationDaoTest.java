package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hsqldb.types.NumberType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
@Transactional
public class NotificationDaoTest extends DBUnitTest {
    
    private static final float FREQUENCY_IMMEDIATELY = 0.0f;
    
    private static final float FREQUENCY_DAILY = 1.0f;
    
    private static final float FREQUENCY_WEEKLY = 7.0f;
    
    private static final float FREQUENCY_QUARTERLY = 91.3105f;
    
    private static final String NOTIFICATION_TYPE_AMENDED = "AMENDED";
    
    private static final String AMENDED_SECTION_UNKNOWN = "UNKNOWN";

    @Resource
    private NotificationDao notificationDao;
    
    @Resource
    private ProfileDao profileDao;

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
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
        assertEquals("CUSTOM", notification.getNotificationType());
    }
    
    @Test
    public void testFindPermissionNotification() {
        NotificationEntity notification = notificationDao.find(5L);
        assertNotNull(notification);
        assertTrue(notification instanceof NotificationAddItemsEntity);
        assertEquals("PERMISSION", notification.getNotificationType());
        NotificationAddItemsEntity addActsNotification = (NotificationAddItemsEntity) notification;
        Set<NotificationItemEntity> acts = addActsNotification.getNotificationItems();
        assertNotNull(acts);
        assertEquals(2, acts.size());
    }

    @Test    
    public void testFindRecordsWithUnsentNotifications() {
        ProfileEntity p1 = profileDao.find("0000-0000-0000-0002");
        ProfileEntity p2 = profileDao.find("4444-4444-4444-4441");
        
        List<Object[]> recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(2, recordsWithNotificationsToSend.size());
        Object[] e0 = recordsWithNotificationsToSend.get(0);
        Object[] e1 = recordsWithNotificationsToSend.get(1);        
        
        assertEquals("0000-0000-0000-0002", e0[0]);
        assertEquals("0.0", String.valueOf(e0[1]));
        Timestamp e0TS = (Timestamp) e0[2];
        assertEquals(p1.getCompletedDate().getTime(), e0TS.getTime());
                
        assertEquals("4444-4444-4444-4441", e1[0]);        
        assertEquals("7.0", String.valueOf(e1[1]));
        Timestamp e1TS = (Timestamp) e1[2];
        assertEquals(p2.getCompletedDate().getTime(), e1TS.getTime());
    }

    @Test
    public void testFindNotificationsToSend() {   
        String orcid1 = "0000-0000-0000-0004";
        ProfileEntity p1 = profileDao.find("0000-0000-0000-0004");
        Date date = new Date(p1.getCompletedDate().getTime());
        // On HSQLDB this is the max value that a float can hold without throwing an exception 
        Float HSQLDB_MAX_FLOAT = Float.valueOf(NumberType.MAX_INT.intValue() - 64);
        ArrayList<Long> ids = new ArrayList<Long>();
        // Setup notifications: never sent any
        ids.add(createNotifiation(orcid1, null));
        ids.add(createNotifiation(orcid1, null));
        ids.add(createNotifiation(orcid1, null));        
        
        List<NotificationEntity> notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_IMMEDIATELY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_DAILY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_WEEKLY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_QUARTERLY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        // Never should return an empty list
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, HSQLDB_MAX_FLOAT, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty()); 
        
        for(Long id: ids) {
            notificationDao.remove(id);
        }
        
        // Setup notifications: last sent a month ago
        ids.clear();
        
        // Setup notifications
        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.MONTH, -1);
        ids.add(createNotifiation(orcid1, calendar.getTime()));
        ids.add(createNotifiation(orcid1, null));
        ids.add(createNotifiation(orcid1, null));
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_IMMEDIATELY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_DAILY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_WEEKLY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        // Quarterly should be empty since the last time we sent was a month ago
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_QUARTERLY, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());
        
        // Never should return an empty list
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, HSQLDB_MAX_FLOAT, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty()); 
        
        for(Long id: ids) {
            notificationDao.remove(id);
        }
        
        // Setup notifications: last sent 6 days ago
        ids.clear();
        
        // Setup notifications
        calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        ids.add(createNotifiation(orcid1, calendar.getTime()));
        ids.add(createNotifiation(orcid1, null));
        ids.add(createNotifiation(orcid1, null));
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_IMMEDIATELY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1,FREQUENCY_DAILY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for(NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }
        
        // Weekly should be empty since the last time we sent was 6 days ago
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_WEEKLY, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());
        
        // Quarterly should be empty since the last time we sent was 6 days ago
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, FREQUENCY_QUARTERLY, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());
        
        // Never should return an empty list
        notificationsToSend = notificationDao.findNotificationsToSend(new Date(), orcid1, HSQLDB_MAX_FLOAT, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty()); 
        
        for(Long id: ids) {
            notificationDao.remove(id);
        }
        
    }
    
    private Long createNotifiation(String orcid, Date sentDate) {
        NotificationEntity entity = new NotificationAddItemsEntity();
        entity.setDateCreated(new Date());
        entity.setNotificationIntro("intro");
        entity.setNotificationSubject("subject");
        entity.setNotificationType(NOTIFICATION_TYPE_AMENDED);
        entity.setProfile(new ProfileEntity(orcid));
        if(sentDate != null){
            entity.setSentDate(sentDate);
        }
        notificationDao.persist(entity);
        notificationDao.flush();
        return entity.getId();
    }
    
    @Test
    public void testFindPermissionByOrcidAndClient() {
    	List<NotificationEntity> entities = notificationDao.findPermissionsByOrcidAndClient("4444-4444-4444-4442", "4444-4444-4444-4445", 0, 10);
    	assertNotNull(entities);
    	assertEquals(2, entities.size());
    	
    	entities = notificationDao.findPermissionsByOrcidAndClient("4444-4444-4444-4441", "4444-4444-4444-4445", 0, 10);
    	assertNotNull(entities);
    	assertEquals(3, entities.size());
    	
     	entities = notificationDao.findPermissionsByOrcidAndClient("4444-4444-4444-4441", "4444-4444-4444-4441", 0, 10);
    	assertNotNull(entities);
    	assertTrue(entities.isEmpty());
    	
    	entities = notificationDao.findPermissionsByOrcidAndClient("4444-4444-4444-4442", "4444-4444-4444-4441", 0, 10);
     	assertNotNull(entities);
    	assertEquals(1, entities.size());
    	
    }
    
    @Test
    public void testFindLatestByOrcid() {
        NotificationEntity entity = notificationDao.findLatestByOrcid("0000-0000-0000-0004");
        assertNull(entity);
        Long lastId = null;
        for(int i = 0; i < 5; i++) {
            Date now = new Date();
            NotificationAmendedEntity newEntity = new NotificationAmendedEntity();
            newEntity.setAmendedSection(AMENDED_SECTION_UNKNOWN);
            newEntity.setClientSourceId("APP-6666666666666666");
            newEntity.setDateCreated(now);
            newEntity.setLastModified(now);
            newEntity.setNotificationIntro("Intro");
            newEntity.setNotificationSubject("Subject");
            newEntity.setNotificationType(NOTIFICATION_TYPE_AMENDED);
            newEntity.setProfile(new ProfileEntity("0000-0000-0000-0004"));
            newEntity.setSendable(true);
            notificationDao.persist(newEntity);
            
            NotificationEntity freshFromDB = notificationDao.findLatestByOrcid("0000-0000-0000-0004");
            assertNotNull(freshFromDB);
            if(lastId == null) {
                lastId = freshFromDB.getId();
            } else {
                assertTrue(lastId < freshFromDB.getId());
                lastId = freshFromDB.getId();
            }
        }                
    }
}
