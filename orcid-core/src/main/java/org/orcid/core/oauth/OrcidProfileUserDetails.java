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

import org.apache.commons.lang.StringUtils;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public class OrcidProfileUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private OrcidProfile realProfile;

    private OrcidProfile effectiveProfile;

    private Date effectiveProfileLastModified;

    private boolean inDelegationMode;

    public OrcidProfileUserDetails() {
    }

    public OrcidProfileUserDetails(OrcidProfile profile) {
        setRealProfile(profile);
    }

    /**
     * Returns the authorities granted to the user. Cannot return
     * <code>null</code>.
     * 
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<OrcidWebRole> result = null;
        if(realProfile == null || realProfile.getType() == null || realProfile.getType().equals(OrcidType.USER)) 
            result = Arrays.asList(OrcidWebRole.ROLE_USER);
        else if(realProfile.getType().equals(OrcidType.GROUP)){
            switch(realProfile.getGroupType()){
            case BASIC:  
                result = Arrays.asList(OrcidWebRole.ROLE_BASIC);
                break;
            case PREMIUM:
                result = Arrays.asList(OrcidWebRole.ROLE_PREMIUM);
                break;
            case BASIC_INSTITUTION:
                result = Arrays.asList(OrcidWebRole.ROLE_BASIC_INSTITUTION);
                break;
            case PREMIUM_INSTITUTION:
                result = Arrays.asList(OrcidWebRole.ROLE_PREMIUM_INSTITUTION);
                break;
            }
        } else if(realProfile.getType().equals(OrcidType.CLIENT)){
            switch(realProfile.getClientType()){
            case CREATOR:
                result = Arrays.asList(OrcidWebRole.ROLE_CREATOR);
                break;
            case UPDATER:
                result = Arrays.asList(OrcidWebRole.ROLE_UPDATER);
                break;
            case PREMIUM_CREATOR:
                result = Arrays.asList(OrcidWebRole.ROLE_PREMIUM_CREATOR);
                break;
            case PREMIUM_UPDATER:
                result = Arrays.asList(OrcidWebRole.ROLE_PREMIUM_UPDATER);
                break;
            }
        }
        
        return result;
    }

    /**
     * Returns the password used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the password (never <code>null</code>)
     */
    @Override
    public String getPassword() {
        return (realProfile.getOrcidInternal() != null && realProfile.getOrcidInternal().getSecurityDetails() != null && realProfile.getOrcidInternal()
                .getSecurityDetails().getEncryptedPassword() != null) ? realProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent() : "";
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return (realProfile.getOrcid() != null && StringUtils.isNotBlank(realProfile.getOrcid().getValue())) ? realProfile.getOrcid().getValue() : "";
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

    public OrcidProfile getRealProfile() {
        return realProfile;
    }

    public void setRealProfile(OrcidProfile profile) {
        this.realProfile = profile;
        if (!inDelegationMode) {
            this.effectiveProfile = profile;
        }
    }

    public OrcidProfile getEffectiveProfile() {
        return effectiveProfile;
    }

    public void setEffectiveProfile(OrcidProfile profile) {
        this.effectiveProfile = profile;
        if (!inDelegationMode) {
            this.realProfile = profile;
        }
    }

    public Date getEffectiveProfileLastModified() {
        return effectiveProfileLastModified;
    }

    public void setEffectiveProfileLastModified(Date effectiveProfileLastModified) {
        this.effectiveProfileLastModified = effectiveProfileLastModified;
    }

    public void switchDelegationMode(OrcidProfile profile) {
        inDelegationMode = !profile.getOrcid().getValue().equals(realProfile.getOrcid().getValue());
        setEffectiveProfile(profile);
    }

    public boolean isInDelegationMode() {
        return inDelegationMode;
    }

    public void setInDelegationMode(boolean inDelegationMode) {
        this.inDelegationMode = inDelegationMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrcidProfileUserDetails)) {
            return false;
        }

        OrcidProfileUserDetails that = (OrcidProfileUserDetails) o;

        if (inDelegationMode != that.inDelegationMode) {
            return false;
        }
        if (effectiveProfile != null ? !effectiveProfile.equals(that.effectiveProfile) : that.effectiveProfile != null) {
            return false;
        }
        if (realProfile != null ? !realProfile.equals(that.realProfile) : that.realProfile != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = realProfile != null ? realProfile.hashCode() : 0;
        result = 31 * result + (effectiveProfile != null ? effectiveProfile.hashCode() : 0);
        result = 31 * result + (inDelegationMode ? 1 : 0);
        return result;
    }
}
