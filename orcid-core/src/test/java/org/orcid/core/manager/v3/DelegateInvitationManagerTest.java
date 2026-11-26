package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.orcid.utils.ExpiringLinkService;
import org.orcid.utils.ExpiringLinkService.ExpiringLinkType;
import org.orcid.utils.ExpiringLinkService.VerificationResult;
import org.orcid.utils.ExpiringLinkService.VerificationStatus;
import com.nimbusds.jwt.JWTClaimsSet;
import org.orcid.core.manager.v3.impl.DelegateInvitationManagerImpl;
import org.orcid.core.manager.v3.impl.DelegateInvitationMessaging;
import org.orcid.persistence.dao.DelegateInvitationDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class DelegateInvitationManagerTest {

    private static final String SENDER = "0000-0000-0000-0006";
    private static final String RECIPIENT = "0000-0000-0000-0003";
    private static final String TOKEN = "signed-delegate-token";

    @Mock
    private DelegateInvitationDao delegateInvitationDao;

    @Mock
    private GivenPermissionToDao givenPermissionToDao;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private DelegateInvitationMessaging delegateInvitationMessaging;

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private ExpiringLinkService expiringLinkService;

    @InjectMocks
    private DelegateInvitationManagerImpl delegateInvitationManager;

    @Before
    public void setUp() throws Exception {
        when(transactionTemplate.execute(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TransactionCallback<?> callback = invocation.getArgument(0);
                return callback.doInTransaction(null);
            }
        });
        when(delegateInvitationDao.lockSender(SENDER)).thenReturn(true);
        when(delegateInvitationDao.findPendingBySenderAndRecipient(SENDER, RECIPIENT)).thenReturn(null);
        when(delegateInvitationDao.hasDelegatePermission(SENDER, RECIPIENT)).thenReturn(false);
        doAnswer(invocation -> {
            DelegateInvitationEntity invitation = invocation.getArgument(0);
            ReflectionTestUtils.setField(invitation, "id", 42L);
            ReflectionTestUtils.setField(invitation, "dateCreated", new Date());
            return null;
        }).when(delegateInvitationDao).persist(any(DelegateInvitationEntity.class));
        when(expiringLinkService.generateExpiringToken("42", 28L * 24 * 60, ExpiringLinkType.DELEGATE_INVITATION)).thenReturn(TOKEN);
        when(expiringLinkService.verifyToken(TOKEN)).thenReturn(verification(VerificationStatus.VALID, System.currentTimeMillis() + 60_000));
    }

    @Test
    public void createInvitationUsesExpiringJwtWithoutPersistingIt() throws Exception {
        DelegateInvitationManager.CreationResult result = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.EMAIL, "recipient@example.org");

        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, result.getStatus());
        DelegateInvitationEntity invitation = result.getInvitation();
        assertEquals(DelegateInvitationEntity.Status.PENDING, invitation.getStatus());
        assertEquals("recipient@example.org", invitation.getEnteredEmail());
        assertNotNull(invitation.getDateCreated());
        verify(delegateInvitationDao).persist(invitation);
        verify(expiringLinkService).generateExpiringToken("42", 28L * 24 * 60, ExpiringLinkType.DELEGATE_INVITATION);
        verify(delegateInvitationMessaging).created(invitation, TOKEN);
    }

    @Test
    public void wrongRecipientCannotRespondAndAcceptanceCannotBeReplayed() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderById(42L)).thenReturn(SENDER);
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.NOT_RECIPIENT,
                delegateInvitationManager.respond(TOKEN, SENDER, DelegateInvitationManager.Decision.ACCEPT));
        assertEquals(DelegateInvitationEntity.Status.PENDING, invitation.getStatus());
        verify(givenPermissionToDao, never()).merge(any(GivenPermissionToEntity.class));

        assertEquals(DelegateInvitationManager.ResponseResult.ACCEPTED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        assertEquals(DelegateInvitationEntity.Status.ACCEPTED, invitation.getStatus());
        verify(givenPermissionToDao).merge(any(GivenPermissionToEntity.class));
        verify(delegateInvitationMessaging).responded(invitation);
        verify(profileEntityManager).updateLastModifed(SENDER);
        verify(profileEntityManager).updateLastModifed(RECIPIENT);
        verify(delegateInvitationDao, Mockito.times(2)).lockSender(SENDER);

        assertEquals(DelegateInvitationManager.ResponseResult.ALREADY_RESPONDED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        verify(givenPermissionToDao).merge(any(GivenPermissionToEntity.class));
        verify(delegateInvitationMessaging).responded(invitation);
    }

    @Test
    public void declineDoesNotCreateDelegatePermission() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderById(42L)).thenReturn(SENDER);
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.DECLINED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.DECLINE));
        assertEquals(DelegateInvitationEntity.Status.DECLINED, invitation.getStatus());
        verify(givenPermissionToDao, never()).merge(any(GivenPermissionToEntity.class));
        verify(delegateInvitationMessaging).responded(invitation);
    }

    @Test
    public void acceptanceLocksSenderBeforeInvitation() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderById(42L)).thenReturn(SENDER);
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.ACCEPTED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));

        InOrder locks = Mockito.inOrder(delegateInvitationDao);
        locks.verify(delegateInvitationDao).lockSender(SENDER);
        locks.verify(delegateInvitationDao).findByIdForUpdate(42L);
    }

    @Test
    public void responseAtOrAfterExpiryExpiresWithoutGrantingPermission() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() - 1));
        invitation.setActionableNotificationId(100L);
        when(delegateInvitationDao.findSenderById(42L)).thenReturn(SENDER);
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);
        when(expiringLinkService.verifyToken(TOKEN)).thenReturn(verification(VerificationStatus.EXPIRED, System.currentTimeMillis() - 1));

        assertEquals(DelegateInvitationManager.ResponseResult.EXPIRED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        assertEquals(DelegateInvitationEntity.Status.EXPIRED, invitation.getStatus());
        verify(givenPermissionToDao, never()).merge(any(GivenPermissionToEntity.class));
        verify(notificationDao).flagAsArchived(RECIPIENT, 100L);
        verify(notificationDao).flagAsNonSendable(RECIPIENT, 100L);
        verify(delegateInvitationMessaging, never()).responded(any());
    }

    @Test
    public void acceptanceChecksDeadlineAfterWaitingForSenderLock() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderById(42L)).thenReturn(SENDER);
        when(delegateInvitationDao.lockSender(SENDER)).thenAnswer(invocation -> {
            ReflectionTestUtils.setField(invitation, "dateCreated", new Date(System.currentTimeMillis() - 29L * 24 * 60 * 60 * 1000));
            return true;
        });
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.EXPIRED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        verify(givenPermissionToDao, never()).merge(any(GivenPermissionToEntity.class));
    }

    @Test
    public void expirationJobTransitionsOnlyDuePendingInvitations() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() - 1));
        invitation.setActionableNotificationId(100L);
        when(delegateInvitationDao.findExpiredPendingIds(any(), any(Integer.class))).thenReturn(Arrays.asList(42L)).thenReturn(Arrays.asList());
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);

        assertEquals(1, delegateInvitationManager.expirePendingInvitations());
        assertEquals(DelegateInvitationEntity.Status.EXPIRED, invitation.getStatus());
        verify(notificationDao).flagAsArchived(RECIPIENT, 100L);
    }

    @Test
    public void expiredPendingInvitationCanBeReissued() {
        DelegateInvitationEntity oldInvitation = pendingInvitation(new Date(System.currentTimeMillis() - 1));
        oldInvitation.setActionableNotificationId(100L);
        when(delegateInvitationDao.findPendingBySenderAndRecipient(SENDER, RECIPIENT)).thenReturn(oldInvitation);

        DelegateInvitationManager.CreationResult result = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null);

        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, result.getStatus());
        assertEquals(DelegateInvitationEntity.Status.EXPIRED, oldInvitation.getStatus());
        assertNotNull(result.getInvitation());
        verify(notificationDao).flagAsArchived(RECIPIENT, 100L);
    }

    @Test
    public void activeInvitationPreventsAnotherInvitation() {
        DelegateInvitationEntity activeInvitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findPendingBySenderAndRecipient(SENDER, RECIPIENT)).thenReturn(activeInvitation);

        DelegateInvitationManager.CreationResult result = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.ORCID, null);

        assertEquals(DelegateInvitationManager.CreationStatus.ACTIVE_INVITATION, result.getStatus());
        assertNull(result.getInvitation());
        verify(delegateInvitationDao, never()).persist(any(DelegateInvitationEntity.class));
        verify(delegateInvitationMessaging, never()).created(any(), any());
    }

    private DelegateInvitationEntity pendingInvitation(Date expiresAt) {
        DelegateInvitationEntity invitation = new DelegateInvitationEntity();
        invitation.setId(42L);
        invitation.setSenderOrcid(SENDER);
        invitation.setRecipientOrcid(RECIPIENT);
        ReflectionTestUtils.setField(invitation, "dateCreated", new Date(expiresAt.getTime() - 28L * 24 * 60 * 60 * 1000));
        invitation.setStatus(DelegateInvitationEntity.Status.PENDING);
        return invitation;
    }

    private VerificationResult verification(VerificationStatus verificationStatus, long expirationMillis) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("42").claim("type", ExpiringLinkType.DELEGATE_INVITATION.name())
                .expirationTime(new Date(expirationMillis)).build();
        return verificationStatus == VerificationStatus.EXPIRED ? VerificationResult.expired(claims) : VerificationResult.valid(claims);
    }
}