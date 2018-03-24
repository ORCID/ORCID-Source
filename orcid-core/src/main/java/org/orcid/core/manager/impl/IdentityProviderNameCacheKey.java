package org.orcid.core.manager.impl;

import java.io.Serializable;
import java.util.Locale;

/**
 * 
 * @author Will Simpson
 *
 */
class IdentityProviderNameCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String providerId;
    private Locale locale;
    private String releaseName;

    IdentityProviderNameCacheKey(String providerId, Locale locale, String releaseName) {
        this.providerId = providerId;
        this.locale = locale;
        this.releaseName = releaseName;
    }

    public String getProviderId() {
        return providerId;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getReleaseName() {
        return releaseName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((providerId == null) ? 0 : providerId.hashCode());
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
        IdentityProviderNameCacheKey other = (IdentityProviderNameCacheKey) obj;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        if (providerId == null) {
            if (other.providerId != null)
                return false;
        } else if (!providerId.equals(other.providerId))
            return false;
        if (releaseName == null) {
            if (other.releaseName != null)
                return false;
        } else if (!releaseName.equals(other.releaseName))
            return false;
        return true;
    }

}
