package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.Resource;

import org.hibernate.exception.ConstraintViolationException;

import org.orcid.core.manager.v3.DelegateInvitationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.persistence.dao.DelegateInvitationDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.utils.ExpiringLinkService;
import org.orcid.utils.ExpiringLinkService.ExpiringLinkType;
import org.orcid.utils.ExpiringLinkService.VerificationResult;
import org.orcid.utils.ExpiringLinkService.VerificationStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.transaction.support.TransactionTemplate;

public class DelegateInvitationManagerImpl implements DelegateInvitationManager {

    private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;
    private static final long MINUTES_PER_DAY = 24L * 60;
    private static final int EXPIRATION_BATCH_SIZE = 200;

    private int invitationLifetimeInDays = 28;

    @Resource
    private DelegateInvitationDao delegateInvitationDao;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private DelegateInvitationMessaging delegateInvitationMessaging;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private ExpiringLinkService expiringLinkService;

    public void setInvitationLifetimeInDays(int invitationLifetimeInDays) {
        if (invitationLifetimeInDays <= 0) {
            throw new IllegalArgumentException("Invitation lifetime must be greater than zero");
        }
        this.invitationLifetimeInDays = invitationLifetimeInDays;
    }

    @Override
    public Eligibility checkEligibility(String senderOrcid, String recipientOrcid) {
        return transactionTemplate.execute(status -> checkEligibilityInTransaction(senderOrcid, recipientOrcid));
    }

    @Override
    public CreationResult createInvitation(String senderOrcid, String recipientOrcid, DelegateInvitationEntity.LookupType lookupType, String enteredEmail) {
        if (senderOrcid == null || recipientOrcid == null || lookupType == null) {
            throw new IllegalArgumentException("Sender, recipient and lookup type are required");
        }
        if (lookupType == DelegateInvitationEntity.LookupType.EMAIL && (enteredEmail == null || enteredEmail.trim().isEmpty())) {
            throw new IllegalArgumentException("Email lookup requires the entered email address");
        }

        try {
            return transactionTemplate.execute(status -> {
                Eligibility eligibility = checkEligibilityInTransaction(senderOrcid, recipientOrcid);
                if (eligibility != Eligibility.AVAILABLE) {
                    return new CreationResult(toCreationStatus(eligibility), null);
                }

                DelegateInvitationEntity invitation = new DelegateInvitationEntity();
                invitation.setSenderOrcid(senderOrcid);
                invitation.setRecipientOrcid(recipientOrcid);
                invitation.setEnteredEmail(lookupType == DelegateInvitationEntity.LookupType.EMAIL ? enteredEmail.trim() : null);
                invitation.setStatus(DelegateInvitationEntity.Status.PENDING);
                invitation.setSenderNoticeAcknowledged(false);
                delegateInvitationDao.persist(invitation);
                delegateInvitationDao.flush();
                delegateInvitationMessaging.created(invitation, createInvitationToken(invitation.getId()));
                return new CreationResult(CreationStatus.CREATED, invitation);
            });
        } catch (RuntimeException e) {
            if (isPendingInvitationConflict(e)) {
                return new CreationResult(CreationStatus.ACTIVE_INVITATION, null);
            }
            throw e;
        }
    }

    @Override
    public ResponseResult respond(String invitationToken, String authenticatedRecipientOrcid, Decision decision) {
        if (invitationToken == null || authenticatedRecipientOrcid == null || decision == null) {
            return ResponseResult.NOT_FOUND;
        }
        VerificationResult verification = expiringLinkService.verifyToken(invitationToken);
        Long invitationId = getInvitationId(verification);
        if (invitationId == null) {
            return ResponseResult.NOT_FOUND;
        }
        return transactionTemplate.execute(status -> {
            String senderOrcid = delegateInvitationDao.findSenderById(invitationId);
            if (senderOrcid == null || !delegateInvitationDao.lockSender(senderOrcid)) {
                return ResponseResult.NOT_FOUND;
            }
            DelegateInvitationEntity invitation = delegateInvitationDao.findByIdForUpdate(invitationId);
            if (invitation == null) {
                return ResponseResult.NOT_FOUND;
            }
            if (!authenticatedRecipientOrcid.equals(invitation.getRecipientOrcid())) {
                return ResponseResult.NOT_RECIPIENT;
            }
            if (invitation.getStatus() != DelegateInvitationEntity.Status.PENDING) {
                return ResponseResult.ALREADY_RESPONDED;
            }

            Date now = new Date();
            if (verification.getStatus() == VerificationStatus.EXPIRED || isExpired(invitation, now)) {
                expire(invitation);
                return ResponseResult.EXPIRED;
            }

            if (decision == Decision.ACCEPT) {
                if (!delegateInvitationDao.hasDelegatePermission(invitation.getSenderOrcid(), invitation.getRecipientOrcid())) {
                    GivenPermissionToEntity permission = new GivenPermissionToEntity();
                    permission.setGiver(invitation.getSenderOrcid());
                    permission.setReceiver(invitation.getRecipientOrcid());
                    permission.setApprovalDate(now);
                    givenPermissionToDao.merge(permission);
                    givenPermissionToDao.flush();
                    profileEntityManager.updateLastModifed(invitation.getSenderOrcid());
                    profileEntityManager.updateLastModifed(invitation.getRecipientOrcid());
                }
                invitation.setStatus(DelegateInvitationEntity.Status.ACCEPTED);
                invitation.setSenderNoticeAcknowledged(false);
                delegateInvitationMessaging.responded(invitation);
                return ResponseResult.ACCEPTED;
            }

            invitation.setStatus(DelegateInvitationEntity.Status.DECLINED);
            invitation.setSenderNoticeAcknowledged(false);
            delegateInvitationMessaging.responded(invitation);
            return ResponseResult.DECLINED;
        });
    }

    @Override
    public List<DelegateInvitationEntity> getUnacknowledgedNotices(String senderOrcid) {
        return transactionTemplate.execute(status -> delegateInvitationDao.findUnacknowledgedNotices(senderOrcid));
    }

    @Override
    public boolean acknowledgeNotice(String senderOrcid, Long invitationId) {
        if (senderOrcid == null || invitationId == null) {
            return false;
        }
        return transactionTemplate.execute(status -> {
            DelegateInvitationEntity invitation = delegateInvitationDao.findByIdForUpdate(invitationId);
            if (invitation == null || !senderOrcid.equals(invitation.getSenderOrcid()) || invitation.getStatus() == DelegateInvitationEntity.Status.PENDING) {
                return false;
            }
            invitation.setSenderNoticeAcknowledged(true);
            return true;
        });
    }

    @Override
    public int expirePendingInvitations() {
        int expiredCount = 0;
        List<Long> expiredIds;
        do {
            Date now = new Date();
            expiredIds = delegateInvitationDao.findExpiredPendingIds(new Date(now.getTime() - invitationLifetimeMillis()), EXPIRATION_BATCH_SIZE);
            for (Long invitationId : expiredIds) {
                if (expireInvitation(invitationId, now)) {
                    expiredCount++;
                }
            }
        } while (!expiredIds.isEmpty());
        return expiredCount;
    }

    private Eligibility checkEligibilityInTransaction(String senderOrcid, String recipientOrcid) {
        if (senderOrcid == null || recipientOrcid == null || senderOrcid.equals(recipientOrcid)) {
            return Eligibility.SELF;
        }
        if (!delegateInvitationDao.lockSender(senderOrcid)) {
            return Eligibility.SELF;
        }

        DelegateInvitationEntity existing = delegateInvitationDao.findPendingBySenderAndRecipient(senderOrcid, recipientOrcid);
        if (existing != null) {
            Date now = new Date();
            if (!isExpired(existing, now)) {
                return Eligibility.ACTIVE_INVITATION;
            }
            expire(existing);
            delegateInvitationDao.flush();
        }
        if (delegateInvitationDao.hasDelegatePermission(senderOrcid, recipientOrcid)) {
            return Eligibility.ALREADY_DELEGATE;
        }
        return Eligibility.AVAILABLE;
    }

    private boolean expireInvitation(Long invitationId, Date now) {
        return transactionTemplate.execute(status -> {
            DelegateInvitationEntity invitation = delegateInvitationDao.findByIdForUpdate(invitationId);
            if (invitation == null || invitation.getStatus() != DelegateInvitationEntity.Status.PENDING || !isExpired(invitation, now)) {
                return false;
            }
            expire(invitation);
            return true;
        });
    }

    private void expire(DelegateInvitationEntity invitation) {
        invitation.setStatus(DelegateInvitationEntity.Status.EXPIRED);
        invitation.setSenderNoticeAcknowledged(false);
        if (invitation.getActionableNotificationId() != null) {
            notificationDao.flagAsArchived(invitation.getRecipientOrcid(), invitation.getActionableNotificationId());
            notificationDao.flagAsNonSendable(invitation.getRecipientOrcid(), invitation.getActionableNotificationId());
        }
    }

    private String createInvitationToken(Long invitationId) {
        try {
            return expiringLinkService.generateExpiringToken(invitationId.toString(), invitationLifetimeInDays * MINUTES_PER_DAY, ExpiringLinkType.DELEGATE_INVITATION);
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to create delegate invitation token", e);
        }
    }

    @Override
    public String findSenderOrcidByToken(String invitationToken, String recipientOrcid) {
        if (invitationToken == null || recipientOrcid == null) {
            return null;
        }
        Long invitationId = getInvitationId(expiringLinkService.verifyToken(invitationToken));
        if (invitationId == null) {
            return null;
        }
        return transactionTemplate.execute(status -> {
            DelegateInvitationEntity invitation = delegateInvitationDao.find(invitationId);
            return invitation != null && recipientOrcid.equals(invitation.getRecipientOrcid()) ? invitation.getSenderOrcid() : null;
        });
    }

    private Long getInvitationId(VerificationResult verification) {
        if (verification == null || verification.getStatus() == VerificationStatus.INVALID || verification.getClaims() == null) {
            return null;
        }
        JWTClaimsSet claims = verification.getClaims();
        if (!ExpiringLinkType.DELEGATE_INVITATION.name().equals(claims.getClaim("type"))) {
            return null;
        }
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private boolean isExpired(DelegateInvitationEntity invitation, Date now) {
        Date dateCreated = invitation.getDateCreated();
        return dateCreated == null || !now.before(new Date(dateCreated.getTime() + invitationLifetimeMillis()));
    }

    private long invitationLifetimeMillis() {
        return invitationLifetimeInDays * MILLIS_PER_DAY;
    }

    private boolean isPendingInvitationConflict(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException) {
                ConstraintViolationException violation = (ConstraintViolationException) cause;
                String name = violation.getConstraintName();
                if (name != null && name.toUpperCase(Locale.ROOT).contains("DELEGATE_INVITATION_ONE_PENDING")) {
                    return true;
                }
                if (violation.getSQLException() != null && "23505".equals(violation.getSQLException().getSQLState())
                        && violation.getSQLException().getMessage() != null
                        && violation.getSQLException().getMessage().toUpperCase(Locale.ROOT).contains("DELEGATE_INVITATION_ONE_PENDING")) {
                    return true;
                }
            }
        }
        return false;
    }

    private CreationStatus toCreationStatus(Eligibility eligibility) {
        switch (eligibility) {
            case SELF:
                return CreationStatus.SELF;
            case ACTIVE_INVITATION:
                return CreationStatus.ACTIVE_INVITATION;
            case ALREADY_DELEGATE:
                return CreationStatus.ALREADY_DELEGATE;
            default:
                return CreationStatus.CREATED;
        }
    }
}