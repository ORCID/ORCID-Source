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
package org.orcid.core.cache.impl;

import java.util.Date;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.Retriever;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.jpa.entities.OrcidAware;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 
 * @author Will Simpson
 *
 */
public class GenericCacheManagerImpl<K extends OrcidAware, V> implements GenericCacheManager<K, V> {

    private Cache cache;
    private Retriever<K, V> retriever;

    private ProfileEntityManagerReadOnly profileEntityManager;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setProfileEntityManager(ProfileEntityManagerReadOnly profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    public void setRetriever(Retriever<K, V> retriever) {
        this.retriever = retriever;
    }

    @Override
    public V retrieve(K key) {
        GenericCacheKey<K> genericKey = createGenericKey(key);
        if(genericKey == null) {
            return null;
        }
        Element element = null;
        try {
            cache.acquireReadLockOnKey(genericKey);
            element = cache.get(genericKey);
        } finally {
            cache.releaseReadLockOnKey(genericKey);
        }
        if (element != null) {
            return extractObjectValue(element);
        } else {
            try {
                cache.acquireWriteLockOnKey(genericKey);
                element = cache.get(genericKey);
                if (element != null) {
                    return extractObjectValue(element);
                } else {
                    // Note that we retrieve from the retriever using key (which
                    // does not contain profile last modified or release name)
                    // not genericKey.
                    V value = retriever.retrieve(key);
                    cache.put(new Element(genericKey, value));
                    return value;
                }
            } finally {
                cache.releaseWriteLockOnKey(genericKey);
            }
        }
    }

    private GenericCacheKey<K> createGenericKey(K key) {
        Date dbDate = profileEntityManager.getLastModifiedDate(key.getOrcid());
        if(dbDate == null) {
            return null;
        }
        return new GenericCacheKey<K>(key, dbDate.getTime());
    }

    @SuppressWarnings("unchecked")
    private V extractObjectValue(Element element) {
        return (V) element.getObjectValue();
    }

    @Override
    public void remove(K key) {
        cache.remove(createGenericKey(key));
    }

}
