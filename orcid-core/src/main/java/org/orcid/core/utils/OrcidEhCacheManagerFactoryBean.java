package org.orcid.core.utils;

import org.springframework.beans.factory.FactoryBean;

/**
 * Backwards-compatible FactoryBean that produces an OrcidCaffeineCacheManager.
 */
public class OrcidEhCacheManagerFactoryBean implements FactoryBean<OrcidCaffeineCacheManager> {

    private static OrcidCaffeineCacheManager cacheManager;

    @Override
    public OrcidCaffeineCacheManager getObject() throws Exception {
        if (cacheManager == null) {
            cacheManager = new OrcidCaffeineCacheManager();
        }
        return cacheManager;
    }

    @Override
    public Class<?> getObjectType() {
        return OrcidCaffeineCacheManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
