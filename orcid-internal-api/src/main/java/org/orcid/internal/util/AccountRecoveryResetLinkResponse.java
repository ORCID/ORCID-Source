package org.orcid.internal.util;

import java.util.Date;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * A freshly minted password reset link, together with the moment it stops working.
 *
 * Minting a link invalidates any link issued earlier for the same record, and the link may only be
 * redeemed once.
 */
public class AccountRecoveryResetLinkResponse {

    @XmlElement(name = "resetLink")
    private String resetLink;

    @XmlElement(name = "issueDate")
    private Date issueDate;

    @XmlElement(name = "expiryDate")
    private Date expiryDate;

    public AccountRecoveryResetLinkResponse() {
    }

    public AccountRecoveryResetLinkResponse(String resetLink, Date issueDate, Date expiryDate) {
        this.resetLink = resetLink;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    public String getResetLink() {
        return resetLink;
    }

    public void setResetLink(String resetLink) {
        this.resetLink = resetLink;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
