/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
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
public class ClientDetailsEntity extends BaseEntity<String> implements ClientDetails {

    private static final long serialVersionUID = 1L;

    // Default is 20 years!
    public static final int DEFAULT_TOKEN_VALIDITY = 631138519;

    private String clientId;
    private String clientName;
    private String clientSecret;
    private String decryptedClientSecret;
    private Set<ClientScopeEntity> clientScopes = Collections.emptySet();
    private Set<ClientResourceIdEntity> clientResourceIds = Collections.emptySet();
    private Set<ClientAuthorisedGrantTypeEntity> clientAuthorizedGrantTypes = Collections.emptySet();
    private SortedSet<ClientRedirectUriEntity> clientRegisteredRedirectUris;
    private List<ClientGrantedAuthorityEntity> clientGrantedAuthorities = Collections.emptyList();
    private Set<OrcidOauth2TokenDetail> tokenDetails;
    private ProfileEntity profileEntity;
    private int accessTokenValiditySeconds = DEFAULT_TOKEN_VALIDITY;

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

    @Column(name = "client_name", length = 255)
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    @OneToOne(mappedBy = "clientDetails")
    public ProfileEntity getProfileEntity() {
        return profileEntity;
    }

    public void setProfileEntity(ProfileEntity profileEntity) {
        this.profileEntity = profileEntity;
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

    @Column(name = "client_secret", length = 150)
    public String getClientSecretForJpa() {
        return clientSecret;
    }

    public void setClientSecretForJpa(String clientSecret) {
        this.clientSecret = clientSecret;
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
    public int getAccessTokenValiditySeconds() {
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
}
