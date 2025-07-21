package org.orcid.persistence.jpa.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @author Declan Newman (declan) Date: 23/04/2012
 */
@Entity
@Table(name = "oauth2_authoriziation_code_detail")
public class OrcidOauth2AuthoriziationCodeDetail extends BaseEntity<String> implements OrcidAware {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // Request attributes
    private String authoriziationCode;
    private Set<String> scopes;
    ////////
    // TODO: The name should change to `scopes` once the authorization server generates all authorization codes
    ////////
    private String newScopes;
    private Set<String> resourceIds;
    private Boolean approved;
    private Set<String> authorities;
    private String redirectUri;
    private String responseType;
    private String state;
    private ClientDetailsEntity clientDetailsEntity;
    private boolean isPersistent;
    private long version;

    // Authentication attributes
    private String orcid;
    private String sessionId;
    private Boolean authenticated;
    
    //openID connect
    private String nonce;

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

    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
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

    @Column(name = "persistent")
    public boolean isPersistent() {
        return isPersistent;
    }

    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }    
    
    @Column(name = "version")
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    ////////
    // TODO: The name should change to `scopes` once the authorization server generates all authorization codes
    ////////
    @Column(name = "scopes")
    public String getNewScopes() {
        return newScopes;
    }

    public void setNewScopes(String newScopes) {
        this.newScopes = newScopes;
    }
}
