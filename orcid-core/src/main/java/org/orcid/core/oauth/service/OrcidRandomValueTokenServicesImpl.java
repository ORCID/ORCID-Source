package org.orcid.core.oauth.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import javassist.compiler.SyntaxError;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.exception.ClientDeactivatedException;
import org.orcid.core.exception.InvalidTokenException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.*;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.orcid.core.exception.OrcidInvalidScopeException;
import com.google.common.collect.Sets;

/**
 * @author Declan Newman (declan) Date: 11/05/2012
 */
@Deprecated
//TODO: This have to be removed, just a placeholder to remember where we validate the tokens!!!!
public class OrcidRandomValueTokenServicesImpl  {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRandomValueTokenServicesImpl.class);
    
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;
    @Value("${org.orcid.core.token.read_validity_seconds:631138519}")
    private int readValiditySeconds;
    @Value("${org.orcid.core.token.implicit_validity_seconds:600}")
    private int implicitValiditySeconds;
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int ietfTokenExchangeValiditySeconds;
    
    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    public OrcidBearerTokenAuthentication loadAuthentication(String accessTokenValue) throws AuthenticationException {
        try {
            JSONObject tokenInfo = authorizationServerUtil.tokenIntrospection(accessTokenValue);
            if(tokenInfo == null) {
                throw new InvalidTokenException("Invalid access token: Unable to obtain information from the authorization server");
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
                        throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
                    }
                } else {
                    throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
                }
            }
        } catch(InvalidTokenException i) {
            throw i;
        } catch(Exception e) {
            LOGGER.error("Exception validating token from authorization server", e);
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

        return builder.build();
    }
}
