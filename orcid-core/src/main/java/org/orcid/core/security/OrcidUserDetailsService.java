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

import javax.annotation.Resource;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class OrcidUserDetailsService implements UserDetailsService {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailDao emailDao;

    /**
     * Locates the user based on the username. In the actual implementation, the
     * search may possibly be case insensitive, or case insensitive depending on
     * how the implementation instance is configured. In this case, the
     * <code>UserDetails</code> object that comes back may have a username that
     * is of a different case than what was actually requested..
     * 
     * @param username
     *            the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
     *             if the user could not be found or the user has no
     *             GrantedAuthority
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ProfileEntity profile = obtainEntity(username);

        if (profile == null) {
            throw new UsernameNotFoundException("Bad username or password");
        }
        if (!profile.getClaimed()) {
            throw new UnclaimedProfileExistsException("orcid.frontend.security.unclaimed_exists");
        }
        if (profile.getDeactivationDate() != null) {
            throw new DisabledException("Account not active, please call helpdesk");
        }
        if (profile.getPrimaryRecord() != null) {
        	throw new DeprecatedException("orcid.frontend.security.deprecated_with_primary", profile.getPrimaryRecord().getId(), profile.getId());
        }
        String primaryEmail = profile.getPrimaryEmail().getId();
        return new OrcidProfileUserDetails(profile.getId(), primaryEmail, profile.getEncryptedPassword(), profile.getOrcidType());
    }

    private ProfileEntity obtainEntity(String username) {
        ProfileEntity profile = null;
        if (OrcidStringUtils.isValidOrcid(username)) {
            profile = profileDao.find(username);
        } else {
            EmailEntity emailEntity = emailDao.findCaseInsensitive(username);
            if (emailEntity != null) {
                profile = emailEntity.getProfile();
            }
        }
        return profile;
    }

}
