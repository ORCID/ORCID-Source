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

    ROLE_USER, ROLE_GROUP, ROLE_PREMIUM_GROUP;

    public String getAuthority() {
        return this.toString();
    }
}
