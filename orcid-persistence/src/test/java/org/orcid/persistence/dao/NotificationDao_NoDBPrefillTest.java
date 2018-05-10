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

        List<Object[]> recordsWithNotificationsToSend = notificationDao.findRecordsWithUnsentNotifications();
        assertTrue(recordsWithNotificationsToSend.isEmpty());

        // Test administrative change notifications
        emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.NEVER);

        NotificationEntity n = new NotificationAdministrativeEntity();
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
    
    @Test
    public void testServiceAnnouncementNotifications() {
        String orcid = "0000-0000-0000-0003";
        ProfileEntity profile = new ProfileEntity(orcid);
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);     

        List<NotificationEntity> recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertTrue(recordsWithNotificationsToSend.isEmpty());

        // Add one Service Announcement and one Tip
        NotificationEntity sa = new NotificationServiceAnnouncementEntity();
        sa.setProfile(profile);
        sa.setNotificationType("SERVICE_ANNOUNCEMENT");
        sa.setSendable(true);
        notificationDao.persist(sa);
        
        NotificationEntity tip = new NotificationTipEntity();
        tip.setProfile(profile);
        tip.setNotificationType("TIP");
        tip.setSendable(true);
        notificationDao.persist(tip);
        
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, recordsWithNotificationsToSend.size());
        assertEquals(sa.getId(), recordsWithNotificationsToSend.get(0).getId());
    
        // Enable tips
        emailFrequencyDao.updateSendQuarterlyTips(orcid, true);
    
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, recordsWithNotificationsToSend.size());
        assertEquals(sa.getId(), recordsWithNotificationsToSend.get(0).getId());
        
        // Disable tips again
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, recordsWithNotificationsToSend.size());
        assertEquals(sa.getId(), recordsWithNotificationsToSend.get(0).getId());    
    
        // Mark a SERVICE_ANNOUNCEMENT as non sendable
        notificationDao.flagAsNonSendable(orcid, sa.getId());
        
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(0, recordsWithNotificationsToSend.size());  
        
        NotificationEntity sa2 = new NotificationServiceAnnouncementEntity();
        sa2.setProfile(profile);
        sa2.setNotificationType("SERVICE_ANNOUNCEMENT");
        sa2.setSendable(true);
        notificationDao.persist(sa2);
        
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(1, recordsWithNotificationsToSend.size());  
        assertEquals(sa2.getId(), recordsWithNotificationsToSend.get(0).getId());
        
        notificationDao.flagAsNonSendable(orcid, sa2.getId());
        
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(0, recordsWithNotificationsToSend.size());
        
        // Mark both SERVICE_ANNOUNCEMENT as sendable
        notificationDao.flagAsSendable(orcid, sa.getId());
        notificationDao.flagAsSendable(orcid, sa2.getId());
        
        recordsWithNotificationsToSend = notificationDao.findUnsentServiceAnnouncements(100);
        assertEquals(2, recordsWithNotificationsToSend.size());  
        assertEquals(sa.getId(), recordsWithNotificationsToSend.get(0).getId());
        assertEquals(sa2.getId(), recordsWithNotificationsToSend.get(1).getId());        
    } 
    
    @Test
    public void testTipsNotifications() {
        String orcid = "0000-0000-0000-0003";
        ProfileEntity profile = new ProfileEntity(orcid);
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);     

        List<NotificationEntity> recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertTrue(recordsWithNotificationsToSend.isEmpty());

        // Add one one Tip
        NotificationEntity tip = new NotificationTipEntity();
        tip.setProfile(profile);
        tip.setNotificationType("TIP");
        tip.setSendable(true);
        notificationDao.persist(tip);
        
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertEquals(0, recordsWithNotificationsToSend.size());
        
        // Enable tips
        emailFrequencyDao.updateSendQuarterlyTips(orcid, true);
    
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertEquals(1, recordsWithNotificationsToSend.size());
        assertEquals(tip.getId(), recordsWithNotificationsToSend.get(0).getId());
        
        // Disable tips again
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertEquals(0, recordsWithNotificationsToSend.size());
        
        // Mark as non sendable and enable tips
        notificationDao.flagAsNonSendable(orcid, tip.getId());
        emailFrequencyDao.updateSendQuarterlyTips(orcid, true);
        
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertEquals(0, recordsWithNotificationsToSend.size());  
        
        NotificationEntity tip2 = new NotificationTipEntity();
        tip2.setProfile(profile);
        tip2.setNotificationType("TIP");
        tip2.setSendable(true);
        notificationDao.persist(tip2);
        
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertEquals(1, recordsWithNotificationsToSend.size());  
        assertEquals(tip2.getId(), recordsWithNotificationsToSend.get(0).getId());
        
        notificationDao.flagAsNonSendable(orcid, tip2.getId());
        
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        assertEquals(0, recordsWithNotificationsToSend.size());    
        
        // Flag both as sendable
        notificationDao.flagAsSendable(orcid, tip.getId());
        notificationDao.flagAsSendable(orcid, tip2.getId());
        
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        
        assertEquals(2, recordsWithNotificationsToSend.size());
        assertEquals(tip.getId(), recordsWithNotificationsToSend.get(0).getId());
        assertEquals(tip2.getId(), recordsWithNotificationsToSend.get(1).getId());
        
        // Disable tips
        emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
        
        recordsWithNotificationsToSend = notificationDao.findUnsentTips(100);
        
        assertEquals(0, recordsWithNotificationsToSend.size());
        
    }
}
