package org.orcid.core.security;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.v3.dev1.common.OrcidType;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class OrcidUserDetailsService implements UserDetailsService {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailDao emailDao;

    @Resource
    private OrcidSecurityManager securityMgr;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidUserDetailsService.class);

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
        LOGGER.info("About to load user by username = {}", username);
        ProfileEntity profile = obtainEntity(username);

        if (profile == null) {
            throw new UsernameNotFoundException("Bad username or password");
        }
        if (profile.getPrimaryRecord() != null) {
			throw new DeprecatedProfileException(
					"orcid.frontend.security.deprecated_with_primary", profile
							.getPrimaryRecord().getId(), profile.getId());
        }
        if (profile.getDeactivationDate() != null && !securityMgr.isAdmin()) {
            throw new DisabledException("Account not active, please call helpdesk");
        }
        if (!profile.getClaimed() && !securityMgr.isAdmin()) {
            throw new UnclaimedProfileExistsException("Unclaimed profile");
        }

        String primaryEmail = null;

        // Clients doesnt have primary email, so, we need to cover that case.
        if (profile.getPrimaryEmail() != null)
            primaryEmail = profile.getPrimaryEmail().getId();

        OrcidProfileUserDetails userDetails = null;

        if (profile.getOrcidType() != null) {
            OrcidType orcidType = OrcidType.fromValue(profile.getOrcidType().value());
            userDetails = new OrcidProfileUserDetails(profile.getId(), primaryEmail, profile.getEncryptedPassword(), orcidType, profile.getGroupType());
        } else {
            userDetails = new OrcidProfileUserDetails(profile.getId(), primaryEmail, profile.getEncryptedPassword());
        }

        return userDetails;
    }

    private ProfileEntity obtainEntity(String username) {
        ProfileEntity profile = null;
        if (!StringUtils.isEmpty(username)) {
            if (OrcidStringUtils.isValidOrcid(username)) {
                profile = profileDao.find(username);
            } else {
                EmailEntity emailEntity = emailDao.findCaseInsensitive(username);
                if (emailEntity != null) {
                    profile = emailEntity.getProfile();
                }
            }
        }
        return profile;
    }
}
