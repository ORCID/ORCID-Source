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

    void remove(K key);

}
