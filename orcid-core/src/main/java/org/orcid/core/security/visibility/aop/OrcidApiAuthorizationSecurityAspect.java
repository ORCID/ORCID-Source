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
package org.orcid.core.security.visibility.aop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.security.PermissionChecker;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.record.Activity;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

/**
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Aspect
@Component
@Order(100)
public class OrcidApiAuthorizationSecurityAspect {

    public static final String CLIENT_ID = "client_id";

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource(name = "visibilityFilter")
    private VisibilityFilter visibilityFilter;

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker permissionChecker;

    @Before("@annotation(accessControl) && (args(uriInfo ,orcid, orcidMessage))")
    public void checkPermissionsWithAll(AccessControl accessControl, UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid, orcidMessage);
    }

    @Before("@annotation(accessControl) && (args(uriInfo, orcidMessage))")
    public void checkPermissionsWithOrcidMessage(AccessControl accessControl, UriInfo uriInfo, OrcidMessage orcidMessage) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcidMessage);

    }

    @Before("@annotation(accessControl) && args(orcid)")
    public void checkPermissionsWithOrcid(AccessControl accessControl, String orcid) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);

    }

    @Before("@annotation(accessControl) && args(orcid, id)")
    public void checkPermissionsWithNotificationId(AccessControl accessControl, String orcid, Long id) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @Before("@annotation(accessControl) && args(orcid, id)")
    public void checkPermissionsWithId(AccessControl accessControl, String orcid, String id) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }

    @Before("@annotation(accessControl) && args(uriInfo, orcid, notification)")
    public void checkPermissionsWithNotification(AccessControl accessControl, UriInfo uriInfo, String orcid, Notification notification) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @Before("@annotation(accessControl) && args(orcid, activity)")
    public void checkPermissionsWithWork(AccessControl accessControl, String orcid, Activity activity) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @Before("@annotation(accessControl) && args(orcid, putCode, activity)")
    public void checkPermissionsWithWork(AccessControl accessControl, String orcid, String putCode, Activity activity) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }

    @Before("@annotation(accessControl) && args(uriInfo, orcid, webhookUri)")
    public void checkPermissionsWithOrcidAndWebhookUri(AccessControl accessControl, UriInfo uriInfo, String orcid, String webhookUri) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }

    @AfterReturning(pointcut = "@annotation(accessControl)", returning = "response")
    public void visibilityResponseFilter(Response response, AccessControl accessControl) {
        Object entity = response.getEntity();
        if (entity != null && OrcidMessage.class.isAssignableFrom(entity.getClass())) {
            OrcidMessage orcidMessage = (OrcidMessage) entity;
            Set<Visibility> visibilities = permissionChecker.obtainVisibilitiesForAuthentication(getAuthentication(), accessControl.requiredScope(), orcidMessage);

            ScopePathType requiredScope = accessControl.requiredScope();
            // If the required scope is */read-limited or */update
            if (isUpdateOrReadScope(requiredScope)) {
                // get the client id
                Object authentication = getAuthentication();
                // If the authentication contains a client_id, use it to check
                // if it should be able to
                if (authentication.getClass().isAssignableFrom(OrcidOAuth2Authentication.class)) {
                    OrcidOAuth2Authentication orcidAuth = (OrcidOAuth2Authentication) getAuthentication();

                    OAuth2Request authorization = orcidAuth.getOAuth2Request();
                    String clientId = authorization.getClientId();

                    // #1: Get the user orcid
                    String userOrcid = getUserOrcidFromOrcidMessage(orcidMessage);
                    // #2: Evaluate the scope to know which field to filter
                    boolean allowWorks = false;
                    boolean allowFunding = false;
                    boolean allowAffiliations = false;

                    // Get the update equivalent scope, if it is reading, but,
                    // doesnt have the read permissions, check if it have the
                    // update permissions
                    ScopePathType equivalentUpdateScope = getEquivalentUpdateScope(requiredScope);
                    if (requiredScope.equals(ScopePathType.ORCID_PROFILE_READ_LIMITED)) {
                        if (hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()))
                            allowWorks = true;
                        if (hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()))
                            allowFunding = true;
                        if (hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()))
                            allowAffiliations = true;
                    } else if (requiredScope.equals(ScopePathType.ORCID_WORKS_UPDATE) || requiredScope.equals(ScopePathType.ORCID_WORKS_READ_LIMITED)) {
                        // Check if the member have the update or read scope on
                        // works
                        if (hasScopeEnabled(clientId, userOrcid, requiredScope.getContent(), equivalentUpdateScope == null ? null : equivalentUpdateScope.getContent()))
                            // If so, allow him to see private works
                            allowWorks = true;
                    } else if (requiredScope.equals(ScopePathType.FUNDING_UPDATE) || requiredScope.equals(ScopePathType.FUNDING_READ_LIMITED)) {
                        // Check if the member have the update or read scope on
                        // funding
                        if (hasScopeEnabled(clientId, userOrcid, requiredScope.getContent(), equivalentUpdateScope == null ? null : equivalentUpdateScope.getContent()))
                            // If so, allow him to see private funding
                            allowFunding = true;
                    } else if (requiredScope.equals(ScopePathType.AFFILIATIONS_UPDATE) || requiredScope.equals(ScopePathType.AFFILIATIONS_READ_LIMITED)) {
                        // Check if the member have the update or read scope on
                        // affiliations
                        if (hasScopeEnabled(clientId, userOrcid, requiredScope.getContent(), equivalentUpdateScope == null ? null : equivalentUpdateScope.getContent()))
                            // If so, allow him to see private affiliations
                            allowAffiliations = true;
                    }

                    visibilityFilter.filter(orcidMessage, clientId, allowWorks, allowFunding, allowAffiliations, false,
                            visibilities.toArray(new Visibility[visibilities.size()]));
                } else {
                    visibilityFilter.filter(orcidMessage, null, false, false, false, false, visibilities.toArray(new Visibility[visibilities.size()]));
                }

            } else {
                visibilityFilter.filter(orcidMessage, null, false, false, false, false, visibilities.toArray(new Visibility[visibilities.size()]));
            }
        }
    }

    private String getUserOrcidFromOrcidMessage(OrcidMessage message) {
        OrcidProfile profile = message.getOrcidProfile();
        return profile.getOrcidIdentifier().getPath();
    }

    private boolean isUpdateOrReadScope(ScopePathType requiredScope) {
        switch (requiredScope) {
        case AFFILIATIONS_READ_LIMITED:
        case AFFILIATIONS_UPDATE:
        case FUNDING_READ_LIMITED:
        case FUNDING_UPDATE:
        case ORCID_BIO_READ_LIMITED:
        case ORCID_BIO_UPDATE:
        case ORCID_PATENTS_READ_LIMITED:
        case ORCID_PATENTS_UPDATE:
        case ORCID_PROFILE_READ_LIMITED:
        case ORCID_WORKS_READ_LIMITED:
        case ORCID_WORKS_UPDATE:
            return true;
        default:
            return false;
        }
    }

    private boolean hasScopeEnabled(String clientId, String userName, String scope, String equivalentScope) {
        List<String> scopes = new ArrayList<String>();
        scopes.add(scope);
        if (equivalentScope != null)
            scopes.add(equivalentScope);
        return orcidOauthTokenDetailService.checkIfScopeIsAvailableForMember(clientId, userName, scopes);
    }

    private Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        } else {
            throw new IllegalStateException("No security context found. This is bad!");
        }
    }

    private ScopePathType getEquivalentUpdateScope(ScopePathType readScope) {
        if (readScope != null)
            switch (readScope) {
            case AFFILIATIONS_READ_LIMITED:
                return ScopePathType.AFFILIATIONS_UPDATE;
            case FUNDING_READ_LIMITED:
                return ScopePathType.FUNDING_UPDATE;
            case ORCID_WORKS_READ_LIMITED:
                return ScopePathType.ORCID_WORKS_UPDATE;
            default:
                return null;
            }
        return null;
    }

}
