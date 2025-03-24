package org.orcid.frontend.spring.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.orcid.frontend.spring.session.redis.OrcidEnableRedisHttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.time.Duration;

@Configuration
@OrcidEnableRedisHttpSession
@Profile("!unitTests")
public class SessionCacheConfig extends AbstractHttpSessionApplicationInitializer {

    @Value("${org.orcid.core.utils.cache.session.redis.pool.idle.max:30}")
    private int poolIdleMax;
    @Value("${org.orcid.core.utils.cache.session.redis.pool.max:300}")
    private int poolMax;
    @Value("${org.orcid.core.utils.cache.session.redis.pool.wait.millis:1500}")
    private int poolWaitMillis;

    @Value("${org.orcid.core.utils.cache.session.redis.host:localhost}")
    private String host;
    @Value("${org.orcid.core.utils.cache.session.redis.port:6379}")
    private int port;
    @Value("${org.orcid.core.utils.cache.session.redis.password}")
    private String password;
    @Value("${org.orcid.core.utils.cache.session.redis.connection_timeout_millis:2000}")
    private int connectionTimeoutMillis;

    @Value("${org.orcid.core.session.cookie.domain:dev.orcid.org}")
    private String cookieDomain;

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

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setDomainName(cookieDomain);
        serializer.setCookiePath("/");
        return serializer;
    }
}
