package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

/**
 * @author Declan Newman
 */
@Entity
@Table(name = "client_details")
public class ClientDetailsEntity extends BaseEntity<String> implements ClientDetails, ProfileAware, Serializable {
    
    private static final long serialVersionUID = 1L;

    // Default is 20 years!
    public static final int DEFAULT_TOKEN_VALIDITY = 631138519;

    private String clientId;
    private String clientType;
    private String clientName;
    private String clientDescription;
    private String clientWebsite;
    private String clientSecret;
    private String decryptedClientSecret;
    private SortedSet<ClientSecretEntity> clientSecrets;
    private Set<ClientScopeEntity> clientScopes = Collections.emptySet();
    private Set<ClientResourceIdEntity> clientResourceIds = Collections.emptySet();
    private Set<ClientAuthorisedGrantTypeEntity> clientAuthorizedGrantTypes = Collections.emptySet();
    private SortedSet<ClientRedirectUriEntity> clientRegisteredRedirectUris;
    private List<ClientGrantedAuthorityEntity> clientGrantedAuthorities = Collections.emptyList();
    private String groupProfileId;
    private String authenticationProviderId;

    private Set<CustomEmailEntity> customEmails = Collections.emptySet();
    private int accessTokenValiditySeconds = DEFAULT_TOKEN_VALIDITY;
    private boolean persistentTokensEnabled = false;
    private String emailAccessReason;
    private boolean allowAutoDeprecate = false;
    
    private Set<MemberOBOWhitelistedClientEntity> oboWhitelist;

    public ClientDetailsEntity() {
    }

    public ClientDetailsEntity(String clientId) {
        this.clientId = clientId;
    }

    public ClientDetailsEntity(String clientId, String clientName) {
    	this.clientId = clientId;
    	this.clientName = clientName;
	}

	/**
     * This should be implemented by all entity classes to return the id of the
     * entity represented by the &lt;T&gt; generic argument
     * 
     * @return the id of the entity
     */
    @Id
    @Column(name = "client_details_id", length = 150)
    public String getId() {
        return clientId;
    }

    public void setId(String clientId) {
        this.clientId = clientId;
    }

    @Column(name = "client_type")
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @Column(name = "client_name", length = 255)
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Column(name = "client_description")
    public String getClientDescription() {
        return clientDescription;
    }

    public void setClientDescription(String clientDescription) {
        this.clientDescription = clientDescription;
    }

    @Column(name = "client_website")
    public String getClientWebsite() {
        return clientWebsite;
    }

    public void setClientWebsite(String clientWebsite) {
        this.clientWebsite = clientWebsite;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    public Set<ClientScopeEntity> getClientScopes() {
        return clientScopes;
    }

    public void setClientScopes(Set<ClientScopeEntity> clientScopes) {
        this.clientScopes = clientScopes;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    public Set<ClientResourceIdEntity> getClientResourceIds() {
        return clientResourceIds;
    }

    public void setClientResourceIds(Set<ClientResourceIdEntity> clientResourceIds) {
        this.clientResourceIds = clientResourceIds;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    public Set<ClientAuthorisedGrantTypeEntity> getClientAuthorizedGrantTypes() {
        return clientAuthorizedGrantTypes;
    }

    public void setClientAuthorizedGrantTypes(Set<ClientAuthorisedGrantTypeEntity> clientAuthorizedGrantTypes) {
        this.clientAuthorizedGrantTypes = clientAuthorizedGrantTypes;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    public SortedSet<ClientRedirectUriEntity> getClientRegisteredRedirectUris() {
        return clientRegisteredRedirectUris;
    }

    public void setClientRegisteredRedirectUris(SortedSet<ClientRedirectUriEntity> clientRegisteredRedirectUris) {
        this.clientRegisteredRedirectUris = clientRegisteredRedirectUris;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    public List<ClientGrantedAuthorityEntity> getClientGrantedAuthorities() {
        return clientGrantedAuthorities;
    }

    public void setClientGrantedAuthorities(List<ClientGrantedAuthorityEntity> clientGrantedAuthorities) {
        this.clientGrantedAuthorities = clientGrantedAuthorities;
    }    

    @Column(name = "group_orcid")
    @JoinColumn(name = "group_orcid")
    public String getGroupProfileId() {
        return groupProfileId;
    }

    public void setGroupProfileId(String groupProfileId) {
        this.groupProfileId = groupProfileId;
    }

    @Override
    @Transient
    public ProfileEntity getProfile() {        
        return new ProfileEntity(this.getGroupProfileId());
    }

    // Below are the overriden ClientDetails methods

    /**
     * The client id. This is a transient field (hence no setter). This is
     * effectively the same as returning the id from {@link #getId()}
     * 
     * @return The client id.
     */
    @Override
    @Transient
    public String getClientId() {
        return clientId;
    }

    /**
     * The resources that this client can access. Ignored if empty.
     * 
     * @return The resources of this client.
     */
    @Override
    @Transient
    public Set<String> getResourceIds() {
        Set<String> rids = new HashSet<String>();
        if (clientResourceIds != null && !clientResourceIds.isEmpty()) {
            for (ClientResourceIdEntity resourceIdEntity : clientResourceIds) {
                rids.add(resourceIdEntity.getResourceId());
            }
        }
        return rids;
    }

    /**
     * Whether a secret is required to authenticate this client.
     * 
     * @return Whether a secret is required to authenticate this client.
     */
    @Override
    @Transient
    public boolean isSecretRequired() {
        return StringUtils.hasText(clientSecret);
    }

    /**
     * The client secret. Ignored if the {@link #isSecretRequired() secret isn't
     * required}.
     * 
     * @return The client secret.
     */
    @Override
    @Transient
    public String getClientSecret() {
        return getDecryptedClientSecret();
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    public Set<ClientSecretEntity> getClientSecrets() {
        return clientSecrets;
    }

    public void setClientSecrets(SortedSet<ClientSecretEntity> clientSecrets) {
        this.clientSecrets = clientSecrets;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "clientDetailsEntity", orphanRemoval = true)
    public Set<CustomEmailEntity> getCustomEmails() {
        return customEmails;
    }

    public void setCustomEmails(Set<CustomEmailEntity> customEmails) {
        this.customEmails = customEmails;
    }
    
    @Column(name = "persistent_tokens_enabled")
    public boolean isPersistentTokensEnabled() {
        return persistentTokensEnabled;
    }

    public void setPersistentTokensEnabled(boolean persistentTokensEnabled) {
        this.persistentTokensEnabled = persistentTokensEnabled;
    }
    
    /**
     * Reason, if any, client wants to access users' private email addresses.
     * 
     * @return reason client wants to know private email addresses
     */
    @Column(name = "email_access_reason", length = 500)
    public String getEmailAccessReason() {
        return emailAccessReason;
    }

    public void setEmailAccessReason(String emailAccessReason) {
        this.emailAccessReason = emailAccessReason;
    }

    @Transient
    public String getClientSecretForJpa() {
        if (clientSecrets == null || clientSecrets.isEmpty()) {
            return null;
        }
        return clientSecrets.first().getClientSecret();
    }

    public void setClientSecretForJpa(String clientSecret) {
        if (clientSecrets == null) {
            clientSecrets = new TreeSet<>();
        }
        clientSecrets.add(new ClientSecretEntity(clientSecret, this));
    }

    public void setClientSecretForJpa(String clientSecret, boolean primary) {
        if (clientSecrets == null) {
            clientSecrets = new TreeSet<>();
        }
        clientSecrets.add(new ClientSecretEntity(clientSecret, this, primary));
    }

    @Transient
    public String getDecryptedClientSecret() {
        return decryptedClientSecret;
    }

    public void setDecryptedClientSecret(String decryptedClientSecret) {
        this.decryptedClientSecret = decryptedClientSecret;
    }

    /**
     * Whether this client is limited to a specific scope. If false, the scope
     * of the authentication request will be ignored.
     * 
     * @return Whether this client is limited to a specific scope.
     */
    @Override
    @Transient
    public boolean isScoped() {
        return this.clientScopes != null && !this.clientScopes.isEmpty();
    }

    /**
     * The scope of this client. Ignored if the {@link #isScoped() client isn't
     * scoped}.
     * 
     * @return The scope of this client.
     */
    @Override
    @Transient
    public Set<String> getScope() {
        Set<String> sps = new HashSet<String>();
        if (clientScopes != null && !clientScopes.isEmpty()) {
            for (ClientScopeEntity cse : clientScopes) {
                sps.add(cse.getScopeType());
            }
        }
        return sps;
    }

    /**
     * The grant types for which this client is authorized.
     * 
     * @return The grant types for which this client is authorized.
     */
    @Override
    @Transient
    public Set<String> getAuthorizedGrantTypes() {
        Set<String> grants = new HashSet<String>();
        if (clientAuthorizedGrantTypes != null && !clientAuthorizedGrantTypes.isEmpty()) {
            for (ClientAuthorisedGrantTypeEntity cagt : clientAuthorizedGrantTypes) {
                grants.add(cagt.getGrantType());
            }
        }
        return grants;
    }

    /**
     * The pre-defined redirect URI for this client to use during the
     * "authorization_code" access grant. See OAuth spec, section 4.1.1.
     * 
     * @return The pre-defined redirect URI for this client.
     */
    @Override
    @Transient
    public Set<String> getRegisteredRedirectUri() {
        Set<String> redirects = null;
        if (clientRegisteredRedirectUris != null && !clientRegisteredRedirectUris.isEmpty()) {
            redirects = new HashSet<String>();
            for (ClientRedirectUriEntity cru : clientRegisteredRedirectUris) {
                redirects.add(cru.getRedirectUri());
            }
        }
        return redirects;
    }

    /**
     * Get the authorities that are granted to the OAuth client. Note that these
     * are NOT the authorities that are granted to the user with an authorized
     * access token. Instead, these authorities are inherent to the client
     * itself.
     * 
     * @return The authorities.
     */
    @Override
    @Transient
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
        for (ClientGrantedAuthorityEntity cgae : clientGrantedAuthorities) {
            gas.add(cgae);
        }
        return gas;
    }

    /**
     * The access token validity period for this client. Zero or negative for
     * unlimited.
     * 
     * @return the access token validity period
     */
    @Override
    @Transient
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    @Transient
    public Integer getRefreshTokenValiditySeconds() {
        // Not currently required
        return null;
    }

    @Override
    @Transient
    public Map<String, Object> getAdditionalInformation() {
        // Not currently required
        return null;
    }

    @Override
    @Transient
    public boolean isAutoApprove(String scope) {        
        return false;
    }

    @Column(name = "authentication_provider_id")
    public String getAuthenticationProviderId() {
        return authenticationProviderId;
    }

    public void setAuthenticationProviderId(String authenticationProviderId) {
        this.authenticationProviderId = authenticationProviderId;
    }

    @Column(name = "allow_auto_deprecate")
    public boolean isAllowAutoDeprecate() {
        return allowAutoDeprecate;
    }

    public void setAllowAutoDeprecate(boolean allowAutoDeprecate) {
        this.allowAutoDeprecate = allowAutoDeprecate;
    }

    @OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "clientDetailsEntity")
    public Set<MemberOBOWhitelistedClientEntity> getOboWhitelist() {
        return oboWhitelist;
    }

    public void setOboWhitelist(Set<MemberOBOWhitelistedClientEntity> oboWhitelist) {
        this.oboWhitelist = oboWhitelist;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accessTokenValiditySeconds;
        result = prime * result + (allowAutoDeprecate ? 1231 : 1237);
        result = prime * result + ((authenticationProviderId == null) ? 0 : authenticationProviderId.hashCode());
        result = prime * result + ((clientAuthorizedGrantTypes == null) ? 0 : clientAuthorizedGrantTypes.hashCode());
        result = prime * result + ((clientDescription == null) ? 0 : clientDescription.hashCode());
        result = prime * result + ((clientGrantedAuthorities == null) ? 0 : clientGrantedAuthorities.hashCode());
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = prime * result + ((clientName == null) ? 0 : clientName.hashCode());
        result = prime * result + ((clientRegisteredRedirectUris == null) ? 0 : clientRegisteredRedirectUris.hashCode());
        result = prime * result + ((clientResourceIds == null) ? 0 : clientResourceIds.hashCode());
        result = prime * result + ((clientScopes == null) ? 0 : clientScopes.hashCode());
        result = prime * result + ((clientSecret == null) ? 0 : clientSecret.hashCode());
        result = prime * result + ((clientSecrets == null) ? 0 : clientSecrets.hashCode());
        result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
        result = prime * result + ((clientWebsite == null) ? 0 : clientWebsite.hashCode());
        result = prime * result + ((customEmails == null) ? 0 : customEmails.hashCode());
        result = prime * result + ((decryptedClientSecret == null) ? 0 : decryptedClientSecret.hashCode());
        result = prime * result + ((emailAccessReason == null) ? 0 : emailAccessReason.hashCode());
        result = prime * result + ((groupProfileId == null) ? 0 : groupProfileId.hashCode());
        result = prime * result + (persistentTokensEnabled ? 1231 : 1237);
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
        ClientDetailsEntity other = (ClientDetailsEntity) obj;
        if (accessTokenValiditySeconds != other.accessTokenValiditySeconds)
            return false;
        if (allowAutoDeprecate != other.allowAutoDeprecate)
            return false;
        if (authenticationProviderId == null) {
            if (other.authenticationProviderId != null)
                return false;
        } else if (!authenticationProviderId.equals(other.authenticationProviderId))
            return false;
        if (clientAuthorizedGrantTypes == null) {
            if (other.clientAuthorizedGrantTypes != null)
                return false;
        } else if (!clientAuthorizedGrantTypes.equals(other.clientAuthorizedGrantTypes))
            return false;
        if (clientDescription == null) {
            if (other.clientDescription != null)
                return false;
        } else if (!clientDescription.equals(other.clientDescription))
            return false;
        if (clientGrantedAuthorities == null) {
            if (other.clientGrantedAuthorities != null)
                return false;
        } else if (!clientGrantedAuthorities.equals(other.clientGrantedAuthorities))
            return false;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        if (clientName == null) {
            if (other.clientName != null)
                return false;
        } else if (!clientName.equals(other.clientName))
            return false;
        if (clientRegisteredRedirectUris == null) {
            if (other.clientRegisteredRedirectUris != null)
                return false;
        } else if (!clientRegisteredRedirectUris.equals(other.clientRegisteredRedirectUris))
            return false;
        if (clientResourceIds == null) {
            if (other.clientResourceIds != null)
                return false;
        } else if (!clientResourceIds.equals(other.clientResourceIds))
            return false;
        if (clientScopes == null) {
            if (other.clientScopes != null)
                return false;
        } else if (!clientScopes.equals(other.clientScopes))
            return false;
        if (clientSecret == null) {
            if (other.clientSecret != null)
                return false;
        } else if (!clientSecret.equals(other.clientSecret))
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
        if (decryptedClientSecret == null) {
            if (other.decryptedClientSecret != null)
                return false;
        } else if (!decryptedClientSecret.equals(other.decryptedClientSecret))
            return false;
        if (emailAccessReason == null) {
            if (other.emailAccessReason != null)
                return false;
        } else if (!emailAccessReason.equals(other.emailAccessReason))
            return false;
        if (groupProfileId == null) {
            if (other.groupProfileId != null)
                return false;
        } else if (!groupProfileId.equals(other.groupProfileId))
            return false;
        if (persistentTokensEnabled != other.persistentTokensEnabled)
            return false;
        return true;
    }                  
}
