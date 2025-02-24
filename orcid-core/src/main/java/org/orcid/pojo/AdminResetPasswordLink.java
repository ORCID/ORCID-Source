package org.orcid.pojo;

import java.util.Date;

public class AdminResetPasswordLink {
    private String resetLink;

    private String orcidOrEmail;

    private String error;
    
    private Date issueDate;
    
    private int durationInHours = 4;

    public String getResetLink() {
        return resetLink;
    }

    public void setResetLink(String resetLink) {
        this.resetLink = resetLink;
    }

    public String getOrcidOrEmail() {
        return orcidOrEmail;
    }

    public void setOrcidOrEmail(String orcidOrEmail) {
        this.orcidOrEmail = orcidOrEmail;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public int getDurationInHours() {
        return durationInHours;
    }

    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

}
