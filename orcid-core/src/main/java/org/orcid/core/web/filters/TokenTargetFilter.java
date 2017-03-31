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
package org.orcid.core.web.filters;

import java.security.AccessControlException;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.ws.rs.ext.Provider;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class TokenTargetFilter implements ContainerRequestFilter {

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        Matcher m = OrcidStringUtils.orcidPattern.matcher(request.getPath());
        if (m.find()) {
            validateTargetRecord(m.group(), request);
        }
        return request;
    }

    private void validateTargetRecord(String targetOrcid, ContainerRequest request) {
        // Verify if it is the owner of the token
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication authentication = context.getAuthentication();
            if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
                OAuth2Authentication oauth2Auth = (OAuth2Authentication) authentication;
                Authentication userAuthentication = oauth2Auth.getUserAuthentication();
                if (userAuthentication != null) {
                    Object principal = userAuthentication.getPrincipal();
                    if (principal instanceof ProfileEntity) {
                        ProfileEntity tokenOwner = (ProfileEntity) principal;
                        if (!targetOrcid.equals(tokenOwner.getId())) {
                            throwException(request);                            
                        }
                    }
                }
            }
        }
    }
    
    private void throwException(ContainerRequest request) {        
        String apiVersion = request.getHeaderValue(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME);
        if(apiVersion.equals("1.2")) {
            throw new AccessControlException("You do not have the required permissions.");
        } else {
            throw new OrcidUnauthorizedException("Access token is for a different record");
        }
    }
}
