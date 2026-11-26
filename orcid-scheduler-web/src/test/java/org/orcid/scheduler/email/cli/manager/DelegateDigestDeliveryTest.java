package org.orcid.scheduler.email.cli.manager;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.impl.NotificationManagerImpl;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.NotificationAdministrativeEntity;
import org.orcid.persistence.jpa.entities.NotificationDelegateEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.DigestEmail;
import org.orcid.utils.email.MailGunManager;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

public class DelegateDigestDeliveryTest {
    @Test
    public void digestIncludesAllDelegateFieldsAndFlagsOnlyAfterSuccessfulSend() {
        EmailMessageSenderImpl sender = new EmailMessageSenderImpl(1, 3);
        NotificationDao dao = mock(NotificationDao.class);
        EmailDao emailDao = mock(EmailDao.class);
        JpaJaxbNotificationAdapter adapter = mock(JpaJaxbNotificationAdapter.class);
        ProfileEntityCacheManager profiles = mock(ProfileEntityCacheManager.class);
        RecordNameManager names = mock(RecordNameManager.class);
        TemplateManager templates = mock(TemplateManager.class);
        MessageSource messages = mock(MessageSource.class);
        OrcidUrlManager urls = mock(OrcidUrlManager.class);
        MailGunManager mail = mock(MailGunManager.class);
        ReflectionTestUtils.setField(sender, "notificationDao", dao);
        ReflectionTestUtils.setField(sender, "notificationDaoReadOnly", dao);
        ReflectionTestUtils.setField(sender, "emailDao", emailDao);
        NotificationManagerImpl manager = new NotificationManagerImpl();
        ReflectionTestUtils.setField(manager, "notificationDao", dao);
        ReflectionTestUtils.setField(manager, "notificationAdapter", adapter);
        ReflectionTestUtils.setField(sender, "notificationManager", manager);
        ReflectionTestUtils.setField(sender, "profileEntityCacheManager", profiles);
        ReflectionTestUtils.setField(sender, "recordNameManagerV3", names);
        ReflectionTestUtils.setField(sender, "templateManager", templates);
        ReflectionTestUtils.setField(sender, "messages", messages);
        ReflectionTestUtils.setField(sender, "orcidUrlManager", urls);
        ReflectionTestUtils.setField(sender, "mailGunManager", mail);
        ProfileEntity profile = new ProfileEntity();
        profile.setId("id");
        when(profiles.retrieve("id")).thenReturn(profile);
        when(names.deriveEmailFriendlyName("id")).thenReturn("name");
        when(messages.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("digest");
        when(templates.processTemplate(anyString(), anyMap(), any(Locale.class))).thenReturn("body");
        EmailEntity email = new EmailEntity();
        email.setEmail("user@example.org");
        when(emailDao.findPrimaryEmail("id")).thenReturn(email);
        when(dao.findRecordsWithUnsentNotifications()).thenReturn(Collections.singletonList(new Object[] { "id", new Date(0) }));
        NotificationDelegateEntity first = delegate(1L, "one");
        NotificationDelegateEntity second = delegate(2L, "two");
        NotificationAdministrativeEntity ordinary = new NotificationAdministrativeEntity();
        ordinary.setId(3L);
        NotificationAdministrative mapped = new NotificationAdministrative();
        mapped.setSubject("ordinary");
        when(adapter.toNotification(ordinary)).thenReturn(mapped);
        when(dao.findNotificationsToSend(any(Date.class), eq("id"), any(Date.class))).thenReturn(Arrays.asList(first, second, ordinary));
        when(mail.sendEmail(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(false, true);

        sender.sendEmailMessages();
        verify(dao, never()).flagAsSent(anyLong());
        sender.sendEmailMessages();
        verify(dao).flagAsSent(1L);
        verify(dao).flagAsSent(2L);
        verify(dao).flagAsSent(3L);
        verify(adapter, never()).toNotification(first);
        verify(adapter, never()).toNotification(second);
        verify(adapter, times(2)).toNotification(ordinary);
        verify(dao, times(2)).findNotificationsToSend(any(Date.class), eq("id"), any(Date.class));

        ArgumentCaptor<Map> params = ArgumentCaptor.forClass(Map.class);
        verify(templates, times(2)).processTemplate(eq("digest_notification_html.ftl"), params.capture(), any(Locale.class));
        Map data = params.getValue();
        List<Map<String, String>> delegates = (List<Map<String, String>>) data.get("delegateNotifications");
        assertEquals(2, delegates.size());
        assertEquals("one", delegates.get(0).get("subject"));
        assertEquals("<p>one</p>", delegates.get(0).get("bodyHtml"));
        assertEquals("two text", delegates.get(1).get("bodyText"));
        DigestEmail digest = (DigestEmail) data.get("digestEmail");
        assertEquals(1, digest.getNotificationsBySourceId().size());
    }

    private NotificationDelegateEntity delegate(long id, String value) {
        NotificationDelegateEntity notification = new NotificationDelegateEntity();
        notification.setId(id);
        notification.setSubject(value);
        notification.setBodyHtml("<p>" + value + "</p>");
        notification.setBodyText(value + " text");
        return notification;
    }
}