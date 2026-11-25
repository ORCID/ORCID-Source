package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "delegate_invitation")
public class DelegateInvitationEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    public enum LookupType {
        ORCID,
        EMAIL
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED,
        EXPIRED
    }

    private Long id;
    private String senderOrcid;
    private String recipientOrcid;
    private String enteredEmail;
    private Status status;
    private Long actionableNotificationId;
    private boolean senderNoticeAcknowledged;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "delegate_invitation_seq")
    @SequenceGenerator(name = "delegate_invitation_seq", sequenceName = "delegate_invitation_seq", allocationSize = 1)
    @Column(name = "delegate_invitation_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "sender_orcid", nullable = false, length = 19)
    public String getSenderOrcid() {
        return senderOrcid;
    }

    public void setSenderOrcid(String senderOrcid) {
        this.senderOrcid = senderOrcid;
    }

    @Column(name = "recipient_orcid", nullable = false, length = 19)
    public String getRecipientOrcid() {
        return recipientOrcid;
    }

    public void setRecipientOrcid(String recipientOrcid) {
        this.recipientOrcid = recipientOrcid;
    }

    @Transient
    public LookupType getLookupType() {
        return enteredEmail == null ? LookupType.ORCID : LookupType.EMAIL;
    }

    @Column(name = "entered_email", length = 320)
    public String getEnteredEmail() {
        return enteredEmail;
    }

    public void setEnteredEmail(String enteredEmail) {
        this.enteredEmail = enteredEmail;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "actionable_notification_id")
    public Long getActionableNotificationId() {
        return actionableNotificationId;
    }

    public void setActionableNotificationId(Long actionableNotificationId) {
        this.actionableNotificationId = actionableNotificationId;
    }

    @Column(name = "sender_notice_acknowledged", nullable = false)
    public boolean isSenderNoticeAcknowledged() {
        return senderNoticeAcknowledged;
    }

    public void setSenderNoticeAcknowledged(boolean senderNoticeAcknowledged) {
        this.senderNoticeAcknowledged = senderNoticeAcknowledged;
    }
}