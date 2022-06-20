package org.orcid.core.oauth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.exception.ClientDeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.persistence.dao.MemberOBOWhitelistedClientDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.MemberOBOWhitelistedClientEntity;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

public class IETFExchangeTokenGranterTest {

    private static final String GRANT_TYPE = OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE;
    
    private static final String ORCID = "0000-0000-0000-0000";
    
    private static final String AUDIENCE_CLIENT_ID = "AUD_CLIENT";
    
    @Mock
    private AuthorizationServerTokenServices tokenServicesMock;
    
    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManagerMock;
    
    @Mock
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidatorMock;
    
    @Mock
    private OpenIDConnectKeyService openIDConnectKeyServiceMock;
    
    @Mock
    private ClientDetailsEntity activeClientMock;
    
    @Mock
    private MemberOBOWhitelistedClientDao memberOBOWhitelistedClientDaoMock;
    
    @Mock
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailServiceMock;
    
    @Mock
    private ProfileEntityManager profileEntityManagerMock;
    
    @Mock
    private ProfileDao profileDaoMock;
    
    private IETFExchangeTokenGranter tokenGranter;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        
        ClientDetailsEntity lockedClient = new ClientDetailsEntity("LOCKED");
        ClientDetailsEntity deactivatedClient = new ClientDetailsEntity("DEACTIVATED");
        
        when(activeClientMock.getId()).thenReturn("ACTIVE");
        when(activeClientMock.getClientId()).thenReturn("ACTIVE");
        when(activeClientMock.getAuthorizedGrantTypes()).thenReturn(Set.of(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE));
        
        // For sake of testing, ACTIVE and AUD_CLIENT could be the same mock
        when(clientDetailsEntityCacheManagerMock.retrieve("ACTIVE")).thenReturn(activeClientMock);
        when(clientDetailsEntityCacheManagerMock.retrieve("AUD_CLIENT")).thenReturn(activeClientMock);
        
        when(clientDetailsEntityCacheManagerMock.retrieve("LOCKED")).thenReturn(lockedClient);
        when(clientDetailsEntityCacheManagerMock.retrieve("DEACTIVATED")).thenReturn(deactivatedClient);
        
        doThrow(LockedException.class).when(orcidOAuth2RequestValidatorMock).validateClientIsEnabled(eq(lockedClient));
        doThrow(ClientDeactivatedException.class).when(orcidOAuth2RequestValidatorMock).validateClientIsEnabled(eq(deactivatedClient));
        
        when(openIDConnectKeyServiceMock.verify(any())).thenReturn(true);
        
        ClientDetailsEntity oboClient = new ClientDetailsEntity("AUD_CLIENT");        
        MemberOBOWhitelistedClientEntity e = new MemberOBOWhitelistedClientEntity();
        e.setWhitelistedClientDetailsEntity(oboClient);
        
        List<MemberOBOWhitelistedClientEntity> oboClients = List.of(e);
        
        when(memberOBOWhitelistedClientDaoMock.getWhitelistForClient("ACTIVE")).thenReturn(oboClients);
                
        OrcidOauth2TokenDetail token1 = new OrcidOauth2TokenDetail();
        token1.setApproved(true);
        token1.setScope("/read-limited");
        token1.setTokenExpiration(new Date(System.currentTimeMillis() + 60000));
        token1.setTokenDisabled(false);
        
        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1));
        
        ProfileEntity profile = new ProfileEntity(ORCID);
        when(profileEntityManagerMock.findByOrcid(eq(ORCID))).thenReturn(profile);        
        
        OrcidGrantedAuthority oga = new OrcidGrantedAuthority();
        oga.setAuthority("ROLE_USER");
        oga.setProfileEntity(profile);
        when(profileDaoMock.getGrantedAuthoritiesForProfile(eq(ORCID))).thenReturn(List.of(oga));
        
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("");
        token.setAdditionalInformation(new HashMap<String, Object>());
        token.setExpiration(new Date(System.currentTimeMillis() + 60000));
        token.setScope(Set.of());
        token.setTokenType("");
        token.setValue("");
        
        when(tokenServicesMock.createAccessToken(any())).thenReturn(token);
        
        tokenGranter = new IETFExchangeTokenGranter(tokenServicesMock);
        
        TargetProxyHelper.injectIntoProxy(tokenGranter, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "orcidOAuth2RequestValidator", orcidOAuth2RequestValidatorMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "openIDConnectKeyService", openIDConnectKeyServiceMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "memberOBOWhitelistedClientDao", memberOBOWhitelistedClientDaoMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "orcidOauthTokenDetailService", orcidOauthTokenDetailServiceMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "profileEntityManager", profileEntityManagerMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "profileDao", profileDaoMock);
    }
    
    @Test(expected = ClientDeactivatedException.class)
    public void grantDeactivatedClientTest() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        tokenGranter.grant(GRANT_TYPE, getTokenRequest("DEACTIVATED", List.of("/read-limited")));
    }
    
    @Test(expected = LockedException.class)
    public void grantLockedClientTest() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        tokenGranter.grant(GRANT_TYPE, getTokenRequest("LOCKED", List.of("/read-limited")));
    }
    
    @Test
    public void grant() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        tokenGranter.grant(GRANT_TYPE, getTokenRequest("ACTIVE", List.of("/read-limited")));
        verify(tokenServicesMock, times(1)).createAccessToken(any());
    }
    
    private TokenRequest getTokenRequest(String clientId, List<String> scope) throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, buildJWTToken());
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:id_token");
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:access_token");
        return new TokenRequest(requestParameters, clientId, scope, GRANT_TYPE);        
    }
    
    private String buildJWTToken() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        Builder claims = new JWTClaimsSet.Builder();
        claims.claim("aud", AUDIENCE_CLIENT_ID);
        claims.claim("sub", ORCID);
        
        OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig config = new OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig();
        config.setKeyName("OpenIDTestKey1");
        config.setJsonKey("{\"keys\":[{\"kty\":\"RSA\",\"d\":\"i6C2Vdr7HDMj9wOBx28epQ7KPpzU_RDfGmQF8c81MoQU2KkpuNcFD49Rixzp3nQa58vtCOzAKeHwglpqm4elcai-uTW0bcdW1DOqYbwzQEk7pVQF-mMEUC-Rvd3Y5SIhCrHQYHGq9Q58uyuolG-Exq4h1AgyhUBX3CETCqzhPshOmB_Y4OuasdhyuVNySBbo-ZOYSd-HMrsrv1lt5WckWz22wmsREjO5AoRPpF17UVp3nMRCTy2v1acUrNtG64MdaFUpmLt9a-RqseFErE2Tm-kEUSBjYucswQ0_ZIs_VUdPWet4twqulB2bJi2ET6pP25DufOtR0x3ijvEPAfvhwQ\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"OpenIDTestKey1\",\"alg\":\"RS256\",\"n\":\"qCtxWP2HppC8PBEXUh6b5RPECAzQS01khDwbxCSndO-YtS1MYpNlmtUgdtoAEoIP9TFMqXOsltKmGFioy0CeWLi53M-iX-Ygjd3zSQAbr0BU0-86somdbIlFxuvGA8v6AC7MNlICTwbGExCufL_hivrzF1XVqi5zIovM1LA8k2bP4BKMEjNwhGBGJ0E9KcQYv65foZr9K0C6YYJDFE6YqsHP_czvbI1ij7MfDvN5cwmHRGMGOyzDCmT_SmjoZAZ4vSXbl2wI5txIj70RLLSK4oahktb-09c0lDVYpCno7LqsLR8E3DuTUniYwYMHlXeBor_G7sJw2alF568m1iZ_zQ\"}]}");
        OpenIDConnectKeyService service = new OpenIDConnectKeyService(config);
        
        return service.sign(claims.build()).serialize();
    }
}
