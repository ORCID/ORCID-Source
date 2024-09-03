package org.orcid.api.common.filter;

import java.security.AccessControlException;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import org.glassfish.jersey.server.ContainerRequest;

@Provider
public class TokenTargetFilter implements ContainerRequestFilter {

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Override
    public void filter(ContainerRequestContext request) {
        Matcher m = OrcidStringUtils.orcidPattern.matcher(request.getUriInfo().getPath());
        if (m.find()) {
            validateTargetRecord(m.group(), request);
        }
        return ;
    }

    private void validateTargetRecord(String targetOrcid, ContainerRequestContext request) {
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
                            throwException();                            
                        }
                    }
                }
            }
        }
    }
    
    private void throwException() {        
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String apiVersion = (String) requestAttributes.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        if(apiVersion.equals("1.2")) {
            throw new AccessControlException("You do not have the required permissions.");
        } else {
            throw new OrcidUnauthorizedException("Access token is for a different record");
        }
    }
}
