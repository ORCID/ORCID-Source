package org.orcid.core.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-oauth2-common-config.xml" })
public class OrcidClientCredentialEndPointDelegatorTest extends DBUnitTest {

    private static final String CLIENT_ID_1 = "APP-5555555555555555";    
    private static final String USER_ORCID = "0000-0000-0000-0001";
    
    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/SubjectEntityData.xml"));
    }
    
    @After
    public void after() {
        SecurityContextHolder.clearContext();     
    }                
    
    private OrcidOauth2AuthoriziationCodeDetail createAuthorizationCode(String value, String clientId, String redirectUri, boolean persistent, String ... scopes) {
        OrcidOauth2AuthoriziationCodeDetail authorizationCode = new OrcidOauth2AuthoriziationCodeDetail();
        authorizationCode.setId(value);
        authorizationCode.setApproved(true);
        authorizationCode.setScopes(new HashSet<String>(Arrays.asList(scopes)));
        authorizationCode.setClientDetailsEntity(new ClientDetailsEntity(clientId));
        authorizationCode.setPersistent(persistent);
        authorizationCode.setProfileEntity(new ProfileEntity(USER_ORCID));
        authorizationCode.setRedirectUri(redirectUri);
        authorizationCode.setResourceIds(new HashSet<String>(Arrays.asList("orcid")));
        authorizationCode.setAuthenticated(true);
        orcidOauth2AuthoriziationCodeDetailDao.persist(authorizationCode);
        return authorizationCode;
    }
    
    @Test
    public void generateAccessTokenTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        OrcidOauth2AuthoriziationCodeDetail authCode = createAuthorizationCode("code-1", CLIENT_ID_1, "http://www.APP-5555555555555555.com/redirect/oauth", true, "/activities/update");
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "authorization_code");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        formParams.add("code", authCode.getId());
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));
    }
    
    @Test
    public void generateClientCredentialsAccessTokenTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "client_credentials");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth"); 
        formParams.add("scope", "/orcid-profile/create");
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));
    }            
    
    @Test(expected = InvalidScopeException.class)
    public void generateClientCredentialsAccessTokenWithInvalidTokenTest() { 
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE);
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "client_credentials");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth"); 
        formParams.add("scope", "/activities/update");
        orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        fail();
    }
    
    @Test
    public void generateRefreshTokenTest() {
        //Generate the access token
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        OrcidOauth2AuthoriziationCodeDetail authCode = createAuthorizationCode("code-1", CLIENT_ID_1, "http://www.APP-5555555555555555.com/redirect/oauth", true, "/activities/update");
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "authorization_code");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        formParams.add("code", authCode.getId());
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));
        
        //Generate the refresh token
        MultivaluedMap<String, String> refreshTokenformParams = new MultivaluedMapImpl();
        refreshTokenformParams.add("client_id", CLIENT_ID_1);
        refreshTokenformParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        refreshTokenformParams.add("grant_type", "refresh_token");
        refreshTokenformParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        refreshTokenformParams.add("refresh_token", token.getRefreshToken().getValue());
        String authorization = "bearer " + token.getValue();
        Response refreshTokenResponse = orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, refreshTokenformParams);
        assertNotNull(refreshTokenResponse);
        assertNotNull(refreshTokenResponse.getEntity());
        DefaultOAuth2AccessToken refreshToken = (DefaultOAuth2AccessToken) refreshTokenResponse.getEntity();
        assertNotNull(refreshToken);
        assertTrue(!PojoUtil.isEmpty(refreshToken.getValue()));
        assertNotNull(refreshToken.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(refreshToken.getRefreshToken().getValue()));
        
        //Assert that both tokens expires at the same time
        assertEquals(token.getExpiration(), refreshToken.getExpiration());
        
        //Try to generate another one, and fail, because parent token was disabled
        try{
            orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, refreshTokenformParams);
        } catch (InvalidTokenException e) {
            assertTrue(e.getMessage().contains("Parent token is disabled"));
        }               
    }
    
    @Test
    public void generateRefreshTokenThatExpireAfterParentTokenTest() {
        //Generate the access token
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        OrcidOauth2AuthoriziationCodeDetail authCode = createAuthorizationCode("code-1", CLIENT_ID_1, "http://www.APP-5555555555555555.com/redirect/oauth", false, "/activities/update");
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "authorization_code");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        formParams.add("code", authCode.getId());
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));
        
        //Generate the refresh token that expires after parent token
        MultivaluedMap<String, String> refreshTokenformParams = new MultivaluedMapImpl();
        refreshTokenformParams.add("client_id", CLIENT_ID_1);
        refreshTokenformParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        refreshTokenformParams.add("grant_type", "refresh_token");
        refreshTokenformParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        refreshTokenformParams.add("refresh_token", token.getRefreshToken().getValue());
        refreshTokenformParams.add("expires_in", String.valueOf(2*60*60));
        String authorization = "bearer " + token.getValue();
        try {
            orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, refreshTokenformParams);
        } catch(IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Token expiration can't be after"));
        }
        
        //Try again with a valid expiration value
        refreshTokenformParams = new MultivaluedMapImpl();
        refreshTokenformParams.add("client_id", CLIENT_ID_1);
        refreshTokenformParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        refreshTokenformParams.add("grant_type", "refresh_token");
        refreshTokenformParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        refreshTokenformParams.add("refresh_token", token.getRefreshToken().getValue());
        refreshTokenformParams.add("expires_in", String.valueOf(60*30));
        response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, refreshTokenformParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken refreshToken = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(refreshToken);
        assertTrue(!PojoUtil.isEmpty(refreshToken.getValue()));
        assertNotNull(refreshToken.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(refreshToken.getRefreshToken().getValue()));
        
        assertTrue(token.getExpiration().getTime() > refreshToken.getExpiration().getTime());
    }
}
