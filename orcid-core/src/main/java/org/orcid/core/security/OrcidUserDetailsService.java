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
package org.orcid.core.security;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidUserDetailsService extends UserDetailsService {

    OrcidProfileUserDetails loadUserByProfile(ProfileEntity profile);

}