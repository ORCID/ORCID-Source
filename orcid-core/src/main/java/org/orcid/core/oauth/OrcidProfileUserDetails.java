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
package org.orcid.core.oauth;

import java.util.Arrays;
import java.util.Collection;

import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.message.OrcidType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public class OrcidProfileUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String realOrcid;

    private String primaryEmail;

    private String password;

    private String effectiveOrcid;

    private boolean inDelegationMode;

    private OrcidType orcidType;
    
    public OrcidProfileUserDetails() {
    }

    public OrcidProfileUserDetails(String orcid, String primaryEmail, String password, OrcidType orcidType) {
        this.realOrcid = orcid;
        this.effectiveOrcid = orcid;
        this.primaryEmail = primaryEmail;
        this.password = password;
        this.orcidType = orcidType;
    }

    /**
     * Returns the authorities granted to the user. Cannot return
     * <code>null</code>.
     * 
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(orcidType != null)            
            switch(orcidType){
                case ADMIN:
                    return Arrays.asList(OrcidWebRole.ROLE_ADMIN);
                default: 
                    return Arrays.asList(OrcidWebRole.ROLE_USER);
            }
        return Arrays.asList(OrcidWebRole.ROLE_USER);
    }

    /**
     * Returns the password used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the password (never <code>null</code>)
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return realOrcid;
    }

    /**
     * Indicates whether the user's account has expired. An expired account
     * cannot be authenticated.
     * 
     * @return <code>true</code> if the user's account is valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     * 
     * @return <code>true</code> if the user is not locked, <code>false</code>
     *         otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     * 
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot
     * be authenticated.
     * 
     * @return <code>true</code> if the user is enabled, <code>false</code>
     *         otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    public void switchDelegationMode(String effectiveOrcid) {
        inDelegationMode = !effectiveOrcid.equals(realOrcid);
        this.effectiveOrcid = effectiveOrcid;
    }

    public boolean isInDelegationMode() {
        return inDelegationMode;
    }

    public void setInDelegationMode(boolean inDelegationMode) {
        this.inDelegationMode = inDelegationMode;
    }

    public String getRealOrcid() {
        return realOrcid;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public String getEffectiveOrcid() {
        return effectiveOrcid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((effectiveOrcid == null) ? 0 : effectiveOrcid.hashCode());
        result = prime * result + (inDelegationMode ? 1231 : 1237);
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((realOrcid == null) ? 0 : realOrcid.hashCode());
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
        OrcidProfileUserDetails other = (OrcidProfileUserDetails) obj;
        if (effectiveOrcid == null) {
            if (other.effectiveOrcid != null)
                return false;
        } else if (!effectiveOrcid.equals(other.effectiveOrcid))
            return false;
        if (inDelegationMode != other.inDelegationMode)
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (realOrcid == null) {
            if (other.realOrcid != null)
                return false;
        } else if (!realOrcid.equals(other.realOrcid))
            return false;
        return true;
    }

}
