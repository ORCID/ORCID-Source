package org.orcid.core.manager.v3;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.adapter.v3.impl.JpaJaxbNotificationAdapterImpl;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.impl.NotificationManagerImpl;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermissions;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.dao.impl.NotificationDaoImpl;
import org.orcid.persistence.jpa.entities.*;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
@Transactional
public class NotificationManagerTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    @Mock
    private ProfileEventDao profileEventDao;

    @Mock
    private SourceManager mockSourceManager;

    @Mock
    private NotificationDao mockNotificationDao;

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

    @Resource(name = "profileDao")
    private ProfileDao profileDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;

    @Resource(name = "jpaJaxbNotificationAdapterV3")
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

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
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(notificationManager, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEventDao", profileEventDao);
        TargetProxyHelper.injectIntoProxy(notificationManager, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "orcidOauth2TokenDetailService", mockOrcidOauth2TokenDetailService);
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailManager", mockEmailManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileDao", mockProfileDao);
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", mockNotificationDao);
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationAdapter", mockNotificationAdapter);
        when(mockOrcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(true);
    }

    @After
    public void after() {
        resetMocks();
        TargetProxyHelper.injectIntoProxy(notificationManager, "sourceManager", sourceManager);
    }
    
    public void resetMocks() {
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailManager", emailManager);
        TargetProxyHelper.injectIntoProxy(notificationManager, "profileDao", profileDao);
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", notificationDao);
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationAdapter", notificationAdapter);
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
    public void testAmendEmail() throws JAXBException, IOException, URISyntaxException {
        resetMocks();

        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-5555555555555555"));
        when(mockSourceManager.retrieveActiveSourceEntity()).thenReturn(sourceEntity);
        when(mockSourceManager.retrieveActiveSourceId()).thenReturn("APP-5555555555555555");
        String testOrcid = "0000-0000-0000-0003";

        for (AvailableLocales locale : AvailableLocales.values()) {
            NotificationEntity previousNotification = notificationDao.findLatestByOrcid(testOrcid);
            long minNotificationId = previousNotification != null ? previousNotification.getId() : -1;
            profileEntityManager.updateLocale(testOrcid, locale);
            Notification n = notificationManager.sendAmendEmail(testOrcid, AmendedSection.UNKNOWN, Collections.emptyList());
            assertNotNull(n);
            assertTrue(n.getPutCode() > minNotificationId);
        }
    }

    @Test
    public void testAddedDelegatesSentCorrectEmail() throws JAXBException, IOException, URISyntaxException {
        final String orcid = "0000-0000-0000-0003";
        String delegateOrcid = "0000-0000-0000-0002";

        ProfileEntity profile = new ProfileEntity();
        
        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-5555555555555555"));
        when(mockSourceManager.retrieveActiveSourceEntity()).thenReturn(sourceEntity);
        when(mockSourceManager.retrieveActiveSourceId()).thenReturn("APP-5555555555555555");
        when(mockNotificationAdapter.toNotificationEntity(Mockito.any(Notification.class))).thenReturn(new NotificationCustomEntity());

        Email email = new Email();
        email.setEmail("test@email.com");

        Email delegateEmail = new Email();
        delegateEmail.setEmail("delegate@email.com");

        when(mockProfileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                profile.setId(invocation.getArgument(0));
                return profile;
            }

        });

        when(mockProfileDao.find(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                profile.setId(invocation.getArgument(0));
                return profile;
            }

        });

        when(mockEmailManager.findPrimaryEmail(orcid)).thenReturn(email);
        when(mockEmailManager.findPrimaryEmail(delegateOrcid)).thenReturn(delegateEmail);

        for (org.orcid.jaxb.model.common_v2.Locale locale : org.orcid.jaxb.model.common_v2.Locale.values()) {
            profile.setLocale(locale.name());
            notificationManager.sendNotificationToAddedDelegate("0000-0000-0000-0003", delegateOrcid);
        }
    }

    @Test
    public void testAdminDelegateRequest() throws JAXBException, IOException, URISyntaxException {
        resetMocks();
        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-5555555555555555"));
        when(mockSourceManager.retrieveActiveSourceEntity()).thenReturn(sourceEntity);
        when(mockSourceManager.retrieveActiveSourceId()).thenReturn("APP-5555555555555555");

        notificationManager.sendDelegationRequestEmail("0000-0000-0000-0003", "0000-0000-0000-0003", "http://test.orcid.org");
    }

    /**
     * 0000-0000-0000-0003 Must have notifications enabled
     */
    @Test
    public void sendAcknowledgeMessageToAccountWithNotificationsEnabledTest() throws Exception {
        when(mockNotificationAdapter.toNotificationEntity(Mockito.any(Notification.class))).thenReturn(new NotificationInstitutionalConnectionEntity());

        String clientId = "APP-5555555555555555";
        String orcid = "0000-0000-0000-0003";
        // Mock the notification DAO
        NotificationManagerImpl notificationManagerImpl = getTargetObject(notificationManager, NotificationManagerImpl.class);
        notificationManagerImpl.setNotificationDao(mockNotificationDao);
        notificationManagerImpl.sendAcknowledgeMessage(orcid, clientId);
        verify(mockNotificationDao, times(1)).persist(Matchers.any(NotificationEntity.class));

        // Rollback mocked
        notificationManagerImpl.setNotificationDao(notificationDao);        
    }

    /**
     * 0000-0000-0000-0002 Must have notifications disabled
     */
    @Test
    public void sendAcknowledgeMessageToAccountWithNotificationsDisabledTest() throws Exception {
        resetMocks();
        String clientId = "APP-5555555555555555";
        String orcid = "0000-0000-0000-0002";
        notificationManager.sendAcknowledgeMessage(orcid, clientId);
        verify(mockNotificationDao, never()).persist(Matchers.any(NotificationEntity.class));
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
        List<Notification> notifications = IntStream.range(0, 10).mapToObj(new IntFunction<Notification>() {
            @Override
            public Notification apply(int value) {
                if (value % 3 == 0) {
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
        for (Notification n : notifications) {
            assertEquals(NotificationType.PERMISSION, n.getNotificationType());
            assertNotNull(n.getPutCode());
            assertThat(n.getPutCode(), not(anyOf(is(Long.valueOf(0)), is(Long.valueOf(3)), is(Long.valueOf(6)), is(Long.valueOf(9)))));
        }
    }

    @Test
    public void createPermissionNotificationTest() {
        resetMocks();
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

        when(mockNotificationAdapter.toNotificationEntity(Mockito.any(Notification.class))).thenReturn(new NotificationInstitutionalConnectionEntity());

        // Should generate the notification ignoring the emailFrequency
        notificationManager.sendAcknowledgeMessage(orcidNever, clientId);
        // Should generate the notification
        notificationManager.sendAcknowledgeMessage(orcidDaily, clientId);
        verify(mockNotificationDao, times(2)).persist(Matchers.any());

        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationDao", notificationDao);
    }

    @Test
    public void testDeleteNotificationsForRecord() {
        ReflectionTestUtils.setField(notificationManager, "notificationDao", mockNotificationDao);
        Mockito.when(mockNotificationDao.deleteNotificationsForRecord(Mockito.eq("orcid"), Mockito.eq(NotificationManagerImpl.DELETE_BATCH_SIZE))).thenReturn(true)
                .thenReturn(true).thenReturn(true).thenReturn(false);

        notificationManager.deleteNotificationsForRecord("orcid");
        Mockito.verify(mockNotificationDao, Mockito.times(4)).deleteNotificationsForRecord(Mockito.eq("orcid"), Mockito.eq(NotificationManagerImpl.DELETE_BATCH_SIZE));

        ReflectionTestUtils.setField(notificationManager, "notificationDao", notificationDao);
    }

    @Test
    public void testAutoArchiveNotifications() {
        ReflectionTestUtils.setField(notificationManager, "notificationDao", mockNotificationDao);
        when(mockNotificationDao.archiveNotificationsCreatedBefore(Mockito.any(Date.class), Mockito.anyInt())).thenReturn(1)
                .thenReturn(2).thenReturn(3).thenReturn(0).thenReturn(4);

        notificationManager.autoArchiveNotifications();
        // The method will be invoked 4 times, when it get 1, 2, 3 and then 0 on the last call
        Mockito.verify(mockNotificationDao, Mockito.times(4)).archiveNotificationsCreatedBefore(Mockito.any(), Mockito.anyInt());
        ReflectionTestUtils.setField(notificationManager, "notificationDao", notificationDao);
    }

    @Test
    public void testAutoDeleteNotifications() {
        ReflectionTestUtils.setField(notificationManager, "notificationDao", mockNotificationDao);
        when(mockNotificationDao.deleteNotificationsCreatedBefore(Mockito.any(Date.class), Mockito.anyInt())).thenReturn(1)
                .thenReturn(2).thenReturn(3).thenReturn(0).thenReturn(4);

        notificationManager.autoDeleteNotifications();
        // The method will be invoked 4 times, when it get 1, 2, 3 and then 0 on the last call
        Mockito.verify(mockNotificationDao, Mockito.times(4)).deleteNotificationsCreatedBefore(Mockito.any(), Mockito.anyInt());
        ReflectionTestUtils.setField(notificationManager, "notificationDao", notificationDao);
    }
                   
}
