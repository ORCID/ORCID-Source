package org.orcid.frontend.service;

import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.persistence.NoResultException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TrustedPartiesServiceImpl implements TrustedPartiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustedPartiesServiceImpl.class);

    @Resource(name="orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource(name="orcidOauth2TokenDetailDaoReadOnly")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDaoReadOnly;

    @Resource
    private RedisClient redisTokenCacheClient;

    private List<OrcidOauth2TokenDetail> findByClientIdAndUserName(String clientDetailsId, String userOrcid) {
        try {
            List<OrcidOauth2TokenDetail> allTokens = orcidOauth2TokenDetailDaoReadOnly.findByClientIdAndUserName(clientDetailsId, userOrcid);
            if(allTokens != null && !allTokens.isEmpty()) {
                Date now = new Date();
                // Return only active tokens
                return allTokens.stream().filter(token -> (token.getTokenExpiration() != null && token.getTokenExpiration().after(now) && (token.getTokenDisabled() == null || !token.getTokenDisabled()))).collect(Collectors.toList());
            } else {
                return null;
            }
        } catch (NoResultException e) {
            LOGGER.debug("No token found for orcid {}", e, userOrcid);
            return null;
        }
    }

    @Transactional
    public void disableClientAccess(String clientDetailsId, String userOrcid) {
        // As a security measure, remove any user tokens from the cache
        List<OrcidOauth2TokenDetail> userTokens = findByClientIdAndUserName(clientDetailsId, userOrcid);
        if(userTokens != null && !userTokens.isEmpty()) {
            for(OrcidOauth2TokenDetail token : userTokens) {
                try {
                    redisTokenCacheClient.remove(token.getTokenValue());
                } catch(Exception e) {
                    LOGGER.info("Unable to remove token from cache", e);
                }
            }
            // And then disable all user tokens
            orcidOauth2TokenDetailDao.disableClientAccessTokensByUserOrcid(userOrcid, clientDetailsId);
        }
    }
}
