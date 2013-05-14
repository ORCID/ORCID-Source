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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Set;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 23/04/2012
 */
@Entity
@Table(name = "oauth2_authoriziation_code_detail")
public class OrcidOauth2AuthoriziationCodeDetail extends BaseEntity<String> implements ProfileAware {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // Request attributes
    private String authoriziationCode;
    private Set<String> scopes;
    private Set<String> resourceIds;
    private Boolean approved;
    private Set<String> authorities;
    private String redirectUri;
    private String responseType;
    private String state;
    private ClientDetailsEntity clientDetailsEntity;

    // Authentication attributes
    private ProfileEntity profileEntity;
    private String credentials;
    private String remoteAddress;
    private String sessionId;
    private Boolean authenticated;

    @Override
    @Id
    @Column(name = "authoriziation_code_value", length = 255)
    public String getId() {
        return authoriziationCode;
    }

    public void setId(String authoriziationCode) {
        this.authoriziationCode = authoriziationCode;
    }

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    @Column(name = "is_aproved")
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcid")
    public ProfileEntity getProfileEntity() {
        return profileEntity;
    }

    @Override
    @Transient
    public ProfileEntity getProfile() {
        return profileEntity;
    }

    public void setProfileEntity(ProfileEntity profileEntity) {
        this.profileEntity = profileEntity;
    }

    @Column(name = "credentials", length = 100)
    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    @Column(name = "remote_address", length = 65)
    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Column(name = "session_id", length = 155)
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name = "is_authenticated")
    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Column(name = "redirect_uri", length = 355)
    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Column(name = "response_type", length = 55)
    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Column(name = "state", length = 55)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_details_id")
    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }
}
