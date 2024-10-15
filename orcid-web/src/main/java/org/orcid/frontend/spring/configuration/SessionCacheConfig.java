package org.orcid.frontend.spring.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import java.time.Duration;

@Configuration
@EnableRedisHttpSession
public class SessionCacheConfig extends AbstractHttpSessionApplicationInitializer {

    @Value("${org.orcid.core.utils.cache.redis.pool.idle.max:30}")
    private int poolIdleMax;
    @Value("${org.orcid.core.utils.cache.redis.pool.max:300}")
    private int poolMax;
    @Value("${org.orcid.core.utils.cache.redis.pool.wait.millis:2000}")
    private int poolWaitMillis;

    @Value("${org.orcid.core.utils.cache.redis.host}")
    private String host;
    @Value("${org.orcid.core.utils.cache.redis.port}")
    private int port;
    @Value("${org.orcid.core.utils.cache.redis.password}")
    private String password;
    @Value("${org.orcid.core.utils.cache.redis.connection_timeout_millis:10000}")
    private int connectionTimeoutMillis;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        Duration timeoutDuration = Duration.ofMillis(connectionTimeoutMillis);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(this.poolIdleMax);
        poolConfig.setMaxTotal(this.poolMax);
        poolConfig.setMaxWaitMillis(this.poolWaitMillis);

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder();
        jedisClientConfigurationBuilder.useSsl().and().connectTimeout(timeoutDuration).usePooling().poolConfig(poolConfig).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfigurationBuilder.build());
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
