package org.orcid.core.utils.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class RedisClient {

    private static final Logger LOG = LoggerFactory.getLogger(RedisClient.class);
    
    private static final int DEFAULT_CACHE_EXPIRY = 60;
    private static final int DEFAULT_TIMEOUT = 3000;
    
    private String redisHost;
    private int redisPort;
    private int cacheExpiryInSecs;
    private int timeoutInMillis;
    private JedisPool pool;
    private SetParams setParams;

    public RedisClient(String redisHost, int redisPort) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        // Default values
        this.cacheExpiryInSecs = DEFAULT_CACHE_EXPIRY;
        this.timeoutInMillis = DEFAULT_TIMEOUT;
        init();
    }

    public RedisClient(String redisHost, int redisPort, int cacheExpiryInSecs) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.cacheExpiryInSecs = cacheExpiryInSecs;
        this.timeoutInMillis = DEFAULT_TIMEOUT;
        init();
    }

    public RedisClient(String redisHost, int redisPort, int cacheExpiryInSecs, int clientTimeoutInMillis) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.cacheExpiryInSecs = cacheExpiryInSecs;
        this.timeoutInMillis = clientTimeoutInMillis;
        init();
    }

    private void init() {
        JedisClientConfig config = DefaultJedisClientConfig.builder().connectionTimeoutMillis(timeoutInMillis).timeoutMillis(timeoutInMillis)
                .socketTimeoutMillis(timeoutInMillis).build();
        pool = new JedisPool(new HostAndPort(redisHost, redisPort), config);
        setParams = new SetParams().ex(cacheExpiryInSecs);
    }

    public boolean set(String key, String value) {
        try (Jedis jedis = pool.getResource()) {
            LOG.debug("Setting Key: {}", key);
            String result = jedis.set(key, value, setParams);
            return "OK".equalsIgnoreCase(result);
        }
    }

    public String get(String key) {
        try (Jedis jedis = pool.getResource()) {
            LOG.debug("Reading Key: {}" , key);
            return jedis.get(key);
        }
    }    
}
