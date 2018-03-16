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