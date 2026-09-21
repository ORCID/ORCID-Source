package org.orcid.frontend.sms;

/**
 * Request to confirm a previously issued verification code for a phone number.
 */
public class SmsVerificationCheckRequest {

    private String phoneNumber;
    private String code;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
