package org.orcid.core.utils.cache.redis;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.resps.ScanResult;

public class RedisClient {

    private static final Logger LOG = LoggerFactory.getLogger(RedisClient.class);

    private static final int DEFAULT_CACHE_EXPIRY = 60;
    private static final int DEFAULT_TIMEOUT = 10000;
    public static final int MACH_KEY_BATCH_SIZE = 1000;

    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;
    private final int cacheExpiryInSecs;
    private final int clientTimeoutInMillis;
    private JedisPool pool;
    private SetParams defaultSetParams;

    @Resource
    private SlackManager slackManager;

    // Assume the connection to Redis is disabled by default
    private boolean enabled = false;

    public RedisClient(String redisHost, int redisPort, String password) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = password;
        this.cacheExpiryInSecs = DEFAULT_CACHE_EXPIRY;
        this.clientTimeoutInMillis = DEFAULT_TIMEOUT;
    }

    public RedisClient(String redisHost, int redisPort, String password, int cacheExpiryInSecs) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = password;
        this.cacheExpiryInSecs = cacheExpiryInSecs;
        this.clientTimeoutInMillis = DEFAULT_TIMEOUT;
    }

    public RedisClient(String redisHost, int redisPort, String password, int cacheExpiryInSecs, int clientTimeoutInMillis) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = password;
        this.cacheExpiryInSecs = cacheExpiryInSecs;
        this.clientTimeoutInMillis = clientTimeoutInMillis;
    }

    @PostConstruct
    private void init() {
        try {
            JedisClientConfig config = DefaultJedisClientConfig.builder().connectionTimeoutMillis(this.clientTimeoutInMillis).timeoutMillis(this.clientTimeoutInMillis)
                    .socketTimeoutMillis(this.clientTimeoutInMillis).password(this.redisPassword).ssl(true).build();
            pool = new JedisPool(new HostAndPort(this.redisHost, this.redisPort), config);
            defaultSetParams = new SetParams().ex(this.cacheExpiryInSecs);
            // Pool test
            try (Jedis jedis = pool.getResource()) {
                if (jedis.isConnected()) {
                    LOG.info("Connected to the Redis cache, elements will be cached for " + this.cacheExpiryInSecs + " seconds");
                    // As it was possible to make the connection, enable the
                    // client
                    enabled = true;
                }
            }
        } catch (Exception e) {
            LOG.error("Exception initializing Redis client", e);
            try {
                // Lets try to get the host name
                InetAddress id = InetAddress.getLocalHost();
                slackManager.sendSystemAlert("Unable to start Redis client on " + id.getHostName());
            } catch (UnknownHostException uhe) {
                // Lets try to get the IP address
                try (final DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    String ip = socket.getLocalAddress().getHostAddress();
                    slackManager.sendSystemAlert("Unable to start Redis client on IP " + ip);
                } catch (SocketException | UnknownHostException se) {
                    slackManager.sendSystemAlert("Unable to start Redis client - Couldn't identify the machine");
                }
            }
        }
    }

    public boolean set(String key, String value) {
        return set(key, value, defaultSetParams);
    }

    public boolean set(String key, String value, int cacheExpiryInSecs) {
        SetParams params = new SetParams().ex(cacheExpiryInSecs);
        return set(key, value, params);
    }

    private boolean set(String key, String value, SetParams params) {
        if (enabled && pool != null) {
            try (Jedis jedis = pool.getResource()) {
                LOG.debug("Setting Key: {}", key);
                String result = jedis.set(key, value, params);
                return "OK".equalsIgnoreCase(result);
            }
        }
        return false;
    }

    public String get(String key) {
        if (enabled && pool != null) {
            try (Jedis jedis = pool.getResource()) {
                LOG.debug("Reading Key: {}", key);
                return jedis.get(key);
            }
        }
        return null;
    }

    public boolean remove(String key) {
        if (enabled && pool != null) {
            try (Jedis jedis = pool.getResource()) {
                LOG.debug("Removing Key: {}", key);
                if (jedis.exists(key)) {
                    return jedis.del(key) > 0;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Retrieve the mapped key, value for all the keys that match the matchKey
     * parameter.
     * 
     * @param machKey
     *            the key pattern for which the mapped values are returned
     * @return
     * @throws JSONException
     */
    public HashMap<String, JSONObject> getAllValuesForKeyPattern(String matchKey) throws JSONException {
        HashMap<String, JSONObject> mappedValuesForKey = new HashMap<String, JSONObject>();
        // Connect to Redis
        try (Jedis jedis = pool.getResource()) {
            String cursor = "0";
            ScanParams scanParams = new ScanParams().match(matchKey).count(MACH_KEY_BATCH_SIZE);
            do {
                // Use SCAN to fetch matching keys in batches
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                List<String> keys = scanResult.getResult();

                // Print each key and its corresponding value
                for (String key : keys) {
                    mappedValuesForKey.put(key, new JSONObject(jedis.get(key)));
                }
            } while (!"0".equals(cursor)); // SCAN ends when cursor returns "0"
        }

        return mappedValuesForKey;
    }
}
