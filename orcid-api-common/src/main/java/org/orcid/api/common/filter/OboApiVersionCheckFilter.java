package org.orcid.api.common.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.exception.OboNotValidForApiVersionException;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;


@Component
@Provider
public class OboApiVersionCheckFilter implements ContainerRequestFilter {

    @Autowired
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Autowired
    private RedisClient redisClient;

    @Value("${org.orcid.core.utils.cache.redis.enabled:true}")
    private boolean isTokenCacheEnabled;

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
                    Map<String, String> cachedAccessToken = getTokenFromCache(authDetails.getTokenValue());
                    if(cachedAccessToken != null) {
                        if(cachedAccessToken.containsKey(OrcidOauth2Constants.IS_OBO_TOKEN)) {
                            return true;
                        }
                    } else {
                        // Fallback to database if it is not in the cache
                        OrcidOauth2TokenDetail tokenDetail = orcidOauth2TokenService.findIgnoringDisabledByTokenValue(authDetails.getTokenValue());
                        if(tokenDetail != null) {
                            return tokenDetail.getOboClientDetailsId() != null;
                        }
                    }
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

    private Map<String, String> getTokenFromCache(String accessTokenValue) {
        if(isTokenCacheEnabled) {
            String tokenJsonInfo = redisClient.get(accessTokenValue);
            if(StringUtils.isNotBlank(tokenJsonInfo)) {
                return JsonUtils.readObjectFromJsonString(tokenJsonInfo, HashMap.class);
            }
        }
        return null;
    }

}
