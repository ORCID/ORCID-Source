package org.orcid.pojo;

public class DelegateInvitationActionResult {

    private String status;
    private String senderName;
    private String senderOrcid;

    public DelegateInvitationActionResult() {
    }

    public DelegateInvitationActionResult(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderOrcid() {
        return senderOrcid;
    }

    public void setSenderOrcid(String senderOrcid) {
        this.senderOrcid = senderOrcid;
    }
}
