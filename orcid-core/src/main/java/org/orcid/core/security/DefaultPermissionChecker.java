package org.orcid.core.security;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

/**
 * @author Declan Newman (declan) Date: 27/04/2012
 */
@Component("defaultPermissionChecker")
public class DefaultPermissionChecker implements PermissionChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPermissionChecker.class);
    
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;

    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileDao profileDao;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
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
                && (orcidMessage.getOrcidProfile().getOrcidIdentifier() == null || StringUtils.isBlank(orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath()))) {
            orcidMessage.getOrcidProfile().setOrcidIdentifier(orcid);
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
        LOGGER.debug("Checking permissions on token");
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

        String orcid = orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath();

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
                Set<String> requestedScopes = oAuth2Authentication.getOAuth2Request().getScope();
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
            OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
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
                && orcidMessage.getOrcidProfile().getOrcidHistory().getSource() != null) {
            return orcidMessage.getOrcidProfile().getOrcidHistory().getSource().retrieveSourcePath();
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
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
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
            // Clean the scope if it is not a persistent token
            if (!tokenDetail.isPersistent()) {
                Set<String> scopes = OAuth2Utils.parseParameterList(tokenDetail.getScope());
                List<String> removeScopes = new ArrayList<String>();
                for (String scope : scopes) {
                    if (scope != null && !scope.isEmpty()) {
                        ScopePathType scopePathType = ScopePathType.fromValue(scope);
                        if (scopePathType.isUserGrantWriteScope()) {
                            Date now = new Date();                            
                            if (now.getTime() > tokenDetail.getDateCreated().getTime() + (writeValiditySeconds * 1000)) {
                                removeScopes.add(scope);
                                scopeRemoved = true;
                            }
                        }
                    }
                }
                if (scopeRemoved) {
                    for (String scope : removeScopes)
                        scopes.remove(scope);
                    orcidOauthTokenDetailService.updateScopes(tokenDetail.getTokenValue(), scopes);
                    return true;
                }
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
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidIdentifier() != null
                && StringUtils.isNotBlank(orcid)) {
            String messageOrcid = orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath();
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
            if(profileDao.isProfileDeprecated(orcid)) {
                ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);
                Map<String, String> params = new HashMap<String, String>();
                StringBuffer primary = new StringBuffer(baseUrl).append("/").append(entity.getPrimaryRecord().getId());
                params.put(OrcidDeprecatedException.ORCID, primary.toString());
                if(entity.getDeprecatedDate() != null) {
                    XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(entity.getDeprecatedDate());
                    params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
                }
                throw new OrcidDeprecatedException(params);
            }
        }
        throw new AccessControlException("You do not have the required permissions.");
    }

    private void performClientChecks(OAuth2Authentication oAuth2Authentication, ScopePathType requiredScope, OrcidMessage orcidMessage, String orcid) {
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        // If we have an ORCID in the request, we assume that this is intended
        // as an update
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && StringUtils.isNotBlank(orcid)) {

            OrcidIdentifier orcidOb = orcidMessage.getOrcidProfile().getOrcidIdentifier();
            String messageOrcid = orcidOb != null ? orcidOb.getPath() : orcid;
            if (StringUtils.isNotBlank(messageOrcid) && !orcid.equals(messageOrcid)) {
                throw new IllegalArgumentException("The ORCID in the body and the URI do NOT match. Body ORCID: " + messageOrcid + " URI ORCID: " + orcid
                        + " do NOT match.");
            }

            profileEntityCacheManager.retrieve(messageOrcid);
            if (!profileEntityManager.existsAndNotClaimedAndBelongsTo(messageOrcid, authorizationRequest.getClientId())) {
                throw new AccessControlException("You cannot update this profile as it has been claimed, or you are not the owner.");
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
