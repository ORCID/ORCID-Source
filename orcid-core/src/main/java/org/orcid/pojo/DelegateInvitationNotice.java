package org.orcid.pojo;

import java.util.Date;

public class DelegateInvitationNotice {

    private Long invitationId;
    private String status;
    private String lookupType;
    private String enteredEmail;
    private Date lastModified;
    private String recipientOrcid;
    private String recipientName;

    public String getRecipientOrcid() {
        return recipientOrcid;
    }

    public void setRecipientOrcid(String recipientOrcid) {
        this.recipientOrcid = recipientOrcid;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getEnteredEmail() {
        return enteredEmail;
    }

    public void setEnteredEmail(String enteredEmail) {
        this.enteredEmail = enteredEmail;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}