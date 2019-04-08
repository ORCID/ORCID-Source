package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
    public void removeByRefreshTokenValue(String refreshTokenValue) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = 'CLIENT_REVOKED' where refreshTokenValue = :refreshToken");
        query.setParameter("refreshToken", refreshTokenValue);
        int i = query.executeUpdate();
        if (i == 0) {
            LOGGER.info("Attempted to revoke using refresh token {0} but it was not present in the database", refreshTokenValue);
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
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = 'CLIENT_REVOKED' where tokenValue = :accessTokenValue");
        query.setParameter("accessTokenValue", accessTokenValue);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the refresh token {0}", accessTokenValue);
        }
    }

    @Override
    @Transactional
    public void disableAccessTokenById(Long tokenId, String userOrcid) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = 'USER_REVOKED' where id = :tokenId and profile.id = :userOrcid");
        query.setParameter("tokenId", tokenId);
        query.setParameter("userOrcid", userOrcid);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the token with id {0}", tokenId);
        }
    }
    
    @Override
    public void disableAccessTokenByRefreshToken(String refreshTokenValue) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = 'CLIENT_REVOKED' where refreshTokenValue = :refreshTokenValue");
        query.setParameter("refreshTokenValue", refreshTokenValue);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the refresh token {0}", refreshTokenValue);
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
    public boolean hasToken(String userName) {
    	Query query = entityManager
                .createNativeQuery("select true from oauth2_token_detail where user_orcid=:userName limit 1");
        query.setParameter("userName", userName);
        try {
            query.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }


    /** Disable all tokens with this code/clientID pair.  Should never be more than one.
     * 
     */
    @Override
    public int disableAccessTokenByCodeAndClient(String authorizationCode, String clientId, String reason) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = :reason where clientDetailsId = :clientId and authorizationCode = :authorizationCode");
        query.setParameter("authorizationCode", authorizationCode);
        query.setParameter("clientId", clientId);
        query.setParameter("reason", reason);
        int count = query.executeUpdate();
        return count;
    }
    
    @Override
    @Transactional
    public void disableAccessTokenByUserOrcid(String userOrcid, String reason) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = :reason where profile.id = :userOrcid AND (tokenDisabled IS NULL OR tokenDisabled = FALSE)");        
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("reason", reason);
        query.executeUpdate();        
    }

    @Override
    public void revokeAccessToken(String accessToken) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = 'CLIENT_REVOKED' where tokenValue = :accessTokenValue");
        query.setParameter("accessTokenValue", accessToken);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove the refresh token {0}", accessToken);
        }
    }

    @Override
    public void disableClientAccessTokensByUserOrcid(String orcid, String clientDetailsId) {
        Query query = entityManager.createQuery("update OrcidOauth2TokenDetail set tokenDisabled = TRUE, revocationDate=now(), revokeReason = 'CLIENT_REVOKED' where clientDetailsId = :clientDetailsId and profile.id = :orcid");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("orcid", orcid);
        int count = query.executeUpdate();
        if (count == 0) {
            LOGGER.debug("Cannot remove disable tokens for client/orcid {0}/{1}", clientDetailsId, orcid);
        }
    }
}
