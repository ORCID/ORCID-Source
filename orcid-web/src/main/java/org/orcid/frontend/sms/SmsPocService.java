package org.orcid.frontend.sms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.phone.PhoneNumberValidationResult;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsMessage;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsPocService {

    private static final int MAX_MESSAGE_LENGTH = 1600;

    @Autowired
    private PhoneNumberValidator phoneNumberValidator;

    private Map<String, SmsSender> smsSendersByProvider = new HashMap<String, SmsSender>();

    @Value("${org.orcid.sms.provider:aws}")
    private String provider;

    @Value("${org.orcid.sms.defaultRegion:US}")
    private String defaultRegion;

    @Value("${org.orcid.sms.regexFilter:}")
    private String regexFilter;

    @Autowired
    public void setSmsSenders(List<SmsSender> smsSenders) {
        smsSendersByProvider.clear();
        if (smsSenders != null) {
            for (SmsSender smsSender : smsSenders) {
                smsSendersByProvider.put(StringUtils.lowerCase(smsSender.getProvider()), smsSender);
            }
        }
    }

    public SmsPocResponse send(SmsPocRequest request) {
        String selectedProvider = StringUtils.lowerCase(StringUtils.defaultIfBlank(
                request != null ? request.getProvider() : null,
                StringUtils.defaultIfBlank(provider, "aws")));
        if (request == null) {
            return SmsPocResponse.failure(selectedProvider, null, "INVALID_REQUEST", "Request body is required");
        }
        if (StringUtils.isBlank(request.getMessage())) {
            return SmsPocResponse.failure(selectedProvider, null, "INVALID_MESSAGE", "Message is required");
        }
        if (request.getMessage().length() > MAX_MESSAGE_LENGTH) {
            return SmsPocResponse.failure(selectedProvider, null, "INVALID_MESSAGE", "Message exceeds " + MAX_MESSAGE_LENGTH + " characters");
        }

        PhoneNumberValidationResult validationResult = phoneNumberValidator.validate(request.getPhoneNumber(), defaultRegion);
        if (!validationResult.isValid()) {
            return SmsPocResponse.failure(selectedProvider, null, "INVALID_PHONE_NUMBER", validationResult.getErrorMessage());
        }

        String normalizedPhoneNumber = validationResult.getE164Number();
        if (StringUtils.isNotBlank(regexFilter) && !normalizedPhoneNumber.matches(regexFilter)) {
            return SmsPocResponse.failure(selectedProvider, normalizedPhoneNumber, "SMS_RECIPIENT_NOT_ALLOWED", "Phone number is not allowed by SMS safety filter");
        }

        SmsSender smsSender = smsSendersByProvider.get(selectedProvider);
        if (smsSender == null) {
            return SmsPocResponse.failure(selectedProvider, normalizedPhoneNumber, "SMS_PROVIDER_NOT_CONFIGURED", "SMS provider is not configured: " + selectedProvider);
        }

        SmsSendResult result = smsSender.send(new SmsMessage(normalizedPhoneNumber, request.getMessage()));
        if (result.isSuccess()) {
            return SmsPocResponse.success(result.getProvider(), result.getProviderMessageId(), normalizedPhoneNumber, result.getStatus());
        }
        return SmsPocResponse.failure(result.getProvider(), normalizedPhoneNumber, result.getErrorCode(), result.getErrorMessage());
    }

    void setPhoneNumberValidator(PhoneNumberValidator phoneNumberValidator) {
        this.phoneNumberValidator = phoneNumberValidator;
    }

    void setProvider(String provider) {
        this.provider = provider;
    }

    void setDefaultRegion(String defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

    void setRegexFilter(String regexFilter) {
        this.regexFilter = regexFilter;
    }
}
