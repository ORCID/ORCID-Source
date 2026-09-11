package org.orcid.frontend.recoveryphone;

/**
 * The outcome of asking for a verification code. The code itself and the full
 * phone number are never returned.
 */
public class RecoveryPhoneSendCodeResponse {

    private boolean success;

    private String errorCode;

    /**
     * Seconds the user has to wait before another code can be sent. Drives the
     * countdown in the UI so the client and the throttle cannot disagree.
     */
    private int resendAfterSeconds;

    public static RecoveryPhoneSendCodeResponse success(int resendAfterSeconds) {
        RecoveryPhoneSendCodeResponse response = new RecoveryPhoneSendCodeResponse();
        response.setSuccess(true);
        response.setResendAfterSeconds(resendAfterSeconds);
        return response;
    }

    public static RecoveryPhoneSendCodeResponse failure(String errorCode) {
        RecoveryPhoneSendCodeResponse response = new RecoveryPhoneSendCodeResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        return response;
    }

    public static RecoveryPhoneSendCodeResponse failure(String errorCode, int resendAfterSeconds) {
        RecoveryPhoneSendCodeResponse response = failure(errorCode);
        response.setResendAfterSeconds(resendAfterSeconds);
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

}
