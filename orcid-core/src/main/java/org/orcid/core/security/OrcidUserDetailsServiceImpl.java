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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.v3.rc1.common.OrcidType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
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

    @Resource(name = "emailManagerReadOnlyV3")
    protected EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource
    private OrcidSecurityManager securityMgr;

    @Resource
    private SlackManager slackManager;

    @Resource
    private SalesForceManager salesForceManager;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

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
    public OrcidProfileUserDetails loadUserByProfile(ProfileEntity profile) {
        if (profile == null) {
            throw new UsernameNotFoundException("Bad username or password");
        } else if(OrcidType.CLIENT.name().equals(profile.getOrcidType())) {
            throw new InvalidUserTypeException("Clients can't login");
        }
        checkStatuses(profile);
        return createUserDetails(profile);
    }

    private OrcidProfileUserDetails createUserDetails(ProfileEntity profile) {
        String primaryEmail = retrievePrimaryEmail(profile);

        OrcidProfileUserDetails userDetails = null;

        if (profile.getOrcidType() != null) {
            OrcidType orcidType = OrcidType.valueOf(profile.getOrcidType());
            userDetails = new OrcidProfileUserDetails(profile.getId(), primaryEmail, profile.getEncryptedPassword(), buildAuthorities(orcidType, profile.getGroupType() != null ? MemberType.valueOf(profile.getGroupType()) : null));
        } else {
            userDetails = new OrcidProfileUserDetails(profile.getId(), primaryEmail, profile.getEncryptedPassword());
        }
        if (!salesForceManager.retrieveAccountIdsByOrcid(profile.getId()).isEmpty()) {
            userDetails.getAuthorities().add(OrcidWebRole.ROLE_SELF_SERVICE);
        }
        return userDetails;
    }

    private String retrievePrimaryEmail(ProfileEntity profile) {
        String orcid = profile.getId();
        try {
            return emailManagerReadOnly.findPrimaryEmail(orcid).getEmail();
        } catch (javax.persistence.NoResultException nre) {
            String message = String.format("User with orcid %s have no primary email", orcid);
            LOGGER.error(message);
            slackManager.sendSystemAlert(message);
            throw nre;
        }
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
                profile = profileDao.find(username);
            } else {
                String orcid = emailManagerReadOnly.findOrcidIdByEmail(username);
                if (!PojoUtil.isEmpty(orcid)) {
                    profile = profileDao.find(orcid);
                }
            }
        }
        return profile;
    }

    private Collection<OrcidWebRole> buildAuthorities(OrcidType orcidType, MemberType groupType) {
        Collection<OrcidWebRole> result = null;
        // If the orcid type is null, assume it is a normal user
        if (orcidType == null)
            result = rolesAsList(OrcidWebRole.ROLE_USER);
        else if (orcidType == OrcidType.ADMIN)
            result = rolesAsList(OrcidWebRole.ROLE_ADMIN, OrcidWebRole.ROLE_USER);
        else if (orcidType.equals(OrcidType.GROUP)) {
            switch (groupType) {
            case BASIC:
                result = rolesAsList(OrcidWebRole.ROLE_BASIC, OrcidWebRole.ROLE_USER);
                break;
            case PREMIUM:
                result = rolesAsList(OrcidWebRole.ROLE_PREMIUM, OrcidWebRole.ROLE_USER);
                break;
            case BASIC_INSTITUTION:
                result = rolesAsList(OrcidWebRole.ROLE_BASIC_INSTITUTION, OrcidWebRole.ROLE_USER);
                break;
            case PREMIUM_INSTITUTION:
                result = rolesAsList(OrcidWebRole.ROLE_PREMIUM_INSTITUTION, OrcidWebRole.ROLE_USER);
                break;
            }
        } else {
            result = rolesAsList(OrcidWebRole.ROLE_USER);
        }

        return result;
    }

    private List<OrcidWebRole> rolesAsList(OrcidWebRole... roles) {
        // Make a mutable list
        List<OrcidWebRole> list = new ArrayList<OrcidWebRole>(Arrays.asList(roles));
        return list;

    }
}
