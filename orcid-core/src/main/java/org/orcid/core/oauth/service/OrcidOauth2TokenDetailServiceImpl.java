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

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
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
     * This should NOT delete the row, but merely remove the value from it
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
    
    /**
     * Check if a member have a specific scope over a client
     * @param clientId
     * @param userName
     * @param scopes
     * @return true if the member have access to any of the specified scope on the specified user
     * */
    @Override
    public boolean checkIfScopeIsAvailableForMember(String clientId, String userName, List<String> scopes) {
        List<String> availableScopes = orcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userName);
        for(String availableScope : availableScopes) {
            for(String scope: scopes) {
                if(availableScope.contains(scope))
                    return true;
            }
        }
        return false;
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
}
