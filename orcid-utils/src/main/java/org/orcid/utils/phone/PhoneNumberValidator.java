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
                return PhoneNumberValidationResult.invalid("Phone number is invalid");
            }
            return PhoneNumberValidationResult.valid(phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
        } catch (NumberParseException e) {
            return PhoneNumberValidationResult.invalid(e.getMessage());
        }
    }
}
