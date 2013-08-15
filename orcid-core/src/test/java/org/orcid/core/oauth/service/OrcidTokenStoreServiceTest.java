/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
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
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 20/04/2012
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidTokenStoreServiceTest extends DBUnitTest {

    @Resource(name = "orcidTokenStore")
    private TokenStore orcidTokenStoreService;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Transactional
    @Rollback
    public void testReadAuthentication() throws Exception {
        OAuth2Authentication oAuth2Authentication = orcidTokenStoreService.readAuthentication("some-long-oauth2-token-value-1");
        assertNotNull(oAuth2Authentication);
        AuthorizationRequest authorizationRequest = oAuth2Authentication.getAuthorizationRequest();
        assertNotNull(authorizationRequest);
        Object principal = oAuth2Authentication.getPrincipal();
        assertNotNull(principal);
        assertTrue(!oAuth2Authentication.isClientOnly());
    }

    @Test
    @Transactional
    @Rollback
    public void testStoreAccessToken() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("some-long-oauth2-token-value-9");
        ExpiringOAuth2RefreshToken refreshToken = new DefaultExpiringOAuth2RefreshToken("some-long-oauth2-refresh-value-9", new Date());
        token.setRefreshToken(refreshToken);
        token.setScope(new HashSet<String>(Arrays.asList("/orcid-bio/read", "/orcid-works/read")));
        token.setTokenType("bearer");
        token.setExpiration(new Date());

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", "4444-4444-4444-4441");
        parameters.put("state", "read");
        parameters.put("scope", "/orcid-profile/write");
        parameters.put("redirect_uri", "http://www.google.com/");
        parameters.put("response_type", "bearer");
        DefaultAuthorizationRequest request = new DefaultAuthorizationRequest(parameters);
        request.setApproved(true);

        ProfileEntity profileEntity = profileEntityManager.findByOrcid("4444-4444-4444-4444");
        OrcidOauth2UserAuthentication userAuthentication = new OrcidOauth2UserAuthentication(profileEntity, true);

        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);

        orcidTokenStoreService.storeAccessToken(token, authentication);
        OAuth2AccessToken oAuth2AccessToken = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-9");
        assertNotNull(oAuth2AccessToken);
    }

    @Test
    @Transactional
    @Rollback
    public void testReadAccessToken() throws Exception {
        OAuth2AccessToken oAuth2AccessToken = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-1");
        assertNotNull(oAuth2AccessToken);
    }

    @Test
    @Transactional
    @Rollback
    public void testRemoveAccessToken() throws Exception {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("some-long-oauth2-token-value-1");
        orcidTokenStoreService.removeAccessToken(accessToken);
        OAuth2AccessToken oAuth2AccessToken = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-1");
        assertNull(oAuth2AccessToken);
    }

    @Test
    @Transactional
    @Rollback
    public void testReadAuthenticationForRefreshToken() throws Exception {
        OAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken("some-long-oauth2-refresh-value-1");
        OAuth2Authentication oAuth2Authentication = orcidTokenStoreService.readAuthenticationForRefreshToken(refreshToken);
        assertNotNull(oAuth2Authentication);
    }

    @Test
    @Transactional
    @Rollback
    public void testRemoveRefreshToken() throws Exception {
        OAuth2AccessToken token = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-1");
        orcidTokenStoreService.removeRefreshToken(token.getRefreshToken());
        OAuth2RefreshToken refreshToken = orcidTokenStoreService.readRefreshToken("some-long-oauth2-refresh-value-1");
        assertNull(refreshToken);
    }

    @Test
    @Transactional
    @Rollback
    public void testRemoveAccessTokenUsingRefreshToken() throws Exception {
        OAuth2AccessToken token = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-1");
        orcidTokenStoreService.removeAccessTokenUsingRefreshToken(token.getRefreshToken());
        token = orcidTokenStoreService.readAccessToken("some-long-oauth2-token-value-1");
        assertNull(token);
    }

    @Test
    @Transactional
    @Rollback
    public void testGetAccessToken() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", "4444-4444-4444-4441");
        parameters.put("state", "read");
        parameters.put("scope", "/orcid-profile/write");
        parameters.put("redirect_uri", "http://www.google.com/");
        parameters.put("response_type", "bearer");
        DefaultAuthorizationRequest request = new DefaultAuthorizationRequest(parameters);
        request.setApproved(true);

        ProfileEntity profileEntity = profileEntityManager.findByOrcid("4444-4444-4444-4444");
        OrcidOauth2UserAuthentication userAuthentication = new OrcidOauth2UserAuthentication(profileEntity, true);

        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("4444-4444-4444-4441");
        token.setExpiration(new Date());
        token.setScope(OAuth2Utils.parseParameterList("/orcid-profile/read,/orcid-profile/write"));
        token.setTokenType("bearer");
        token.setRefreshToken(new DefaultExpiringOAuth2RefreshToken("a-refresh-token", new Date()));

        orcidTokenStoreService.storeAccessToken(token, authentication);

        OAuth2AccessToken accessToken = orcidTokenStoreService.getAccessToken(authentication);
        assertNotNull(accessToken);
    }

    @Test
    @Transactional
    @Rollback
    public void testFindTokensByUserName() throws Exception {
        Collection<OAuth2AccessToken> tokensByUserName = orcidTokenStoreService.findTokensByUserName("4444-4444-4444-4441");
        assertNotNull(tokensByUserName);
        assertEquals(1, tokensByUserName.size());
    }

    @Test
    @Transactional
    @Rollback
    public void testFindTokensByClientId() throws Exception {
        Collection<OAuth2AccessToken> tokensByClientId = orcidTokenStoreService.findTokensByClientId("4444-4444-4444-4441");
        assertNotNull(tokensByClientId);
        assertEquals(1, tokensByClientId.size());
    }
}
