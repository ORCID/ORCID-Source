package org.orcid.frontend.spring.session.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.Session;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This code is an adaptation from the original Spring Session Data Redis (https://spring.io/projects/spring-session,
 * source code https://github.com/spring-projects/spring-session/tree/main/spring-session-data-redis)
 * and has been modified to meet ORCID requirements.
 * */
public class OrcidRedisSessionExpirationPolicy {
    private static final Log logger = LogFactory.getLog(OrcidRedisSessionExpirationPolicy.class);
    private static final String SESSION_EXPIRES_PREFIX = "expires:";
    private final RedisOperations<Object, Object> redis;
    private final Function<Long, String> lookupExpirationKey;
    private final Function<String, String> lookupSessionKey;

    OrcidRedisSessionExpirationPolicy(RedisOperations<Object, Object> sessionRedisOperations, Function<Long, String> lookupExpirationKey, Function<String, String> lookupSessionKey) {
        this.redis = sessionRedisOperations;
        this.lookupExpirationKey = lookupExpirationKey;
        this.lookupSessionKey = lookupSessionKey;
    }

    void onDelete(Session session) {
        long toExpire = roundUpToNextMinute(expiresInMillis(session));
        String expireKey = this.getExpirationKey(toExpire);
        String entryToRemove = "expires:" + session.getId();
        this.redis.boundSetOps(expireKey).remove(new Object[]{entryToRemove});
    }

    void onExpirationUpdated(Long originalExpirationTimeInMilli, Session session) {
        String keyToExpire = "expires:" + session.getId();
        long toExpire = roundUpToNextMinute(expiresInMillis(session));
        long sessionExpireInSeconds;
        String sessionKey;
        if (originalExpirationTimeInMilli != null) {
            sessionExpireInSeconds = roundUpToNextMinute(originalExpirationTimeInMilli);
            if (toExpire != sessionExpireInSeconds) {
                sessionKey = this.getExpirationKey(sessionExpireInSeconds);
                this.redis.boundSetOps(sessionKey).remove(new Object[]{keyToExpire});
            }
        }

        sessionExpireInSeconds = session.getMaxInactiveInterval().getSeconds();
        sessionKey = this.getSessionKey(keyToExpire);
        if (sessionExpireInSeconds < 0L) {
            this.redis.boundValueOps(sessionKey).append("");
            this.redis.boundValueOps(sessionKey).persist();
            this.redis.boundHashOps(this.getSessionKey(session.getId())).persist();
        } else {
            String expireKey = this.getExpirationKey(toExpire);
            BoundSetOperations<Object, Object> expireOperations = this.redis.boundSetOps(expireKey);
            expireOperations.add(new Object[]{keyToExpire});
            long fiveMinutesAfterExpires = sessionExpireInSeconds + TimeUnit.MINUTES.toSeconds(5L);
            expireOperations.expire(fiveMinutesAfterExpires, TimeUnit.SECONDS);
            if (sessionExpireInSeconds == 0L) {
                this.redis.delete(sessionKey);
            } else {
                this.redis.boundValueOps(sessionKey).append("");
                this.redis.boundValueOps(sessionKey).expire(sessionExpireInSeconds, TimeUnit.SECONDS);
            }

            this.redis.boundHashOps(this.getSessionKey(session.getId())).expire(fiveMinutesAfterExpires, TimeUnit.SECONDS);
        }
    }

    String getExpirationKey(long expires) {
        return (String)this.lookupExpirationKey.apply(expires);
    }

    String getSessionKey(String sessionId) {
        return (String)this.lookupSessionKey.apply(sessionId);
    }

    void cleanExpiredSessions() {
        long now = System.currentTimeMillis();
        long prevMin = roundDownMinute(now);
        if (logger.isDebugEnabled()) {
            logger.debug("Cleaning up sessions expiring at " + new Date(prevMin));
        }

        String expirationKey = this.getExpirationKey(prevMin);
        Set<Object> sessionsToExpire = this.redis.boundSetOps(expirationKey).members();
        this.redis.delete(expirationKey);
        Iterator var7 = sessionsToExpire.iterator();

        while(var7.hasNext()) {
            Object session = var7.next();
            String sessionKey = this.getSessionKey((String)session);
            this.touch(sessionKey);
        }

    }

    private void touch(String key) {
        this.redis.hasKey(key);
    }

    static long expiresInMillis(Session session) {
        int maxInactiveInSeconds = (int)session.getMaxInactiveInterval().getSeconds();
        long lastAccessedTimeInMillis = session.getLastAccessedTime().toEpochMilli();
        return lastAccessedTimeInMillis + TimeUnit.SECONDS.toMillis((long)maxInactiveInSeconds);
    }

    static long roundUpToNextMinute(long timeInMs) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.add(12, 1);
        date.clear(13);
        date.clear(14);
        return date.getTimeInMillis();
    }

    static long roundDownMinute(long timeInMs) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.clear(13);
        date.clear(14);
        return date.getTimeInMillis();
    }
}
