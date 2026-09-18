package org.orcid.utils.sms;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

/**
 * Writes the verification code to the application log instead of texting it.
 *
 * This sender is for local and development use only, and it is gated so that it cannot exist anywhere else: the bean
 * is registered only where the environment selects {@code org.orcid.sms.provider=log}, and {@link #sendCode} refuses
 * unless the configuration it reads says the same. The gate is what keeps it local: senders are looked up by name,
 * and a caller can ask for a name, so a bean that merely expects not to be chosen is not safe enough.
 *
 * It sends no message to anyone. It exists so that an automated end to end run can read a code it could never
 * receive by text.
 *
 * Even here the recipient is masked: the full number must never appear in a log line, not even from the local sender.
 */
@Component
@Conditional(LogVerificationCodeSender.LogProviderConfiguredCondition.class)
public class LogVerificationCodeSender implements VerificationCodeSender {

    public static final String PROVIDER = "log";

    static final String PROVIDER_PROPERTY = "org.orcid.sms.provider";

    private static final Logger LOG = LoggerFactory.getLogger(LogVerificationCodeSender.class);

    /** Fixed length mask, so the mask does not leak how long the number is. The same mask the Registry serves over HTTP. */
    private static final String MASK = "***********";

    private static final int VISIBLE_CHARACTERS = 4;

    /** A counter, not a random value: the id only has to be non-null and distinct within the run. */
    private static final AtomicLong MESSAGE_ID_SEQUENCE = new AtomicLong();

    @Value("${org.orcid.sms.provider:aws}")
    private String configuredProvider;

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @Override
    public SmsSendResult sendCode(String toE164Number, String code, String locale) {
        // Belt and braces behind the bean gate, so that a wiring mistake fails closed: a sender that puts live
        // verification codes in the log refuses outright wherever the log provider was not the configured one, and
        // says so without writing the code first.
        if (!isLogProviderSelected(configuredProvider)) {
            return SmsSendResult.failure(PROVIDER, "SMS_PROVIDER_NOT_CONFIGURED",
                    "The log sender only sends when org.orcid.sms.provider=log");
        }
        LOG.info("RECOVERY_PHONE_CODE to={} code={} locale={}", mask(toE164Number), code, locale);
        return SmsSendResult.success(PROVIDER, PROVIDER + "-" + MESSAGE_ID_SEQUENCE.incrementAndGet(), "logged");
    }

    /**
     * Keeps only the last four characters of the number, behind the fixed length mask. A null, blank or short value
     * is masked whole rather than printed, so nothing that is too short to spare four characters can be read back out
     * of the log.
     */
    static String mask(String toE164Number) {
        if (StringUtils.length(toE164Number) <= VISIBLE_CHARACTERS) {
            return MASK;
        }
        return MASK + toE164Number.substring(toE164Number.length() - VISIBLE_CHARACTERS);
    }

    private static boolean isLogProviderSelected(String provider) {
        return PROVIDER.equalsIgnoreCase(StringUtils.trim(provider));
    }

    void setConfiguredProvider(String configuredProvider) {
        this.configuredProvider = configuredProvider;
    }

    /**
     * Keeps the bean out of every context that has not asked for it. The classpath scanner evaluates
     * {@code @Conditional} itself, so it applies to the plain {@code <context:component-scan>} this application is
     * wired with; there is no Spring Boot here, so {@code @ConditionalOnProperty} is not available.
     *
     * The scan runs long before the property placeholder merges the ORCID configuration file, so the Spring
     * Environment is the only source a condition can read: select this sender with
     * {@code -Dorg.orcid.sms.provider=log} on the JVM. A system property also wins over the properties file when the
     * {@code @Value} above is resolved, so one setting satisfies both gates and neither opens on its own.
     */
    static class LogProviderConfiguredCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return isLogProviderSelected(context.getEnvironment().getProperty(PROVIDER_PROPERTY));
        }
    }
}
