package org.orcid.core.utils;

import java.io.File;
import java.net.URI;

import jakarta.annotation.Resource;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class OrcidEhCacheManagerFactoryBean implements FactoryBean<CacheManager> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidEhCacheManagerFactoryBean.class);
    
    private static final String EHCACHE_DIR_PROPERTY = "org.orcid.ehcache.dir";

    /**
     * Must differ from the springCoreCacheManager URI (classpath:ehcache_*.xml), otherwise the two
     * cache managers collide. Becomes the CacheManager= key in the JMX ObjectName.
     */
    private static final URI PROGRAMMATIC_URI = URI.create("orcid-programmatic");

    private static CacheManager cacheManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    /**
     * Not a pure getter: when org.orcid.ehcache.dir is unset this also publishes the generated
     * directory as a system property. The ehcache_*.xml configs resolve their own
     * ${org.orcid.ehcache.dir}/APP_NAME/annotations persistence path from it, so this bean must be
     * built before springCoreCacheManager -- hence the depends-on in orcid-core-cache-config.xml.
     * Removing or relocating the setProperty call sends the XML caches to a literal
     * "${org.orcid.ehcache.dir}" directory.
     */
    private String getStoragePath() {
        String ehcacheDir = System.getProperty(EHCACHE_DIR_PROPERTY);
        if(StringUtils.isBlank(ehcacheDir)) {
            String tmpDir = System.getProperty("java.io.tmpdir");
            if (!tmpDir.endsWith(File.separator)) {
                tmpDir += File.separator;
            }
            String randomString = RandomStringUtils.randomAlphanumeric(16);
            ehcacheDir = tmpDir + "ehcache" + File.separator + randomString;
            System.setProperty(EHCACHE_DIR_PROPERTY, ehcacheDir);
        }
        return ehcacheDir + File.separator + orcidUrlManager.getAppName() + File.separator + "programmatic";
    }

    @Override
    public CacheManager getObject() throws Exception {
        if (cacheManager == null) {
            String storagePath = getStoragePath();
            LOGGER.info("Cache manager dir: {}", storagePath);
            EhcacheCachingProvider provider = (EhcacheCachingProvider) Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
            cacheManager = provider.getCacheManager(PROGRAMMATIC_URI,
                    new DefaultConfiguration(provider.getDefaultClassLoader(), new CacheManagerPersistenceConfiguration(new File(storagePath))));
        }
        return cacheManager;
    }

    @Override
    public Class<?> getObjectType() {
        return CacheManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
