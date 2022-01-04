package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrcidOauth2TokenDetailDaoTest extends DBUnitTest {

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    private final Date expirationDate;
    
    public OrcidOauth2TokenDetailDaoTest() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);        
        expirationDate = formatter.parse("2030-01-01");        
    }
        
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml"));
    }

    @Test
    public void findByTokenValueTest() throws ParseException {
        // Verify existing non disabled token works
        OrcidOauth2TokenDetail token = orcidOauth2TokenDetailDao.findByTokenValue("00000000-0000-0000-0000-00000000000");
        validateActiveToken(token);
        
        // Verify existing disabled token works
        OrcidOauth2TokenDetail disabledToken = orcidOauth2TokenDetailDao.findByTokenValue("00000000-0000-0000-0000-00000000001");
        validateDisabledToken(disabledToken);
        
        // Verify non existing token throws NoResultException
        try {
            orcidOauth2TokenDetailDao.findByTokenValue("0");
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void findNonDisabledByTokenValueTest() throws ParseException {
        // Verify existing non disabled token works
        OrcidOauth2TokenDetail token = orcidOauth2TokenDetailDao.findNonDisabledByTokenValue("00000000-0000-0000-0000-00000000000");
        validateActiveToken(token);
        
        // Verify disable token throws NoResultException
        try {
            orcidOauth2TokenDetailDao.findNonDisabledByTokenValue("00000000-0000-0000-0000-00000000001");
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
        
        // Verify non existing token throws NoResultException
        try {
            orcidOauth2TokenDetailDao.findNonDisabledByTokenValue("0");
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void findByRefreshTokenValueTest() throws ParseException {
        // Verify existing non disabled token works
        OrcidOauth2TokenDetail token = orcidOauth2TokenDetailDao.findByRefreshTokenValue("REFRESH-0000-0000-0000-00000000000");
        validateActiveToken(token);
        
        // Verify existing disabled token works
        OrcidOauth2TokenDetail disabledToken = orcidOauth2TokenDetailDao.findByRefreshTokenValue("REFRESH-0000-0000-0000-00000000001");
        validateDisabledToken(disabledToken);        
        
        // Verify non existing token throws NoResultException
        try {
            orcidOauth2TokenDetailDao.findByRefreshTokenValue("0");
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void findByAuthenticationKeyTest() throws ParseException {
        // Verify existing non disabled token works
        List<OrcidOauth2TokenDetail> token = orcidOauth2TokenDetailDao.findByAuthenticationKey("097843f6f740d94d5825f3684e0d4c7b");
        assertEquals(1, token.size());
        validateDisabledToken(token.get(0));
        
        // Verify existing disabled token works
        List<OrcidOauth2TokenDetail> disabledToken = orcidOauth2TokenDetailDao.findByAuthenticationKey("097843f6f740d94d5825f3684e0d4c6e");
        assertEquals(1, disabledToken.size());
        validateActiveToken(disabledToken.get(0));        
        
        List<OrcidOauth2TokenDetail> mustBeEmpty = orcidOauth2TokenDetailDao.findByAuthenticationKey("0");
        assertTrue(mustBeEmpty.isEmpty());
    }

    @Test
    public void findByUserNameTest() throws ParseException {
        // Only active tokens should be returned
        List<OrcidOauth2TokenDetail> token = orcidOauth2TokenDetailDao.findByUserName("0000-0000-0000-0001");
        assertEquals(1, token.size());
        validateActiveToken(token.get(0));
        
        List<OrcidOauth2TokenDetail> mustBeEmpty = orcidOauth2TokenDetailDao.findByUserName("0000-0000-0000-0002");
        assertTrue(mustBeEmpty.isEmpty());
    }

    @Test
    public void findByClientIdTest() throws ParseException {
        List<OrcidOauth2TokenDetail> token = orcidOauth2TokenDetailDao.findByClientId("APP-5555555555555555");
        assertEquals(2, token.size());
        validateActiveToken(token.get(0));        
        validateDisabledToken(token.get(1));
        
        List<OrcidOauth2TokenDetail> mustBeEmpty = orcidOauth2TokenDetailDao.findByClientId("APP-5555555555555567");
        assertTrue(mustBeEmpty.isEmpty());
    }

    @Test
    public void findByClientIdAndUserNameTest() throws ParseException {
        List<OrcidOauth2TokenDetail> token = orcidOauth2TokenDetailDao.findByClientIdAndUserName("APP-5555555555555555", "0000-0000-0000-0001");
        assertEquals(2, token.size());
        validateActiveToken(token.get(0));        
        validateDisabledToken(token.get(1));
        
        List<OrcidOauth2TokenDetail> mustBeEmpty = orcidOauth2TokenDetailDao.findByClientIdAndUserName("APP-555555555555555", "0000-0000-0000-0002");
        assertTrue(mustBeEmpty.isEmpty());
    }

    @Test
    public void findAvailableScopesByUserAndClientIdTest() {
        List<String> scopes = orcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId("APP-5555555555555555", "0000-0000-0000-0001");
        assertEquals(1, scopes.size());
        assertEquals("/read-limited", scopes.get(0));
        
        List<String> mustBeEmpty = orcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId("APP-555555555555555", "0000-0000-0000-0002");
        assertTrue(mustBeEmpty.isEmpty());
    }

    @Test
    public void hasTokenTest() {
        assertTrue(orcidOauth2TokenDetailDao.hasToken("0000-0000-0000-0001"));
        assertFalse(orcidOauth2TokenDetailDao.hasToken("0000-0000-0000-0002"));
    }

    @Test
    public void disableAccessTokenTest() {
        OrcidOauth2TokenDetail token = createToken("token-1");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.disableAccessToken(token.getTokenValue());
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("CLIENT_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }
    
    @Test
    public void disableAccessTokenByUserOrcidTest() {
        OrcidOauth2TokenDetail token = createToken("token-2");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.disableAccessTokenByUserOrcid(token.getProfile().getId(), "USER_REVOKED");
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("USER_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }
    
    @Test
    public void disableAccessTokenByCodeAndClientTest() {
        OrcidOauth2TokenDetail token = createToken("token-3");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.disableAccessTokenByCodeAndClient(token.getAuthorizationCode(), "APP-5555555555555558", "CLIENT_REVOKED");
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("CLIENT_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }

    @Test
    public void disableAccessTokenByIdTest() {
        OrcidOauth2TokenDetail token = createToken("token-4");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.disableAccessTokenById(token.getId(), token.getProfile().getId());
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("USER_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }

    @Test
    public void disableAccessTokenByRefreshTokenTest() {
        OrcidOauth2TokenDetail token = createToken("token-5");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.disableAccessTokenByRefreshToken(token.getRefreshTokenValue());
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("CLIENT_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }

    @Test
    public void disableClientAccessTokensByUserOrcidTest() {
        OrcidOauth2TokenDetail token = createToken("token-6");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.disableClientAccessTokensByUserOrcid(token.getProfile().getId(), token.getClientDetailsId());
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("USER_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }

    @Test
    public void revokeAccessTokenTest() {
        OrcidOauth2TokenDetail token = createToken("token-7");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.revokeAccessToken(token.getTokenValue());
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("CLIENT_REVOKED", token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
    }
    
    @Test
    public void updateScopesTest() {
        OrcidOauth2TokenDetail token = createToken("token-8");
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited", token.getScope());
        
        orcidOauth2TokenDetailDao.updateScopes(token.getTokenValue(), "/read-limited /activities/update");
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertNotNull(token.getId());
        assertNull(token.getTokenDisabled());
        assertNull(token.getRevocationDate());
        assertNull(token.getRevokeReason());
        assertEquals("/read-limited /activities/update", token.getScope());        
    }
    
    @Test
    public void tokenRevokedByMemberMustNotOverwriteRevokeReasonIfRevokedByUser() {
        OrcidOauth2TokenDetail token = createToken("token-9");
        assertNotNull(token.getId());
        
        // Revoke the token by a client
        orcidOauth2TokenDetailDao.revokeAccessToken(token.getTokenValue());
        
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        assertEquals("CLIENT_REVOKED", token.getRevokeReason());
        
        Date initialRevokationDate = token.getRevocationDate();
        
        // Revoke it again by user
        orcidOauth2TokenDetailDao.disableClientAccessTokensByUserOrcid(token.getProfile().getId(), token.getClientDetailsId());
        token = orcidOauth2TokenDetailDao.find(token.getId());
        assertTrue(token.getTokenDisabled());
        assertNotNull(token.getRevocationDate());
        // Must be CLIENT_REVOKED
        assertEquals("CLIENT_REVOKED", token.getRevokeReason());
        assertEquals(initialRevokationDate, token.getRevocationDate());            
    }
    
    private void validateDisabledToken(OrcidOauth2TokenDetail token) throws ParseException {
        assertNotNull(token);
        assertEquals(Long.valueOf(7), token.getId());        
        assertEquals("00000000-0000-0000-0000-00000000001", token.getTokenValue());
        assertEquals("bearer", token.getTokenType());
        assertEquals("097843f6f740d94d5825f3684e0d4c7b", token.getAuthenticationKey());
        assertEquals("abcdef", token.getAuthorizationCode());
        assertEquals("APP-5555555555555555", token.getClientDetailsId());
        assertEquals("0000-0000-0000-0001", token.getProfile().getId());
        assertEquals("http://www.google.com/", token.getRedirectUri());
        assertEquals("/activities-update", token.getScope());
        assertEquals(expirationDate, token.getTokenExpiration());        
        assertEquals("AUTH_CODE_REUSED", token.getRevokeReason());
        assertTrue(token.getTokenDisabled());
    }
    
    private void validateActiveToken(OrcidOauth2TokenDetail token) throws ParseException {
        assertNotNull(token);
        assertEquals(Long.valueOf(6), token.getId());        
        assertEquals("00000000-0000-0000-0000-00000000000", token.getTokenValue());
        assertEquals("bearer", token.getTokenType());
        assertEquals("097843f6f740d94d5825f3684e0d4c6e", token.getAuthenticationKey());
        assertEquals("uvwxy", token.getAuthorizationCode());
        assertEquals("APP-5555555555555555", token.getClientDetailsId());
        assertEquals("0000-0000-0000-0001", token.getProfile().getId());
        assertEquals("http://www.google.com/", token.getRedirectUri());
        assertEquals("/read-limited", token.getScope());
        assertEquals(expirationDate, token.getTokenExpiration());                       
    }
    
    private OrcidOauth2TokenDetail createToken(String tokenValue) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setTokenValue(tokenValue);
        token.setRefreshTokenValue("REFRESH-" + tokenValue);
        token.setAuthenticationKey("authentication-key-" + tokenValue);
        token.setAuthorizationCode("authorization-code-" + tokenValue);
        token.setClientDetailsId("APP-5555555555555558");
        token.setProfile(new ProfileEntity("0000-0000-0000-0007"));
        token.setPersistent(true);
        token.setRedirectUri("http://orcid.org/" + tokenValue);
        token.setRefreshTokenExpiration(expirationDate);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setScope("/read-limited");
        orcidOauth2TokenDetailDao.persist(token);
        return token;
    }
}
