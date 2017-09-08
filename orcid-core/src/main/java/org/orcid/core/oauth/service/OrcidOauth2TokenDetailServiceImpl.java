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
package org.orcid.core.oauth.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 19/04/2012
 */
@Service
public class OrcidOauth2TokenDetailServiceImpl implements OrcidOauth2TokenDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidOauth2TokenDetailServiceImpl.class);

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Override
    public void setOrcidOauth2TokenDetailDao(OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao) {
        this.orcidOauth2TokenDetailDao = orcidOauth2TokenDetailDao;
    }
    
    @Override
    public OrcidOauth2TokenDetail findNonDisabledByTokenValue(String token) {
        try {
            return orcidOauth2TokenDetailDao.findNonDisabledByTokenValue(token);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for token value {}", e, token);
            return null;
        }
    }

    @Override
    public OrcidOauth2TokenDetail findIgnoringDisabledByTokenValue(String token) {
        try {
            return orcidOauth2TokenDetailDao.findByTokenValue(token);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for token value {}", e, token);
            return null;
        }
    }

    @Override
    public List<OrcidOauth2TokenDetail> getAll() {
        return orcidOauth2TokenDetailDao.getAll();
    }

    @Override
    @Transactional
    public void remove(OrcidOauth2TokenDetail detail) {
        orcidOauth2TokenDetailDao.remove(detail);
    }

    @Override
    @Transactional
    public void remove(String tokenValue) {
        orcidOauth2TokenDetailDao.removeByTokenValue(tokenValue);
    }

    @Override
    @Transactional
    public void saveOrUpdate(OrcidOauth2TokenDetail detail) {
        if (detail.getId() != null) detail = orcidOauth2TokenDetailDao.merge(detail);
        orcidOauth2TokenDetailDao.persist(detail);
    }

    @Override
    public Long getCount() {
        return orcidOauth2TokenDetailDao.countAll();
    }

    @Override
    @Transactional
    public void removeByTokenValue(String tokenValue) {
        orcidOauth2TokenDetailDao.removeByTokenValue(tokenValue);
    }

    @Override
    public OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue) {
        try {
            return orcidOauth2TokenDetailDao.findByRefreshTokenValue(refreshTokenValue);
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
            return orcidOauth2TokenDetailDao.findByAuthenticationKey(authKey);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for auth token {}", e, authKey);
            return null;
        }
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByUserName(String userName) {
        try {
            return orcidOauth2TokenDetailDao.findByUserName(userName);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for username {}", e, userName);
            return null;
        }
    }
    
    @Override
    @Cacheable(value = "count-tokens", key = "#userName.concat('-').concat(#lastModified)")
    public int findCountByUserName(String userName, long lastModified) {
        return orcidOauth2TokenDetailDao.findCountByUserName(userName);
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByClientId(String clientId) {
        try {
            return orcidOauth2TokenDetailDao.findByClientId(clientId);
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
    public void disableAccessToken(String accessToken) {
        orcidOauth2TokenDetailDao.disableAccessToken(accessToken);
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
    public void disableAccessToken(Long tokenId, String userOrcid) {
        if(PojoUtil.isEmpty(userOrcid) || tokenId == null) {
            throw new IllegalArgumentException("One of the provided params is empty: userOrcid='" + userOrcid + "' tokenId='" + String.valueOf(tokenId) + "'");
        }
        
        //Iterate over all tokens that belongs to this user and client, to remove all the ones that have the same scopes
        OrcidOauth2TokenDetail tokenToDisable = orcidOauth2TokenDetailDao.find(tokenId);
        String scopesToDisableString = tokenToDisable.getScope();
        Set<ScopePathType> scopesToDisable = ScopePathType.getScopesFromSpaceSeparatedString(scopesToDisableString);
        
        List<OrcidOauth2TokenDetail> allTokens = orcidOauth2TokenDetailDao.findByClientIdAndUserName(tokenToDisable.getClientDetailsId(), userOrcid);
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
    public void disableAccessTokenByRefreshToken(String refreshTokenValue) {
        orcidOauth2TokenDetailDao.disableAccessTokenByRefreshToken(refreshTokenValue);
    }

    @Override
    public void removeConflictsAndCreateNew(OrcidOauth2TokenDetail detail) {
        // We should allow multiple tokens for the same combo user-scopes, thats why we will
        // not delete based on the authentication key
        orcidOauth2TokenDetailDao.removeByAuthenticationKeyOrTokenValueOrRefreshTokenValue(null, detail.getTokenValue(), detail.getRefreshTokenValue());
        orcidOauth2TokenDetailDao.persist(detail);
    }        

    @Override
    public List<OrcidOauth2TokenDetail> findByClientIdAndUserName(String clientId, String userName) {
        try {
            return orcidOauth2TokenDetailDao.findByClientIdAndUserName(clientId, userName);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for client id {}", e, clientId);
            return null;
        }
    }
    
    @Override
    public boolean doesClientKnowUser(String clientId, String userOrcid) {
        List<OrcidOauth2TokenDetail> existingTokens = orcidOauth2TokenDetailDao.findByClientIdAndUserName(clientId, userOrcid);
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
    public int disableAccessTokenByCodeAndClient(String authorizationCode, String clientID) {
        return orcidOauth2TokenDetailDao.disableAccessTokenByCodeAndClient(authorizationCode, clientID);
    }

    @Override
    public void disableAccessTokenByUserOrcid(String userOrcid) {
        orcidOauth2TokenDetailDao.disableAccessTokenByUserOrcid(userOrcid);
    }
    
}
