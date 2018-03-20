package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author Declan Newman (declan) Date: 16/04/2012
 */
@Entity
@Table(name = "oauth2_token_detail")
public class OrcidOauth2TokenDetail extends BaseEntity<Long> implements ProfileAware, Comparable<OrcidOauth2TokenDetail> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private String tokenValue;
    private String clientDetailsId;
    private ProfileEntity profile;    
    private boolean approved = false;
    private String resourceId;
    private String redirectUri;
    private String responseType;
    private String state;
    private String scope;
    private String tokenType;
    private Date tokenExpiration;
    private String refreshTokenValue;
    private Date refreshTokenExpiration;
    private String authenticationKey;
    private Boolean tokenDisabled;
    private boolean isPersistent;
    private long version;
    private String authorizationCode;
    private Date revocationDate;
    private String revokeReason;
    
    /**
     * This should be implemented by all entity classes to return the id of the
     * entity represented by the &lt;T&gt; generic argument
     * 
     * @return the id of the entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "access_token_seq")
    @SequenceGenerator(name = "access_token_seq", sequenceName = "access_token_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "token_value", length = 150, unique = true, nullable = true)
    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_orcid")
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    
    @Column(name = "client_details_id")
    public String getClientDetailsId() {
        return clientDetailsId;
    }

    public void setClientDetailsId(String clientDetailsId) {
        this.clientDetailsId = clientDetailsId;
    }

    @Column(name = "refresh_token_expiration")
    public Date getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Date refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Column(name = "refresh_token_value", length = 150, unique = true, nullable = true)
    public String getRefreshTokenValue() {
        return refreshTokenValue;
    }

    public void setRefreshTokenValue(String refreshTokenValue) {
        this.refreshTokenValue = refreshTokenValue;
    }

    @Column(name = "is_approved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Column(name = "redirect_uri", length = 350)
    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Column(name = "response_type", length = 100)
    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Column(name = "state", length = 40)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "scope_type", length = 500)
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Column(name = "resource_id", length = 50)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name = "token_type", length = 50)
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Column(name = "token_expiration")
    public Date getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Date tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    @Column(name = "authentication_key", length = 150, unique = true)
    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    @Column(name = "token_disabled")
    public Boolean getTokenDisabled() {
        return tokenDisabled;
    }

    public void setTokenDisabled(Boolean tokenDisabled) {
        this.tokenDisabled = tokenDisabled;
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

    @Override
    public int compareTo(OrcidOauth2TokenDetail other) {
        String clientId = clientDetailsId;
        String otherClientId = other.getClientDetailsId();
        int compareClientId = 0;
        if(StringUtils.isNotBlank(clientId)){ 
            compareClientId = clientId.compareTo(otherClientId);            
        } 
        
        if (compareClientId != 0) {
            return compareClientId;
        }
        Date thisDateCreated = getDateCreated();
        if (thisDateCreated != null) {
            Date otherDateCreated = other.getDateCreated();
            if (otherDateCreated != null) {
                int compareDateCreated = thisDateCreated.compareTo(otherDateCreated);
                if (compareDateCreated != 0) {
                    return compareDateCreated;
                }
            }
        }
        return tokenValue.compareTo(other.tokenValue);
    }

    
    @Column(name = "authorization_code")
    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    public void setAuthorizationCode(String code) {
        this.authorizationCode = code;
    }

    @Column(name = "revocation_date")
    public Date getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    @Column(name = "revoke_reason")
    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }        
}