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
package org.orcid.core.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public enum OrcidWebRole implements GrantedAuthority {
    
    //User role
    ROLE_USER, ROLE_GROUP,
    //Group roles
    ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION, 
    //Client roles
    ROLE_CREATOR, ROLE_PREMIUM_CREATOR, ROLE_UPDATER, ROLE_PREMIUM_UPDATER;
            
    public String getAuthority() {
        return this.toString();
    }
}
