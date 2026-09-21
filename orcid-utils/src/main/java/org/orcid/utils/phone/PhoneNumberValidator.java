package org.orcid.utils.phone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

@Component
public class PhoneNumberValidator {

    private static final String DEFAULT_REGION = "US";

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public PhoneNumberValidationResult validate(String rawPhoneNumber, String defaultRegion) {
        if (StringUtils.isBlank(rawPhoneNumber)) {
            return PhoneNumberValidationResult.invalid("Phone number is required");
        }

        try {
            PhoneNumber phoneNumber = phoneNumberUtil.parse(rawPhoneNumber, StringUtils.defaultIfBlank(defaultRegion, DEFAULT_REGION));
            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                // Length problems get their own reason so the UI can tell the user what to fix
                switch (phoneNumberUtil.isPossibleNumberWithReason(phoneNumber)) {
                case TOO_SHORT:
                case INVALID_LENGTH:
                // Only long enough to dial locally, which is short of what we need
                case IS_POSSIBLE_LOCAL_ONLY:
                    return PhoneNumberValidationResult.invalid(PhoneNumberValidationResult.TOO_SHORT, "Phone number is too short");
                case TOO_LONG:
                    return PhoneNumberValidationResult.invalid(PhoneNumberValidationResult.TOO_LONG, "Phone number is too long");
                default:
                    return PhoneNumberValidationResult.invalid("Phone number is invalid");
                }
            }
            return PhoneNumberValidationResult.valid(phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
        } catch (NumberParseException e) {
            switch (e.getErrorType()) {
            case TOO_LONG:
                return PhoneNumberValidationResult.invalid(PhoneNumberValidationResult.TOO_LONG, "Phone number is too long");
            case TOO_SHORT_AFTER_IDD:
            case TOO_SHORT_NSN:
                return PhoneNumberValidationResult.invalid(PhoneNumberValidationResult.TOO_SHORT, "Phone number is too short");
            default:
                return PhoneNumberValidationResult.invalid(e.getMessage());
            }
        }
    }
}
