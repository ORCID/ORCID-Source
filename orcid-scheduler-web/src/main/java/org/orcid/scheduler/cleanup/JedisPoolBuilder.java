package org.orcid.scheduler.cleanup;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolBuilder {
    private String host;
    private int port;
    private String password;
    private int minIdle;
    private int poolTimeout;
    private int maxPoolSize;

    public JedisPoolBuilder(String host,
                            String port,
                            String password,
                            String poolTimeout,
                            String maxPoolSize) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.password = password;
        this.poolTimeout = Integer.parseInt(poolTimeout);
        this.maxPoolSize = Integer.parseInt(maxPoolSize);
    }

    public JedisPool build() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(this.minIdle);
        poolConfig.setMaxTotal(this.maxPoolSize);
        poolConfig.setMaxWaitMillis(this.poolTimeout);
        JedisPool pool = new JedisPool(poolConfig, this.host, this.port, this.poolTimeout, this.password, true);
        Jedis jedis = pool.getResource();
        if(!jedis.isConnected()) {
            throw new RuntimeException("Unable to start redis connection on RedisExpiredMainIndexCleanup");
        }
        jedis.close();
        return pool;
    }
}
