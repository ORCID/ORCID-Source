package org.orcid.core.oauth.service;

import java.util.*;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.exception.ClientDeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
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
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Sets;

/**
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidRandomValueTokenServicesImpl extends DefaultTokenServices implements OrcidRandomValueTokenServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRandomValueTokenServicesImpl.class);
    
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;
    @Value("${org.orcid.core.token.read_validity_seconds:631138519}")
    private int readValiditySeconds;
    @Value("${org.orcid.core.token.implicit_validity_seconds:600}")
    private int implicitValiditySeconds;
    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int ietfTokenExchangeValiditySeconds;
    
    private OrcidTokenStore orcidTokenStore;

    private TokenEnhancer customTokenEnhancer;
    
    @Resource(name="orcidOauth2AuthoriziationCodeDetailDaoReadOnly")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDaoReadOnly;       

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    private boolean customSupportRefreshToken;
    
    @Resource(name="orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private AuthenticationKeyGenerator authenticationKeyGenerator;
    
    @Resource 
    private RedisClient redisClient;
    
    @Value("${org.orcid.core.utils.cache.redis.enabled:true}") 
    private boolean isTokenCacheEnabled;
    
    public boolean isCustomSupportRefreshToken() {
        return customSupportRefreshToken;
    }

    public void setCustomSupportRefreshToken(boolean customSupportRefreshToken) {
        this.customSupportRefreshToken = customSupportRefreshToken;
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        
        //Do we have an implicit request with update permissions?
        if (OrcidOauth2Constants.IMPLICIT_GRANT_TYPE.equals(authentication.getOAuth2Request().getGrantType())) {
            // authentication.getOAuth2Request().getResponseTypes().contains("token")) {
            Collection<String> combinedStrings = ScopePathType.getCombinedScopesFromStringsAsStrings(authentication.getOAuth2Request().getScope());
            Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(combinedStrings);
            Set<String> allowedScopes = Sets.newHashSet();
            for (ScopePathType s: requestedScopes) {
                if (ScopePathType.AUTHENTICATE.equals(s) || ScopePathType.READ_PUBLIC.equals(s) || ScopePathType.OPENID.equals(s)) {
                    allowedScopes.add(s.value());
                }
            }
            //If so, create a long life update token in the background
            if (requestedScopes.size() != allowedScopes.size()) {
                //create token behind the scenes.
                DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
                accessToken.setExpiration(new Date(System.currentTimeMillis() + (readValiditySeconds * 1000L)));
                accessToken.setScope(authentication.getOAuth2Request().getScope());
                accessToken = new DefaultOAuth2AccessToken(customTokenEnhancer.enhance(accessToken, authentication));
                //accessToken.setAdditionalInformation(Maps.newHashMap());
                orcidTokenStore.storeAccessToken(accessToken, authentication);
                
                //create a read only token request
                OAuth2Request r = authentication.getOAuth2Request().narrowScope(allowedScopes);
                authentication = new OAuth2Authentication(r, authentication.getUserAuthentication());
            }
        }
        
        //create the regular token
        DefaultOAuth2AccessToken accessToken = generateAccessToken(authentication);
        try {
            orcidTokenStore.storeAccessToken(accessToken, authentication);
        } catch(PersistenceException e) {
            // In the unlikely case that there is a constraint violation, lets try to generate the token one more time
            if(e.getCause() instanceof ConstraintViolationException) {
                accessToken = generateAccessToken(authentication);
                try {
                    orcidTokenStore.storeAccessToken(accessToken, authentication);
                    return accessToken;
                } catch(Exception e2) {
                    // Just throw the first exception
                }
                
            }
            OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
            LOGGER.error("Exception creating access token for client {}, scopes {} and user {}", new Object[] {authInfo.getClientId(), authInfo.getScopes(), authInfo.getUserOrcid()});
            LOGGER.error("Error info", e);
            throw e;
        } 
        return accessToken;
    }
    
    private DefaultOAuth2AccessToken generateAccessToken(OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            accessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }        
        accessToken.setScope(authentication.getOAuth2Request().getScope());
        
        if(customTokenEnhancer != null) {
            accessToken = new DefaultOAuth2AccessToken(customTokenEnhancer.enhance(accessToken, authentication));
        }
        
        if(!OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE.equals(authentication.getOAuth2Request().getGrantType()) 
                && this.isSupportRefreshToken(authentication.getOAuth2Request())) {
            OAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken(UUID.randomUUID().toString());
            accessToken.setRefreshToken(refreshToken);
        }
        return accessToken;
    }

    /**
     * The access token validity period in seconds
     * 
     * @param authorizationRequest
     *            the current authorization request
     * @return the access token validity period in seconds
     */
    @Override
    protected int getAccessTokenValiditySeconds(OAuth2Request authorizationRequest) {
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(authorizationRequest.getScope());
        if (isClientCredentialsGrantType(authorizationRequest)) {
            boolean allAreClientCredentialsScopes = true;

            if (requestedScopes.size() == 1) {
                if (requestedScopes.stream().findFirst().get().isInternalScope()) {
                    return readValiditySeconds;
                }
            }

            for (ScopePathType scope : requestedScopes) {
                if (!scope.isClientCreditalScope()) {
                    allAreClientCredentialsScopes = false;
                    break;
                }
            }

            if (allAreClientCredentialsScopes) {
                return readValiditySeconds;
            }
        } else if(OrcidOauth2Constants.IMPLICIT_GRANT_TYPE.equals(authorizationRequest.getGrantType())){
            return implicitValiditySeconds;
        } else if(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE.equals(authorizationRequest.getGrantType())) {
            return ietfTokenExchangeValiditySeconds;
        } else if (isPersistentTokenEnabled(authorizationRequest)) {
            return readValiditySeconds;
        } 

        return writeValiditySeconds;
    }

    public int getWriteValiditySeconds() {
        return writeValiditySeconds;
    }

    public int getReadValiditySeconds() {
        return readValiditySeconds;
    }

    /**
     * Checks the authorization code to verify if the user enable the persistent
     * token or not
     * */
    private boolean isPersistentTokenEnabled(OAuth2Request authorizationRequest) {
        if (authorizationRequest != null) {
            Map<String, String> params = authorizationRequest.getRequestParameters();
            if (params != null) {
                if (params.containsKey(OrcidOauth2Constants.IS_PERSISTENT)) {
                    String isPersistent = params.get(OrcidOauth2Constants.IS_PERSISTENT);
                    if (Boolean.valueOf(isPersistent)) {
                        return true;
                    }
                } else if (params.containsKey("code")) {
                    String code = params.get("code");
                    if (orcidOauth2AuthoriziationCodeDetailDaoReadOnly.find(code) != null) {
                        if (orcidOauth2AuthoriziationCodeDetailDaoReadOnly.isPersistentToken(code)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if the authorization request grant type is client_credentials
     * */
    private boolean isClientCredentialsGrantType(OAuth2Request authorizationRequest) {
        Map<String, String> params = authorizationRequest.getRequestParameters();
        if (params != null) {
            if (params.containsKey(OrcidOauth2Constants.GRANT_TYPE)) {
                String grantType = params.get(OrcidOauth2Constants.GRANT_TYPE);
                if (OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType))
                    return true;
            }
        }
        return false;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        LOGGER.debug("Loading authentication from token");
        // Feature flag: If the request is to delete an element, allow
        if (RequestMethod.DELETE.name().equals(attr.getRequest().getMethod())) {
            OrcidOauth2TokenDetail orcidAccessToken = orcidTokenStore.readOrcidOauth2TokenDetail(accessTokenValue);
            if (orcidAccessToken == null) {
                // Access token not found
                throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
            }
            String revokeReason = orcidAccessToken.getRevokeReason();
            if (PojoUtil.isEmpty(revokeReason) || RevokeReason.USER_REVOKED.equals(RevokeReason.valueOf(revokeReason))) {
                OAuth2AccessToken accessToken = orcidTokenStore.readEvenDisabledAccessToken(accessTokenValue);
                validateTokenExpirationAndClientStatus(accessToken, accessTokenValue);
                return orcidTokenStore.readAuthenticationEvenOnDisabledTokens(accessTokenValue);
            } else {
                throw new InvalidTokenException("Invalid access token: " + accessTokenValue + ", revoke reason: " + revokeReason);
            }
        } else {
            // Get the token from the cache
            Map<String, String> cachedAccessToken = getTokenFromCache(accessTokenValue);
            if(cachedAccessToken != null) {
                 return orcidTokenStore.readAuthenticationFromCachedToken(cachedAccessToken);                 
            }  else {
                // Fallback to database if it is not in the cache
                OAuth2AccessToken accessToken = orcidTokenStore.readAccessToken(accessTokenValue);
                validateTokenExpirationAndClientStatus(accessToken, accessTokenValue);
                return orcidTokenStore.readAuthentication(accessTokenValue);
            }            
        }
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
    
    private void validateTokenExpirationAndClientStatus(OAuth2AccessToken accessToken, String tokenValue) {
        // Throw an exception if access token is not found
        if (accessToken != null) {
            // Throw an exception if the token is expired
            if (accessToken.isExpired()) {
                orcidTokenStore.removeAccessToken(accessToken);
                throw new InvalidTokenException("Access token expired: " + tokenValue);
            }
            Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
            if (additionalInfo != null) {
                String clientId = (String) additionalInfo.get(OrcidOauth2Constants.CLIENT_ID);
                orcidTokenStore.isClientEnabled(clientId);
            }
        } else {
            // Access token not found
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        }
    }        
    
    public void setOrcidtokenStore(OrcidTokenStore orcidTokenStore) {
        super.setTokenStore(orcidTokenStore);
        this.orcidTokenStore = orcidTokenStore;
    }

    public void setCustomTokenEnhancer(TokenEnhancer customTokenEnhancer) {
        super.setTokenEnhancer(customTokenEnhancer);
        this.customTokenEnhancer = customTokenEnhancer;
    }        
    
    public boolean longLifeTokenExist(String clientId, String userId, Collection<String> scopes) {
        Collection<OAuth2AccessToken> existingTokens = orcidTokenStore.findTokensByClientIdAndUserName(clientId, userId);
        
        if(existingTokens == null || existingTokens.isEmpty()) {
            return false;
        }
        
        for(OAuth2AccessToken token : existingTokens) {
            if (token.getAdditionalInformation().get(OrcidOauth2Constants.PERSISTENT) != null && Boolean.valueOf((token.getAdditionalInformation().get("persistent").toString()))){
                if(token.getScope().containsAll(scopes) && scopes.containsAll(token.getScope())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    //see https://github.com/spring-projects/spring-security-oauth/pull/957#issuecomment-304729865
    @Override
    @Transactional
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
        String parentTokenValue = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.AUTHORIZATION);
        String clientId = tokenRequest.getClientId();
        String scopes = tokenRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
        Long expiresIn = tokenRequest.getRequestParameters().containsKey(OrcidOauth2Constants.EXPIRES_IN)
                ? Long.valueOf(tokenRequest.getRequestParameters().get(OrcidOauth2Constants.EXPIRES_IN)) : 0L;
        Boolean revokeOld = tokenRequest.getRequestParameters().containsKey(OrcidOauth2Constants.REVOKE_OLD) ? Boolean.valueOf(tokenRequest.getRequestParameters().get(OrcidOauth2Constants.REVOKE_OLD)) : true;

        // Check if the refresh token is enabled
        if (!customSupportRefreshToken) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        }
        // Check if the client support refresh token
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        if (!clientDetails.getAuthorizedGrantTypes().contains(OrcidOauth2Constants.REFRESH_TOKEN)) {
            throw new InvalidGrantException("Client " + clientId + " doesnt have refresh token enabled");
        }

        OrcidOauth2TokenDetail parentToken = orcidOauth2TokenDetailDao.findByRefreshTokenValue(refreshTokenValue);

        ProfileEntity profileEntity = new ProfileEntity(parentToken.getOrcid());
        OrcidOauth2TokenDetail newToken = new OrcidOauth2TokenDetail();

        newToken.setApproved(true);
        newToken.setClientDetailsId(clientId);
        newToken.setPersistent(parentToken.isPersistent());
        newToken.setOrcid(parentToken.getOrcid());
        newToken.setRedirectUri(parentToken.getRedirectUri());
        newToken.setRefreshTokenValue(UUID.randomUUID().toString());
        newToken.setResourceId(parentToken.getResourceId());
        newToken.setResponseType(parentToken.getResponseType());
        newToken.setState(parentToken.getState());
        newToken.setTokenDisabled(false);
        if(expiresIn <= 0) {
            //If expiresIn is 0 or less, set the parent token 
            newToken.setTokenExpiration(parentToken.getTokenExpiration());
        } else {
            //Assumes expireIn already contains the real expired time expressed in millis 
            newToken.setTokenExpiration(new Date(expiresIn));   
        }        
        newToken.setTokenType(parentToken.getTokenType());
        newToken.setTokenValue(UUID.randomUUID().toString());
        newToken.setVersion(parentToken.getVersion());

        if (PojoUtil.isEmpty(scopes)) {
            newToken.setScope(parentToken.getScope());
        } else {
            newToken.setScope(scopes);
        }

        //Generate an authentication object to be able to generate the authentication key
        Set<String> scopesSet = OAuth2Utils.parseParameterList(newToken.getScope());
        AuthorizationRequest request = new AuthorizationRequest(clientId, scopesSet);        
        request.setApproved(true);
        Authentication authentication = new OrcidOauth2UserAuthentication(profileEntity, true);        
        OrcidOAuth2Authentication orcidAuthentication = new OrcidOAuth2Authentication(request, authentication, newToken.getTokenValue());
        newToken.setAuthenticationKey(authenticationKeyGenerator.extractKey(orcidAuthentication));
        
        // Store the new token and return it
        orcidOauth2TokenDetailDao.persist(newToken);

        // Revoke the old token when required
        if (revokeOld) {
            orcidOauth2TokenDetailDao.disableAccessToken(parentTokenValue);
        }

        // Save the changes
        orcidOauth2TokenDetailDao.flush();

        // Transform the OrcidOauth2TokenDetail into a OAuth2AccessToken
        // and return it                
        return toOAuth2AccessToken(newToken);
    }
    
    private OAuth2AccessToken toOAuth2AccessToken(OrcidOauth2TokenDetail token) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(token.getTokenValue());
        result.setExpiration(token.getTokenExpiration());                         
        result.setRefreshToken(new DefaultOAuth2RefreshToken(token.getRefreshTokenValue()));
        result.setScope(OAuth2Utils.parseParameterList(token.getScope()));
        result.setTokenType(token.getTokenType());
        result.setValue(token.getTokenValue());
        
        Map<String, Object> additionalInfo = new HashMap<String, Object>();
        if(token.getOrcid() != null) {
            additionalInfo.put(OrcidOauth2Constants.ORCID, token.getOrcid());
            additionalInfo.put(OrcidOauth2Constants.NAME, profileEntityManager.retrivePublicDisplayName(token.getOrcid()));
        }                        
        
        result.setAdditionalInformation(additionalInfo);
        return result;        
    }

    @Override
    public OAuth2AccessToken createRevokedAccessToken(OAuth2Authentication authentication, RevokeReason revokeReason) throws AuthenticationException {
        // create the regular token
        DefaultOAuth2AccessToken accessToken = generateAccessToken(authentication);
        try {
            if(accessToken.getAdditionalInformation() == null) {
                accessToken.setAdditionalInformation(Collections.emptyMap());
            }
            accessToken.getAdditionalInformation().put(OrcidOauth2Constants.TOKEN_DISABLED, true);
            orcidTokenStore.storeRevokedAccessToken(accessToken, authentication, revokeReason);
        } catch (PersistenceException e) {
            // In the unlikely case that there is a constraint violation, lets
            // try to generate the token one more time
            if (e.getCause() instanceof ConstraintViolationException) {
                accessToken = generateAccessToken(authentication);
                try {
                    orcidTokenStore.storeRevokedAccessToken(accessToken, authentication, revokeReason);
                    return accessToken;
                } catch (Exception e2) {
                    // Just throw the first exception
                }

            }
            OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
            LOGGER.error("Exception creating revoked access token for client {}, scopes {} and user {}",
                    new Object[] { authInfo.getClientId(), authInfo.getScopes(), authInfo.getUserOrcid() });
            LOGGER.error("Error info", e);
            throw e;
        }
        return accessToken;
    }
}
