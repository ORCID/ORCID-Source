package org.orcid.frontend.recoveryphone;

public class RecoveryPhoneSendCodeRequest {

    private String phoneNumber;

    private String locale;

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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
