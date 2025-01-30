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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class OrcidUserDetailsServiceImpl implements OrcidUserDetailsService {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailDao emailDao;

    @Resource
    private OrcidSecurityManager securityMgr;

    @Resource (name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    private final GrantedAuthority adminAuthority = new SimpleGrantedAuthority(OrcidRoles.ROLE_ADMIN.name());

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidUserDetailsServiceImpl.class);

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
        return loadUserByProfile(profile);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orcid.core.security.OrcidUserDetailsService#loadUserByProfile(org.
     * orcid.persistence.jpa.entities.ProfileEntity)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDetails loadUserByProfile(ProfileEntity profile) {
        if (profile == null) {
            throw new UsernameNotFoundException("Bad username or password");
        } else if(OrcidType.CLIENT.name().equals(profile.getOrcidType())) {
            throw new InvalidUserTypeException("Clients can't login");
        }
        checkStatuses(profile);
        return createUserDetails(profile);
    }

    private UserDetails createUserDetails(ProfileEntity profile) {
        String primaryEmail = retrievePrimaryEmail(profile.getId());
        OrcidType orcidType = OrcidType.valueOf(profile.getOrcidType());
        return new User(profile.getId(), profile.getEncryptedPassword(), buildAuthorities(orcidType, profile.getGroupType() != null ? MemberType.valueOf(profile.getGroupType()) : null));
    }

    private void checkStatuses(ProfileEntity profile) {
        if (profile.getPrimaryRecord() != null) {
            throw new DeprecatedProfileException("orcid.frontend.security.deprecated_with_primary", profile.getPrimaryRecord().getId(), profile.getId());
        }
        if (profile.getDeactivationDate() != null && !securityMgr.isAdmin()) {
            throw new DisabledException("Account not active, please call helpdesk");
        }
        if (!profile.getClaimed() && !securityMgr.isAdmin()) {
            throw new UnclaimedProfileExistsException("Unclaimed profile");
        }
    }

    private ProfileEntity obtainEntity(String username) {
        ProfileEntity profile = null;
        if (!StringUtils.isEmpty(username)) {
            if (OrcidStringUtils.isValidOrcid(username)) {
                profile = profileDao.find(username.toUpperCase());
            } else {
                try {
                    String orcid = emailManagerReadOnly.findOrcidIdByEmail(username);
                    if (!PojoUtil.isEmpty(orcid)) {
                        profile = profileDao.find(orcid.toUpperCase());
                    }
                } catch (javax.persistence.NoResultException nre) {
                    LOGGER.error("User " + username + " was not found");
                } catch (Exception e) {
                    LOGGER.error("Error finding user " + username, e);
                }
            }
        }
        return profile;
    }

    private Collection<GrantedAuthority> buildAuthorities(OrcidType orcidType, MemberType groupType) {
        Collection<GrantedAuthority> result = null;
        // If the orcid type is null, assume it is a normal user
        if (orcidType == null)
            result = rolesAsList(OrcidRoles.ROLE_USER);
        else if (orcidType == OrcidType.ADMIN)
            result = rolesAsList(OrcidRoles.ROLE_ADMIN, OrcidRoles.ROLE_USER);
        else if (orcidType.equals(OrcidType.GROUP)) {
            switch (groupType) {
            case BASIC:
                result = rolesAsList(OrcidRoles.ROLE_BASIC, OrcidRoles.ROLE_USER);
                break;
            case PREMIUM:
                result = rolesAsList(OrcidRoles.ROLE_PREMIUM, OrcidRoles.ROLE_USER);
                break;
            case BASIC_INSTITUTION:
                result = rolesAsList(OrcidRoles.ROLE_BASIC_INSTITUTION, OrcidRoles.ROLE_USER);
                break;
            case PREMIUM_INSTITUTION:
                result = rolesAsList(OrcidRoles.ROLE_PREMIUM_INSTITUTION, OrcidRoles.ROLE_USER);
                break;
            }
        } else {
            result = rolesAsList(OrcidRoles.ROLE_USER);
        }

        return result;
    }

    private List<GrantedAuthority> rolesAsList(OrcidRoles... roles) {
        List<GrantedAuthority> result = new ArrayList<>();
        for(OrcidRoles r : roles) {
            result.add(new SimpleGrantedAuthority(r.name()));
        }
        return result;
    }

    @Deprecated(forRemoval = true)
    private String retrievePrimaryEmail(String orcid) {
        try {
            return emailDao.findPrimaryEmail(orcid).getEmail();
        } catch (javax.persistence.NoResultException nre) {
            String alternativePrimaryEmail = emailDao.findNewestVerifiedOrNewestEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            String message = String.format("User with orcid %s have no primary email, so, we are setting the newest verified email, or, the newest email in case non is verified as the primary one", orcid);
            LOGGER.error(message);
            return alternativePrimaryEmail;
        } catch (javax.persistence.NonUniqueResultException nure) {
            String alternativePrimaryEmail = emailDao.findNewestPrimaryEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            String message = String.format("User with orcid %s have more than one primary email, so, we are setting the latest modified primary as the primary one", orcid);
            LOGGER.error(message);
            return alternativePrimaryEmail;
        }
    }

    @Override
    public boolean isAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = null;
        if (context != null && context.getAuthentication() != null) {
            authentication = context.getAuthentication();
        }

        if (authentication != null) {
            Object details = authentication.getDetails();
            if (details instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) details;
                return userDetails.getAuthorities().contains(adminAuthority);
            }
        }
        return false;
    }

}
