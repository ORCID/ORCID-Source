package org.orcid.frontend.recoveryphone;

import org.orcid.pojo.ajaxForm.Date;

/**
 * The outcome of saving a recovery phone number, carrying the refreshed panel
 * state so the settings page does not have to re-read the status straight away.
 */
public class RecoveryPhoneSaveResponse {

    private boolean success;

    private String errorCode;

    private String maskedRecoveryPhoneNumber;

    private Date recoveryPhoneCreationDate;

    private Date recoveryPhoneLastModifiedDate;

    private boolean recoveryPhoneModified;

    public static RecoveryPhoneSaveResponse failure(String errorCode) {
        RecoveryPhoneSaveResponse response = new RecoveryPhoneSaveResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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
