package org.orcid.frontend.recoveryphone;

/**
 * The outcome of asking for a code during an authentication challenge, where
 * the user never types the number: it is the one stored on the record.
 *
 * It carries the masked number as well as the outcome, so the panel can tell
 * the user where the code went. The full number is never returned.
 */
public class RecoveryPhoneChallengeSendCodeResponse {

    private boolean success;

    private String errorCode;

    /**
     * Seconds the user has to wait before another code can be sent. Drives the
     * countdown in the UI so the client and the throttle cannot disagree.
     */
    private int resendAfterSeconds;

    private String maskedRecoveryPhoneNumber;

    public static RecoveryPhoneChallengeSendCodeResponse failure(String errorCode) {
        RecoveryPhoneChallengeSendCodeResponse response = new RecoveryPhoneChallengeSendCodeResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        return response;
    }

    /**
     * Restates the outcome of the underlying send, adding the masked number.
     * A failed send keeps its resend countdown, since a refused resend is the
     * one failure the countdown belongs to.
     */
    public static RecoveryPhoneChallengeSendCodeResponse from(RecoveryPhoneSendCodeResponse sendCodeResponse, String maskedRecoveryPhoneNumber) {
        RecoveryPhoneChallengeSendCodeResponse response = new RecoveryPhoneChallengeSendCodeResponse();
        response.setSuccess(sendCodeResponse.isSuccess());
        response.setErrorCode(sendCodeResponse.getErrorCode());
        response.setResendAfterSeconds(sendCodeResponse.getResendAfterSeconds());
        response.setMaskedRecoveryPhoneNumber(maskedRecoveryPhoneNumber);
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
