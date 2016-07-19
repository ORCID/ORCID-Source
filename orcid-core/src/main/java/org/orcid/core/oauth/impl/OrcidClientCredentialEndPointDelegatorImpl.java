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
package org.orcid.core.oauth.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 18/04/2012
 */
@Component("orcidClientCredentialEndPointDelegator")
public class OrcidClientCredentialEndPointDelegatorImpl extends AbstractEndpoint implements OrcidClientCredentialEndPointDelegator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidClientCredentialEndPointDelegatorImpl.class);

    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    @Resource
    protected LocaleManager localeManager;
    
    @Transactional
    public Response obtainOauth2Token(String clientId, String clientSecret, String refreshToken, String grantType, String code, Set<String> scopes, String state,
            String redirectUri, String resourceId) {

        LOGGER.info("OAuth2 authorization requested: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] { clientId,
                grantType, refreshToken, code, scopes, state, redirectUri });

        Authentication client = getClientAuthentication();
        if (!client.isAuthenticated()) {
            LOGGER.info("Not authenticated for OAuth2: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] {
                    clientId, grantType, refreshToken, code, scopes, state, redirectUri });
            throw new InsufficientAuthenticationException(localeManager.resolveMessage("apiError.client_not_authenticated.exception"));
        }        
        
        /**
         * Patch, update any orcid-grants scope to funding scope
         * */
        for (String scope : scopes) {
            if (scope.contains("orcid-grants")) {
                String newScope = scope.replace("orcid-grants", "funding");
                LOGGER.info("Client {} provided a grants scope {} which will be updated to {}", new Object[] { clientId, scope, newScope });
                scopes.remove(scope);
                scopes.add(newScope);
            }
        }

        try {
            boolean isClientCredentialsGrantType = OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType);            
            if (scopes != null) {
                List<String> toRemove = new ArrayList<String>();
                for (String scope : scopes) {
                    ScopePathType scopeType = ScopePathType.fromValue(scope);
                    if(scopeType.isInternalScope()) {
                        // You should not allow any internal scope here! go away!
                        String message = localeManager.resolveMessage("apiError.9015.developerMessage", new Object[]{});
                        throw new OrcidInvalidScopeException(message);
                    } else if(isClientCredentialsGrantType) {
                        if(!scopeType.isClientCreditalScope())
                            toRemove.add(scope);
                    } else {
                        if(scopeType.isClientCreditalScope())
                            toRemove.add(scope);
                    }
                }
                
                for (String remove : toRemove) {
                    scopes.remove(remove);
                }
            }
        } catch (IllegalArgumentException iae) {
            String message = localeManager.resolveMessage("apiError.9015.developerMessage", new Object[]{});
            throw new OrcidInvalidScopeException(message);
        }
        
        OAuth2AccessToken token = generateToken(client, scopes, code, redirectUri, grantType, refreshToken, state);
        return getResponse(token);
    }

    protected OAuth2AccessToken generateToken(Authentication client, Set<String> scopes, String code, String redirectUri, String grantType, String refreshToken, String state) {        
        String clientId = client.getName();
        Map<String, String> authorizationParameters = new HashMap<String, String>();
        
        if(scopes != null) {
            String scopesString = StringUtils.join(scopes, ' ');
            authorizationParameters.put(OAuth2Utils.SCOPE, scopesString);
        }
                
        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        if (code != null) {
            authorizationParameters.put("code", code);
            OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity = orcidOauth2AuthoriziationCodeDetailDao.find(code);            
            
            if(authorizationCodeEntity != null) {
                if(orcidOauth2AuthoriziationCodeDetailDao.isPersistentToken(code)) {
                    authorizationParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "true");
                } else {
                    authorizationParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "false");
                }
                
                if(!authorizationParameters.containsKey(OAuth2Utils.SCOPE) || PojoUtil.isEmpty(authorizationParameters.get(OAuth2Utils.SCOPE))) {
                    String scopesString = StringUtils.join(authorizationCodeEntity.getScopes(), ' ');
                    authorizationParameters.put(OAuth2Utils.SCOPE, scopesString);
                }
            } else {
                authorizationParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "false");
            }                        
        }
        if (redirectUri != null) {
            authorizationParameters.put(OAuth2Utils.REDIRECT_URI, redirectUri);
        }        
        AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(authorizationParameters);   
                
        TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(authorizationRequest, grantType);                
        
        OAuth2AccessToken token = getTokenGranter().grant(grantType, tokenRequest);
        Object params[] = {grantType};
        if (token == null) {
            LOGGER.info("Unsupported grant type for OAuth2: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] {
                    clientId, grantType, refreshToken, code, scopes, state, redirectUri });
            throw new UnsupportedGrantTypeException(localeManager.resolveMessage("apiError.unsupported_client_type.exception", params));
        }
        LOGGER.info("OAuth2 access token granted: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}, token={}", new Object[] {
                clientId, grantType, refreshToken, code, scopes, state, redirectUri, token });
        
        return token;
    }
    
    protected Response getResponse(OAuth2AccessToken accessToken) {
        if(accessToken != null && accessToken.getAdditionalInformation() != null) {
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.TOKEN_VERSION))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.TOKEN_VERSION);
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.PERSISTENT))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.PERSISTENT);
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.DATE_CREATED))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.DATE_CREATED);
        }
        
        return Response.ok(accessToken).header("Cache-Control", "no-store").header("Pragma", "no-cache").build();
    }

    protected Authentication getClientAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication;
        } else {
            throw new InsufficientAuthenticationException(localeManager.resolveMessage("apiError.client_authentication_notfound.exception"));
        }

    }

}
