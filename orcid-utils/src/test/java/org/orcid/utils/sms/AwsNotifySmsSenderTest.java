package org.orcid.utils.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.services.pinpointsmsvoicev2.PinpointSmsVoiceV2Client;
import software.amazon.awssdk.services.pinpointsmsvoicev2.model.SendNotifyTextMessageRequest;
import software.amazon.awssdk.services.pinpointsmsvoicev2.model.SendNotifyTextMessageResponse;

public class AwsNotifySmsSenderTest {

    private static final String PHONE_NUMBER = "+50688887777";

    private AwsNotifySmsSender sender = new AwsNotifySmsSender();

    @Before
    public void configure() {
        ReflectionTestUtils.setField(sender, "region", "us-east-2");
        ReflectionTestUtils.setField(sender, "accessKey", "AKIDEXAMPLE");
        ReflectionTestUtils.setField(sender, "secretKey", "not-a-secret");
        ReflectionTestUtils.setField(sender, "notifyConfigurationId", "notify-test");
        ReflectionTestUtils.setField(sender, "codeVariable", "code");
    }

    /**
     * Builds the real client from this module's classpath. With the SDK's
     * Apache 5 client on it, the build died on a class the pinned httpclient5
     * does not have, before any request was made (PD-14414). Nothing listens on
     * the endpoint, so the send can only fail on the connection.
     */
    @Test
    public void theRealClientBuildsAndFailsOnlyOnTheConnection() {
        sender.setEndpointOverride(URI.create("http://127.0.0.1:1"));
        SmsSendResult result = sender.sendCode(PHONE_NUMBER, "123456", "en");
        assertFalse(result.isSuccess());
        assertEquals("SdkClientException", result.getErrorCode());
    }

    /** A broken classpath is a failed send, not an error thrown into the controller. */
    @Test
    public void aLinkageErrorIsAFailedSend() {
        sender.setClient(clientThrowing(new NoClassDefFoundError("org/example/Missing")));
        SmsSendResult result = sender.sendCode(PHONE_NUMBER, "123456", "en");
        assertFalse(result.isSuccess());
        assertEquals("NoClassDefFoundError", result.getErrorCode());
    }

    @Test
    public void aLinkageErrorOnFeedbackIsAFailedReport() {
        sender.setClient(clientThrowing(new NoClassDefFoundError("org/example/Missing")));
        SmsSendResult result = sender.reportResult(PHONE_NUMBER, "123456", "message-id", true);
        assertFalse(result.isSuccess());
        assertEquals("NoClassDefFoundError", result.getErrorCode());
    }

    @Test
    public void sendsWithDeliveryConfigurationAndFiveMinuteValidity() {
        ReflectionTestUtils.setField(sender, "configurationSetName", "delivery-events");
        AtomicReference<SendNotifyTextMessageRequest> captured = captureRequest();

        SmsSendResult result = sender.sendCode(PHONE_NUMBER, "123456", "es-419");

        assertTrue(result.isSuccess());
        assertEquals("delivery-events", captured.get().configurationSetName());
        assertEquals(Integer.valueOf(300), captured.get().timeToLive());
        assertEquals("notify-code-verification-spanish-001", captured.get().templateId());
        assertEquals("123456", captured.get().templateVariables().get("code"));
        assertEquals(Boolean.TRUE, captured.get().messageFeedbackEnabled());
    }

    @Test
    public void omitsBlankDeliveryConfiguration() {
        ReflectionTestUtils.setField(sender, "configurationSetName", "  ");
        AtomicReference<SendNotifyTextMessageRequest> captured = captureRequest();

        assertTrue(sender.sendCode(PHONE_NUMBER, "123456", "en").isSuccess());
        assertNull(captured.get().configurationSetName());
        assertEquals(Integer.valueOf(300), captured.get().timeToLive());
    }

    @Test
    public void injectsDefaultDeliveryValidity() {
        assertInjectedDeliveryValidity(Map.of(), 300);
    }

    @Test
    public void injectsConfiguredDeliveryValidity() {
        assertInjectedDeliveryValidity(Map.of("org.orcid.sms.aws.deliveryValiditySeconds", "120"), 120);
    }

    private void assertInjectedDeliveryValidity(Map<String, Object> properties, int expected) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
            context.register(AwsNotifySmsSender.class);
            context.refresh();
            sender = context.getBean(AwsNotifySmsSender.class);
            ReflectionTestUtils.setField(sender, "notifyConfigurationId", "notify-test");
            AtomicReference<SendNotifyTextMessageRequest> captured = captureRequest();

            assertTrue(sender.sendCode(PHONE_NUMBER, "123456", "en").isSuccess());
            assertEquals(Integer.valueOf(expected), captured.get().timeToLive());
        }
    }

    @Test
    public void successfulSendLogsMessageIdWithoutPhoneOrCode() {
        Logger logger = (Logger) LogManager.getLogger(AwsNotifySmsSender.class);
        Level previousLevel = logger.getLevel();
        List<String> messages = new ArrayList<>();
        AbstractAppender appender = new AbstractAppender("sms-test", null, null, false, null) {
            @Override
            public void append(LogEvent event) {
                messages.add(event.getMessage().getFormattedMessage());
            }
        };
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        try {
            captureRequest();
            assertTrue(sender.sendCode(PHONE_NUMBER, "123456", "en").isSuccess());
            assertEquals(1, messages.size());
            assertTrue(messages.get(0).contains("message-id"));
            assertFalse(messages.get(0).contains(PHONE_NUMBER));
            assertFalse(messages.get(0).contains("123456"));
        } finally {
            logger.removeAppender(appender);
            logger.setLevel(previousLevel);
            appender.stop();
        }
    }

    private AtomicReference<SendNotifyTextMessageRequest> captureRequest() {
        AtomicReference<SendNotifyTextMessageRequest> captured = new AtomicReference<>();
        sender.setClient((PinpointSmsVoiceV2Client) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] { PinpointSmsVoiceV2Client.class }, (proxy, method, args) -> {
                    captured.set((SendNotifyTextMessageRequest) args[0]);
                    return SendNotifyTextMessageResponse.builder().messageId("message-id").build();
                }));
        return captured;
    }

    private static PinpointSmsVoiceV2Client clientThrowing(Error error) {
        return (PinpointSmsVoiceV2Client) Proxy.newProxyInstance(AwsNotifySmsSenderTest.class.getClassLoader(),
                new Class<?>[] { PinpointSmsVoiceV2Client.class }, (proxy, method, args) -> {
                    throw error;
                });
    }
}
