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

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ProfileCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orcid;
    private long lastModified;
    private String releaseName;

    public ProfileCacheKey(String orcid, long lastModified, String releaseName) {
        this.orcid = orcid;
        this.lastModified = lastModified;
        this.releaseName = releaseName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
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
        ProfileCacheKey other = (ProfileCacheKey) obj;
        if (lastModified != other.lastModified)
            return false;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
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
        return "ProfileCacheKey [orcid=" + orcid + ", lastModified=" + lastModified + ", releaseName=" + releaseName + "]";
    }    
}
