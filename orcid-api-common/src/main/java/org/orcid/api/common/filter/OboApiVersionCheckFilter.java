package org.orcid.api.common.filter;

import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.orcid.core.exception.OboNotValidForApiVersionException;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;


@Provider
public class OboApiVersionCheckFilter implements ContainerRequestFilter {

    @Autowired
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;
    
    @Override
    public void filter(ContainerRequestContext request) {
        String version = getApiVersion(request);
        boolean oboRequest = isOboRequest();
        
        if ((oboRequest && version.startsWith("2.")) || (oboRequest && version.startsWith("3.0_rc1"))) {
            // OBO tokens can't be used pre v3.0_rc2
            throw new OboNotValidForApiVersionException();
        }
        
        return;
    }

    private boolean isOboRequest() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
                OAuth2AuthenticationDetails authDetails = (OAuth2AuthenticationDetails) ((OAuth2Authentication) authentication).getDetails();
                if (authDetails != null && authDetails.getTokenValue() != null) { 
                    OrcidOauth2TokenDetail tokenDetail = orcidOauth2TokenService.findIgnoringDisabledByTokenValue(authDetails.getTokenValue());
                    return tokenDetail.getOboClientDetailsId() != null;
                }
            }
        }
        return false;
    }

    private String getApiVersion(ContainerRequestContext request) {
        String path = request.getUriInfo().getPath();
        Matcher matcher = ApiVersionCheckFilter.VERSION_PATTERN.matcher(path);
        if (matcher.lookingAt()) {
            return matcher.group(1);
        }
        return null;
    }

}
