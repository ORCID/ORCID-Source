package org.orcid.frontend.sms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * POC-ONLY {@link VerificationCodeStore}. Codes for BOTH providers (AWS Notify and Twilio Verify) are held in this
 * JVM's heap, which is only correct on a single node: codes are lost on restart/redeploy, are invisible to other
 * cluster nodes (a verify request routed to a different node fails with CODE_NOT_FOUND), and the attempt counter is
 * per-JVM so the maxAttempts limit is not enforced cluster-wide. Do not ship this beyond the POC — replace it with a
 * Redis-backed implementation per the migration contract on {@link VerificationCodeStore}.
 */
@Component
public class InMemoryVerificationCodeStore implements VerificationCodeStore {

    private final Map<String, VerificationCodeEntry> entries = new ConcurrentHashMap<String, VerificationCodeEntry>();

    @Override
    public void save(String phoneNumber, VerificationCodeEntry entry) {
        entries.put(phoneNumber, entry);
    }

    @Override
    public VerificationCodeEntry get(String phoneNumber) {
        VerificationCodeEntry entry = entries.get(phoneNumber);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired(System.currentTimeMillis())) {
            entries.remove(phoneNumber, entry);
            return null;
        }
        return entry;
    }

    @Override
    public void remove(String phoneNumber) {
        entries.remove(phoneNumber);
    }
}
