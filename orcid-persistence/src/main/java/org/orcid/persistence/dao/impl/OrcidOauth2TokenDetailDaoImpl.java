/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.apache.commons.lang.StringUtils;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where tokenValue = :token", OrcidOauth2TokenDetail.class);
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
        Query query = entityManager.createQuery("delete from OrcidOauth2TokenDetail where profile.id = :userOrcid and " + "clientDetailsEntity.id = :clientOrcid");
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
    public OrcidOauth2TokenDetail findByAuthenticationKey(String authenticationKey) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where authenticationKey = :authenticationKey",
                OrcidOauth2TokenDetail.class);
        query.setParameter("authenticationKey", authenticationKey);
        return query.getSingleResult();
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByUserName(String userName) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where profile.id = :userName",
                OrcidOauth2TokenDetail.class);
        query.setParameter("userName", userName);
        return query.getResultList();
    }

    @Override
    public List<OrcidOauth2TokenDetail> findByClientId(String clientId) {
        TypedQuery<OrcidOauth2TokenDetail> query = entityManager.createQuery("from " + "OrcidOauth2TokenDetail where clientDetailsEntity.profileEntity.id = :clientId",
                OrcidOauth2TokenDetail.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Override
    public void disableAccessToken(String accessTokenValue) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE where " + "tokenValue = :accessTokenValue");
        query.setParameter("accessTokenValue", accessTokenValue);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the refresh token {0}", accessTokenValue);
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
}
