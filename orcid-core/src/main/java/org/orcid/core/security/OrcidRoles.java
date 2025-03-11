package org.orcid.core.security;

/**
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public enum OrcidRoles {
    
    // User role
    ROLE_USER, ROLE_ADMIN, ROLE_GROUP,
    //Group roles
    ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION, 
    // Client roles
    ROLE_CREATOR, ROLE_PREMIUM_CREATOR, ROLE_UPDATER, ROLE_PREMIUM_UPDATER,
    // Additional roles
    ROLE_SELF_SERVICE,
    // Switch user role
    ROLE_PREVIOUS_ADMINISTRATOR;
            
    public String getAuthority() {
        return this.toString();
    }
}
