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
package org.orcid.core.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
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
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/SubjectEntityData.xml",
                "/data/SecurityQuestionEntityData.xml"));
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

    private OAuth2AccessToken generateRefreshToken(OrcidOauth2TokenDetail tokenDetails, Boolean revokeOld, Long expiresIn, String... scopesParam) {
        Set<String> scopes = null;
        if(scopesParam != null) {
            scopes = new HashSet<String>(Arrays.asList(scopesParam));   
        }

        Map<String, String> authorizationParameters = new HashMap<String, String>();
        String scopesString = scopes == null ? null : StringUtils.join(scopes, ' ');
        String clientId = tokenDetails.getClientDetailsId();
        String refreshTokenValue = tokenDetails.getRefreshTokenValue();

        
        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        authorizationParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "true");
        authorizationParameters.put(OrcidOauth2Constants.AUTHORIZATION, tokenDetails.getTokenValue());
        authorizationParameters.put(OrcidOauth2Constants.REFRESH_TOKEN, refreshTokenValue);
        authorizationParameters.put(OAuth2Utils.REDIRECT_URI, tokenDetails.getRedirectUri());

        if(!PojoUtil.isEmpty(scopesString)) {
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
        // Create token, create refresh, parent should be disabled, scopes should
        // be equal
        long time = System.currentTimeMillis();
        String scope = "/activities/update";
        String tokenValue = "parent-token-" + time;
        String refreshTokenValue = "refresh-token-" + time;
        Boolean revokeOld = null;
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, new Date(time + 10000), scope);
        OAuth2AccessToken refresh = generateRefreshToken(parent, revokeOld, expireIn, scope);
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
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, new Date(time + 10000), parentScope);        
        OAuth2AccessToken refresh = generateRefreshToken(parent, revokeOld, expireIn, refreshScope);
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
        Long expireIn = null;
        
        OrcidOauth2TokenDetail parent = createToken(CLIENT_ID_1, USER_ORCID, tokenValue, refreshTokenValue, new Date(time + 10000), parentScope);        
        OAuth2AccessToken refresh = generateRefreshToken(parent, revokeOld, expireIn);
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

    // 4: Create token, create refresh with narrower scopes and without
    // disabling parent token, parent should work, refresh should have narrower
    // scopes
    // 5: Create token, try to create refresh token with invalid scopes, fail
    // 6: Create token, try to create refresh token that expires after parent
    // token, fail
    // 7: Create token for client # 1, try to create a refresh token using
    // client # 2, fail
    // 8: Create token, try to create refresh token with invalid refresh value,
    // fail
    // 9: Create token, try to create refresh token with invalid parent token
    // value, fail
}
