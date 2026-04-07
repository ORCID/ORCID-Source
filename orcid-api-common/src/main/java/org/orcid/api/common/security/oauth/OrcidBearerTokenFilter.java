package org.orcid.api.common.security.oauth;


import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.InvalidTokenException;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;

public class OrcidBearerTokenFilter implements Filter {
    private static final Logger logger = Logger.getLogger(OrcidBearerTokenFilter.class);

    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    @Resource
    private APIAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        String tokenValue = extractToken(request);
        if(StringUtils.isBlank(tokenValue)) {
            // If the token is not present, continue the chain
            chain.doFilter(request, response);
        }

        try {
            JSONObject tokenData = authorizationServerUtil.tokenIntrospection(tokenValue);
            OrcidBearerTokenAuthentication authentication = validateTokenData(tokenValue, tokenData);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (URISyntaxException | InterruptedException | JSONException e) {
            //TODO: Define error message and add exception type to it
            apiAuthenticationEntryPoint.commence(request, response, null);
            return;
        }
    }

    private OrcidBearerTokenAuthentication validateTokenData(String accessTokenValue, JSONObject tokenInfo) {
        try {
            if(tokenInfo == null) {
                throw new AccessControlException("Invalid access token: Unable to obtain information from the authorization server");
            }

            boolean isTokenActive = tokenInfo.getBoolean("active");
            if(isTokenActive) {
                // If the token is user revoked it might be used for DELETE requests
                return buildAuthentication(accessTokenValue, tokenInfo);
            } else {
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(RequestMethod.DELETE.name().equals(attr.getRequest().getMethod())) {
                    if(tokenInfo.has("USER_REVOKED") && tokenInfo.getBoolean("USER_REVOKED") == true) {
                        return buildAuthentication(accessTokenValue, tokenInfo);
                    } else {
                        throw new AccessControlException("Invalid access token: " + accessTokenValue);
                    }
                } else {
                    throw new AccessControlException("Invalid access token: " + accessTokenValue);
                }
            }
        } catch(AccessControlException i) {
            throw i;
        } catch(Exception e) {
            logger.error("Exception validating token from authorization server", e);
            throw new RuntimeException("Exception validating token from authorization server", e);
        }
    }

    private OrcidBearerTokenAuthentication buildAuthentication(String accessTokenValue, JSONObject tokenInfo) throws JSONException {
        String clientId = tokenInfo.getString("client_id");
        String userOrcid = tokenInfo.getString("username");

        OrcidBearerTokenAuthentication.Builder builder = OrcidBearerTokenAuthentication.builder(clientId, userOrcid, accessTokenValue);

        Set<String> scopes = Arrays.stream(tokenInfo.getString("scope").split("[\\s,]+"))
                .collect(Collectors.toSet());
        builder.scopes(scopes);

        // Set granted authorities
        if(tokenInfo.has("clientGrantedAuthority")) {
            builder.authorities(Sets.newHashSet(new SimpleGrantedAuthority(tokenInfo.getString("clientGrantedAuthority"))));
        }

        if(tokenInfo.has("OBO_CLIENT_ID")) {
            String oboClientId = tokenInfo.getString("OBO_CLIENT_ID");
            builder.oboClientId(oboClientId);
        }
        builder.authenticated(true);
        return builder.build();
    }

    private String extractToken(HttpServletRequest request) {
        // first check the header...
        String token = extractHeaderToken(request);

        // bearer type allows a request parameter as well
        if (token == null) {
            logger.debug("Token not found in headers. Trying request parameters.");
            token = request.getParameter("access_token");
            if (token == null) {
                logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
            }
        }

        return token;
    }

    private String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith("bearer"))) {
                String authHeaderValue = value.substring("bearer".length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }
}
