package org.orcid.frontend.recoveryphone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Resource;

import org.orcid.core.utils.cache.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Holds the pending recovery phone verification code for a record, keyed by
 * ORCID iD so one record can never consume or interfere with another's code.
 *
 * Redis is the real store: the registry runs on several nodes and the request
 * that verifies a code is not necessarily the one that sent it. When Redis is
 * unavailable {@link #save} reports failure so the caller can refuse to send a
 * code nobody would be able to confirm.
 *
 * The in-memory map is a local development convenience only. It is off unless
 * {@code org.orcid.sms.code.allowInMemoryStore} is set, and it is never correct
 * on a multi-node deployment.
 */
@Component
public class RecoveryPhoneCodeStore {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryPhoneCodeStore.class);

    @Resource
    private RedisClient redisClient;

    @Value("${org.orcid.sms.code.allowInMemoryStore:false}")
    private boolean allowInMemoryStore;

    private final Map<String, InMemoryEntry> inMemoryEntries = new ConcurrentHashMap<>();

    /**
     * @return true when the code was stored and can be confirmed later
     */
    public boolean save(String orcid, RecoveryPhoneCodeEntry entry, int ttlSeconds) {
        String key = RecoveryPhoneCodeEntry.redisKey(orcid);
        if (redisClient.set(key, entry.serialize(), ttlSeconds)) {
            return true;
        }
        if (allowInMemoryStore) {
            LOG.warn("Redis unavailable, storing the recovery phone code in memory. This is only valid for local development.");
            inMemoryEntries.put(key, new InMemoryEntry(entry.serialize(), System.currentTimeMillis() + (ttlSeconds * 1000L)));
            return true;
        }
        LOG.error("Unable to store the recovery phone verification code, Redis is unavailable");
        return false;
    }

    public RecoveryPhoneCodeEntry get(String orcid) {
        String key = RecoveryPhoneCodeEntry.redisKey(orcid);
        RecoveryPhoneCodeEntry entry = RecoveryPhoneCodeEntry.parse(redisClient.get(key));
        if (entry != null) {
            return entry;
        }
        if (allowInMemoryStore) {
            InMemoryEntry inMemory = inMemoryEntries.get(key);
            if (inMemory != null) {
                if (inMemory.isExpired()) {
                    inMemoryEntries.remove(key);
                    return null;
                }
                return RecoveryPhoneCodeEntry.parse(inMemory.getValue());
            }
        }
        return null;
    }

    public void remove(String orcid) {
        String key = RecoveryPhoneCodeEntry.redisKey(orcid);
        redisClient.remove(key);
        if (allowInMemoryStore) {
            inMemoryEntries.remove(key);
        }
    }

    void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    void setAllowInMemoryStore(boolean allowInMemoryStore) {
        this.allowInMemoryStore = allowInMemoryStore;
    }

    private static class InMemoryEntry {

        private final String value;

        private final long expiresAt;

        InMemoryEntry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        String getValue() {
            return value;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

}
