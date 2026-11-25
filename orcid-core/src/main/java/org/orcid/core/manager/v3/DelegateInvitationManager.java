package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;

public interface DelegateInvitationManager {

    enum Eligibility {
        AVAILABLE,
        SELF,
        ACTIVE_INVITATION,
        ALREADY_DELEGATE
    }

    enum CreationStatus {
        CREATED,
        SELF,
        ACTIVE_INVITATION,
        ALREADY_DELEGATE
    }

    enum ResponseResult {
        ACCEPTED,
        DECLINED,
        EXPIRED,
        NOT_FOUND,
        NOT_RECIPIENT,
        ALREADY_RESPONDED
    }

    enum Decision {
        ACCEPT,
        DECLINE
    }

    class CreationResult {
        private final CreationStatus status;
        private final DelegateInvitationEntity invitation;

        public CreationResult(CreationStatus status, DelegateInvitationEntity invitation) {
            this.status = status;
            this.invitation = invitation;
        }

        public CreationStatus getStatus() {
            return status;
        }

        public DelegateInvitationEntity getInvitation() {
            return invitation;
        }
    }

    Eligibility checkEligibility(String senderOrcid, String recipientOrcid);

    CreationResult createInvitation(String senderOrcid, String recipientOrcid, DelegateInvitationEntity.LookupType lookupType, String enteredEmail);

    ResponseResult respond(String invitationToken, String authenticatedRecipientOrcid, Decision decision);

    List<DelegateInvitationEntity> getUnacknowledgedNotices(String senderOrcid);

    boolean acknowledgeNotice(String senderOrcid, String invitationToken);

    int expirePendingInvitations();
}