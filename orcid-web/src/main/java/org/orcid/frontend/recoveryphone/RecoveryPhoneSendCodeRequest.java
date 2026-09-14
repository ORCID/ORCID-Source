package org.orcid.frontend.recoveryphone;

public class RecoveryPhoneSendCodeRequest {

    private String phoneNumber;

    private String locale;

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

}
