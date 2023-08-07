package org.orcid.core.utils.cache.redis;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.annotation.Resource;

import org.orcid.utils.alerting.SlackManager;
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
    private static final int DEFAULT_TIMEOUT = 5000;
    
    private JedisPool pool;
    private SetParams setParams;
    
    @Resource
    private SlackManager slackManager;
    
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
        try {
            JedisClientConfig config = DefaultJedisClientConfig.builder().connectionTimeoutMillis(timeoutInMillis).timeoutMillis(timeoutInMillis)
                    .socketTimeoutMillis(timeoutInMillis).password(password).ssl(true).build();        
            pool = new JedisPool(new HostAndPort(redisHost, redisPort), config);            
            setParams = new SetParams().ex(cacheExpiryInSecs);  
            // Pool test
            try(Jedis jedis = pool.getResource()) {
                if(jedis.isConnected()) {
                    LOG.info("Connected to the Redis cache");
                }
            }
        } catch(Exception e) {
            LOG.error("Exception initializing Redis client", e);
            try {
                // Lets try to get the host name 
                InetAddress id = InetAddress.getLocalHost();  
                slackManager.sendSystemAlert("Unable to start Redis client on " + id.getHostName());
            } catch(UnknownHostException uhe) {
                // Lets try to get the IP address
                try(final DatagramSocket socket = new DatagramSocket()){
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    String ip = socket.getLocalAddress().getHostAddress();
                    slackManager.sendSystemAlert("Unable to start Redis client on IP " + ip);
                  } catch(SocketException | UnknownHostException se) {
                      slackManager.sendSystemAlert("Unable to start Redis client - Couldn't identify the machine");
                  }               
            }
        }
    }

    public boolean set(String key, String value) {
        if(pool != null) {
            try (Jedis jedis = pool.getResource()) {
                LOG.debug("Setting Key: {}", key);
                String result = jedis.set(key, value, setParams);
                return "OK".equalsIgnoreCase(result);
            }
        }
        return false;
    }

    public String get(String key) {
        if(pool != null) {
            try (Jedis jedis = pool.getResource()) {
                LOG.debug("Reading Key: {}" , key);
                return jedis.get(key);
            }
        }
        return null;
    }       
}
