package org.orcid.utils.sms;

import java.util.Collections;
import java.util.Locale;

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

    // Per-language AWS-managed template ids; the base id is the English/default fallback. When all are blank the
    // Notify configuration's default template is used instead. The -001 series supports the US; -005/-006 do not.
    // {{brandName}} is system-managed (taken from the Notify configuration) and must NOT be sent as a variable.
    @Value("${org.orcid.sms.aws.notifyTemplateId:notify-code-verification-english-001}")
    private String notifyTemplateId;

    @Value("${org.orcid.sms.aws.notifyTemplateId.es:notify-code-verification-spanish-001}")
    private String notifyTemplateIdSpanish;

    @Value("${org.orcid.sms.aws.notifyTemplateId.fr:notify-code-verification-french-001}")
    private String notifyTemplateIdFrench;

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
            SendNotifyTextMessageRequest.Builder builder = SendNotifyTextMessageRequest.builder()
                    .notifyConfigurationId(notifyConfigurationId)
                    .destinationPhoneNumber(toE164Number)
                    .templateVariables(Collections.singletonMap(codeVariable, code))
                    .messageFeedbackEnabled(true);
            String templateId = resolveTemplateId(locale);
            if (StringUtils.isNotBlank(templateId)) {
                builder.templateId(templateId);
            }
            SendNotifyTextMessageResponse response = getClient().sendNotifyTextMessage(builder.build());
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
     * Maps the caller's UI locale to the matching localized template, falling back to the default (English) template
     * for any other language. Only the language subtag matters ({@code es-419} → Spanish).
     */
    private String resolveTemplateId(String locale) {
        String language = StringUtils.isBlank(locale) ? ""
                : StringUtils.substringBefore(StringUtils.substringBefore(locale.trim(), "-"), "_").toLowerCase(Locale.ROOT);
        if ("es".equals(language) && StringUtils.isNotBlank(notifyTemplateIdSpanish)) {
            return notifyTemplateIdSpanish;
        }
        if ("fr".equals(language) && StringUtils.isNotBlank(notifyTemplateIdFrench)) {
            return notifyTemplateIdFrench;
        }
        return notifyTemplateId;
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
