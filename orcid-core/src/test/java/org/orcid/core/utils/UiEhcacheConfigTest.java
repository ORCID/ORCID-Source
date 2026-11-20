package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourceType;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.TierStatistics;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.xml.XmlConfiguration;
import org.hibernate.LazyInitializationException;
import org.hibernate.collection.spi.PersistentBag;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.orcid.persistence.dao.impl.PeerReviewDaoImpl;
import org.orcid.persistence.dao.impl.ProfileFundingDaoImpl;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class UiEhcacheConfigTest {

    private static final String EHCACHE_DIR_PROPERTY = "org.orcid.ehcache.dir";
    private static final String IDENTITY_COPIER = "org.ehcache.impl.copy.IdentityCopier";
    private static final String TEST_ORCID = "0000-0000-0000-0001";
    private static final String[] ACTIVITY_CACHES = { "fundings", "peer-reviews" };
    private static final String[] PUBLIC_CACHES = {
            "public-external-identifiers", "public-researcher-urls", "public-keywords", "public-address",
            "public-other-names", "public-emails" };
    private static final String[][] PUBLIC_PRIVATE_CACHE_PAIRS = {
            { "public-external-identifiers", "dao-external-identifiers" },
            { "public-researcher-urls", "researcher-urls" },
            { "public-keywords", "dao-keywords" },
            { "public-address", "dao-address" },
            { "public-other-names", "dao-other-names" },
            { "public-emails", "emails" } };

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String previousEhcacheDirectory;
    private Configuration uiConfiguration;

    @Before
    public void createCacheManager() throws Exception {
        previousEhcacheDirectory = System.getProperty(EHCACHE_DIR_PROPERTY);
        File storageDirectory = temporaryFolder.newFolder("ehcache");
        System.setProperty(EHCACHE_DIR_PROPERTY, storageDirectory.getAbsolutePath());

        URL configurationUrl = getClass().getClassLoader().getResource("ehcache_orcid-web.xml");
        assertNotNull("missing ehcache_orcid-web.xml", configurationUrl);
        uiConfiguration = new XmlConfiguration(configurationUrl);
    }

    @After
    public void closeCacheManager() {
        if (previousEhcacheDirectory == null) {
            System.clearProperty(EHCACHE_DIR_PROPERTY);
        } else {
            System.setProperty(EHCACHE_DIR_PROPERTY, previousEhcacheDirectory);
        }
    }

    @Test
    public void activityCachesUseLargerEntryAndNonpersistentDiskPoolsWithoutChangingTemplateBehavior() {
        for (String alias : ACTIVITY_CACHES) {
            assertCacheConfiguration(uiConfiguration, alias, 9000, 256);
        }
    }

    @Test
    public void publicCachesUseLargerPoolsWhileUnrelatedAndOtherDeploymentCachesRemainUnchanged() {
        for (String alias : PUBLIC_CACHES) {
            assertCacheConfiguration(uiConfiguration, alias, 9000, 128);
        }

        for (String alias : new String[] { "biography", "emails", "dao-keywords", "dao-address", "researcher-urls" }) {
            assertCacheConfiguration(uiConfiguration, alias, 4500, 64);
        }

        URL apiConfigurationUrl = getClass().getClassLoader().getResource("ehcache_orcid-api-web.xml");
        assertNotNull("missing ehcache_orcid-api-web.xml", apiConfigurationUrl);
        Configuration apiConfiguration = new XmlConfiguration(apiConfigurationUrl);
        assertCacheConfiguration(apiConfiguration, "fundings", 500, 16);
        assertCacheConfiguration(apiConfiguration, "peer-reviews", 500, 16);
        assertCacheConfiguration(apiConfiguration, "public-keywords", 500, 16);
        assertCacheConfiguration(apiConfiguration, "public-emails", 500, 16);
    }

    @Test
    public void publicAndPrivateCacheAliasesDoNotShareEntries() {
        try (CacheManager testCacheManager = createInMemoryCacheManager(allPublicAndPrivateAliases())) {
            for (String[] aliases : PUBLIC_PRIVATE_CACHE_PAIRS) {
                Cache<Serializable, Serializable> publicCache = testCacheManager.getCache(aliases[0], Serializable.class, Serializable.class);
                Cache<Serializable, Serializable> privateCache = testCacheManager.getCache(aliases[1], Serializable.class, Serializable.class);
                assertNotNull(aliases[0], publicCache);
                assertNotNull(aliases[1], privateCache);
                assertNotSame(aliases[0] + " and " + aliases[1] + " must be distinct caches", publicCache, privateCache);

                String key = "same-profile-version";
                publicCache.put(key, "public value");
                privateCache.put(key, "private value");
                assertEquals(aliases[0], "public value", publicCache.get(key));
                assertEquals(aliases[1], "private value", privateCache.get(key));
            }
        }
    }

    @Test
    public void realEhcachePromotesAnEvictedDetachedValueFromDiskAndExpiresEntries() throws Exception {
        DefaultStatisticsService statisticsService = new DefaultStatisticsService();
        try (PersistentCacheManager tieredCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .using(statisticsService)
                .with(CacheManagerBuilder.persistence(temporaryFolder.newFolder("tiered-cache")))
                .withCache("activity-test", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(Serializable.class, Serializable.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder()
                                        .heap(1, EntryUnit.ENTRIES)
                                        .disk(4, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(15))))
                .withCache("expiry-test", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(Serializable.class, Serializable.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, EntryUnit.ENTRIES))
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(100))))
                .build(true)) {
            Cache<Serializable, Serializable> activityCache = tieredCacheManager.getCache("activity-test", Serializable.class, Serializable.class);
            ProfileEntity detachedProfile = new ProfileEntity("0000-0000-0000-0002");
            detachedProfile.setAuthorities(new PersistentBag<>(null));
            activityCache.put("detached-profile", detachedProfile);
            activityCache.put("recent-entry", "recent value");

            CacheStatistics statistics = statisticsService.getCacheStatistics("activity-test");
            TierStatistics diskTier = findDiskTier(statistics);
            assertTrue("a lower-tier entry must be present before testing promotion", diskTier.getMappings() > 0);
            long diskHitsBeforePromotion = diskTier.getHits();
            ProfileEntity promotedProfile = (ProfileEntity) activityCache.get("detached-profile");
            assertNotNull(promotedProfile);
            assertTrue("the detached value should be read from disk", diskTier.getHits() > diskHitsBeforePromotion);
            assertFalse(((PersistentBag<?>) promotedProfile.getAuthorities()).wasInitialized());
            assertTrue(statistics.getCacheHits() > 0);

            Cache<Serializable, Serializable> expiryCache = tieredCacheManager.getCache("expiry-test", Serializable.class, Serializable.class);
            expiryCache.put("expires", "soon");
            Thread.sleep(150);
            assertNull(expiryCache.get("expires"));
        }
    }

    @Test
    public void activityCachesHandleDetachedLazyValuesAndUseLastModifiedVersionedKeys() throws Exception {
        try (CacheManager testCacheManager = createInMemoryCacheManager(ACTIVITY_CACHES)) {
            Cache<Serializable, Serializable> peerReviews = testCacheManager.getCache("peer-reviews", Serializable.class, Serializable.class);
            assertNotNull(peerReviews);

            ProfileEntity detachedProfile = new ProfileEntity("0000-0000-0000-0001");
            PersistentBag<OrcidGrantedAuthority> authorities = new PersistentBag<>(null);
            detachedProfile.setAuthorities(authorities);
            try {
                assertNotNull(authorities.toString());
                fail("the test fixture must contain an uninitialized detached Hibernate collection");
            } catch (LazyInitializationException expected) {
                // Verify that the cache operation does not size or initialize the detached collection.
            }

            peerReviews.put("detached-profile", detachedProfile);
            ProfileEntity cachedProfile = (ProfileEntity) peerReviews.get("detached-profile");
            assertNotNull(cachedProfile);
            assertFalse(((PersistentBag<?>) cachedProfile.getAuthorities()).wasInitialized());

            assertVersionedKeyRefresh("peer-reviews", peerReviews, PeerReviewDaoImpl.class);

            Cache<Serializable, Serializable> fundings = testCacheManager.getCache("fundings", Serializable.class, Serializable.class);
            assertNotNull(fundings);
            assertVersionedKeyRefresh("fundings", fundings, ProfileFundingDaoImpl.class);
        }
    }

    private void assertVersionedKeyRefresh(String alias, Cache<Serializable, Serializable> cache, Class<?> daoClass) throws Exception {
        Method method = daoClass.getMethod("getByUser", String.class, long.class);
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        assertNotNull(alias + " DAO method must remain cacheable", cacheable);
        assertTrue(alias + " DAO must use the expected cache", Arrays.asList(cacheable.value()).contains(alias));

        SpelExpressionParser parser = new SpelExpressionParser();
        Expression keyExpression = parser.parseExpression(cacheable.key());
        String oldKey = evaluateKey(keyExpression, 100L);
        String newKey = evaluateKey(keyExpression, 200L);
        assertNotEquals(alias + " last-modified changes must create a new key", oldKey, newKey);

        cache.put(oldKey, "old profile snapshot");
        cache.put(newKey, "fresh profile snapshot");
        assertEquals(alias, "fresh profile snapshot", cache.get(newKey));
        assertEquals(alias, "old profile snapshot", cache.get(oldKey));
    }

    private String evaluateKey(Expression expression, long lastModified) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("userOrcid", TEST_ORCID);
        context.setVariable("lastModified", lastModified);
        return expression.getValue(context, String.class);
    }

    private String valueCopierClassName(CacheConfiguration<?, ?> cacheConfiguration) {
        Collection<?> serviceConfigurations = cacheConfiguration.getServiceConfigurations();
        for (Object serviceConfiguration : serviceConfigurations) {
            if (serviceConfiguration instanceof DefaultCopierConfiguration<?> copierConfiguration) {
                if (DefaultCopierConfiguration.Type.VALUE.equals(copierConfiguration.getType())) {
                    return copierConfiguration.getClazz().getName();
                }
            }
        }
        return null;
    }

    private void assertCacheConfiguration(Configuration configuration, String alias, long heapEntries, long diskMegabytes) {
        CacheConfiguration<?, ?> cacheConfiguration = configuration.getCacheConfigurations().get(alias);
        assertNotNull("missing cache " + alias, cacheConfiguration);

        SizedResourcePool heap = cacheConfiguration.getResourcePools().getPoolForResource(ResourceType.Core.HEAP);
        assertEquals(alias, EntryUnit.ENTRIES, heap.getUnit());
        assertEquals(alias, heapEntries, heap.getSize());

        SizedResourcePool disk = cacheConfiguration.getResourcePools().getPoolForResource(ResourceType.Core.DISK);
        assertEquals(alias, MemoryUnit.MB, disk.getUnit());
        assertEquals(alias, diskMegabytes, disk.getSize());
        assertFalse(alias + " disk tier must remain nonpersistent", disk.isPersistent());

        @SuppressWarnings("unchecked")
        ExpiryPolicy<Object, Object> expiryPolicy = (ExpiryPolicy<Object, Object>) cacheConfiguration.getExpiryPolicy();
        assertEquals(alias, Duration.ofMinutes(15), expiryPolicy.getExpiryForCreation("key", "value"));
        assertEquals(alias, Duration.ofMinutes(15), expiryPolicy.getExpiryForAccess("key", () -> "value"));
        assertEquals(alias, IDENTITY_COPIER, valueCopierClassName(cacheConfiguration));
    }

    private TierStatistics findDiskTier(CacheStatistics statistics) {
        for (Map.Entry<String, TierStatistics> tier : statistics.getTierStatistics().entrySet()) {
            if ("disk".equalsIgnoreCase(tier.getKey())) {
                return tier.getValue();
            }
        }
        throw new AssertionError("Ehcache statistics did not expose a disk tier: " + statistics.getTierStatistics().keySet());
    }

    private CacheManager createInMemoryCacheManager(String... aliases) {
        CacheManagerBuilder<CacheManager> builder = CacheManagerBuilder.newCacheManagerBuilder();
        for (String alias : aliases) {
            builder = builder.withCache(alias, CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Serializable.class, Serializable.class,
                            ResourcePoolsBuilder.newResourcePoolsBuilder().heap(32, EntryUnit.ENTRIES))
                    .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(15))));
        }
        return builder.build(true);
    }

    private String[] allPublicAndPrivateAliases() {
        return Arrays.stream(PUBLIC_PRIVATE_CACHE_PAIRS)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }
}
