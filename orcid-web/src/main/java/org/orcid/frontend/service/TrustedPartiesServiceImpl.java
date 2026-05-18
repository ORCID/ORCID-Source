package org.orcid.frontend.service;

import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import java.util.List;

public class TrustedPartiesServiceImpl implements TrustedPartiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustedPartiesServiceImpl.class);

    @Resource(name="orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource(name="orcidOauth2TokenDetailDaoReadOnly")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDaoReadOnly;

    @Resource
    private RedisClient redisTokenCacheClient;

    public List<OrcidOauth2TokenDetail> findByUserName(String userName) {
        try {
            return orcidOauth2TokenDetailDaoReadOnly.findByUserName(userName);
        } catch (NoResultException e) {
            LOGGER.debug("No token found for username {}", e, userName);
            return null;
        }
    }

    @Transactional
    public void disableClientAccess(String clientDetailsId, String userOrcid) {
        // As a security measure, remove any user tokens from the cache
        List<OrcidOauth2TokenDetail> userTokens = findByUserName(userOrcid);
        if(userTokens != null && !userTokens.isEmpty()) {
            for(OrcidOauth2TokenDetail token : userTokens) {
                if(clientDetailsId.equals(token.getClientDetailsId())) {
                    try {
                        redisTokenCacheClient.remove(token.getTokenValue());
                    } catch(Exception e) {
                        LOGGER.info("Unable to remove token from cache", e);
                    }
                }
            }
        }
        // And then disable all user tokens
        orcidOauth2TokenDetailDao.disableClientAccessTokensByUserOrcid(userOrcid, clientDetailsId);
    }
}
