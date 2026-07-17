package org.orcid.frontend.sms;

/**
 * Stores issued verification codes keyed by normalized (E.164) phone number until they are confirmed or expire. Every
 * pending code lives here regardless of the delivering provider — AWS End User Messaging Notify and Twilio Verify
 * sends both depend on this store, because ORCID (not the provider) is authoritative for code verification.
 *
 * <p><strong>Production requirement:</strong> moving past the POC means replacing the in-memory implementation with a
 * Redis-backed one (see {@code org.orcid.core.utils.cache.redis.RedisClient} in orcid-core: {@code set(key, value,
 * cacheExpiryInSecs)} maps to save+TTL, {@code get}/{@code remove} map directly). The registry runs multi-node, so a
 * JVM-local store means the verify request only succeeds when it happens to hit the node that sent the code. A
 * replacement must keep three behaviors: (1) entry expiry equal to {@code org.orcid.sms.code.ttlSeconds}, (2) an
 * attempt counter that increments atomically across nodes (serialize {@link VerificationCodeEntry} or use a Redis
 * counter), and (3) round-tripping {@code provider} + {@code providerMessageId} intact — they drive the post-verify
 * provider feedback (Twilio VerificationCheck / AWS PutMessageFeedback).
 */
public interface VerificationCodeStore {

    void save(String phoneNumber, VerificationCodeEntry entry);

    /**
     * Returns the active entry for the number, or {@code null} if none exists or it has expired.
     */
    VerificationCodeEntry get(String phoneNumber);

    void remove(String phoneNumber);
}
