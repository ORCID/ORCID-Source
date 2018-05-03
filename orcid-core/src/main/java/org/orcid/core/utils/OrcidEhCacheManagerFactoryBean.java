package org.orcid.core.utils;

import java.io.File;

import javax.annotation.Resource;

import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class OrcidEhCacheManagerFactoryBean implements FactoryBean<PersistentCacheManager> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidEhCacheManagerFactoryBean.class);

    private static PersistentCacheManager persistentCacheManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    private String getStoragePath() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!tmpDir.endsWith(File.separator)) {
            tmpDir += File.separator;
        }
        return tmpDir + "ehcache" + File.separator + orcidUrlManager.getAppName() + File.separator + "programmatic";
    }

    @Override
    public PersistentCacheManager getObject() throws Exception {
        if (persistentCacheManager == null) {
            String storagePath = getStoragePath();
            LOGGER.info("Cache manager dir: {}", storagePath);
            persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(storagePath)).build(true);
        }
        return persistentCacheManager;
    }

    @Override
    public Class<?> getObjectType() {
        return PersistentCacheManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
