package org.orcid.core.oauth.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 19/04/2012
 */
@Service
public class OrcidOauth2TokenDetailServiceImpl implements OrcidOauth2TokenDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidOauth2TokenDetailServiceImpl.class);

    @Resource(name="orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Resource(name="orcidOauth2TokenDetailDaoReadOnly")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDaoReadOnly;
    
    @Resource
    private RedisClient redisClient;
    
    @Value("${org.orcid.core.utils.cache.redis.enabled:true}") 
    private boolean isTokenCacheEnabled;

    @Override
    public void setOrcidOauth2TokenDetailDao(OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao) {
        this.orcidOauth2TokenDetailDao = orcidOauth2TokenDetailDao;
    }
    
    @Override
    public OrcidOauth2TokenDetail findNonDisabledByTokenValue(String token) {
        if(StringUtils.isBlank(token)) {
            return null;
        }
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findNonDisabledByTokenValue(token);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for token value {}", e, token);
            return null;
        }
    }

    @Override
    public OrcidOauth2TokenDetail findIgnoringDisabledByTokenValue(String token) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByTokenValue(token);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for token value {}", e, token);
            return null;
        }
    }    
    
    @Override
    public OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByRefreshTokenValue(refreshTokenValue);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for refresh token value {}", e, refreshTokenValue);
            return null;
        }
    }

    @Override
    @Transactional
    public void removeByRefreshTokenValue(String refreshToken) {
        orcidOauth2TokenDetailDao.removeByRefreshTokenValue(refreshToken);
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByAuthenticationKey(String authKey) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByAuthenticationKey(authKey);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for auth token {}", e, authKey);
            return null;
        }
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByUserName(String userName) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByUserName(userName);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for username {}", e, userName);
            return null;
        }
    }
    
    @Override
    @Cacheable(value = "count-tokens", key = "#userName.concat('-').concat(#lastModified)")
    public boolean hasToken(String userName, long lastModified) {
        return orcidOauth2TokenDetailDaoReadOnly.hasToken(userName);
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByClientId(String clientId) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByClientId(clientId);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for client id {}", e, clientId);
            return null;
        }
    }

    /**
     * This should NOT delete the row, but merely set it as disabled 
     * 
     * @param accessToken
     *            the value to use to identify the row containing the access
     *            token
     */
    @Override    
    @Transactional
    public void disableAccessToken(String accessToken) {
        orcidOauth2TokenDetailDao.disableAccessToken(accessToken);
    }
    
    @Override
    @Transactional
    public void revokeAccessToken(String accessToken) {
        // Remove the token from the cache
        if(isTokenCacheEnabled) {
            redisClient.remove(accessToken);
        }
        // Revoke the token
        orcidOauth2TokenDetailDao.revokeAccessToken(accessToken);
    }
    
    /**
     * This should NOT delete the row, but merely set it as disabled
     * 
     * @param tokenId
     *            the id of the token that should be disabled
     * @param userOrcid
     *            the id of the user owner of the token
     */
    @Override    
    @Transactional
    public void disableAccessToken(Long tokenId, String userOrcid) {
        if(PojoUtil.isEmpty(userOrcid) || tokenId == null) {
            throw new IllegalArgumentException("One of the provided params is empty: userOrcid='" + userOrcid + "' tokenId='" + String.valueOf(tokenId) + "'");
        }
        
        //Iterate over all tokens that belongs to this user and client, to remove all the ones that have the same scopes
        OrcidOauth2TokenDetail tokenToDisable = orcidOauth2TokenDetailDaoReadOnly.find(tokenId);
        String scopesToDisableString = tokenToDisable.getScope();
        Set<ScopePathType> scopesToDisable = ScopePathType.getScopesFromSpaceSeparatedString(scopesToDisableString);
        
        List<OrcidOauth2TokenDetail> allTokens = orcidOauth2TokenDetailDaoReadOnly.findByClientIdAndUserName(tokenToDisable.getClientDetailsId(), userOrcid);
        //Iterate over all tokens and verify we disable all the ones that have the same scopes
        for(OrcidOauth2TokenDetail token : allTokens) {
            if(token.getTokenDisabled() == null || !token.getTokenDisabled()) {
                if(!PojoUtil.isEmpty(token.getScope())) {
                    Set<ScopePathType> tokenScopes = ScopePathType.getScopesFromSpaceSeparatedString(token.getScope());
                    if(scopesToDisable.equals(tokenScopes)) {
                        orcidOauth2TokenDetailDao.disableAccessTokenById(token.getId(), userOrcid);
                    }                
                }            
            }
        }                        
    }
        
    /**
     * This should NOT delete the row, but merely remove the value from it
     * 
     * @param refreshTokenValue
     *            the value to use to identify the row containing the access
     *            token
     */
    @Override
    @Transactional
    public void disableAccessTokenByRefreshToken(String refreshTokenValue) {
        orcidOauth2TokenDetailDao.disableAccessTokenByRefreshToken(refreshTokenValue);
    }

    @Override
    @Transactional
    public void createNew(OrcidOauth2TokenDetail detail) {
        orcidOauth2TokenDetailDao.persist(detail); 
        orcidOauth2TokenDetailDao.flush();
    }        

    @Override
    public List<OrcidOauth2TokenDetail> findByClientIdAndUserName(String clientId, String userName) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByClientIdAndUserName(clientId, userName);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for client id {}", e, clientId);
            return null;
        }
    }
    
    @Override
    public boolean doesClientKnowUser(String clientId, String userOrcid) {
        List<OrcidOauth2TokenDetail> existingTokens = orcidOauth2TokenDetailDaoReadOnly.findByClientIdAndUserName(clientId, userOrcid);
        if (existingTokens == null || existingTokens.isEmpty()) {
            return false;
        }
        Date now = new Date();
        for (OrcidOauth2TokenDetail token : existingTokens) {
            if (token.getTokenExpiration() != null && token.getTokenExpiration().after(now) && (token.getTokenDisabled() == null || !token.getTokenDisabled())) {
                // Verify the token have at least one of the required scopes
                List<String> scopes = Arrays.asList(ScopePathType.ACTIVITIES_UPDATE.value(), ScopePathType.AFFILIATIONS_CREATE.value(), ScopePathType.AFFILIATIONS_UPDATE.value());
                if(!PojoUtil.isEmpty(token.getScope())) {
                    for(String scope : token.getScope().split(" ")) {
                        if(scopes.contains(scope.trim())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    @Transactional
    public int disableAccessTokenByCodeAndClient(String authorizationCode, String clientID, RevokeReason reason) {
        // Find the tokens to disable
        List<String> tokensToDisable = orcidOauth2TokenDetailDao.findAccessTokenByCodeAndClient(authorizationCode, clientID); 
        // Remove them from the cache
        for(String accessToken : tokensToDisable) {
            LOGGER.info("Token {} will be disabled because auth code {} was reused", accessToken, authorizationCode);
            if(isTokenCacheEnabled) {
                redisClient.remove(accessToken);
            }            
        }
        // Disable them
        return orcidOauth2TokenDetailDao.disableAccessTokenByCodeAndClient(authorizationCode, clientID, reason.name());
    }

    @Override
    @Transactional
    public void disableAccessTokenByUserOrcid(String userOrcid, RevokeReason reason) {
        orcidOauth2TokenDetailDao.disableAccessTokenByUserOrcid(userOrcid, reason.name());
    }

    @Override
    @Transactional
    public void disableClientAccess(String clientDetailsId, String userOrcid) {
        // As a security measure, remove any user tokens from the cache
        List<OrcidOauth2TokenDetail> userTokens = findByUserName(userOrcid);
        if(userTokens != null && !userTokens.isEmpty()) {
            for(OrcidOauth2TokenDetail token : userTokens) {
                if(clientDetailsId.equals(token.getClientDetailsId())) {
                    redisClient.remove(token.getTokenValue());
                }
            }
        }
        // And then disable all user tokens
        orcidOauth2TokenDetailDao.disableClientAccessTokensByUserOrcid(userOrcid, clientDetailsId);
    }
    
    @Override
    @Transactional
    public boolean updateScopes(String acessToken, Set<String> newScopes) {
        return orcidOauth2TokenDetailDao.updateScopes(acessToken, OAuth2Utils.formatParameterList(newScopes));        
    }
}
