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
package org.orcid.persistence.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

/**
 * @author Declan Newman
 */
@Entity
@Table(name = "client_details")
public class ClientDetailsEntity extends BaseEntity<String> implements ClientDetails, ProfileAware {

    private static final long serialVersionUID = 1L;

    // Default is 20 years!
    public static final int DEFAULT_TOKEN_VALIDITY = 631138519;

    private String clientId;
    private ClientType clientType;
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
    private Set<OrcidOauth2TokenDetail> tokenDetails;
    private ProfileEntity groupProfile;

    private Set<CustomEmailEntity> customEmails = Collections.emptySet();
    private int accessTokenValiditySeconds = DEFAULT_TOKEN_VALIDITY;
    private boolean persistentTokensEnabled = false;

    public ClientDetailsEntity() {
    }

    public ClientDetailsEntity(String clientId) {
        this.clientId = clientId;
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

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "client_type")
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
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

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "clientDetailsEntity")
    public Set<OrcidOauth2TokenDetail> getTokenDetails() {
        return tokenDetails;
    }

    public void setTokenDetails(Set<OrcidOauth2TokenDetail> tokenDetails) {
        this.tokenDetails = tokenDetails;
    }

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_orcid")
    public ProfileEntity getGroupProfile() {
        return groupProfile;
    }

    public void setGroupProfile(ProfileEntity groupProfile) {
        this.groupProfile = groupProfile;
    }

    @Override
    @Transient
    public ProfileEntity getProfile() {
        return getGroupProfile();
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientDetailsEntity that = (ClientDetailsEntity) o;

        if (!clientId.equals(that.clientId))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return clientId.hashCode();
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

}
