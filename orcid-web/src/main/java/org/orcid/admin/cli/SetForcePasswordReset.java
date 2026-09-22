package org.orcid.admin.cli;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.frontend.spring.session.redis.OrcidRedisIndexedSessionRepository;
import org.orcid.frontend.spring.configuration.OrcidBeanClassLoaderAware;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;

public class SetForcePasswordReset {

    private static final Logger LOG = LoggerFactory.getLogger(SetForcePasswordReset.class);
    private static final int FORCE_PASSWORD_RESET_BATCH_SIZE = 10000;
    private static final String CONFIG_FILE_KEY = "org.orcid.config.file";
    private static final String SESSION_REDIS_HOST_KEY = "org.orcid.core.utils.cache.session.redis.host";
    private static final String SESSION_REDIS_PORT_KEY = "org.orcid.core.utils.cache.session.redis.port";
    private static final String SESSION_REDIS_PASSWORD_KEY = "org.orcid.core.utils.cache.session.redis.password";
    private static final String SESSION_REDIS_POOL_IDLE_MAX_KEY = "org.orcid.core.utils.cache.session.redis.pool.idle.max";
    private static final String SESSION_REDIS_POOL_MAX_KEY = "org.orcid.core.utils.cache.session.redis.pool.max";
    private static final String SESSION_REDIS_POOL_WAIT_MILLIS_KEY = "org.orcid.core.utils.cache.session.redis.pool.wait.millis";
    private static final String SESSION_REDIS_CONNECTION_TIMEOUT_MILLIS_KEY = "org.orcid.core.utils.cache.session.redis.connection_timeout_millis";
    private static final String SESSION_REDIS_SSL_ENABLED_KEY = "org.orcid.core.utils.cache.session.redis.ssl.enabled";
    private static final String SESSION_TIMEOUT_KEY = "org.orcid.core.utils.cache.session.redis.session.timeout";
    private static final int DEFAULT_POOL_IDLE_MAX = 30;
    private static final int DEFAULT_POOL_MAX = 300;
    private static final int DEFAULT_POOL_WAIT_MILLIS = 1500;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 2000;
    private static final boolean DEFAULT_REDIS_SSL_ENABLED = true;
    private static final int DEFAULT_SESSION_TIMEOUT_SECONDS = 3600;

    @SuppressWarnings("unused")
    @Option(name = "-o", usage = "Comma-separated list of ORCID iDs", required = true)
    private String orcidIds;

    private ProfileDao profileDao;
    private OrcidRedisIndexedSessionRepository sessionRepository;
    private JedisConnectionFactory redisConnectionFactory;
    private ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        int exitCode = 0;
        SetForcePasswordReset setForcePasswordReset = new SetForcePasswordReset();
        CmdLineParser parser = new CmdLineParser(setForcePasswordReset);

        try {
            parser.parseArgument(args);
            setForcePasswordReset.validateArgs(parser);
            setForcePasswordReset.init();
            setForcePasswordReset.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            exitCode = 1;
        } catch (Throwable t) {
            LOG.error("Error forcing password reset", t);
            System.err.println(t.getMessage());
            exitCode = 2;
        } finally {
            setForcePasswordReset.close();
            System.exit(exitCode);
        }
    }

    void execute() {
        List<String> ids = parseOrcidIds();
        List<String> existingOrcidIds = new ArrayList<>();
        List<String> missingOrcidIds = new ArrayList<>();

        for (String orcidId : ids) {
            if (profileDao.orcidExists(orcidId)) {
                existingOrcidIds.add(orcidId);
            } else {
                missingOrcidIds.add(orcidId);
            }
        }

        if (existingOrcidIds.isEmpty()) {
            LOG.warn("No matching profiles found for the provided ORCID iDs");
            if (!missingOrcidIds.isEmpty()) {
                LOG.warn("ORCID iDs not found: {}", missingOrcidIds);
            }
            return;
        }

        Date executionDate = new Date();
        int updatedProfiles = updateForcePasswordResetInBatches(existingOrcidIds, executionDate);
        int evictedSessions = evictSessions(existingOrcidIds);

        LOG.info("Updated force_password_reset for {} profiles with execution date {}", updatedProfiles, executionDate);
        LOG.info("Evicted {} active sessions linked to the provided ORCID iDs", evictedSessions);
        if (!missingOrcidIds.isEmpty()) {
            LOG.warn("Skipped {} ORCID iDs that do not exist: {}", missingOrcidIds.size(), missingOrcidIds);
        }
    }

    void validateArgs(CmdLineParser parser) throws CmdLineException {
        List<String> ids = parseOrcidIds();
        if (ids.isEmpty()) {
            throw new CmdLineException(parser, "-o must include at least one ORCID iD");
        }

        List<String> invalidOrcidIds = new ArrayList<>();
        for (String orcidId : ids) {
            if (!OrcidStringUtils.isValidOrcid(orcidId)) {
                invalidOrcidIds.add(orcidId);
            }
        }

        if (!invalidOrcidIds.isEmpty()) {
            throw new CmdLineException(parser, "Invalid ORCID iD(s): " + StringUtils.join(invalidOrcidIds, ", "));
        }
    }

    private void init() {
        context = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        profileDao = context.getBean("profileDao", ProfileDao.class);
        sessionRepository = buildSessionRepository(loadSessionRedisProperties());
    }

    private void close() {
        if (redisConnectionFactory != null) {
            redisConnectionFactory.destroy();
        }
        if (context != null) {
            context.close();
        }
    }

    private OrcidRedisIndexedSessionRepository buildSessionRepository(Properties properties) {
        String host = requireProperty(properties, SESSION_REDIS_HOST_KEY);
        int port = parseRequiredPortProperty(properties);
        String password = requireProperty(properties, SESSION_REDIS_PASSWORD_KEY);
        int poolIdleMax = getIntProperty(properties, SESSION_REDIS_POOL_IDLE_MAX_KEY, DEFAULT_POOL_IDLE_MAX);
        int poolMax = getIntProperty(properties, SESSION_REDIS_POOL_MAX_KEY, DEFAULT_POOL_MAX);
        int poolWaitMillis = getIntProperty(properties, SESSION_REDIS_POOL_WAIT_MILLIS_KEY, DEFAULT_POOL_WAIT_MILLIS);
        int connectionTimeoutMillis = getIntProperty(properties, SESSION_REDIS_CONNECTION_TIMEOUT_MILLIS_KEY, DEFAULT_CONNECTION_TIMEOUT_MILLIS);
        boolean redisSslEnabled = isRedisSslEnabled(properties);
        int sessionTimeoutSeconds = getIntProperty(properties, SESSION_TIMEOUT_KEY, DEFAULT_SESSION_TIMEOUT_SECONDS);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);

        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(poolIdleMax);
        poolConfig.setMaxTotal(poolMax);
        poolConfig.setMaxWait(Duration.ofMillis(poolWaitMillis));

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder();
        if (redisSslEnabled) {
            jedisClientConfigurationBuilder.useSsl();
        }
        jedisClientConfigurationBuilder.connectTimeout(Duration.ofMillis(connectionTimeoutMillis)).usePooling().poolConfig(poolConfig);

        redisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfigurationBuilder.build());
        redisConnectionFactory.afterPropertiesSet();

        RedisSerializer<Object> redisSerializer = createRedisSerializer();
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(redisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();

        OrcidRedisIndexedSessionRepository repository = new OrcidRedisIndexedSessionRepository(redisTemplate);
        repository.setDefaultSerializer(redisSerializer);
        repository.setDefaultMaxInactiveInterval(sessionTimeoutSeconds);
        repository.setFlushMode(FlushMode.ON_SAVE);
        repository.setSaveMode(SaveMode.ON_SET_ATTRIBUTE);
        repository.setDatabase(redisConnectionFactory.getDatabase());
        return repository;
    }

    private Properties loadSessionRedisProperties() {
        String configFilePath = resolveConfigFilePath();
        if (StringUtils.isBlank(configFilePath)) {
            throw new IllegalStateException("Missing required system property: " + CONFIG_FILE_KEY);
        }

        Properties properties = new Properties();
        String[] configFiles = StringUtils.split(configFilePath, ',');
        if (configFiles == null) {
            return properties;
        }

        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        for (String configFile : configFiles) {
            String trimmedConfigFile = StringUtils.trimToEmpty(configFile);
            if (StringUtils.isBlank(trimmedConfigFile)) {
                continue;
            }

            Resource resource = StringUtils.startsWithIgnoreCase(trimmedConfigFile, "file:")
                    ? new FileSystemResource(normalizeConfigFilePath(trimmedConfigFile))
                    : resourceLoader.getResource(trimmedConfigFile);
            if (!resource.exists()) {
                throw new IllegalStateException("Could not load config file resource: " + trimmedConfigFile);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read config file resource: " + trimmedConfigFile, e);
            }
        }

        return properties;
    }

    private String resolveConfigFilePath() {
        Object systemEnvironmentValue = context.getEnvironment().getSystemEnvironment().get(CONFIG_FILE_KEY);
        String configFilePath = systemEnvironmentValue == null ? null : systemEnvironmentValue.toString();
        if (StringUtils.isNotBlank(configFilePath)) {
            return configFilePath;
        }
        return context.getEnvironment().getProperty(CONFIG_FILE_KEY);
    }

    private String normalizeConfigFilePath(String configFilePath) {
        String trimmedConfigFilePath = StringUtils.trimToEmpty(configFilePath);
        return StringUtils.removeStart(trimmedConfigFilePath, "file:");
    }

    private RedisSerializer<Object> createRedisSerializer() {
        OrcidBeanClassLoaderAware beanClassLoaderAware = new OrcidBeanClassLoaderAware();
        beanClassLoaderAware.setBeanClassLoader(getClass().getClassLoader());
        return beanClassLoaderAware.springSessionDefaultRedisSerializer();
    }

    private String requireProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return StringUtils.trimToEmpty(value);
    }

    private int parseRequiredPortProperty(Properties properties) {
        String value = requireProperty(properties, SESSION_REDIS_PORT_KEY);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer value for property: " + SESSION_REDIS_PORT_KEY, e);
        }
    }

    private int getIntProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(StringUtils.trimToEmpty(value));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer value for property: " + key, e);
        }
    }

    private boolean isRedisSslEnabled(Properties properties) {
        String value = properties.getProperty(SESSION_REDIS_SSL_ENABLED_KEY);
        if (StringUtils.isBlank(value)) {
            return DEFAULT_REDIS_SSL_ENABLED;
        }
        return Boolean.parseBoolean(StringUtils.trimToEmpty(value));
    }

    private List<String> parseOrcidIds() {
        Set<String> uniqueOrcidIds = new LinkedHashSet<>();
        if (StringUtils.isBlank(orcidIds)) {
            return new ArrayList<>();
        }

        String[] splitOrcidIds = StringUtils.split(orcidIds, ',');
        if (splitOrcidIds == null) {
            return new ArrayList<>();
        }

        for (String value : splitOrcidIds) {
            String trimmedValue = StringUtils.trimToEmpty(value);
            if (StringUtils.isNotBlank(trimmedValue)) {
                uniqueOrcidIds.add(trimmedValue);
            }
        }

        return new ArrayList<>(uniqueOrcidIds);
    }

    private int evictSessions(List<String> orcidIds) {
        int evictedSessions = 0;
        for (String orcidId : orcidIds) {
            Map<String, ?> activeSessions = sessionRepository.findByIndexNameAndIndexValue(
                    FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, orcidId);
            for (String sessionId : activeSessions.keySet()) {
                sessionRepository.deleteById(sessionId);
                evictedSessions++;
            }
        }
        return evictedSessions;
    }

    int updateForcePasswordResetInBatches(List<String> orcidIds, Date executionDate) {
        int updatedProfiles = 0;
        for (int i = 0; i < orcidIds.size(); i += FORCE_PASSWORD_RESET_BATCH_SIZE) {
            int batchEnd = Math.min(i + FORCE_PASSWORD_RESET_BATCH_SIZE, orcidIds.size());
            updatedProfiles += profileDao.updateForcePasswordReset(orcidIds.subList(i, batchEnd), executionDate);
        }
        return updatedProfiles;
    }
}
