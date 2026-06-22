package org.orcid.api.common.security.oauth;


import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrcidBearerTokenFilter implements Filter {
    private static final Logger logger = Logger.getLogger(OrcidBearerTokenFilter.class);
    private static final String CLIENT_GRANTED_AUTHORITY = "clientGrantedAuthority";
    private static final String ROLE_PUBLIC = "ROLE_PUBLIC";
    private static final String READ_PUBLIC_SCOPE = "/read-public";
    private static final String ACTIVE = "active";
    private static final String USERNAME = "username";
    private static final String ORCID = "orcid";

    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    @Resource
    private APIAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @Resource
    private AccessDeniedHandler orcidAPIAccessDeniedHandler;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        String tokenValue = extractToken(request);
        
        logger.debug("OrcidBearerTokenFilter request: method=" + request.getMethod() + " uri=" + request.getRequestURI() + " tokenPresent=" + StringUtils.isNotBlank(tokenValue) + " token=" + tokenFingerprint(tokenValue));
        

        if(StringUtils.isBlank(tokenValue)) {
            // If the token is not present, continue the chain
            chain.doFilter(request, response);
            return;
        }

        try {
            JSONObject tokenData = authorizationServerUtil.tokenIntrospection(tokenValue);
            logger.debug("Token introspection successful for token=" + tokenData);
            
            logger.debug("Token introspection response: token=" + tokenFingerprint(tokenValue)
                        + " active=" + safeGet(tokenData, ACTIVE)
                        + " client_id=" + safeGet(tokenData, "client_id")
                        + " username=" + safeGet(tokenData, "username")
                        + " scope=" + safeGet(tokenData, "scope")
                        + " clientGrantedAuthority=" + safeGet(tokenData, CLIENT_GRANTED_AUTHORITY));
            OrcidBearerTokenAuthentication authentication = validateTokenData(tokenValue, tokenData);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (RevokedTokenException e) {
            // Revoked/expired token: authentication failure (401 Unauthorized)
            logger.warn("Revoked access token for token=" + tokenFingerprint(tokenValue) + " reason=" + e.getMessage());
            apiAuthenticationEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage(), e));
            return;
        } catch (AccessDeniedException e) {
            // Valid token but lacks permission: authorization failure (403 Forbidden)
            logger.warn("Access denied for token=" + tokenFingerprint(tokenValue) + " reason=" + e.getMessage());
            orcidAPIAccessDeniedHandler.handle(request, response, e);
            return;
        } catch (AccessControlException e) {
            // Invalid/fake token: authentication failure (401 Unauthorized)
            logger.warn("Invalid access token for token=" + tokenFingerprint(tokenValue) + " reason=" + e.getMessage());
            apiAuthenticationEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage(), e));
            return;
        } catch (IOException | URISyntaxException | InterruptedException | JSONException e) {
            //TODO: Define error message and add exception type to it
            logger.warn("Token introspection failed for token=" + tokenFingerprint(tokenValue), e);
            apiAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid access token", e));
            return;
        }
    }

    private OrcidBearerTokenAuthentication validateTokenData(String accessTokenValue, JSONObject tokenInfo) {
        try {
            if(tokenInfo == null) {
                throw new AccessControlException("Invalid access token: Unable to obtain information from the authorization server");
            }

            boolean isTokenActive = tokenInfo.getBoolean(ACTIVE);
            if(isTokenActive) {
                // If the token is user revoked it might be used for DELETE requests
                return buildAuthentication(accessTokenValue, tokenInfo);
            } else {
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(RequestMethod.DELETE.name().equals(attr.getRequest().getMethod())) {
                    if(tokenInfo.has("USER_REVOKED") && tokenInfo.getBoolean("USER_REVOKED") == true) {
                        return buildAuthentication(accessTokenValue, tokenInfo);
                    } else {
                        // Token is inactive and not marked as user-revoked (likely expired or revoked by admin)
                        throw new RevokedTokenException("Invalid access token");
                    }
                } else {
                    // Token is inactive for non-DELETE request (revoked or expired)
                    throw new RevokedTokenException("Invalid access token");
                }
            }
        } catch(RevokedTokenException r) {
            logger.warn("Revoked token for token=" + tokenFingerprint(accessTokenValue) + " reason=" + r.getMessage());
            throw r;
        } catch(AccessControlException i) {
            logger.warn("Access control failure for token=" + tokenFingerprint(accessTokenValue) + " reason=" + i.getMessage());
            throw i;
        } catch(Exception e) {
            logger.error("Exception validating token from authorization server", e);
            throw new RuntimeException("Exception validating token from authorization server", e);
        }
    }

    private OrcidBearerTokenAuthentication buildAuthentication(String accessTokenValue, JSONObject tokenInfo) throws JSONException {
        String clientId = tokenInfo.getString("client_id");
        String userOrcid = resolveUserOrcid(tokenInfo);
        boolean isClientOnlyToken = StringUtils.isBlank(userOrcid);

        OrcidBearerTokenAuthentication.Builder builder = OrcidBearerTokenAuthentication.builder(clientId, userOrcid, accessTokenValue);

        Set<String> scopes = tokenizeSpaceOrCsvField(tokenInfo, "scope");
        builder.scopes(scopes);

        // Set granted authorities from token introspection when present.
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if(tokenInfo.has(CLIENT_GRANTED_AUTHORITY)) {
            String grantedAuthorities = tokenInfo.getString(CLIENT_GRANTED_AUTHORITY);
            if(StringUtils.isNotBlank(grantedAuthorities)) {
                Arrays.stream(grantedAuthorities.split("[\\s,]+"))
                        .filter(StringUtils::isNotBlank)
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
        }

        // Fallback for public API tokens: if introspection does not include a granted authority
        // but the token can read public data, treat it as ROLE_PUBLIC.
        if(authorities.isEmpty() && scopes.contains(READ_PUBLIC_SCOPE)) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PUBLIC));
        }

        if(!authorities.isEmpty()) {
            builder.authorities(Sets.newHashSet(authorities));
        }

            logger.debug("Built authentication for token=" + tokenFingerprint(accessTokenValue)
                + " clientId=" + clientId
                + " userOrcid=" + userOrcid
                + " clientOnly=" + isClientOnlyToken
                + " scopes=" + scopes
                + " authorities=" + authorities);

        if(tokenInfo.has("OBO_CLIENT_ID")) {
            String oboClientId = tokenInfo.getString("OBO_CLIENT_ID");
            builder.oboClientId(oboClientId);
        }
        builder.authenticated(true);
        return builder.build();
    }

    private String resolveUserOrcid(JSONObject tokenInfo) {
        String username = extractOptionalString(tokenInfo, USERNAME);
        if(StringUtils.isNotBlank(username)) {
            return username;
        }

        // Some introspection responses expose the user ORCID in "orcid" instead of "username".
        String orcid = extractOptionalString(tokenInfo, ORCID);
        if(StringUtils.isNotBlank(orcid)) {
            return orcid;
        }

        return null;
    }

    private Set<String> tokenizeSpaceOrCsvField(JSONObject tokenInfo, String fieldName) {
        String rawValue = extractOptionalString(tokenInfo, fieldName);
        if(StringUtils.isBlank(rawValue)) {
            return new HashSet<>();
        }

        return Arrays.stream(rawValue.split("[\\s,]+"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    private String extractOptionalString(JSONObject tokenInfo, String fieldName) {
        if(tokenInfo == null || StringUtils.isBlank(fieldName) || !tokenInfo.has(fieldName)) {
            return null;
        }

        try {
            String value = tokenInfo.getString(fieldName);
            return StringUtils.isBlank(value) ? null : value;
        } catch(JSONException e) {
            return null;
        }
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

    private String safeGet(JSONObject tokenInfo, String field) {
        if (tokenInfo == null || StringUtils.isBlank(field) || !tokenInfo.has(field)) {
            return "<missing>";
        }

        try {
            Object value = tokenInfo.get(field);
            return value == null ? "<null>" : String.valueOf(value);
        } catch (JSONException e) {
            return "<error>";
        }
    }

    private String tokenFingerprint(String token) {
        if (StringUtils.isBlank(token)) {
            return "<empty>";
        }

        int len = token.length();
        if (len <= 8) {
            return "***" + token;
        }

        return "***" + token.substring(len - 8);
    }
}
