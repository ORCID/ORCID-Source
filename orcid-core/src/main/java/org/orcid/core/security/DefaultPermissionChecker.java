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
import org.orcid.core.oauth.service.OrcidRandomValueTokenServices;
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
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    @Resource(name = "tokenServices")
    private DefaultTokenServices defaultTokenServices;

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
            orcidMessage.getOrcidProfile().setOrcidId(orcid);
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
            return visibilities;
        } else {
            throw new IllegalArgumentException("Cannot obtain authentication details from " + authentication);
        }
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
                Set<String> requestedScopes = oAuth2Authentication.getAuthorizationRequest().getScope();
                for (String scope : requestedScopes) {
                    if (ScopePathType.hasStringScope(scope, requiredScope)) {
                        visibilities.add(Visibility.LIMITED);
                        break;
                    }
                }
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
            return orcidMessage.getOrcidProfile().getOrcidHistory().getSource().getSourceOrcid().getPath();
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
            checkScopes(oAuth2Authentication, requiredScope);
            performSecurityChecks(oAuth2Authentication, requiredScope, orcidMessage, orcid);
        } else {
            throw new AccessControlException("Cannot access method with authentication type " + authentication != null ? authentication.toString() : ", as it's null!");
        }
    }

    private void checkScopes(OAuth2Authentication oAuth2Authentication, ScopePathType requiredScope) {
        AuthorizationRequest authorizationRequest = oAuth2Authentication.getAuthorizationRequest();
        Set<String> requestedScopes = authorizationRequest.getScope();
        if (requiredScope.isUserGrantWriteScope()) {
            OrcidOAuth2Authentication orcidOauth2Authentication = (OrcidOAuth2Authentication) oAuth2Authentication;
            String activeToken = orcidOauth2Authentication.getActiveToken();
            if (activeToken != null) {
                OrcidOauth2TokenDetail tokenDetail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(activeToken);
                if (removeUserGrantWriteScopePastValitity(tokenDetail)) {
                    throw new AccessControlException("Write scopes for this token have expired ");
                }
            }
        }
        if (!hasRequiredScope(requestedScopes, requiredScope)) {
            throw new AccessControlException("Insufficient or wrong scope " + requestedScopes);
        }
    }

    /*
     * Remove UserGrantWriteScope past the specified validity, returns true if
     * modified false otherwise
     */
    public boolean removeUserGrantWriteScopePastValitity(OrcidOauth2TokenDetail tokenDetail) {
        boolean scopeRemoved = false;
        if (tokenDetail != null && tokenDetail.getScope() != null) {
            Set<String> scopes = OAuth2Utils.parseParameterList(tokenDetail.getScope());
            List<String> removeScopes =  new ArrayList<String>();
            for (String scope : scopes) {
                if (scope != null && !scope.isEmpty()) {
                    ScopePathType scopePathType = ScopePathType.fromValue(scope);
                    if (scopePathType.isUserGrantWriteScope()) {
                        Date now = new Date();
                        OrcidRandomValueTokenServices orcidRandomValueTokenServices = (OrcidRandomValueTokenServices) defaultTokenServices;
                        if (now.getTime() > tokenDetail.getDateCreated().getTime() + (orcidRandomValueTokenServices.getWriteValiditySeconds() * 1000)) {
                            removeScopes.add(scope);
                            scopeRemoved = true;
                        }
                    }
                }
            }
            if (scopeRemoved) {
                for (String scope:removeScopes) scopes.remove(scope); 
                tokenDetail.setScope(OAuth2Utils.formatParameterList(scopes));
                orcidOauthTokenDetailService.saveOrUpdate(tokenDetail);
                return true;
            }
        }
        return false;
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
        throw new AccessControlException("You do not have the required permissions.");
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

            profileEntityManager.findByOrcid(messageOrcid);
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
            if (requiredScope.isReadOnlyScope()) {
                // If read only (limited or otherwise) then let it through it
                // the user has /read-public, and let the visibility filter take
                // care of it.
                if (ScopePathType.hasStringScope(requestedScope, ScopePathType.READ_PUBLIC)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setOrcidOauthTokenDetailService(OrcidOauth2TokenDetailService orcidOauthTokenDetailService) {
        this.orcidOauthTokenDetailService = orcidOauthTokenDetailService;
    }
}
