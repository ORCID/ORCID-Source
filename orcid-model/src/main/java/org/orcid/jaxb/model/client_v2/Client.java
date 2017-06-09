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
import java.util.TreeSet;

import org.orcid.jaxb.model.clientgroup.ClientType;

public class Client implements Serializable {
    private static final long serialVersionUID = -8570136307981439796L;
    private String name;
    private String description;
    private ClientType clientType;
    private String clientWebsite;
    private String groupProfileId;
    private String authenticationProviderId;
    private String emailAccessReason;
    private int accessTokenValiditySeconds;
    private boolean persistentTokensEnabled = false;
    private boolean allowAutoDeprecate = false;
    private Set<String> clientScopes;
    private Set<String> resourceId;
    private Set<String> authorizedGrantTypes;
    private Set<String> grantedAuthorities;
    private Set<CustomEmail> customEmails;
    private Set<ClientSecret> clientSecrets;
    private Set<ClientRedirectUri> clientRedirectUris;

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

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getClientWebsite() {
        return clientWebsite;
    }

    public void setClientWebsite(String clientWebsite) {
        this.clientWebsite = clientWebsite;
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

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public boolean isPersistentTokensEnabled() {
        return persistentTokensEnabled;
    }

    public void setPersistentTokensEnabled(boolean persistentTokensEnabled) {
        this.persistentTokensEnabled = persistentTokensEnabled;
    }

    public boolean isAllowAutoDeprecate() {
        return allowAutoDeprecate;
    }

    public void setAllowAutoDeprecate(boolean allowAutoDeprecate) {
        this.allowAutoDeprecate = allowAutoDeprecate;
    }

    public Set<String> getClientScopes() {
        if (this.clientScopes == null) {
            this.clientScopes = new TreeSet<String>();
        }
        return clientScopes;
    }

    public void setClientScopes(Set<String> clientScopes) {
        this.clientScopes = clientScopes;
    }

    public Set<String> getResourceId() {
        if (this.resourceId == null) {
            this.resourceId = new TreeSet<String>();
        }
        return resourceId;
    }

    public void setResourceId(Set<String> resourceId) {
        this.resourceId = resourceId;
    }

    public Set<String> getAuthorizedGrantTypes() {
        if (this.authorizedGrantTypes == null) {
            this.authorizedGrantTypes = new TreeSet<String>();
        }
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public Set<String> getGrantedAuthorities() {
        if (this.grantedAuthorities == null) {
            this.grantedAuthorities = new TreeSet<String>();
        }
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Set<String> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public Set<CustomEmail> getCustomEmails() {
        if (this.customEmails == null) {
            this.customEmails = new TreeSet<CustomEmail>();
        }
        return customEmails;
    }

    public void setCustomEmails(Set<CustomEmail> customEmails) {
        this.customEmails = customEmails;
    }

    public Set<ClientSecret> getClientSecrets() {
        if (this.clientSecrets == null) {
            this.clientSecrets = new TreeSet<ClientSecret>();
        }
        return clientSecrets;
    }

    public void setClientSecrets(Set<ClientSecret> clientSecrets) {
        this.clientSecrets = clientSecrets;
    }

    public Set<ClientRedirectUri> getClientRedirectUris() {
        if (this.clientRedirectUris == null) {
            this.clientRedirectUris = new TreeSet<ClientRedirectUri>();
        }
        return clientRedirectUris;
    }

    public void setClientRedirectUris(Set<ClientRedirectUri> clientRedirectUris) {
        this.clientRedirectUris = clientRedirectUris;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accessTokenValiditySeconds;
        result = prime * result + (allowAutoDeprecate ? 1231 : 1237);
        result = prime * result + ((authenticationProviderId == null) ? 0 : authenticationProviderId.hashCode());
        result = prime * result + ((authorizedGrantTypes == null) ? 0 : authorizedGrantTypes.hashCode());
        result = prime * result + ((clientRedirectUris == null) ? 0 : clientRedirectUris.hashCode());
        result = prime * result + ((clientScopes == null) ? 0 : clientScopes.hashCode());
        result = prime * result + ((clientSecrets == null) ? 0 : clientSecrets.hashCode());
        result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
        result = prime * result + ((clientWebsite == null) ? 0 : clientWebsite.hashCode());
        result = prime * result + ((customEmails == null) ? 0 : customEmails.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((emailAccessReason == null) ? 0 : emailAccessReason.hashCode());
        result = prime * result + ((grantedAuthorities == null) ? 0 : grantedAuthorities.hashCode());
        result = prime * result + ((groupProfileId == null) ? 0 : groupProfileId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (persistentTokensEnabled ? 1231 : 1237);
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
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
        if (accessTokenValiditySeconds != other.accessTokenValiditySeconds)
            return false;
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
        if (clientWebsite == null) {
            if (other.clientWebsite != null)
                return false;
        } else if (!clientWebsite.equals(other.clientWebsite))
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
        return true;
    }

}
