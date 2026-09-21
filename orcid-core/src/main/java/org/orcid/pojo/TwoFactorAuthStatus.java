package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.Date;

public class TwoFactorAuthStatus extends AuthChallenge {
    
    private boolean enabled;

    private Date twoFactorCreationDate;

    private Date recoveryCodeCreationDate;

    private String maskedRecoveryPhoneNumber;

    private Date recoveryPhoneCreationDate;

    private Date recoveryPhoneLastModifiedDate;

    private boolean recoveryPhoneModified;

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

    public String getMaskedRecoveryPhoneNumber() {
        return maskedRecoveryPhoneNumber;
    }

    public void setMaskedRecoveryPhoneNumber(String maskedRecoveryPhoneNumber) {
        this.maskedRecoveryPhoneNumber = maskedRecoveryPhoneNumber;
    }

    public Date getRecoveryPhoneCreationDate() {
        return recoveryPhoneCreationDate;
    }

    public void setRecoveryPhoneCreationDate(Date recoveryPhoneCreationDate) {
        this.recoveryPhoneCreationDate = recoveryPhoneCreationDate;
    }

    public Date getRecoveryPhoneLastModifiedDate() {
        return recoveryPhoneLastModifiedDate;
    }

    public void setRecoveryPhoneLastModifiedDate(Date recoveryPhoneLastModifiedDate) {
        this.recoveryPhoneLastModifiedDate = recoveryPhoneLastModifiedDate;
    }

    public boolean isRecoveryPhoneModified() {
        return recoveryPhoneModified;
    }

    public void setRecoveryPhoneModified(boolean recoveryPhoneModified) {
        this.recoveryPhoneModified = recoveryPhoneModified;
    }
}
