package org.orcid.frontend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

public class TrustedPartiesServiceImplTest {

    @Mock
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Mock
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDaoReadOnly;

    @Mock
    private RedisClient redisTokenCacheClient;

    @InjectMocks
    private TrustedPartiesServiceImpl trustedPartiesService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDisableClientAccess_WithTokens() {
        String userOrcid = "some-orcid";
        String clientDetailsId = "some-client-id";
        String tokenValue = "some-token-value";
        
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId(clientDetailsId);
        token.setTokenValue(tokenValue);
        token.setTokenExpiration(Date.from(Instant.now().plusSeconds(10)));
        
        List<OrcidOauth2TokenDetail> userTokens = Collections.singletonList(token);
        
        when(orcidOauth2TokenDetailDaoReadOnly.findByClientIdAndUserName(clientDetailsId, userOrcid)).thenReturn(userTokens);
        
        trustedPartiesService.disableClientAccess(clientDetailsId, userOrcid);
        
        // Verify call to Redis
        verify(redisTokenCacheClient).remove(tokenValue);
        
        // Verify call to DAO
        verify(orcidOauth2TokenDetailDao).disableClientAccessTokensByUserOrcid(userOrcid, clientDetailsId);
    }

    @Test
    public void testDisableClientAccess_NoTokens() {
        String userOrcid = "some-orcid";
        String clientDetailsId = "some-client-id";
        
        when(orcidOauth2TokenDetailDaoReadOnly.findByClientIdAndUserName(clientDetailsId, userOrcid)).thenReturn(null);
        
        trustedPartiesService.disableClientAccess(clientDetailsId, userOrcid);
        
        // Verify call to Redis DID NOT happen
        verify(redisTokenCacheClient, never()).remove(anyString());
        
        // Verify call to DAO DOES NOT happen because there are no tokens
        verify(orcidOauth2TokenDetailDao, never()).disableClientAccessTokensByUserOrcid(userOrcid, clientDetailsId);
    }
}
