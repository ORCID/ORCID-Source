package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.IdentityProviderManager;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class IdentityProviderNameCacheEntryFactory implements CacheEntryFactory {

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Override
    public Object createEntry(Object key) throws Exception {
        IdentityProviderNameCacheKey idpNameKey = (IdentityProviderNameCacheKey) key;
        return identityProviderManager.retrieveFreshIdentitifyProviderName(idpNameKey.getProviderId(), idpNameKey.getLocale());
    }

}
