package org.orcid.pojo;

public class DelegateInvitationActionResult {

    private String status;

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
}