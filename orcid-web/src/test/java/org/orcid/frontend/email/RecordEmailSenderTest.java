package org.orcid.frontend.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBException;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailListChange;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.email.MailGunManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
@ActiveProfiles("unitTests")
public class RecordEmailSenderTest {

    @Mock
    private ProfileEventDao mockProfileEventDao;

    @Mock
    private MailGunManager mockMailGunManager;

    @Mock
    private ProfileEntityCacheManager mockProfileEntityCacheManager;

    @Mock
    private EmailManager mockEmailManager;

    @Mock
    public RecordNameManager mockRecordNameManager;

    @Mock
    private RedisClient mockRedisClient;
    
    @Resource
    RecordEmailSender recordEmailSender;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        ProfileEntity p = new ProfileEntity();
        p.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(anyString())).thenReturn(p);
        
        Email e = new Email();
        e.setEmail("public_0000-0000-0000-0003@test.orcid.org");
        when(mockEmailManager.findPrimaryEmail(eq("0000-0000-0000-0003"))).thenReturn(e);
        
        when(mockRecordNameManager.deriveEmailFriendlyName(anyString())).thenReturn("User name");
        
        ReflectionTestUtils.setField(recordEmailSender, "profileEntityCacheManager", mockProfileEntityCacheManager);
        ReflectionTestUtils.setField(recordEmailSender, "emailManager", mockEmailManager);
        ReflectionTestUtils.setField(recordEmailSender, "recordNameManager", mockRecordNameManager);
        ReflectionTestUtils.setField(recordEmailSender, "profileEventDao", mockProfileEventDao);
        ReflectionTestUtils.setField(recordEmailSender, "mailgunManager", mockMailGunManager);
        // expiringLinkService stays the real bean so the rendered bodies carry a genuine JWT.
        ReflectionTestUtils.setField(recordEmailSender, "redisClient", mockRedisClient);
    }

    /** Builds a verified {@link Emails} list; the reset fan out reads it through getVerifiedEmails. */
    private Emails verifiedEmails(String... addresses) {
        Emails emails = new Emails();
        for (String address : addresses) {
            Email email = new Email();
            email.setEmail(address);
            email.setVerified(true);
            emails.getEmails().add(email);
        }
        return emails;
    }

    private List<String> capturedResetRecipients() {
        ArgumentCaptor<String> to = ArgumentCaptor.forClass(String.class);
        verify(mockMailGunManager, atLeastOnce()).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), to.capture(), anyString(), anyString(),
                anyString());
        return to.getAllValues();
    }
    
    @Test
    public void testSendWelcomeEmail() throws JAXBException, IOException, URISyntaxException {        
        Email email = new Email();
        email.setEmail("josiah_carberry@brown.edu");
        when(mockEmailManager.findPrimaryEmail(anyString())).thenReturn(email);
        
        recordEmailSender.sendWelcomeEmail("4444-4444-4444-4446", "josiah_carberry@brown.edu");
        
        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_VERIFY_ORCID_ORG), eq("josiah_carberry@brown.edu"), eq("[ORCID] Welcome to ORCID - verify your email address"), anyString(), anyString());
    }

    @Test
    public void testSendEmailListChangeEmail_onlyCurrentEmails() throws JAXBException, IOException, URISyntaxException {
        final String orcid = "0000-0000-0000-0003";
        final String currentVerifiedEmail = "current_verified@test.com";

        ProfileEntity profile = new ProfileEntity(orcid);
        profile.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(orcid)).thenReturn(profile);

        org.orcid.jaxb.model.v3.release.record.Emails emails = new org.orcid.jaxb.model.v3.release.record.Emails();

        org.orcid.jaxb.model.v3.release.record.Email email = new org.orcid.jaxb.model.v3.release.record.Email();
        email.setEmail(currentVerifiedEmail);
        email.setVerified(true);

        org.orcid.jaxb.model.v3.release.record.Email email2 = new org.orcid.jaxb.model.v3.release.record.Email();
        email2.setEmail("2_" + currentVerifiedEmail);
        email2.setVerified(false);

        emails.getEmails().add(email);
        emails.getEmails().add(email2);
        when(mockEmailManager.getEmails(orcid)).thenReturn(emails);

        EmailListChange emailListChange = new EmailListChange();

        recordEmailSender.sendEmailListChangeEmail(orcid, emailListChange);

        verify(mockMailGunManager, times(1)).sendEmail(
                eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG),
                eq(currentVerifiedEmail),
                anyString(),
                anyString(),
                anyString());

        verify(mockMailGunManager, times(1)).sendEmail(
                eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG),
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }

    @Test
    public void testSendEmailListChangeEmail_withRemovedVerifiedEmail() throws JAXBException, IOException, URISyntaxException {
        final String orcid = "0000-0000-0000-0003";
        final String currentVerifiedEmail = "current_verified@test.com";
        final String removedVerifiedEmail = "removed_verified@test.com";

        ProfileEntity profile = new ProfileEntity(orcid);
        profile.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(orcid)).thenReturn(profile);

        org.orcid.jaxb.model.v3.release.record.Emails emails = new org.orcid.jaxb.model.v3.release.record.Emails();
        org.orcid.jaxb.model.v3.release.record.Email currentEmail = new org.orcid.jaxb.model.v3.release.record.Email();
        currentEmail.setEmail(currentVerifiedEmail);
        currentEmail.setVerified(true);
        org.orcid.jaxb.model.v3.release.record.Email currentEmail2 = new org.orcid.jaxb.model.v3.release.record.Email();
        currentEmail2.setEmail("2_" + currentVerifiedEmail);
        currentEmail2.setVerified(false);
        emails.getEmails().add(currentEmail);
        emails.getEmails().add(currentEmail2);
        when(mockEmailManager.getEmails(orcid)).thenReturn(emails);

        org.orcid.jaxb.model.v3.release.record.Email removedEmail = new org.orcid.jaxb.model.v3.release.record.Email();
        removedEmail.setEmail(removedVerifiedEmail);
        removedEmail.setVerified(true);

        EmailListChange emailListChange = new EmailListChange();
        emailListChange.getRemovedEmails().add(removedEmail);

        recordEmailSender.sendEmailListChangeEmail(orcid, emailListChange);

        verify(mockMailGunManager, times(1)).sendEmail(
                eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG),
                eq(currentVerifiedEmail),
                anyString(),
                anyString(),
                anyString());

        verify(mockMailGunManager, times(1)).sendEmail(
                eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG),
                eq(removedVerifiedEmail),
                anyString(),
                anyString(),
                anyString());

        verify(mockMailGunManager, times(2)).sendEmail(
                eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG),
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }

    @Test
    public void testSendOrcidDeactivatedEmail() throws JAXBException, IOException, URISyntaxException {
        Email email = new Email();
        email.setEmail("josiah_carberry@brown.edu");
        when(mockEmailManager.findPrimaryEmail(anyString())).thenReturn(email);

        recordEmailSender.sendOrcidDeactivatedEmail("4444-4444-4444-4446");

        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq("josiah_carberry@brown.edu"), eq("[ORCID] Your ORCID account has been deactivated"), anyString(), anyString());
    }

    @Test
    public void testSendVerificationEmail() throws JAXBException, IOException, URISyntaxException {
        ProfileEntity p = new ProfileEntity();
        p.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(anyString())).thenReturn(p);

        Email email = new Email();
        email.setEmail("josiah_carberry@brown.edu");
        when(mockEmailManager.findPrimaryEmail(anyString())).thenReturn(email);

        recordEmailSender.sendVerificationEmail("4444-4444-4444-4446", "josiah_carberry@brown.edu", true);
    }

    @Test
    public void testResetEmail() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String primaryEmail = "public_0000-0000-0000-0003@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(primaryEmail));
        for (AvailableLocales locale : AvailableLocales.values()) {
            EncryptionManager mockEncypter = mock(EncryptionManager.class);
            when(mockEncypter.encryptForExternalUse(any(String.class)))
                    .thenReturn("Ey+qsh7G2BFGEuqqkzlYRidL4NokGkIgDE+1KOv6aLTmIyrppdVA6WXFIaQ3KsQpKEb9FGUFRqiWorOfhbB2ww==");
            recordEmailSender.sendPasswordResetEmail(primaryEmail, userOrcid);
        }
    }

    @Test
    public void testResetEmail_sendsToEveryVerifiedEmail() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(submitted, "second@test.orcid.org", "third@test.orcid.org"));

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq(submitted), anyString(), anyString(), anyString());
        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq("second@test.orcid.org"), anyString(), anyString(),
                anyString());
        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq("third@test.orcid.org"), anyString(), anyString(),
                anyString());
        verify(mockMailGunManager, times(3)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_sameLinkToEveryRecipient() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(submitted, "second@test.orcid.org", "third@test.orcid.org"));

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        verify(mockMailGunManager, times(3)).sendEmail(anyString(), anyString(), anyString(), bodies.capture(), anyString());

        String firstLink = resetLinkIn(bodies.getAllValues().get(0));
        assertTrue("no reset link found in the body", firstLink.length() > 0);
        for (String body : bodies.getAllValues()) {
            assertEquals(firstLink, resetLinkIn(body));
        }
    }

    private String resetLinkIn(String body) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("/reset-password-email/(\\S+)").matcher(body);
        return matcher.find() ? matcher.group(1) : "";
    }

    @Test
    public void testResetEmail_writesOneRedisEntryForAllRecipients() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails("primary@test.orcid.org", "second@test.orcid.org"));

        recordEmailSender.sendPasswordResetEmail("primary@test.orcid.org", userOrcid);

        verify(mockRedisClient, times(1)).set(eq("password-reset-token-" + userOrcid), anyString(), anyInt());
        verify(mockMailGunManager, times(2)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_tokenIsWrittenBeforeAnySend() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails("primary@test.orcid.org", "second@test.orcid.org"));

        recordEmailSender.sendPasswordResetEmail("primary@test.orcid.org", userOrcid);

        InOrder inOrder = inOrder(mockRedisClient, mockMailGunManager);
        inOrder.verify(mockRedisClient).set(eq("password-reset-token-" + userOrcid), anyString(), anyInt());
        inOrder.verify(mockMailGunManager, atLeastOnce()).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_dedupesSubmittedAddressCaseInsensitively() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String stored = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(stored));

        recordEmailSender.sendPasswordResetEmail("Primary@Test.Orcid.Org", userOrcid);

        // One message only, addressed in the stored casing rather than as typed.
        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq(stored), anyString(), anyString(), anyString());
        verify(mockMailGunManager, times(1)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_includesUnverifiedSubmittedAddress() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String unverifiedSubmitted = "unverified@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails("verified@test.orcid.org"));

        recordEmailSender.sendPasswordResetEmail(unverifiedSubmitted, userOrcid);

        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq(unverifiedSubmitted), anyString(), anyString(), anyString());
        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq("verified@test.orcid.org"), anyString(), anyString(),
                anyString());
        verify(mockMailGunManager, times(2)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_stillSendsWhenRecordHasNoVerifiedEmails() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "unverified@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(new Emails());

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq(submitted), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_submittedAddressIsFirstRecipient() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "second@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails("first@test.orcid.org", submitted));

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        assertEquals(submitted, capturedResetRecipients().get(0));
    }

    @Test
    public void testResetEmail_bodyNamesEachRecipientsOwnAddress() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(submitted, "second@test.orcid.org"));

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        ArgumentCaptor<String> to = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        verify(mockMailGunManager, times(2)).sendEmail(anyString(), to.capture(), anyString(), bodies.capture(), anyString());

        for (int i = 0; i < to.getAllValues().size(); i++) {
            String recipient = to.getAllValues().get(i);
            String body = bodies.getAllValues().get(i);
            assertTrue("body for " + recipient + " should name that recipient", body.contains(recipient));
        }
        // ...and must not leak the other address on the record.
        assertEquals(submitted, to.getAllValues().get(0));
        assertTrue(!bodies.getAllValues().get(0).contains("second@test.orcid.org"));
    }

    @Test
    public void testResetEmail_rendersEachTemplateOnceForAllRecipients() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(submitted, "second@test.orcid.org", "third@test.orcid.org"));

        TemplateManager real = (TemplateManager) ReflectionTestUtils.getField(recordEmailSender, "templateManager");
        TemplateManager spyTemplateManager = spy(real);
        ReflectionTestUtils.setField(recordEmailSender, "templateManager", spyTemplateManager);
        try {
            recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

            // Three recipients, but only the address differs between copies, so each template is
            // rendered once and the placeholder is substituted per recipient.
            verify(spyTemplateManager, times(1)).processTemplate(eq("reset_password_email.ftl"), any());
            verify(spyTemplateManager, times(1)).processTemplate(eq("reset_password_email_html.ftl"), any());
            verify(mockMailGunManager, times(3)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
        } finally {
            ReflectionTestUtils.setField(recordEmailSender, "templateManager", real);
        }
    }

    @Test
    public void testResetEmail_escapesRecipientAddressInHtmlBody() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        // The substitution happens after FreeMarker's <#escape x as x?html> has run, so the
        // address is the one value that could reach the HTML unescaped.
        String submitted = "a&b@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(new Emails());

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> htmlBody = ArgumentCaptor.forClass(String.class);
        verify(mockMailGunManager, times(1)).sendEmail(anyString(), eq(submitted), anyString(), body.capture(), htmlBody.capture());

        assertTrue("plain text body should carry the raw address", body.getValue().contains(submitted));
        assertTrue("html body should carry the escaped address", htmlBody.getValue().contains("a&amp;b@test.orcid.org"));
        assertTrue("html body must not carry the raw ampersand", !htmlBody.getValue().contains("a&b@test.orcid.org"));
    }

    @Test
    public void testResetEmail_continuesWhenOneRecipientFails() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(submitted, "second@test.orcid.org", "third@test.orcid.org"));
        doThrow(new RuntimeException("mailgun down")).when(mockMailGunManager).sendEmail(anyString(), eq("second@test.orcid.org"), anyString(), anyString(),
                anyString());

        // No exception escapes: the token is already redeemable, so the rest must still be tried.
        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        verify(mockMailGunManager, times(1)).sendEmail(eq(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG), eq("third@test.orcid.org"), anyString(), anyString(),
                anyString());
        verify(mockMailGunManager, times(3)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_toleratesMailgunReturningFalse() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(submitted, "second@test.orcid.org"));
        when(mockMailGunManager.sendEmail(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(false);

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        verify(mockMailGunManager, times(2)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testResetEmail_capsRecipients() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String submitted = "primary@test.orcid.org";
        int cap = 25;
        String[] addresses = new String[40];
        addresses[0] = submitted;
        for (int i = 1; i < addresses.length; i++) {
            addresses[i] = "extra" + i + "@test.orcid.org";
        }
        when(mockEmailManager.getVerifiedEmails(userOrcid)).thenReturn(verifiedEmails(addresses));

        recordEmailSender.sendPasswordResetEmail(submitted, userOrcid);

        verify(mockMailGunManager, times(cap)).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString());
        // The address the user typed is always inside the cap.
        assertEquals(submitted, capturedResetRecipients().get(0));
    }

    @Test
    public void testResetNotFoundEmail() throws Exception {        
        String submittedEmail = "email_not_in_orcid@test.orcid.org";
        for (AvailableLocales curLocale : AvailableLocales.values()) {
            recordEmailSender.sendPasswordResetNotFoundEmail(submittedEmail, LocaleUtils.toLocale(curLocale.value()));
        }
    }
    
    @Test
    public void testSendDeactivateEmail() throws JAXBException, IOException, URISyntaxException {
        final String orcid = "0000-0000-0000-0003";

        ProfileEntity profile = new ProfileEntity(orcid);

        Email email = new Email();
        email.setEmail("test@email.com");

        when(mockProfileEntityCacheManager.retrieve(orcid)).thenReturn(profile);
        when(mockEmailManager.findPrimaryEmail(orcid)).thenReturn(email);

        for (org.orcid.jaxb.model.common_v2.Locale locale : org.orcid.jaxb.model.common_v2.Locale.values()) {
            profile.setLocale(locale.name());
            recordEmailSender.sendOrcidDeactivateEmail(orcid);
        }
    }
    
    @Test
    public void testClaimReminderEmail() throws JAXBException, IOException, URISyntaxException {
        String userOrcid = "0000-0000-0000-0003";
        ProfileEntity profile = new ProfileEntity(userOrcid);
        for (AvailableLocales locale : AvailableLocales.values()) {
            // Ignore CS locale as there is no available locale for it on
            // common_v2.Locale
            if (!locale.equals(AvailableLocales.CS)) {
                profile.setLocale(locale.name());
                when(mockProfileEntityCacheManager.retrieve(userOrcid)).thenReturn(profile);
                recordEmailSender.sendClaimReminderEmail(userOrcid, 2, "test@test.com");
            }
        }
    }

    @Test
    public void testChangeEmailAddress() throws Exception {
        final String orcid = "0000-0000-0000-0003";

        ProfileEntity profile = new ProfileEntity(orcid);
        Email email = new Email();
        email.setEmail("test@email.com");

        when(mockProfileEntityCacheManager.retrieve(orcid)).thenReturn(profile);
        when(mockEmailManager.findPrimaryEmail(orcid)).thenReturn(email);

        for (org.orcid.jaxb.model.common_v2.Locale locale : org.orcid.jaxb.model.common_v2.Locale.values()) {
            profile.setLocale(locale.name());
        }
    }

    @Test
    public void testSendReactivationEmail() throws Exception {
        ProfileEntity p = new ProfileEntity();
        p.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(anyString())).thenReturn(p);

        String userOrcid = "0000-0000-0000-0003";
        String email = "original@email.com";
        for (AvailableLocales locale : AvailableLocales.values()) {
            recordEmailSender.sendReactivationEmail(email, userOrcid);
        }
    }
    
    @Test
    public void testSend2FADisabledEmail() throws JAXBException, IOException, URISyntaxException {
        ProfileEntity p = new ProfileEntity();
        p.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(anyString())).thenReturn(p);

        org.orcid.jaxb.model.v3.release.record.Emails emails = new org.orcid.jaxb.model.v3.release.record.Emails();
        List<org.orcid.jaxb.model.v3.release.record.Email> emailsList = new ArrayList<>();
        org.orcid.jaxb.model.v3.release.record.Email email = new org.orcid.jaxb.model.v3.release.record.Email();
        email.setEmail("test@test.com");
        emailsList.add(email);
        emails.setEmails(emailsList);
        when(mockEmailManager.getEmails(anyString())).thenReturn(emails);

        recordEmailSender.send2FADisabledEmail("4444-4444-4444-4446");
    }
    
    @Test
    public void testSendForgottenIdEmail() throws JAXBException, IOException, URISyntaxException {
        String userOrcid = "0000-0000-0000-0003";
        ProfileEntity profile = new ProfileEntity(userOrcid);
        for (AvailableLocales locale : AvailableLocales.values()) {
            // Ignore CS locale as there is no available locale for it on
            // common_v2.Locale
            if (!locale.equals(AvailableLocales.CS)) {
                profile.setLocale(locale.name());
                when(mockProfileEntityCacheManager.retrieve(userOrcid)).thenReturn(profile);
                recordEmailSender.sendForgottenIdEmail("test@test.com", userOrcid);
            }
        }
    }
    
    @Test
    public void testSendForgottenIdEmailNotFound() throws Exception {
        String submittedEmail = "email_not_in_orcid@test.orcid.org";
        for (AvailableLocales curLocale : AvailableLocales.values()) {
            recordEmailSender.sendForgottenIdEmailNotFoundEmail(submittedEmail, LocaleUtils.toLocale(curLocale.value()));
        }
    }

    @Test
    public void testSendOrcidLockedEmail() throws JAXBException, IOException, URISyntaxException {
        ProfileEntity p = new ProfileEntity();
        p.setLocale("EN");
        when(mockProfileEntityCacheManager.retrieve(anyString())).thenReturn(p);

        Email email = new Email();
        email.setEmail("josiah_carberry@brown.edu");
        when(mockEmailManager.findPrimaryEmail(anyString())).thenReturn(email);

        recordEmailSender.sendOrcidLockedEmail("4444-4444-4444-4446");
    }
}
