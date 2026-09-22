package org.orcid.frontend.recoveryphone;

/**
 * The outcome of confirming a recovery number code from the sign in screen.
 *
 * On success 2FA is off, and the ORCID iD is returned so the client can replay
 * the ordinary sign in against the record it has just recovered.
 */
public class RecoveryPhoneSigninVerifyResponse {

    private boolean success;

    private String errorCode;

    private String orcid;

    public static RecoveryPhoneSigninVerifyResponse failure(String errorCode) {
        RecoveryPhoneSigninVerifyResponse response = new RecoveryPhoneSigninVerifyResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        return response;
    }

    public static RecoveryPhoneSigninVerifyResponse success(String orcid) {
        RecoveryPhoneSigninVerifyResponse response = new RecoveryPhoneSigninVerifyResponse();
        response.setSuccess(true);
        response.setOrcid(orcid);
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

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

}
