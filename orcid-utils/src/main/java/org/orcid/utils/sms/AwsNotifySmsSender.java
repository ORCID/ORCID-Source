package org.orcid.utils.sms;

import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
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

    private static final Logger LOG = LoggerFactory.getLogger(AwsNotifySmsSender.class);

    @Value("${org.orcid.sms.aws.deliveryValiditySeconds:300}")
    private int deliveryValiditySeconds = 300;

    @Value("${org.orcid.sms.aws.region:us-east-2}")
    private String region;

    @Value("${org.orcid.sms.aws.accessKey:}")
    private String accessKey;

    @Value("${org.orcid.sms.aws.secretKey:}")
    private String secretKey;

    @Value("${org.orcid.sms.aws.notifyConfigurationId:}")
    private String notifyConfigurationId;

    @Value("${org.orcid.sms.aws.configurationSetName:}")
    private String configurationSetName;

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

    private URI endpointOverride;

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
                    .configurationSetName(StringUtils.trimToNull(configurationSetName))
                    .timeToLive(deliveryValiditySeconds)
                    .messageFeedbackEnabled(true)
                    .build();
            SendNotifyTextMessageResponse response = getClient().sendNotifyTextMessage(request);
            LOG.info("AWS Notify accepted SMS messageId={}", response.messageId());
            return SmsSendResult.success(PROVIDER, response.messageId(), "SENT");
        } catch (Exception | LinkageError e) {
            // LinkageError too: a classpath the SDK cannot run on fails every send, and
            // it must come back as a failed send rather than escape as a 500
            logSendFailure(toE164Number, locale, e);
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
        } catch (Exception | LinkageError e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void logSendFailure(String toE164Number, String locale, Throwable e) {
        StringBuilder details = new StringBuilder("AWS Notify SMS send failed")
                .append(" | to=").append(maskPhone(toE164Number))
                .append(" | locale=").append(locale)
                .append(" | templateId=").append(resolveTemplateId(locale))
                .append(" | notifyConfigurationId=").append(notifyConfigurationId)
                .append(" | configurationSetName=").append(configurationSetName)
                .append(" | region=").append(region)
                .append(" | exceptionType=").append(e.getClass().getName())
                .append(" | message=").append(e.getMessage());
        if (e instanceof AwsServiceException) {
            AwsServiceException ase = (AwsServiceException) e;
            details.append(" | statusCode=").append(ase.statusCode())
                    .append(" | requestId=").append(ase.requestId())
                    .append(" | extendedRequestId=").append(ase.extendedRequestId())
                    .append(" | retryable=").append(ase.retryable())
                    .append(" | throttling=").append(ase.isThrottlingException());
            AwsErrorDetails errorDetails = ase.awsErrorDetails();
            if (errorDetails != null) {
                details.append(" | awsServiceName=").append(errorDetails.serviceName())
                        .append(" | awsErrorCode=").append(errorDetails.errorCode())
                        .append(" | awsErrorMessage=").append(errorDetails.errorMessage());
                try {
                    if (errorDetails.rawResponse() != null) {
                        details.append(" | awsRawResponse=").append(errorDetails.rawResponse().asUtf8String());
                    }
                } catch (Exception ignored) {
                    details.append(" | awsRawResponse=<unreadable>");
                }
                if (errorDetails.sdkHttpResponse() != null) {
                    details.append(" | awsHttpStatusText=").append(errorDetails.sdkHttpResponse().statusText().orElse(null))
                            .append(" | awsHttpHeaders=").append(errorDetails.sdkHttpResponse().headers());
                }
            }
        } else if (e instanceof SdkException) {
            details.append(" | retryable=").append(((SdkException) e).retryable());
        }
        if (e.getCause() != null) {
            details.append(" | cause=").append(e.getCause().getClass().getName())
                    .append(": ").append(e.getCause().getMessage());
        }
        LOG.error(details.toString(), e);
    }

    private static String maskPhone(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() <= 4) {
            return phone;
        }
        return StringUtils.repeat('*', phone.length() - 4) + phone.substring(phone.length() - 4);
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
            // The HTTP client is named rather than discovered: the SDK's default, the Apache 5
            // client, needs a newer httpclient5 than the one this build pins (PD-14414)
            PinpointSmsVoiceV2ClientBuilder builder = PinpointSmsVoiceV2Client.builder().region(Region.of(region))
                    .httpClient(UrlConnectionHttpClient.create());
            if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
                builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
            } else {
                builder.credentialsProvider(DefaultCredentialsProvider.create());
            }
            if (endpointOverride != null) {
                builder.endpointOverride(endpointOverride);
            }
            client = builder.build();
        }
        return client;
    }

    void setClient(PinpointSmsVoiceV2Client client) {
        this.client = client;
    }

    void setEndpointOverride(URI endpointOverride) {
        this.endpointOverride = endpointOverride;
    }
}
