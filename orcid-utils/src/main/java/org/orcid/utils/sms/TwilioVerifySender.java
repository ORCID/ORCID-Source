package org.orcid.utils.sms;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.rest.verify.v2.service.VerificationCreator;

/**
 * Sends verification codes through Twilio Verify. ORCID generates the code and passes it as a custom verification code
 * so the same application-owned code is used across every provider. Verification is authoritative in ORCID; the
 * {@link #reportResult} call performs the Twilio VerificationCheck, which both closes Twilio's verification and provides
 * the feedback Twilio requires when custom codes are used.
 *
 * @see <a href="https://www.twilio.com/docs/verify/api/customization-options#custom-verification-codes">Twilio custom verification codes</a>
 */
@Component
public class TwilioVerifySender implements VerificationCodeSender {

    public static final String PROVIDER = "twilio";

    private static final String CHANNEL_SMS = "sms";

    @Value("${org.orcid.sms.twilio.accountSid:}")
    private String accountSid;

    @Value("${org.orcid.sms.twilio.authToken:}")
    private String authToken;

    @Value("${org.orcid.sms.twilio.verifyServiceSid:}")
    private String verifyServiceSid;

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @PostConstruct
    public void initTwilioClient() {
        if (StringUtils.isNotBlank(accountSid) && StringUtils.isNotBlank(authToken)) {
            Twilio.init(accountSid, authToken);
        }
    }

    @Override
    public SmsSendResult sendCode(String toE164Number, String code, String locale) {
        SmsSendResult configError = configError();
        if (configError != null) {
            return configError;
        }
        try {
            VerificationCreator creator = Verification
                    .creator(verifyServiceSid, toE164Number, CHANNEL_SMS)
                    .setCustomCode(code);
            String twilioLocale = normalizeLocale(locale);
            if (StringUtils.isNotBlank(twilioLocale)) {
                creator.setLocale(twilioLocale);
            }
            Verification verification = creator.create();
            return SmsSendResult.success(PROVIDER, verification.getSid(),
                    verification.getStatus() == null ? "pending" : verification.getStatus());
        } catch (Exception e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public SmsSendResult reportResult(String toE164Number, String code, String providerMessageId, boolean approved) {
        SmsSendResult configError = configError();
        if (configError != null) {
            return configError;
        }
        // Twilio only accepts a VerificationCheck for the approved case; a denied local check leaves the Twilio
        // verification to expire on its own.
        if (!approved) {
            return SmsSendResult.success(PROVIDER, providerMessageId, "denied");
        }
        try {
            VerificationCheck check = VerificationCheck.creator(verifyServiceSid)
                    .setTo(toE164Number)
                    .setCode(code)
                    .create();
            return SmsSendResult.success(PROVIDER, providerMessageId,
                    check.getStatus() == null ? "approved" : check.getStatus());
        } catch (Exception e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Twilio's Verify locale is the lowercase language subtag except for region-specific translations (zh-CN, zh-HK,
     * pt-BR, en-GB), which are passed through. Twilio falls back to English for unsupported values.
     */
    private static String normalizeLocale(String locale) {
        if (StringUtils.isBlank(locale)) {
            return null;
        }
        String normalized = locale.trim().replace('_', '-');
        String language = StringUtils.substringBefore(normalized, "-").toLowerCase(java.util.Locale.ROOT);
        if ("zh".equals(language) || "pt".equals(language) || "en".equals(language)) {
            String region = StringUtils.substringAfter(normalized, "-").toUpperCase(java.util.Locale.ROOT);
            return StringUtils.isBlank(region) ? language : language + "-" + region;
        }
        return language;
    }

    private SmsSendResult configError() {
        if (StringUtils.isBlank(accountSid) || StringUtils.isBlank(authToken)) {
            return SmsSendResult.failure(PROVIDER, "TWILIO_NOT_CONFIGURED", "Twilio account SID and auth token are required");
        }
        if (StringUtils.isBlank(verifyServiceSid)) {
            return SmsSendResult.failure(PROVIDER, "TWILIO_VERIFY_NOT_CONFIGURED",
                    "Twilio Verify service SID is required; set org.orcid.sms.twilio.verifyServiceSid");
        }
        return null;
    }
}
