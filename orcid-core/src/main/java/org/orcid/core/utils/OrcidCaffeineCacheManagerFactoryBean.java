package org.orcid.core.utils;

import org.springframework.beans.factory.FactoryBean;

public class OrcidCaffeineCacheManagerFactoryBean implements FactoryBean<OrcidCaffeineCacheManager> {

    private static OrcidCaffeineCacheManager cacheManager;

    @Override
    public OrcidCaffeineCacheManager getObject() {
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