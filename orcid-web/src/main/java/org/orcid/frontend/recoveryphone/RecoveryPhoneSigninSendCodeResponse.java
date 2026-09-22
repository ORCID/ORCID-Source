package org.orcid.frontend.recoveryphone;

/**
 * The outcome of asking, from the sign in screen, for a code to be sent to the
 * recovery number on the account.
 *
 * The caller is anonymous, so this carries nothing it did not already supply
 * beyond the masked number: never the code, never the number in full.
 */
public class RecoveryPhoneSigninSendCodeResponse {

    private boolean success;

    private String errorCode;

    /**
     * Seconds the user has to wait before another code can be sent. Drives the
     * countdown in the UI so the client and the throttle cannot disagree.
     */
    private int resendAfterSeconds;

    private String maskedRecoveryPhoneNumber;

    public static RecoveryPhoneSigninSendCodeResponse failure(String errorCode) {
        RecoveryPhoneSigninSendCodeResponse response = new RecoveryPhoneSigninSendCodeResponse();
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

    public int getResendAfterSeconds() {
        return resendAfterSeconds;
    }

    public void setResendAfterSeconds(int resendAfterSeconds) {
        this.resendAfterSeconds = resendAfterSeconds;
    }

    public String getMaskedRecoveryPhoneNumber() {
        return maskedRecoveryPhoneNumber;
    }

    public void setMaskedRecoveryPhoneNumber(String maskedRecoveryPhoneNumber) {
        this.maskedRecoveryPhoneNumber = maskedRecoveryPhoneNumber;
    }

}
