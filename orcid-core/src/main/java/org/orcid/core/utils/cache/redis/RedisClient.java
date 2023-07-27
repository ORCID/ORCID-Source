package org.orcid.core.utils.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.RedisCredentials;
import redis.clients.jedis.RedisCredentialsProvider;
import redis.clients.jedis.params.SetParams;

public class RedisClient {

    private static final Logger LOG = LoggerFactory.getLogger(RedisClient.class);
    
    private static final int DEFAULT_CACHE_EXPIRY = 60;
    private static final int DEFAULT_TIMEOUT = 5000;
    
    private JedisPool pool;
    private SetParams setParams;
    
    public RedisClient(String redisHost, int redisPort, String password) {
        init(redisHost, redisPort, password, DEFAULT_CACHE_EXPIRY, DEFAULT_TIMEOUT);
    }

    public RedisClient(String redisHost, int redisPort, String password, int cacheExpiryInSecs) {
        init(redisHost, redisPort, password, cacheExpiryInSecs, DEFAULT_TIMEOUT);
    }

    public RedisClient(String redisHost, int redisPort, String password, int cacheExpiryInSecs, int clientTimeoutInMillis) {
        init(redisHost, redisPort, password, cacheExpiryInSecs, clientTimeoutInMillis);
    }

    private void init(String redisHost, int redisPort, String password, int cacheExpiryInSecs, int timeoutInMillis) {
        JedisClientConfig config = DefaultJedisClientConfig.builder().connectionTimeoutMillis(timeoutInMillis).timeoutMillis(timeoutInMillis)
                .socketTimeoutMillis(timeoutInMillis).password(password).ssl(true).build();        
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
    
    //Trying to test TLS
    public static void main(String [] args) {
        RedisClient c = new RedisClient("xxx", 6379, "xxxxx");
        c.set("name", "angelito");
        c.set("name2", "angelito2");
        c.set("name3", "angelito3");
        System.out.println(c.get("name"));
        System.out.println(c.get("name2"));
        System.out.println(c.get("name3"));
    }
}
