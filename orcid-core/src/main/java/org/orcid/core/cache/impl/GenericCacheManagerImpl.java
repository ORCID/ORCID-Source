package org.orcid.core.cache.impl;

import java.util.Date;

import javax.annotation.Resource;

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

    @Resource(name = "profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly profileEntityManager;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setRetriever(Retriever<K, V> retriever) {
        this.retriever = retriever;
    }

    @Override
    public V retrieve(K key) {
        Date dbDate = profileEntityManager.getLastModifiedDate(key.getOrcid());
        GenericCacheKey<K> genericKey = new GenericCacheKey<K>(key, dbDate.getTime());
        V value = null;
        try {
            cache.acquireReadLockOnKey(genericKey);
            value = retrieveFromCache(genericKey);
        } finally {
            cache.releaseReadLockOnKey(genericKey);
        }
        if (value == null) {
            try {
                cache.acquireWriteLockOnKey(genericKey);
                value = retrieveFromCache(genericKey);
                if (value == null) {
                    // Note that we retrieve from the retriever using key (which
                    // does not contain profile last modified or release name)
                    // not genericKey.
                    value = retriever.retrieve(key);
                    cache.put(new Element(genericKey, value));
                }
            } finally {
                cache.releaseWriteLockOnKey(genericKey);
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private V retrieveFromCache(GenericCacheKey<K> genericKey) {
        Element element = cache.get(genericKey);
        return element != null ? (V) element.getObjectValue() : null;
    }

}
