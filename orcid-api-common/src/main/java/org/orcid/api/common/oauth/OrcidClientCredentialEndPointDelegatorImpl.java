package org.orcid.api.common.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.oauth.OAuthErrorUtils;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
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
    
    @Resource(name="profileLastModifiedDao")
    private ProfileLastModifiedDao profileLastModifiedDao;
    
    @Resource
    private RedisClient redisClient;    
    
    @Value("${org.orcid.core.utils.cache.redis.enabled:true}") 
    private boolean isTokenCacheEnabled;
    
    @Transactional
    public Response obtainOauth2Token(String authorization, MultivaluedMap<String, String> formParams) {
        String code = formParams.getFirst("code");
        String clientId = formParams.getFirst(OrcidOauth2Constants.CLIENT_ID_PARAM);        
        String state = formParams.getFirst(OrcidOauth2Constants.STATE_PARAM);
        String redirectUri = formParams.getFirst(OrcidOauth2Constants.REDIRECT_URI_PARAM);
        String refreshToken = formParams.getFirst(OrcidOauth2Constants.REFRESH_TOKEN);
        String scopeList = formParams.getFirst(OrcidOauth2Constants.SCOPE_PARAM);
        String grantType = formParams.getFirst(OrcidOauth2Constants.GRANT_TYPE);
        Boolean revokeOld = formParams.containsKey(OrcidOauth2Constants.REVOKE_OLD) ? Boolean.valueOf(formParams.getFirst(OrcidOauth2Constants.REVOKE_OLD)) : true;
        Long expiresIn = calculateExpiresIn(formParams);
        //IETF Token exchange
        String subjectToken = formParams.getFirst(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN);
        String subjectTokenType = formParams.getFirst(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE);
        String requestedTokenType = formParams.getFirst(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE);
        
        String bearerToken = null;
        Set<String> scopes = new HashSet<String>();
        if (StringUtils.isNotEmpty(scopeList)) {
            scopes = OAuth2Utils.parseParameterList(scopeList);
        }
        if(OrcidOauth2Constants.REFRESH_TOKEN.equals(grantType)) {
            if(!PojoUtil.isEmpty(authorization)) {
                if ((authorization.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
                    String authHeaderValue = authorization.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
                    int commaIndex = authHeaderValue.indexOf(',');
                    if (commaIndex > 0) {
                            authHeaderValue = authHeaderValue.substring(0, commaIndex);
                    }
                    bearerToken = authHeaderValue;
                    if(PojoUtil.isEmpty(bearerToken)) {
                        throw new IllegalArgumentException("Refresh token request doesnt include the authorization");
                    }
                }            
            }                       
        }        
        
        Authentication client = getClientAuthentication();
        if (!client.isAuthenticated()) {
            LOGGER.error("Not authenticated for OAuth2: clientId={}, grantType={}, refreshToken={}, code={}, scopes={}, state={}, redirectUri={}", new Object[] {
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
            if (scopes != null) {
                List<String> toRemove = new ArrayList<String>();
                for (String scope : scopes) {
                    ScopePathType scopeType = ScopePathType.fromValue(scope);
                    if(scopeType.isInternalScope()) {
                        // You should not allow any internal scope here! go away!
                        String message = localeManager.resolveMessage("apiError.9015.developerMessage", new Object[]{});
                        throw new OrcidInvalidScopeException(message);
                    } else if(OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
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
                
        try{
            OAuth2AccessToken token = generateToken(client, scopes, code, redirectUri, grantType, refreshToken, state, bearerToken, revokeOld, expiresIn, subjectToken, subjectTokenType, requestedTokenType);
            
            //Lets check if the user needs to be indexed
            if(token != null && token.getAdditionalInformation() != null) {
                if(token.getAdditionalInformation().containsKey(OrcidOauth2Constants.ORCID)) {
                    String orcidId = (String) token.getAdditionalInformation().get(OrcidOauth2Constants.ORCID);
                    if(!PojoUtil.isEmpty(orcidId)) {
                        profileLastModifiedDao.updateIndexingStatus(Arrays.asList(orcidId), IndexingStatus.PENDING);
                    }                    
                }                
            }
            
            // Do not put the token in the cache if the token is disabled
            if(token.getAdditionalInformation() != null && !token.getAdditionalInformation().containsKey(OrcidOauth2Constants.TOKEN_DISABLED)) {
                setToCache(client.getName(), token);
            }
            removeMetadataFromToken(token);
            return getResponse(token);
        } catch (InvalidGrantException e){ //this needs to be caught here so the transaction doesn't roll back
            OAuthError error = OAuthErrorUtils.getOAuthError(e);
            return Response.status(error.getResponseStatus().getStatusCode()).entity(error).build();
        }
    }
    
    /**
     * Set the access token in the cache
     * */
    protected void setToCache(String clientId, OAuth2AccessToken accessToken) {
        if(isTokenCacheEnabled) {
            try {
                String tokenValue = accessToken.getValue();
                Map<String, String> tokenData = new HashMap<String, String>();
                tokenData.put(OrcidOauth2Constants.ACCESS_TOKEN, tokenValue);
                tokenData.put(OrcidOauth2Constants.TOKEN_EXPIRATION_TIME, String.valueOf(accessToken.getExpiration().getTime()));
                StringBuilder sb = new StringBuilder();
                accessToken.getScope().forEach(x -> {sb.append(x); sb.append(' ');});
                tokenData.put(OrcidOauth2Constants.SCOPE_PARAM, sb.toString());
                tokenData.put(OrcidOauth2Constants.ORCID, (String) accessToken.getAdditionalInformation().get(OrcidOauth2Constants.ORCID));
                tokenData.put(OrcidOauth2Constants.CLIENT_ID, clientId);
                tokenData.put(OrcidOauth2Constants.RESOURCE_IDS, OrcidOauth2Constants.ORCID);
                tokenData.put(OrcidOauth2Constants.APPROVED, Boolean.TRUE.toString());
                if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.IS_OBO_TOKEN)) {
                    tokenData.put(OrcidOauth2Constants.IS_OBO_TOKEN, Boolean.TRUE.toString());
                }
                redisClient.set(tokenValue, JsonUtils.convertToJsonString(tokenData));
            } catch(Exception e) {
                LOGGER.info("Unable to set token in Redis cache", e);
            }
        }
    }

    /**
     * Calculates the real value of the "expires_in" param based on the current time
     * 
     * @param formParams
     *          The params container
     *          
     * @return the expiration time in milliseconds based on the param OrcidOauth2Constants.EXPIRES_IN.
     * @throws IllegalArgumentException in case the parameter is not a number 
     * */
    private Long calculateExpiresIn(MultivaluedMap<String, String> formParams) {
        if(!formParams.containsKey(OrcidOauth2Constants.EXPIRES_IN)){
            return 0L;
        }
        
        String expiresInParam = formParams.getFirst(OrcidOauth2Constants.EXPIRES_IN);
        Long result = 0L;
        
        try {
            result = Long.valueOf(expiresInParam);
        } catch(Exception e) {
            throw new IllegalArgumentException(expiresInParam + " is not a number");
        }
        
        return result == 0 ? result : (System.currentTimeMillis() + (result * 1000));
    }
    
    protected OAuth2AccessToken generateToken(Authentication client, Set<String> scopes, String grantType) {
        return generateToken(client, scopes, null, null, grantType, null, null, null, false, 0L, null, null, null);
    }
    
    protected OAuth2AccessToken generateToken(Authentication client, Set<String> scopes, String code, String redirectUri, String grantType, String refreshToken, String state, String authorization, boolean revokeOld, Long expiresIn, String subjectToken, String subjectTokenType, String requestedTokenType) {        
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
                
                //This will pass through to the token generator as a request param.
                if (authorizationCodeEntity.getNonce() !=null){
                    authorizationParameters.put(OrcidOauth2Constants.NONCE, authorizationCodeEntity.getNonce());
                }
            } else {
                authorizationParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "false");
            }                        
        }
        
        //If it is a refresh token request, set the needed authorization parameters
        if(OrcidOauth2Constants.REFRESH_TOKEN.equals(grantType)) {
            authorizationParameters.put(OrcidOauth2Constants.AUTHORIZATION, authorization);
            authorizationParameters.put(OrcidOauth2Constants.REVOKE_OLD, String.valueOf(revokeOld));
            authorizationParameters.put(OrcidOauth2Constants.EXPIRES_IN, String.valueOf(expiresIn));
            authorizationParameters.put(OrcidOauth2Constants.REFRESH_TOKEN, String.valueOf(refreshToken));
        } else if (OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE.equals(grantType)) {
            authorizationParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, String.valueOf(subjectToken));
            authorizationParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, String.valueOf(subjectTokenType));
            authorizationParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, String.valueOf(requestedTokenType));  
            //required so OrcidRandomValueTokenServicesImpl doesn't generate a refresh token
            authorizationParameters.put(OrcidOauth2Constants.GRANT_TYPE, OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE);  
        }
        
        if (redirectUri != null) {
            authorizationParameters.put(OAuth2Utils.REDIRECT_URI, redirectUri);
        }        
        AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(authorizationParameters);   
                
        TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(authorizationRequest, grantType);                
        //Need to change this to either the DefaultTokenType or start using a different token type.
        OAuth2AccessToken token = getTokenGranter().grant(grantType, tokenRequest);
        Object params[] = {grantType};
        if (token == null) {
            LOGGER.error("Unsupported grant type for OAuth2: clientId={}, grantType={}, code={}", new Object[] {
                    clientId, grantType, code});
            throw new UnsupportedGrantTypeException(localeManager.resolveMessage("apiError.unsupported_client_type.exception", params));
        }
        
        Long tokenId = token.getAdditionalInformation() != null ? (Long) token.getAdditionalInformation().get(OrcidOauth2Constants.TOKEN_ID) : null;
        
        LOGGER.info("OAuth2 access token granted: tokenId={}, clientId={}, code={}, scopes={}", new Object[] {
                tokenId, clientId, code, token.getScope() });
        
        return token;
    }
    
    protected void removeMetadataFromToken(OAuth2AccessToken accessToken) {
        if(accessToken != null && accessToken.getAdditionalInformation() != null) {
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.TOKEN_VERSION))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.TOKEN_VERSION);
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.PERSISTENT))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.PERSISTENT);
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.DATE_CREATED))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.DATE_CREATED);
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.TOKEN_ID))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.TOKEN_ID);
            if(accessToken.getAdditionalInformation().containsKey(OrcidOauth2Constants.TOKEN_DISABLED))
                accessToken.getAdditionalInformation().remove(OrcidOauth2Constants.TOKEN_DISABLED);
        }
    }
    
    protected Response getResponse(OAuth2AccessToken accessToken) {                        
        return Response.ok((DefaultOAuth2AccessToken)accessToken).header("Cache-Control", "no-store").header("Pragma", "no-cache").build();
    }

    protected Authentication getClientAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication;
        } else {
            throw new InsufficientAuthenticationException(localeManager.resolveMessage("apiError.client_authentication_notfound.exception"));
        }

    }

    public boolean isTokenCacheEnabled() {
        return isTokenCacheEnabled;
    }

    public void setTokenCacheEnabled(boolean isTokenCacheEnabled) {
        this.isTokenCacheEnabled = isTokenCacheEnabled;
    }

}
