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
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.GroupType;
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
    
    private ClientType clientType;
    
    private GroupType groupType;
    
    public OrcidProfileUserDetails() {
    }
    
    public OrcidProfileUserDetails(String orcid, String primaryEmail, String password) {
        this.realOrcid = orcid;
        this.effectiveOrcid = orcid;
        this.primaryEmail = primaryEmail;
        this.password = password;
    }
    
    public OrcidProfileUserDetails(String orcid, String primaryEmail, String password, OrcidType orcidType, ClientType clientType, GroupType groupType) {
        this.realOrcid = orcid;
        this.effectiveOrcid = orcid;
        this.primaryEmail = primaryEmail;
        this.password = password;
        this.orcidType = orcidType;
        this.clientType = clientType;
        this.groupType = groupType;
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
        //If the orcid type is null, assume it is a normal user
        if(orcidType == null) 
            result = Arrays.asList(OrcidWebRole.ROLE_USER);
        else if(orcidType.equals(OrcidType.GROUP)){
            switch(groupType){
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
        } else if(orcidType.equals(OrcidType.CLIENT)){
            switch(clientType){
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
        } else {
        	result = Arrays.asList(OrcidWebRole.ROLE_USER);
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

    public OrcidType getOrcidType() {
		return orcidType;
	}

	public void setOrcidType(OrcidType orcidType) {
		this.orcidType = orcidType;
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public GroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((effectiveOrcid == null) ? 0 : effectiveOrcid.hashCode());
        result = prime * result + (inDelegationMode ? 1231 : 1237);
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((realOrcid == null) ? 0 : realOrcid.hashCode());
        result = prime * result + ((orcidType == null) ? 0 : orcidType.hashCode());
        result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
        result = prime * result + ((groupType == null) ? 0 : groupType.hashCode());
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
        if(orcidType == null){
            if(other.orcidType != null)
                return false;
        } else if(!orcidType.equals(other.orcidType))
            return false;
        
        if(clientType == null){
            if(other.clientType != null)
                return false;
        } else if(!clientType.equals(other.clientType))
            return false;
     
        if(groupType == null){
            if(other.groupType != null)
                return false;
        } else if (!groupType.equals(other.groupType))
            return false;
        return true;
    }
}
