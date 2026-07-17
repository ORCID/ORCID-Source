package org.orcid.utils.sms;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpointsmsvoicev2.PinpointSmsVoiceV2Client;
import software.amazon.awssdk.services.pinpointsmsvoicev2.PinpointSmsVoiceV2ClientBuilder;
import software.amazon.awssdk.services.pinpointsmsvoicev2.model.MessageFeedbackStatus;
import software.amazon.awssdk.services.pinpointsmsvoicev2.model.SendNotifyTextMessageRequest;
import software.amazon.awssdk.services.pinpointsmsvoicev2.model.SendNotifyTextMessageResponse;

/**
 * Sends verification codes through AWS End User Messaging Notify ({@code pinpoint-sms-voice-v2} namespace). Notify only
 * delivers a templated message; ORCID supplies the code as the {@code code} template variable and verifies it locally.
 */
@Component
public class AwsNotifySmsSender implements VerificationCodeSender {

    public static final String PROVIDER = "aws";

    @Value("${org.orcid.sms.aws.region:us-east-2}")
    private String region;

    @Value("${org.orcid.sms.aws.accessKey:}")
    private String accessKey;

    @Value("${org.orcid.sms.aws.secretKey:}")
    private String secretKey;

    @Value("${org.orcid.sms.aws.notifyConfigurationId:}")
    private String notifyConfigurationId;

    // AWS-managed per-language code-verification templates. The -001 series supports the US (-005/-006 do not).
    // {{brandName}} is system-managed (taken from the Notify configuration's registered brand) and must NOT be sent
    // as a template variable; only {{code}} is. Keyed by ORCID UI language code (see LANGUAGE_MENU_OPTIONS in
    // orcid-angular's environment files); any language ORCID adds before a matching template exists falls back to
    // English.
    private static final String TEMPLATE_ENGLISH = "notify-code-verification-english-001";

    private static final Map<String, String> TEMPLATES_BY_LANGUAGE = Map.ofEntries(
            Map.entry("ar", "notify-code-verification-arabic-001"),
            Map.entry("cs", "notify-code-verification-czech-001"),
            Map.entry("de", "notify-code-verification-german-001"),
            Map.entry("en", TEMPLATE_ENGLISH),
            Map.entry("es", "notify-code-verification-spanish-001"),
            Map.entry("fr", "notify-code-verification-french-001"),
            Map.entry("it", "notify-code-verification-italian-001"),
            Map.entry("ja", "notify-code-verification-japanese-001"),
            Map.entry("ko", "notify-code-verification-korean-001"),
            Map.entry("pl", "notify-code-verification-polish-001"),
            Map.entry("pt", "notify-code-verification-portuguese-portugal-001"),
            Map.entry("ru", "notify-code-verification-russian-001"),
            Map.entry("tr", "notify-code-verification-turkish-001"),
            // AWS has no separate Traditional Chinese template; Simplified covers both zh-CN and zh-TW.
            Map.entry("zh", "notify-code-verification-chinese-simplified-001"));

    @Value("${org.orcid.sms.aws.notifyCodeVariable:code}")
    private String codeVariable;

    private PinpointSmsVoiceV2Client client;

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @Override
    public SmsSendResult sendCode(String toE164Number, String code, String locale) {
        if (StringUtils.isBlank(notifyConfigurationId)) {
            return SmsSendResult.failure(PROVIDER, "AWS_NOTIFY_NOT_CONFIGURED",
                    "AWS Notify configuration id is required; set org.orcid.sms.aws.notifyConfigurationId");
        }
        try {
            SendNotifyTextMessageRequest request = SendNotifyTextMessageRequest.builder()
                    .notifyConfigurationId(notifyConfigurationId)
                    .destinationPhoneNumber(toE164Number)
                    .templateId(resolveTemplateId(locale))
                    .templateVariables(Collections.singletonMap(codeVariable, code))
                    .messageFeedbackEnabled(true)
                    .build();
            SendNotifyTextMessageResponse response = getClient().sendNotifyTextMessage(request);
            return SmsSendResult.success(PROVIDER, response.messageId(), "SENT");
        } catch (Exception e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public SmsSendResult reportResult(String toE164Number, String code, String providerMessageId, boolean approved) {
        if (StringUtils.isBlank(providerMessageId)) {
            return SmsSendResult.success(PROVIDER, providerMessageId, approved ? "approved" : "denied");
        }
        try {
            getClient().putMessageFeedback(r -> r.messageId(providerMessageId)
                    .messageFeedbackStatus(approved ? MessageFeedbackStatus.RECEIVED : MessageFeedbackStatus.FAILED));
            return SmsSendResult.success(PROVIDER, providerMessageId, approved ? "approved" : "denied");
        } catch (Exception e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Maps the caller's UI locale to the matching localized template, falling back to the English template for any
     * language without an AWS-managed template. Only the language subtag matters ({@code es-419} → Spanish,
     * {@code zh-TW} → the shared Chinese template).
     */
    private static String resolveTemplateId(String locale) {
        String language = StringUtils.isBlank(locale) ? ""
                : StringUtils.substringBefore(StringUtils.substringBefore(locale.trim(), "-"), "_").toLowerCase(Locale.ROOT);
        return TEMPLATES_BY_LANGUAGE.getOrDefault(language, TEMPLATE_ENGLISH);
    }

    private PinpointSmsVoiceV2Client getClient() {
        if (client == null) {
            PinpointSmsVoiceV2ClientBuilder builder = PinpointSmsVoiceV2Client.builder().region(Region.of(region));
            if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
                builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
            } else {
                builder.credentialsProvider(DefaultCredentialsProvider.create());
            }
            client = builder.build();
        }
        return client;
    }

    void setClient(PinpointSmsVoiceV2Client client) {
        this.client = client;
    }
}
