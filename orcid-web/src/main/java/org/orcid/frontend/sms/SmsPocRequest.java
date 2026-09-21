package org.orcid.frontend.sms;

/**
 * Request to start a phone verification: the provider delivers an ORCID-generated code to this number.
 */
public class SmsPocRequest {

    private String phoneNumber;
    private String provider;
    // The caller's UI locale (BCP 47, e.g. "es" or "zh-CN"); used to pick a localized message template.
    private String locale;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
