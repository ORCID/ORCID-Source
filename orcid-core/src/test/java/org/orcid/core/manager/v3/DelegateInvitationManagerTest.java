package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.orcid.core.manager.v3.impl.DelegateInvitationManagerImpl;
import org.orcid.core.manager.v3.impl.DelegateInvitationMessaging;
import org.orcid.persistence.dao.DelegateInvitationDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(MockitoJUnitRunner.class)
public class DelegateInvitationManagerTest {

    private static final String SENDER = "0000-0000-0000-0006";
    private static final String RECIPIENT = "0000-0000-0000-0003";
    private static final String TOKEN = "7e7d2ab1-0cf9-4c65-99ed-1809af5d5e75";

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

    @InjectMocks
    private DelegateInvitationManagerImpl delegateInvitationManager;

    @Before
    public void setUp() {
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
    }

    @Test
    public void createInvitationSetsExactTwentyEightDayExpiry() {
        DelegateInvitationManager.CreationResult result = delegateInvitationManager.createInvitation(SENDER, RECIPIENT,
                DelegateInvitationEntity.LookupType.EMAIL, "recipient@example.org");

        assertEquals(DelegateInvitationManager.CreationStatus.CREATED, result.getStatus());
        DelegateInvitationEntity invitation = result.getInvitation();
        assertNotNull(invitation.getInvitationToken());
        assertEquals(DelegateInvitationEntity.Status.PENDING, invitation.getStatus());
        assertEquals("recipient@example.org", invitation.getEnteredEmail());
        assertEquals(28L * 24 * 60 * 60 * 1000, invitation.getExpiresAt().getTime() - invitation.getSentDate().getTime());
        verify(delegateInvitationDao).persist(invitation);
        verify(delegateInvitationMessaging).created(invitation);
    }

    @Test
    public void wrongRecipientCannotRespondAndAcceptanceCannotBeReplayed() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderByToken(TOKEN)).thenReturn(SENDER);
        when(delegateInvitationDao.findByTokenForUpdate(TOKEN)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.NOT_RECIPIENT,
                delegateInvitationManager.respond(TOKEN, SENDER, DelegateInvitationManager.Decision.ACCEPT));
        assertEquals(DelegateInvitationEntity.Status.PENDING, invitation.getStatus());
        verify(givenPermissionToDao, never()).persist(any(GivenPermissionToEntity.class));

        assertEquals(DelegateInvitationManager.ResponseResult.ACCEPTED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        assertEquals(DelegateInvitationEntity.Status.ACCEPTED, invitation.getStatus());
        verify(givenPermissionToDao).persist(any(GivenPermissionToEntity.class));
        verify(delegateInvitationMessaging).responded(invitation);
        verify(profileEntityManager).updateLastModifed(SENDER);
        verify(profileEntityManager).updateLastModifed(RECIPIENT);
        verify(delegateInvitationDao, Mockito.times(2)).lockSender(SENDER);

        assertEquals(DelegateInvitationManager.ResponseResult.ALREADY_RESPONDED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        verify(givenPermissionToDao).persist(any(GivenPermissionToEntity.class));
        verify(delegateInvitationMessaging).responded(invitation);
    }

    @Test
    public void declineDoesNotCreateDelegatePermission() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderByToken(TOKEN)).thenReturn(SENDER);
        when(delegateInvitationDao.findByTokenForUpdate(TOKEN)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.DECLINED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.DECLINE));
        assertEquals(DelegateInvitationEntity.Status.DECLINED, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        verify(givenPermissionToDao, never()).persist(any(GivenPermissionToEntity.class));
        verify(delegateInvitationMessaging).responded(invitation);
    }

    @Test
    public void acceptanceLocksSenderBeforeInvitation() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderByToken(TOKEN)).thenReturn(SENDER);
        when(delegateInvitationDao.findByTokenForUpdate(TOKEN)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.ACCEPTED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));

        InOrder locks = Mockito.inOrder(delegateInvitationDao);
        locks.verify(delegateInvitationDao).lockSender(SENDER);
        locks.verify(delegateInvitationDao).findByTokenForUpdate(TOKEN);
    }

    @Test
    public void responseAtOrAfterExpiryExpiresWithoutGrantingPermission() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() - 1));
        invitation.setActionableNotificationId(100L);
        when(delegateInvitationDao.findSenderByToken(TOKEN)).thenReturn(SENDER);
        when(delegateInvitationDao.findByTokenForUpdate(TOKEN)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.EXPIRED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        assertEquals(DelegateInvitationEntity.Status.EXPIRED, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        verify(givenPermissionToDao, never()).persist(any(GivenPermissionToEntity.class));
        verify(notificationDao).flagAsArchived(RECIPIENT, 100L);
        verify(notificationDao).flagAsNonSendable(RECIPIENT, 100L);
        verify(delegateInvitationMessaging, never()).responded(any());
    }

    @Test
    public void acceptanceChecksDeadlineAfterWaitingForSenderLock() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() + 60_000));
        when(delegateInvitationDao.findSenderByToken(TOKEN)).thenReturn(SENDER);
        when(delegateInvitationDao.lockSender(SENDER)).thenAnswer(invocation -> {
            invitation.setExpiresAt(new Date(System.currentTimeMillis() - 1));
            return true;
        });
        when(delegateInvitationDao.findByTokenForUpdate(TOKEN)).thenReturn(invitation);

        assertEquals(DelegateInvitationManager.ResponseResult.EXPIRED,
                delegateInvitationManager.respond(TOKEN, RECIPIENT, DelegateInvitationManager.Decision.ACCEPT));
        verify(givenPermissionToDao, never()).persist(any(GivenPermissionToEntity.class));
    }

    @Test
    public void expirationJobTransitionsOnlyDuePendingInvitations() {
        DelegateInvitationEntity invitation = pendingInvitation(new Date(System.currentTimeMillis() - 1));
        invitation.setActionableNotificationId(100L);
        when(delegateInvitationDao.findExpiredPendingIds(any(), any(Integer.class))).thenReturn(Arrays.asList(42L)).thenReturn(Arrays.asList());
        when(delegateInvitationDao.findByIdForUpdate(42L)).thenReturn(invitation);

        assertEquals(1, delegateInvitationManager.expirePendingInvitations());
        assertEquals(DelegateInvitationEntity.Status.EXPIRED, invitation.getStatus());
        assertTrue(invitation.getRespondedAt().getTime() >= invitation.getExpiresAt().getTime());
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
        verify(delegateInvitationMessaging, never()).created(any());
    }

    private DelegateInvitationEntity pendingInvitation(Date expiresAt) {
        DelegateInvitationEntity invitation = new DelegateInvitationEntity();
        invitation.setId(42L);
        invitation.setInvitationToken(TOKEN);
        invitation.setSenderOrcid(SENDER);
        invitation.setRecipientOrcid(RECIPIENT);
        invitation.setLookupType(DelegateInvitationEntity.LookupType.ORCID);
        invitation.setSentDate(new Date(expiresAt.getTime() - 28L * 24 * 60 * 60 * 1000));
        invitation.setExpiresAt(expiresAt);
        invitation.setStatus(DelegateInvitationEntity.Status.PENDING);
        return invitation;
    }
}