package org.orcid.core.web.filters;

import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.ws.rs.ext.Provider;

import org.orcid.core.exception.OboNotValidForApiVersionException;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.service.OrcidOauth2TokenDetailServiceImpl;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class OboApiVersionCheckFilter implements ContainerRequestFilter {

    @InjectParam("orcidOauth2TokenDetailServiceImpl")
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;
    
    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String version = getApiVersion(request);
        boolean oboRequest = isOboRequest();
        
        if ((oboRequest && version.startsWith("2.")) || (oboRequest && version.startsWith("3.0_rc1"))) {
            // OBO tokens can't be used pre v3.0_rc2
            throw new OboNotValidForApiVersionException();
        }
        
        return request;
    }

    private boolean isOboRequest() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            OAuth2AuthenticationDetails authDetails = (OAuth2AuthenticationDetails) ((OAuth2Authentication) authentication).getDetails();
            if (authDetails != null && authDetails.getTokenValue() != null) { 
                OrcidOauth2TokenDetail tokenDetail = orcidOauth2TokenService.findIgnoringDisabledByTokenValue(authDetails.getTokenValue());
                return tokenDetail.getOboClientDetailsId() != null;
            }
        }
        return false;
    }

    private String getApiVersion(ContainerRequest request) {
        String path = request.getPath();
        Matcher matcher = ApiVersionCheckFilter.VERSION_PATTERN.matcher(path);
        if (matcher.lookingAt()) {
            return matcher.group(1);
        }
        return null;
    }

}
