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
 * Holds the recovery phone verification state for a record, keyed by ORCID iD so
 * one record can never consume or interfere with another's: the single pending
 * code, and the history of the texts that have actually been sent.
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
        return put(RecoveryPhoneCodeEntry.redisKey(orcid), entry.serialize(), ttlSeconds, "verification code");
    }

    public RecoveryPhoneCodeEntry get(String orcid) {
        return RecoveryPhoneCodeEntry.parse(read(RecoveryPhoneCodeEntry.redisKey(orcid)));
    }

    public void remove(String orcid) {
        delete(RecoveryPhoneCodeEntry.redisKey(orcid));
    }

    /**
     * @return true when the history was stored, so the record's texts can still
     *         be counted after this one
     */
    public boolean saveSendHistory(String orcid, RecoveryPhoneSendHistory history, int ttlSeconds) {
        return put(RecoveryPhoneSendHistory.redisKey(orcid), history.serialize(), ttlSeconds, "send history");
    }

    /**
     * @return the record's send history, an empty one when it has none, and null
     *         when something is stored that cannot be read - which the caller
     *         must treat as a reason to refuse rather than as no history
     */
    public RecoveryPhoneSendHistory getSendHistory(String orcid) {
        return RecoveryPhoneSendHistory.parse(read(RecoveryPhoneSendHistory.redisKey(orcid)));
    }

    private boolean put(String key, String value, int ttlSeconds, String what) {
        if (redisClient.set(key, value, ttlSeconds)) {
            return true;
        }
        if (allowInMemoryStore) {
            LOG.warn("Redis unavailable, storing the recovery phone {} in memory. This is only valid for local development.", what);
            inMemoryEntries.put(key, new InMemoryEntry(value, System.currentTimeMillis() + (ttlSeconds * 1000L)));
            return true;
        }
        LOG.error("Unable to store the recovery phone {}, Redis is unavailable", what);
        return false;
    }

    private String read(String key) {
        String value = redisClient.get(key);
        if (value != null) {
            return value;
        }
        if (allowInMemoryStore) {
            InMemoryEntry inMemory = inMemoryEntries.get(key);
            if (inMemory != null) {
                if (inMemory.isExpired()) {
                    inMemoryEntries.remove(key);
                    return null;
                }
                return inMemory.getValue();
            }
        }
        return null;
    }

    private void delete(String key) {
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
