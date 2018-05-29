package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.orcid.core.manager.IdentityProviderManager;

/**
 * 
 * @author Will Simpson
 *
 */
public class IdentityProviderNameCacheEntryFactory implements CacheLoaderWriter<Object, Object> {

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Override
    public Object load(Object key) throws Exception {
        IdentityProviderNameCacheKey idpNameKey = (IdentityProviderNameCacheKey) key;
        return identityProviderManager.retrieveFreshIdentitifyProviderName(idpNameKey.getProviderId(), idpNameKey.getLocale());
    }

    @Override
    public void write(Object key, Object value) throws Exception {
        // Not needed, populating only
    }

    @Override
    public void delete(Object key) throws Exception {
        // Not needed, populating only
    }

}
