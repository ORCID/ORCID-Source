package org.orcid.utils.phone;

public class PhoneNumberValidationResult {

    /** Generic reason, used when nothing more specific can be determined. */
    public static final String INVALID = "INVALID_PHONE_NUMBER";

    public static final String TOO_SHORT = "PHONE_TOO_SHORT";

    public static final String TOO_LONG = "PHONE_TOO_LONG";

    private final boolean valid;
    private final String e164Number;
    private final String errorCode;
    private final String errorMessage;

    private PhoneNumberValidationResult(boolean valid, String e164Number, String errorCode, String errorMessage) {
        this.valid = valid;
        this.e164Number = e164Number;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static PhoneNumberValidationResult valid(String e164Number) {
        return new PhoneNumberValidationResult(true, e164Number, null, null);
    }

    public static PhoneNumberValidationResult invalid(String errorMessage) {
        return new PhoneNumberValidationResult(false, null, INVALID, errorMessage);
    }

    public static PhoneNumberValidationResult invalid(String errorCode, String errorMessage) {
        return new PhoneNumberValidationResult(false, null, errorCode, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getE164Number() {
        return e164Number;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
