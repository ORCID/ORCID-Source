package org.orcid.utils.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Proxy;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.services.pinpointsmsvoicev2.PinpointSmsVoiceV2Client;

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

    private static PinpointSmsVoiceV2Client clientThrowing(Error error) {
        return (PinpointSmsVoiceV2Client) Proxy.newProxyInstance(AwsNotifySmsSenderTest.class.getClassLoader(),
                new Class<?>[] { PinpointSmsVoiceV2Client.class }, (proxy, method, args) -> {
                    throw error;
                });
    }
}
