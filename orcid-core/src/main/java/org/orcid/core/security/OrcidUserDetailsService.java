package org.orcid.core.security;

import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidUserDetailsService extends UserDetailsService {

    UserDetails loadUserByProfile(ProfileEntity profile);

    boolean isAdmin();
}