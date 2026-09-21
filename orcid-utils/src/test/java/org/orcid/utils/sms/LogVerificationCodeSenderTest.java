package org.orcid.utils.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.orcid.utils.sms.LogVerificationCodeSender.LogProviderConfiguredCondition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;

public class LogVerificationCodeSenderTest {

    private static final String PHONE_NUMBER = "+50688887777";

    private LogVerificationCodeSender sender = new LogVerificationCodeSender();

    @Before
    public void selectTheLogProvider() {
        sender.setConfiguredProvider(LogVerificationCodeSender.PROVIDER);
    }

    @Test
    public void providerIsLog() {
        assertEquals("log", LogVerificationCodeSender.PROVIDER);
        assertEquals(LogVerificationCodeSender.PROVIDER, sender.getProvider());
    }

    @Test
    public void theBeanIsOnlyRegisteredWhereTheEnvironmentSelectsTheLogProvider() {
        LogProviderConfiguredCondition condition = new LogProviderConfiguredCondition();

        assertTrue(condition.matches(contextConfiguredWith("log"), null));
        assertFalse(condition.matches(contextConfiguredWith("aws"), null));
        assertFalse(condition.matches(contextConfiguredWith(null), null));
    }

    @Test
    public void sendCodeReportsSuccessForTheLogProvider() {
        SmsSendResult result = sender.sendCode(PHONE_NUMBER, "123456", "en");

        assertTrue(result.isSuccess());
        assertEquals(LogVerificationCodeSender.PROVIDER, result.getProvider());
        assertTrue(result.getProviderMessageId() != null && result.getProviderMessageId().startsWith("log-"));
    }

    /**
     * What this test can and cannot prove.
     *
     * It cannot assert that the line reaches a log, because on this module's unit test classpath it does not reach
     * one at all: slf4j-api is 1.7.24 while the only binding present is log4j-slf4j2-impl, which implements the
     * slf4j 2.x service interface. A 1.7 api looks for the old StaticLoggerBinder, finds nothing, and falls back to
     * a no-op logger, so every LOG call in this module is silently discarded under test. Attaching a log4j2 appender
     * captures nothing for the same reason.
     *
     * So the masking rule (R1.2) is proved here against the helper that produces the value, and that the line really
     * is written, in the format the end to end suite reads, is proved against a running Registry instead.
     */
    @Test
    public void sendCodeRefusesWhenAnotherProviderIsConfigured() {
        sender.setConfiguredProvider("aws");

        SmsSendResult result = sender.sendCode(PHONE_NUMBER, "123456", "en");

        assertFalse(result.isSuccess());
        assertEquals(LogVerificationCodeSender.PROVIDER, result.getProvider());
        assertEquals("SMS_PROVIDER_NOT_CONFIGURED", result.getErrorCode());
        assertEquals(null, result.getProviderMessageId());
        // Deliberately no assertion about the log here: see the note above -- an appender on this classpath
        // captures nothing whatever the sender does, so "nothing was logged" would pass for the wrong reason.
    }

    @Test
    public void sendCodeGivesEachMessageItsOwnId() {
        String firstId = sender.sendCode(PHONE_NUMBER, "123456", "en").getProviderMessageId();
        String secondId = sender.sendCode(PHONE_NUMBER, "654321", "en").getProviderMessageId();

        assertNotNull(firstId);
        assertNotNull(secondId);
        assertFalse(firstId.equals(secondId));
    }

    @Test
    public void maskKeepsOnlyTheLastFourCharacters() {
        String masked = LogVerificationCodeSender.mask(PHONE_NUMBER);

        assertEquals("***********7777", masked);
        assertFalse(masked.contains(PHONE_NUMBER));
        assertFalse(masked.contains("5068"));
    }

    @Test
    public void maskIsFixedLengthSoItDoesNotLeakHowLongTheNumberIs() {
        assertEquals(15, LogVerificationCodeSender.mask("+15550001111").length());
        assertEquals(15, LogVerificationCodeSender.mask("+5068888777766").length());
    }

    @Test
    public void maskHidesANumberTooShortToSpareFourCharacters() {
        assertEquals("***********", LogVerificationCodeSender.mask("123"));
        assertEquals("***********", LogVerificationCodeSender.mask("1234"));
        assertEquals("***********", LogVerificationCodeSender.mask(""));
        assertEquals("***********", LogVerificationCodeSender.mask(null));
    }

    @Test
    public void sendCodeDoesNotThrowForANullOrShortNumber() {
        SmsSendResult nullNumber = sender.sendCode(null, "123456", "en");
        SmsSendResult shortNumber = sender.sendCode("123", "123456", null);

        assertTrue(nullNumber.isSuccess());
        assertNotNull(nullNumber.getProviderMessageId());
        assertTrue(shortNumber.isSuccess());
        assertNotNull(shortNumber.getProviderMessageId());
    }

    private static ConditionContext contextConfiguredWith(String provider) {
        MockEnvironment environment = new MockEnvironment();
        if (provider != null) {
            environment.setProperty(LogVerificationCodeSender.PROVIDER_PROPERTY, provider);
        }
        return new StubConditionContext(environment);
    }

    /** The condition reads nothing but the environment, so the rest of the context is not part of what is tested. */
    private static class StubConditionContext implements ConditionContext {

        private final Environment environment;

        StubConditionContext(Environment environment) {
            this.environment = environment;
        }

        @Override
        public BeanDefinitionRegistry getRegistry() {
            return null;
        }

        @Override
        public ConfigurableListableBeanFactory getBeanFactory() {
            return null;
        }

        @Override
        public Environment getEnvironment() {
            return environment;
        }

        @Override
        public ResourceLoader getResourceLoader() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }
    }

    /** Collects what the sender writes, so a test can assert on what did, and what did not, reach the log. */
}
