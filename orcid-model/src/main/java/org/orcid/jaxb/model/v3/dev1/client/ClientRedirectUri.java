package org.orcid.jaxb.model.v3.dev1.client;

import java.io.Serializable;
import java.util.Set;

import org.orcid.jaxb.model.message.ScopePathType;

public class ClientRedirectUri implements Serializable {
    private static final long serialVersionUID = 2095596438916124470L;
    private String redirectUri;
    private String redirectUriType;
    private String uriActType;
    private String uriGeoArea;
    private Set<ScopePathType> predefinedClientScopes;

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRedirectUriType() {
        return redirectUriType;
    }

    public void setRedirectUriType(String redirectUriType) {
        this.redirectUriType = redirectUriType;
    }

    public String getUriActType() {
        return uriActType;
    }

    public void setUriActType(String uriActType) {
        this.uriActType = uriActType;
    }

    public String getUriGeoArea() {
        return uriGeoArea;
    }

    public void setUriGeoArea(String uriGeoArea) {
        this.uriGeoArea = uriGeoArea;
    }

    public Set<ScopePathType> getPredefinedClientScopes() {
        return predefinedClientScopes;
    }

    public void setPredefinedClientScopes(Set<ScopePathType> predefinedClientScopes) {
        this.predefinedClientScopes = predefinedClientScopes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predefinedClientScopes == null) ? 0 : predefinedClientScopes.hashCode());
        result = prime * result + ((redirectUri == null) ? 0 : redirectUri.hashCode());
        result = prime * result + ((redirectUriType == null) ? 0 : redirectUriType.hashCode());
        result = prime * result + ((uriActType == null) ? 0 : uriActType.hashCode());
        result = prime * result + ((uriGeoArea == null) ? 0 : uriGeoArea.hashCode());
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
        ClientRedirectUri other = (ClientRedirectUri) obj;
        if (predefinedClientScopes == null) {
            if (other.predefinedClientScopes != null)
                return false;
        } else if (!predefinedClientScopes.equals(other.predefinedClientScopes))
            return false;
        if (redirectUri == null) {
            if (other.redirectUri != null)
                return false;
        } else if (!redirectUri.equals(other.redirectUri))
            return false;
        if (redirectUriType == null) {
            if (other.redirectUriType != null)
                return false;
        } else if (!redirectUriType.equals(other.redirectUriType))
            return false;
        if (uriActType == null) {
            if (other.uriActType != null)
                return false;
        } else if (!uriActType.equals(other.uriActType))
            return false;
        if (uriGeoArea == null) {
            if (other.uriGeoArea != null)
                return false;
        } else if (!uriGeoArea.equals(other.uriGeoArea))
            return false;
        return true;
    }

}
