package org.orcid.core.oauth.service;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidOauth2TokenDetailServiceTest extends DBUnitTest {
    private static final String CLIENT_ID_1 = "APP-5555555555555555";
    private static final String CLIENT_ID_2 = "APP-5555555555555556";
    private static final String USER_ORCID = "0000-0000-0000-0001";
    private static final String USER_ORCID_2 = "0000-0000-0000-0002";
    private static final String USER_ORCID_3 = "0000-0000-0000-0003";
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/SubjectEntityData.xml"));
    }  
    
    @Test
    @Transactional
    @Rollback
    public void dontGetExpiredTokensTest() {
        //Token # 1: expired
        createToken(CLIENT_ID_1, "expired-1", USER_ORCID, new Date(System.currentTimeMillis() - 1000), "/read-limited", false);
        //Token # 2: /activities/update
        createToken(CLIENT_ID_1, "active-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", false);
        //Token # 3: disabled
        createToken(CLIENT_ID_1, "disabled-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", true);
        //Token # 4: /read-limited
        createToken(CLIENT_ID_1, "active-2", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false);
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
    }
    
    @Test
    @Transactional
    @Rollback    
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
    
    private OrcidOauth2TokenDetail createToken(String clientId, String tokenValue, String userOrcid, Date expirationDate, String scopes, boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setDateCreated(new Date());
        token.setLastModified(new Date());
        token.setProfile(new ProfileEntity(userOrcid));
        token.setScope(scopes);
        token.setTokenDisabled(disabled);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        orcidOauth2TokenDetailService.saveOrUpdate(token);
        return token;
    }
}
