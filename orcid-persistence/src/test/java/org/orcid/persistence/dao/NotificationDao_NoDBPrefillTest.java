package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAdministrativeEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
@Transactional
public class NotificationDao_NoDBPrefillTest extends DBUnitTest {

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private EmailFrequencyDao emailFrequencyDao;

    @Resource
    private ProfileDao profileDao;

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml");

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
    public void testFindRecordsWithUnsentNotifications() {
        String orcid = "0000-0000-0000-0003";
        ProfileEntity profile = new ProfileEntity(orcid);
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.NEVER);
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.NEVER);
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.NEVER);
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);

        List<Object[]> recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertTrue(recordsWithNotificationsToSend.isEmpty());

        // Service announcement should always be sent
        NotificationEntity n = new NotificationServiceAnnouncementEntity();
        n.setProfile(profile);
        n.setNotificationType("SERVICE_ANNOUNCEMENT");
        notificationDao.persist(n);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        notificationDao.remove(n.getId());

        // Test tips, tips are disabled, so, should found nothing
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
        n = new NotificationTipEntity();
        n.setProfile(profile);
        n.setNotificationType("TIP");
        notificationDao.persist(n);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(0, recordsWithNotificationsToSend.size());

        // Tips enabled, so, it should find this one
        emailFrequencyDao.updateSendQuarterlyTips(orcid, true);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        notificationDao.remove(n.getId());

        // Test administrative change notifications
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.NEVER);

        n = new NotificationAdministrativeEntity();
        n.setProfile(profile);
        n.setNotificationType("ADMINISTRATIVE");
        notificationDao.persist(n);

        // Disabled, so, should find nothing
        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(0, recordsWithNotificationsToSend.size());

        // Quarterly, so, it should find them
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.QUARTERLY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        // Immediately, so, it should find them
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.IMMEDIATELY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        notificationDao.remove(n.getId());

        // Test send change notifications
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.NEVER);

        n = new NotificationAmendedEntity();
        n.setProfile(profile);
        n.setNotificationType("AMENDED");
        notificationDao.persist(n);

        // Disabled, so, should find nothing
        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(0, recordsWithNotificationsToSend.size());

        // Quarterly, so, it should find them
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.QUARTERLY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        // Immediately, so, it should find them
        emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.IMMEDIATELY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        notificationDao.remove(n.getId());

        // Test send member updates
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.NEVER);

        n = new NotificationInstitutionalConnectionEntity();
        n.setProfile(profile);
        n.setNotificationType("INSTITUTIONAL_CONNECTION");
        notificationDao.persist(n);

        // Disabled, so, should find nothing
        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(0, recordsWithNotificationsToSend.size());

        // Quarterly, so, it should find them
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.QUARTERLY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        // Immediately, so, it should find them
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.IMMEDIATELY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        notificationDao.remove(n.getId());

        // Again with PERMISSION notification
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.NEVER);

        n = new NotificationAddItemsEntity();
        n.setProfile(profile);
        n.setNotificationType("PERMISSION");
        notificationDao.persist(n);

        // Disabled, so, should find nothing
        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(0, recordsWithNotificationsToSend.size());

        // Quarterly, so, it should find them
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.QUARTERLY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        // Immediately, so, it should find them
        emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.IMMEDIATELY);

        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(1, recordsWithNotificationsToSend.size());

        notificationDao.remove(n.getId());

        // All removed, there should be nothing
        recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertEquals(0, recordsWithNotificationsToSend.size());
    }
}
