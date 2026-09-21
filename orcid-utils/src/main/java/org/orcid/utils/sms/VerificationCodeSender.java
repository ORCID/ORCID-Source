package org.orcid.utils.sms;

/**
 * Delivers an application-generated verification code through a managed provider (AWS End User Messaging Notify or
 * Twilio Verify). The code is always generated and verified by ORCID; the provider only handles templated delivery,
 * routing, and fraud protection.
 */
public interface VerificationCodeSender {

    String getProvider();

    /**
     * Sends the supplied verification code to the given E.164 number through the provider's managed template.
     *
     * @param locale the recipient's UI locale (BCP 47, e.g. {@code es} or {@code zh-CN}); providers use it to pick a
     *               localized message template and fall back to English when the language is not available
     */
    SmsSendResult sendCode(String toE164Number, String code, String locale);

    /**
     * Reports the outcome of the local verification back to the provider. This closes the provider-side verification
     * loop and satisfies the feedback requirement that both Twilio Verify (custom codes) and AWS Notify expect. The
     * default implementation is a no-op success for providers that do not need feedback.
     *
     * @param providerMessageId the id returned by {@link #sendCode(String, String, String)} (Twilio verification SID / AWS message id)
     */
    default SmsSendResult reportResult(String toE164Number, String code, String providerMessageId, boolean approved) {
        return SmsSendResult.success(getProvider(), providerMessageId, approved ? "approved" : "denied");
    }
}
