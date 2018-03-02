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
package org.orcid.core.oauth;

import java.util.Collection;
import java.util.HashSet;

import org.orcid.core.security.OrcidWebRole;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public class OrcidProfileUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String orcid;

    private String primaryEmail;

    private String password;

    private Collection<OrcidWebRole> grantedAuthorities = new HashSet<>();

    public OrcidProfileUserDetails() {
    }

    public OrcidProfileUserDetails(String orcid, String primaryEmail, String password) {
        this.orcid = orcid;
        this.primaryEmail = primaryEmail;
        this.password = password;
    }

    public OrcidProfileUserDetails(String orcid, String primaryEmail, String password, Collection<OrcidWebRole> grantedAuthorities) {
        this.orcid = orcid;
        this.primaryEmail = primaryEmail;
        this.password = password;
        this.grantedAuthorities = grantedAuthorities;
    }

    /**
     * Returns the authorities granted to the user. Cannot return
     * <code>null</code>.
     * 
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<OrcidWebRole> getAuthorities() {
        return grantedAuthorities;
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
        return orcid;
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

    public String getOrcid() {
        return orcid;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((grantedAuthorities == null) ? 0 : grantedAuthorities.hashCode());
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((primaryEmail == null) ? 0 : primaryEmail.hashCode());
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
        if (grantedAuthorities == null) {
            if (other.grantedAuthorities != null)
                return false;
        } else if (!grantedAuthorities.equals(other.grantedAuthorities))
            return false;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (primaryEmail == null) {
            if (other.primaryEmail != null)
                return false;
        } else if (!primaryEmail.equals(other.primaryEmail))
            return false;
        return true;
    }

}
