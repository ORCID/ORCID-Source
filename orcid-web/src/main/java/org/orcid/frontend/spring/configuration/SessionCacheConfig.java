package org.orcid.frontend.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.JedisClientConfig;

import java.time.Duration;

@Configuration
@EnableRedisHttpSession
public class SessionCacheConfig extends AbstractHttpSessionApplicationInitializer {

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

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder();
        jedisClientConfigurationBuilder.useSsl().and().connectTimeout(timeoutDuration).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfigurationBuilder.build());
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
