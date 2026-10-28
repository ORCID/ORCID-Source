package org.orcid.frontend.recoveryphone;

/**
 * The stored recovery number in full, in E.164 form, for the one field that
 * shows it: the manage page's phone field, which starts from the number on
 * file (F4.2). This is the only response that carries the number in the
 * clear; every other one carries the mask `***********NNNN` (R1.2).
 */
public class RecoveryPhoneNumberResponse {

    private boolean success;

    private String errorCode;

    private String phoneNumber;

    public static RecoveryPhoneNumberResponse failure(String errorCode) {
        RecoveryPhoneNumberResponse response = new RecoveryPhoneNumberResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        return response;
    }

    public static RecoveryPhoneNumberResponse success(String phoneNumber) {
        RecoveryPhoneNumberResponse response = new RecoveryPhoneNumberResponse();
        response.setSuccess(true);
        response.setPhoneNumber(phoneNumber);
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
