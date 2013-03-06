/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.NotificationManagerImpl;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "/test-orcid-core-context.xml" })
public class NotificationManagerTest extends BaseTest {

    public static final String ORCID_INTERNAL_FULL_XML = "/orcid-internal-full-message-latest.xml";

    private Unmarshaller unmarshaller;

    @Mock
    private MailSender mailSender;

    @Mock
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Mock
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Autowired
    private NotificationManager notificationManager;

    @Before
    public void initJaxb() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Before
    public void initMocks() throws Exception {
        notificationManager.setMailSender(mailSender);
        notificationManager.setSecurityQuestionDao(securityQuestionDao);
        NotificationManagerImpl notificationManagerImpl = getTargetObject(notificationManager, NotificationManagerImpl.class);
        notificationManagerImpl.setEncryptionManager(encryptionManager);
        notificationManagerImpl.setProfileEventDao(profileEventDao);
    }

    @Test
    @Rollback
    public void testSendLegacyVerificationEmail() throws JAXBException, IOException, URISyntaxException {
        URI baseUri = new URI("http://testserver.orcid.org");
        SecurityQuestionEntity securityQuestion = new SecurityQuestionEntity();
        securityQuestion.setId(1);
        securityQuestion.setQuestion("What is the name of your favorite teacher?");
        when(securityQuestionDao.find(1)).thenReturn(securityQuestion);

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendLegacyVerificationEmail(orcidProfile, baseUri);

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("[ORCID] Registration Complete");
        expected.setText(IOUtils.toString(getClass().getResourceAsStream("example_legacy_verification_email_body.txt")));

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    @Rollback
    public void testSendVerificationEmail() throws JAXBException, IOException, URISyntaxException {
        URI baseUri = new URI("http://testserver.orcid.org");

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendVerificationEmail(orcidProfile, baseUri);

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("[ORCID] Email Verification Required");
        expected.setText(IOUtils.toString(getClass().getResourceAsStream("example_verification_email_body.txt")));

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    @Rollback
    public void testResetEmail() throws Exception {
        URI baseUri = new URI("http://testserver.orcid.org");

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        orcidProfile.setPassword("r$nd0m");
        EncryptionManager mockEncypter = mock(EncryptionManager.class);
        getTargetObject(notificationManager, NotificationManagerImpl.class).setEncryptionManager(mockEncypter);
        when(mockEncypter.encryptForExternalUse(any(String.class)))
                .thenReturn("Ey+qsh7G2BFGEuqqkzlYRidL4NokGkIgDE+1KOv6aLTmIyrppdVA6WXFIaQ3KsQpKEb9FGUFRqiWorOfhbB2ww==");
        notificationManager.sendPasswordResetEmail(orcidProfile, baseUri);
        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("[ORCID] Password Reset");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_reset_email_body.txt"));
        expected.setText(expectedText);

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    @Rollback
    public void testAmendEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendAmendEmail(orcidProfile, "8888-8888-8888-8880");

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("[ORCID] Record Amended");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_amend_email_body.txt"));
        expected.setText(expectedText);

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    @Rollback
    public void testAddedDelegatesSentCorrectEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        DelegationDetails firstNewDelegate = new DelegationDetails();
        DelegateSummary firstNewDelegateSummary = new DelegateSummary();
        firstNewDelegateSummary.setCreditName(new CreditName("Jimmy Dove"));
        firstNewDelegate.setDelegateSummary(firstNewDelegateSummary);

        DelegationDetails secondNewDelegate = new DelegationDetails();
        DelegateSummary secondNewDelegateSummary = new DelegateSummary();
        secondNewDelegate.setDelegateSummary(secondNewDelegateSummary);

        notificationManager.sendNotificationToAddedDelegate(orcidProfile, Arrays.asList(new DelegationDetails[] { firstNewDelegate }));

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("[ORCID] You've been Made a Proxy!");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_added_as_delegate_email.txt"));
        expected.setText(expectedText);

        verify(mailSender, times(1)).send(expected);

        notificationManager.sendNotificationToAddedDelegate(orcidProfile, Arrays.asList(new DelegationDetails[] { firstNewDelegate, secondNewDelegate }));
        // check that the mail sender has been called an additional two times
        // because we've added a second delegate
        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));

    }

    @Test
    @Rollback
    public void testSendDeactivateEmail() throws JAXBException, IOException, URISyntaxException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendOrcidDeactivateEmail(orcidProfile, new URI("http://testserver.orcid.org"));

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("[ORCID] Request to Deactivate Your Orcid Account");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_deactivate_orcid_email.txt"));
        expected.setText(expectedText);

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    @Rollback
    public void testApiCreatedRecordEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendApiRecordCreationEmail(orcidProfile);

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("ORCID - Claim your ORCID Account");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_api_record_creation_email_body.txt"));
        expected.setText(expectedText);

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    @Rollback
    public void testClaimReminderEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendClaimReminderEmail(orcidProfile, 2);

        SimpleMailMessage expected = new SimpleMailMessage();
        expected.setFrom("no_reply@orcid.org");
        expected.setTo("josiah_carberry@brown.edu");
        expected.setSubject("ORCID - Reminder to claim your ORCID Account");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_claim_reminder_email_body.txt"));
        expected.setText(expectedText);

        verify(mailSender, times(1)).send(expected);
    }

    @Test
    public void testChangeEmailAddress() throws Exception {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        Email originalEmail = new Email("original@email.com");
        notificationManager.sendEmailAddressChangedNotification(orcidProfile, originalEmail, new URI("http://testserver.orcid.org"));

        SimpleMailMessage deactivateMessage = new SimpleMailMessage();
        deactivateMessage.setFrom("support@orcid.org");
        deactivateMessage.setTo("original@email.com");
        deactivateMessage.setSubject("ORCID - Your email has been successfully changed");
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_deactivated_email.txt"));
        deactivateMessage.setText(expectedText);

        SimpleMailMessage newMessage = new SimpleMailMessage();
        newMessage.setFrom("no_reply@orcid.org");
        newMessage.setTo("josiah_carberry@brown.edu");
        newMessage.setSubject("[ORCID] Email Verification Required");
        newMessage.setText(IOUtils.toString(getClass().getResourceAsStream("example_verification_email_body.txt")));

        verify(mailSender, times(1)).send(newMessage);
        verify(mailSender, times(1)).send(deactivateMessage);
    }

}
