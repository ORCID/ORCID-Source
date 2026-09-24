package org.orcid.frontend.recoveryphone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.orcid.utils.phone.PhoneNumberValidator;
import org.orcid.utils.sms.SmsSendResult;
import org.orcid.utils.sms.VerificationCodeSender;

public class RecoveryPhoneVerificationServiceTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String OTHER_ORCID = "0000-0000-0000-0002";

    private static final String PHONE = "+441234567890";

    private static final String OTHER_PHONE = "+441234567891";

    private static final String THIRD_PHONE = "+441234567892";

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
        // the shipped defaults, so every other case runs with the caps live
        service.setMaxSendsPerDay(10);
        service.setMaxDestinationsPerDay(3);
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

    /*
     * The three ways the pending code disappears, and the reason the buffer
     * cannot be read off it: each of these left the next send accepted at once.
     */

    @Test
    public void resendIsRefusedAfterTheAttemptsAreExhausted() {
        send(ORCID, PHONE);
        for (int attempt = 0; attempt <= 3; attempt++) {
            service.verifyCode(ORCID, PHONE, "000000");
        }
        assertNull("the attempts should have taken the pending code with them", store.get(ORCID));

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.RESEND_TOO_SOON, response.getErrorCode());
        assertTrue(response.getResendAfterSeconds() > 0);
        assertEquals("only the first send should have texted anyone", 1, awsSender.sent);
    }

    @Test
    public void resendIsRefusedAfterTheCodeWasUsed() {
        send(ORCID, PHONE);
        assertNull(service.verifyCode(ORCID, PHONE, awsSender.lastCode));

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.RESEND_TOO_SOON, response.getErrorCode());
        assertEquals(1, awsSender.sent);
    }

    @Test
    public void resendIsRefusedWhenThePendingCodeHasGoneOnItsOwn() {
        send(ORCID, PHONE);
        // what an expiry or an eviction leaves behind
        store.remove(ORCID);

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.RESEND_TOO_SOON, response.getErrorCode());
        assertEquals(1, awsSender.sent);
    }

    @Test
    public void resendIsAllowedOnceTheBufferHasPassedWithNoPendingCode() {
        send(ORCID, PHONE);
        for (int attempt = 0; attempt <= 3; attempt++) {
            service.verifyCode(ORCID, PHONE, "000000");
        }
        store.ageEntriesBySeconds(ORCID, 31);

        assertTrue(send(ORCID, PHONE).isSuccess());
        assertEquals(2, awsSender.sent);
    }

    @Test
    public void nothingIsTextedWhenTheSendHistoryCannotBeWritten() {
        store.failSaves = true;

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.CODE_STORAGE_UNAVAILABLE, response.getErrorCode());
        assertEquals("a text nothing can count is a text that should not go out", 0, awsSender.sent);
    }

    @Test
    public void nothingIsTextedWhenTheSendHistoryCannotBeRead() {
        store.historyIsUnreadable = true;

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertEquals(RecoveryPhoneVerificationService.CODE_STORAGE_UNAVAILABLE, response.getErrorCode());
        assertEquals(0, awsSender.sent);
    }

    @Test
    public void aSendTheProviderRefusedDoesNotStartTheBuffer() {
        awsSender.fail = true;
        assertEquals(RecoveryPhoneVerificationService.SMS_SEND_FAILED, send(ORCID, PHONE).getErrorCode());

        awsSender.fail = false;
        assertTrue("nobody was texted, so nothing should be waited out", send(ORCID, PHONE).isSuccess());
    }

    @Test
    public void oneRecordsBufferIsItsOwn() {
        send(ORCID, PHONE);

        assertTrue(send(OTHER_ORCID, PHONE).isSuccess());
    }

    /*
     * The buffer spaces texts out; on its own a record can still be made to send
     * all day. These cover the two ceilings that stop that.
     */

    private static final int A_DAY_AND_A_SECOND = (24 * 60 * 60) + 1;

    /** Sends spaced far enough apart that only the caps can refuse them. */
    private RecoveryPhoneSendCodeResponse sendPastTheBuffer(String orcid, String phone) {
        store.ageEntriesBySeconds(orcid, 31);
        return send(orcid, phone);
    }

    @Test
    public void theTextAfterTheDailyCapIsRefused() {
        service.setMaxSendsPerDay(3);

        assertTrue(send(ORCID, PHONE).isSuccess());
        assertTrue(sendPastTheBuffer(ORCID, PHONE).isSuccess());
        assertTrue(sendPastTheBuffer(ORCID, PHONE).isSuccess());
        RecoveryPhoneSendCodeResponse response = sendPastTheBuffer(ORCID, PHONE);

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, response.getErrorCode());
        assertEquals("the refused send must not have texted anyone", 3, awsSender.sent);
    }

    @Test
    public void theDailyCapRollsOverWithTheWindow() {
        service.setMaxSendsPerDay(2);
        send(ORCID, PHONE);
        sendPastTheBuffer(ORCID, PHONE);
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, sendPastTheBuffer(ORCID, PHONE).getErrorCode());

        store.ageEntriesBySeconds(ORCID, A_DAY_AND_A_SECOND);

        assertTrue("a day later the record starts again", send(ORCID, PHONE).isSuccess());
    }

    @Test
    public void aTextTheProviderRefusedDoesNotCountTowardsTheCap() {
        service.setMaxSendsPerDay(2);
        awsSender.fail = true;
        assertEquals(RecoveryPhoneVerificationService.SMS_SEND_FAILED, send(ORCID, PHONE).getErrorCode());

        awsSender.fail = false;
        assertTrue(send(ORCID, PHONE).isSuccess());
        assertTrue(sendPastTheBuffer(ORCID, PHONE).isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, sendPastTheBuffer(ORCID, PHONE).getErrorCode());
    }

    @Test
    public void theCapCountsTextsToEveryNumberTheRecordReached() {
        service.setMaxSendsPerDay(2);
        service.setMaxDestinationsPerDay(9);

        assertTrue(send(ORCID, PHONE).isSuccess());
        assertTrue(sendPastTheBuffer(ORCID, OTHER_PHONE).isSuccess());

        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, sendPastTheBuffer(ORCID, THIRD_PHONE).getErrorCode());
    }

    @Test
    public void aFurtherDistinctNumberIsRefusedEvenWithSendsToSpare() {
        service.setMaxSendsPerDay(10);
        service.setMaxDestinationsPerDay(2);
        assertTrue(send(ORCID, PHONE).isSuccess());
        assertTrue(sendPastTheBuffer(ORCID, OTHER_PHONE).isSuccess());

        RecoveryPhoneSendCodeResponse response = sendPastTheBuffer(ORCID, THIRD_PHONE);

        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, response.getErrorCode());
        assertEquals(2, awsSender.sent);
        // a number this record has already texted is not a new destination
        assertTrue(sendPastTheBuffer(ORCID, PHONE).isSuccess());
    }

    @Test
    public void theDestinationCapRollsOverWithTheWindow() {
        service.setMaxDestinationsPerDay(1);
        assertTrue(send(ORCID, PHONE).isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, sendPastTheBuffer(ORCID, OTHER_PHONE).getErrorCode());

        store.ageEntriesBySeconds(ORCID, A_DAY_AND_A_SECOND);

        assertTrue(send(ORCID, OTHER_PHONE).isSuccess());
    }

    @Test
    public void aCapOfZeroIsNoCap() {
        service.setMaxSendsPerDay(0);
        service.setMaxDestinationsPerDay(0);

        for (int i = 0; i < 12; i++) {
            assertTrue(sendPastTheBuffer(ORCID, PHONE).isSuccess());
        }
        assertTrue(sendPastTheBuffer(ORCID, OTHER_PHONE).isSuccess());
    }

    @Test
    public void oneRecordsCapIsItsOwn() {
        service.setMaxSendsPerDay(1);
        assertTrue(send(ORCID, PHONE).isSuccess());
        assertEquals(RecoveryPhoneVerificationService.SEND_LIMIT_REACHED, sendPastTheBuffer(ORCID, PHONE).getErrorCode());

        assertTrue(send(OTHER_ORCID, PHONE).isSuccess());
    }

    @Test
    public void whatIsStoredToCountTheTextsHoldsNoPhoneNumber() {
        send(ORCID, PHONE);

        String stored = store.storedHistory(ORCID);
        assertNotNull(stored);
        assertFalse("the send history must not hold the number it texted", stored.contains("441234567890"));
        assertFalse(stored.contains("1234567890"));
        assertFalse(stored.contains("234567890"));
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
        // Only the code entry's write fails. The history was written and the
        // text has already gone out by then, so this is the guard that decides
        // what the user is told about a code they will never be able to use.
        store.failCodeEntrySave = true;

        RecoveryPhoneSendCodeResponse response = send(ORCID, PHONE);

        assertFalse(response.isSuccess());
        assertEquals(RecoveryPhoneVerificationService.CODE_STORAGE_UNAVAILABLE, response.getErrorCode());
        assertEquals("the text went out before the entry could be stored", 1, awsSender.sent);
        assertNull("nothing may be left pending that the user cannot use", store.get(ORCID));
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
     * passed by rewriting the stored stamps - both the pending code's and the
     * send history's, because the two have to age together for the same reason
     * they have to be stored apart.
     */
    private static class FakeStore extends RecoveryPhoneCodeStore {

        private final Map<String, RecoveryPhoneCodeEntry> entries = new HashMap<>();

        /** Serialized, as Redis holds it, so a test can rewrite what is stored. */
        private final Map<String, String> histories = new HashMap<>();

        private boolean failSaves;

        /**
         * Fails only the code entry's write, leaving the send history writable.
         *
         * They need separate switches. The service writes the history before it
         * texts anything and the code entry after, so a single switch stops the
         * run at the first write and the second guard is never reached - which
         * left the test named for the code-entry guard exercising the history
         * guard instead, and the code-entry guard covered by nothing.
         */
        private boolean failCodeEntrySave;

        private boolean historyIsUnreadable;

        @Override
        public boolean save(String orcid, RecoveryPhoneCodeEntry entry, int ttlSeconds) {
            if (failSaves || failCodeEntrySave) {
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

        @Override
        public boolean saveSendHistory(String orcid, RecoveryPhoneSendHistory history, int ttlSeconds) {
            if (failSaves) {
                return false;
            }
            histories.put(orcid, history.serialize());
            return true;
        }

        @Override
        public RecoveryPhoneSendHistory getSendHistory(String orcid) {
            if (historyIsUnreadable) {
                return null;
            }
            return RecoveryPhoneSendHistory.parse(histories.get(orcid));
        }

        String storedHistory(String orcid) {
            return histories.get(orcid);
        }

        void ageEntriesBySeconds(String orcid, int seconds) {
            RecoveryPhoneCodeEntry entry = entries.get(orcid);
            if (entry != null) {
                entries.put(orcid, new RecoveryPhoneCodeEntry(entry.getCode(), entry.getPhoneE164(), entry.getProvider(),
                        entry.getProviderMessageId(), entry.getAttempts(), entry.getSentAt() - (seconds * 1000L)));
            }
            String history = histories.get(orcid);
            if (history != null) {
                histories.put(orcid, ageHistoryBySeconds(history, seconds));
            }
        }

        private static String ageHistoryBySeconds(String stored, int seconds) {
            try {
                long by = seconds * 1000L;
                JSONObject json = new JSONObject(stored);
                JSONArray sends = json.getJSONArray("sends");
                JSONArray agedSends = new JSONArray();
                for (int i = 0; i < sends.length(); i++) {
                    agedSends.put(sends.getLong(i) - by);
                }
                json.put("sends", agedSends);
                // and the destinations, which carry their own stamps: ageing one
                // half only would make the window look as though it rolled over
                // for the send cap and not for the destination cap
                JSONObject destinations = json.getJSONObject("destinations");
                JSONObject agedDestinations = new JSONObject();
                Iterator<?> digests = destinations.keys();
                while (digests.hasNext()) {
                    String digest = String.valueOf(digests.next());
                    agedDestinations.put(digest, destinations.getLong(digest) - by);
                }
                json.put("destinations", agedDestinations);
                return json.toString();
            } catch (JSONException e) {
                throw new IllegalStateException("could not age the stored send history", e);
            }
        }
    }

    private static class CapturingSender implements VerificationCodeSender {

        private final String provider;

        private String lastTo;

        private String lastCode;

        private int sent;

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
            this.sent++;
            return SmsSendResult.success(provider, "message-id", "PENDING");
        }
    }

}
