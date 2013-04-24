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
package org.orcid.core.security.visibility.aop;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orcid.core.security.PermissionChecker;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.Visibility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Aspect
@Component
public class OrcidApiAuthorizationSecurityAspect {

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

    @Before("@annotation(accessControl) && args(uriInfo , orcid, webhookUri)")
    public void checkPermissionsWithOrcidAndWebhookUri(AccessControl accessControl, UriInfo uriInfo, String orcid, String webhookUri) {
        permissionChecker.checkPermissions(getAuthentication(), accessControl.requiredScope(), orcid);
    }
    
    @AfterReturning(pointcut = "@annotation(accessControl)", returning = "response")
    public void visibilityResponseFilter(Response response, AccessControl accessControl) {
        Object entity = response.getEntity();
        if (entity != null && OrcidMessage.class.isAssignableFrom(entity.getClass())) {
            OrcidMessage orcidMessage = (OrcidMessage) entity;
            Set<Visibility> visibilities = permissionChecker.obtainVisibilitiesForAuthentication(getAuthentication(), accessControl.requiredScope(), orcidMessage);
            visibilityFilter.filter(orcidMessage, false, visibilities.toArray(new Visibility[visibilities.size()]));
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

    private Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        } else {
            throw new IllegalStateException("No security context found. This is bad!");
        }
    }

}
