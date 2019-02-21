package org.orcid.pojo;

public class ReactivationData {
    
    private boolean tokenValid;

    private String email;

    private boolean reactivationLinkExpired;

    private String resetParams;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isReactivationLinkExpired() {
        return reactivationLinkExpired;
    }

    public void setReactivationLinkExpired(boolean reactivationLinkExpired) {
        this.reactivationLinkExpired = reactivationLinkExpired;
    }

    public String getResetParams() {
        return resetParams;
    }

    public void setResetParams(String resetParams) {
        this.resetParams = resetParams;
    }

    public boolean isTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        this.tokenValid = tokenValid;
    }
    
}
