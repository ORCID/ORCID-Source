package org.orcid.core.utils.cache.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import redis.clients.jedis.JedisClientConfig;

/**
 * TLS was hardcoded on, which meant a plaintext Redis never answered the handshake and every
 * connect blocked for the full socket timeout -- once per client, per Spring context. These tests
 * pin the flag and, more importantly, pin the default: production must keep getting TLS.
 *
 * Deliberately a plain unit test with no Spring context. RedisClient carries no
 * {@code @Profile("!unitTests")} (unlike SessionCacheConfig and JedisPoolBuilder), so any
 * context-loading test really attempts a TLS connect and waits out the timeout.
 */
public class RedisClientTest {

    private static final String HOST = "localhost";
    private static final int PORT = 6379;
    private static final String PASSWORD = "";
    private static final int EXPIRY = 600;
    private static final int TIMEOUT = 10000;

    @Test
    public void sslIsEnabledByDefaultOnEveryConstructor() {
        assertTrue("3-arg constructor must default to TLS",
                new RedisClient(HOST, PORT, PASSWORD).buildClientConfig().isSsl());
        assertTrue("4-arg constructor must default to TLS",
                new RedisClient(HOST, PORT, PASSWORD, EXPIRY).buildClientConfig().isSsl());
        assertTrue("5-arg constructor must default to TLS",
                new RedisClient(HOST, PORT, PASSWORD, EXPIRY, TIMEOUT).buildClientConfig().isSsl());
    }

    @Test
    public void sslCanBeTurnedOff() {
        JedisClientConfig config = new RedisClient(HOST, PORT, PASSWORD, EXPIRY, TIMEOUT, false).buildClientConfig();
        assertFalse("ssl=false must reach the Jedis config", config.isSsl());
    }

    @Test
    public void sslCanBeTurnedOnExplicitly() {
        JedisClientConfig config = new RedisClient(HOST, PORT, PASSWORD, EXPIRY, TIMEOUT, true).buildClientConfig();
        assertTrue(config.isSsl());
    }

    @Test
    public void aBlankPasswordMeansNoAuthentication() {
        // Jedis sends AUTH "" if a password is set at all, and a Redis without requirepass
        // rejects that outright, so a blank password must not reach the config.
        assertNull("empty password must not be sent",
                new RedisClient(HOST, PORT, "", EXPIRY, TIMEOUT, false).buildClientConfig().getPassword());
        assertNull("blank password must not be sent",
                new RedisClient(HOST, PORT, "   ", EXPIRY, TIMEOUT, false).buildClientConfig().getPassword());
        assertNull("null password must not be sent",
                new RedisClient(HOST, PORT, null, EXPIRY, TIMEOUT, false).buildClientConfig().getPassword());
    }

    @Test
    public void arealPasswordIsStillSent() {
        assertEquals("s3cret",
                new RedisClient(HOST, PORT, "s3cret", EXPIRY, TIMEOUT, true).buildClientConfig().getPassword());
    }

    @Test
    public void theOtherSettingsStillReachTheConfig() {
        JedisClientConfig config = new RedisClient(HOST, PORT, PASSWORD, EXPIRY, 1234, false).buildClientConfig();
        assertEquals(1234, config.getConnectionTimeoutMillis());
        assertEquals(1234, config.getSocketTimeoutMillis());
    }
}
