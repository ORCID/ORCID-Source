package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;

public interface DelegateInvitationDao extends GenericDao<DelegateInvitationEntity, Long> {

    boolean lockSender(String senderOrcid);

    DelegateInvitationEntity findPendingBySenderAndRecipient(String senderOrcid, String recipientOrcid);

    DelegateInvitationEntity findByIdForUpdate(Long invitationId);

    String findSenderById(Long invitationId);

    boolean hasDelegatePermission(String senderOrcid, String recipientOrcid);

    List<DelegateInvitationEntity> findUnacknowledgedNotices(String senderOrcid);

    List<Long> findExpiredPendingIds(Date cutoff, int limit);
}