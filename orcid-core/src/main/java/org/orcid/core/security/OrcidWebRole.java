package org.orcid.core.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public enum OrcidWebRole implements GrantedAuthority {
    
    //User role
    ROLE_USER, ROLE_ADMIN, ROLE_GROUP,
    //Group roles
    ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION, 
    //Client roles
    ROLE_CREATOR, ROLE_PREMIUM_CREATOR, ROLE_UPDATER, ROLE_PREMIUM_UPDATER;
            
    public String getAuthority() {
        return this.toString();
    }
}
