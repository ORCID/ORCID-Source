package org.orcid.core.oauth.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jwt.SignedJWT;

/**
 * @author Declan Newman (declan) Date: 15/04/2012
 */

@Service("orcidTokenStore")
public class OrcidTokenStoreServiceImpl implements TokenStore {

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    
    @Resource
    private AuthenticationKeyGenerator authenticationKeyGenerator;

    /**
     * Read the authentication stored under the specified token value.
     * 
     * @param token
     *            The token value under which the authentication is stored.
     * @return The authentication, or null if none.
     */
    @Override
    @Transactional
    public OAuth2Authentication readAuthentication(String token) {
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(token);
        return getOAuth2AuthenticationFromDetails(detail);
    }

    @Override
    @Transactional
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    /**
     * Store an access token.
     * 
     * @param token
     *            The token to store.
     * @param authentication
     *            The authentication associated with the token.
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        OrcidOauth2TokenDetail detail = populatePropertiesFromTokenAndAuthentication(token, authentication, null);
        orcidOauthTokenDetailService.createNew(detail);
        // Set the token id in the additional details
        token.getAdditionalInformation().put(OrcidOauth2Constants.TOKEN_ID, detail.getId());
    }

    /**
     * Read an access token from the store.
     * 
     * @param tokenValue
     *            The token value.
     * @return The access token to read.
     */
    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        if (tokenValue == null) {
            return null;
        }

        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(tokenValue);
        return getOauth2AccessTokenFromDetails(detail);
    }

    /**
     * Remove an access token from the database.
     * 
     * @param tokenValue
     *            The token to remove from the database.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeAccessToken(OAuth2AccessToken accessToken) {
        orcidOauthTokenDetailService.disableAccessToken(accessToken.getValue());
    }

    /**
     * Store the specified refresh token in the database.
     * 
     * @param refreshToken
     *            The refresh token to store.
     * @param authentication
     *            The authentication associated with the refresh token.
     */
    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        // The refresh token will be stored during the token creation. We don't
        // want to create it beforehand
    }

    /**
     * Read a refresh token from the store.
     * 
     * @param refreshTokenValue
     *            The value of the token to read.
     * @return The token.
     */
    @Override
    public ExpiringOAuth2RefreshToken readRefreshToken(String refreshTokenValue) {
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findByRefreshTokenValue(refreshTokenValue);
        if (detail != null && StringUtils.isNotBlank(detail.getTokenValue())) {
            return new DefaultExpiringOAuth2RefreshToken(detail.getRefreshTokenValue(), detail.getRefreshTokenExpiration());
        }
        return null;
    }

    /**
     * @param refreshTokenValue
     *            a refresh token value
     * @return the authentication originally used to grant the refresh token
     */
    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken) {
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findByRefreshTokenValue(refreshToken.getValue());
        return getOAuth2AuthenticationFromDetails(detail);
    }

    /**
     * Remove a refresh token from the database.
     * 
     * @param refreshTokenValue
     *            The value of the token to remove from the database.
     */
    @Override
    @Transactional
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        orcidOauthTokenDetailService.removeByRefreshTokenValue(refreshToken.getValue());
    }

    /**
     * Remove an access token using a refresh token. This functionality is
     * necessary so refresh tokens can't be used to create an unlimited number
     * of access tokens.
     * 
     * @param refreshTokenValue
     *            The refresh token.
     */
    @Override
    @Transactional
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        orcidOauthTokenDetailService.disableAccessTokenByRefreshToken(refreshToken.getValue());
    }

    /**
     * Retrieve an access token stored against the provided authentication key,
     * if it exists.
     * 
     * @param authentication
     *            the authentication key for the access token
     * @return the access token or null if there was none
     */
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String authKey = authenticationKeyGenerator.extractKey(authentication);
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByAuthenticationKey(authKey);
        // Since we are now able to have more than one token for the combo
        // user-scopes, we will need to return the oldest token, the first token
        // created with these scopes
        if (details != null && !details.isEmpty()) {
            OrcidOauth2TokenDetail oldestToken = null;
            for (OrcidOauth2TokenDetail tokenDetails : details) {
                if (oldestToken == null) {
                    oldestToken = tokenDetails;
                } else {
                    if (tokenDetails.getDateCreated().before(oldestToken.getDateCreated())) {
                        oldestToken = tokenDetails;
                    }
                }
            }
            return getOauth2AccessTokenFromDetails(oldestToken);
        }
        return null;
    }

    /**
     * @param userName
     *            the user name to search
     * @return a collection of access tokens
     */    
    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByUserName(userName);
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        if (details != null && !details.isEmpty()) {
            for (OrcidOauth2TokenDetail detail : details) {
                accessTokens.add(getOauth2AccessTokenFromDetails(detail));
            }
        }
        return accessTokens;
    }

    /**
     * @param clientId
     *            the client id
     * @return a collection of access tokens
     */
    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByClientId(clientId);
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        if (details != null && !details.isEmpty()) {
            for (OrcidOauth2TokenDetail detail : details) {
                accessTokens.add(getOauth2AccessTokenFromDetails(detail));
            }
        }
        return accessTokens;
    }

    private OAuth2AccessToken getOauth2AccessTokenFromDetails(OrcidOauth2TokenDetail detail) {
        DefaultOAuth2AccessToken token = null;
        if (detail != null && StringUtils.isNotBlank(detail.getTokenValue())) {
            token = new DefaultOAuth2AccessToken(detail.getTokenValue());
            token.setExpiration(detail.getTokenExpiration());
            token.setScope(OAuth2Utils.parseParameterList(detail.getScope()));
            token.setTokenType(detail.getTokenType());
            String refreshToken = detail.getRefreshTokenValue();
            OAuth2RefreshToken rt;
            if (StringUtils.isNotBlank(refreshToken)) {
                if (detail.getRefreshTokenExpiration() != null) {
                    rt = new DefaultExpiringOAuth2RefreshToken(detail.getRefreshTokenValue(), detail.getRefreshTokenExpiration());
                } else {
                    rt = new DefaultOAuth2RefreshToken(detail.getRefreshTokenValue());
                }
                token.setRefreshToken(rt);
            }
            ProfileEntity profile = detail.getProfile();                        
            if (profile != null) {
                Map<String, Object> additionalInfo = new HashMap<String, Object>();
                additionalInfo.put(OrcidOauth2Constants.ORCID, profile.getId());
                additionalInfo.put(OrcidOauth2Constants.PERSISTENT, detail.isPersistent());
                additionalInfo.put(OrcidOauth2Constants.DATE_CREATED, detail.getDateCreated());
                additionalInfo.put(OrcidOauth2Constants.TOKEN_VERSION, detail.getVersion());
                token.setAdditionalInformation(additionalInfo);
            }
            
            String clientId = detail.getClientDetailsId();
            if(!PojoUtil.isEmpty(clientId)) {
                Map<String, Object> additionalInfo = new HashMap<String, Object>(); 
                Map<String, Object> additionalInfoInToken = token.getAdditionalInformation();
                if(additionalInfoInToken != null && !additionalInfoInToken.isEmpty()) {
                    additionalInfo.putAll(additionalInfoInToken);
                } 
                // Copy to a new one to avoid unmodifiable  
                additionalInfo.put(OrcidOauth2Constants.CLIENT_ID, clientId);
                token.setAdditionalInformation(additionalInfo);
            }
        }

        return token;
    }

    private OAuth2Authentication getOAuth2AuthenticationFromDetails(OrcidOauth2TokenDetail details) {
        if (details != null) {
            ClientDetailsEntity clientDetailsEntity = clientDetailsEntityCacheManager.retrieve(details.getClientDetailsId());
            Authentication authentication = null;
            AuthorizationRequest request = null;
            if (clientDetailsEntity != null) {
                //Check member is not locked                
                orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetailsEntity);
                Set<String> scopes = OAuth2Utils.parseParameterList(details.getScope());
                request = new AuthorizationRequest(clientDetailsEntity.getClientId(), scopes);
                request.setAuthorities(clientDetailsEntity.getAuthorities());
                Set<String> resourceIds = new HashSet<>();
                resourceIds.add(details.getResourceId());
                request.setResourceIds(resourceIds);
                request.setApproved(details.isApproved());
                ProfileEntity profile = details.getProfile();
                if (profile != null) {
                    authentication = new OrcidOauth2UserAuthentication(profile, details.isApproved());
                }
            }
            return new OrcidOAuth2Authentication(request, authentication, details.getTokenValue());
        }
        throw new InvalidTokenException("Token not found");
    }

    private OrcidOauth2TokenDetail populatePropertiesFromTokenAndAuthentication(OAuth2AccessToken token, OAuth2Authentication authentication,
            OrcidOauth2TokenDetail detail) {
        OAuth2Request authorizationRequest = authentication.getOAuth2Request();
        if (detail == null) {
            detail = new OrcidOauth2TokenDetail();
        }
        
        //Update to put auth code in token detail so it can be revoked based on code if needed.
        if (authentication.getOAuth2Request().getRequestParameters().get("code") != null){
            detail.setAuthorizationCode(authentication.getOAuth2Request().getRequestParameters().get("code").toString());
        }
        
        String clientId = authorizationRequest.getClientId();
        String authKey = authenticationKeyGenerator.extractKey(authentication);
        detail.setAuthenticationKey(authKey);
        detail.setClientDetailsId(clientId);

        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        if (refreshToken != null && StringUtils.isNotBlank(refreshToken.getValue())) {
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                // Override the refresh token expiration from the client
                // details, and make it the same as the token itself
                detail.setRefreshTokenExpiration(token.getExpiration());
            }
            detail.setRefreshTokenValue(refreshToken.getValue());
        }
        if (!authentication.isClientOnly()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof ProfileEntity) {
                ProfileEntity profileEntity = (ProfileEntity) authentication.getPrincipal();
                profileEntity = profileEntityCacheManager.retrieve(profileEntity.getId());
                detail.setProfile(profileEntity);
            }
        }

        detail.setTokenValue(token.getValue());
        detail.setTokenType(token.getTokenType());
        detail.setTokenExpiration(token.getExpiration());
        detail.setApproved(authorizationRequest.isApproved());
        detail.setRedirectUri(authorizationRequest.getRedirectUri());

        Set<String> resourceIds = authorizationRequest.getResourceIds();
        if(resourceIds == null || resourceIds.isEmpty()) {
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
            resourceIds = clientDetails.getResourceIds();
        }
        
        detail.setResourceId(OAuth2Utils.formatParameterList(resourceIds));
        detail.setResponseType(OAuth2Utils.formatParameterList(authorizationRequest.getResponseTypes()));
        detail.setScope(OAuth2Utils.formatParameterList(authorizationRequest.getScope()));        

        Map<String, Object> additionalInfo = token.getAdditionalInformation();
        if (additionalInfo != null) {
            if (additionalInfo.containsKey(OrcidOauth2Constants.TOKEN_VERSION)) {
                String sVersion = String.valueOf(additionalInfo.get(OrcidOauth2Constants.TOKEN_VERSION));
                detail.setVersion(Long.valueOf(sVersion));
            } else {
                // TODO: As of Jan 2015 all tokens will be new tokens, so, we
                // will have to remove the token version code and
                // treat all tokens as new tokens
                detail.setVersion(Long.valueOf(OrcidOauth2Constants.PERSISTENT_TOKEN));
            }

            if (additionalInfo.containsKey(OrcidOauth2Constants.PERSISTENT)) {
                boolean isPersistentKey = (Boolean) additionalInfo.get(OrcidOauth2Constants.PERSISTENT);
                detail.setPersistent(isPersistentKey);
            } else {
                detail.setPersistent(false);
            }
        } else {
            detail.setPersistent(false);
        }
        
        //Set OBO client if possible.
        if(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE.equals(authentication.getOAuth2Request().getGrantType()) 
                && authentication.getOAuth2Request().getRequestParameters().containsKey(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN)
                && OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN.equals(authentication.getOAuth2Request().getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE))) {
            try {
                SignedJWT claims = SignedJWT.parse(authentication.getOAuth2Request().getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN));
                detail.setOboClientDetailsId(claims.getJWTClaimsSet().getAudience().get(0));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Unexpected id token value, cannot parse the id_token");
            }
        }       

        return detail;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByClientIdAndUserName(clientId, userName);
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        if (details != null && !details.isEmpty()) {
            for (OrcidOauth2TokenDetail detail : details) {
                if(detail.getTokenDisabled() == null || !detail.getTokenDisabled()) {
                    accessTokens.add(getOauth2AccessTokenFromDetails(detail));
                }                
            }
        }
        return accessTokens;
    }          
}
