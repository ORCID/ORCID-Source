/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
