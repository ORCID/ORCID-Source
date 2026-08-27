package org.orcid.internal.util;

/**
 * Request to mint a single use password reset link for a record.
 */
public class AccountRecoveryResetLinkRequest {

    private String orcid;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}
