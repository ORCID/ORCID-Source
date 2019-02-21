package org.orcid.pojo;

public class AuthorizeDelegatesResult {
    
    private boolean approved;
    
    private boolean failed;
    
    private boolean notYou;
    
    private String approvalMessage;
   
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean isFailed) {
        this.failed = isFailed;
    }

    public boolean isNotYou() {
        return notYou;
    }

    public void setNotYou(boolean notYou) {
        this.notYou = notYou;
    }

    public String getApprovalMessage() {
        return approvalMessage;
    }

    public void setApprovalMessage(String approvalMessage) {
        this.approvalMessage = approvalMessage;
    }

}
