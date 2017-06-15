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
package org.orcid.jaxb.model.client_v2;

import java.io.Serializable;
import java.util.Set;

import org.orcid.jaxb.model.clientgroup.ClientType;

public class Client implements Serializable {
    private static final long serialVersionUID = 5085242454098119135L;
    private String id;
    private String name;
    private String description;
    private String website;
    private String groupProfileId;
    private String authenticationProviderId;
    private String emailAccessReason;
    private ClientType clientType;
    private boolean allowAutoDeprecate = false;
    private boolean persistentTokensEnabled = false;
    private Set<String> clientScopes;
    private Set<String> resourceId;
    private Set<String> authorizedGrantTypes;
    private Set<String> grantedAuthorities;
    private Set<CustomEmail> customEmails;
    private Set<ClientSecret> clientSecrets;
    private Set<ClientRedirectUri> clientRedirectUris;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGroupProfileId() {
        return groupProfileId;
    }

    public void setGroupProfileId(String groupProfileId) {
        this.groupProfileId = groupProfileId;
    }

    public String getAuthenticationProviderId() {
        return authenticationProviderId;
    }

    public void setAuthenticationProviderId(String authenticationProviderId) {
        this.authenticationProviderId = authenticationProviderId;
    }

    public String getEmailAccessReason() {
        return emailAccessReason;
    }

    public void setEmailAccessReason(String emailAccessReason) {
        this.emailAccessReason = emailAccessReason;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public boolean isAllowAutoDeprecate() {
        return allowAutoDeprecate;
    }

    public void setAllowAutoDeprecate(boolean allowAutoDeprecate) {
        this.allowAutoDeprecate = allowAutoDeprecate;
    }

    public boolean isPersistentTokensEnabled() {
        return persistentTokensEnabled;
    }

    public void setPersistentTokensEnabled(boolean persistentTokensEnabled) {
        this.persistentTokensEnabled = persistentTokensEnabled;
    }

    public Set<String> getClientScopes() {
        return clientScopes;
    }

    public void setClientScopes(Set<String> clientScopes) {
        this.clientScopes = clientScopes;
    }

    public Set<String> getResourceId() {
        return resourceId;
    }

    public void setResourceId(Set<String> resourceId) {
        this.resourceId = resourceId;
    }

    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public Set<String> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Set<String> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public Set<CustomEmail> getCustomEmails() {
        return customEmails;
    }

    public void setCustomEmails(Set<CustomEmail> customEmails) {
        this.customEmails = customEmails;
    }

    public Set<ClientSecret> getClientSecrets() {
        return clientSecrets;
    }

    public void setClientSecrets(Set<ClientSecret> clientSecrets) {
        this.clientSecrets = clientSecrets;
    }

    public Set<ClientRedirectUri> getClientRedirectUris() {
        return clientRedirectUris;
    }

    public void setClientRedirectUris(Set<ClientRedirectUri> clientRedirectUris) {
        this.clientRedirectUris = clientRedirectUris;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allowAutoDeprecate ? 1231 : 1237);
        result = prime * result + ((authenticationProviderId == null) ? 0 : authenticationProviderId.hashCode());
        result = prime * result + ((authorizedGrantTypes == null) ? 0 : authorizedGrantTypes.hashCode());
        result = prime * result + ((clientRedirectUris == null) ? 0 : clientRedirectUris.hashCode());
        result = prime * result + ((clientScopes == null) ? 0 : clientScopes.hashCode());
        result = prime * result + ((clientSecrets == null) ? 0 : clientSecrets.hashCode());
        result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
        result = prime * result + ((customEmails == null) ? 0 : customEmails.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((emailAccessReason == null) ? 0 : emailAccessReason.hashCode());
        result = prime * result + ((grantedAuthorities == null) ? 0 : grantedAuthorities.hashCode());
        result = prime * result + ((groupProfileId == null) ? 0 : groupProfileId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (persistentTokensEnabled ? 1231 : 1237);
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((website == null) ? 0 : website.hashCode());
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
        Client other = (Client) obj;
        if (allowAutoDeprecate != other.allowAutoDeprecate)
            return false;
        if (authenticationProviderId == null) {
            if (other.authenticationProviderId != null)
                return false;
        } else if (!authenticationProviderId.equals(other.authenticationProviderId))
            return false;
        if (authorizedGrantTypes == null) {
            if (other.authorizedGrantTypes != null)
                return false;
        } else if (!authorizedGrantTypes.equals(other.authorizedGrantTypes))
            return false;
        if (clientRedirectUris == null) {
            if (other.clientRedirectUris != null)
                return false;
        } else if (!clientRedirectUris.equals(other.clientRedirectUris))
            return false;
        if (clientScopes == null) {
            if (other.clientScopes != null)
                return false;
        } else if (!clientScopes.equals(other.clientScopes))
            return false;
        if (clientSecrets == null) {
            if (other.clientSecrets != null)
                return false;
        } else if (!clientSecrets.equals(other.clientSecrets))
            return false;
        if (clientType != other.clientType)
            return false;
        if (customEmails == null) {
            if (other.customEmails != null)
                return false;
        } else if (!customEmails.equals(other.customEmails))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (emailAccessReason == null) {
            if (other.emailAccessReason != null)
                return false;
        } else if (!emailAccessReason.equals(other.emailAccessReason))
            return false;
        if (grantedAuthorities == null) {
            if (other.grantedAuthorities != null)
                return false;
        } else if (!grantedAuthorities.equals(other.grantedAuthorities))
            return false;
        if (groupProfileId == null) {
            if (other.groupProfileId != null)
                return false;
        } else if (!groupProfileId.equals(other.groupProfileId))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (persistentTokensEnabled != other.persistentTokensEnabled)
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (website == null) {
            if (other.website != null)
                return false;
        } else if (!website.equals(other.website))
            return false;
        return true;
    }
}
