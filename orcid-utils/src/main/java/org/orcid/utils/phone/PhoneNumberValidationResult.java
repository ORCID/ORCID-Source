package org.orcid.utils.phone;

public class PhoneNumberValidationResult {

    private final boolean valid;
    private final String e164Number;
    private final String errorMessage;

    private PhoneNumberValidationResult(boolean valid, String e164Number, String errorMessage) {
        this.valid = valid;
        this.e164Number = e164Number;
        this.errorMessage = errorMessage;
    }

    public static PhoneNumberValidationResult valid(String e164Number) {
        return new PhoneNumberValidationResult(true, e164Number, null);
    }

    public static PhoneNumberValidationResult invalid(String errorMessage) {
        return new PhoneNumberValidationResult(false, null, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getE164Number() {
        return e164Number;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
