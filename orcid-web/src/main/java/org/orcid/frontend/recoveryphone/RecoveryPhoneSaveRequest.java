package org.orcid.frontend.recoveryphone;

public class RecoveryPhoneSaveRequest {

    private String phoneNumber;

    private String verificationCode;

    /**
     * Where the form is being shown: SETTINGS, ONBOARDING or INTERSTITIAL. It
     * decides which proof of identity the request is accepted on, never what
     * the request is allowed to do.
     */
    private String context;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
