/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.NotificationManagerImpl;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.notification.amended_rc2.AmendedSection;
import org.orcid.jaxb.model.notification.custom_rc2.NotificationCustom;
import org.orcid.jaxb.model.notification_rc2.Notification;
import org.orcid.jaxb.model.notification_rc2.NotificationType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificationManagerTest extends BaseTest {

    public static final String ORCID_INTERNAL_FULL_XML = "/orcid-internal-full-message-latest.xml";

    private Unmarshaller unmarshaller;

    @Mock
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Mock
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Mock
    private SourceManager sourceManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private NotificationDao notificationDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Before
    public void initJaxb() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Before
    public void initMocks() throws Exception {
        NotificationManagerImpl notificationManagerImpl = getTargetObject(notificationManager, NotificationManagerImpl.class);
        notificationManagerImpl.setEncryptionManager(encryptionManager);
        notificationManagerImpl.setProfileEventDao(profileEventDao);
        notificationManagerImpl.setSourceManager(sourceManager);
    }

    @Test
    public void test01SendWelcomeEmail() throws JAXBException, IOException, URISyntaxException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendWelcomeEmail(orcidProfile, orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
    }

    @Test
    public void test02SendVerificationEmail() throws JAXBException, IOException, URISyntaxException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendVerificationEmail(orcidProfile, orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
    }

    @Test
    public void test03ResetEmail() throws Exception {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            orcidProfile.setPassword("r$nd0m");
            EncryptionManager mockEncypter = mock(EncryptionManager.class);
            getTargetObject(notificationManager, NotificationManagerImpl.class).setEncryptionManager(mockEncypter);
            when(mockEncypter.encryptForExternalUse(any(String.class))).thenReturn(
                    "Ey+qsh7G2BFGEuqqkzlYRidL4NokGkIgDE+1KOv6aLTmIyrppdVA6WXFIaQ3KsQpKEb9FGUFRqiWorOfhbB2ww==");
            notificationManager.sendPasswordResetEmail(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), orcidProfile);
        }
    }

    private OrcidProfile getProfile(Locale locale) throws JAXBException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        orcidProfile.getOrcidPreferences().setLocale(locale);
        return orcidProfile;
    }

    @Test
    public void test04AmendEmail() throws JAXBException, IOException, URISyntaxException {
        when(sourceManager.retrieveSourceOrcid()).thenReturn("8888-8888-8888-8880");
        String testOrcid = "4444-4444-4444-4446";
        ProfileEntity testProfile = new ProfileEntity(testOrcid);
        testProfile.setEnableNotifications(true);
        profileDao.merge(testProfile);
        createClient();
        for (Locale locale : Locale.values()) {
            NotificationEntity previousNotification = notificationDao.findLatestByOrcid(testOrcid);
            long minNotificationId = previousNotification != null ? previousNotification.getId() : -1;
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendAmendEmail(orcidProfile, AmendedSection.UNKNOWN);
            // New notification entity should have been created
            NotificationEntity latestNotification = notificationDao.findLatestByOrcid(testOrcid);
            assertNotNull(latestNotification);
            assertTrue(latestNotification.getId() > minNotificationId);
            assertEquals(NotificationType.AMENDED, latestNotification.getNotificationType());
        }
    }

    private void createClient() {
		ClientDetailsEntity clientEntity = new ClientDetailsEntity();
		clientEntity.setId("8888-8888-8888-8880");
		clientEntity.setGroupProfileId("4444-4444-4444-4446");
		clientEntity.setClientName("Test Name");
		clientDetailsDao.persist(clientEntity);
	}

	@Test
    public void test05AddedDelegatesSentCorrectEmail() throws JAXBException, IOException, URISyntaxException {
        String delegateOrcid = "1234-5678-1234-5678";
        ProfileEntity delegateProfileEntity = new ProfileEntity(delegateOrcid);
        EmailEntity delegateEmail = new EmailEntity();
        delegateEmail.setId("jimmy@dove.com");
        delegateEmail.setVisibility(Visibility.PRIVATE);
        delegateEmail.setCurrent(true);
        delegateEmail.setVerified(true);
        delegateProfileEntity.setPrimaryEmail(delegateEmail);
        delegateProfileEntity.setSendChangeNotifications(true);
        profileDao.merge(delegateProfileEntity);

        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            DelegationDetails firstNewDelegate = new DelegationDetails();
            DelegateSummary firstNewDelegateSummary = new DelegateSummary();
            firstNewDelegateSummary.setCreditName(new CreditName("Jimmy Dove"));
            firstNewDelegate.setDelegateSummary(firstNewDelegateSummary);
            firstNewDelegateSummary.setOrcidIdentifier(new OrcidIdentifier(delegateOrcid));

            DelegationDetails secondNewDelegate = new DelegationDetails();
            DelegateSummary secondNewDelegateSummary = new DelegateSummary();
            secondNewDelegate.setDelegateSummary(secondNewDelegateSummary);

            notificationManager.sendNotificationToAddedDelegate(orcidProfile, Arrays.asList(new DelegationDetails[] { firstNewDelegate }));
        }
    }

    @Test
    public void test06SendDeactivateEmail() throws JAXBException, IOException, URISyntaxException {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendOrcidDeactivateEmail(orcidProfile);

        }
    }

    @Test
    public void test07ApiCreatedRecordEmail() throws JAXBException, IOException, URISyntaxException {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendApiRecordCreationEmail(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), orcidProfile);
        }
    }

    @Test
    public void test08SendServiceAnnouncement_1_For_2015() throws JAXBException, IOException, URISyntaxException {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendServiceAnnouncement_1_For_2015(orcidProfile);
        }
    }

    @Test
    public void test09SendVerificationReminderEmail() throws JAXBException, IOException, URISyntaxException {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendVerificationReminderEmail(orcidProfile, orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        }
    }

    @Test
    public void test10ClaimReminderEmail() throws JAXBException, IOException, URISyntaxException {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendClaimReminderEmail(orcidProfile, 2);
        }
    }

    @Test
    public void test11ChangeEmailAddress() throws Exception {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            Email originalEmail = new Email("original@email.com");
            notificationManager.sendEmailAddressChangedNotification(orcidProfile, originalEmail);
        }
    }

    @Test
    public void test12AdminDelegateRequest() throws JAXBException, IOException, URISyntaxException {
        for (Locale locale : Locale.values()) {
            OrcidProfile orcidProfile = getProfile(locale);
            notificationManager.sendDelegationRequestEmail(orcidProfile, orcidProfile, "http://test.orcid.org");
        }
    }

    @Test
    public void test13CreateCustomNotification() {
        String testOrcid = "4444-4444-4444-4446";
        ProfileEntity testProfile = new ProfileEntity(testOrcid);
        profileDao.merge(testProfile);
        NotificationCustom notification = new NotificationCustom();
        notification.setSubject("Test subject");
        notification.setLang("en-gb");
        Notification result = notificationManager.createNotification(testOrcid, notification);
        assertNotNull(result);
        assertTrue(result instanceof NotificationCustom);
        NotificationCustom customResult = (NotificationCustom) result;
        assertEquals("Test subject", customResult.getSubject());
        assertEquals("en-gb", customResult.getLang());
    }

}
