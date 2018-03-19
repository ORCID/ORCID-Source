package org.orcid.core.manager.impl;

import java.io.Serializable;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class ClientIdCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientId;

    private String releaseName;

    public ClientIdCacheKey(String clientId, String releaseName) {
        this.clientId = clientId;
        this.releaseName = releaseName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
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
        ClientIdCacheKey other = (ClientIdCacheKey) obj;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        if (releaseName == null) {
            if (other.releaseName != null)
                return false;
        } else if (!releaseName.equals(other.releaseName))
            return false;
        return true;
    }

}
