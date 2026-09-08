package org.orcid.core.manager.impl;

import jakarta.annotation.Resource;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.orcid.core.manager.IdentityProviderManager;

/**
 * 
 * @author Will Simpson
 *
 */
public class IdentityProviderNameCacheEntryFactory implements CacheLoader<Object, Object> {

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Override
    public Object load(Object key) throws Exception {
        IdentityProviderNameCacheKey idpNameKey = (IdentityProviderNameCacheKey) key;
        return identityProviderManager.retrieveFreshIdentitifyProviderName(idpNameKey.getProviderId(), idpNameKey.getLocale());
    }

}
