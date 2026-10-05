package org.orcid.frontend.recoveryphone;

/**
 * Whether the account behind the credentials has a recovery number stored
 * (F1.2). It carries that one boolean and nothing else - not the mask, not the
 * dates - because the screen asking only has to choose between offering a
 * text and offering the help centre.
 */
public class RecoveryPhoneSigninStatusResponse {

    private boolean success;

    private String errorCode;

    private boolean hasRecoveryPhone;

    public static RecoveryPhoneSigninStatusResponse failure(String errorCode) {
        RecoveryPhoneSigninStatusResponse response = new RecoveryPhoneSigninStatusResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        return response;
    }

    public static RecoveryPhoneSigninStatusResponse success(boolean hasRecoveryPhone) {
        RecoveryPhoneSigninStatusResponse response = new RecoveryPhoneSigninStatusResponse();
        response.setSuccess(true);
        response.setHasRecoveryPhone(hasRecoveryPhone);
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

    public boolean isHasRecoveryPhone() {
        return hasRecoveryPhone;
    }

    public void setHasRecoveryPhone(boolean hasRecoveryPhone) {
        this.hasRecoveryPhone = hasRecoveryPhone;
    }

}
