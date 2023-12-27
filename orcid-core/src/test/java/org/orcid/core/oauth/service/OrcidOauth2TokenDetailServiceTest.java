package org.orcid.core.oauth.service;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class OrcidOauth2TokenDetailServiceTest extends DBUnitTest {
    private static final String CLIENT_ID_1 = "APP-5555555555555555";
    private static final String CLIENT_ID_2 = "APP-5555555555555556";
    private static final String USER_ORCID = "0000-0000-0000-0001";
    private static final String USER_ORCID_2 = "0000-0000-0000-0002";
    private static final String USER_ORCID_3 = "0000-0000-0000-0003";
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource(name="orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Mock
    private RedisClient redisClientMock;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        // Enable the cache
        TargetProxyHelper.injectIntoProxy(orcidOauth2TokenDetailService, "isTokenCacheEnabled", true);
        TargetProxyHelper.injectIntoProxy(orcidOauth2TokenDetailService, "redisClient", redisClientMock);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/SubjectEntityData.xml"));
    }  
    
    @Test
    public void dontGetExpiredTokensTest() {
        //Token # 1: expired
        Long token1Id = createToken(CLIENT_ID_1, "expired-1", USER_ORCID, new Date(System.currentTimeMillis() - 100000), "/read-limited", false).getId();
        //Token # 2: /activities/update
        Long token2Id = createToken(CLIENT_ID_1, "active-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", false).getId();
        //Token # 3: disabled
        Long token3Id = createToken(CLIENT_ID_1, "disabled-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", true).getId();
        //Token # 4: /read-limited
        Long token4Id = createToken(CLIENT_ID_1, "active-2", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false).getId();
        //Fetch all active tokens
        List<OrcidOauth2TokenDetail> activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertNotNull(activeTokens);
        assertEquals(2, activeTokens.size());
        assertThat(activeTokens.get(0).getScope(), anyOf(is("/activities/update"), is("/read-limited")));
        assertThat(activeTokens.get(1).getScope(), anyOf(is("/activities/update"), is("/read-limited")));
        
        //Find the id of the token with scope '/activities/update' and disable that token
        Long tokenToDisableId = null;
        for(OrcidOauth2TokenDetail token : activeTokens) {
            if("/activities/update".equals(token.getScope())) {
                tokenToDisableId = token.getId();
                break;
            }
        }
        
        assertNotNull(tokenToDisableId);
        //Disable that access token
        orcidOauth2TokenDetailService.disableAccessToken(tokenToDisableId, USER_ORCID);
        //Fetch the active tokens again, it should contain just one
        activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertNotNull(activeTokens);
        assertEquals(1, activeTokens.size());
        assertEquals("/read-limited", activeTokens.get(0).getScope());
        assertEquals("active-2", activeTokens.get(0).getTokenValue());
        
        orcidOauth2TokenDetailDao.remove(token1Id);
        orcidOauth2TokenDetailDao.remove(token2Id);
        orcidOauth2TokenDetailDao.remove(token3Id);
        orcidOauth2TokenDetailDao.remove(token4Id);        
    }
    
    @Test    
    public void removeAllTokensWithSameScopesTest() {
        //We will test deleting this token
        Long token1Id = createToken(CLIENT_ID_1, "token-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update /read-limited", false).getId(); //Delete
        //Should not delete this        
        Long token2Id = createToken(CLIENT_ID_1, "token-2", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", false).getId();
        //Should not delete this
        Long token3Id = createToken(CLIENT_ID_1, "token-3", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false).getId();
        //Should delete this one since it have the same scopes but in different order
        Long token4Id = createToken(CLIENT_ID_1, "token-4", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited /activities/update", false).getId(); //Delete
        //Should not delete this since it have one scope more /orcid-profile/read-limited
        Long token5Id = createToken(CLIENT_ID_1, "token-5", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/orcid-profile/read-limited /activities/update /read-limited", false).getId();
        //Should not delete this since it have one scope more /activities/read-limited
        Long token6Id = createToken(CLIENT_ID_1, "token-6", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited /activities/update /activities/read-limited", false).getId();
        //Should not delete this
        Long token7Id = createToken(CLIENT_ID_1, "token-7", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/person/read-limited", false).getId();
        //Should not delete this since it have several more scopes
        Long token8Id = createToken(CLIENT_ID_1, "token-8", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/funding/read-limited /read-limited /activities/read-limited /orcid-works/create /affiliations/update /activities/update", false).getId();
        //Should remove this since it contains the same scopes
        Long token9Id = createToken(CLIENT_ID_1, "token-9", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update /read-limited", false).getId(); //Delete
        
        
        List<OrcidOauth2TokenDetail> activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertNotNull(activeTokens);
        assertEquals(9, activeTokens.size());
        
        orcidOauth2TokenDetailService.disableAccessToken(token1Id, USER_ORCID);
        activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertEquals(6, activeTokens.size());
        
        for(OrcidOauth2TokenDetail token : activeTokens) {
            assertThat(token.getId(), allOf(not(token1Id), not(token4Id), not(token9Id)));
            assertThat(token.getId(), anyOf(is(token2Id), is(token3Id), is(token5Id), is(token6Id), is(token7Id), is(token8Id)));
        }
        
        orcidOauth2TokenDetailDao.remove(token1Id);
        orcidOauth2TokenDetailDao.remove(token2Id);
        orcidOauth2TokenDetailDao.remove(token3Id);
        orcidOauth2TokenDetailDao.remove(token4Id);
        orcidOauth2TokenDetailDao.remove(token5Id);
        orcidOauth2TokenDetailDao.remove(token6Id);
        orcidOauth2TokenDetailDao.remove(token7Id);
        orcidOauth2TokenDetailDao.remove(token8Id);
        orcidOauth2TokenDetailDao.remove(token9Id);
    }
    
    @Test
    public void dontRemoveOtherClientScopesTest() {
        Long seed = System.currentTimeMillis();
        Long token1Id = createToken(CLIENT_ID_1, "token-1-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false).getId(); //Delete
        Long token2Id = createToken(CLIENT_ID_1, "token-2-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", false).getId(); 
        Long token3Id = createToken(CLIENT_ID_1, "token-3-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update /read-limited", false).getId(); 
        Long token4Id = createToken(CLIENT_ID_1, "token-4-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false).getId(); //Delete
        
        Long token5Id = createToken(CLIENT_ID_2, "token-5-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false).getId();
        Long token6Id = createToken(CLIENT_ID_2, "token-6-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", false).getId();
        Long token7Id = createToken(CLIENT_ID_2, "token-7-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update /read-limited", false).getId();
        Long token8Id = createToken(CLIENT_ID_2, "token-8-" + seed, USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false).getId();
        
        List<OrcidOauth2TokenDetail> activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertNotNull(activeTokens);
        assertEquals(8, activeTokens.size());
        
        orcidOauth2TokenDetailService.disableAccessToken(token1Id, USER_ORCID);
        
        activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertEquals(6, activeTokens.size());
        
        for(OrcidOauth2TokenDetail token : activeTokens) {
            assertThat(token.getId(), allOf(not(token1Id), not(token4Id)));
            assertThat(token.getId(), anyOf(is(token2Id), is(token3Id), is(token5Id), is(token6Id), is(token7Id), is(token8Id)));
        }
    }
    
    @Test
    public void disableAccessTokenByUserOrcidTest() {        
        Date date = new Date(System.currentTimeMillis() + 100000);
        createToken(CLIENT_ID_1, "active-1-user-1", USER_ORCID_2, date, "/activities/update", false);
        createToken(CLIENT_ID_1, "active-2-user-1", USER_ORCID_2, date, "/activities/update", false);
        createToken(CLIENT_ID_1, "active-3-user-1", USER_ORCID_2, date, "/activities/update", false);
        createToken(CLIENT_ID_1, "active-1-user-2", USER_ORCID_3, date, "/activities/update", false);
        createToken(CLIENT_ID_1, "active-2-user-2", USER_ORCID_3, date, "/activities/update", false);
        createToken(CLIENT_ID_1, "active-3-user-2", USER_ORCID_3, date, "/activities/update", false);
        
        List<OrcidOauth2TokenDetail> tokensUser1 = orcidOauth2TokenDetailService.findByClientIdAndUserName(CLIENT_ID_1, USER_ORCID_2);
        assertEquals(3, tokensUser1.size());
        for(OrcidOauth2TokenDetail token : tokensUser1) {
            assertFalse(token.getTokenDisabled());
        }
        
        List<OrcidOauth2TokenDetail> tokensUser2 = orcidOauth2TokenDetailService.findByClientIdAndUserName(CLIENT_ID_1, USER_ORCID_3);
        assertEquals(3, tokensUser2.size());
        for(OrcidOauth2TokenDetail token : tokensUser2) {
            assertFalse(token.getTokenDisabled());
        }
        
        orcidOauth2TokenDetailService.disableAccessTokenByUserOrcid(USER_ORCID_2, RevokeReason.RECORD_DEACTIVATED);
        
        tokensUser1 = orcidOauth2TokenDetailService.findByClientIdAndUserName(CLIENT_ID_1, USER_ORCID_2);
        assertEquals(3, tokensUser1.size());
        for(OrcidOauth2TokenDetail token : tokensUser1) {
            // Tokens for this user MUST be disabled at this point
            assertTrue(token.getTokenDisabled());
        }
        
        tokensUser2 = orcidOauth2TokenDetailService.findByClientIdAndUserName(CLIENT_ID_1, USER_ORCID_3);
        assertEquals(3, tokensUser2.size());
        for(OrcidOauth2TokenDetail token : tokensUser2) {
            assertFalse(token.getTokenDisabled());
        }
    }
    
    @Test
    public void disableAccessTokenByCodeAndClientTest() {
        Date date = new Date(System.currentTimeMillis() + 100000);
        String authCode = "auth-code-1";
        OrcidOauth2TokenDetail dbt1 = createToken(CLIENT_ID_1, "token-1", USER_ORCID, date, "/activities/update", false, authCode);
        OrcidOauth2TokenDetail dbt2 = createToken(CLIENT_ID_1, "token-2", USER_ORCID, date, "/activities/update", false, authCode);
        OrcidOauth2TokenDetail dbt3 = createToken(CLIENT_ID_1, "token-3", USER_ORCID, date, "/activities/update", false, authCode);
        OrcidOauth2TokenDetail dbt4 = createToken(CLIENT_ID_1, "token-4", USER_ORCID_2, date, "/activities/update", false, authCode);
        OrcidOauth2TokenDetail dbt5 = createToken(CLIENT_ID_2, "token-5", USER_ORCID_3, date, "/activities/update", false, authCode);
        OrcidOauth2TokenDetail dbt6 = createToken(CLIENT_ID_2, "token-6", USER_ORCID, date, "/activities/update", false, authCode);
        
        // Disable tokens with authCode and CLIENT_ID_1
        orcidOauth2TokenDetailService.disableAccessTokenByCodeAndClient(authCode, CLIENT_ID_1, RevokeReason.AUTH_CODE_REUSED);
        
        verify(redisClientMock, times(1)).remove("token-1");
        verify(redisClientMock, times(1)).remove("token-2");
        verify(redisClientMock, times(1)).remove("token-3");
        verify(redisClientMock, times(1)).remove("token-4");
        
        // Tokens 1, 2, 3 and 4 should be revoked
        OrcidOauth2TokenDetail t1 = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue("token-1");
        assertNotNull(t1.getRevocationDate());
        assertEquals(RevokeReason.AUTH_CODE_REUSED.toString(), t1.getRevokeReason());
        OrcidOauth2TokenDetail t2 = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue("token-2");
        assertNotNull(t2.getRevocationDate());
        assertEquals(RevokeReason.AUTH_CODE_REUSED.toString(), t2.getRevokeReason());
        OrcidOauth2TokenDetail t3 = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue("token-3");
        assertNotNull(t3.getRevocationDate());
        assertEquals(RevokeReason.AUTH_CODE_REUSED.toString(), t3.getRevokeReason());
        // This case is never possible, the client used the same auth code to create a token on other user
        OrcidOauth2TokenDetail t4 = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue("token-4");
        assertNotNull(t4.getRevocationDate());
        assertEquals(RevokeReason.AUTH_CODE_REUSED.toString(), t4.getRevokeReason());        
        
        // Tokens 5 and 6 should be active
        OrcidOauth2TokenDetail t5 = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue("token-5");
        assertNull(t5.getRevocationDate());
        assertNull(t5.getRevokeReason());
        OrcidOauth2TokenDetail t6 = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue("token-6");
        assertNull(t6.getRevocationDate());
        assertNull(t6.getRevokeReason());
        
        // Cleanup
        orcidOauth2TokenDetailDao.remove(dbt1.getId());
        orcidOauth2TokenDetailDao.remove(dbt2.getId());
        orcidOauth2TokenDetailDao.remove(dbt3.getId());
        orcidOauth2TokenDetailDao.remove(dbt4.getId());
        orcidOauth2TokenDetailDao.remove(dbt5.getId());
        orcidOauth2TokenDetailDao.remove(dbt6.getId());
    }
    
    @Test    
    public void updateScopesTest() {
        String tokenValue = "TOKEN123";
        String scopes1 = "/person/read-limited /activities/update /read-limited";
        String scopes2 = "/person/read-limited /read-limited";        
        String scopes3 = "/read-limited";
        //We will test deleting this token
        Long token1Id = createToken(CLIENT_ID_1, tokenValue, USER_ORCID, new Date(System.currentTimeMillis() + 100000), scopes1, false).getId(); //Delete
        OrcidOauth2TokenDetail token1 = orcidOauth2TokenDetailDao.find(token1Id);
        assertNotNull(token1);
        assertEquals(CLIENT_ID_1, token1.getClientDetailsId());
        assertEquals(tokenValue, token1.getTokenValue());
        assertEquals(USER_ORCID, token1.getOrcid());
        assertEquals(scopes1, token1.getScope());
        assertEquals("bearer", token1.getTokenType());
        
        orcidOauth2TokenDetailDao.updateScopes(tokenValue, scopes2);
        OrcidOauth2TokenDetail token2 = orcidOauth2TokenDetailDao.findByTokenValue(tokenValue);
        assertNotNull(token2);
        assertEquals(CLIENT_ID_1, token2.getClientDetailsId());
        assertEquals(tokenValue, token2.getTokenValue());
        assertEquals(USER_ORCID, token2.getOrcid());
        assertEquals(scopes2, token2.getScope());
        assertEquals("bearer", token2.getTokenType());
                
        orcidOauth2TokenDetailDao.updateScopes(tokenValue, scopes3);
        OrcidOauth2TokenDetail token3 = orcidOauth2TokenDetailDao.findByTokenValue(tokenValue);
        assertNotNull(token3);
        assertEquals(CLIENT_ID_1, token3.getClientDetailsId());
        assertEquals(tokenValue, token3.getTokenValue());
        assertEquals(USER_ORCID, token3.getOrcid());
        assertEquals(scopes3, token3.getScope());
        assertEquals("bearer", token3.getTokenType()); 
        
        orcidOauth2TokenDetailDao.remove(token1Id);
        
        try {
            orcidOauth2TokenDetailDao.findByTokenValue(tokenValue);
            fail();
        } catch(NoResultException nre) {
            //Expected behavior
        }
    }
    
    private OrcidOauth2TokenDetail createToken(String clientId, String tokenValue, String userOrcid, Date expirationDate, String scopes, boolean disabled) {
        return createToken(clientId, tokenValue, userOrcid, expirationDate, scopes, disabled, null);
    }
    
    private OrcidOauth2TokenDetail createToken(String clientId, String tokenValue, String userOrcid, Date expirationDate, String scopes, boolean disabled, String authCode) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setOrcid(userOrcid);
        token.setScope(scopes);
        token.setTokenDisabled(disabled);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        token.setAuthorizationCode(authCode);
        orcidOauth2TokenDetailDao.persist(token);
        return token;
    }
        
}
