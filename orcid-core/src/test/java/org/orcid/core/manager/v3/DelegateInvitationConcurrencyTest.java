package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assume;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.dao.DelegateInvitationDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.ExpiringLinkService;
import org.orcid.utils.ExpiringLinkService.ExpiringLinkType;
import com.nimbusds.jose.JOSEException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-orcid-core-context.xml"})
public class DelegateInvitationConcurrencyTest extends DBUnitTest {

    private static final String SENDER = "0000-0000-0000-0006";
    private static final String RECIPIENT = "0000-0000-0000-0007";

    @Resource
    private DelegateInvitationManager delegateInvitationManager;

    @Resource
    private DelegateInvitationDao delegateInvitationDao;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ExpiringLinkService expiringLinkService;

    @Resource(name = "pooledDataSource")
    private DataSource dataSource;

    private Set<Long> existingNotifications;

    @BeforeClass
    public static void initData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Before
    public void rememberNotifications() {
        existingNotifications = notificationIds();
    }

    @After
    public void removeInvitations() {
        for (DelegateInvitationEntity invitation : delegateInvitationDao.getAll()) {
            delegateInvitationDao.remove(invitation.getId());
        }
        for (Long id : notificationIds()) {
            if (!existingNotifications.contains(id)) {
                notificationDao.deleteNotificationById(id);
            }
        }
    }

    @Test
    public void concurrentSendsCreateOnlyOnePendingInvitation() throws Exception {
        // Duplicate pending invitations are blocked by a PostgreSQL partial unique index, which HSQLDB cannot provide.
        try (java.sql.Connection connection = dataSource.getConnection()) {
            Assume.assumeTrue("Requires PostgreSQL partial unique index",
                    connection.getMetaData().getDatabaseProductName().toLowerCase().contains("postgres"));
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<DelegateInvitationManager.CreationResult> first = executor.submit(() -> {
                start.await();
                return delegateInvitationManager.createInvitation(SENDER, RECIPIENT, DelegateInvitationEntity.LookupType.ORCID, null);
            });
            Future<DelegateInvitationManager.CreationResult> second = executor.submit(() -> {
                start.await();
                return delegateInvitationManager.createInvitation(SENDER, RECIPIENT, DelegateInvitationEntity.LookupType.EMAIL, "recipient@example.org");
            });
            start.countDown();

            Set<DelegateInvitationManager.CreationStatus> results = new HashSet<>();
            results.add(first.get(30, TimeUnit.SECONDS).getStatus());
            results.add(second.get(30, TimeUnit.SECONDS).getStatus());
            assertEquals("Unexpected concurrent send outcomes: " + results, 2, results.size());
            assertTrue(results.contains(DelegateInvitationManager.CreationStatus.CREATED));
            assertTrue(results.contains(DelegateInvitationManager.CreationStatus.ACTIVE_INVITATION));
            assertEquals(1L, delegateInvitationDao.countAll().longValue());
            assertEquals(2, notificationIds().size() - existingNotifications.size());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void declinedInvitationCanBeReissued() throws Exception {
        DelegateInvitationEntity first = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null).getInvitation();
        assertEquals(DelegateInvitationManager.ResponseResult.DECLINED,
                delegateInvitationManager.respond(invitationToken(first.getId()), RECIPIENT, DelegateInvitationManager.Decision.DECLINE));

        DelegateInvitationManager.CreationResult second = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.EMAIL, "recipient@example.org");

        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, second.getStatus());
        assertNotNull(second.getInvitation());
        assertFalse(first.getId().equals(second.getInvitation().getId()));
        assertEquals(DelegateInvitationEntity.Status.DECLINED, delegateInvitationDao.find(first.getId()).getStatus());
    }

    @Test
    public void concurrentDeclinesOnlyTransitionOnce() throws Exception {
        DelegateInvitationManager.CreationResult created = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null);
        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, created.getStatus());
        String token = invitationToken(created.getInvitation().getId());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<DelegateInvitationManager.ResponseResult> first = executor.submit(() -> {
                start.await();
                return delegateInvitationManager.respond(token, RECIPIENT, DelegateInvitationManager.Decision.DECLINE);
            });
            Future<DelegateInvitationManager.ResponseResult> second = executor.submit(() -> {
                start.await();
                return delegateInvitationManager.respond(token, RECIPIENT, DelegateInvitationManager.Decision.DECLINE);
            });
            start.countDown();
            Set<DelegateInvitationManager.ResponseResult> results = new HashSet<>();
            results.add(first.get(30, TimeUnit.SECONDS));
            results.add(second.get(30, TimeUnit.SECONDS));
            assertEquals("Unexpected concurrent response outcomes: " + results, 2, results.size());
            assertTrue(results.contains(DelegateInvitationManager.ResponseResult.DECLINED));
            assertTrue(results.contains(DelegateInvitationManager.ResponseResult.ALREADY_RESPONDED));
            assertEquals(4, notificationIds().size() - existingNotifications.size());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void acceptanceRacingExpiryNeverCreatesPermission() throws Exception {
        DelegateInvitationManager.CreationResult created = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null);
        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, created.getStatus());
        DelegateInvitationEntity invitation = created.getInvitation();
        ReflectionTestUtils.setField(invitation, "dateCreated", new Date(System.currentTimeMillis() - 29L * 24 * 60 * 60 * 1000));
        delegateInvitationDao.merge(invitation);
        String token = invitationToken(invitation.getId());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<DelegateInvitationManager.ResponseResult> response = executor.submit(() -> {
                start.await();
                return delegateInvitationManager.respond(token, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT);
            });
            Future<Integer> expiration = executor.submit(() -> {
                start.await();
                return delegateInvitationManager.expirePendingInvitations();
            });
            start.countDown();
            DelegateInvitationManager.ResponseResult result = response.get(30, TimeUnit.SECONDS);
            int expiredByJob = expiration.get(30, TimeUnit.SECONDS);
            assertTrue(result == DelegateInvitationManager.ResponseResult.EXPIRED || result == DelegateInvitationManager.ResponseResult.ALREADY_RESPONDED);
            assertEquals(result == DelegateInvitationManager.ResponseResult.EXPIRED ? 0 : 1, expiredByJob);
            assertEquals(DelegateInvitationEntity.Status.EXPIRED, delegateInvitationDao.find(invitation.getId()).getStatus());
            NotificationEntity notification = notificationDao.find(invitation.getActionableNotificationId());
            assertTrue(notification.getArchivedDate() != null);
            assertTrue(!notification.isSendable());
            assertEquals(null, givenPermissionToDao.findByGiverAndReceiverOrcid(SENDER, RECIPIENT));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void rollbackRemovesInvitationAndBothNotifications() {
        transactionTemplate.execute(status -> {
            DelegateInvitationManager.CreationResult result = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                    DelegateInvitationEntity.LookupType.EMAIL, "recipient@example.org");
            assertEquals(DelegateInvitationManager.CreationStatus.CREATED, result.getStatus());
            status.setRollbackOnly();
            return null;
        });
        assertEquals(0L, delegateInvitationDao.countAll().longValue());
        assertEquals(existingNotifications, notificationIds());
    }

    @Test
    public void rolledBackResponseCanBeRetriedWithoutDuplicateMessages() throws Exception {
        DelegateInvitationEntity invitation = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null).getInvitation();
        String token = invitationToken(invitation.getId());
        transactionTemplate.execute(status -> {
            assertEquals(DelegateInvitationManager.ResponseResult.DECLINED,
                    delegateInvitationManager.respond(token, RECIPIENT, DelegateInvitationManager.Decision.DECLINE));
            status.setRollbackOnly();
            return null;
        });
        assertEquals(DelegateInvitationEntity.Status.PENDING, delegateInvitationDao.find(invitation.getId()).getStatus());
        assertEquals(2, notificationIds().size() - existingNotifications.size());
        assertEquals(DelegateInvitationManager.ResponseResult.DECLINED,
                delegateInvitationManager.respond(token, RECIPIENT, DelegateInvitationManager.Decision.DECLINE));
        assertEquals(4, notificationIds().size() - existingNotifications.size());
    }

    private Set<Long> notificationIds() {
        Set<Long> ids = new HashSet<>();
        for (String orcid : Arrays.asList(SENDER, RECIPIENT)) {
            for (NotificationEntity notification : notificationDao.findByOrcid(orcid, true, 0, 1000)) {
                ids.add(notification.getId());
            }
        }
        return ids;
    }

    private String invitationToken(Long invitationId) throws JOSEException {
        return expiringLinkService.generateExpiringToken(invitationId.toString(), 28L * 24 * 60, ExpiringLinkType.DELEGATE_INVITATION);
    }

    @Test
    public void deletingActionableNotificationKeepsInvitationHistory() {
        DelegateInvitationManager.CreationResult created = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null);
        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, created.getStatus());
        Long invitationId = created.getInvitation().getId();
        Long notificationId = created.getInvitation().getActionableNotificationId();
        assertNotNull(notificationId);

        notificationDao.remove(notificationId);

        assertNotNull(delegateInvitationDao.find(invitationId));
        assertNull(delegateInvitationDao.find(invitationId).getActionableNotificationId());
    }

    @Test
    public void trustedIndividualsAreOrderedByApprovalThenOrcid() {
        GivenPermissionToEntity first = new GivenPermissionToEntity();
        first.setGiver(SENDER);
        first.setReceiver(RECIPIENT);
        first.setApprovalDate(new Date());
        GivenPermissionToEntity second = new GivenPermissionToEntity();
        second.setGiver(SENDER);
        second.setReceiver("0000-0000-0000-0008");
        second.setApprovalDate(first.getApprovalDate());
        try {
            transactionTemplate.execute(status -> {
                givenPermissionToDao.persist(second);
                givenPermissionToDao.persist(first);
                givenPermissionToDao.flush();
                return null;
            });
            java.util.List<GivenPermissionToEntity> permissions = givenPermissionToDao.findByGiver(SENDER);
            assertEquals("0000-0000-0000-0003", permissions.get(0).getReceiver());
            assertEquals(RECIPIENT, permissions.get(1).getReceiver());
            assertEquals("0000-0000-0000-0008", permissions.get(2).getReceiver());
        } finally {
            if (first.getId() != null) {
                givenPermissionToDao.remove(first.getId());
            }
            if (second.getId() != null) {
                givenPermissionToDao.remove(second.getId());
            }
        }
    }
}