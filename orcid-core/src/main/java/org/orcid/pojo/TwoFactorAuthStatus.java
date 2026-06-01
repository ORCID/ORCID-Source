package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.Date;

public class TwoFactorAuthStatus extends AuthChallenge {
    
    private boolean enabled;

    private Date twoFactorCreationDate;

    private Date recoveryCodeCreationDate;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getTwoFactorCreationDate() {
        return twoFactorCreationDate;
    }

    public void setTwoFactorCreationDate(Date twoFactorCreationDate) {
        this.twoFactorCreationDate = twoFactorCreationDate;
    }

    public Date getRecoveryCodeCreationDate() {
        return recoveryCodeCreationDate;
    }

    public void setRecoveryCodeCreationDate(Date recoveryCodeCreationDate) {
        this.recoveryCodeCreationDate = recoveryCodeCreationDate;
    }
}
