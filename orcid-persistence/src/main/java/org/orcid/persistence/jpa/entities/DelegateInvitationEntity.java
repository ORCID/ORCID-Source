package org.orcid.persistence.jpa.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

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
    private String invitationToken;
    private String senderOrcid;
    private String recipientOrcid;
    private LookupType lookupType;
    private String enteredEmail;
    private Date sentDate;
    private Date expiresAt;
    private Date respondedAt;
    private Status status;
    private String pendingRecipientOrcid;
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

    @Column(name = "invitation_token", nullable = false, length = 36)
    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "lookup_type", nullable = false, length = 10)
    public LookupType getLookupType() {
        return lookupType;
    }

    public void setLookupType(LookupType lookupType) {
        this.lookupType = lookupType;
    }

    @Column(name = "entered_email", length = 320)
    public String getEnteredEmail() {
        return enteredEmail;
    }

    public void setEnteredEmail(String enteredEmail) {
        this.enteredEmail = enteredEmail;
    }

    @Column(name = "sent_date", nullable = false)
    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Column(name = "expires_at", nullable = false)
    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Column(name = "responded_at")
    public Date getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Date respondedAt) {
        this.respondedAt = respondedAt;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.pendingRecipientOrcid = status == Status.PENDING ? recipientOrcid : null;
    }

    @Column(name = "pending_recipient_orcid", length = 19)
    public String getPendingRecipientOrcid() {
        return pendingRecipientOrcid;
    }

    public void setPendingRecipientOrcid(String pendingRecipientOrcid) {
        this.pendingRecipientOrcid = pendingRecipientOrcid;
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