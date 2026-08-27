package org.orcid.internal.util;

/**
 * Request to confirm that an ORCID iD and an email address belong to the same record.
 */
public class AccountRecoveryMatchRequest {

    private String orcid;

    private String email;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
