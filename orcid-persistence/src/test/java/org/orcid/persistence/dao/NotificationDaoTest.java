package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
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
import org.orcid.persistence.constants.SendEmailFrequency;
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
    private EmailFrequencyDao emailFrequencyDao;

    @Resource
    private ProfileDao profileDao;

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml", "/data/NotificationEntityData.xml");

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
    public void testFindRecordsWithUnsentNotificationsLegacy() {
        ProfileEntity p1 = profileDao.find("0000-0000-0000-0002");
        ProfileEntity p2 = profileDao.find("4444-4444-4444-4441");

        List<Object[]> recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotificationsLegacy();
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
    public void testFindNotificationsToSendLegacy() {
        String orcid1 = "0000-0000-0000-0004";
        ProfileEntity p1 = profileDao.find("0000-0000-0000-0004");
        Date date = new Date(p1.getCompletedDate().getTime());
        // On HSQLDB this is the max value that a float can hold without
        // throwing an exception
        Float HSQLDB_MAX_FLOAT = Float.valueOf(NumberType.MAX_INT.intValue() - 64);
        ArrayList<Long> ids = new ArrayList<Long>();
        // Setup notifications: never sent any
        ids.add(createNotifiation(orcid1, null));
        ids.add(createNotifiation(orcid1, null));
        ids.add(createNotifiation(orcid1, null));

        List<NotificationEntity> notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_IMMEDIATELY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_DAILY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_WEEKLY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_QUARTERLY, date);
        assertNotNull(notificationsToSend);
        assertEquals(3, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        // Never should return an empty list
        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, HSQLDB_MAX_FLOAT, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());

        for (Long id : ids) {
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

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_IMMEDIATELY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_DAILY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_WEEKLY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        // Quarterly should be empty since the last time we sent was a month ago
        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_QUARTERLY, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());

        // Never should return an empty list
        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, HSQLDB_MAX_FLOAT, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());

        for (Long id : ids) {
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

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_IMMEDIATELY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_DAILY, date);
        assertNotNull(notificationsToSend);
        assertEquals(2, notificationsToSend.size());
        for (NotificationEntity e : notificationsToSend) {
            assertTrue(ids.contains(e.getId()));
        }

        // Weekly should be empty since the last time we sent was 6 days ago
        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_WEEKLY, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());

        // Quarterly should be empty since the last time we sent was 6 days ago
        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, FREQUENCY_QUARTERLY, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());

        // Never should return an empty list
        notificationsToSend = notificationDao.findNotificationsToSendLegacy(new Date(), orcid1, HSQLDB_MAX_FLOAT, date);
        assertNotNull(notificationsToSend);
        assertTrue(notificationsToSend.isEmpty());

        for (Long id : ids) {
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
        if (sentDate != null) {
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
        for (int i = 0; i < 5; i++) {
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
            if (lastId == null) {
                lastId = freshFromDB.getId();
            } else {
                assertTrue(lastId < freshFromDB.getId());
                lastId = freshFromDB.getId();
            }
        }
    }

    @Test
    public void testFindNotificationsToSend() {
        String orcid = "0000-0000-0000-0003";

        Calendar c = Calendar.getInstance();
        c.set(2018, 0, 6, 0, 0);
        Date date1 = c.getTime();
        c.set(2018, 1, 9, 0, 0);
        Date date2 = c.getTime();

        c.set(2017, 11, 1, 0, 0);
        Date recordOldEnough = c.getTime();

        c.set(2018, 0, 1, 0, 0);
        Date recordNotOldEnough = c.getTime();

        // Setup email_frequency
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.NEVER);
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.NEVER);
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.NEVER);
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);

        List<NotificationEntity> results = null;

        // Test #1: All set to never
        results = notificationDao.findNotificationsToSend(date1, orcid, recordOldEnough);
        assertEquals(0, results.size());

        results = notificationDao.findNotificationsToSend(date2, orcid, recordNotOldEnough);
        assertEquals(0, results.size());

        // Test #2: Include member updated requests (INSTITUTIONAL_CONNECTION
        // and PERMISSION)
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.WEEKLY);

        // With date1 it hasn't been a week since the last time one of the
        // INSTITUTIONAL_CONNECTION or PERMISSION was sent
        results = notificationDao.findNotificationsToSend(date1, orcid, recordOldEnough);
        assertEquals(0, results.size());

        // With date2 it should fetch one INSTITUTIONAL_CONNECTION and one
        // PERMISSION
        results = notificationDao.findNotificationsToSend(date2, orcid, recordOldEnough);
        assertEquals(2, results.size());
        assertEquals(Long.valueOf(1003), results.get(0).getId());
        assertEquals("INSTITUTIONAL_CONNECTION", results.get(0).getNotificationType());
        assertEquals(Long.valueOf(1005), results.get(1).getId());
        assertEquals("PERMISSION", results.get(1).getNotificationType());

        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.NEVER);

        // Test #3: Include change requests (AMENDED)
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.WEEKLY);

        // With date1 it it hasn't been a week since the last time one of the
        // AMENDED was sent
        results = notificationDao.findNotificationsToSend(date1, orcid, recordOldEnough);
        assertEquals(0, results.size());

        // With date2 it should fetch one AMENDED
        results = notificationDao.findNotificationsToSend(date2, orcid, recordOldEnough);
        assertEquals(1, results.size());
        assertEquals(Long.valueOf(1007), results.get(0).getId());
        assertEquals("AMENDED", results.get(0).getNotificationType());

        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.NEVER);

        // Test #3: Include administrative update requests (ADMINISTRATIVE and
        // CUSTOM)
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.WEEKLY);

        // With date1 it hasn't been a week since the last time one of the
        // ADMINISTRATIVE or CUSTOM was sent
        results = notificationDao.findNotificationsToSend(date1, orcid, recordOldEnough);
        assertEquals(0, results.size());

        // With date2 it should fetch one ADMINISTRATIVE and one CUSTOM
        results = notificationDao.findNotificationsToSend(date2, orcid, recordOldEnough);
        assertEquals(2, results.size());
        assertEquals(Long.valueOf(1009), results.get(0).getId());
        assertEquals("CUSTOM", results.get(0).getNotificationType());
        assertEquals(Long.valueOf(1011), results.get(1).getId());
        assertEquals("ADMINISTRATIVE", results.get(1).getNotificationType());

        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.NEVER);

        // Test #4: Include them all
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.WEEKLY);
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.WEEKLY);
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.WEEKLY);

        // With date1 it shouldn't fetch anything
        results = notificationDao.findNotificationsToSend(date1, orcid, recordOldEnough);
        assertEquals(0, results.size());

        // With date2 it should fetch one of each
        results = notificationDao.findNotificationsToSend(date2, orcid, recordOldEnough);
        assertEquals(5, results.size());
        assertEquals(Long.valueOf(1003), results.get(0).getId());
        assertEquals("INSTITUTIONAL_CONNECTION", results.get(0).getNotificationType());
        assertEquals(Long.valueOf(1005), results.get(1).getId());
        assertEquals("PERMISSION", results.get(1).getNotificationType());
        assertEquals(Long.valueOf(1007), results.get(2).getId());
        assertEquals("AMENDED", results.get(2).getNotificationType());
        assertEquals(Long.valueOf(1009), results.get(3).getId());
        assertEquals("CUSTOM", results.get(3).getNotificationType());
        assertEquals(Long.valueOf(1011), results.get(4).getId());
        assertEquals("ADMINISTRATIVE", results.get(4).getNotificationType());

        // Test #6: Include them all but quarterly
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.QUARTERLY);
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.QUARTERLY);
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.QUARTERLY);
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);

        // With date1 or date2 it shouldn't fetch anything
        results = notificationDao.findNotificationsToSend(date1, orcid, recordOldEnough);
        assertEquals(0, results.size());

        results = notificationDao.findNotificationsToSend(date2, orcid, recordOldEnough);
        assertEquals(0, results.size());
    }

    @Test
    public void testServiceAnnouncementNotifications() {
        String orcid = "0000-0000-0000-0003";
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);

        // Test #1: Only service announcements
        List<NotificationEntity> results = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, results.size());
        assertEquals(Long.valueOf(1001), results.get(0).getId());

        // Test #2: Enable Tips
        emailFrequencyDao.updateSendQuarterlyTips(orcid, true);
        results = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, results.size());
        assertEquals(Long.valueOf(1001), results.get(0).getId());
        assertEquals("SERVICE_ANNOUNCEMENT", results.get(0).getNotificationType());
        
        // Test #3: Disable Tips again
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
        results = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, results.size());
        assertEquals(Long.valueOf(1001), results.get(0).getId());
    }

    @Test
    public void testTipsNotifications() {
        String orcid = "0000-0000-0000-0003";
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);

        // Test #1: Tips disabled
        List<NotificationEntity> results = notificationDao.findUnsentTips(100);
        assertEquals(0, results.size());

        // Test #2: Enable Tips
        emailFrequencyDao.updateSendQuarterlyTips(orcid, true);
        results = notificationDao.findUnsentTips(100);
        assertEquals(1, results.size());
        assertEquals(Long.valueOf(1013), results.get(0).getId());
        assertEquals("TIP", results.get(0).getNotificationType());

        // Test #3: Disable Tips again
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
        results = notificationDao.findUnsentTips(100);
        assertEquals(0, results.size());
    }
    
    @Test
    public void archiveOffsetNotificationsTest() throws Exception {
        String orcid = "0000-0000-0000-0003";
        Integer unread = notificationDao.getUnreadCount(orcid);
        assertEquals(Integer.valueOf(14), unread);
        Integer archived = notificationDao.archiveOffsetNotifications(3);
        // It will archive 15 notifications:
        // 11 from 0000-0000-0000-0003
        // 3 from 4444-4444-4444-4441
        // 1 from 
        assertEquals(Integer.valueOf(15), archived);
        List<NotificationEntity> notifications = notificationDao.findByOrcid(orcid, false, 0, 100);
        assertEquals(3, notifications.size());
        boolean found1 = false, found2 = false, found3 = false;
        for (NotificationEntity n : notifications) {
            if(n.getId().equals(Long.valueOf(1001))) {
                found1 = true;
            } else if(n.getId().equals(Long.valueOf(1003))) {
                found2 = true;
            } else if(n.getId().equals(Long.valueOf(1005))) {
                found3 = true;
            } else {
                fail("Invalid put code found: " + n.getId());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }  
    
    @Test
    public void findNotificationsToDeleteByOffsetTest() {
        List<Object[]> toDelete = notificationDao.findNotificationsToDeleteByOffset(3, 10);
        assertEquals(15, toDelete.size());
        boolean found1 = false, found2 = false, found3 = false;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        
        for(Object [] o : toDelete) {
            BigInteger id = (BigInteger) o[0];
            String orcid = (String) o[1];
            
            if("0000-0000-0000-0003".equals(orcid)) {
                found1 = true;
                count1++;
            } else if("4444-4444-4444-4441".equals(orcid)) {
                found2 = true;
                count2++;
            } else if("4444-4444-4444-4442".equals(orcid)) {
                found3 = true;
                count3++;
            } else {
                fail("Invalid orcid found: " + orcid + " " + id.toString());
            }
        }
        
        assertTrue(found1);
        assertEquals(11, count1);
        assertTrue(found2);
        assertEquals(3, count2);
        assertTrue(found3);
        assertEquals(1, count3);        
    }
}
