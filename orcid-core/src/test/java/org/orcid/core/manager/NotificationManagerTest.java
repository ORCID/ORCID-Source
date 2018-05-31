package org.orcid.core.manager;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.core.adapter.impl.JpaJaxbNotificationAdapterImpl;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.NotificationManagerImpl;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermissions;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.jaxb.model.notification_v2.NotificationType;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.impl.NotificationDaoImpl;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
@Transactional
public class NotificationManagerTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    public static final String ORCID_INTERNAL_FULL_XML = "/orcid-internal-full-message-latest.xml";

    private Unmarshaller unmarshaller;

    @Mock
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Mock
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private NotificationDao mockNotificationDao;

    @Mock
    private MailGunManager mockMailGunManager;
    
    @Mock
    private OrcidOauth2TokenDetailService mockOrcidOauth2TokenDetailService;

    @Mock
    private ProfileEntityCacheManager mockProfileEntityCacheManager;
    
    @Mock
    private EmailManager mockEmailManager;
    
    @Mock
    private ProfileDao mockProfileDao;
    
    @Mock
    private JpaJaxbNotificationAdapter mockNotificationAdapter;
    
    @Resource
    private ProfileDao profileDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private MailGunManager mailGunManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private EmailManager emailManager;

    @Resource
    private JpaJaxbNotificationAdapter notificationAdapter;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
        
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Before
    public void initJaxb() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(notificationManager, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEventDao", profileEventDao);
        TargetProxyHelper.injectIntoProxy(notificationManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "orcidOauth2TokenDetailService", mockOrcidOauth2TokenDetailService);
        when(mockOrcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(true);        
    }
    
    @After
    public void resetMocks() {
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailManager", emailManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileDao", profileDao);        
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", notificationDao);        
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationAdapter", notificationAdapter);
    }

    protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        return TargetProxyHelper.getTargetObject(proxy, targetClass);
    }

    @Test
    public void testSendWelcomeEmail() throws JAXBException, IOException, URISyntaxException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendWelcomeEmail(orcidProfile.getOrcidIdentifier().getPath(),
                orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
    }

    @Test
    public void testSendVerificationEmail() throws JAXBException, IOException, URISyntaxException {
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(ORCID_INTERNAL_FULL_XML));
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        notificationManager.sendVerificationEmail(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
    }

    @Test
    public void testResetEmail() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String primaryEmail = "public_0000-0000-0000-0003@test.orcid.org";
        for (Locale locale : Locale.values()) {
            profileEntityManager.updateLocale(userOrcid, locale);
            EncryptionManager mockEncypter = mock(EncryptionManager.class);
            getTargetObject(notificationManager, NotificationManagerImpl.class).setEncryptionManager(mockEncypter);
            when(mockEncypter.encryptForExternalUse(any(String.class)))
                    .thenReturn("Ey+qsh7G2BFGEuqqkzlYRidL4NokGkIgDE+1KOv6aLTmIyrppdVA6WXFIaQ3KsQpKEb9FGUFRqiWorOfhbB2ww==");
            notificationManager.sendPasswordResetEmail(primaryEmail, userOrcid);
        }
    }

    @Test
    public void testAmendEmail() throws JAXBException, IOException, URISyntaxException {
        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-5555555555555555"));
        when(sourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
        when(sourceManager.retrieveSourceOrcid()).thenReturn("APP-5555555555555555");
        String testOrcid = "0000-0000-0000-0003";

        for (Locale locale : Locale.values()) {
            profileEntityManager.updateLocale(testOrcid, locale);
            NotificationEntity previousNotification = notificationDao.findLatestByOrcid(testOrcid);
            long minNotificationId = previousNotification != null ? previousNotification.getId() : -1;
            
            Notification n = notificationManager.sendAmendEmail(testOrcid, AmendedSection.UNKNOWN, null);
            assertNotNull(n);
            assertTrue(n.getPutCode() > minNotificationId);
            // New notification entity should have been created
            NotificationEntity latestNotification = notificationDao.findLatestByOrcid(testOrcid);
            assertNotNull(latestNotification);
            System.out.println("----------->" + locale.value() + " -> " + minNotificationId + " -> " + latestNotification.getId());
            assertTrue(latestNotification.getId() > minNotificationId);
            assertEquals(NotificationType.AMENDED.name(), latestNotification.getNotificationType());
        }
    }

    @Test
    public void testSendDeactivateEmail() throws JAXBException, IOException, URISyntaxException {
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailManager", mockEmailManager);
        final String orcid = "0000-0000-0000-0003";
        
        ProfileEntity profile = new ProfileEntity(orcid);
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setCreditName("My credit name");
        recordName.setVisibility(Visibility.PUBLIC.name());
        profile.setRecordNameEntity(recordName);
        
        Email email = new Email();
        email.setEmail("test@email.com");
        
        when(mockProfileEntityCacheManager.retrieve(orcid)).thenReturn(profile);
        when(mockEmailManager.findPrimaryEmail(orcid)).thenReturn(email);
        
        for (org.orcid.jaxb.model.common_v2.Locale locale : org.orcid.jaxb.model.common_v2.Locale.values()) {
            profile.setLocale(locale.name());
            notificationManager.sendOrcidDeactivateEmail(orcid);
        }        
    }

    @Test
    public void testApiCreatedRecordEmail() throws JAXBException, IOException, URISyntaxException {
        String userOrcid = "0000-0000-0000-0003";
        String primaryEmail = "public_0000-0000-0000-0003@test.orcid.org";
        for (Locale locale : Locale.values()) {
            profileEntityManager.updateLocale(userOrcid, locale);
            notificationManager.sendApiRecordCreationEmail(primaryEmail, userOrcid);
        }
    }    

    @Test
    public void testClaimReminderEmail() throws JAXBException, IOException, URISyntaxException {
        String userOrcid = "0000-0000-0000-0003";
        for (Locale locale : Locale.values()) {
            profileEntityManager.updateLocale(userOrcid, locale);
            notificationManager.sendClaimReminderEmail(userOrcid, 2);
        }
    }

    @Test
    public void testChangeEmailAddress() throws Exception {
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailManager", mockEmailManager);
        final String orcid = "0000-0000-0000-0003";
        
        ProfileEntity profile = new ProfileEntity(orcid);
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setCreditName("My credit name");
        recordName.setVisibility(Visibility.PUBLIC.name());
        profile.setRecordNameEntity(recordName);
        
        Email email = new Email();
        email.setEmail("test@email.com");
        
        when(mockProfileEntityCacheManager.retrieve(orcid)).thenReturn(profile);
        when(mockEmailManager.findPrimaryEmail(orcid)).thenReturn(email);
        
        for (org.orcid.jaxb.model.common_v2.Locale locale : org.orcid.jaxb.model.common_v2.Locale.values()) {            
            profile.setLocale(locale.name());
            notificationManager.sendEmailAddressChangedNotification(orcid, "new@email.com", "original@email.com");
        }
    }

    @Test
    public void testSendReactivationEmail() throws Exception {
        String userOrcid = "0000-0000-0000-0003";
        String primaryEmail = "public_0000-0000-0000-0003@test.orcid.org";
        String email = "original@email.com";
        for (Locale locale : Locale.values()) {
            profileEntityManager.updateLocale(userOrcid, locale);
            notificationManager.sendReactivationEmail(email, userOrcid);
        }
    }

    @Test
    public void testAdminDelegateRequest() throws JAXBException, IOException, URISyntaxException {
        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-5555555555555555"));
        when(sourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
        when(sourceManager.retrieveSourceOrcid()).thenReturn("APP-5555555555555555");

        for (Locale locale : Locale.values()) {            
            notificationManager.sendDelegationRequestEmail("0000-0000-0000-0003", "0000-0000-0000-0003", "http://test.orcid.org");
        }
    }    

    @Test
    public void deriveEmailFriendlyNameTest() {
        ProfileEntity testProfile = new ProfileEntity("0000-0000-0000-0003");
        assertEquals("ORCID Registry User", notificationManager.deriveEmailFriendlyName(testProfile));
        testProfile.setRecordNameEntity(new RecordNameEntity());
        assertEquals("ORCID Registry User", notificationManager.deriveEmailFriendlyName(testProfile));
        testProfile.getRecordNameEntity().setGivenNames("Given Name");
        assertEquals("Given Name", notificationManager.deriveEmailFriendlyName(testProfile));
        testProfile.getRecordNameEntity().setFamilyName("Family Name");
        assertEquals("Given Name Family Name", notificationManager.deriveEmailFriendlyName(testProfile));
        testProfile.getRecordNameEntity().setCreditName("Credit Name");
        assertEquals("Credit Name", notificationManager.deriveEmailFriendlyName(testProfile));
    }

    /**
     * 0000-0000-0000-0003 Must have notifications enabled
     */
    @Test
    public void sendAcknowledgeMessageToAccountWithNotificationsEnabledTest() throws Exception {
        String clientId = "APP-5555555555555555";
        String orcid = "0000-0000-0000-0003";
        // Mock the notification DAO
        NotificationManagerImpl notificationManagerImpl = getTargetObject(notificationManager, NotificationManagerImpl.class);
        notificationManagerImpl.setNotificationDao(mockNotificationDao);
        notificationManagerImpl.setMailGunManager(mockMailGunManager);
        notificationManagerImpl.sendAcknowledgeMessage(orcid, clientId);
        verify(mockNotificationDao, times(1)).persist(Matchers.any(NotificationEntity.class));
        verify(mockMailGunManager, never()).sendEmail(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString());

        // Rollback mocked
        notificationManagerImpl.setNotificationDao(notificationDao);
        notificationManagerImpl.setMailGunManager(mailGunManager);
    }

    /**
     * Test independent of spring context, sets up NotificationManager with
     * mocked notifiation dao and notification adapter
     */
    @Test
    public void testFindPermissionsByOrcidAndClient() {
        List<Notification> notificationPermissions = IntStream.range(0, 10).mapToObj(i -> new NotificationPermission()).collect(Collectors.toList());

        NotificationDao notificationDao = mock(NotificationDaoImpl.class);
        JpaJaxbNotificationAdapter adapter = mock(JpaJaxbNotificationAdapterImpl.class);
        when(notificationDao.findPermissionsByOrcidAndClient(anyString(), anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<NotificationEntity>());
        when(adapter.toNotification(Matchers.<ArrayList<NotificationEntity>> any())).thenReturn(notificationPermissions);

        NotificationManager notificationManager = new NotificationManagerImpl();
        ReflectionTestUtils.setField(notificationManager, "notificationAdapter", adapter);
        ReflectionTestUtils.setField(notificationManager, "notificationDao", notificationDao);

        NotificationPermissions notifications = notificationManager.findPermissionsByOrcidAndClient("some-orcid", "some-client", 0,
                OrcidApiConstants.MAX_NOTIFICATIONS_AVAILABLE);

        assertEquals(notificationPermissions.size(), notifications.getNotifications().size());
    }    
    
    @Test
    public void filterActionedNotificationAlertsTest() {
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", mockNotificationDao);
        when(mockNotificationDao.findByOricdAndId(Matchers.anyString(), Matchers.anyLong())).thenReturn(null);
        List<Notification> notifications = IntStream.range(0, 10).mapToObj(new IntFunction<Notification> () {
            @Override
            public Notification apply(int value) {
                if(value % 3 == 0) {
                    NotificationInstitutionalConnection n = new NotificationInstitutionalConnection();
                    n.setSource(new Source("0000-0000-0000-0000"));
                    n.setPutCode(Long.valueOf(value));
                    return n;
                } else {
                    NotificationPermission n = new NotificationPermission();
                    n.setPutCode(Long.valueOf(value));
                    return n;
                }
            }            
        }).collect(Collectors.toList());                
        
        assertEquals(10, notifications.size());
        notifications = notificationManager.filterActionedNotificationAlerts(notifications, "some-orcid");
        assertEquals(6, notifications.size());
        for(Notification n : notifications) {
            assertEquals(NotificationType.PERMISSION, n.getNotificationType());
            assertNotNull(n.getPutCode());
            assertThat(n.getPutCode(), not(anyOf(is(Long.valueOf(0)), is(Long.valueOf(3)), is(Long.valueOf(6)), is(Long.valueOf(9)))));
        }                
    }
    
    @Test
    public void createPermissionNotificationTest() {
        String orcid = "0000-0000-0000-0003";
        NotificationPermission notification = new NotificationPermission();
        notification.setAuthorizationUrl(new AuthorizationUrl("http://test.orcid.org"));
        Notification result = notificationManager.createPermissionNotification(orcid, notification);
        assertNotNull(result.getPutCode());
    }
    
    @Test
    public void sendAcknowledgeMessageTest() throws Exception {
        String clientId = "APP-5555555555555555";
        String orcidNever = "0000-0000-0000-0002";
        String orcidDaily = "0000-0000-0000-0003";
        
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", mockNotificationDao);
        
        // Should not generate the notification
        notificationManager.sendAcknowledgeMessage(orcidNever, clientId);
        verify(mockNotificationDao, never()).persist(Matchers.any());
        
        // Should generate the notification
        notificationManager.sendAcknowledgeMessage(orcidDaily, clientId);
        verify(mockNotificationDao, times(1)).persist(Matchers.any());
        
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", notificationDao);
    }
    
}
