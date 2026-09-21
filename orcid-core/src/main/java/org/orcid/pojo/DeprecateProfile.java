package org.orcid.pojo;

import java.util.List;

public class DeprecateProfile extends AuthChallenge {

    private List<String> deprecatingEmails;
    
    private String deprecatingAccountName;
    
    private String deprecatingOrcidOrEmail;
    
    private String deprecatingOrcid;
    
    private List<String> primaryEmails;
    
    private String primaryOrcid;
    
    private String primaryAccountName;

    private String twoFactorToken;
    
    public List<String> getDeprecatingEmails() {
        return deprecatingEmails;
    }

    public void setDeprecatingEmails(List<String> deprecatingEmails) {
        this.deprecatingEmails = deprecatingEmails;
    }

    public String getDeprecatingAccountName() {
        return deprecatingAccountName;
    }

    public void setDeprecatingAccountName(String deprecatingAccountName) {
        this.deprecatingAccountName = deprecatingAccountName;
    }

    public String getDeprecatingOrcidOrEmail() {
        return deprecatingOrcidOrEmail;
    }

    public void setDeprecatingOrcidOrEmail(String deprecatingOrcidOrEmail) {
        this.deprecatingOrcidOrEmail = deprecatingOrcidOrEmail;
    }

    public List<String> getPrimaryEmails() {
        return primaryEmails;
    }

    public void setPrimaryEmails(List<String> primaryEmails) {
        this.primaryEmails = primaryEmails;
    }

    public String getPrimaryOrcid() {
        return primaryOrcid;
    }

    public void setPrimaryOrcid(String primaryOrcid) {
        this.primaryOrcid = primaryOrcid;
    }

    public String getPrimaryAccountName() {
        return primaryAccountName;
    }

    public void setPrimaryAccountName(String primaryAccountName) {
        this.primaryAccountName = primaryAccountName;
    }

    public String getTwoFactorToken() {
        return twoFactorToken;
    }

    public void setTwoFactorToken(String twoFactorToken) {
        this.twoFactorToken = twoFactorToken;
    }

    public String getDeprecatingOrcid() {
        return deprecatingOrcid;
    }

    public void setDeprecatingOrcid(String deprecatingOrcid) {
        this.deprecatingOrcid = deprecatingOrcid;
    }

}
