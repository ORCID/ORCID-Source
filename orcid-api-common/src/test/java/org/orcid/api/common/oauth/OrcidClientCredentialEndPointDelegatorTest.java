package org.orcid.api.common.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;

public class OrcidClientCredentialEndPointDelegatorTest {

    private static final String CLIENT_ID_1 = "APP-5555555555555555";
    private static final String USER_ORCID = "0000-0000-0000-0001";

    private OrcidClientCredentialEndPointDelegatorImpl orcidClientCredentialEndPointDelegator;

    @Mock
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDaoMock;
    
    @Mock
    protected LocaleManager localeManagerMock;  
    
    @Mock
    private ProfileLastModifiedDao profileLastModifiedDaoMock;    
    
    @Mock
    private OAuth2RequestFactory oAuth2RequestFactoryMock;
    
    @Mock
    private TokenGranter tokenGranterMock;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        orcidClientCredentialEndPointDelegator = new OrcidClientCredentialEndPointDelegatorImpl();
        orcidClientCredentialEndPointDelegator.setOAuth2RequestFactory(oAuth2RequestFactoryMock);
        orcidClientCredentialEndPointDelegator.setTokenGranter(tokenGranterMock);
        
        TargetProxyHelper.injectIntoProxy(orcidClientCredentialEndPointDelegator, "profileLastModifiedDao", profileLastModifiedDaoMock);
        TargetProxyHelper.injectIntoProxy(orcidClientCredentialEndPointDelegator, "localeManager", localeManagerMock);
        TargetProxyHelper.injectIntoProxy(orcidClientCredentialEndPointDelegator, "orcidOauth2AuthoriziationCodeDetailDao", orcidOauth2AuthoriziationCodeDetailDaoMock);
        
        when(orcidOauth2AuthoriziationCodeDetailDaoMock.isPersistentToken(eq("code-1"))).thenReturn(true);
        
        AuthorizationRequest ar = new AuthorizationRequest(); 
        when(oAuth2RequestFactoryMock.createAuthorizationRequest(anyMap())).thenReturn(ar);
        
        Map<String, Object> additionalInformation = new HashMap<String, Object>();
        additionalInformation.put(OrcidOauth2Constants.TOKEN_ID, "token-1");
        additionalInformation.put(OrcidOauth2Constants.TOKEN_ID, 1L);
        TokenRequest tr = new TokenRequest(null, null, null, null);
        when(oAuth2RequestFactoryMock.createTokenRequest(any(), anyString())).thenReturn(tr);
        
        
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token-1");
        token.setAdditionalInformation(additionalInformation);
        token.setRefreshToken(new DefaultOAuth2RefreshToken("refresh-token-1"));
        when(tokenGranterMock.grant(any(), any())).thenReturn(token);
    }

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }    

    @Test
    public void generateAccessTokenTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        //OrcidOauth2AuthoriziationCodeDetail authCode = createAuthorizationCode("code-1", CLIENT_ID_1, "http://www.APP-5555555555555555.com/redirect/oauth", true, "/activities/update");
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "authorization_code");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        formParams.add("code", "code-1");
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));
        verify(profileLastModifiedDaoMock, times(1)).updateIndexingStatus(Mockito.any(), Mockito.any());
    }

    @Test
    public void generateClientCredentialsAccessTokenTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
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
        
        verify(profileLastModifiedDaoMock, times(0)).updateIndexingStatus(Arrays.asList(USER_ORCID), IndexingStatus.PENDING);
    }

    @Test(expected = InvalidScopeException.class)
    public void generateClientCredentialsAccessTokenWithInvalidTokenTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE);
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
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
        // Generate the access token
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        //OrcidOauth2AuthoriziationCodeDetail authCode = createAuthorizationCode("code-1", CLIENT_ID_1, "http://www.APP-5555555555555555.com/redirect/oauth", true, "/activities/update");
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "authorization_code");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        formParams.add("code", "code-1");
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));

        // Generate the refresh token
        MultivaluedMap<String, String> refreshTokenformParams = new MultivaluedHashMap<String, String>();
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

        // Assert that both tokens expires at the same time
        assertEquals(token.getExpiration(), refreshToken.getExpiration());

        // Try to generate another one, and fail, because parent token was
        // disabled
        try {
            orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, refreshTokenformParams);
        } catch (InvalidTokenException e) {
            assertTrue(e.getMessage().contains("Parent token is disabled"));
        }
    }

    @Test
    public void generateRefreshTokenThatExpireAfterParentTokenTest() {
        // Generate the access token
        SecurityContextTestUtils.setUpSecurityContextForClientOnly(CLIENT_ID_1, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.READ_LIMITED);
        //OrcidOauth2AuthoriziationCodeDetail authCode = createAuthorizationCode("code-1", CLIENT_ID_1, "http://www.APP-5555555555555555.com/redirect/oauth", false, "/activities/update");
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
        formParams.add("client_id", CLIENT_ID_1);
        formParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        formParams.add("grant_type", "authorization_code");
        formParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        formParams.add("code", "code-1");
        
        Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(null, formParams);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) response.getEntity();
        assertNotNull(token);
        assertTrue(!PojoUtil.isEmpty(token.getValue()));
        assertNotNull(token.getRefreshToken());
        assertTrue(!PojoUtil.isEmpty(token.getRefreshToken().getValue()));

        // Generate the refresh token that expires after parent token
        MultivaluedMap<String, String> refreshTokenformParams = new MultivaluedHashMap<String, String>();
        refreshTokenformParams.add("client_id", CLIENT_ID_1);
        refreshTokenformParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        refreshTokenformParams.add("grant_type", "refresh_token");
        refreshTokenformParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        refreshTokenformParams.add("refresh_token", token.getRefreshToken().getValue());
        refreshTokenformParams.add("expires_in", String.valueOf(2 * 60 * 60));
        String authorization = "bearer " + token.getValue();
        try {
            orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, refreshTokenformParams);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Token expiration can't be after"));
        }

        // Try again with a valid expiration value
        refreshTokenformParams = new MultivaluedHashMap<String, String>();
        refreshTokenformParams.add("client_id", CLIENT_ID_1);
        refreshTokenformParams.add("client_secret", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
        refreshTokenformParams.add("grant_type", "refresh_token");
        refreshTokenformParams.add("redirect_uri", "http://www.APP-5555555555555555.com/redirect/oauth");
        refreshTokenformParams.add("refresh_token", token.getRefreshToken().getValue());
        refreshTokenformParams.add("expires_in", String.valueOf(60 * 30));
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
