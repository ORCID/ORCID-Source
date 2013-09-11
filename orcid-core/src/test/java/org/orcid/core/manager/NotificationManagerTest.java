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
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
    public void testSendVerificationEmail() throws JAXBException, IOException, URISyntaxException {
        URI baseUri = new URI("http://testserver.orcid.org");

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendVerificationEmail(orcidProfile, baseUri, orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
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
        notificationManager.sendPasswordResetEmail(
                orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(),orcidProfile, baseUri);
    }

    @Test
    @Rollback
    public void testAmendEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendAmendEmail(orcidProfile, "8888-8888-8888-8880");
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
    }

    @Test
    @Rollback
    public void testSendDeactivateEmail() throws JAXBException, IOException, URISyntaxException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendOrcidDeactivateEmail(orcidProfile, new URI("http://testserver.orcid.org"));
    }

    @Test
    @Rollback
    public void testApiCreatedRecordEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendApiRecordCreationEmail(
                orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(),orcidProfile);
    }

    
    @Test
    public void testSendVerificationReminderEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendVerificationReminderEmail(orcidProfile, orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
    }
    
    @Test
    @Rollback
    public void testClaimReminderEmail() throws JAXBException, IOException, URISyntaxException {

        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendClaimReminderEmail(orcidProfile, 2);
    }


    @Test
    public void testChangeEmailAddress() throws Exception {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        Email originalEmail = new Email("original@email.com");
        notificationManager.sendEmailAddressChangedNotification(orcidProfile, originalEmail, new URI("http://testserver.orcid.org"));
    }

}
