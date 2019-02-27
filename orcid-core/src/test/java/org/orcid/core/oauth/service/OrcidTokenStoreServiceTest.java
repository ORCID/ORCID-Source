package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 20/04/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidTokenStoreServiceTest extends DBUnitTest {

    @Resource(name = "orcidTokenStore")
    private TokenStore orcidTokenStoreService;    
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml"));
    }

    @Test 
    @Transactional
    public void testReadAuthentication() throws Exception {
        OAuth2Authentication oAuth2Authentication = orcidTokenStoreService.readAuthentication("persistent-token-2");
        assertNotNull(oAuth2Authentication);
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        assertNotNull(oAuth2Request);
        Object principal = oAuth2Authentication.getPrincipal();
        assertNotNull(principal);
        assertTrue(!oAuth2Authentication.isClientOnly());
    }

    @Test
    @Transactional
    public void testStoreAccessToken() throws Exception {
        String clientId = "4444-4444-4444-4441";
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("some-long-oauth2-token-value-9");
        
        ExpiringOAuth2RefreshToken refreshToken = new DefaultExpiringOAuth2RefreshToken("some-long-oauth2-refresh-value-9", new Date());
        token.setRefreshToken(refreshToken);
        token.setScope(new HashSet<String>(Arrays.asList("/orcid-bio/read", "/orcid-works/read")));
        token.setTokenType("bearer");
        token.setExpiration(new Date());
        token.setAdditionalInformation(new HashMap<String, Object>());
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("state", "read");
        parameters.put("scope", "/orcid-profile/write");
        parameters.put("redirect_uri", "http://www.google.com/");
        parameters.put("response_type", "bearer");
        OAuth2Request request = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
        
        ProfileEntity profileEntity = profileEntityManager.findByOrcid("4444-4444-4444-4444");
        OrcidOauth2UserAuthentication userAuthentication = new OrcidOauth2UserAuthentication(profileEntity, true);

        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);

        orcidTokenStoreService.storeAccessToken(token, authentication);
        OAuth2AccessToken oAuth2AccessToken = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-9");
        assertNotNull(oAuth2AccessToken);
    }

    @Test
    public void testReadAccessToken() throws Exception {
        OAuth2AccessToken oAuth2AccessToken = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-1");
        assertNotNull(oAuth2AccessToken);
    }

    @Test
    @Transactional    
    public void testRemoveAccessToken() throws Exception {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("code3");
        orcidTokenStoreService.removeAccessToken(accessToken);
        OAuth2AccessToken oAuth2AccessToken = orcidTokenStoreService.readAccessToken("code3");
        assertNull(oAuth2AccessToken);
    }

    @Test
    @Transactional
    public void testReadAuthenticationForRefreshToken() throws Exception {
        OAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken("some-long-oauth2-refresh-value-1");
        OAuth2Authentication oAuth2Authentication = orcidTokenStoreService.readAuthenticationForRefreshToken(refreshToken);
        assertNotNull(oAuth2Authentication);
    }

    @Test
    @Transactional
    public void testRemoveByRefreshToken() throws Exception {
        OAuth2AccessToken token = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-3");       
        assertNotNull(token);
        orcidTokenStoreService.removeRefreshToken(token.getRefreshToken());
        token = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-3");
        assertNull(token);
    }

    @Test
    @Transactional
    public void testRemoveAccessTokenUsingRefreshToken() throws Exception {
        OAuth2AccessToken token = orcidTokenStoreService.readAccessToken("persistent-token-1");
        orcidTokenStoreService.removeAccessTokenUsingRefreshToken(token.getRefreshToken());
        token = orcidTokenStoreService.readAccessToken("persistent-token-1");
        assertNull(token);
    }

    @Test
    @Transactional
    public void testGetAccessToken() throws Exception {
        String clientId = "4444-4444-4444-4441";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("state", "read");
        parameters.put("scope", "/orcid-profile/write");
        parameters.put("redirect_uri", "http://www.google.com/");
        parameters.put("response_type", "bearer");
        OAuth2Request request = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());

        ProfileEntity profileEntity = profileEntityManager.findByOrcid("4444-4444-4444-4444");
        OrcidOauth2UserAuthentication userAuthentication = new OrcidOauth2UserAuthentication(profileEntity, true);

        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("4444-4444-4444-4441");
        token.setExpiration(new Date());
        token.setScope(OAuth2Utils.parseParameterList("/orcid-profile/read,/orcid-profile/write"));
        token.setTokenType("bearer");
        token.setRefreshToken(new DefaultExpiringOAuth2RefreshToken("a-refresh-token", new Date()));
        token.setAdditionalInformation(new HashMap<String, Object>());
        
        orcidTokenStoreService.storeAccessToken(token, authentication);

        OAuth2AccessToken accessToken = orcidTokenStoreService.getAccessToken(authentication);
        assertNotNull(accessToken);
    }

    @Test
    public void testFindTokensByUserName() throws Exception {
        Collection<OAuth2AccessToken> tokensByUserName = orcidTokenStoreService.findTokensByClientIdAndUserName("4444-4444-4444-4441", "4444-4444-4444-4441");
        assertNotNull(tokensByUserName);
        assertEquals(1, tokensByUserName.size());
    }

    @Test
    public void testFindTokensByClientId() throws Exception {
        Collection<OAuth2AccessToken> tokensByClientId = orcidTokenStoreService.findTokensByClientId("4444-4444-4444-4441");
        assertNotNull(tokensByClientId);
        assertEquals(1, tokensByClientId.size());
    }
    
    @Test
    public void testDisabledTokensAreNotReturnedWhenLookingByClientAndUserName() {
        String clientId = "4444-4444-4444-4441";
        String userId = "0000-0000-0000-0001";
        OrcidOauth2TokenDetail token1 = createAccessToken("enabled-1", "/read-limited", clientId, userId, false);
        assertNotNull(token1);
        assertNotNull(token1.getId());
        
        OrcidOauth2TokenDetail token2 = createAccessToken("enabled-2", "/read-limited", clientId, userId, false);
        assertNotNull(token2);
        assertNotNull(token2.getId());
        
        OrcidOauth2TokenDetail token3 = createAccessToken("enabled-3", "/read-limited", clientId, userId, false);
        assertNotNull(token3);
        assertNotNull(token3.getId());
        
        OrcidOauth2TokenDetail disabledToken1 = createAccessToken("disabled-1", "/read-limited", clientId, userId, true);
        assertNotNull(disabledToken1);
        assertNotNull(disabledToken1.getId());
        
        OrcidOauth2TokenDetail disabledToken2 = createAccessToken("disabled-2", "/read-limited", clientId, userId, true);
        assertNotNull(disabledToken2);
        assertNotNull(disabledToken2.getId());
        
        OrcidOauth2TokenDetail disabledToken3 = createAccessToken("disabled-3", "/read-limited", clientId, userId, true);
        assertNotNull(disabledToken3);
        assertNotNull(disabledToken3.getId());
        
        Collection<OAuth2AccessToken> tokens = orcidTokenStoreService.findTokensByClientIdAndUserName(clientId, userId);
        assertNotNull(tokens);
        assertEquals(3, tokens.size());
        
        for(OAuth2AccessToken token : tokens) {
            assertThat(token.getValue(), anyOf(is("enabled-1"), is("enabled-2"), is("enabled-3")));
        }
    }
    
    private OrcidOauth2TokenDetail createAccessToken(String tokenValue, String scope, String clientId, String userOrcid, boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setDateCreated(new Date());
        token.setProfile(new ProfileEntity(userOrcid));
        token.setScope(scope);
        token.setTokenDisabled(disabled);
        token.setTokenValue(tokenValue);        
        orcidOauth2TokenDetailService.saveOrUpdate(token);
        return token;
    }
}
