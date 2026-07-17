package org.orcid.frontend.sms;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.phone.PhoneNumberValidationResult;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.VerificationCodeSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Drives phone verification through managed providers. ORCID generates the verification code, stores it, and confirms
 * it; the provider (AWS End User Messaging Notify or Twilio Verify) only delivers the code through its managed,
 * templated, fraud-protected pipeline.
 */
@Component
public class SmsPocService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private PhoneNumberValidator phoneNumberValidator;

    @Autowired
    private VerificationCodeStore verificationCodeStore;

    private Map<String, VerificationCodeSender> sendersByProvider = new HashMap<String, VerificationCodeSender>();

    @Value("${org.orcid.sms.provider:aws}")
    private String provider;

    @Value("${org.orcid.sms.defaultRegion:US}")
    private String defaultRegion;

    @Value("${org.orcid.sms.regexFilter:}")
    private String regexFilter;

    @Value("${org.orcid.sms.code.length:6}")
    private int codeLength;

    @Value("${org.orcid.sms.code.ttlSeconds:300}")
    private int codeTtlSeconds;

    @Value("${org.orcid.sms.code.maxAttempts:5}")
    private int maxAttempts;

    @Autowired
    public void setSenders(List<VerificationCodeSender> senders) {
        sendersByProvider.clear();
        if (senders != null) {
            for (VerificationCodeSender sender : senders) {
                sendersByProvider.put(StringUtils.lowerCase(sender.getProvider()), sender);
            }
        }
    }

    /**
     * Generates a verification code, sends it through the selected managed provider, and stores it for later
     * confirmation. The code itself is never returned to the caller.
     */
    public SmsPocResponse startVerification(SmsPocRequest request) {
        String selectedProvider = resolveProvider(request != null ? request.getProvider() : null);
        if (request == null) {
            return SmsPocResponse.failure(selectedProvider, null, "INVALID_REQUEST", "Request body is required");
        }

        PhoneNumberValidationResult validationResult = phoneNumberValidator.validate(request.getPhoneNumber(), defaultRegion);
        if (!validationResult.isValid()) {
            return SmsPocResponse.failure(selectedProvider, null, "INVALID_PHONE_NUMBER", validationResult.getErrorMessage());
        }

        String normalizedPhoneNumber = validationResult.getE164Number();
        if (StringUtils.isNotBlank(regexFilter) && !normalizedPhoneNumber.matches(regexFilter)) {
            return SmsPocResponse.failure(selectedProvider, normalizedPhoneNumber, "SMS_RECIPIENT_NOT_ALLOWED",
                    "Phone number is not allowed by SMS safety filter");
        }

        VerificationCodeSender sender = sendersByProvider.get(selectedProvider);
        if (sender == null) {
            return SmsPocResponse.failure(selectedProvider, normalizedPhoneNumber, "SMS_PROVIDER_NOT_CONFIGURED",
                    "SMS provider is not configured: " + selectedProvider);
        }

        String code = generateCode();
        SmsSendResult result = sender.sendCode(normalizedPhoneNumber, code, sanitizeLocale(request.getLocale()));
        if (!result.isSuccess()) {
            return SmsPocResponse.failure(result.getProvider(), normalizedPhoneNumber, result.getErrorCode(), result.getErrorMessage());
        }

        long expiresAt = System.currentTimeMillis() + (codeTtlSeconds * 1000L);
        verificationCodeStore.save(normalizedPhoneNumber,
                new VerificationCodeEntry(code, result.getProvider(), result.getProviderMessageId(), expiresAt));
        return SmsPocResponse.success(result.getProvider(), result.getProviderMessageId(), normalizedPhoneNumber, result.getStatus());
    }

    /**
     * Confirms a code against the stored value. ORCID is authoritative; the matching provider is then told the outcome
     * so its verification record is closed (Twilio VerificationCheck / AWS PutMessageFeedback).
     */
    public SmsPocResponse checkVerification(SmsVerificationCheckRequest request) {
        if (request == null) {
            return SmsPocResponse.failure(provider, null, "INVALID_REQUEST", "Request body is required");
        }
        if (StringUtils.isBlank(request.getCode())) {
            return SmsPocResponse.failure(provider, null, "INVALID_CODE", "Code is required");
        }

        PhoneNumberValidationResult validationResult = phoneNumberValidator.validate(request.getPhoneNumber(), defaultRegion);
        if (!validationResult.isValid()) {
            return SmsPocResponse.failure(provider, null, "INVALID_PHONE_NUMBER", validationResult.getErrorMessage());
        }
        String normalizedPhoneNumber = validationResult.getE164Number();

        VerificationCodeEntry entry = verificationCodeStore.get(normalizedPhoneNumber);
        if (entry == null) {
            return SmsPocResponse.failure(provider, normalizedPhoneNumber, "CODE_NOT_FOUND",
                    "No active verification code for this number; it may have expired");
        }

        if (entry.incrementAttempts() > maxAttempts) {
            verificationCodeStore.remove(normalizedPhoneNumber);
            return SmsPocResponse.failure(entry.getProvider(), normalizedPhoneNumber, "TOO_MANY_ATTEMPTS",
                    "Maximum verification attempts exceeded");
        }

        VerificationCodeSender sender = sendersByProvider.get(entry.getProvider());
        boolean matches = constantTimeEquals(entry.getCode(), request.getCode().trim());
        if (!matches) {
            return SmsPocResponse.verificationResult(entry.getProvider(), entry.getProviderMessageId(), normalizedPhoneNumber,
                    false, "NOT_VERIFIED");
        }

        verificationCodeStore.remove(normalizedPhoneNumber);
        // Best-effort provider feedback; a feedback failure does not invalidate a code ORCID already confirmed.
        if (sender != null) {
            sender.reportResult(normalizedPhoneNumber, entry.getCode(), entry.getProviderMessageId(), true);
        }
        return SmsPocResponse.verificationResult(entry.getProvider(), entry.getProviderMessageId(), normalizedPhoneNumber,
                true, "VERIFIED");
    }

    /**
     * Accepts only a plausible BCP 47 tag from the caller; anything else is dropped so senders fall back to English.
     */
    private static String sanitizeLocale(String locale) {
        if (StringUtils.isBlank(locale)) {
            return null;
        }
        String trimmed = locale.trim();
        return trimmed.matches("[A-Za-z]{2,8}([_-][A-Za-z0-9]{1,8}){0,3}") ? trimmed : null;
    }

    private String resolveProvider(String requestedProvider) {
        return StringUtils.lowerCase(StringUtils.defaultIfBlank(requestedProvider, StringUtils.defaultIfBlank(provider, "aws")));
    }

    private String generateCode() {
        int length = codeLength > 0 ? codeLength : 6;
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(RANDOM.nextInt(10));
        }
        return builder.toString();
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null || expected.length() != actual.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < expected.length(); i++) {
            result |= expected.charAt(i) ^ actual.charAt(i);
        }
        return result == 0;
    }

    void setPhoneNumberValidator(PhoneNumberValidator phoneNumberValidator) {
        this.phoneNumberValidator = phoneNumberValidator;
    }

    void setVerificationCodeStore(VerificationCodeStore verificationCodeStore) {
        this.verificationCodeStore = verificationCodeStore;
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

    void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    void setCodeTtlSeconds(int codeTtlSeconds) {
        this.codeTtlSeconds = codeTtlSeconds;
    }

    void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
