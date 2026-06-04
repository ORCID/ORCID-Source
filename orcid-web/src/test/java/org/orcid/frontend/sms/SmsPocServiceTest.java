package org.orcid.frontend.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsMessage;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.SmsSender;

public class SmsPocServiceTest {

    private SmsPocService service;
    private CapturingSmsSender awsSender;
    private CapturingSmsSender twilioSender;

    @Before
    public void setUp() {
        service = new SmsPocService();
        service.setPhoneNumberValidator(new PhoneNumberValidator());
        service.setProvider("aws");
        service.setDefaultRegion("US");
        service.setRegexFilter(".*");
        awsSender = new CapturingSmsSender("aws");
        twilioSender = new CapturingSmsSender("twilio");
        service.setSmsSenders(Arrays.asList(awsSender, twilioSender));
    }

    @Test
    public void sendValidatesAndDispatchesToSelectedProvider() {
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber("+50688888888");
        request.setMessage("ORCID SMS POC test");

        SmsPocResponse response = service.send(request);

        assertTrue(response.isSuccess());
        assertEquals("aws", response.getProvider());
        assertEquals("+50688888888", response.getNormalizedPhoneNumber());
        assertEquals("+50688888888", awsSender.lastMessage.getTo());
        assertEquals("ORCID SMS POC test", awsSender.lastMessage.getBody());
    }

    @Test
    public void sendRejectsPhoneNumbersOutsideSafetyFilter() {
        service.setRegexFilter("^\\+1555.*$");
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber("+50688888888");
        request.setMessage("ORCID SMS POC test");

        SmsPocResponse response = service.send(request);

        assertFalse(response.isSuccess());
        assertEquals("SMS_RECIPIENT_NOT_ALLOWED", response.getErrorCode());
    }

    @Test
    public void sendUsesRequestProviderOverConfigDefault() {
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber("+50688888888");
        request.setMessage("ORCID SMS POC test");
        request.setProvider("twilio");

        SmsPocResponse response = service.send(request);

        assertTrue(response.isSuccess());
        assertEquals("twilio", response.getProvider());
        assertEquals("+50688888888", twilioSender.lastMessage.getTo());
        assertEquals("ORCID SMS POC test", twilioSender.lastMessage.getBody());
    }

    @Test
    public void sendRejectsUnknownProvider() {
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber("+50688888888");
        request.setMessage("ORCID SMS POC test");
        request.setProvider("unknown");

        SmsPocResponse response = service.send(request);

        assertFalse(response.isSuccess());
        assertEquals("SMS_PROVIDER_NOT_CONFIGURED", response.getErrorCode());
    }

    private static class CapturingSmsSender implements SmsSender {

        private final String provider;
        private SmsMessage lastMessage;

        private CapturingSmsSender(String provider) {
            this.provider = provider;
        }

        @Override
        public String getProvider() {
            return provider;
        }

        @Override
        public SmsSendResult send(SmsMessage smsMessage) {
            lastMessage = smsMessage;
            return SmsSendResult.success(provider, "message-id", "SENT");
        }
    }
}
