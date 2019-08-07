package org.orcid.core.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-oauth2-common-config.xml" })
public class OrcidRefreshTokenTokenGranterTest extends DBUnitTest {

    private static final String CLIENT_ID_1 = "APP-5555555555555555";
    private static final String CLIENT_ID_2 = "APP-5555555555555556";
    private static final String USER_ORCID = "0000-0000-0000-0001";

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private OrcidRefreshTokenTokenGranter refreshTokenTokenGranter;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    private OrcidOauth2TokenDetail createToken(String clientId, String userOrcid, String tokenValue, String refreshTokenValue, Date expirationDate, String scopes) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setDateCreated(new Date());
        token.setLastModified(new Date());
        token.setProfile(new ProfileEntity(userOrcid));
        token.setScope(scopes);
        token.setTokenDisabled(false);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        token.setRefreshTokenValue(refreshTokenValue);
        orcidOauth2TokenDetailService.saveOrUpdate(token);
        return token;
    }

    private OAuth2AccessToken generateRefreshToken(OrcidOauth2TokenDetail tokenDetails, String customClientId, Boolean revokeOld, Long expiresIn, String... scopesParam) {
        Set<String> scopes = null;
        if (scopesParam != null) {
            scopes = new HashSet<String>(Arrays.asList(scopesParam));
        }

        Map<String, String> authorizationParameters = new HashMap<String, String>();
        String scopesString = scopes == null ? null : StringUtils.join(scopes, ' ');
        String clientId = PojoUtil.isEmpty(customClientId) ? tokenDetails.getClientDetailsId() : customClientId;
        String refreshTokenValue = tokenDetails.getRefreshTokenValue();

        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        authorizationParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "true");
        authorizationParameters.put(OrcidOauth2Constants.AUTHORIZATION, tokenDetails.getTokenValue());
        authorizationParameters.put(OrcidOauth2Constants.REFRESH_TOKEN, refreshTokenValue);
        authorizationParameters.put(OAuth2Utils.REDIRECT_URI, tokenDetails.getRedirectUri());

        if (!PojoUtil.isEmpty(scopesString)) {
            authorizationParameters.put(OAuth2Utils.SCOPE, scopesString);
        }

        if (revokeOld != null) {
            authorizationParameters.put(OrcidOauth2Constants.REVOKE_OLD, String.valueOf(revokeOld));
        }

        if (expiresIn != null) {
            authorizationParameters.put(OrcidOauth2Constants.EXPIRES_IN, String.valueOf(expiresIn));
        }

        TokenRequest tokenRequest = new TokenRequest(authorizationParameters, clientId, scopes, OrcidOauth2Constants.REFRESH_TOKEN);
        return refreshTokenTokenGranter.grant(OrcidOauth2Constants.REFRESH_TOKEN, tokenRequest);
    }

    @Test
    public void createRefreshTokenTest() {
        // Create token, create refresh, parent should be disabled, scopes
        // should be equal
        long time = System.currentTimeMillis();
        String scope = "/activities/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = null;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;

        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, scope);
        OAuth2AccessToken refresh = generateRefreshToken(parent, null, revokeOld, expireIn, scope);
        assertNotNull(refresh);

        OrcidOauth2TokenDetail parentToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(parent.getTokenValue());
        assertNotNull(parentToken);
        assertEquals(tokenValue, parentToken.getTokenValue());
        assertTrue(parentToken.getTokenDisabled());
        assertEquals(scope, parentToken.getScope());
        assertNotNull(parentToken.getTokenExpiration());

        OrcidOauth2TokenDetail refreshToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(refresh.getValue());
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getTokenValue());
        assertNotNull(refreshToken.getRefreshTokenValue());
        assertFalse(refreshToken.getTokenDisabled());
        assertEquals(scope, refreshToken.getScope());
        assertNotNull(refreshToken.getTokenExpiration());

        assertEquals(parentToken.getTokenExpiration().getTime(), refreshToken.getTokenExpiration().getTime());
    }

    @Test
    public void createRefreshTokenWithNarrowerScopesTest() {
        // Create token, create refresh with narrower scopes, parent should be
        // disabled, scopes should be narrower
        long time = System.currentTimeMillis();
        String parentScope = "/activities/update";
        String refreshScope = "/orcid-works/create";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;

        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        OAuth2AccessToken refresh = generateRefreshToken(parent, null, revokeOld, expireIn, refreshScope);
        assertNotNull(refresh);

        OrcidOauth2TokenDetail parentToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(parent.getTokenValue());
        assertNotNull(parentToken);
        assertEquals(tokenValue, parentToken.getTokenValue());
        assertTrue(parentToken.getTokenDisabled());
        assertEquals(parentScope, parentToken.getScope());
        assertNotNull(parentToken.getTokenExpiration());

        OrcidOauth2TokenDetail refreshToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(refresh.getValue());
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getTokenValue());
        assertNotNull(refreshToken.getRefreshTokenValue());
        assertFalse(refreshToken.getTokenDisabled());
        assertEquals(refreshScope, refreshToken.getScope());
        assertNotNull(refreshToken.getTokenExpiration());

        assertEquals(parentToken.getTokenExpiration().getTime(), refreshToken.getTokenExpiration().getTime());
    }

    @Test
    public void createRefreshTokenWithoutRevokeParent() {
        // Create token, create refresh without disabling parent token, parent
        // should be enabled, refresh should be enabled
        long time = System.currentTimeMillis();
        String parentScope = "/activities/update /read-limited";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = false;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;

        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        OAuth2AccessToken refresh = generateRefreshToken(parent, null, revokeOld, expireIn);
        assertNotNull(refresh);

        OrcidOauth2TokenDetail parentToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(parent.getTokenValue());
        assertNotNull(parentToken);
        assertEquals(tokenValue, parentToken.getTokenValue());
        assertFalse(parentToken.getTokenDisabled());
        assertNotNull(parentToken.getTokenExpiration());

        OrcidOauth2TokenDetail refreshToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(refresh.getValue());
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getTokenValue());
        assertNotNull(refreshToken.getRefreshTokenValue());
        assertFalse(refreshToken.getTokenDisabled());
        assertNotNull(refreshToken.getTokenExpiration());

        assertEquals(parentToken.getTokenExpiration().getTime(), refreshToken.getTokenExpiration().getTime());

        assertEquals(parentToken.getScope(), refreshToken.getScope());

        Set<String> tokenScopes = OAuth2Utils.parseParameterList(parentToken.getScope());
        Set<String> originalScopes = OAuth2Utils.parseParameterList(parentScope);
        assertEquals(originalScopes, tokenScopes);
    }

    @Test
    public void createRefreshTokenWithoutRevokeParentAndWithNarrowerScopes() {
        // Create token, create refresh with narrower scopes and without
        // disabling parent token, parent should work, refresh should have
        // narrower scopes
        long time = System.currentTimeMillis();
        String parentScope = "/person/read-limited";
        String refreshScope = "/orcid-bio/read-limited";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = false;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;

        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        OAuth2AccessToken refresh = generateRefreshToken(parent, null, revokeOld, expireIn, refreshScope);
        assertNotNull(refresh);

        OrcidOauth2TokenDetail parentToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(parent.getTokenValue());
        assertNotNull(parentToken);
        assertEquals(tokenValue, parentToken.getTokenValue());
        assertFalse(parentToken.getTokenDisabled());
        assertEquals(parentScope, parentToken.getScope());
        assertNotNull(parentToken.getTokenExpiration());

        OrcidOauth2TokenDetail refreshToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(refresh.getValue());
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getTokenValue());
        assertNotNull(refreshToken.getRefreshTokenValue());
        assertFalse(refreshToken.getTokenDisabled());
        assertEquals(refreshScope, refreshToken.getScope());
        assertNotNull(refreshToken.getTokenExpiration());

        assertEquals(parentToken.getTokenExpiration().getTime(), refreshToken.getTokenExpiration().getTime());
    }

    @Test
    public void createRefreshTokenWithExpirationOf10Secs() {
        // Create token, dont revoke parent and set expiration to 10 secs
        long time = System.currentTimeMillis();
        String parentScope = "/person/read-limited";
        String refreshScope = "/orcid-bio/read-limited";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = false;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = 5L;

        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        OAuth2AccessToken refresh = generateRefreshToken(parent, null, revokeOld, expireIn, refreshScope);
        assertNotNull(refresh);

        OrcidOauth2TokenDetail parentToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(parent.getTokenValue());
        assertNotNull(parentToken);
        assertEquals(tokenValue, parentToken.getTokenValue());
        assertFalse(parentToken.getTokenDisabled());
        assertEquals(parentScope, parentToken.getScope());
        assertNotNull(parentToken.getTokenExpiration());

        OrcidOauth2TokenDetail refreshToken = orcidOauth2TokenDetailService.findIgnoringDisabledByTokenValue(refresh.getValue());
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getTokenValue());
        assertNotNull(refreshToken.getRefreshTokenValue());
        assertFalse(refreshToken.getTokenDisabled());
        assertEquals(refreshScope, refreshToken.getScope());
        assertNotNull(refreshToken.getTokenExpiration());

        assertTrue(parentToken.getTokenExpiration().getTime() > refreshToken.getTokenExpiration().getTime());
        // Assert that current time plus 6 secs is greather than refresh token
        // expiration
        assertTrue((time + 6000) > refreshToken.getTokenExpiration().getTime());
    }

    @Test
    public void tryToCreateRefreshTokenWithInvalidScopesTest() {
        // Create token, try to create refresh token with invalid scopes, fail
        long time = System.currentTimeMillis();
        String parentScope = "/person/update";
        String refreshScope = "/orcid-works/read-limited";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        try {
            generateRefreshToken(parent, null, revokeOld, expireIn, refreshScope);        
            fail();
        } catch(InvalidScopeException e) {
            assertTrue(e.getMessage().contains("is not allowed for the parent token"));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void tryToCreateRefreshTokenWithThatExpireAfterParentTokenTest() {
        // Create token, try to create refresh token that expires after parent
        // token, fail
        long time = System.currentTimeMillis();
        String parentScope = "/person/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = time + (15000);
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        try {
            generateRefreshToken(parent, null, revokeOld, expireIn, parentScope);        
            fail();
        } catch(IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Token expiration can't be after"));
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void tryToCreateRefreshTokenWithInvalidClientTest() {
        // Create token for client # 1, try to create a refresh token using
        // client # 2, fail
        long time = System.currentTimeMillis();
        String parentScope = "/person/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        try {
            generateRefreshToken(parent, CLIENT_ID_2, revokeOld, expireIn, parentScope);        
            fail();
        } catch(IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("This token does not belong to the given client"));
        } catch(Exception e) {
            fail();
        }

    }
    
    @Test
    public void tryToRefreshAnExpiredTokenTest() {
        long time = System.currentTimeMillis();
        String parentScope = "/person/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time - 10000);
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        try {
            generateRefreshToken(parent, null, revokeOld, expireIn, parentScope);        
            fail();
        } catch(InvalidTokenException e) {
            assertTrue(e.getMessage().contains("Access token expired:"));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void tryToCreateRefreshTokenWithInvalidRefreshTokenTest() {
        // Create token, try to create refresh token with invalid refresh value,
        // fail
        long time = System.currentTimeMillis();
        String parentScope = "/person/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        try {
            //Change the value we are going to use for the refresh token
            parent.setRefreshTokenValue("invalid-value");
            generateRefreshToken(parent, null, revokeOld, expireIn, parentScope);        
            fail();
        } catch(InvalidTokenException e) {
            assertTrue(e.getMessage().contains("Unable to find refresh token"));
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void tryToCreateRefreshTokenWithInvalidParentTokenValueTest() {
        // Create token, try to create refresh token with invalid parent token
        // value, fail
        long time = System.currentTimeMillis();
        String parentScope = "/person/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = true;
        Date parentTokenExpiration = new Date(time + 10000);
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, parentTokenExpiration, parentScope);
        // Change the value we are going to use for the refresh token
        parent.setTokenValue("invalid-value");
        OAuth2AccessToken refreshedToken = generateRefreshToken(parent, null, revokeOld, expireIn, parentScope);
        // We shouldn't care about the access token, it's not required and
        // shouldn't really be there. If the refresh token and client
        // credentials are good, we can generate the refresh token.
        assertNotNull(refreshedToken);
    } 
       
}
