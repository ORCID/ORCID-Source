package org.orcid.frontend.recoveryphone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.VerificationCodeSender;

public class RecoveryPhoneVerificationServiceTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String OTHER_ORCID = "0000-0000-0000-0002";

    private static final String PHONE = "+441234567890";

    private RecoveryPhoneVerificationService service;

    private CapturingSender awsSender;

    private FakeStore store;

    @Before
    public void setUp() {
        store = new FakeStore();
        service = new RecoveryPhoneVerificationService();
        service.setPhoneNumberValidator(new PhoneNumberValidator());
        service.setRecoveryPhoneCodeStore(store);
        service.setProvider("aws");
        service.setDefaultRegion("GB");
        service.setRegexFilter("");
        service.setCodeLength(6);
        service.setCodeTtlSeconds(300);
        service.setMaxAttempts(3);
        service.setResendBufferSeconds(30);
        awsSender = new CapturingSender("aws");
        service.setSenders(Arrays.asList(awsSender));
    }

    private static RecoveryPhoneSendCodeRequest sendRequest(String phone) {
        RecoveryPhoneSendCodeRequest request = new RecoveryPhoneSendCodeRequest();
        request.setPhoneNumber(phone);
        return request;
    }

    private RecoveryPhoneSendCodeResponse send(String orcid, String phone) {
        return service.sendCode(orcid, sendRequest(phone));
    }

    @Test
    public void sendCodeDispatchesACodeAndReportsTheResendBuffer() {
        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertTrue(response.isSuccess());
        assertEquals(30, response.getResendAfterSeconds());
        assertEquals(PHONE, awsSender.lastTo);
        assertEquals(6, awsSender.lastCode.length());
    }

    @Test
    public void sendCodeNeverReturnsTheCodeOrTheNumber() {
        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        String serialized = response.getErrorCode() + String.valueOf(response.getResendAfterSeconds()) + response.isSuccess();
        assertFalse(serialized.contains(awsSender.lastCode));
        assertFalse(serialized.contains(PHONE));
    }

    @Test
    public void tooShortAndTooLongNumbersAreReportedSeparately() {
        assertEquals("PHONE_TOO_SHORT", send(ORCID, "+441234").getErrorCode());
        assertEquals("PHONE_TOO_LONG", send(ORCID, "+4412345678901234").getErrorCode());
    }

    @Test
    public void anUnparseableNumberIsReportedAsInvalid() {
        assertEquals("INVALID_PHONE_NUMBER", send(ORCID, "?123456789").getErrorCode());
    }

    @Test
    public void aNumberOutsideTheSafetyFilterIsRefused() {
        service.setRegexFilter("\\+506.*");
        assertEquals(RecoveryPhoneVerificationService.SMS_RECIPIENT_NOT_ALLOWED, send(ORCID, PHONE).getErrorCode());
    }

    @Test
    public void resendIsRefusedUntilTheBufferHasPassed() {
        send(ORCID, PHONE);

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.RESEND_TOO_SOON, response.getErrorCode());
        assertTrue(response.getResendAfterSeconds() > 0);
    }

    @Test
    public void resendIsAllowedOnceTheBufferHasPassed() {
        send(ORCID, PHONE);
        store.ageEntriesBySeconds(ORCID, 31);

        assertTrue(send(ORCID, PHONE).isSuccess());
    }

    @Test
    public void aNewCodeRetiresThePreviousOne() {
        send(ORCID, PHONE);
        String firstCode = awsSender.lastCode;
        store.ageEntriesBySeconds(ORCID, 31);
        send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.INVALID_CODE, service.verifyCode(ORCID, PHONE, firstCode));
        assertNull(service.verifyCode(ORCID, PHONE, awsSender.lastCode));
    }

    @Test
    public void aCorrectCodeVerifiesAndIsThenConsumed() {
        send(ORCID, PHONE);
        String code = awsSender.lastCode;

        assertNull(service.verifyCode(ORCID, PHONE, code));
        assertEquals(RecoveryPhoneVerificationService.CODE_EXPIRED, service.verifyCode(ORCID, PHONE, code));
    }

    @Test
    public void aCodeSentToOneNumberCannotAuthoriseAnother() {
        send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.PHONE_MISMATCH, service.verifyCode(ORCID, "+441234567891", awsSender.lastCode));
    }

    @Test
    public void aCodeBelongsToTheRecordItWasSentFor() {
        send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.CODE_EXPIRED, service.verifyCode(OTHER_ORCID, PHONE, awsSender.lastCode));
    }

    @Test
    public void wrongCodesAreRefusedAndEventuallyExhaustTheAttempts() {
        send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.INVALID_CODE, service.verifyCode(ORCID, PHONE, "000000"));
        assertEquals(RecoveryPhoneVerificationService.INVALID_CODE, service.verifyCode(ORCID, PHONE, "000000"));
        assertEquals(RecoveryPhoneVerificationService.INVALID_CODE, service.verifyCode(ORCID, PHONE, "000000"));
        assertEquals(RecoveryPhoneVerificationService.TOO_MANY_ATTEMPTS, service.verifyCode(ORCID, PHONE, "000000"));
        // the entry is gone, so even the right code no longer works
        assertEquals(RecoveryPhoneVerificationService.CODE_EXPIRED, service.verifyCode(ORCID, PHONE, awsSender.lastCode));
    }

    @Test
    public void anExpiredCodeIsRefused() {
        send(ORCID, PHONE);
        store.ageEntriesBySeconds(ORCID, 301);

        assertEquals(RecoveryPhoneVerificationService.CODE_EXPIRED, service.verifyCode(ORCID, PHONE, awsSender.lastCode));
    }

    @Test
    public void aBlankCodeIsRefusedWithoutTouchingTheStore() {
        send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.INVALID_CODE, service.verifyCode(ORCID, PHONE, " "));
        assertNotNull(store.get(ORCID));
    }

    @Test
    public void sendFailsWhenTheCodeCannotBeStored() {
        store.failSaves = true;

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.CODE_STORAGE_UNAVAILABLE, response.getErrorCode());
    }

    @Test
    public void sendFailsWhenTheProviderRejectsTheMessage() {
        awsSender.fail = true;

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SMS_SEND_FAILED, response.getErrorCode());
        assertNull(store.get(ORCID));
    }

    @Test
    public void normalizeReturnsTheE164FormOrNull() {
        assertEquals(PHONE, service.normalize("01234 567890"));
        assertNull(service.normalize("?12345"));
    }

    /**
     * Stands in for the redis backed store, and lets a test pretend time has
     * passed by rewriting the entry's sent-at stamp.
     */
    private static class FakeStore extends RecoveryPhoneCodeStore {

        private final Map<String, RecoveryPhoneCodeEntry> entries = new HashMap<>();

        private boolean failSaves;

        @Override
        public boolean save(String orcid, RecoveryPhoneCodeEntry entry, int ttlSeconds) {
            if (failSaves) {
                return false;
            }
            // round trip through the serialized form, as the real store does
            entries.put(orcid, RecoveryPhoneCodeEntry.parse(entry.serialize()));
            return true;
        }

        @Override
        public RecoveryPhoneCodeEntry get(String orcid) {
            return entries.get(orcid);
        }

        @Override
        public void remove(String orcid) {
            entries.remove(orcid);
        }

        void ageEntriesBySeconds(String orcid, int seconds) {
            RecoveryPhoneCodeEntry entry = entries.get(orcid);
            if (entry != null) {
                entries.put(orcid, new RecoveryPhoneCodeEntry(entry.getCode(), entry.getPhoneE164(), entry.getProvider(),
                        entry.getProviderMessageId(), entry.getAttempts(), entry.getSentAt() - (seconds * 1000L)));
            }
        }
    }

    private static class CapturingSender implements VerificationCodeSender {

        private final String provider;

        private String lastTo;

        private String lastCode;

        private boolean fail;

        CapturingSender(String provider) {
            this.provider = provider;
        }

        @Override
        public String getProvider() {
            return provider;
        }

        @Override
        public SmsSendResult sendCode(String to, String code, String locale) {
            this.lastTo = to;
            this.lastCode = code;
            if (fail) {
                return SmsSendResult.failure(provider, "PROVIDER_ERROR", "boom");
            }
            return SmsSendResult.success(provider, "message-id", "PENDING");
        }
    }

}
