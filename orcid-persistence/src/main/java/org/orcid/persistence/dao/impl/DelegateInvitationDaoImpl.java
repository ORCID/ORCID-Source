package org.orcid.persistence.dao.impl;

import java.util.Date;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.orcid.persistence.dao.DelegateInvitationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.transaction.annotation.Transactional;

@PersistenceContext(unitName = "orcid")
public class DelegateInvitationDaoImpl extends GenericDaoImpl<DelegateInvitationEntity, Long> implements DelegateInvitationDao {

    public DelegateInvitationDaoImpl() {
        super(DelegateInvitationEntity.class);
    }

    @Override
    @Transactional
    public boolean lockSender(String senderOrcid) {
        return entityManager.find(ProfileEntity.class, senderOrcid, LockModeType.PESSIMISTIC_WRITE) != null;
    }

    @Override
    @Transactional
    public DelegateInvitationEntity findPendingBySenderAndRecipient(String senderOrcid, String recipientOrcid) {
        TypedQuery<DelegateInvitationEntity> query = entityManager.createQuery(
                "from DelegateInvitationEntity where senderOrcid = :senderOrcid and recipientOrcid = :recipientOrcid and status = :status",
                DelegateInvitationEntity.class);
        query.setParameter("senderOrcid", senderOrcid);
        query.setParameter("recipientOrcid", recipientOrcid);
        query.setParameter("status", DelegateInvitationEntity.Status.PENDING);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        query.setMaxResults(1);
        List<DelegateInvitationEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public DelegateInvitationEntity findByIdForUpdate(Long invitationId) {
        return entityManager.find(DelegateInvitationEntity.class, invitationId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    @Transactional
    public String findSenderByToken(String invitationToken) {
        TypedQuery<String> query = entityManager.createQuery(
                "select invitation.senderOrcid from DelegateInvitationEntity invitation where invitation.invitationToken = :invitationToken", String.class);
        query.setParameter("invitationToken", invitationToken);
        List<String> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public DelegateInvitationEntity findByTokenForUpdate(String invitationToken) {
        TypedQuery<DelegateInvitationEntity> query = entityManager.createQuery(
                "from DelegateInvitationEntity where invitationToken = :invitationToken", DelegateInvitationEntity.class);
        query.setParameter("invitationToken", invitationToken);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        List<DelegateInvitationEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public boolean hasDelegatePermission(String senderOrcid, String recipientOrcid) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select count(permission) from GivenPermissionToEntity permission where permission.giver = :senderOrcid and permission.receiver = :recipientOrcid",
                Long.class);
        query.setParameter("senderOrcid", senderOrcid);
        query.setParameter("recipientOrcid", recipientOrcid);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public List<DelegateInvitationEntity> findUnacknowledgedNotices(String senderOrcid) {
        TypedQuery<DelegateInvitationEntity> query = entityManager.createQuery(
                "from DelegateInvitationEntity where senderOrcid = :senderOrcid and status in :statuses and senderNoticeAcknowledged = false order by respondedAt",
                DelegateInvitationEntity.class);
        query.setParameter("senderOrcid", senderOrcid);
        query.setParameter("statuses", Arrays.asList(DelegateInvitationEntity.Status.ACCEPTED, DelegateInvitationEntity.Status.DECLINED,
                DelegateInvitationEntity.Status.EXPIRED));
        return query.getResultList();
    }

    @Override
    @Transactional
    public List<Long> findExpiredPendingIds(Date now, int limit) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select invitation.id from DelegateInvitationEntity invitation where invitation.status = :status and invitation.expiresAt <= :now order by invitation.expiresAt",
                Long.class);
        query.setParameter("status", DelegateInvitationEntity.Status.PENDING);
        query.setParameter("now", now);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}