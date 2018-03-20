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

import java.io.Serializable;

import org.orcid.utils.ReleaseNameUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
class GenericCacheKey<K> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K baseKey;
    private long profileLastModified;
    private String releaseName = ReleaseNameUtils.getReleaseName();

    public GenericCacheKey(K baseKey, long profileLastModified) {
        this.baseKey = baseKey;
        this.profileLastModified = profileLastModified;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseKey == null) ? 0 : baseKey.hashCode());
        result = prime * result + (int) (profileLastModified ^ (profileLastModified >>> 32));
        result = prime * result + ((releaseName == null) ? 0 : releaseName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        GenericCacheKey other = (GenericCacheKey) obj;
        if (baseKey == null) {
            if (other.baseKey != null)
                return false;
        } else if (!baseKey.equals(other.baseKey))
            return false;
        if (profileLastModified != other.profileLastModified)
            return false;
        if (releaseName == null) {
            if (other.releaseName != null)
                return false;
        } else if (!releaseName.equals(other.releaseName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GenericCacheKey [baseKey=" + baseKey + ", profileLastModified=" + profileLastModified + ", releaseName=" + releaseName + "]";
    }

}
