package org.orcid.frontend.spring.session.redis;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.session.FlushMode;
import org.springframework.session.IndexResolver;
import org.springframework.session.SaveMode;
import org.springframework.session.Session;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Configuration(
        proxyBeanMethods = false
)
public class OrcidRedisHttpSessionConfiguration extends SpringHttpSessionConfiguration implements BeanClassLoaderAware, EmbeddedValueResolverAware, ImportAware {

    static final String DEFAULT_CLEANUP_CRON = "0 * * * * *";
    private Integer maxInactiveIntervalInSeconds = 1800;
    private String redisNamespace = "spring:session";
    private FlushMode flushMode;
    private SaveMode saveMode;
    private String cleanupCron;
    private ConfigureRedisAction configureRedisAction;
    private RedisConnectionFactory redisConnectionFactory;
    private IndexResolver<Session> indexResolver;
    private RedisSerializer<Object> defaultRedisSerializer;
    private ApplicationEventPublisher applicationEventPublisher;
    private Executor redisTaskExecutor;
    private Executor redisSubscriptionExecutor;
    private List<SessionRepositoryCustomizer<OrcidRedisIndexedSessionRepository>> sessionRepositoryCustomizers;
    private ClassLoader classLoader;
    private StringValueResolver embeddedValueResolver;

    public OrcidRedisHttpSessionConfiguration() {
        this.flushMode = FlushMode.ON_SAVE;
        this.saveMode = SaveMode.ON_SET_ATTRIBUTE;
        this.cleanupCron = "0 * * * * *";
        this.configureRedisAction = new ConfigureNotifyKeyspaceEventsAction();
    }

    @Bean
    public OrcidRedisIndexedSessionRepository sessionRepository() {
        RedisTemplate<Object, Object> redisTemplate = this.createRedisTemplate();
        OrcidRedisIndexedSessionRepository sessionRepository = new OrcidRedisIndexedSessionRepository(redisTemplate);
        sessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
        if (this.indexResolver != null) {
            sessionRepository.setIndexResolver(this.indexResolver);
        }

        if (this.defaultRedisSerializer != null) {
            sessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }

        sessionRepository.setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
        if (StringUtils.hasText(this.redisNamespace)) {
            sessionRepository.setRedisKeyNamespace(this.redisNamespace);
        }

        sessionRepository.setFlushMode(this.flushMode);
        sessionRepository.setSaveMode(this.saveMode);
        int database = this.resolveDatabase();
        sessionRepository.setDatabase(database);
        this.sessionRepositoryCustomizers.forEach((sessionRepositoryCustomizer) -> {
            sessionRepositoryCustomizer.customize(sessionRepository);
        });
        return sessionRepository;
    }

    @Bean
    public RedisMessageListenerContainer springSessionRedisMessageListenerContainer(OrcidRedisIndexedSessionRepository sessionRepository) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(this.redisConnectionFactory);
        if (this.redisTaskExecutor != null) {
            container.setTaskExecutor(this.redisTaskExecutor);
        }

        if (this.redisSubscriptionExecutor != null) {
            container.setSubscriptionExecutor(this.redisSubscriptionExecutor);
        }

        container.addMessageListener(sessionRepository, Arrays.asList(new ChannelTopic(sessionRepository.getSessionDeletedChannel()), new ChannelTopic(sessionRepository.getSessionExpiredChannel())));
        container.addMessageListener(sessionRepository, Collections.singletonList(new PatternTopic(sessionRepository.getSessionCreatedChannelPrefix() + "*")));
        return container;
    }

    @Bean
    public InitializingBean enableRedisKeyspaceNotificationsInitializer() {
        return new OrcidRedisHttpSessionConfiguration.EnableRedisKeyspaceNotificationsInitializer(this.redisConnectionFactory, this.configureRedisAction);
    }

    public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds) {
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    public void setRedisNamespace(String namespace) {
        this.redisNamespace = namespace;
    }

    /** @deprecated */
    @Deprecated
    public void setRedisFlushMode(RedisFlushMode redisFlushMode) {
        Assert.notNull(redisFlushMode, "redisFlushMode cannot be null");
        this.setFlushMode(redisFlushMode.getFlushMode());
    }

    public void setFlushMode(FlushMode flushMode) {
        Assert.notNull(flushMode, "flushMode cannot be null");
        this.flushMode = flushMode;
    }

    public void setSaveMode(SaveMode saveMode) {
        this.saveMode = saveMode;
    }

    public void setCleanupCron(String cleanupCron) {
        this.cleanupCron = cleanupCron;
    }

    @Autowired(
            required = false
    )
    public void setConfigureRedisAction(ConfigureRedisAction configureRedisAction) {
        this.configureRedisAction = configureRedisAction;
    }

    @Autowired
    public void setRedisConnectionFactory(@SpringSessionRedisConnectionFactory ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory, ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
        RedisConnectionFactory redisConnectionFactoryToUse = (RedisConnectionFactory)springSessionRedisConnectionFactory.getIfAvailable();
        if (redisConnectionFactoryToUse == null) {
            redisConnectionFactoryToUse = (RedisConnectionFactory)redisConnectionFactory.getObject();
        }

        this.redisConnectionFactory = redisConnectionFactoryToUse;
    }

    @Autowired(
            required = false
    )
    @Qualifier("springSessionDefaultRedisSerializer")
    public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Autowired(
            required = false
    )
    public void setIndexResolver(IndexResolver<Session> indexResolver) {
        this.indexResolver = indexResolver;
    }

    @Autowired(
            required = false
    )
    @Qualifier("springSessionRedisTaskExecutor")
    public void setRedisTaskExecutor(Executor redisTaskExecutor) {
        this.redisTaskExecutor = redisTaskExecutor;
    }

    @Autowired(
            required = false
    )
    @Qualifier("springSessionRedisSubscriptionExecutor")
    public void setRedisSubscriptionExecutor(Executor redisSubscriptionExecutor) {
        this.redisSubscriptionExecutor = redisSubscriptionExecutor;
    }

    @Autowired(
            required = false
    )
    public void setSessionRepositoryCustomizer(ObjectProvider<SessionRepositoryCustomizer<OrcidRedisIndexedSessionRepository>> sessionRepositoryCustomizers) {
        this.sessionRepositoryCustomizers = (List)sessionRepositoryCustomizers.orderedStream().collect(Collectors.toList());
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> attributeMap = importMetadata.getAnnotationAttributes(OrcidEnableRedisHttpSession.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);
        this.maxInactiveIntervalInSeconds = (Integer)attributes.getNumber("maxInactiveIntervalInSeconds");
        String redisNamespaceValue = attributes.getString("redisNamespace");
        if (StringUtils.hasText(redisNamespaceValue)) {
            this.redisNamespace = this.embeddedValueResolver.resolveStringValue(redisNamespaceValue);
        }

        FlushMode flushMode = (FlushMode)attributes.getEnum("flushMode");
        RedisFlushMode redisFlushMode = (RedisFlushMode)attributes.getEnum("redisFlushMode");
        if (flushMode == FlushMode.ON_SAVE && redisFlushMode != RedisFlushMode.ON_SAVE) {
            flushMode = redisFlushMode.getFlushMode();
        }

        this.flushMode = flushMode;
        this.saveMode = (SaveMode)attributes.getEnum("saveMode");
        String cleanupCron = attributes.getString("cleanupCron");
        if (StringUtils.hasText(cleanupCron)) {
            this.cleanupCron = cleanupCron;
        }

    }

    private RedisTemplate<Object, Object> createRedisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null) {
            redisTemplate.setDefaultSerializer(this.defaultRedisSerializer);
        }

        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.setBeanClassLoader(this.classLoader);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private int resolveDatabase() {
        if (ClassUtils.isPresent("io.lettuce.core.RedisClient", (ClassLoader)null) && this.redisConnectionFactory instanceof LettuceConnectionFactory) {
            return ((LettuceConnectionFactory)this.redisConnectionFactory).getDatabase();
        } else {
            return ClassUtils.isPresent("redis.clients.jedis.Jedis", (ClassLoader)null) && this.redisConnectionFactory instanceof JedisConnectionFactory ? ((JedisConnectionFactory)this.redisConnectionFactory).getDatabase() : 0;
        }
    }

    static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean {
        private final RedisConnectionFactory connectionFactory;
        private ConfigureRedisAction configure;

        EnableRedisKeyspaceNotificationsInitializer(RedisConnectionFactory connectionFactory, ConfigureRedisAction configure) {
            this.connectionFactory = connectionFactory;
            this.configure = configure;
        }

        public void afterPropertiesSet() {
            if (this.configure != ConfigureRedisAction.NO_OP) {
                RedisConnection connection = this.connectionFactory.getConnection();

                try {
                    this.configure.configure(connection);
                } finally {
                    try {
                        connection.close();
                    } catch (Exception var8) {
                        Exception ex = var8;
                        LogFactory.getLog(this.getClass()).error("Error closing RedisConnection", ex);
                    }

                }

            }
        }
    }

    @EnableScheduling
    @Configuration(
            proxyBeanMethods = false
    )
    class SessionCleanupConfiguration implements SchedulingConfigurer {
        private final OrcidRedisIndexedSessionRepository sessionRepository;

        SessionCleanupConfiguration(OrcidRedisIndexedSessionRepository sessionRepository) {
            this.sessionRepository = sessionRepository;
        }

        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            OrcidRedisIndexedSessionRepository var10001 = this.sessionRepository;
            Objects.requireNonNull(var10001);
            taskRegistrar.addCronTask(var10001::cleanupExpiredSessions, OrcidRedisHttpSessionConfiguration.this.cleanupCron);
        }
    }
}
