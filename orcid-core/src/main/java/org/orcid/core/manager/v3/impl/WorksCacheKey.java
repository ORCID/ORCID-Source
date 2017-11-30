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
package org.orcid.core.manager.v3.impl;

import java.io.Serializable;

public class WorksCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orcid;
    private long lastModified;

    public WorksCacheKey(String orcid, long lastModified) {
        this.orcid = orcid;
        this.lastModified = lastModified;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
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
        WorksCacheKey other = (WorksCacheKey) obj;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorksCacheKey [orcid=" + orcid + ", lastModified=" + lastModified + "]";
    }    
    
}
