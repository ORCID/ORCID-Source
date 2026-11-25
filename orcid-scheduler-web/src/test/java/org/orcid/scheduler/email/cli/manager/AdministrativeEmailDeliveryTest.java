package org.orcid.scheduler.email.cli.manager;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.utils.email.MailGunManager;
import org.springframework.test.util.ReflectionTestUtils;

public class AdministrativeEmailDeliveryTest {
    private EmailMessageSenderImpl sender;
    private NotificationDao dao;
    private MailGunManager mail;

    @Before
    public void setUp() {
        sender = spy(new EmailMessageSenderImpl(1, 3));
        dao = mock(NotificationDao.class);
        NotificationDao readOnlyDao = mock(NotificationDao.class);
        NotificationManager manager = mock(NotificationManager.class);
        EmailDao emailDao = mock(EmailDao.class);
        mail = mock(MailGunManager.class);
        ReflectionTestUtils.setField(sender, "notificationDao", dao);
        ReflectionTestUtils.setField(sender, "notificationDaoReadOnly", readOnlyDao);
        ReflectionTestUtils.setField(sender, "notificationManager", manager);
        ReflectionTestUtils.setField(sender, "emailDao", emailDao);
        ReflectionTestUtils.setField(sender, "mailGunManager", mail);

        String orcid = "0000-0000-0000-0001";
        Date activeDate = new Date();
        when(readOnlyDao.findRecordsWithUnsentNotifications()).thenReturn(Collections.singletonList(new Object[] { orcid, activeDate }));
        NotificationAdministrative notification = new NotificationAdministrative();
        notification.setNotificationType(NotificationType.ADMINISTRATIVE);
        notification.setPutCode(7L);
        List<Notification> notifications = Collections.singletonList(notification);
        when(manager.findNotificationsToSend(orcid, null, activeDate)).thenReturn(notifications);
        EmailEntity primary = new EmailEntity();
        primary.setEmail("primary@example.org");
        when(emailDao.findPrimaryEmail(orcid)).thenReturn(primary);
        EmailMessage digest = new EmailMessage();
        digest.setSubject("digest");
        digest.setBodyText("text");
        digest.setBodyHtml("html");
        doReturn(digest).when(sender).createDigest(orcid, notifications);
    }

    @Test
    public void successfulDigestFlagsAdministrativeNotificationAsSent() {
        when(mail.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, "primary@example.org", "digest", "text", "html")).thenReturn(true);
        sender.sendEmailMessages();
        verify(mail).sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, "primary@example.org", "digest", "text", "html");
        verify(dao).flagAsSent(7L);
    }

    @Test
    public void failedDigestLeavesAdministrativeNotificationUnsent() {
        when(mail.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, "primary@example.org", "digest", "text", "html")).thenReturn(false);
        sender.sendEmailMessages();
        verify(mail).sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, "primary@example.org", "digest", "text", "html");
        verify(dao, never()).flagAsSent(7L);
    }
}