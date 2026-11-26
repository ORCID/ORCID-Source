package org.orcid.core.manager.v3;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.impl.TemplateManagerImpl;
import org.orcid.core.manager.v3.impl.DelegateInvitationMessaging;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.NotificationDelegateEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class DelegateInvitationMessagingTest {
    private static final String SENDER = "0000-0000-0000-0006";
    private static final String RECIPIENT = "0000-0000-0000-0003";
    private static final String TOKEN = "7e7d2ab1-0cf9-4c65-99ed-1809af5d5e75";

    @Mock
    private NotificationDao notificationDao;
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    @Mock
    private OrcidUrlManager orcidUrlManager;
    @Mock
    private org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly recordNameManagerReadOnly;
    @InjectMocks
    private DelegateInvitationMessaging messaging;

    private final List<NotificationDelegateEntity> saved = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        TemplateManagerImpl templates = new TemplateManagerImpl();
        templates.afterPropertiesSet();
        ReloadableResourceBundleMessageSource messages = new ReloadableResourceBundleMessageSource();
        messages.setBasename("classpath:i18n/notification_delegate_invitation");
        messages.setDefaultEncoding("UTF-8");
        messages.setFallbackToSystemLocale(false);
        ReflectionTestUtils.setField(messaging, "templateManager", templates);
        ReflectionTestUtils.setField(messaging, "messages", messages);
        lenient().when(orcidUrlManager.getBaseUrl()).thenReturn("https://orcid.org/");
        doAnswer(call -> {
            NotificationDelegateEntity entity = call.getArgument(0);
            entity.setId((long) saved.size() + 1);
            saved.add(entity);
            return null;
        }).when(notificationDao).persist(any(NotificationEntity.class));
    }

    @Test
    public void invitationTranslationsContainNoParametersOrHtml() throws Exception {
        Properties properties = new Properties();
        try (java.io.InputStream input = getClass().getResourceAsStream("/i18n/notification_delegate_invitation.properties")) {
            assertNotNull(input);
            properties.load(new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8));
        }
        assertFalse(properties.isEmpty());
        properties.forEach((key, value) -> {
            String text = (String) value;
            assertFalse(key + " contains a parameter", text.matches("(?s).*\\{\\d+[^}]*}.*"));
            assertFalse(key + " contains HTML", text.matches("(?s).*<[^>]+>.*"));
        });
    }

    @Test
    public void creationRendersActionLinksAndKeepsEmailAddressesPrivate() {
        DelegateInvitationEntity invitation = invitation("other@example.org");
        ProfileEntity profile = new ProfileEntity();
        profile.setLocale("FR");
        when(profileEntityCacheManager.retrieve(RECIPIENT)).thenReturn(profile);
        messaging.created(invitation, TOKEN);

        assertEquals(2, saved.size());
        NotificationDelegateEntity sender = saved.get(0);
        NotificationDelegateEntity recipient = saved.get(1);
        assertEquals(Long.valueOf(2), invitation.getActionableNotificationId());
        assertEquals(SENDER, sender.getOrcid());
        assertEquals(RECIPIENT, recipient.getOrcid());
        assertTrue(sender.isSendable());
        assertTrue(recipient.isSendable());
        assertEquals("fr", recipient.getLang());
        assertTrue(recipient.getBodyText().contains("https://orcid.org/delegate-invitation?token=" + TOKEN + "&decision=ACCEPT"));
        assertTrue(recipient.getBodyHtml().contains("&amp;decision=DECLINE"));
        assertTrue(sender.getBodyText().contains("other@example.org"));
        assertFalse(sender.getBodyText().contains(RECIPIENT));
        assertFalse(recipient.getBodyText().contains("@"));
        assertFalse(recipient.getBodyHtml().contains("@"));
        assertFalse(sender.getBodyText().contains(TOKEN));
        assertTrue(recipient.getBodyText().contains(SENDER));
    }


    @Test
    public void recipientInvitationShowsPublicNameAndStyledActionsSafely() {
        when(recordNameManagerReadOnly.fetchDisplayablePublicName(SENDER)).thenReturn("Alice <Example>");
        messaging.created(invitation("other@example.org"), TOKEN);

        NotificationDelegateEntity recipient = saved.get(1);
        assertEquals("Alice <Example> has invited you to be an account delegate", recipient.getSubject());
        String html = recipient.getBodyHtml();
        assertTrue(html.contains("<strong>Alice &lt;Example&gt;</strong>"));
        assertTrue(html.contains("href=\"https://orcid.org/" + SENDER + "\""));
        assertTrue(html.contains("Accept the invitation"));
        assertTrue(html.contains("Reject the invitation"));
        assertTrue(html.contains("&amp;decision=ACCEPT"));
        assertTrue(html.contains("&amp;decision=DECLINE"));
        assertFalse(html.contains("Alice <Example>"));
    }

    @Test
    public void acceptedSenderNotificationShowsPublicNameAndLinkedOrcid() {
        when(recordNameManagerReadOnly.fetchDisplayablePublicName(RECIPIENT)).thenReturn("Tom <Sawyer>");
        DelegateInvitationEntity invitation = invitation("tom@example.org");
        invitation.setStatus(DelegateInvitationEntity.Status.ACCEPTED);
        messaging.responded(invitation);

        NotificationDelegateEntity sender = saved.get(0);
        assertEquals("Tom <Sawyer> has accepted your account delegate invitation", sender.getSubject());
        assertTrue(sender.getBodyText().contains("Tom <Sawyer> (https://orcid.org/" + RECIPIENT + ") has accepted your invitation to become an account delegate for your ORCID record."));
        assertTrue(sender.getBodyText().contains("They have been added to your list of trusted individuals"));
        assertTrue(sender.getBodyHtml().contains("<strong>Tom &lt;Sawyer&gt;</strong>"));
        assertTrue(sender.getBodyHtml().contains("<a href=\"https://orcid.org/" + RECIPIENT + "\">https://orcid.org/" + RECIPIENT + "</a>"));
        assertFalse(sender.getBodyHtml().contains("Tom <Sawyer>"));
    }

    @Test
    public void acceptedSenderNotificationFallsBackToOrcidForPrivateName() {
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.ACCEPTED);
        messaging.responded(invitation);

        assertTrue(saved.get(0).getSubject().startsWith(RECIPIENT));
        assertTrue(saved.get(0).getBodyHtml().contains("href=\"https://orcid.org/" + RECIPIENT + "\""));
    }

    @Test
    public void acceptedRecipientConfirmationShowsSenderAndDelegateInformationSafely() {
        when(recordNameManagerReadOnly.fetchDisplayablePublicName(SENDER)).thenReturn("Alice <Example>");
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.ACCEPTED);
        messaging.responded(invitation);

        NotificationDelegateEntity recipient = saved.get(1);
        assertEquals("You have accepted an account delegate invitation from Alice <Example>", recipient.getSubject());
        assertTrue(recipient.getBodyText().contains("You have accepted an account delegate invitation from Alice <Example> (https://orcid.org/" + SENDER + ")."));
        assertTrue(recipient.getBodyText().contains("change Alice <Example>'s ORCID record"));
        assertTrue(recipient.getBodyText().contains("https://support.orcid.org/hc/articles/360006973613"));
        assertTrue(recipient.getBodyHtml().contains("<strong>Alice &lt;Example&gt;</strong>"));
        assertTrue(recipient.getBodyHtml().contains("<a href=\"https://orcid.org/" + SENDER + "\">https://orcid.org/" + SENDER + "</a>"));
        assertTrue(recipient.getBodyHtml().contains("<a href=\"https://support.orcid.org/hc/articles/360006973613\">"));
        assertFalse(recipient.getBodyHtml().contains("Alice <Example>"));
    }

    @Test
    public void acceptedRecipientConfirmationFallsBackToOrcidForPrivateName() {
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.ACCEPTED);
        messaging.responded(invitation);

        assertTrue(saved.get(1).getSubject().contains(SENDER));
        assertTrue(saved.get(1).getBodyHtml().contains("href=\"https://orcid.org/" + SENDER + "\""));
    }

    @Test
    public void declinedRecipientConfirmationShowsSenderSafely() {
        when(recordNameManagerReadOnly.fetchDisplayablePublicName(SENDER)).thenReturn("Alice <Example>");
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
        messaging.responded(invitation);

        NotificationDelegateEntity recipient = saved.get(1);
        assertEquals("You have declined an account delegate invitation from Alice <Example>", recipient.getSubject());
        assertTrue(recipient.getBodyText().contains("Alice <Example> (https://orcid.org/" + SENDER + ")"));
        assertTrue(recipient.getBodyText().contains("They have been informed that you have declined."));
        assertTrue(recipient.getBodyHtml().contains("<strong>Alice &lt;Example&gt;</strong>"));
        assertTrue(recipient.getBodyHtml().contains("<a href=\"https://orcid.org/" + SENDER + "\">https://orcid.org/" + SENDER + "</a>"));
        assertFalse(recipient.getBodyHtml().contains("Alice <Example>"));
    }

    @Test
    public void declinedRecipientConfirmationFallsBackToOrcidWhenNameIsPrivate() {
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
        messaging.responded(invitation);

        NotificationDelegateEntity recipient = saved.get(1);
        assertTrue(recipient.getSubject().contains(SENDER));
        assertTrue(recipient.getBodyHtml().contains("href=\"https://orcid.org/" + SENDER + "\""));
    }

    @Test
    public void declinedSenderNotificationShowsPublicNameAndLinkedOrcid() {
        when(recordNameManagerReadOnly.fetchDisplayablePublicName(RECIPIENT)).thenReturn("Tom <Sawyer>");
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
        messaging.responded(invitation);

        NotificationDelegateEntity sender = saved.get(0);
        assertEquals("Tom <Sawyer> has declined your account delegate invitation", sender.getSubject());
        assertTrue(sender.getBodyText().contains("Tom <Sawyer> (https://orcid.org/" + RECIPIENT + ") has declined your invitation to become an account delegate for your ORCID record."));
        assertTrue(sender.getBodyHtml().contains("<strong>Tom &lt;Sawyer&gt;</strong>"));
        assertTrue(sender.getBodyHtml().contains("<a href=\"https://orcid.org/" + RECIPIENT + "\">https://orcid.org/" + RECIPIENT + "</a>"));
        assertFalse(sender.getBodyHtml().contains("Tom <Sawyer>"));
    }

    @Test
    public void declinedSenderEmailNotificationDoesNotExposeRecipientIdentity() {
        DelegateInvitationEntity invitation = invitation("tom+<test>@example.org");
        invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
        messaging.responded(invitation);

        NotificationDelegateEntity sender = saved.get(0);
        assertEquals("tom+<test>@example.org has declined your account delegate invitation", sender.getSubject());
        assertTrue(sender.getBodyText().contains("tom+<test>@example.org has declined your invitation to become an account delegate for your ORCID record."));
        assertTrue(sender.getBodyHtml().contains("<strong>tom+&lt;test&gt;@example.org</strong>"));
        assertFalse(sender.getBodyHtml().contains(RECIPIENT));
        assertFalse(sender.getBodyText().contains(RECIPIENT));
    }

    @Test
    public void declinedSenderNotificationFallsBackToOrcidForPrivateName() {
        DelegateInvitationEntity invitation = invitation(null);
        invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
        messaging.responded(invitation);

        assertTrue(saved.get(0).getSubject().startsWith(RECIPIENT));
        assertTrue(saved.get(0).getBodyHtml().contains("href=\"https://orcid.org/" + RECIPIENT + "\""));
    }

    @Test
    public void responsesProduceTwoNonActionableMessages() {
        for (DelegateInvitationEntity.Status status : new DelegateInvitationEntity.Status[]{
                DelegateInvitationEntity.Status.ACCEPTED, DelegateInvitationEntity.Status.DECLINED}) {
            saved.clear();
            DelegateInvitationEntity invitation = invitation("other@example.org");
            invitation.setStatus(status);
            messaging.responded(invitation);
            assertEquals(2, saved.size());
            assertTrue(saved.get(0).getBodyText().contains(status == DelegateInvitationEntity.Status.ACCEPTED ? "accepted" : "declined"));
            assertEquals(RECIPIENT, saved.get(1).getOrcid());
            assertTrue(saved.get(1).isSendable());
            assertFalse(saved.get(1).getBodyHtml().contains(TOKEN));
        }
    }

    @Test
    public void senderCreationAndDeclineUseLookupSpecificIdentity() {
        String entered = "other+<tag>@example.org";
        for (DelegateInvitationEntity.LookupType type : DelegateInvitationEntity.LookupType.values()) {
            DelegateInvitationEntity invitation = invitation(type == DelegateInvitationEntity.LookupType.EMAIL ? entered : null);
            for (boolean decline : new boolean[]{false, true}) {
                saved.clear();
                if (decline) {
                    invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
                    messaging.responded(invitation);
                } else {
                    messaging.created(invitation, TOKEN);
                }
                NotificationDelegateEntity sender = saved.get(0);
                String expected = type == DelegateInvitationEntity.LookupType.EMAIL ? entered : RECIPIENT;
                assertTrue(sender.getBodyText(), sender.getBodyText().contains(expected));
                assertFalse(sender.getBodyText().contains("Primary@example.org"));
                assertFalse(sender.getBodyHtml().contains("Primary@example.org"));
                assertFalse(sender.getBodyText().contains(type == DelegateInvitationEntity.LookupType.EMAIL ? RECIPIENT : entered));
                assertFalse(sender.getBodyHtml().contains(type == DelegateInvitationEntity.LookupType.EMAIL ? RECIPIENT : entered));
                if (type == DelegateInvitationEntity.LookupType.EMAIL) {
                    assertTrue(sender.getBodyHtml().contains("other+&lt;tag&gt;@example.org"));
                } else {
                    assertTrue(sender.getBodyHtml().contains(RECIPIENT));
                }
                assertFalse(saved.get(1).getBodyText().contains(entered));
                assertFalse(saved.get(1).getBodyHtml().contains("Primary@example.org"));
            }
        }
    }


    private DelegateInvitationEntity invitation(String enteredEmail) {
        DelegateInvitationEntity invitation = new DelegateInvitationEntity();
        invitation.setSenderOrcid(SENDER);
        invitation.setRecipientOrcid(RECIPIENT);
        invitation.setEnteredEmail(enteredEmail);
        invitation.setStatus(DelegateInvitationEntity.Status.PENDING);
        return invitation;
    }
}