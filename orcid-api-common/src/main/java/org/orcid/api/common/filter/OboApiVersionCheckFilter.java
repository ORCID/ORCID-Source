package org.orcid.api.common.filter;

import java.util.regex.Matcher;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OboNotValidForApiVersionException;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Provider
public class OboApiVersionCheckFilter implements ContainerRequestFilter {

    @Autowired
    private RedisClient redisClient;

    @Value("${org.orcid.core.utils.cache.redis.enabled:true}")
    private boolean isTokenCacheEnabled;

    @Override
    public void filter(ContainerRequestContext request) {
        String version = getApiVersion(request);
        boolean oboRequest = isOboRequest();
        
        if (oboRequest && (version.startsWith("2.") || version.startsWith("3.0_rc1"))) {
            // OBO tokens can't be used pre v3.0_rc2
            throw new OboNotValidForApiVersionException();
        }
        
        return;
    }

    private boolean isOboRequest() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(OrcidBearerTokenAuthentication.class.isAssignableFrom(authentication.getClass())) {
                OrcidBearerTokenAuthentication authDetails = (OrcidBearerTokenAuthentication) authentication;
                if (StringUtils.isNotBlank(authDetails.getOboClientId())) {
                    return true;
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
