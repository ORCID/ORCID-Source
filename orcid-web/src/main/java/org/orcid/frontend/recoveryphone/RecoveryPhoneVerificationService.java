package org.orcid.frontend.recoveryphone;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.phone.PhoneNumberValidationResult;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.VerificationCodeSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Sends and confirms the one time codes used to prove a user controls the phone
 * number they are setting as their 2FA recovery number.
 *
 * ORCID generates and checks the code; the provider only delivers it. Codes are
 * held against the ORCID iD rather than the phone number, so a pending code
 * belongs to one record and asking for a new one always retires the old one.
 */
@Component
public class RecoveryPhoneVerificationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryPhoneVerificationService.class);

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String INVALID_CODE = "INVALID_CODE";
    public static final String CODE_EXPIRED = "CODE_EXPIRED";
    public static final String TOO_MANY_ATTEMPTS = "TOO_MANY_ATTEMPTS";
    public static final String PHONE_MISMATCH = "PHONE_MISMATCH";
    public static final String RESEND_TOO_SOON = "RESEND_TOO_SOON";
    public static final String SMS_SEND_FAILED = "SMS_SEND_FAILED";
    public static final String SMS_RECIPIENT_NOT_ALLOWED = "SMS_RECIPIENT_NOT_ALLOWED";
    public static final String SMS_PROVIDER_NOT_CONFIGURED = "SMS_PROVIDER_NOT_CONFIGURED";
    public static final String CODE_STORAGE_UNAVAILABLE = "CODE_STORAGE_UNAVAILABLE";

    @Autowired
    private PhoneNumberValidator phoneNumberValidator;

    @Autowired
    private RecoveryPhoneCodeStore recoveryPhoneCodeStore;

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

    @Value("${org.orcid.sms.code.resendBufferSeconds:30}")
    private int resendBufferSeconds;

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
     * Validates the number, sends a fresh code to it and stores that code
     * against the record. Any previously issued code stops working.
     */
    public RecoveryPhoneSendCodeResponse sendCode(String orcid, RecoveryPhoneSendCodeRequest request) {
        PhoneNumberValidationResult validationResult = phoneNumberValidator.validate(request == null ? null : request.getPhoneNumber(),
                defaultRegion);
        if (!validationResult.isValid()) {
            return RecoveryPhoneSendCodeResponse.failure(validationResult.getErrorCode());
        }

        String phoneE164 = validationResult.getE164Number();
        if (StringUtils.isNotBlank(regexFilter) && !phoneE164.matches(regexFilter)) {
            return RecoveryPhoneSendCodeResponse.failure(SMS_RECIPIENT_NOT_ALLOWED);
        }

        // One pending code per record: refuse a resend until the buffer has passed
        RecoveryPhoneCodeEntry pending = recoveryPhoneCodeStore.get(orcid);
        if (pending != null) {
            int remaining = remainingResendSeconds(pending);
            if (remaining > 0) {
                return RecoveryPhoneSendCodeResponse.failure(RESEND_TOO_SOON, remaining);
            }
        }

        String selectedProvider = StringUtils.lowerCase(StringUtils.defaultIfBlank(provider, "aws"));
        VerificationCodeSender sender = sendersByProvider.get(selectedProvider);
        if (sender == null) {
            return RecoveryPhoneSendCodeResponse.failure(SMS_PROVIDER_NOT_CONFIGURED);
        }

        String code = generateCode();
        SmsSendResult result = sender.sendCode(phoneE164, code, sanitizeLocale(request.getLocale()));
        if (!result.isSuccess()) {
            LOG.warn("Unable to send a recovery phone verification code for {}: {}", orcid, result.getErrorCode());
            return RecoveryPhoneSendCodeResponse.failure(SMS_SEND_FAILED);
        }

        RecoveryPhoneCodeEntry entry = new RecoveryPhoneCodeEntry(code, phoneE164, result.getProvider(), result.getProviderMessageId(), 0,
                System.currentTimeMillis());
        if (!recoveryPhoneCodeStore.save(orcid, entry, codeTtlSeconds)) {
            // Better to fail loudly than to leave the user with a code we cannot confirm
            return RecoveryPhoneSendCodeResponse.failure(CODE_STORAGE_UNAVAILABLE);
        }
        return RecoveryPhoneSendCodeResponse.success(resendBufferSeconds);
    }

    /**
     * Confirms the code the user typed against the pending code for the record,
     * and checks it was issued for the number they are trying to save.
     *
     * @return null when the code is good, otherwise the error code to report
     */
    public String verifyCode(String orcid, String rawPhoneNumber, String code) {
        if (StringUtils.isBlank(code)) {
            return INVALID_CODE;
        }

        PhoneNumberValidationResult validationResult = phoneNumberValidator.validate(rawPhoneNumber, defaultRegion);
        if (!validationResult.isValid()) {
            return validationResult.getErrorCode();
        }

        RecoveryPhoneCodeEntry entry = recoveryPhoneCodeStore.get(orcid);
        if (entry == null) {
            return CODE_EXPIRED;
        }

        // Redis drops the entry on its own, but never accept a stale code even if
        // the store still has it
        int remainingTtl = remainingTtlSeconds(entry);
        if (remainingTtl <= 0) {
            recoveryPhoneCodeStore.remove(orcid);
            return CODE_EXPIRED;
        }

        // The code was sent to a different number, so it cannot authorise this one
        if (!StringUtils.equals(entry.getPhoneE164(), validationResult.getE164Number())) {
            return PHONE_MISMATCH;
        }

        if (entry.incrementAttempts() > maxAttempts) {
            recoveryPhoneCodeStore.remove(orcid);
            return TOO_MANY_ATTEMPTS;
        }

        if (!constantTimeEquals(entry.getCode(), code.trim())) {
            // Persist the incremented attempt count, keeping the original expiry window
            recoveryPhoneCodeStore.save(orcid, entry, remainingTtl);
            return INVALID_CODE;
        }

        recoveryPhoneCodeStore.remove(orcid);
        VerificationCodeSender sender = sendersByProvider.get(entry.getProvider());
        if (sender != null) {
            // Best effort provider feedback; a failure here does not undo a code ORCID already confirmed
            sender.reportResult(entry.getPhoneE164(), entry.getCode(), entry.getProviderMessageId(), true);
        }
        return null;
    }

    /**
     * The E.164 form of a number the caller has already validated, for storing
     * against the record.
     */
    public String normalize(String rawPhoneNumber) {
        PhoneNumberValidationResult validationResult = phoneNumberValidator.validate(rawPhoneNumber, defaultRegion);
        return validationResult.isValid() ? validationResult.getE164Number() : null;
    }

    public void discardPendingCode(String orcid) {
        recoveryPhoneCodeStore.remove(orcid);
    }

    private int remainingResendSeconds(RecoveryPhoneCodeEntry entry) {
        long elapsed = (System.currentTimeMillis() - entry.getSentAt()) / 1000L;
        long remaining = resendBufferSeconds - elapsed;
        return remaining > 0 ? (int) remaining : 0;
    }

    private int remainingTtlSeconds(RecoveryPhoneCodeEntry entry) {
        long elapsed = (System.currentTimeMillis() - entry.getSentAt()) / 1000L;
        long remaining = codeTtlSeconds - elapsed;
        return remaining > 0 ? (int) remaining : 0;
    }

    /**
     * Accepts only a plausible BCP 47 tag from the caller; anything else is
     * dropped so senders fall back to English.
     */
    private static String sanitizeLocale(String locale) {
        if (StringUtils.isBlank(locale)) {
            return null;
        }
        String trimmed = locale.trim();
        return trimmed.matches("[A-Za-z]{2,8}([_-][A-Za-z0-9]{1,8}){0,3}") ? trimmed : null;
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

    void setRecoveryPhoneCodeStore(RecoveryPhoneCodeStore recoveryPhoneCodeStore) {
        this.recoveryPhoneCodeStore = recoveryPhoneCodeStore;
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

    void setResendBufferSeconds(int resendBufferSeconds) {
        this.resendBufferSeconds = resendBufferSeconds;
    }

}
