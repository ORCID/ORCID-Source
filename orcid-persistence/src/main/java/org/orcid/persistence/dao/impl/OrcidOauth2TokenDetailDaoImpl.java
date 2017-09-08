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
package org.orcid.persistence.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author Declan Newman
 */
public class OrcidOauth2TokenDetailDaoImpl extends GenericDaoImpl<OrcidOauth2TokenDetail, Long> implements OrcidOauth2TokenDetailDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidOauth2TokenDetailDaoImpl.class);

    public OrcidOauth2TokenDetailDaoImpl() {
        super(OrcidOauth2TokenDetail.class);
    }

    @Override
    public OrcidOauth2TokenDetail findByTokenValue(String tokenValue) {
        Assert.hasText(tokenValue, "Attempt to retrieve a OrcidOauth2TokenDetail with a null or empty token value");
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from OrcidOauth2TokenDetail where tokenValue = :token", OrcidOauth2TokenDetail.class);
        query.setParameter("token", tokenValue);
        return query.getSingleResult();
    }

    @Override
    public OrcidOauth2TokenDetail findNonDisabledByTokenValue(String tokenValue) {
        Assert.hasText(tokenValue, "Attempt to retrieve a OrcidOauth2TokenDetail with a null or empty token value");
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from "
                + "OrcidOauth2TokenDetail where tokenValue = :token and (tokenDisabled = FALSE or tokenDisabled is null)", OrcidOauth2TokenDetail.class);
        query.setParameter("token", tokenValue);
        return query.getSingleResult();
    }

    @Override
    @ExcludeFromProfileLastModifiedUpdate
    public void removeByTokenValue(String tokenValue) {
        Assert.hasText(tokenValue, "Attempt to retrieve a OrcidOauth2TokenDetail with a null or empty token value");
        Query query = entityManager.createQuery("delete from OrcidOauth2TokenDetail where tokenValue = " + ":tokenValue");
        query.setParameter("tokenValue", tokenValue);
        int i = query.executeUpdate();
        if (i == 0) {
            LOGGER.info("Attempted to delete access token {0} but it was not present in the database", tokenValue);
        }
    }

    @Override
    @ExcludeFromProfileLastModifiedUpdate
    public void removeByRefreshTokenValue(String refreshTokenValue) {
        Query query = entityManager.createQuery("delete from OrcidOauth2TokenDetail where refreshTokenValue = " + ":refreshToken");
        query.setParameter("refreshToken", refreshTokenValue);
        int i = query.executeUpdate();
        if (i == 0) {
            LOGGER.info("Attempted to delete refresh token {0} but it was not present in the database", refreshTokenValue);
        }
    }

    @Override
    public void removeByUserOrcidAndClientOrcid(String userOrcid, String clientOrcid) {
        Query query = entityManager.createQuery("delete from OrcidOauth2TokenDetail where profile.id = :userOrcid and clientDetailsId = :clientOrcid");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("clientOrcid", clientOrcid);
        int i = query.executeUpdate();
        if (i == 0) {
            LOGGER.info("Attempted to tokens for user {0} and client application {1} but none were present in the database", userOrcid, clientOrcid);
        }
    }

    @Override
    public OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where refreshTokenValue = :refreshTokenValue",
                OrcidOauth2TokenDetail.class);
        query.setParameter("refreshTokenValue", refreshTokenValue);
        return query.getSingleResult();
    }

    @Override
    @ExcludeFromProfileLastModifiedUpdate
    public List<OrcidOauth2TokenDetail> findByAuthenticationKey(String authenticationKey) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where authenticationKey = :authenticationKey",
                OrcidOauth2TokenDetail.class);
        query.setParameter("authenticationKey", authenticationKey);
        return query.getResultList();
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByUserName(String userName) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from OrcidOauth2TokenDetail where profile.id = :userName and tokenExpiration > now() and (tokenDisabled IS NULL OR tokenDisabled = FALSE)",
                OrcidOauth2TokenDetail.class);
        query.setParameter("userName", userName);
        return query.getResultList();
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByClientId(String clientId) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where clientDetailsId = :clientId",
                OrcidOauth2TokenDetail.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }
    
    @Override
    public List<OrcidOauth2TokenDetail> findByClientIdAndUserName(String clientId, String userName) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from OrcidOauth2TokenDetail where clientDetailsId = :clientId and profile.id = :userName",
                OrcidOauth2TokenDetail.class);
        query.setParameter("clientId", clientId);
        query.setParameter("userName", userName);
        return query.getResultList();
    }

    @Override
    public void disableAccessToken(String accessTokenValue) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE where tokenValue = :accessTokenValue");
        query.setParameter("accessTokenValue", accessTokenValue);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the refresh token {0}", accessTokenValue);
        }
    }

    @Override
    @Transactional
    public void disableAccessTokenById(Long tokenId, String userOrcid) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE where id = :tokenId and profile.id = :userOrcid");
        query.setParameter("tokenId", tokenId);
        query.setParameter("userOrcid", userOrcid);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the token with id {0}", tokenId);
        }
    }
    
    @Override
    public void disableAccessTokenByRefreshToken(String refreshTokenValue) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE where " + "refreshTokenValue = :refreshTokenValue");
        query.setParameter("refreshTokenValue", refreshTokenValue);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the refresh token {0}", refreshTokenValue);
        }
    }

    @Override
    @Transactional
    @ExcludeFromProfileLastModifiedUpdate
    public void removeByAuthenticationKeyOrTokenValueOrRefreshTokenValue(String authenticationKey, String tokenValue, String refreshTokenValue) {
        String or = " or ";
        Map<String, String> queryParams = new HashMap<String, String>();

        StringBuilder queryString = new StringBuilder("delete from OrcidOauth2TokenDetail where (");
        if (StringUtils.isNotBlank(authenticationKey)) {
            queryString.append("authenticationKey = :authenticationKey" + or);
            queryParams.put("authenticationKey", authenticationKey);
        }
        if (StringUtils.isNotBlank(tokenValue)) {
            queryString.append("tokenValue = :tokenValue" + or);
            queryParams.put("tokenValue", tokenValue);
        }
        if (StringUtils.isNotBlank(refreshTokenValue)) {
            queryString.append("refreshTokenValue = :refreshTokenValue" + or);
            queryParams.put("refreshTokenValue", refreshTokenValue);
        }

        if (!queryParams.isEmpty()) {
            queryString.replace(queryString.length() - or.length(), queryString.length(), ")");
            Query query = entityManager.createQuery(queryString.toString());
            for (String key : queryParams.keySet()) {
                query.setParameter(key, queryParams.get(key));
            }
            int i = query.executeUpdate();
            LOGGER.debug(i + " tokens deleted as a result of the parameters {}", queryParams);
        } else {
            LOGGER.info("Attempted to delete tokens with no parameters");
        }

    }

    /**
     * Get the list of available scopes for a member over a client
     * 
     * @param clientId
     * @param userName
     * @return the list of available scopes over a profile
     * */
    @SuppressWarnings("unchecked")
    public List<String> findAvailableScopesByUserAndClientId(String clientId, String userName) {
        Query query = entityManager
                .createNativeQuery("select distinct(scope_type) from oauth2_token_detail where user_orcid=:userName and client_details_id=:clientId and (token_disabled = FALSE or token_disabled is null)");
        query.setParameter("clientId", clientId);
        query.setParameter("userName", userName);
        return query.getResultList();
    }
    
    @Override
    public int findCountByUserName(String userName) {
    	Query query = entityManager
                .createNativeQuery("select count(*) from oauth2_token_detail where user_orcid=:userName");
        query.setParameter("userName", userName);
        int count = ((java.math.BigInteger)query.getSingleResult()).intValue();
        return count;
    }


    /** Disable all tokens with this code/clientID pair.  Should never be more than one.
     * 
     */
    @Override
    public int disableAccessTokenByCodeAndClient(String authorizationCode, String clientId) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE where clientDetailsId = :clientId and authorizationCode = :authorizationCode");
        query.setParameter("authorizationCode", authorizationCode);
        query.setParameter("clientId", clientId);
        int count = query.executeUpdate();
        return count;
    }
    
    @Override
    @Transactional
    public void disableAccessTokenByUserOrcid(String userOrcid) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE where profile.id = :userOrcid");        
        query.setParameter("userOrcid", userOrcid);
        query.executeUpdate();        
    }
}
