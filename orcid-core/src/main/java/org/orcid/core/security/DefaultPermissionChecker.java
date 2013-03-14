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

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 27/04/2012
 */
@Component("defaultPermissionChecker")
public class DefaultPermissionChecker implements PermissionChecker {

    // Initialise to 01-01-1970 (I was only 9 months old ;-))
    private static final Date EXPIRES_DATE = new Date(0L);

    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    /**
     * Check the permissions for the given
     * {@link org.springframework.security.core.Authentication} object and the
     * scopes defined in the required scopes
     * 
     * @param authentication
     *            The authentication object associated with this session
     * @param requiredScope
     *            the scopes required to perform the requested operation
     * @param orcid
     *            the orcid passed into the request. This is for requests, such
     *            as a GET /1234-1234-1234-1234/orcid-bio
     * @param orcidMessage
     *            the {@link org.orcid.jaxb.model.message.OrcidMessage} that has
     *            been sent as part of this request. This will only apply to
     *            PUTs and POSTs
     */
    @Override
    public void checkPermissions(Authentication authentication, ScopePathType requiredScope, String orcid, OrcidMessage orcidMessage) {
        if (StringUtils.isNotBlank(orcid) && orcidMessage != null && orcidMessage.getOrcidProfile() != null
                && (orcidMessage.getOrcidProfile().getOrcid() == null || StringUtils.isBlank(orcidMessage.getOrcidProfile().getOrcid().getValue()))) {
            orcidMessage.getOrcidProfile().setOrcid(orcid);
        }
        performPermissionChecks(authentication, requiredScope, orcid, orcidMessage);
    }

    /**
     * Check the permissions for the given
     * {@link org.springframework.security.core.Authentication} object and the
     * scopes defined in the required scopes
     * 
     * @param authentication
     *            The authentication object associated with this session
     * @param requiredScope
     *            the scopes required to perform the requested operation
     * @param orcidMessage
     *            the {@link org.orcid.jaxb.model.message.OrcidMessage} that has
     *            been sent as part of this request. This will only apply to
     *            PUTs and POSTs
     */
    @Override
    public void checkPermissions(Authentication authentication, ScopePathType requiredScope, OrcidMessage orcidMessage) {
        performPermissionChecks(authentication, requiredScope, null, orcidMessage);
    }

    /**
     * Check the permissions for the given
     * {@link org.springframework.security.core.Authentication} object and the
     * scopes defined in the required scopes
     * 
     * @param authentication
     *            The authentication object associated with this session
     * @param requiredScope
     *            the scopes required to perform the requested operation
     * @param orcid
     *            the orcid passed into the request. This is for requests, such
     *            as a GET /1234-1234-1234-1234/orcid-bio
     */
    @Override
    public void checkPermissions(Authentication authentication, ScopePathType requiredScope, String orcid) {
        performPermissionChecks(authentication, requiredScope, orcid, null);
    }

    /**
     * Obtain the current users' permission and return the
     * {@link org.orcid.jaxb.model.message.Visibility} array containing those
     * 
     * @param authentication
     *            the object containing the user's security information
     * @return the {@alink Visibility} array of the current user
     */
    @Override
    public Set<Visibility> obtainVisibilitiesForAuthentication(Authentication authentication, ScopePathType requiredScope, OrcidMessage orcidMessage) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authoritiesHasRole(authorities, "ROLE_SYSTEM")) {
            return new HashSet<Visibility>(Arrays.asList(Visibility.SYSTEM));
        } else if (OrcidOAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OrcidOAuth2Authentication auth2Authentication = (OrcidOAuth2Authentication) authentication;
            Set<Visibility> visibilities = getVisibilitiesForOauth2Authentication(auth2Authentication, orcidMessage, requiredScope);

            if (requiredScope.isWriteOperationScope() && orcidMessage.getOrcidProfile().getOrcidHistory().isClaimed()) {
                OrcidOauth2TokenDetail tokenDetail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(auth2Authentication.getActiveToken());
                tokenDetail.setTokenExpiration(EXPIRES_DATE);
                removeWriteScopes(tokenDetail);
                orcidOauthTokenDetailService.saveOrUpdate(tokenDetail);
            }

            return visibilities;
        } else {
            throw new IllegalArgumentException("Cannot obtain authentication details from " + authentication);
        }
    }

    private void removeWriteScopes(OrcidOauth2TokenDetail tokenDetail) {
        Set<String> scopes = OAuth2Utils.parseParameterList(tokenDetail.getScope());
        Set<String> newScopes = new HashSet<String>();
        Set<ScopePathType> scopesFromStrings = ScopePathType.getScopesFromStrings(scopes);
        for (Iterator<ScopePathType> it = scopesFromStrings.iterator(); it.hasNext();) {
            ScopePathType scopePathType = it.next();
            if (!scopePathType.isWriteOperationScope()) {
                newScopes.add(scopePathType.value());
            }
        }
        tokenDetail.setScope(OAuth2Utils.formatParameterList(newScopes));
    }

    private Set<Visibility> getVisibilitiesForOauth2Authentication(OAuth2Authentication oAuth2Authentication, OrcidMessage orcidMessage, ScopePathType requiredScope) {
        Set<Visibility> visibilities = new HashSet<Visibility>();
        visibilities.add(Visibility.PUBLIC);

        String orcid = orcidMessage.getOrcidProfile().getOrcid().getValue();

        // Check the scopes and it will throw an an AccessControlException if
        // the correct scope is not found. This
        // effectively means that the user can only see the public data
        try {
            checkScopes(oAuth2Authentication, requiredScope);
        } catch (AccessControlException e) {
            return visibilities;
        }
        // If the user has granted permission to the client and the orcid that
        // has been requested is that of the user
        // we can allow for access of protected data
        if (!oAuth2Authentication.isClientOnly() && oAuth2Authentication.getPrincipal() != null
                && ProfileEntity.class.isAssignableFrom(oAuth2Authentication.getPrincipal().getClass())) {
            ProfileEntity principal = (ProfileEntity) oAuth2Authentication.getPrincipal();
            visibilities.add(Visibility.REGISTERED_ONLY);
            if (principal != null && principal.getId().equals(orcid)) {
                visibilities.add(Visibility.LIMITED);
            }
            // This is a client credential authenticated client. If the profile
            // was created using this client and it
            // hasn't been claimed, it's theirs to read
        } else if (oAuth2Authentication.isClientOnly()) {
            AuthorizationRequest authorizationRequest = oAuth2Authentication.getAuthorizationRequest();
            String clientId = authorizationRequest.getClientId();
            String sponsorOrcid = getSponsorOrcid(orcidMessage);
            if (StringUtils.isNotBlank(sponsorOrcid) && clientId.equals(sponsorOrcid) && !orcidMessage.getOrcidProfile().getOrcidHistory().isClaimed()) {
                visibilities.add(Visibility.LIMITED);
                visibilities.add(Visibility.PRIVATE);
            }
        }

        return visibilities;
    }

    private String getSponsorOrcid(OrcidMessage orcidMessage) {
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidHistory() != null
                && orcidMessage.getOrcidProfile().getOrcidHistory().getSource() != null
                && orcidMessage.getOrcidProfile().getOrcidHistory().getSource().getSourceOrcid() != null) {
            return orcidMessage.getOrcidProfile().getOrcidHistory().getSource().getSourceOrcid().getValue();
        } else {
            return null;
        }
    }

    private void performPermissionChecks(Authentication authentication, ScopePathType requiredScope, String orcid, OrcidMessage orcidMessage) {
        // We can trust that this will return a not-null Authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authoritiesHasRole(authorities, "ROLE_SYSTEM")) {
            return;
        } else if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            // Assume that this is write operation if the OrcidMessage is not
            // null
            if (orcidMessage != null) {
                checkScopes(oAuth2Authentication, requiredScope);
            }
            performSecurityChecks(oAuth2Authentication, requiredScope, orcidMessage, orcid);
        } else {
            throw new AccessControlException("Cannot access method with authentication type " + authentication != null ? authentication.toString() : ", as it's null!");
        }
    }

    private void checkScopes(OAuth2Authentication oAuth2Authentication, ScopePathType requiredScope) {
        AuthorizationRequest authorizationRequest = oAuth2Authentication.getAuthorizationRequest();
        Set<String> requestedScopes = authorizationRequest.getScope();
        if (!hasRequiredScope(requestedScopes, requiredScope)) {
            throw new AccessControlException("Insufficient or wrong scope " + requestedScopes);
        }
    }

    private void performSecurityChecks(OAuth2Authentication oAuth2Authentication, ScopePathType requiredScope, OrcidMessage orcidMessage, String orcid) {
        if (oAuth2Authentication.isClientOnly()) {
            performClientChecks(oAuth2Authentication, requiredScope, orcidMessage, orcid);
        } else {
            performUserChecks(oAuth2Authentication, requiredScope, orcidMessage, orcid);
        }
    }

    private void performUserChecks(OAuth2Authentication oAuth2Authentication, ScopePathType requiredScope, OrcidMessage orcidMessage, String orcid) {
        ProfileEntity principal = (ProfileEntity) oAuth2Authentication.getPrincipal();
        String userOrcid = principal.getId();
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcid() != null && StringUtils.isNotBlank(orcid)) {
            String messageOrcid = orcidMessage.getOrcidProfile().getOrcid().getValue();
            // First check that this is a valid call. If these don't match then
            // the request is invalid
            if (!messageOrcid.equals(orcid)) {
                throw new IllegalArgumentException("The ORCID in the body and the URI do not match. Body ORCID: " + messageOrcid + " URI ORCID: " + orcid
                        + " do NOT match.");
            }

            // Is this the owner making the call? If it is, then let 'em on
            // through
            if (userOrcid.equals(orcid)) {
                return;
            } else {
                // Have they been granted permission?
                if (profileEntityManager.hasBeenGivenPermissionTo(orcid, userOrcid)) {
                    // TODO: We will need to parse both incoming and existing to
                    // make sure they're not trying to
                    // update private information.
                    return;
                }
            }
            // Looks like they're trying to call a create. Something that they
            // aren't allowed to do
        } else if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcid() == null) {
            throw new AccessControlException("You do not have the correct privileges to create new profiles.");
        }
    }

    private void performClientChecks(OAuth2Authentication oAuth2Authentication, ScopePathType requiredScope, OrcidMessage orcidMessage, String orcid) {
        AuthorizationRequest authorizationRequest = oAuth2Authentication.getAuthorizationRequest();
        // If we have an ORCID in the request, we assume that this is intended
        // as an update
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && StringUtils.isNotBlank(orcid)) {

            Orcid orcidOb = orcidMessage.getOrcidProfile().getOrcid();
            String messageOrcid = orcidOb != null ? orcidOb.getValue() : orcid;
            if (StringUtils.isNotBlank(messageOrcid) && !orcid.equals(messageOrcid)) {
                throw new IllegalArgumentException("The ORCID in the body and the URI do NOT match. Body ORCID: " + messageOrcid + " URI ORCID: " + orcid
                        + " do NOT match.");
            }

            // Does the profile exist? If so, has it been claimed? Does it
            // belong to the calling client?
            ProfileEntity tmp = profileEntityManager.findByOrcid(messageOrcid);
            if (!profileEntityManager.existsAndNotClaimedAndBelongsTo(messageOrcid, authorizationRequest.getClientId())) {
                throw new AccessControlException("You cannot update this profile as it has been claimed, " + "or you are not the owner.");
            }
        }
    }

    private boolean authoritiesHasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRequiredScope(Set<String> requestedScopes, ScopePathType requiredScope) {
        for (String requestedScope : requestedScopes) {
            if (ScopePathType.hasStringScope(requestedScope, requiredScope)) {
                return true;
            }
        }
        return false;
    }

    public void setOrcidOauthTokenDetailService(OrcidOauth2TokenDetailService orcidOauthTokenDetailService) {
        this.orcidOauthTokenDetailService = orcidOauthTokenDetailService;
    }
}
