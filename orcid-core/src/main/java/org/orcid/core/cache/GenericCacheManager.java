package org.orcid.core.cache;

import org.orcid.persistence.jpa.entities.OrcidAware;

/**
 * 
 * @author Will Simpson
 *
 * @param <K>
 *            The key used by the cache, should implement hash code and equals
 *            (but does not need to include profile last modified or release
 *            name, because they are added automatically)
 * @param <V>
 *            The type of object in the cache
 */
public interface GenericCacheManager<K extends OrcidAware, V> {

    V retrieve(K key);

}
