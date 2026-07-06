package org.orcid.frontend.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.VerificationCodeSender;

public class SmsPocServiceTest {

    private SmsPocService service;
    private CapturingSender awsSender;
    private CapturingSender twilioSender;

    @Before
    public void setUp() {
        service = new SmsPocService();
        service.setPhoneNumberValidator(new PhoneNumberValidator());
        service.setVerificationCodeStore(new InMemoryVerificationCodeStore());
        service.setProvider("aws");
        service.setDefaultRegion("US");
        service.setRegexFilter(".*");
        service.setCodeLength(6);
        service.setCodeTtlSeconds(300);
        service.setMaxAttempts(3);
        awsSender = new CapturingSender("aws");
        twilioSender = new CapturingSender("twilio");
        service.setSenders(Arrays.asList(awsSender, twilioSender));
    }

    private static SmsPocRequest sendRequest(String phone, String provider) {
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber(phone);
        request.setProvider(provider);
        return request;
    }

    @Test
    public void startVerificationGeneratesCodeAndDispatchesToSelectedProvider() {
        SmsPocResponse response = service.startVerification(sendRequest("+50688888888", null));

        assertTrue(response.isSuccess());
        assertEquals("aws", response.getProvider());
        assertEquals("+50688888888", response.getNormalizedPhoneNumber());
        assertEquals("+50688888888", awsSender.lastTo);
        assertEquals(6, awsSender.lastCode.length());
    }

    @Test
    public void startVerificationUsesRequestProviderOverConfigDefault() {
        SmsPocResponse response = service.startVerification(sendRequest("+50688888888", "twilio"));

        assertTrue(response.isSuccess());
        assertEquals("twilio", response.getProvider());
        assertEquals("+50688888888", twilioSender.lastTo);
    }

    @Test
    public void startVerificationPassesSanitizedLocaleToSender() {
        SmsPocRequest request = sendRequest("+50688888888", null);
        request.setLocale("es-419");
        service.startVerification(request);
        assertEquals("es-419", awsSender.lastLocale);

        request.setLocale("not a locale!");
        service.startVerification(request);
        assertEquals(null, awsSender.lastLocale);
    }

    @Test
    public void startVerificationRejectsPhoneNumbersOutsideSafetyFilter() {
        service.setRegexFilter("^\\+1555.*$");

        SmsPocResponse response = service.startVerification(sendRequest("+50688888888", null));

        assertFalse(response.isSuccess());
        assertEquals("SMS_RECIPIENT_NOT_ALLOWED", response.getErrorCode());
    }

    @Test
    public void startVerificationRejectsUnknownProvider() {
        SmsPocResponse response = service.startVerification(sendRequest("+50688888888", "unknown"));

        assertFalse(response.isSuccess());
        assertEquals("SMS_PROVIDER_NOT_CONFIGURED", response.getErrorCode());
    }

    @Test
    public void checkVerificationApprovesMatchingCodeAndReportsToProvider() {
        service.startVerification(sendRequest("+50688888888", null));

        SmsPocResponse response = check("+50688888888", awsSender.lastCode);

        assertTrue(response.isSuccess());
        assertEquals(Boolean.TRUE, response.getVerified());
        assertTrue(awsSender.reportedApproved);
    }

    @Test
    public void checkVerificationRejectsWrongCodeWithoutConsumingIt() {
        service.startVerification(sendRequest("+50688888888", null));

        SmsPocResponse wrong = check("+50688888888", "000000");
        assertTrue(wrong.isSuccess());
        assertEquals(Boolean.FALSE, wrong.getVerified());

        // The real code still works afterwards.
        SmsPocResponse right = check("+50688888888", awsSender.lastCode);
        assertEquals(Boolean.TRUE, right.getVerified());
    }

    @Test
    public void checkVerificationEnforcesMaxAttempts() {
        service.startVerification(sendRequest("+50688888888", null));

        check("+50688888888", "000000");
        check("+50688888888", "000000");
        check("+50688888888", "000000");
        SmsPocResponse overLimit = check("+50688888888", "000000");

        assertFalse(overLimit.isSuccess());
        assertEquals("TOO_MANY_ATTEMPTS", overLimit.getErrorCode());
    }

    @Test
    public void checkVerificationFailsWhenNoCodeIssued() {
        SmsPocResponse response = check("+50688888888", "123456");

        assertFalse(response.isSuccess());
        assertEquals("CODE_NOT_FOUND", response.getErrorCode());
    }

    private SmsPocResponse check(String phone, String code) {
        SmsVerificationCheckRequest request = new SmsVerificationCheckRequest();
        request.setPhoneNumber(phone);
        request.setCode(code);
        return service.checkVerification(request);
    }

    private static class CapturingSender implements VerificationCodeSender {

        private final String provider;
        private String lastTo;
        private String lastCode;
        private String lastLocale;
        private boolean reportedApproved;

        private CapturingSender(String provider) {
            this.provider = provider;
        }

        @Override
        public String getProvider() {
            return provider;
        }

        @Override
        public SmsSendResult sendCode(String toE164Number, String code, String locale) {
            this.lastTo = toE164Number;
            this.lastCode = code;
            this.lastLocale = locale;
            return SmsSendResult.success(provider, "message-id", "SENT");
        }

        @Override
        public SmsSendResult reportResult(String toE164Number, String code, String providerMessageId, boolean approved) {
            this.reportedApproved = approved;
            return SmsSendResult.success(provider, providerMessageId, approved ? "approved" : "denied");
        }
    }
}
